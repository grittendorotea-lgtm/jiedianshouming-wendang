import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, KvTable, QABody, QAList, Section, Sub } from "../blocks";
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
        <QAList
          items={[
            {
              q: "CRC 在这套系统里不是“校验一下”，而是安全边界：它如何把干扰帧挡在判定和曲线之外？",
              a: (
                <QABody
                  contradiction={
                    <>
                      接触器动作时 RS-485
                      上常见半包和位翻转。若把损坏的 16
                      位整数当电阻，一次尖峰就能触发停机，寿命曲线也会留下无法解释的断点。
                    </>
                  }
                  implementation={
                    <>
                      请求对前 6 字节、应答对除 CRC
                      外整帧计算 Modbus CRC-16（初值 0xFFFF，多项式反射 0xA001）。失败则返回空/0，不写{" "}
                      <code>pureValue</code>，监听器也不会加点。PLC
                      还必须核对功能码 04H，否则 10ms
                      重问——防止把仪表的 03H 残帧当成凸轮沿。一拍访问顺序见图
                      13-1：先 08 04，再 02/03/04 的 03H，再交还，最后 01
                      03。
                    </>
                  }
                  bound={
                    <>
                      CRC
                      只能保证“这帧没坏”，不能保证“这帧是刚才问的那台”——基础版靠串行等待配对，4
                      拖 1 必须再读 <code>data[0]</code>{" "}
                      地址。丢弃后本拍缺数，保护比较可能仍用上一拍旧值。
                    </>
                  }
                />
              ),
            },
          ]}
        />
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
                v: "先执行 resetForNextEnter()：停 Timer、取消 Future、移除监听、关串口、复位 AtomicBoolean 和时间变量，再回 Home。避免下次继承上一次线程或串口占用。",
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
        <QAList
          items={[
            {
              q: "试验页是长生命周期 JPanel。退出后再进入，如何拆除上一次的线程、沿闭锁和串口占用？",
              a: (
                <QABody
                  contradiction={
                    <>
                      面板对象往往被复用而不是 new。若只{" "}
                      <code>setVisible(false)</code>
                      ，上一轮的 Future 还在读 COM3，<code>processed2</code>{" "}
                      仍为 true，<code>lastTime*</code>{" "}
                      还是旧时间戳——下次一进界面就会跳过采集或立刻报凸轮超时。
                    </>
                  }
                  implementation={
                    <>
                      “退出”强制走 <code>resetForNextEnter()</code>：停
                      Timer、 <code>cancel</code> Future、移除{" "}
                      <code>DocumentListener</code>
                      、关闭串口、复位全部{" "}
                      <code>AtomicBoolean</code>{" "}
                      与时间基，再回 Home。三类线程的职责被切开：EDT
                      只改控件，3 线程池跑试验循环，jSerialComm
                      回调只写电流缓存。
                    </>
                  }
                  bound={
                    <>
                      复位是手工清单，不是自动生命周期。漏一项就是下一次的偶发故障。4
                      拖 1 有四套旗标和四个 Future，清单更长，漏清的概率更高。
                    </>
                  }
                />
              ),
            },
          ]}
        />
      </Section>

      <Section
        id="conclusion"
        kicker="第十五节"
        title="关键技术与总结"
      >
        <Sub title="5.1 核心技术">
          <KvTable
            rows={[
              {
                k: "状态驱动式测试",
                v: "不以固定周期读全部传感器。第一状态触发电阻，第二状态触发电流，状态变化算速度，状态长时间不变判机械故障。",
              },
              {
                k: "多源数据协同采集",
                v: "PLC 读输入寄存器，电阻同步问表，电流事件监听。业务层把不同来源组织成一个测试周期。",
              },
              {
                k: "后台线程与界面分离",
                v: "测试循环在 ExecutorService，界面更新回 EDT。AtomicBoolean 保证某一输入持续为 1 时只处理一次。",
              },
              {
                k: "实时质量判定与联锁",
                v: "每个循环比较电阻、电流和动作时间，异常立即停机记录，形成采集—判定—控制闭环。",
              },
              {
                k: "数据追溯与试验恢复",
                v: "test_results 保存过程曲线，policetime 保存异常；loadedPointCount 保证续测只写新增点。",
              },
            ]}
          />
        </Sub>
        <Sub title="5.2 答辩应突出的三条主线">
          <p>
            不建议把主要篇幅放在按钮颜色、字体、绝对坐标等界面细节上，而应突出：
          </p>
          <ol className="list-decimal space-y-2 pl-5">
            <li>
              <strong>状态驱动自动测试：</strong>
              以 PLC/IO 状态识别机械位置，通过状态变化控制不同测量任务的执行时机。
            </li>
            <li>
              <strong>多源参数实时监测：</strong>
              同步组织三路接触电阻、一路电流、速度和测试次数，并用双 Y 轴趋势图可视化。
            </li>
            <li>
              <strong>安全联锁与数据追溯：</strong>
              电阻、电流、机构动作或寿命次数异常时自动停机，并通过数据库保存测试过程和故障事件。
            </li>
          </ol>
        </Sub>
        <Sub title="5.3 总结表述">
          <p>
            本系统以 PLC
            机械状态为测试时序依据，通过串口与 Modbus RTU
            协议协调三路接触电阻检测模块、电流检测模块及设备控制模块，在后台线程中完成自动循环测试；同时利用
            Swing 和 JFreeChart
            实时显示测试参数及趋势曲线，并结合数据库实现测试数据、累计寿命与异常信息的持久化管理。当出现接触电阻超限、电流超限、机构动作超时或达到设定次数等情况时，系统能够自动执行停机和故障记录，从而形成“控制—采集—分析—报警—存储—追溯”的闭环测试流程。
          </p>
        </Sub>
        <Sub title="5.4 可能被问到的关键问题">
          <div className="overflow-x-auto rounded-lg border border-border bg-white">
            <table className="w-full min-w-[36rem] text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="px-3 py-2">可能问题</th>
                  <th className="px-3 py-2">回答要点</th>
                </tr>
              </thead>
              <tbody className="text-slate-700">
                {[
                  [
                    "为什么要用后台线程？",
                    "硬件读取包含串口打开、请求、等待和循环。放在 Swing 事件线程会导致界面卡死，因此测试循环放在 ExecutorService 中，界面更新再回到 EDT。",
                  ],
                  [
                    "为什么电阻和电流不同时读取？",
                    "软件不是定时全量采集，而是根据两个 PLC 状态位区分机械阶段：第一状态触发电阻，第二状态触发电流，使采样与机构位置同步。",
                  ],
                  [
                    "为什么需要 CRC？",
                    "串口可能受干扰。CRC16 验证接收帧完整性，校验失败的数据不进入数值解析和合格性判断。",
                  ],
                  [
                    "为什么使用双 Y 轴？",
                    "接触电阻单位为 mΩ，电流单位为 A，量纲和数值范围不同，双 Y 轴可在同一 X 轴下同时观察趋势。",
                  ],
                  [
                    "历史续测如何避免重复保存？",
                    "加载历史时记录 loadedPointCount，续测保存只从该索引之后收集新增曲线点。",
                  ],
                  [
                    "软件如何保证异常时停止设备？",
                    "主循环实时检查电阻、电流、机械动作时间和设定次数，异常时向控制寄存器写入停止/报警并结束循环，同时保存故障。",
                  ],
                ].map((row) => (
                  <tr key={row[0]} className="border-t border-border align-top">
                    <td className="px-3 py-2 font-medium">{row[0]}</td>
                    <td className="px-3 py-2 leading-7">{row[1]}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </Sub>
        <Sub title="5.5 不足与改进">
          <ul className="list-disc space-y-2 pl-5">
            <li>
              COM3 分时复用依赖人工 close/open。异常路径若未及时释放端口，下一模块可能无法通信。宜抽统一
              SerialPortManager。
            </li>
            <li>
              <code>DBConnection</code> 与 <code>JdbcDeal</code> 两套入口并存。
            </li>
            <li>三路电阻单位共用 <code>dianZUnit1</code>。</li>
            <li>电流阈值写死 2.1 A。</li>
            <li>界面为 1620×950 绝对布局。</li>
          </ul>
        </Sub>
        <QAList
          items={[
            {
              q: "基础版与 4 拖 1 的本质难点差在哪一层：画图、协议，还是调度与隔离？",
              a: (
                <QABody
                  contradiction={
                    <>
                      把 4 拖 1 理解成“多画三张图、多复制三套按钮”，会完全说错难度。图和页签是同构副本；真正增加的是并发度对半双工介质的压力。
                    </>
                  }
                  implementation={
                    <>
                      基础版难在单总线编排：COM3
                      上用凸轮电平把同步电阻、异步电流和 PLC
                      巡检收成同一拍，再用沿闭锁和内存游标保证不重读、不重存。4
                      拖 1
                      难在解耦：问表从试验循环剥离到 COM5
                      常驻调度器，控制面按线圈 1～4
                      隔离，测量面按缓存共享，COM4 用{" "}
                      <code>synchronized</code>{" "}
                      串行化。第 17 节把这两条主线按矛盾—实现—边界写开。
                    </>
                  }
                  bound={
                    <>
                      仍未收干净的是公共报警线圈、两套 JDBC
                      入口、以及 ExecuteCommon
                      单记录接口与分设备阈值调用的缺口。这些应作为改进点，而不是已解决的难点。
                    </>
                  }
                />
              ),
            },
          ]}
        />
      </Section>
    </>
  );
}
