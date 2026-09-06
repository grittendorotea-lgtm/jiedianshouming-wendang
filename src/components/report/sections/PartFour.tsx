import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, KvTable, Section, Sub } from "../blocks";
import { Figure, SeqTable } from "../flowchart";

export function PartFour() {
  return (
    <>
      <Section id="protocol" kicker="第十三节" title="通信协议设计">
        <p>
          电阻、电流、PLC 都是 Modbus-RTU。写课题报告时建议单独开一节“通信协议设计”，把帧格式和地址分配写清楚。
        </p>
        <Sub title="统一的请求帧">
          <CodeBlock title="8 字节请求">{`[从站地址] [功能码] [起始地址高] [起始地址低]
[数量高] [数量低] [CRC低] [CRC高]`}</CodeBlock>
          <div className="overflow-x-auto rounded-lg border border-border bg-white">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="px-3 py-2">设备</th>
                  <th className="px-3 py-2">地址</th>
                  <th className="px-3 py-2">功能码</th>
                  <th className="px-3 py-2">起始</th>
                  <th className="px-3 py-2">数量</th>
                </tr>
              </thead>
              <tbody className="text-slate-700">
                <tr className="border-t">
                  <td className="px-3 py-2">电流表 JiaoL</td>
                  <td className="px-3 py-2">0x01</td>
                  <td className="px-3 py-2">0x03 读保持</td>
                  <td className="px-3 py-2">1</td>
                  <td className="px-3 py-2">3</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">电阻表 1/2/3</td>
                  <td className="px-3 py-2">2 / 3 / 4</td>
                  <td className="px-3 py-2">0x03 读保持</td>
                  <td className="px-3 py-2">1</td>
                  <td className="px-3 py-2">3</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">PLC</td>
                  <td className="px-3 py-2">0x08</td>
                  <td className="px-3 py-2">0x04 读输入</td>
                  <td className="px-3 py-2">0</td>
                  <td className="px-3 py-2">2</td>
                </tr>
              </tbody>
            </table>
          </div>
        </Sub>
        <Sub title="仪表应答的数据区">
          <p>电阻/电流表在功能码之后给出：</p>
          <KvTable
            rows={[
              { k: "data[3..4]", v: "16 位原始测量值，大端。" },
              { k: "data[6]", v: "小数点位数。" },
              { k: "data[8]", v: "单位码。电阻 3/4/5，电流 3/4。" },
              { k: "末 2 字节", v: "CRC，低字节在前。" },
            ]}
          />
        </Sub>
        <Sub title="CRC-16 算法（三类设备共用）">
          <CodeBlock title="calculateCRC">{`int crc = 0xFFFF;
for (int j = 0; j < length; j++) {
    crc ^= data[j] & 0xFF;
    for (int i = 8; i > 0; i--) {
        if ((crc & 0x0001) != 0) {
            crc >>= 1;
            crc ^= 0xA001;
        } else {
            crc >>= 1;
        }
    }
}`}</CodeBlock>
          <p>
            该算法为标准 Modbus CRC-16（多项式 0x8005 的反射形式 0xA001）。请求对前 6
            字节计算，应答对除 CRC 外的整帧计算。校验失败的数据不进入曲线。
          </p>
        </Sub>
        <Sub title="一拍访问顺序与请求帧">
          <p>
            系统在 RS-485
            总线上采用主从问答。PLC 使用功能码 04H
            循环读取两路开关量；电阻表、电流表使用功能码 03H
            读取测量值、小数位和单位。由于多设备共享同一串口，上位机在开关量有效时主动关闭
            PLC 通道，再分别访问从站 2、3、4 和从站
            1，完成后再交还 PLC。每一帧均附带 CRC-16
            校验，校验失败的数据不进入曲线，避免干扰点污染寿命曲线。
          </p>
          <Figure no="13-1" title="一拍内 COM3 访问时序">
            <SeqTable
              rows={[
                {
                  step: "1 循环",
                  who: "PLC",
                  frame: "08 04 00 00 00 02 71 52",
                  purpose: "读 2 路输入。功能码必须是 04，否则睡 10ms 重问。",
                },
                {
                  step: "2",
                  who: "电阻表 地址 2",
                  frame: "02 03 00 01 00 03 54 38",
                  purpose: "X1=1 后关 PLC 口，读接触电阻 1。",
                },
                {
                  step: "3",
                  who: "电阻表 地址 3",
                  frame: "03 03 00 01 00 03 55 E9",
                  purpose: "读接触电阻 2，间隔约 50ms。",
                },
                {
                  step: "4",
                  who: "电阻表 地址 4",
                  frame: "04 03 00 01 00 03 54 5E",
                  purpose: "读接触电阻 3，然后关电阻仪口。",
                },
                {
                  step: "5 循环",
                  who: "PLC",
                  frame: "08 04 00 00 00 02 71 52",
                  purpose: "重新打开 PLC 口，继续盯 X2。",
                },
                {
                  step: "6",
                  who: "电流表 地址 1",
                  frame: "01 03 00 01 00 03 54 0B",
                  purpose: "X2=1 后关 PLC 口，发一帧，等约 200ms 取缓存值。",
                },
              ]}
            />
          </Figure>
          <p>
            假设电阻表回：字节 3、4 为 <code>04 D2</code>（1234），字节 6 为{" "}
            <code>02</code>（两位小数），字节 8 为 <code>03</code>（mΩ），则实际值为
            12.34 mΩ。主界面 <code>extractNumber()</code> 去掉单位后再和阈值比较。
          </p>
        </Sub>
      </Section>

      <Section id="classes" kicker="第十四节" title="软件结构">
        <div className="grid gap-3 md:grid-cols-2">
          <Card>
            <CardHeader>
              <Badge variant="chart">约 2750 行</Badge>
              <CardTitle className="pt-2 text-base">ZzhejiPanel</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              测试主面板。负责全部人机交互、测试状态机、曲线、保存、回显、保护停机、速度计算。内部还有
              StyledButton、GradientPanel 两个内部类做渐变按钮和背景。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="rust">电阻通信</Badge>
              <CardTitle className="pt-2 text-base">DianZu00000</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              同步轮询从站 2/3/4。打开、读取、解析、关闭。给主界面三个
              getter：getDianZu1/2/3。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="current">电流通信</Badge>
              <CardTitle className="pt-2 text-base">JiaoL</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              异步监听从站 1。缓存 jiaoLValue 和单位。主界面问一次就关口。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="db">数据访问</Badge>
              <CardTitle className="pt-2 text-base">ExecuteCommon</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              两份文件内容相同。参数读写 + 曲线批插 + 通用
              queryDatas/updateDatas。main 方法只是早期查 t_userinfo 的调试入口。
            </CardContent>
          </Card>
        </div>
        <Sub title="主界面还依赖、但不在这批文件里的类">
          <p>
            写报告时要承认系统不只有这 5 个文件。从 import
            可以列出周边模块：
          </p>
          <KvTable
            rows={[
              { k: "RRuANDWone / RRu", v: "COM3 上的 PLC 读写。" },
              { k: "JdbcDeal / DBConnection", v: "两套数据库连接。" },
              { k: "Result", v: "装着 X1、X2 两个整数的简单结果对象。" },
              { k: "IndexFrame", v: "主窗体，退出时 showCard(\"Home\")。" },
              {
                k: "DianZu001/002/003、DDianZuOnce",
                v: "更早的单路电阻类，本面板已改用 DianZu00000 一次读三路。",
              },
              { k: "Sleep / PDialog / TemporaryData", v: "延时、进度框、临时缓存。" },
            ]}
          />
        </Sub>
        <Sub title="线程模型">
          <p>
            界面在 Swing EDT 上更新。正式采集不在 EDT 里跑，否则读串口一卡，窗口就假死。
          </p>
          <KvTable
            rows={[
              {
                k: "EDT",
                v: "按钮、setText、series.add、弹窗。采集结果一律 SwingUtilities.invokeLater 回界面。",
              },
              {
                k: "executorServicebingxing",
                v: "3 线程池。开始测试只 submit 一个 while 任务，Future 存在 currentTestFuture，停止时 cancel(true)。",
              },
              {
                k: "JiaoL 监听线程",
                v: "jSerialComm 回调线程。主界面只睡 200ms 再读缓存，所以必须先发请求再等。",
              },
              {
                k: "testTimer",
                v: "1ms 的 Swing Timer，给未上屏的模拟测试刷随机电阻。正式流程不用它。",
              },
            ]}
          />
          <p>
            串口互斥完全靠“先 close 再 open”。
            <code>readAndProcessRegisters</code> 加了{" "}
            <code>synchronized</code>
            ，避免速度计算和主循环同时打开 PLC 口，但电阻、电流对象各有自己的{" "}
            <code>SerialPort</code> 实例，真正防冲突的还是调用顺序。
          </p>
        </Sub>
        <Sub title="界面控件对照">
          <KvTable
            rows={[
              {
                k: "测试编号",
                v: "可编辑 JComboBox testBianHao。选项来自 test_results.test_bianhao 去重。",
              },
              {
                k: "测试日期",
                v: "JDatePickerImpl testTime，默认今天，保存时转 java.sql.Date。",
              },
              {
                k: "监测次数 / 累计次数",
                v: "只读 countTest、allcountTest。次数框写成当前 timeSeconds 上限。",
              },
              {
                k: "速度",
                v: "只读 suduTest，单位“次/分钟”，值为 roundedFrequency111。",
              },
              {
                k: "接触电阻 1/2/3",
                v: "dianzu111/222/333。DocumentListener 驱动加点。",
              },
              {
                k: "电流",
                v: "dianzu444 显示；dianzu444NO 隐藏缓存。先写隐藏框，读完电阻再抄到显示框。",
              },
              {
                k: "开始测试 / 保存 / 退出",
                v: "beginCLBtn、btn1、btn8。测试中按钮改成“停止测试”。",
              },
            ]}
          />
        </Sub>
        <Sub title="界面上其余按钮在做什么">
          <KvTable
            rows={[
              {
                k: "开始测试 / 停止测试",
                v: "正式流程。开线程、读 PLC、采电阻电流、保护停机。",
              },
              {
                k: "保存",
                v: "按新编号/旧编号决定全量或追加，批插入 test_results。",
              },
              {
                k: "退出",
                v: "resetForNextEnter() 停任务、关串口、清曲线和标志，再回到 Home。",
              },
              {
                k: "清除（已布局未放置）",
                v: "清空四个显示框和曲线。按钮创建了，placeButton 被注释。",
              },
              {
                k: "充值/模拟测试 btn4",
                v: "用 Timer(1ms) 刷随机电阻，曾用于不接仪表时看界面。未放到当前布局。",
              },
            ]}
          />
        </Sub>
      </Section>

      <Section
        id="conclusion"
        kicker="第十五节"
        title="总结与展望"
      >
        <p>
          本系统实现了接点接触电阻试验的在线采集、实时监测、保护停机、故障记录与历史回放，形成“节拍同步采集—多仪表解析—双坐标曲线—数据库存储”的闭环。其主要工作在于：在单串口、多从站条件下，以凸轮开关量为同步信号，将三路接触电阻与一路电流对齐至同一动作周期，并在电阻、电流、机构状态和试验次数四个维度实施自动停机。
        </p>
        <p>对照源码可以指出的局限如下，比空泛的“界面不够美观”更适合写进报告：</p>
        <ul className="list-disc space-y-2 pl-5">
          <li>
            单串口访问依赖人工 close/open，没有统一调度器。总线一忙或关口失败，整拍会丢。
          </li>
          <li>
            <code>DBConnection</code> 与 <code>JdbcDeal</code>{" "}
            两套入口并存，这批文件里看不到连接池配置。
          </li>
          <li>三路电阻单位共用 <code>dianZUnit1</code>，最后一路会覆盖前两路。</li>
          <li>电流阈值写死 2.1 A，现场改门槛要改代码。</li>
          <li>界面为 1620×950 绝对布局，换分辨率容易错位。</li>
        </ul>
        <p>
          后续可将串口访问收敛为调度组件，将电阻上限、电流上限、凸轮超时和次数上限全部参数化入库，并将曲线存储拆成试验主表与采样点表，避免每个点重复存日期和编号。
        </p>
        <Callout title="核心工作表述" tone="idea">
          系统解决的工程问题是节拍对齐与安全停机，而不是单独完成折线绘图。答辩与结论中建议强调：单总线时分复用、凸轮同步采集，以及电阻、电流、机构、次数四类保护。
        </Callout>
      </Section>
    </>
  );
}
