import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, ExtBox, KvTable, Section, Sub } from "../blocks";

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
                  <td className="px-3 py-2">PLC（基础版 COM3）</td>
                  <td className="px-3 py-2">0x08</td>
                  <td className="px-3 py-2">0x04 读输入</td>
                  <td className="px-3 py-2">0</td>
                  <td className="px-3 py-2">2</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">电流表 1～4（COM5）</td>
                  <td className="px-3 py-2">0x01～0x04</td>
                  <td className="px-3 py-2">0x03 读保持</td>
                  <td className="px-3 py-2">1</td>
                  <td className="px-3 py-2">3</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">电阻表 1～4（COM5）</td>
                  <td className="px-3 py-2">0x05～0x08</td>
                  <td className="px-3 py-2">0x03 读保持</td>
                  <td className="px-3 py-2">1</td>
                  <td className="px-3 py-2">3</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">PLC（拓展版 COM4）</td>
                  <td className="px-3 py-2">0x01</td>
                  <td className="px-3 py-2">0x04 读输入</td>
                  <td className="px-3 py-2">0</td>
                  <td className="px-3 py-2">8</td>
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
            按源码参数与同一 CRC 算法，一拍内的请求帧如下：PLC 循环读取{" "}
            <code>08 04 00 00 00 02 71 52</code>；X1 有效后依次访问电阻从站 2/3/4，例如{" "}
            <code>02 03 00 01 00 03 54 38</code>；交还 PLC 后再于 X2 有效时访问电流从站{" "}
            <code>01 03 00 01 00 03 54 0B</code>。PLC
            应答功能码须为 04H，否则延时 10ms 重试。
          </p>
          <ExtBox
            title="协议拓展：双总线与八从站轮询帧"
            tech="COM4/COM5 隔离、03H 八地址排队、04H 一次读 8 字、按 data[0] 分流"
          >
            仪表总线改为 COM5，由{" "}
            <code>SixMeterInstrumentReader</code> 每 100ms
            发一帧，地址 01→08 循环。请求与基础版同一格式，例如电流 1{" "}
            <code>01 03 00 01 00 03 54 0B</code>，电阻 1{" "}
            <code>05 03 00 01 00 03 55 8F</code>。PLC
            改挂 COM4，地址改为 0x01，一次读 8 个输入字：{" "}
            <code>01 04 00 00 00 08 F1 CC</code>
            。功能码不是 04H 时仍延时 10ms 重试。写侧寄存器 1～4
            分控四台运行，寄存器 5 为公共报警。半双工条件下，应答必须按帧头地址入缓存，CRC
            失败直接丢弃，否则八台表会串值。
          </ExtBox>
        </Sub>
      </Section>

      <Section id="classes" kicker="第十四节" title="软件结构">
        <div className="grid gap-3 md:grid-cols-2">
          <Card>
            <CardHeader>
              <Badge variant="chart">约 2750 / 7200 行</Badge>
              <CardTitle className="pt-2 text-base">ZzhejiPanel</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              基础版单工位面板。2026-08-11 拓展版改为四个“监测设备”页签、四套状态机、四张双轴图和分设备启停。内部仍有
              StyledButton、GradientPanel。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="plc">八表调度</Badge>
              <CardTitle className="pt-2 text-base">
                SixMeterInstrumentReader
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              包名 <code>com.ytzg.sealer.comm.caidanduqu</code>
              。COM5 上每 100ms 轮询地址 01～08，CRC
              通过后按从站地址写入并发缓存。工位只调用{" "}
              <code>getCurrentValue(1～4)</code> /{" "}
              <code>getResistanceValue(5～8)</code>。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="rust">电阻通信</Badge>
              <CardTitle className="pt-2 text-base">DianZu00000</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              基础版同步轮询从站 2/3/4。拓展后现场问表由统一读取器取代，本类保留作对照实现。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="current">电流通信</Badge>
              <CardTitle className="pt-2 text-base">JiaoL</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              基础版异步监听从站 1。拓展后四路电流并入同一调度队列，主界面不再开关电流表串口。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="db">数据访问</Badge>
              <CardTitle className="pt-2 text-base">ExecuteCommon</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              基础版两份文件内容相同。拓展面板已调用{" "}
              <code>getDianzumaxValue1～4</code>、
              <code>getTestmaxValue1～4</code>、
              <code>getAllcountValue1(shebeihao)</code>{" "}
              及带设备号的批插，数据访问层需与之对齐。
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
              {
                k: "RRuANDWone / RRu / Wone",
                v: "基础版在 COM3 读写 PLC。拓展版 RRuANDWone 改绑 COM4；源码里仍留有 COM5 的 RRu/Wone 对象，与仪表读取器同口，不能同时打开。",
              },
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
            界面更新位于 Swing EDT。正式采集提交至 3 线程池中的单一循环任务，停止时通过
            Future 取消。电流监听运行于 jSerialComm 回调线程，主界面以约 200ms
            延时读取缓存。串口互斥依赖关闭后再打开的调用顺序，尚未抽象为独立调度器。
          </p>
          <ExtBox
            title="结构拓展：问表与用数拆开"
            tech="ScheduledExecutorService、ConcurrentHashMap、四线程池、synchronized 读 PLC"
          >
            构造时即{" "}
            <code>instrumentReader.startReading()</code>
            ，查询线程与串口监听线程常驻。四个工位各有线程池和{" "}
            <code>isTestingStarted1～4</code>
            。凸轮到位后只读缓存，不再 open/close 仪表口。四个循环都调用同一个{" "}
            <code>synchronized readAndProcessRegisters()</code>
            ，PLC 访问被串行化，避免四路同时打 COM4。这是多工位能跑起来的关键结构，也是最容易在停机写线圈时出现竞态的位置。
          </ExtBox>
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
          本系统实现了接点接触电阻试验的在线采集、实时监测、保护停机、故障记录与历史回放，形成“节拍同步采集—多仪表解析—双坐标曲线—数据库存储”的闭环。基础版的主要工作在于：在单串口、多从站条件下，以凸轮开关量为同步信号，将三路接触电阻与一路电流对齐至同一动作周期，并在电阻、电流、机构状态和试验次数四个维度实施自动停机。
        </p>
        <p>
          四工位拓展把这一闭环铺到四台设备上：控制走 COM4，测量走 COM5；八台仪表由统一调度器轮询入缓存；四个状态机并行，保护、曲线和台账都带设备号。难度从“一把口轮流问表”上升到“半双工调度 + 缓存一致 + 四套隔离状态机”。
        </p>
        <p>
          现有实现仍存在不足：拓展面板已调用分设备数据接口，而这批{" "}
          <code>ExecuteCommon</code> 仍是单记录版本；电流阈值 2.1A
          仍硬编码；源码中残留 COM5 的 PLC 对象，与仪表读取器同口；界面仍是固定分辨率绝对布局。后续宜把数据访问与四工位方法对齐，将全部保护阈值参数化入库，并把曲线存储拆成试验主表与采样点表。
        </p>
        <Callout title="核心工作表述" tone="idea">
          基础版解决的是单总线节拍对齐与四类保护；拓展版解决的是双总线隔离、八从站轮询和四工位状态隔离。答辩时建议把“问表与用数拆开”作为关键演进，而不是只强调多画了几张图。
        </Callout>
      </Section>
    </>
  );
}
