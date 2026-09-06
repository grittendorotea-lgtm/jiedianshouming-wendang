import { Callout, CodeBlock, Flow, KvTable, QAList, Section, Sub } from "../blocks";
import {
  ArrowDown,
  ChartNode,
  Decision,
  Figure,
  Split,
  VChart,
} from "../flowchart";

export function PartTwo() {
  return (
    <>
      <Section id="i-collect" kicker="第五节" title="电流采集">
        <p>
          类 <code>com.ytzg.sealer.plc.JiaoL</code>{" "}
          读的是电流检测模块。界面标签写“电流”，单位是 mA / A。它和电阻类共用同一套
          CRC 和组帧方法，但收数改为{" "}
          <code>SerialPortDataListener</code>{" "}
          监听 <code>LISTENING_EVENT_DATA_AVAILABLE</code>
          。主循环在第二状态位（X2）有效时才启动本类：电阻是同步轮询，电流是事件到达后再解析。
        </p>
        <Sub title="1）打开口后只发一帧，靠监听器收应答">
          <p>
            <code>startReading()</code> 打开 COM3，立刻写一帧：
          </p>
          <CodeBlock title="JiaoL 的请求">{`byte[] request = buildModbusRequest((byte) 0x01, (byte) 0x03, 1, 3);
comPort.writeBytes(request, request.length);`}</CodeBlock>
          <p>
            从站地址是 <strong>1</strong>，功能码仍是 0x03，起始 1、数量
            3。然后挂上 <code>SerialPortDataListener</code>，事件为{" "}
            <code>LISTENING_EVENT_DATA_AVAILABLE</code>。有数据时先
            <code>sleep(100)</code> 等帧收齐，再
            <code>processSensorData()</code>。
          </p>
          <p>
            主界面用法更干脆：<code>JiaoL.startReading()</code> →{" "}
            <code>Sleep.sleepTh(200)</code> →{" "}
            <code>getJiaoLZValueWithUnit()</code> →{" "}
            <code>stopReading()</code>。也就是“问一次、等 200ms、取缓存值、关口”，不是长期后台采集。
          </p>
        </Sub>
        <Sub title="2）电流值的解析规则">
          <p>
            帧结构和电阻几乎一样：原始值、小数位、单位码、CRC。差别在单位码：
          </p>
          <KvTable
            rows={[
              { k: "unitCode = 3", v: "单位 mA，值 = 原始值 / 10^小数位" },
              { k: "unitCode = 4", v: "单位 A，值 = 原始值 / 10^小数位" },
              { k: "其他", v: "打印未知单位，返回 0，界面电流按 0 处理" },
            ]}
          />
          <p>
            对外两个 getter：<code>getJiaoLValue()</code> 给纯数字，
            <code>getJiaoLZValueWithUnit()</code> 给 <code>&quot;1.85 A&quot;</code>{" "}
            这种字符串。主界面用空格拆开第一段，转成{" "}
            <code>dianliu1Value1</code>，再和 2.1A 比较。
          </p>
        </Sub>
        <Callout title="与电阻采集的差异" tone="note">
          电阻采集为同步轮询、三从站、单位 mΩ/Ω/kΩ；电流采集为异步监听、单从站地址
          1、单位 mA/A。二者共用 COM3、9600、功能码 0x03 及同一 CRC
          算法，并由上位机在关闭 PLC 通道后依次访问。
        </Callout>
        <QAList
          items={[
            {
              q: "电流用监听、电阻用轮询，怎样保证问完一次就能关口、还不卡界面？",
              a: (
                <>
                  <code>JiaoL.startReading()</code> 打开 COM3 后只发一帧 03H，靠{" "}
                  <code>SerialPortDataListener</code> 收应答，监听里{" "}
                  <code>sleep(100)</code> 等整帧。主界面再{" "}
                  <code>Sleep.sleepTh(200)</code>，取{" "}
                  <code>getJiaoLZValueWithUnit()</code>，立刻{" "}
                  <code>stopReading()</code>
                  。这是“问一次、等缓存、关口”，不是长期后台占口。单位码不是
                  3/4 则返回 0，避免脏数据拿去和 2.1 A 比较。
                </>
              ),
            },
            {
              q: "电阻同步、电流异步，怎样对齐成同一个采样点？",
              a: (
                <>
                  电流先写入隐藏框 <code>dianzu444NO</code>
                  ，电阻读完再抄到显示框 <code>dianzu444</code>
                  。四个显示框共用{" "}
                  <code>DocumentListener</code>
                  ，三路电阻和一路电流都非空，才在同一{" "}
                  <code>timeSeconds</code> 上加点。
                </>
              ),
            },
          ]}
        />
      </Section>

      <Section
        id="plc"
        kicker="第六节"
        title="PLC通信与控制"
      >
        <p>
          <code>readAndProcessRegisters()</code>{" "}
          是整个测试时序的关键方法。PLC 通信类{" "}
          <code>RRuANDWone</code> 源码不在这批文件里，但主界面把它用得很完整：设备地址{" "}
          <code>0x08</code>，功能码 <code>0x04</code>，从寄存器 0 起读 2
          个字，并验证应答功能码确为 0x04。这两个状态不是单纯用于显示，而是决定“什么时候读电阻”“什么时候读电流”，同时还参与机械超时判定和速度计算。系统本质上是<strong>状态驱动式采集</strong>，而不是固定时间间隔轮询全部传感器。
        </p>
        <Sub title="1）读到的两个字分别是什么">
          <CodeBlock title="ZzhejiPanel.readAndProcessRegisters()">{`response = ruAndwone.readInputRegisters(0x08, 0, 2);
int value  = ((response[3] & 0xFF) << 8) | (response[4] & 0xFF); // X1
int value5 = ((response[5] & 0xFF) << 8) | (response[6] & 0xFF); // X2
return new Result(value, value5);`}</CodeBlock>
          <KvTable
            rows={[
              {
                k: "value / X1",
                v: "右侧凸轮（或右侧行程开关）。1=到位/亮，0=离开/灭。X1 变 1 时去读三路电阻。",
              },
              {
                k: "value2 / X2",
                v: "左侧凸轮。1 时去读电流。注释里写的是“555 亮了/灭了”。",
              },
              {
                k: "写寄存器 1",
                v: "设备运行控制。开始测试写 0，停机/故障写 1。",
              },
              {
                k: "写寄存器 2",
                v: "报警输出。故障时写 1，手动停止写 0。",
              },
            ]}
          />
        </Sub>
        <Sub title="2）开始测试时对 PLC 做了什么">
          <p>点“开始测试”后立刻：</p>
          <Flow
            steps={[
              "isTestingStarted 取反为 true，标记 isDataUnsaved。",
              "ruAndwone.openSerialPort()，writeSingleRegister(1, 0) 让设备转起来。",
              "从数据库刷新电阻上限 dianzumax、次数上限 testcount。",
              "按钮改成红色“停止测试”，并 startListening()，开始盯四个电阻/电流文本框。",
              "把正式采集循环丢进 3 线程池 executorServicebingxing。",
            ]}
          />
        </Sub>
        <Sub title="3）停止测试时对 PLC 做了什么">
          <p>
            再点一次按钮走 else 分支：用{" "}
            <code>currentTestFuture.cancel(true)</code>{" "}
            打断后台循环，清零两路超时计时，打开串口{" "}
            <code>writeSingleRegister(2, 0)</code>{" "}
            消报警，按钮改回蓝色“开始测试”，并{" "}
            <code>stopListening()</code>。注意这里没有再写寄存器 1
            为 0，注释掉了，所以手动停止主要是停软件循环和关报警，设备运行位的处理以现场调试为准。
          </p>
        </Sub>
        <QAList
          items={[
            {
              q: "PLC 只给出两个输入字，怎样同时驱动采集、速度和机械超时？",
              a: (
                <>
                  <code>readAndProcessRegisters()</code> 用功能码 04H
                  从地址 0x08 读 2 个字：X1 右侧凸轮、X2
                  左侧凸轮。X1 上升沿去读三路电阻并刷新速度；X2
                  上升沿去读电流。任一凸轮长时间为 0
                  就判超时。开始测试写寄存器 1 为 0 让设备转；故障写寄存器 1、2
                  为 1。这是状态驱动，不是按固定周期扫全部传感器。
                </>
              ),
            },
            {
              q: "点“停止测试”为什么主要写报警位，运行位却被注释掉？",
              a: (
                <>
                  else 分支用 <code>currentTestFuture.cancel(true)</code>{" "}
                  打断后台循环，清超时计时，打开串口{" "}
                  <code>writeSingleRegister(2, 0)</code>{" "}
                  消报警。寄存器 1 写 0 的语句被注释，所以手动停侧重停软件循环和关报警，设备运行位以现场调试为准。保护停机路径才会明确把运行位写成
                  1。
                </>
              ),
            },
          ]}
        />
      </Section>

      <Section
        id="loop"
        kicker="第七节"
        title="试验流程控制"
      >
        <p>
          试验过程由双层循环实现。外层 <code>continueLoop2</code>{" "}
          控制本次试验是否继续，内层 <code>continueLoop</code>{" "}
          等待本拍电阻采集完成。源码注释明确要求先采集电阻、再采集电流，以避免电流信号保持有效时重复读表。软件不是定时全量采集，而是根据两个
          PLC 状态位区分不同机械阶段，使采样与机构运动位置同步。
        </p>
        <Sub title="内循环在等什么">
          <p>
            每圈调用 <code>readAndProcessRegisters()</code> 拿到 X1、X2。
          </p>
          <p>
            <strong>当 X1==1 且本拍还没处理过（processed2 为 false）</strong>
          </p>
          <Flow
            steps={[
              "processed2 置 true，避免同一拍重复读电阻。",
              "关闭 PLC 串口。",
              "dianZu.startReading() 打开电阻仪口。",
              "readAndStoreValues() 依次问地址 2、3、4。",
              "取出带单位字符串，extractNumber() 得到 pureValue1/2/3。",
              "在 EDT 线程里刷新三个电阻框；电流框先抄隐藏框 dianzu444NO 的值。",
              "关掉电阻仪口，内循环结束（continueLoop=false）。",
              "记录 lastTime111000，供右侧凸轮超时计时。",
            ]}
          />
          <p>
            <strong>当 X1==0</strong>：认为右侧凸轮还没到位。若这一状态持续超过
            12 秒且小于 10000 秒，判定“设备右侧凸轮转动故障”：写报警寄存器、弹窗、写
            policetime、内外循环都停。
          </p>
          <p>
            <strong>当 X2==1 且 processed555111 为 false</strong>
            ：关 PLC 口，<code>JiaoL.startReading()</code>，睡
            200ms，取电流字符串，拆出数字到{" "}
            <code>dianliu1Value1</code>，写入隐藏框{" "}
            <code>dianzu444NO</code>。这一拍电流采完。
          </p>
          <p>
            <strong>当 X2==0</strong>：左侧凸轮离开。超过 12
            秒则报“设备左侧凸轮转动故障”，同样停机入库。
          </p>
        </Sub>
        <Sub title="外循环在检查什么">
          <p>内循环结束后，外循环立刻做三道门禁，任一成立就停整次试验：</p>
          <ul className="list-disc space-y-1 pl-5">
            <li>三路电阻任一路 &gt; 数据库里的 dianzumax</li>
            <li>电流 &gt; 2.1 A</li>
            <li>已测次数 timeSeconds ≥ 设定最大次数 testcount</li>
          </ul>
          <p>
            然后 <code>Sleep.sleepTh(100)</code>，再开下一拍。整次试验就是“等凸轮
            → 换串口读表 → 刷新界面 → 判超限”的循环。
          </p>
        </Sub>
        <Figure no="7-1" title="试验主循环">
          <VChart>
            <ChartNode kind="start">开始测试：PLC 寄存器 1 写 0</ChartNode>
            <ArrowDown />
            <ChartNode>读 X1 / X2</ChartNode>
            <ArrowDown />
            <Decision>X1 上升沿？</Decision>
            <Split
              left={
                <VChart>
                  <ChartNode kind="io">读三路电阻</ChartNode>
                </VChart>
              }
              right={
                <VChart>
                  <ChartNode>判右侧凸轮是否超时</ChartNode>
                </VChart>
              }
            />
            <ArrowDown />
            <Decision>X2 上升沿？</Decision>
            <Split
              left={
                <VChart>
                  <ChartNode kind="io">读一路电流</ChartNode>
                </VChart>
              }
              right={
                <VChart>
                  <ChartNode>判左侧凸轮是否超时</ChartNode>
                </VChart>
              }
            />
            <ArrowDown />
            <ChartNode>四路齐备则曲线加点，次数 +2</ChartNode>
            <ArrowDown />
            <Decision>超限或到次数？</Decision>
            <Split
              left={<ChartNode kind="end">停机报警入库</ChartNode>}
              right={<ChartNode>延时 100ms，下一拍</ChartNode>}
            />
          </VChart>
        </Figure>
        <Sub title="操作员完整流程">
          <p>
            操作员进入接点接触电阻试验界面后，系统自动加载电阻停机阈值、累计动作次数和历史试验编号。操作员输入或选择试验编号后启动测试，上位机向
            PLC 发出运行指令，设备凸轮开始转动。每当右侧行程开关有效，系统释放串口并依次读取三路接触电阻；每当左侧行程开关有效，系统读取回路电流。四路数据同时刷新到界面后，折线图在当前动作次数处增加一个采样点，并按相邻右侧信号间隔计算动作速度。试验过程中若出现电阻超限、电流超限、凸轮超时或次数到达设定值，系统立即下达停机和报警指令。试验结束后，操作员可将本次曲线按编号保存；若编号已存在，则只追加新采样点，从而实现同一试品的分段试验与连续回放。退出时须复位沿标志、计时器和串口，避免下次误判凸轮超时或跳过采集。
          </p>
          <Flow
            steps={[
              "进入界面：读取电阻上限、累计次数，去重填充编号下拉框，日期默认今天。",
              "填写或选择测试编号。旧号回显历史曲线；新号不清空正在画的曲线。",
              "点“开始测试”：PLC 寄存器 1 写 0，后台线程盯 X1/X2。",
              "右侧凸轮到位读三路电阻，左侧到位读电流；四框齐了则加点，次数 +2，速度按 120/Δt 更新。",
              "保护条件成立则停机、报警、弹窗，必要时写 policetime。",
              "点“停止测试”打断任务并消报警；再点“保存”，新号全量、旧号追加。",
              "点“退出”走 resetForNextEnter()：停任务、关串口、清曲线和沿标志，回到 Home。",
            ]}
          />
        </Sub>
        <Callout title="曲线刷新方式" tone="note">
          采集线程不直接调用 <code>series.add()</code>，仅更新文本框。四个显示框共用{" "}
          <code>DocumentListener</code>，在文本插入且四路均非空时由{" "}
          <code>updateChart()</code>{" "}
          完成加点。通信成功与曲线更新通过界面文本解耦。
        </Callout>
        <QAList
          items={[
            {
              q: "为什么必须先采电阻、再采电流，还要内外两层循环？",
              a: (
                <>
                  源码注释要求先电阻、后电流，避免电流信号保持有效时反复问表。内层{" "}
                  <code>continueLoop</code> 等到本拍电阻采完才退出；外层{" "}
                  <code>continueLoop2</code>{" "}
                  立刻做电阻超限、电流超限、次数上限三道门禁。同一凸轮持续为
                  1 时，靠 <code>processed2</code>、
                  <code>processed555111</code> 闭锁，回到 0 才清标志。
                </>
              ),
            },
            {
              q: "串口等待会不会把界面卡死？采集线程为什么不直接往图上加点？",
              a: (
                <>
                  正式循环丢进 <code>executorServicebingxing</code>，停止用{" "}
                  <code>Future.cancel(true)</code>
                  。界面刷新一律{" "}
                  <code>SwingUtilities.invokeLater</code>
                  。采集线程只改文本框，由监听器在 EDT 上{" "}
                  <code>updateChart()</code>
                  ，避免违反 Swing 单线程规则。
                </>
              ),
            },
          ]}
        />
      </Section>

      <Section id="speed" kicker="第八节" title="动作速度测算">
        <p>
          界面“速度”框只读，单位标签是“次/分钟”。计算藏在{" "}
          <code>readAndProcessRegisters()</code> 里，不单独开定时器。
        </p>
        <p>
          每次读到 <code>value==1</code>（X1
          亮）且上一拍已经灭过（<code>processedjishi1</code> 为
          false），就认为来了一个新的动作沿。用当前毫秒时间减{" "}
          <code>lastTime111111</code> 得到间隔 Δt（秒）。若 Δt &gt; 2
          秒，才认作一次有效间隔，避免开关抖动：
        </p>
        <CodeBlock title="速度公式">{`double frequency = 120.0 / timeDifference;
roundedFrequency111 = Math.round(frequency);
suduTest.setText(String.valueOf(roundedFrequency111));`}</CodeBlock>
        <p>
          公式在代码里明确为 <strong>v = 120 / Δt</strong>
          。若两次 X1 有效间隔是 2 秒，显示就是 60 次/分钟。曲线横坐标每次加点后{" "}
          <code>timeSeconds += 2</code>
          ，与“一个采样点折算 2 次动作”的保存逻辑一致。系数 120
          的机械物理含义需结合实际机构确认，例如是否一次完整循环与该状态信号存在半周期关系；报告中不应在缺少硬件依据时自行解释。
        </p>
        <p>
          X1 回到 0 时把 <code>processedjishi1</code>{" "}
          清掉，这样下一次再亮才会重新计时。这是标准的上升沿检测。
        </p>
        <QAList
          items={[
            {
              q: "速度为什么写成 120/Δt，开关抖动时会不会把次数刷飞？",
              a: (
                <>
                  代码明确是 <code>v = 120.0 / timeDifference</code>
                  。两次 X1 间隔 2 秒就显示 60 次/分钟。系数 120
                  的机械含义要结合机构确认，报告里不擅自解释成半周期或别的物理量。只有
                  Δt &gt; 2 秒才认作有效间隔；X1 回到 0 才清{" "}
                  <code>processedjishi1</code>，同一亮灯不会连算两次。
                </>
              ),
            },
          ]}
        />
      </Section>
    </>
  );
}
