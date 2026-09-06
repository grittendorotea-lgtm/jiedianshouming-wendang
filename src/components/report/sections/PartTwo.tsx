import { Callout, CodeBlock, Flow, KvTable, Section, Sub } from "../blocks";

export function PartTwo() {
  return (
    <>
      <Section id="i-collect" kicker="F2 · JiaoL" title="电流采集是怎么实现的">
        <p>
          类 <code>com.ytzg.sealer.plc.JiaoL</code>{" "}
          读的是电流表，不是电阻。命名来自“角/铰/绞”现场叫法或电流回路，界面标签写的是“电流”，单位是
          mA / A。它和电阻类共用同一套 CRC 和组帧方法，但收数方式不同：电阻是同步轮询，电流是监听器异步收包。
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
        <Callout title="和电阻采集的对照，写报告很好用" tone="note">
          电阻：同步、三从站、单位 mΩ/Ω/kΩ。电流：异步监听、单从站地址
          1、单位 mA/A。两者都走 COM3、9600、功能码
          0x03、同一套 CRC。上位机用“先关 PLC 口再打开仪表口”把它们串进同一条测试节拍。
        </Callout>
      </Section>

      <Section
        id="plc"
        kicker="F3 · PLC"
        title="PLC 启停、报警和凸轮信号是怎么做的"
      >
        <p>
          PLC 通信类 <code>RRuANDWone</code> 源码不在这 5
          个文件里，但主界面把它用得很完整，可以还原出协议和用途。设备地址{" "}
          <code>0x08</code>，读输入寄存器功能码 <code>0x04</code>，从寄存器 0
          起读 2 个字。
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
      </Section>

      <Section
        id="loop"
        kicker="F4 · 核心状态机"
        title="测试主循环：电阻一拍、电流一拍"
      >
        <p>
          这是整套软件最值得写进课题报告的部分。外层{" "}
          <code>continueLoop2</code> 管整次试验是否继续，内层{" "}
          <code>continueLoop</code>{" "}
          管“这一拍有没有采完电阻”。设计目的写在注释里：先读电阻，再读电流，再读电阻……防止一直读电流。
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
        <Callout title="文本框一变，曲线才会加点" tone="note">
          采集线程自己并不调用 <code>series.add()</code>。它只
          <code>setText()</code>。四个框都挂了{" "}
          <code>DocumentListener</code>，文本插入后{" "}
          <code>updateChart()</code>{" "}
          才把这一点画上去。所以“通信成功”和“曲线更新”是通过界面文本耦合的。
        </Callout>
      </Section>

      <Section id="speed" kicker="F5 · 速度" title="动作速度是怎么算出来的">
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
          公式是 <strong>v = 120 / Δt</strong>。若两次 X1
          有效间隔是 2 秒，速度就是 60 次/分钟；间隔 4
          秒就是 30 次/分钟。系数 120 相当于{" "}
          <code>60 × 2</code>：把“半周期/单边凸轮间隔”换算成每分钟动作次数。曲线的
          X 轴每次加点后 <code>timeSeconds += 2</code>
          ，和这个“一次节拍记 2 次”是对齐的。
        </p>
        <p>
          X1 回到 0 时把 <code>processedjishi1</code>{" "}
          清掉，这样下一次再亮才会重新计时。这是标准的上升沿检测。
        </p>
      </Section>
    </>
  );
}
