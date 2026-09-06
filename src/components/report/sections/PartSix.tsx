import { Callout, QA, QAList, Section, Sub } from "../blocks";
import {
  ArrowDown,
  ChartNode,
  Figure,
  VChart,
} from "../flowchart";

export function PartSix() {
  return (
    <Section id="hard-qa" kicker="第十七节" title="难点问题与代码解答">
      <p>
        前面各节已经按功能写了实现。本节把真正难写、答辩也最容易被追问的问题集中起来，一律用“问—答”：先说难点，再对照代码说明怎么解决。4
        拖 1 里“只有两路串口、四组设备还要能单独启停”是整份拓展的核心题。
      </p>

      <Sub title="1）两路串口如何拖四组独立设备">
        <QA
          q="现场只有 COM4、COM5 两个串口，四组设备却要能单独启动和停止，总线不打架、邻站不误停，代码怎么做？"
          a={
            <>
              不按“一台设备一把口”去扩。COM4 专给 PLC，COM5 专给八台仪表。
              <code> SixMeterInstrumentReader </code>
              在构造时就 <code>startReading()</code>
              ，每 100ms 轮询地址 01～08，结果写入{" "}
              <code>ConcurrentHashMap</code>
              。四个工位凸轮到位后只调用{" "}
              <code>getResistanceValue(5～8)</code>、
              <code>getCurrentValue(1～4)</code>
              ，不再 open/close 仪表口。启停则写 PLC 寄存器 1～4：开始写
              0，停止或故障写 1。每台有自己的{" "}
              <code>isTestingStarted1～4</code>{" "}
              和独立线程池，一台 <code>cancel</code>{" "}
              只停本工位 Future。报警共用寄存器 5，但故障入库带{" "}
              <code>Shebeihao</code>。这样就用两路半双工总线拖起四套可独立运行的状态机。
            </>
          }
        />
        <Figure no="17-1" title="两串口拖四工位的解耦">
          <VChart>
            <ChartNode kind="start">四工位各自点开始</ChartNode>
            <ArrowDown />
            <ChartNode>写 PLC 寄存器 1～4（COM4）</ChartNode>
            <ArrowDown />
            <ChartNode>本工位线程循环读 8 路凸轮</ChartNode>
            <ArrowDown />
            <ChartNode kind="io">到位后只读 COM5 缓存</ChartNode>
            <ArrowDown />
            <ChartNode>保护只 stop 本工位 Future</ChartNode>
            <ArrowDown />
            <ChartNode kind="end">邻站继续试验</ChartNode>
          </VChart>
        </Figure>
      </Sub>

      <QAList
        title="2）基础版：单串口、状态驱动、线程与存盘"
        items={[
          {
            q: "PLC、三路电阻、一路电流都挂 COM3，为什么不同时读、怎么避免抢口？",
            a: (
              <>
                基础版是状态驱动，不是定时全量采集。X1 到位才关 PLC 口、让{" "}
                <code>DianZu00000</code> 问从站 2/3/4；X2 到位才让{" "}
                <code>JiaoL</code>{" "}
                问从站 1。读完立刻 close，交还 PLC。三个类都写死 COM3，真正互斥靠调用顺序上的先关后开。
              </>
            ),
          },
          {
            q: "电阻同步轮询、电流异步监听，怎样对齐成同一个采样点？",
            a: (
              <>
                电流先写入隐藏框 <code>dianzu444NO</code>
                ，电阻读完再抄到显示框。四个显示框共用{" "}
                <code>DocumentListener</code>
                ，只有三路电阻和一路电流都非空，才在同一{" "}
                <code>timeSeconds</code> 上加点。
              </>
            ),
          },
          {
            q: "串口等待会不会把界面卡死？曲线为什么不在采集线程里直接 add？",
            a: (
              <>
                正式循环提交到 <code>ExecutorService</code>，停止用{" "}
                <code>Future.cancel(true)</code>
                。界面更新一律{" "}
                <code>SwingUtilities.invokeLater</code>
                。采集线程只改文本框，由监听器在 EDT 上{" "}
                <code>series.add</code>，避免违反 Swing 单线程规则。
              </>
            ),
          },
          {
            q: "同一输入持续为 1 时，会不会反复读表、反复算速度？",
            a: (
              <>
                用 <code>AtomicBoolean</code> 做上升沿闭锁：
                <code>processed2</code>、<code>processed555111</code>、
                <code>processedjishi1</code>
                。状态回到 0 才清标志。速度还要求 Δt &gt; 2
                秒，滤掉开关抖动。
              </>
            ),
          },
          {
            q: "加载历史曲线后再测，怎样避免把旧点再插一遍？",
            a: (
              <>
                回显时记下 <code>loadedPointCount</code> 和{" "}
                <code>loadedTestBianHao</code>
                。保存时只有“编号已存在且等于回显编号”才从该索引追加，否则从第 0
                点全量写。这是基于历史采样点索引的增量存储。
              </>
            ),
          },
          {
            q: "串口干扰时错误读数会不会拿去比阈值、画进寿命曲线？",
            a: (
              <>
                请求和应答都算 Modbus CRC-16（初值 0xFFFF，多项式 0xA001）。长度不够或校验失败直接丢弃，不进入{" "}
                <code>pureValue</code> 和 <code>series.add</code>。
              </>
            ),
          },
        ]}
      />

      <QAList
        title="3）4拖1：调度、隔离、停机与台账"
        items={[
          {
            q: "八台表共一条 RS-485，怎样保证应答不会张冠李戴？",
            a: (
              <>
                查询线程每 100ms 只问一台；监听线程 sleep 80ms
                再取整帧。解析先看 <code>data[0]</code>
                ：01～04 走电流，05～08 走电阻。CRC
                失败不写缓存。工位 1 固定取地址 05/01，不会误用邻站值。
              </>
            ),
          },
          {
            q: "四个试验循环同时跑，会不会同时打开 COM4？",
            a: (
              <>
                四个循环都调用同一个{" "}
                <code>synchronized readAndProcessRegisters()</code>
                ，PLC 访问被串行化。读的是 8 个输入字，一次供四工位使用。
              </>
            ),
          },
          {
            q: "工位 2 电阻超限，会不会把工位 1、3、4 一起停掉？",
            a: (
              <>
                不会。保护只比较本工位缓存和本工位{" "}
                <code>getDianzumaxValue2()</code>
                ，然后写寄存器 2 停本台，写寄存器 5 报警，灯改 N，停{" "}
                <code>isTestingStarted2</code>
                。其他工位的 Future 继续跑。
              </>
            ),
          },
          {
            q: "四台同时保存或同时写报警，界面和线圈会不会互相覆盖？",
            a: (
              <>
                保存走 <code>SwingWorker</code> +{" "}
                <code>LoadingGifDialog</code>
                ，JDBC 不堵 EDT。行末带设备号，查重走{" "}
                <code>isProductNumberExists06shebeihao01～04</code>
                。停机写线圈时源码里加了短延时交错写寄存器 5
                和本工位运行位，降低覆盖。故障记录带{" "}
                <code>Shebeihao</code>。
              </>
            ),
          },
        ]}
      />

      <Callout title="答辩时可以先讲这一句" tone="idea">
        基础版的难点是“一把 COM3 上用凸轮状态把电阻和电流对齐”；4 拖 1
        的难点是“两把口上把问表和用数拆开，让四套状态机独立启停”。代码上的关键不是多画四张图，而是后台轮询缓存、分线圈控制和分设备台账。
      </Callout>
    </Section>
  );
}
