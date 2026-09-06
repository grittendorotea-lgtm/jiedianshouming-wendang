import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, Flow, KvTable, Section, Sub } from "../blocks";
import {
  ArrowDown,
  ChartNode,
  Figure,
  LayerStack,
  VChart,
} from "../flowchart";

export function PartFive() {
  return (
    <Section id="four-to-one" kicker="第十六节" title="4拖1方案的多工位拓展">
      <p>
        前十五节只分析单工位基础版。本节依据{" "}
        <code>ZzhejiPanel-20260811.java</code>（约 7200 行）和{" "}
        <code>SixMeterInstrumentReader.java</code>
        ，单独说明后来做成的<strong>4拖1方案</strong>：一台上位机、一块
        PLC、一条仪表总线，同时拖四台监测设备做寿命试验。
      </p>

      <Sub title="1）4拖1指什么">
        <p>
          “4拖1”不是四套软件各管一台，而是一套程序并行管理四台。界面用{" "}
          <code>JTabbedPane</code>{" "}
          做成“监测设备1～4”四个页签；每台设备各有启停按钮、状态灯、编号、次数、速度、电阻/电流显示和一张双轴曲线。现场接线则收成两路：COM4
          只连 PLC，COM5           只连八台仪表。
        </p>
        <Figure no="16-1" title="4拖1双总线结构">
          <LayerStack
            layers={[
              {
                title: "ZzhejiPanel　监测设备 1～4",
                detail: "四套启停、状态灯、编号、曲线；一台停机不影响邻站",
                tone: "ui",
              },
              {
                title: "COM4　RRuANDWone → PLC",
                detail: "地址 0x01，一次读 8 路凸轮；寄存器 1～4 分控，寄存器 5 公共报警",
                tone: "comm",
              },
              {
                title: "COM5　SixMeterInstrumentReader → 八台仪表",
                detail: "01～04 电流，05～08 电阻；100ms 轮询入缓存，工位只读 getter",
                tone: "data",
              },
            ]}
          />
        </Figure>
        <div className="grid gap-3 md:grid-cols-2">
          <Card>
            <CardHeader>
              <Badge variant="plc">一台主机</Badge>
              <CardTitle className="pt-2 text-base">控制与测量分开</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              <code>RRuANDWone(&quot;COM4&quot;)</code> 专访 PLC；
              <code>SixMeterInstrumentReader(&quot;COM5&quot;)</code>{" "}
              专访仪表。问表时仍能发出停机。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="chart">四台设备</Badge>
              <CardTitle className="pt-2 text-base">状态完全隔离</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              各有 <code>isTestingStarted1～4</code>
              、独立线程池、独立沿标志和独立曲线。一台超限停机，不影响邻站继续试验。
            </CardContent>
          </Card>
        </div>
      </Sub>

      <Sub title="2）工位、仪表、线圈怎么对应">
        <p>
          每个工位只绑一路电阻和一路电流。构造时即{" "}
          <code>instrumentReader.startReading()</code>
          ，凸轮到位后只读缓存，不再打开或关闭仪表口。
        </p>
        <div className="overflow-x-auto rounded-lg border border-border bg-white">
          <table className="w-full min-w-[40rem] text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2">监测设备</th>
                <th className="px-3 py-2">电阻从站</th>
                <th className="px-3 py-2">电流从站</th>
                <th className="px-3 py-2">PLC 凸轮输入</th>
                <th className="px-3 py-2">运行线圈</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {[
                ["1", "05  getResistanceValue(5)", "01  getCurrentValue(1)", "第 1、5 字", "寄存器 1"],
                ["2", "06  getResistanceValue(6)", "02  getCurrentValue(2)", "第 2、6 字", "寄存器 2"],
                ["3", "07  getResistanceValue(7)", "03  getCurrentValue(3)", "第 3、7 字", "寄存器 3"],
                ["4", "08  getResistanceValue(8)", "04  getCurrentValue(4)", "第 4、8 字", "寄存器 4"],
              ].map((row) => (
                <tr key={row[0]} className="border-t border-border align-top">
                  {row.map((cell) => (
                    <td key={cell} className="px-3 py-2">
                      {cell}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
        <p>
          报警共用寄存器 5。状态灯三种图：停机{" "}
          <code>stop</code>、运行绿色 <code>Y</code>、故障红色 <code>N</code>
          。
        </p>
      </Sub>

      <Sub title="3）统一仪表调度：问表和用数拆开">
        <p>
          <code>SixMeterInstrumentReader</code> 包名为{" "}
          <code>com.ytzg.sealer.comm.caidanduqu</code>
          。类名仍写“六表”，实际轮询八台：01～04 电流，05～08 电阻。协议与基础版相同：功能码
          03H，起始寄存器 1，数量 3，CRC-16（初值 0xFFFF，多项式 0xA001）。
        </p>
        <Flow
          steps={[
            "ScheduledExecutorService 每 100ms 只问一台，地址 01→08 循环，一整轮约 800ms。",
            "SerialPortDataListener 收到数据后 sleep 80ms，再取整帧，避免半包。",
            "CRC 通过后按 data[0] 分流：01～04 走电流解析，05～08 走电阻解析。",
            "数值写入 ConcurrentHashMap，键如 current_01、resistance_05。",
            "四个试验循环只调用 getter，不再 startReading / stopReading。",
          ]}
        />
        <Figure no="16-2" title="八表轮询与四工位用数">
          <VChart>
            <ChartNode kind="start">startReading() 打开 COM5</ChartNode>
            <ArrowDown />
            <ChartNode>每 100ms 问下一台：01→08</ChartNode>
            <ArrowDown />
            <ChartNode kind="io">监听线程等待 80ms 取整帧</ChartNode>
            <ArrowDown />
            <ChartNode>CRC 通过后按 data[0] 分流入缓存</ChartNode>
            <ArrowDown />
            <ChartNode>工位 1 读电阻 05、电流 01</ChartNode>
            <ArrowDown />
            <ChartNode>工位 2/3/4 同理读 06/07/08 与 02/03/04</ChartNode>
            <ArrowDown />
            <ChartNode kind="end">凸轮到位只取缓存，不再抢口</ChartNode>
          </VChart>
        </Figure>
        <p>
          解析用 <code>BigDecimal.movePointLeft</code>{" "}
          还原小数位。电流单位 mA/A/kA，电阻单位 mΩ/Ω/kΩ/MΩ。
        </p>
        <CodeBlock title="工位 1 凸轮到位后只取缓存">{`String dianzu = instrumentReader.getResistanceTextValue(5);
dianzu1Value1 = instrumentReader.getResistanceValue(5);
String current = instrumentReader.getCurrentTextValue(1);
dianliu1Value1 = instrumentReader.getCurrentValue(1);`}</CodeBlock>
      </Sub>

      <Sub title="4）PLC：一次读八路，分线圈启停">
        <p>
          拓展后面板把 PLC 地址改成 0x01，功能码仍是 04H，但一次读 8
          个输入字。四个工位的试验循环都调用同一个{" "}
          <code>synchronized readAndProcessRegisters()</code>
          ，COM4 上的访问被串行化，避免四路同时打口。
        </p>
        <KvTable
          rows={[
            {
              k: "读输入",
              v: "请求帧 01 04 00 00 00 08 F1 CC。功能码不是 04H 则延时 10ms 重试。",
            },
            {
              k: "写运行",
              v: "开始时 writeSingleRegister(工位号, 0)；停机或故障写 1。",
            },
            {
              k: "写报警",
              v: "故障时写寄存器 5 为 1，处理后清 0。四台共用这一位。",
            },
            {
              k: "仪表请求示例",
              v: "电流 1：01 03 00 01 00 03 54 0B；电阻 1：05 03 00 01 00 03 55 8F。",
            },
          ]}
        />
      </Sub>

      <Sub title="5）四套试验循环并行">
        <p>
          每个页签点“开始检测”后，把本工位循环丢进各自的线程池。内层仍等凸轮沿，外层仍做电阻、电流、次数三道门禁，但比较的是本工位缓存和本工位阈值。
        </p>
        <p>
          速度仍按 <code>v = 120 / Δt</code>{" "}
          计算，四路各有 <code>lastTime111111～444111</code>
          。源码里还留着窗口为 5 的滑动平均写法，现行主路径是直接四舍五入。
        </p>
        <Callout title="这一步为什么难" tone="idea">
          半双工总线上八台表必须排队，四个工位却要几乎同时拿到当前值。再叠加四套沿闭锁和共享
          PLC 对象，任一通道错位就会误停机或画错曲线。难点不在多画几张图，而在调度、缓存和状态隔离。
        </Callout>
      </Sub>

      <Sub title="6）保护、曲线、存储都按设备号切开">
        <KvTable
          rows={[
            {
              k: "电阻保护",
              v: "工位 1～4 分别与 getDianzumaxValue1～4() 比较，超限只停本工位，灯改 N。",
            },
            {
              k: "电流保护",
              v: "四路各自与 2.1 A 比较。每路持有自己的 dianliuNValueN，不共用一个电流变量。",
            },
            {
              k: "凸轮超时",
              v: "四套 12 s 计时。故障调用 saveToPolicetimeTableStop(shebeihao, count)。",
            },
            {
              k: "次数上限",
              v: "timeSeconds1～4 分别对照 getTestmaxValue1～4()。先到上限的工位先停。",
            },
            {
              k: "实时曲线",
              v: "四张独立双轴图。每张只画本工位一路电阻和一路电流，两框齐了才加点。",
            },
            {
              k: "保存",
              v: "SwingWorker + LoadingGifDialog。每行末尾写设备号 1～4，查重走 isProductNumberExists06shebeihao01～04。",
            },
            {
              k: "故障台账",
              v: "policetime 增加 Shebeihao。电阻、电流各四套入库方法，凸轮走统一方法。",
            },
            {
              k: "累计次数",
              v: "getAllcountValue1(1～4) / updateAllcountValues(text, shebeihao)，与单次试验上限分开。",
            },
          ]}
        />
        <p>
          这批仓库里的 <code>ExecuteCommon.java</code>{" "}
          仍是单记录接口。四工位面板已经按分设备方法调用，数据访问层需要同步对齐，否则四套阈值会读成同一行。
        </p>
      </Sub>

      <Sub title="7）方案本身还能再扩展">
        <p>
          4拖1 的价值不只是“从 1 台变成 4 台”，而是把扩展点收成同一套规则。再加工位时，不必再回到“凸轮到位就抢串口问表”的写法。
        </p>
        <ul className="list-disc space-y-1 pl-5">
          <li>
            <strong>地址可加长：</strong>
            调度器按 <code>(queryIndex % 8) + 1</code>{" "}
            循环。若总线还能挂表，只需加长取模、补解析分支和 getter。
          </li>
          <li>
            <strong>页签可复制：</strong>
            监测设备 2/3/4 是设备 1 的同构副本。新增工位主要是复制状态机、曲线对象和线圈号。
          </li>
          <li>
            <strong>线圈可顺延：</strong>
            运行位按工位号写寄存器 1～4，报警集中在 5。继续扩展时运行位往后排，公共报警仍可共用。
          </li>
          <li>
            <strong>台账可筛选：</strong>
            曲线和故障都带设备号，历史查询按工位过滤，不会把邻站点画进当前页签。
          </li>
        </ul>
        <Callout title="源码里要避开的一点" tone="warn">
          面板里仍留着 <code>RRu(&quot;COM5&quot;)</code>、
          <code>Wone(&quot;COM5&quot;)</code>
          。它们和仪表读取器同口，一旦打开会把统一调度打断。4拖1
          能成立，前提是 COM5 只属于读取器。
        </Callout>
      </Sub>
    </Section>
  );
}
