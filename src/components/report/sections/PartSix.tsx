import { Callout, QA, QABody, QAList, Section, Sub } from "../blocks";
import {
  ArrowDown,
  ChartNode,
  Figure,
  Split,
  VChart,
} from "../flowchart";

export function PartSix() {
  return (
    <Section id="hard-qa" kicker="第十七节" title="重难点问题与代码解答">
      <p>
        前面各节已经按功能写了实现。本节不再重复“做了什么”，只收那些真正构成工程矛盾的问题：半双工总线数量少于工位数、同步采集与异步监听如何对齐、阻塞 I/O
        与 Swing 单线程规则如何共存、续测如何避免重复持久化。每道题按<strong>矛盾—实现—边界</strong>写：先说明朴素做法为什么失败，再对照源码写解法，最后点出仍未收干净的约束。
      </p>

      <Sub title="1）核心题：两路半双工总线如何拖四套独立状态机">
        <QA
          q="工位数大于物理串口数时，如何同时满足四套独立启停，并且在问表期间仍能对故障工位下发停机？"
          a={
            <QABody
              contradiction={
                <>
                  RS-485
                  是半双工：同一时刻一条总线上只能有一对主从问答。若沿用基础版“凸轮到位就{" "}
                  <code>close</code> PLC 口、<code>open</code>{" "}
                  仪表口”，四个试验循环并发时会互相踢掉对方的{" "}
                  <code>SerialPort</code>{" "}
                  对象，凸轮沿丢失、保护写线圈失败。若给每台各开一口，现场只有
                  COM4、COM5。更苛刻的一点是：电阻超限必须在读表过程中仍能写停机，控制和测量不能再抢同一把口。
                </>
              }
              implementation={
                <>
                  代码把问题拆成三条互不重叠的通道。第一，介质分离：
                  <code>RRuANDWone(&quot;COM4&quot;)</code> 专访 PLC，
                  <code>SixMeterInstrumentReader(&quot;COM5&quot;)</code>{" "}
                  专访八台仪表，问表不再关闭 PLC 口。第二，生产/消费解耦：读取器构造即{" "}
                  <code>startReading()</code>，
                  <code>ScheduledExecutorService</code> 每 100ms 只问一台（地址{" "}
                  <code>(queryIndex % 8) + 1</code>
                  ，一轮约 800ms）；监听线程 <code>sleep(80)</code>{" "}
                  取整帧，CRC 通过后按 <code>data[0]</code> 写入{" "}
                  <code>ConcurrentHashMap</code>
                  。工位循环在凸轮上升沿只调用{" "}
                  <code>getResistanceValue(5～8)</code>、
                  <code>getCurrentValue(1～4)</code>
                  ，复杂度从“占口+组帧+等待”降为 O(1) 读缓存。第三，控制面按工位切开：写寄存器{" "}
                  <code>i ∈ {"{1,2,3,4}"}</code> 控制本台运行（0 启动 / 1
                  停机），寄存器 5 公共报警；
                  <code>isTestingStarted1～4</code>、四套线程池、四套{" "}
                  <code>AtomicBoolean</code>{" "}
                  沿标志和四张曲线彼此隔离，<code>cancel(true)</code>{" "}
                  只停本工位 <code>Future</code>。
                </>
              }
              bound={
                <>
                  缓存最大陈旧约 800ms，凸轮沿读到的是上一轮值而非当拍实时值——这是用时效换吞吐。四个循环共享同一个{" "}
                  <code>synchronized readAndProcessRegisters()</code>
                  ，COM4 被串行化，一次读 8 个输入字避免四次开销。报警线圈 5
                  仍是共享资源，源码用 100ms 交错写降低覆盖，并发故障时仍可能互相清报警。面板残留的{" "}
                  <code>RRu(&quot;COM5&quot;)</code> /{" "}
                  <code>Wone(&quot;COM5&quot;)</code>{" "}
                  一旦打开会拆掉调度器。仓库里的{" "}
                  <code>ExecuteCommon</code>{" "}
                  仍是单记录接口，分设备阈值方法若未对齐，四套门限会读成同一行。
                </>
              }
            />
          }
        />
        <Figure no="17-1" title="控制面与测量面解耦">
          <VChart>
            <ChartNode kind="start">工位 i 点开始：写寄存器 i = 0</ChartNode>
            <ArrowDown />
            <Split
              leftLabel="控制面"
              rightLabel="测量面"
              left={
                <VChart>
                  <ChartNode kind="io">COM4　PLC 一次读 8 字</ChartNode>
                  <ArrowDown />
                  <ChartNode>synchronized 串行化四工位访问</ChartNode>
                </VChart>
              }
              right={
                <VChart>
                  <ChartNode kind="io">COM5　100ms 轮询 01→08</ChartNode>
                  <ArrowDown />
                  <ChartNode>CRC 后写入 ConcurrentHashMap</ChartNode>
                </VChart>
              }
            />
            <ArrowDown />
            <ChartNode>凸轮沿只读本工位 getter，不再 open/close</ChartNode>
            <ArrowDown />
            <ChartNode>保护只写寄存器 i 与 Future i</ChartNode>
            <ArrowDown />
            <ChartNode kind="end">邻站状态机继续推进</ChartNode>
          </VChart>
        </Figure>
      </Sub>

      <QAList
        title="2）基础版：单总线时分、异构采集与增量持久化"
        items={[
          {
            q: "PLC、三路电阻、一路电流共用 COM3 时，为什么不能定时全量轮询，时分复用的互斥条件是什么？",
            a: (
              <QABody
                contradiction={
                  <>
                    三个 Java 对象都写死{" "}
                    <code>SerialPort.getCommPort(&quot;COM3&quot;)</code>
                    。Windows 下同一物理口不能被多个{" "}
                    <code>SerialPort</code>{" "}
                    实例同时打开；RS-485
                    半双工也不允许主站并发发两帧。若按固定周期轮询全部从站，凸轮未到位也会占口，真正到位时反而读不到。
                  </>
                }
                implementation={
                  <>
                    采集改成状态驱动。平时{" "}
                    <code>RRuANDWone</code> 占口，功能码 04H
                    循环读 X1/X2。仅当 X1 上升沿（
                    <code>value==1 && !processed2</code>
                    ）才 <code>closeSerialPort()</code>，把口交给{" "}
                    <code>DianZu00000</code> 串行问从站 2/3/4；仅当 X2
                    上升沿才交给 <code>JiaoL</code> 问从站 1。读完立刻{" "}
                    <code>stopReading()</code>{" "}
                    归还。真正的互斥不在锁对象，而在调用顺序上的“先关后开”。
                  </>
                }
                bound={
                  <>
                    这是协作式互斥，不是{" "}
                    <code>SerialPortManager</code>
                    。异常路径若未 close，下一模块会因口被占用而空转{" "}
                    <code>waitForPort()</code>
                    。电阻侧 50ms 重试、电流侧固定睡 200ms，都是在赌 PLC
                    已经释放。
                  </>
                }
              />
            ),
          },
          {
            q: "电阻是同步轮询、电流是事件监听，如何在缺少公共时钟的条件下对齐成同一个寿命采样点？",
            a: (
              <QABody
                contradiction={
                  <>
                    两路采集的完成时刻天然错开：电阻在 X1
                    沿上阻塞读完三从站，电流在 X2 沿上由{" "}
                    <code>SerialPortDataListener</code>{" "}
                    异步回填。若谁先到谁就{" "}
                    <code>series.add</code>
                    ，曲线会出现“有阻无流”或“有流无阻”的半拍点，后续超限判定和存盘都会错位。
                  </>
                }
                implementation={
                  <>
                    电流先写入隐藏框 <code>dianzu444NO</code>
                    ，不触发正式显示。电阻读完后，在{" "}
                    <code>invokeLater</code> 里把隐藏值抄到{" "}
                    <code>dianzu444</code>
                    ，并同时刷新三路电阻框。四个显示框共用一个{" "}
                    <code>DocumentListener</code>
                    ，只处理 <code>insertUpdate</code>
                    ，四路均非空才在同一 <code>timeSeconds</code> 上给{" "}
                    <code>series1/2/3</code> 和 <code>rightSeries</code>{" "}
                    各加一点，然后横坐标 +2。通信成功与曲线更新通过界面文本解耦，采集线程从不直接碰
                    JFreeChart。
                  </>
                }
                bound={
                  <>
                    对齐依赖“四框同时非空”这一弱不变式，不是事务。若某路 CRC
                    失败保持空串，本拍不会加点，但超时计时仍在走。隐藏框抄完后被清空，下一拍必须重新采齐。
                  </>
                }
              />
            ),
          },
          {
            q: "Modbus 等待是阻塞 I/O，如何既不冻死停机按钮，又不在工作线程里违反 Swing 单线程规则？",
            a: (
              <QABody
                contradiction={
                  <>
                    打开串口、sleep、读应答若放在 EDT，停机按钮和曲线都无法重绘。若工作线程直接{" "}
                    <code>XYSeries.add</code>，JFreeChart / Swing
                    会在非 EDT 上改组件树，出现竞态重绘或偶发 NPE。
                  </>
                }
                implementation={
                  <>
                    正式循环提交到{" "}
                    <code>executorServicebingxing</code>（3
                    线程池），句柄保存在 <code>currentTestFuture</code>
                    ，停止用 <code>cancel(true)</code>
                    。所有文本和速度更新包在{" "}
                    <code>SwingUtilities.invokeLater</code>
                    。加点发生在 EDT 上的{" "}
                    <code>DocumentListener → updateChart()</code>
                    。保护写线圈仍在工作线程里完成，避免等 UI
                    排队才停机。
                  </>
                }
                bound={
                  <>
                    <code>cancel(true)</code>{" "}
                    只能打断 sleep，不能中止已经发出的 Modbus
                    帧；停机后仍可能有一拍应答到达。电流监听回调也在 jSerialComm
                    线程，必须先写缓存再 <code>invokeLater</code>。
                  </>
                }
              />
            ),
          },
          {
            q: "凸轮开关量会在高电平上保持数个扫描周期，如何避免同一拍被重复读表、速度被重复积分？",
            a: (
              <QABody
                contradiction={
                  <>
                    PLC 输入字是电平，不是边沿。主循环每圈都读寄存器，若只判断{" "}
                    <code>value==1</code>
                    ，一次到位会触发数十次 03H
                    请求，COM3 被占死，速度公式会对同一间隔连除。
                  </>
                }
                implementation={
                  <>
                    三套 <code>AtomicBoolean</code> 做成上升沿闭锁：
                    <code>processed2</code> 锁电阻拍，
                    <code>processed555111</code> 锁电流拍，
                    <code>processedjishi1</code>{" "}
                    锁速度积分。置位后即使输入保持 1 也不再进入采集；状态回到 0
                    才清标志，形成“亮—处理—灭—解锁”。速度另加 Δt &gt; 2
                    秒的死区，滤掉行程开关抖动脉冲。
                  </>
                }
                bound={
                  <>
                    闭锁是协作式的，退出时必须{" "}
                    <code>resetForNextEnter()</code>{" "}
                    清掉，否则下次一进界面就认为本拍已处理。速度系数 120
                    与横坐标步进 2、累计次数 ×2
                    一致，但其机械含义源码未定义，报告不得臆造。
                  </>
                }
              />
            ),
          },
          {
            q: "同一试品分段试验时，如何在不改表结构的前提下只持久化新增采样点？",
            a: (
              <QABody
                contradiction={
                  <>
                    <code>test_results</code>{" "}
                    一行对应一个采样点，没有“已保存到第几拍”的游标列。若按编号全量再插，续测会把历史点复制一份，累计次数和回放都会翻倍。
                  </>
                }
                implementation={
                  <>
                    回显时记录内存游标 <code>loadedPointCount</code> 和绑定编号{" "}
                    <code>loadedTestBianHao</code>
                    。保存时{" "}
                    <code>appendMode = exists && 编号.equals(loadedTestBianHao)</code>
                    ，仅追加模式从该索引起收集{" "}
                    <code>XYSeries</code>；否则从 0
                    全量写。批插关自动提交，失败 rollback。成功后再{" "}
                    <code>addAllCountBySaveCount(新增点数 × 2)</code>
                    ，并把游标推到当前点数。新编号（库中不存在）直接
                    return，不清空正在画的曲线；
                    <code>loadingHistory</code> 防止下拉回填递归清空。
                  </>
                }
                bound={
                  <>
                    游标在进程内，不在库里。异常退出后再打开，只要编号仍对得上就能续；若有人清空内存曲线却保留编号，可能少存。查重只认“当前回显编号”，手动改号会走全量插入。
                  </>
                }
              />
            ),
          },
          {
            q: "串口干扰下，如何保证错误帧既不进入合格判定，也不污染寿命曲线？",
            a: (
              <QABody
                contradiction={
                  <>
                    9600 bit/s 的 RS-485
                    在接触器旁边容易出半包和位翻转。若把原始整数直接当 mΩ
                    去比 <code>dianzumax</code>
                    ，一次干扰就能误停机或在曲线上留下尖峰。
                  </>
                }
                implementation={
                  <>
                    请求与应答都按 Modbus CRC-16 校验（初值 0xFFFF，多项式反射形
                    0xA001）。长度 &lt; 9 或 CRC 失败直接丢弃，不写{" "}
                    <code>dianZValue</code> / 缓存。PLC 应答功能码不是 04H
                    则延时 10ms 重问，避免把半包当凸轮沿。电流{" "}
                    <code>unitCode</code> 不是 3/4 返回 0，不进入 2.1 A
                    比较。曲线监听只在四路合法非空时加点。
                  </>
                }
                bound={
                  <>
                    丢弃不等于重读成功；本拍可能缺数。基础版三路电阻单位实际共用{" "}
                    <code>dianZUnit1</code>
                    ，最后一路会覆盖前两路的单位字段，量程不一致时显示会骗人，判定用的是{" "}
                    <code>extractNumber</code> 后的纯数值。
                  </>
                }
              />
            ),
          },
        ]}
      />

      <QAList
        title="3）4拖1：总线调度、共享对象与故障隔离"
        items={[
          {
            q: "八台仪表共一条 RS-485，如何在约 800ms 一轮的轮询下保证应答不会写进邻站缓存？",
            a: (
              <QABody
                contradiction={
                  <>
                    半双工上若并发八路 03H，应答在总线上重叠，主站无法配对。即便串行发送，监听器若按“当前 queryIndex”而不是按帧内地址入表，迟到的应答会张冠李戴。
                  </>
                }
                implementation={
                  <>
                    查询线程严格“每 100ms 只问一台”，<code>queryIndex</code>{" "}
                    自增取模。监听不信任发送序号，而是读{" "}
                    <code>data[0]</code>：01～04 走{" "}
                    <code>parseCurrent</code>，05～08 走电阻解析，键为{" "}
                    <code>current_01</code> / <code>resistance_05</code>{" "}
                    等。工位 1 写死取 05/01，工位 2 取 06/02，以此类推。CRC
                    失败不覆盖旧缓存，工位读到的是上一轮合法值。
                  </>
                }
                bound={
                  <>
                    100ms 节拍叠加 80ms
                    组帧等待，总线利用率已经偏满；再加从站会拉长一轮周期。旧缓存被保留，意味着仪表掉线时工位仍可能用过期值做超限比较。
                  </>
                }
              />
            ),
          },
          {
            q: "四个试验循环同时调用 PLC 读接口，如何避免 COM4 被重入打开，又避免四次重复扫描？",
            a: (
              <QABody
                contradiction={
                  <>
                    每个工位都有自己的 <code>while (continueLoop2)</code>
                    ，都会调用{" "}
                    <code>readAndProcessRegisters()</code>
                    。若各自 <code>openSerialPort</code>，jSerialComm
                    会对同一 COM4 报占用；若各读一遍 8
                    字，总线带宽被浪费四倍，写线圈也会插进读周期。
                  </>
                }
                implementation={
                  <>
                    方法声明为实例上的{" "}
                    <code>public synchronized Result readAndProcessRegisters()</code>
                    ，四个循环共享同一{" "}
                    <code>ZzhejiPanel</code>{" "}
                    对象，COM4 访问被监视器串行化。内部用 04H
                    一次读 8 个输入字（
                    <code>01 04 00 00 00 08 F1 CC</code>
                    ），功能码不符则 10ms 重试，再拆成{" "}
                    <code>value1～8</code>{" "}
                    返回。速度积分也在这个同步方法里按工位各自的{" "}
                    <code>processedjishi1～4</code> 更新。
                  </>
                }
                bound={
                  <>
                    <code>synchronized</code>{" "}
                    保护的是 Java
                    监视器，不是 PLC 事务。一工位写寄存器 5
                    时，另一工位可能紧接着读到中间态。方法过长，读输入和算速度耦在一起，持锁时间偏长。
                  </>
                }
              />
            ),
          },
          {
            q: "工位 2 电阻超限时，怎样保证停的是本台机构，而不是把整条总线或邻站状态机一起拆掉？",
            a: (
              <QABody
                contradiction={
                  <>
                    四台共享 COM4 对象、共享报警寄存器 5、共享同一个仪表读取器。朴素的“停机就{" "}
                    <code>stopReading()</code> + 写总停”
                    会让邻站失去凸轮扫描和仪表缓存。
                  </>
                }
                implementation={
                  <>
                    保护比较的是本工位缓存对{" "}
                    <code>getDianzumaxValue2()</code>
                    ，不是全局 <code>dianzumax</code>
                    。成立后交错写：寄存器 5 置 1 报警，寄存器 2 置 1
                    停本台机构，灯改 <code>N</code>，
                    <code>isTestingStarted2 = false</code>，
                    <code>continueLoop2</code> 结束本循环；
                    <code>currentTestFuture2.cancel(true)</code>{" "}
                    只取消工位 2。COM5 调度器不停，工位 1/3/4 的 Future
                    继续跑。入库走{" "}
                    <code>saveToPolicetimeTableStop(2, …)</code>，带{" "}
                    <code>Shebeihao</code>
                    。保存查重走{" "}
                    <code>isProductNumberExists06shebeihao02</code>
                    ，曲线按设备号切开。
                  </>
                }
                bound={
                  <>
                    机构隔离成立，信号隔离不彻底：寄存器 5
                    是公共报警，工位 2 稍后写 0
                    可能清掉工位 3 刚置的报警。电流阈值仍硬编码 2.1
                    A，四台共用同一数值。
                  </>
                }
              />
            ),
          },
        ]}
      />

      <Callout title="两条技术主线" tone="idea">
        基础版的本质难点是：在一把 COM3
        上用凸轮电平把异构采集（同步电阻 / 异步电流 /
        PLC 巡检）编排成同一拍，并用沿闭锁和增量游标保证不重读、不重存。4
        拖 1
        的本质难点是：把“问表”从试验循环里彻底拿出去，让控制面按线圈隔离、测量面按缓存共享，从而在两路半双工介质上跑四套可独立停机的状态机。图多画三张不是难点，调度、互斥和隔离才是。
      </Callout>
    </Section>
  );
}
