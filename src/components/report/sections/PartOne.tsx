import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, Flow, KvTable, Section, Sub } from "../blocks";
import { ArrowDown, ChartNode, Figure, LayerStack, VChart } from "../flowchart";

export function PartOne() {
  return (
    <>
      <Section id="overview" kicker="第一节" title="系统概述">
        <p>
          本软件是密封电器、继电器接点的<strong>接触电阻在线监测与寿命试验上位机</strong>。试品触点周期性开合时，上位机按凸轮节拍采集三路接触电阻与一路回路电流，生成“动作次数—电阻/电流”曲线，并在电阻超限、电流超限、凸轮卡滞或达到设定次数时自动停机、记录故障。试验曲线和故障记录写入数据库，可按测试编号回放。软件包名为
          <code className="mx-1 rounded bg-slate-100 px-1.5 py-0.5 text-[13px]">com.ytzg.sealer</code>
          ，主界面为 <code className="mx-1 rounded bg-slate-100 px-1.5 py-0.5 text-[13px]">ZzhejiPanel</code>，图表标题为“接点接触电阻参数曲线”。
        </p>
        <p>
          两份 <code>ExecuteCommon.java</code> 源码一致，按同一数据访问类分析。系统闭环由以下四部分构成：
        </p>
        <ul className="list-disc space-y-1 pl-5">
          <li>
            <strong>ZzhejiPanel</strong>：测试主界面、测试状态机、曲线、保存、报警、历史回显。
          </li>
          <li>
            <strong>DianZu00000</strong>：按 Modbus 轮询从站 2、3、4，读三路电阻。
          </li>
          <li>
            <strong>JiaoL</strong>：按 Modbus 读从站 1 的电流。
          </li>
          <li>
            <strong>ExecuteCommon</strong>：读写阈值、最大次数、累计次数，并批量保存曲线点。
          </li>
        </ul>
        <Callout title="系统定位" tone="idea">
          本系统面向密封电器接点试验。上位机采用 Java Swing 与 JFreeChart
          实现人机界面和实时曲线，采用 jSerialComm 完成 RS-485 Modbus-RTU
          通信，采用 JDBC 持久化试验曲线与故障记录。其核心工作不是单独画图，而是在单串口多从站条件下，把三路微欧电阻和一路电流对齐到同一次动作上，并在电阻、电流、机构、次数四个维度自动停机。
        </Callout>
      </Section>

      <Section id="arch" kicker="第二节" title="总体架构">
        <p>
          软件按工业上位机惯例划分为三层：人机交互层负责显示与操作；设备通信层独占
          COM3，按时间片依次访问 PLC、电阻仪与电流表；数据访问层将参数、曲线和故障记录写入数据库。
        </p>
        <div className="grid gap-3 md:grid-cols-3">
          <Card>
            <CardHeader>
              <Badge variant="chart">界面层</Badge>
              <CardTitle className="pt-2 text-base">ZzhejiPanel</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              测试编号、日期、次数、速度、三路电阻、一路电流、开始/停止/保存、双
              Y 轴曲线、滚动条、曲线显隐。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="plc">通信层</Badge>
              <CardTitle className="pt-2 text-base">
                DianZu00000 / JiaoL / RRuANDWone
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              全部挂在 COM3、9600。PLC
              负责启停和凸轮开关量；电阻仪、电流表在 PLC
              给出节拍后再临时占用串口。
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
        <Figure no="2-1" title="系统三层结构">
          <LayerStack
            layers={[
              {
                title: "人机交互层　ZzhejiPanel",
                detail: "编号、日期、次数、速度、三路电阻、一路电流、启停保存、双 Y 轴曲线",
                tone: "ui",
              },
              {
                title: "设备通信层　DianZu00000 / JiaoL / RRuANDWone",
                detail: "共用 COM3、9600、Modbus-RTU；按凸轮节拍轮流占用串口",
                tone: "comm",
              },
              {
                title: "数据访问层　ExecuteCommon",
                detail: "d_dianzumax、allcount、test_results、policetime",
                tone: "data",
              },
            ]}
          />
        </Figure>
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
            上同时挂接 PLC 与多块仪表，上位机依据开关量节拍决定当前访问对象。同一物理口不能同时被三个
            Java 串口对象打开，因此必须“先关 PLC 口，再打开仪表口，读完立刻归还”。
          </p>
          <Figure no="2-2" title="单串口时分复用流程">
            <VChart>
              <ChartNode kind="start">PLC 占用 COM3，循环读 X1/X2</ChartNode>
              <ArrowDown />
              <ChartNode>X1 到位：关 PLC 口</ChartNode>
              <ArrowDown />
              <ChartNode kind="io">DianZu00000 读从站 2/3/4</ChartNode>
              <ArrowDown />
              <ChartNode>关电阻仪口，交还 PLC</ChartNode>
              <ArrowDown />
              <ChartNode>X2 到位：关 PLC 口</ChartNode>
              <ArrowDown />
              <ChartNode kind="io">JiaoL 读从站 1</ChartNode>
              <ArrowDown />
              <ChartNode kind="end">关电流表口，进入下一拍</ChartNode>
            </VChart>
          </Figure>
        </Sub>
      </Section>

      <Section id="features" kicker="第三节" title="功能组成">
        <p>
          按可独立描述的业务能力归纳，系统实现以下 15 项功能。后文第 4 节至第 14 节说明其实现方法。
        </p>
        <div className="overflow-x-auto rounded-lg border border-border bg-white">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2">编号</th>
                <th className="px-3 py-2">功能</th>
                <th className="px-3 py-2">主要实现位置</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {[
                ["F1", "三路接触电阻在线采集", "DianZu00000"],
                ["F2", "回路电流在线采集", "JiaoL"],
                ["F3", "PLC 运行控制与报警输出", "RRuANDWone"],
                ["F4", "凸轮到位信号监测", "readAndProcessRegisters"],
                ["F5", "节拍同步试验控制", "ZzhejiPanel 试验循环"],
                ["F6", "动作速度实时测算", "v = 120 / Δt"],
                ["F7", "接触电阻超限保护", "pureValue 与 dianzumax"],
                ["F8", "电流超限保护", "dianliu1Value1 > 2.1 A"],
                ["F9", "凸轮卡滞超时保护", "X1/X2 无效超过 12 s"],
                ["F10", "试验次数上限控制", "timeSeconds 与 testcount"],
                ["F11", "双纵坐标实时曲线监测", "JFreeChart"],
                ["F12", "试验数据批量存储与追加", "saveTestResultsBatch"],
                ["F13", "历史曲线按编号回放", "loadSelectedTestResultToChart"],
                ["F14", "试验参数与累计次数管理", "d_dianzumax / allcount"],
                ["F15", "故障停机记录", "policetime"],
              ].map((row) => (
                <tr key={row[0]} className="border-t border-border">
                  <td className="px-3 py-2 font-medium">{row[0]}</td>
                  <td className="px-3 py-2">{row[1]}</td>
                  <td className="px-3 py-2 text-slate-500">{row[2]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
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
      </Section>
    </>
  );
}
