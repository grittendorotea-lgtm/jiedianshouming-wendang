import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, ExtBox, Flow, KvTable, Section, Sub } from "../blocks";

export function PartOne() {
  return (
    <>
      <Section id="overview" kicker="第一节" title="系统概述">
        <p>
          本系统为<strong>接点接触电阻在线监测与寿命试验软件</strong>。基础版本按凸轮节拍采集三路电阻与一路电流。后续拓展将主界面升级为四工位并行监测，并以
          <code className="mx-1 rounded bg-slate-100 px-1.5 py-0.5 text-[13px]">SixMeterInstrumentReader</code>
          在独立串口上统一轮询八台仪表（四路电流、四路电阻）。软件包名为
          <code className="mx-1 rounded bg-slate-100 px-1.5 py-0.5 text-[13px]">com.ytzg.sealer</code>
          ，主界面仍为 <code className="mx-1 rounded bg-slate-100 px-1.5 py-0.5 text-[13px]">ZzhejiPanel</code>（2026-08-11 拓展版约 7200 行）。
        </p>
        <p>
          两份 <code>ExecuteCommon.java</code> 源码一致，按同一数据访问类分析。系统闭环由以下四部分构成：
        </p>
        <ul className="list-disc space-y-1 pl-5">
          <li>
            <strong>ZzhejiPanel</strong>：基础为单工位；拓展版为四工位选项卡、四套状态机与四套曲线。
          </li>
          <li>
            <strong>SixMeterInstrumentReader</strong>：COM5 统一轮询电流 01～04、电阻 05～08。
          </li>
          <li>
            <strong>DianZu00000 / JiaoL</strong>：基础版节拍式问表，拓展后由统一读取器取代现场问表。
          </li>
          <li>
            <strong>ExecuteCommon</strong>：拓展后按设备号维护四套阈值、次数与曲线。
          </li>
        </ul>
        <Callout title="系统定位" tone="idea">
          本系统面向密封电器接点试验。上位机采用 Java Swing 与 JFreeChart
          实现人机界面和实时曲线，采用 jSerialComm 完成 RS-485 Modbus-RTU
          通信，采用 JDBC 持久化试验曲线与故障记录。其核心能力是节拍同步采集、多维保护停机与历史可追溯，而不仅是曲线显示。
        </Callout>
      </Section>

      <Section id="arch" kicker="第二节" title="总体架构">
        <p>
          软件按工业上位机惯例划分为三层。基础版通信层独占 COM3。拓展后改为双总线：COM4
          专供 PLC，COM5 专供八台仪表，避免控制通道与测量通道互相抢口。界面层扩展为四个监测设备选项卡，可同时开展四路寿命试验。
        </p>
        <div className="grid gap-3 md:grid-cols-3">
          <Card>
            <CardHeader>
              <Badge variant="chart">界面层</Badge>
              <CardTitle className="pt-2 text-base">ZzhejiPanel</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              四工位选项卡、分设备状态灯、四套编号/次数/曲线与独立启停。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="plc">通信层</Badge>
              <CardTitle className="pt-2 text-base">
                SixMeter + PLC 双总线
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              COM4 专访 PLC；COM5 由 SixMeterInstrumentReader
              以 100ms 节拍轮询八台仪表，结果写入并发缓存。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="db">数据层</Badge>
              <CardTitle className="pt-2 text-base">ExecuteCommon</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              表 <code>d_dianzumax</code>、<code>allcount</code>、
              <code>test_results</code>、<code>policetime</code>。曲线用 JDBC
              批处理一次写入。
            </CardContent>
          </Card>
        </div>
        <Sub title="单串口时分复用">
          <p>
            <code>DianZu00000</code>、<code>JiaoL</code>、
            <code>RRuANDWone</code> 都写死了{" "}
            <code>SerialPort.getCommPort(&quot;COM3&quot;)</code>。Windows
            下同一个物理口不能同时被三个对象打开，所以正式测试里的顺序是：
          </p>
          <Flow
            steps={[
              "平时由 RRuANDWone 打开 COM3，循环读 PLC 输入寄存器，看 X1/X2 凸轮信号。",
              "X1=1 时先 closeSerialPort()，再让 DianZu00000 打开串口读三路电阻，读完立刻关闭。",
              "X2=1 时同样先关掉 PLC 口，再让 JiaoL 读电流，读完关闭。",
              "外层循环继续，下一次再打开 PLC 口监测下一拍。",
            ]}
          />
          <p>
            该约束可概括为单总线时分复用：RS-485
            上同时挂接 PLC 与多块仪表，上位机依据开关量节拍决定当前访问对象。
          </p>
        </Sub>
        <ExtBox
          title="架构拓展：双总线 + 统一仪表调度"
          tech="双串口隔离、ScheduledExecutorService 周期轮询、ConcurrentHashMap 无锁缓存、生产者-消费者解耦"
        >
          拓展后不再在试验节拍里临时打开/关闭仪表口。
          <code> SixMeterInstrumentReader </code>
          在 COM5 上以 100ms 节拍轮询地址 01～08，一整轮约
          800ms；应答经 CRC 校验后按从站地址分流写入并发缓存。四个工位的试验循环只读取最新缓存，不再阻塞总线。这把“问表”和“用数”拆开，是从单工位演示程序演进到多工位实时系统的关键难点。
        </ExtBox>
      </Section>

      <Section id="features" kicker="第三节" title="功能组成">
        <p>
          按可独立描述的业务能力归纳为 15 项。下表同时给出四工位八表拓展，以及使该功能变难的关键技术。
        </p>
        <div className="overflow-x-auto rounded-lg border border-border bg-white">
          <table className="w-full min-w-[52rem] text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2">编号</th>
                <th className="px-3 py-2">功能</th>
                <th className="px-3 py-2">四工位八表拓展</th>
                <th className="px-3 py-2">关键技术难点</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {[
                ["F1", "接触电阻在线采集", "四工位绑定电阻从站 05～08，后台持续刷新缓存", "半双工时分轮询、按应答地址分流、CRC 拒噪"],
                ["F2", "回路电流在线采集", "四工位绑定电流从站 01～04，与电阻同总线交错查询", "8 从站共线、100ms 节拍、监听与查询并发"],
                ["F3", "PLC 运行控制与报警", "COM4 独立控制；寄存器 1～4 分控四台，寄存器 5 公共报警", "双总线隔离、多线圈交错写、停机时序"],
                ["F4", "凸轮到位信号监测", "一次读取 8 路输入，为四工位提供两路凸轮状态", "04H 多寄存器解析、功能码重试、四套沿闭锁"],
                ["F5", "节拍同步试验控制", "四个独立状态机并行，节拍到点只取缓存", "四线程池、大量 AtomicBoolean、控制与测量解耦"],
                ["F6", "动作速度实时测算", "四路独立计时，另有窗口为 5 的移动平均方案", "多通道计时、抖动抑制、EDT 刷新"],
                ["F7", "接触电阻超限保护", "四套阈值 dianzumax1～4，超限停对应工位并亮红灯", "分设备参数、停机写多线圈、状态灯联动"],
                ["F8", "电流超限保护", "四路电流各自与 2.1A 比较，互不误停", "缓存一致性、并发试验下的通道隔离"],
                ["F9", "凸轮卡滞超时保护", "四工位各有 12s 超时，故障带设备号入库", "8 路计时器、公共报警寄存器复用"],
                ["F10", "试验次数上限控制", "四套 timeSeconds 与 testcount1～4 独立截止", "四套横坐标状态、次数与累计次数分离"],
                ["F11", "双纵坐标实时曲线", "四张独立图表、四套监听器与滚动条", "JTabbedPane、四数据集、EDT 批量重绘"],
                ["F12", "试验数据批量存储", "四工位分别批插并带设备号，SwingWorker 防卡死", "后台事务、LoadingGif、按设备查重"],
                ["F13", "历史曲线按编号回放", "各工位独立编号与曲线对象", "多系列状态隔离、避免串台回放"],
                ["F14", "参数与累计次数管理", "四套阈值、最大次数与累计次数", "多记录参数表、启动时分别加载"],
                ["F15", "故障停机记录", "增加 Shebeihao，区分哪一台设备停机", "分设备入库、电阻/电流/凸轮三类故障"],
              ].map((row) => (
                <tr key={row[0]} className="border-t border-border align-top">
                  <td className="px-3 py-2 font-medium">{row[0]}</td>
                  <td className="px-3 py-2">{row[1]}</td>
                  <td className="px-3 py-2">{row[2]}</td>
                  <td className="px-3 py-2 text-slate-500">{row[3]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <Callout title="为何拓展后实现难度显著上升" tone="idea">
          半双工 RS-485 同一时刻只能一问一答。八台仪表必须排队访问，四个工位又要几乎同时拿到当前值。再叠加四套保护、四套曲线和独立
          PLC 线圈，任一通道错位都会误停机或画错曲线。难点不在多画几根线，而在总线调度、缓存一致性和多工位状态隔离。
        </Callout>
      </Section>

      <Section
        id="r-collect"
        kicker="第四节"
        title="接触电阻采集"
      >
        <p>
          类 <code>com.ytzg.sealer.plc.test.DianZu00000</code>{" "}
          专门负责三块电阻表。构造时绑定 COM3、波特率
          9600。它不用监听器持续收数，而是“打开串口 → 按地址轮询 →
          解析 → 关闭串口”，刚好配合主界面的节拍式采集。
        </p>
        <Sub title="1）打开串口：失败就短间隔重试">
          <p>
            <code>startReading()</code>{" "}
            是一个死循环。口没打开就
            <code>openPort()</code>，失败则
            <code>waitForPort()</code> 睡 50ms 再试；口已被占用也等待。成功打开后
            <code>break</code>。这是在跟 PLC 抢同一条 COM3
            时的典型写法：PLC 刚关掉口，电阻仪这边马上能抢到。
          </p>
        </Sub>
        <Sub title="2）一次读三路：从站地址 2、3、4">
          <p>
            <code>readAndStoreValues()</code> 对 address = 2、3、4
            各发一帧 Modbus 读保持寄存器：功能码{" "}
            <code>0x03</code>，起始地址 1，数量 3。返回值分别写入
            <code>dianZValue1/2/3</code>。界面上的“接触电阻1/2/3”就是这三路。
          </p>
          <CodeBlock title="DianZu00000.readAndStoreValues() 核心逻辑">
            {`for (int address = 2; address <= 4; address++) {
    byte[] response = readRegisters((byte) address);
    if (response.length > 0) {
        double resistanceValue = parseSensorData(response);
        switch (address) {
            case 2: dianZValue1 = resistanceValue; break;
            case 3: dianZValue2 = resistanceValue; break;
            case 4: dianZValue3 = resistanceValue; break;
        }
    }
}`}
          </CodeBlock>
        </Sub>
        <Sub title="3）一帧请求怎么拼">
          <p>
            <code>buildModbusRequest(address, 0x03, 1, 3)</code>{" "}
            生成标准 8 字节 RTU 帧：
          </p>
          <KvTable
            rows={[
              { k: "字节0", v: "从站地址。电阻表分别是 2、3、4。" },
              { k: "字节1", v: "功能码 0x03，读保持寄存器。" },
              { k: "字节2-3", v: "起始寄存器地址 1，高字节在前。" },
              { k: "字节4-5", v: "寄存器个数 3。" },
              {
                k: "字节6-7",
                v: "Modbus CRC-16，低字节在前。算法初值 0xFFFF，多项式 0xA001。",
              },
            ]}
          />
          <p>
            发出后 <code>Thread.sleep(50)</code>{" "}
            等仪表回答。若可用字节少于 9，认为帧不完整，返回空数组，这一路电阻保持上次值或
            0。
          </p>
        </Sub>
        <Sub title="4）应答怎么还原成带单位的欧姆值">
          <p>
            <code>parseSensorData()</code> 先算 CRC，和帧尾两字节比较，失败直接返回
            0。通过后按仪表自定义寄存器布局取值：
          </p>
          <ul className="list-disc space-y-1 pl-5">
            <li>
              <code>rawValue = (data[3]&lt;&lt;8) | data[4]</code>：16
              位原始整数。
            </li>
            <li>
              <code>decimalPlaces = data[6]</code>：小数点位数。
            </li>
            <li>
              <code>unitCode = data[8]</code>：单位代码。
            </li>
            <li>
              实际值 = <code>rawValue / 10^decimalPlaces</code>。
            </li>
          </ul>
          <p>单位码约定：3 = mΩ，4 = Ω，5 = kΩ。显示时用</p>
          <CodeBlock>{`String.format("%.2f %s", dianZValue1, dianZUnit1)`}</CodeBlock>
          <p>
            例如原始值 1234、小数位 2、单位码 3，就显示成{" "}
            <strong>12.34 mΩ</strong>。主界面再用正则
            <code>extractNumber()</code> 把数字抠出来，和阈值比较、往曲线上加点。
          </p>
        </Sub>
        <Callout title="实现细节：三路单位实际共用了一个字段" tone="warn">
          <code>parseResistanceValue()</code> 每次都写{" "}
          <code>dianZUnit1</code>，<code>getDianZUnit()</code>{" "}
          也只返回这个字段。三路读完后，单位会变成最后一路的单位。如果三块表量程单位一致（都是
          mΩ），现场看不出来；写报告时可以提一句“当前实现默认三路单位相同”。
        </Callout>
        <ExtBox
          title="F1 拓展：四路电阻改为后台缓存读取"
          tech="Modbus-RTU 03H、CRC-16、BigDecimal 定点还原、ConcurrentHashMap、按从站地址分流"
        >
          拓展后电阻表地址改为 05～08，分别对应监测设备 1～4。
          <code>SixMeterInstrumentReader</code>{" "}
          不再在凸轮到位时同步阻塞问表，而是持续轮询；工位 1 取{" "}
          <code>getResistanceValue(5)</code>
          ，工位 2/3/4 取 6/7/8。解析用 BigDecimal 左移小数位，单位扩展到
          mΩ/Ω/kΩ/MΩ。难点在于：半双工总线上八台表交错应答，必须用应答帧的{" "}
          <code>data[0]</code>{" "}
          识别来源，错误地址或 CRC 失败绝不能写入别的工位缓存，否则会出现“张冠李戴”的误判。
        </ExtBox>
      </Section>
    </>
  );
}
