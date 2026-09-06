import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, Flow, KvTable, QABody, QAList, Section, Sub } from "../blocks";
import { ArrowDown, ChartNode, Figure, LayerStack, VChart } from "../flowchart";

export function PartOne() {
  return (
    <>
      <Section id="overview" kicker="第一节" title="系统概述">
        <p>
          该软件是一套<strong>电气接点寿命试验的自动测试与实时监测系统</strong>。系统通过
          PLC/IO 状态判断机械机构的动作阶段，在不同状态下分时读取三路接触电阻和一路电流，并将采集结果用于实时显示、趋势分析、异常判断、联锁停机、故障记录及历史追溯。
        </p>
        <p>
          从功能闭环看，软件实现了“设备控制—状态识别—参数采集—实时可视化—异常保护—数据存储—历史数据恢复”的完整测试过程。
          <code>ZzhejiPanel</code> 承担主界面与业务控制，
          <code>DianZu00000</code> 负责三路接触电阻采集，
          <code>JiaoL</code> 负责电流采集；同时还依赖{" "}
          <code>RRuANDWone</code>、<code>ExecuteCommon</code>、
          <code>JdbcDeal</code> 等控制与数据库类。包名为{" "}
          <code>com.ytzg.sealer</code>，图表标题为“接点接触电阻参数曲线”。
        </p>
        <Sub title="1.1 系统核心目标">
          <ul className="list-disc space-y-1 pl-5">
            <li>在试验过程中自动控制设备启停，减少人工反复操作。</li>
            <li>
              根据 PLC 输入状态识别机械动作位置，并在合适的动作阶段触发电阻或电流测量。
            </li>
            <li>实时采集三路接触电阻与一路工作电流，形成完整的多参数监测数据。</li>
            <li>将电阻、电流、速度、测试次数等信息实时显示并绘制趋势曲线。</li>
            <li>
              对电阻超限、电流超限、机械动作超时及达到设定测试次数等情况执行自动停机和报警。
            </li>
            <li>
              将正常测试数据与故障信息写入数据库，为后续查询、续测和质量追溯提供依据。
            </li>
          </ul>
        </Sub>
        <Callout title="系统定位" tone="idea">
          本系统既是数据采集系统，也是设备安全控制与质量监测系统。核心不是单独画图，而是以
          PLC 状态驱动测试时序，把三路微欧电阻和一路电流对齐到同一次机械动作上，并形成“采集—判定—控制”闭环。
        </Callout>
        <QAList
          items={[
            {
              q: "寿命曲线只是结果。真正的工程难点是什么，它为什么比“定时采数 + 画图”难一个量级？",
              a: (
                <QABody
                  contradiction={
                    <>
                      若按固定周期读全部传感器，凸轮未到位时电阻无意义，到位瞬间却可能正被 PLC
                      占着 COM3。采集、判定、停机必须落在同一次机械动作上，否则曲线好看也不能保护设备。
                    </>
                  }
                  implementation={
                    <>
                      软件用 PLC 两个输入字识别机械阶段，在 X1/X2
                      上升沿分时读取三路接触电阻和一路电流，当场与阈值比较并写停机线圈，形成“状态识别—分时采集—联锁停机—增量存盘”闭环。基础版把这件事挤在一把
                      COM3 上；4 拖 1 再拆成控制面 / 测量面，让四套状态机独立启停。第
                      17 节按矛盾—实现—边界展开。
                    </>
                  }
                  bound={
                    <>
                      难点不在 JFreeChart，而在半双工互斥、沿闭锁和线程模型。缺任何一环，都会出现误采、误停或界面卡死。
                    </>
                  }
                />
              ),
            },
          ]}
        />
      </Section>

      <Section id="arch" kicker="第二节" title="总体架构">
        <p>
          按软件职责划分为四层。人机交互层向操作人员展示状态并接收操作；业务控制层决定何时读设备、何时采集、何时报警；硬件通信层通过串口和
          Modbus 与 PLC、电阻模块、电流模块通信；数据持久层负责试验结果、累计次数和故障信息的保存与恢复。
        </p>
        <div className="grid gap-3 md:grid-cols-2">
          <Card>
            <CardHeader>
              <Badge variant="chart">人机交互层</Badge>
              <CardTitle className="pt-2 text-base">ZzhejiPanel</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              测试编号、日期、次数、速度、三路电阻、电流、操作按钮与双 Y 轴曲线。界面既是参数输入端，也是实时状态显示端。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="plc">业务控制层</Badge>
              <CardTitle className="pt-2 text-base">测试主循环</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              启停控制、状态判断、速度计算、超限判断、故障联锁、历史续测与增量保存。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="current">硬件通信层</Badge>
              <CardTitle className="pt-2 text-base">
                RRuANDWone / DianZu00000 / JiaoL
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              共用 COM3、9600、Modbus-RTU。PLC 平时占口，电阻仪和电流表在对应状态到位后再临时占用。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="db">数据持久层</Badge>
              <CardTitle className="pt-2 text-base">
                JdbcDeal / ExecuteCommon
              </CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              表 <code>d_dianzumax</code>、<code>allcount</code>、
              <code>test_results</code>、<code>policetime</code>
              。曲线批插带事务，故障记录供追溯。
            </CardContent>
          </Card>
        </div>
        <Figure no="2-1" title="系统四层结构">
          <LayerStack
            layers={[
              {
                title: "人机交互层　ZzhejiPanel",
                detail: "编号 / 日期 / 次数 / 速度 / 曲线 / 按钮",
                tone: "ui",
              },
              {
                title: "业务控制层",
                detail: "启停控制 · 状态判断 · 速度计算 · 超限判断 · 故障联锁 · 增量保存",
                tone: "comm",
              },
              {
                title: "硬件通信层　RRuANDWone / DianZu00000 / JiaoL",
                detail: "jSerialComm · Modbus RTU · COM3 9600",
                tone: "dev",
              },
              {
                title: "数据持久层　JdbcDeal / ExecuteCommon",
                detail: "test_results · policetime · d_dianzumax · allcount",
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
            Java 串口对象打开，因此必须“先关 PLC 口，再打开仪表口，读完立刻归还”。任何异常路径如果未及时释放端口，下一模块可能因
            COM3 被占用而无法通信。后续可设计统一 SerialPortManager 集中调度。
          </p>
          <QAList
            items={[
              {
                q: "三个 Java 串口对象都绑定 COM3，互斥条件是什么？异常路径未 close 会导致什么故障模式？",
                a: (
                  <QABody
                    contradiction={
                      <>
                        <code>DianZu00000</code>、<code>JiaoL</code>、
                        <code>RRuANDWone</code> 都写死{" "}
                        <code>getCommPort(&quot;COM3&quot;)</code>
                        。Windows 下同一物理口不能被多个实例同时打开；RS-485
                        半双工也不允许主站并发发两帧。现场又没有三把独立口可分。
                      </>
                    }
                    implementation={
                      <>
                        互斥靠调用顺序而不是锁：平时 PLC 占口巡检 X1/X2；上升沿先{" "}
                        <code>closeSerialPort()</code>，再让仪表类{" "}
                        <code>openPort</code>
                        ，读完立刻 close
                        归还。电阻侧打不开就 50ms 重试，正好抢 PLC
                        刚释放的窗口。这是单总线时分复用，访问对象由凸轮节拍决定。
                      </>
                    }
                    bound={
                      <>
                        协作式互斥没有统一{" "}
                        <code>SerialPortManager</code>
                        。保护停机、超时弹窗、线程取消任一条路径漏 close，下一模块会卡在{" "}
                        <code>waitForPort()</code>
                        ，表现为“灯亮了但不读数”。4 拖 1
                        正是为了消灭这条风险，把控制和测量拆到 COM4/COM5。
                      </>
                    }
                  />
                ),
              },
            ]}
          />
          <Figure no="2-2" title="COM3 分时复用流程">
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
        <Sub title="2.2 项目使用的主要技术">
          <KvTable
            rows={[
              {
                k: "Java Swing",
                v: "JPanel、JButton、JTextField、JComboBox 完成人机交互；FlatLaf 优化观感。",
              },
              {
                k: "JFreeChart",
                v: "三路电阻左轴、电流右轴。支持显隐、最近点查看、滚动和范围调整。",
              },
              {
                k: "jSerialComm",
                v: "与 PLC、电阻仪、电流表通信。基础版 COM3、9600 bit/s。",
              },
              {
                k: "Modbus RTU",
                v: "按地址、功能码、寄存器和 CRC16 组帧；校验失败的数据不参与判定。",
              },
              {
                k: "多线程",
                v: "ExecutorService / Future 跑正式测试；界面更新回到 EDT；AtomicBoolean 做沿闭锁。",
              },
              {
                k: "JDBC",
                v: "PreparedStatement 参数化。曲线批插关自动提交，成功 commit，失败 rollback。",
              },
              {
                k: "事件驱动",
                v: "按钮 ActionListener，文本框 DocumentListener，电流 SerialPortDataListener。",
              },
            ]}
          />
        </Sub>
      </Section>

      <Section id="features" kicker="第三节" title="系统总体功能">
        <p>
          操作人员在同一窗口完成编号录入、日期确认、测试启动、参数观察、曲线查看、结果保存和退出复位。后文第
          4 节至第 14 节按实现展开。
        </p>
        <div className="overflow-x-auto rounded-lg border border-border bg-white">
          <table className="w-full min-w-[36rem] text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2">序号</th>
                <th className="px-3 py-2">功能模块</th>
                <th className="px-3 py-2">主要作用</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {[
                ["1", "测试参数与界面管理", "显示编号、日期、次数、速度、累计次数、三路电阻、电流及操作按钮。"],
                ["2", "测试编号与历史数据管理", "支持输入新编号，也支持选择旧编号并加载历史曲线。"],
                ["3", "测试启动/停止控制", "按钮切换测试状态，并向 PLC 发送启停指令。"],
                ["4", "PLC 状态监测", "读取两个输入寄存器，作为电阻、电流采集和速度计算的触发条件。"],
                ["5", "三路接触电阻采集", "Modbus RTU 读取地址 2、3、4 三个测量通道。"],
                ["6", "电流采集", "串口事件监听方式读取电流检测模块。"],
                ["7", "实时曲线显示", "JFreeChart 构建三路电阻与一路电流双 Y 轴趋势图。"],
                ["8", "速度与次数统计", "用 PLC 状态间隔计算速度，并维护当前/累计测试次数。"],
                ["9", "故障检测与安全联锁", "电阻、电流、机构动作、测试次数异常时自动停机报警。"],
                ["10", "数据保存与追溯", "批量保存曲线，记录故障，支持历史回显和续测增量保存。"],
              ].map((row) => (
                <tr key={row[0]} className="border-t border-border align-top">
                  <td className="px-3 py-2 font-medium">{row[0]}</td>
                  <td className="px-3 py-2">{row[1]}</td>
                  <td className="px-3 py-2 text-slate-600">{row[2]}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <QAList
          items={[
            {
              q: "十个功能模块如何收敛成可答辩的技术主线，而不是功能清单？",
              a: (
                <QABody
                  contradiction={
                    <>
                      按钮、编号框、双轴图都可以单独讲，但它们不构成难度。评委追问的是：多源数据如何对齐、总线如何互斥、异常如何闭环。
                    </>
                  }
                  implementation={
                    <>
                      十条功能压成三条主线。其一，状态驱动：用 X1/X2
                      决定何时读电阻、何时读电流，而不是定时全量扫描。其二，多源协同：同步电阻、异步电流、PLC
                      巡检被编排成同一拍，再画到双 Y
                      轴。其三，联锁与追溯：每拍比较阈值并写线圈，曲线增量落{" "}
                      <code>test_results</code>，故障落{" "}
                      <code>policetime</code>。4 拖 1
                      是第三条主线的横向扩展：隔离从“一台上的模块”变成“四套状态机”。
                    </>
                  }
                  bound={
                    <>
                      第 4～16 节末尾的问—答都按“矛盾—实现—边界”写。第 17
                      节把两串口拖四工位、沿闭锁、增量游标等题收在一起。
                    </>
                  }
                />
              ),
            },
          ]}
        />
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
          解析 → 关闭串口”。主循环只有在第一状态位（X1）满足条件时才释放
          PLC 串口并调用本类，因此电阻采集是状态驱动，而不是定时全量轮询。
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
        <QAList
          items={[
            {
              q: "三块电阻表挂在同一 RS-485 上，如何避免把从站 3 的应答写入电阻 1，CRC 失败时又为何宁可丢数？",
              a: (
                <QABody
                  contradiction={
                    <>
                      半双工总线上三台从站会同时“听见”主机。若并发发三帧，应答重叠无法配对；若按到达顺序而不是按请求地址入表，邻站值会串进{" "}
                      <code>dianZValue1</code>，保护会停错通道。
                    </>
                  }
                  implementation={
                    <>
                      <code>readAndStoreValues()</code> 对 address = 2、3、4
                      串行发 03H（起始 1、数量 3），每问一路 sleep
                      约 50ms 再收。解析后 <code>switch (address)</code>{" "}
                      写入对应字段。CRC 失败返回 0
                      且不覆盖为邻站值——空失败优于错成功。
                    </>
                  }
                  bound={
                    <>
                      串行等待拉长了 X1
                      拍的占口时间，这段时间 PLC
                      看不到凸轮。三路单位实际共用{" "}
                      <code>dianZUnit1</code>
                      ，最后一路会覆盖显示单位；判定用{" "}
                      <code>extractNumber</code>{" "}
                      后的纯数值，单位不一致时人眼和机器会看到两种量纲。
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
