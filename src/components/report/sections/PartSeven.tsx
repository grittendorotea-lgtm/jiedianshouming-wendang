import { Algo, Callout, CodeBlock, KvTable, Section, Sub } from "../blocks";
import {
  ArrowDown,
  ChartNode,
  Figure,
  LayerStack,
  Split,
  VChart,
} from "../flowchart";

export function PartSeven() {
  return (
    <Section id="algo" kicker="第十八节" title="模块框图与关键算法分析">
      <p>
        本章按论文体例组织：先给出软件模块框图，再对源码中实际运行的算法作形式化描述。每一算法给出输入、输出、步骤、复杂度与正确性条件，并标注对应类与方法。分析范围限于{" "}
        <code>ZzhejiPanel</code>、<code>DianZu00000</code>、<code>JiaoL</code>、
        <code>ExecuteCommon</code>、<code>SixMeterInstrumentReader</code> 以及 4
        拖 1 面板对 <code>RRuANDWone</code> 的调用，不引入源码未出现的算法。
      </p>

      <Sub title="1）总体模块框图">
        <p>
          软件按职责划分为四个子系统。人机交互子系统负责参数输入、状态显示与曲线交互；试验控制子系统维护状态机、沿闭锁、保护判定与速度估算；设备通信子系统完成
          Modbus-RTU 组帧、校验与串口时分；数据访问子系统完成阈值读取、曲线批插与故障台账。子系统之间只通过明确的数据契约交互：控制层向通信层发出“读输入
          / 读表 / 写线圈”请求，向数据层发出“查阈值 / 批插 / 记故障”请求，向交互层回写文本框，再由监听器驱动曲线。
        </p>
        <Figure no="18-1" title="软件总体模块框图">
          <LayerStack
            layers={[
              {
                title: "人机交互　ZzhejiPanel",
                detail: "编号 / 日期 / 次数 / 速度 / 双Y轴曲线 / 启停与保存",
                tone: "ui",
              },
              {
                title: "试验控制　状态机 · 沿闭锁 · 保护 · 速度 · 增量游标",
                detail: "X1/X2 驱动采集节拍；四类门禁写线圈；loadedPointCount 控制续测",
                tone: "comm",
              },
              {
                title: "设备通信　RRuANDWone / DianZu00000 / JiaoL / SixMeterInstrumentReader",
                detail: "Modbus RTU · CRC-16 · 基础版 COM3 时分 · 4拖1 COM4/COM5 分离",
                tone: "dev",
              },
              {
                title: "数据访问　ExecuteCommon / JdbcDeal / DBConnection",
                detail: "d_dianzumax · allcount · test_results · policetime",
                tone: "data",
              },
            ]}
          />
        </Figure>
        <KvTable
          rows={[
            {
              k: "控制→通信",
              v: "readInputRegisters、writeSingleRegister、startReading / readAndStoreValues / get*Value。4 拖 1 中读表退化为缓存 getter。",
            },
            {
              k: "控制→数据",
              v: "getDianzumaxValue、getTestmaxValue1、saveTestResultsBatch、saveToPolicetimeTable、addAllCountBySaveCount。",
            },
            {
              k: "通信→控制",
              v: "输入字 X1/X2（或八路凸轮）、带单位字符串、CRC 失败时的 0 / 空帧。",
            },
            {
              k: "交互→控制",
              v: "按钮切换 isTestingStarted；文本插入触发 DocumentListener，四路齐备后 updateChart。",
            },
          ]}
        />
      </Sub>

      <Sub title="2）基础版采集—控制数据流">
        <p>
          基础版把 PLC 巡检、电阻同步轮询和电流异步监听编排在同一把 COM3
          上。模块之间的时序关系不是“周期采样”，而是“凸轮电平上升沿触发一次事务”。事务结束后口必须归还，否则下一模块无法打开串口。
        </p>
        <Figure no="18-2" title="基础版采集控制数据流">
          <VChart>
            <ChartNode kind="start">试验控制循环（线程池）</ChartNode>
            <ArrowDown />
            <ChartNode kind="io">RRuANDWone 读 X1 / X2</ChartNode>
            <ArrowDown />
            <Split
              leftLabel="X1 上升沿"
              rightLabel="X2 上升沿"
              left={
                <VChart>
                  <ChartNode kind="io">DianZu00000 问 2/3/4</ChartNode>
                </VChart>
              }
              right={
                <VChart>
                  <ChartNode kind="io">JiaoL 问 1，写入隐藏框</ChartNode>
                </VChart>
              }
            />
            <ArrowDown />
            <ChartNode>四路显示框对齐 → updateChart</ChartNode>
            <ArrowDown />
            <ChartNode>保护判定 → 写线圈 / policetime</ChartNode>
            <ArrowDown />
            <ChartNode kind="end">保存：增量批插 test_results</ChartNode>
          </VChart>
        </Figure>
      </Sub>

      <Sub title="3）4拖1 调度模块框图">
        <p>
          拓展后的模块边界发生变化：测量从试验循环中剥离，成为独立的生产者；四个工位循环成为消费者。控制面仍走
          PLC，但读输入由一次 8 字事务供给四套状态机。
        </p>
        <Figure no="18-3" title="4拖1 调度模块框图">
          <VChart>
            <ChartNode kind="start">工位 i 控制循环</ChartNode>
            <ArrowDown />
            <Split
              leftLabel="控制面"
              rightLabel="测量面"
              left={
                <VChart>
                  <ChartNode kind="io">COM4　RRuANDWone</ChartNode>
                  <ArrowDown />
                  <ChartNode>synchronized 读 8 字 / 写线圈 i</ChartNode>
                </VChart>
              }
              right={
                <VChart>
                  <ChartNode kind="io">COM5　SixMeterInstrumentReader</ChartNode>
                  <ArrowDown />
                  <ChartNode>100ms 轮询 → ConcurrentHashMap</ChartNode>
                </VChart>
              }
            />
            <ArrowDown />
            <ChartNode>工位 i 只读地址绑定的 getter</ChartNode>
            <ArrowDown />
            <ChartNode kind="end">保护只 cancel Future i，调度器不停</ChartNode>
          </VChart>
        </Figure>
      </Sub>

      <Sub title="4）关键算法一览">
        <p>
          下表列出源码中真正参与闭环的算法。滑动平均（窗口 5）仅存在于注释块，现行主路径是四舍五入，故不列为正式算法。
        </p>
        <div className="overflow-x-auto rounded-lg border border-border bg-white">
          <table className="w-full min-w-[36rem] text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2">编号</th>
                <th className="px-3 py-2">算法</th>
                <th className="px-3 py-2">所属问题</th>
                <th className="px-3 py-2">实现位置</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              {[
                ["18-1", "状态驱动分时采集", "单总线互斥", "ZzhejiPanel 主循环"],
                ["18-2", "Modbus CRC-16", "帧完整性", "DianZu00000 / JiaoL / SixMeter"],
                ["18-3", "定点小数还原", "量纲还原", "parseResistanceValue / BigDecimal"],
                ["18-4", "上升沿闭锁", "电平去重", "AtomicBoolean 三套旗标"],
                ["18-5", "动作频次估算", "速度显示", "v = 120/Δt，Δt>2s"],
                ["18-6", "四类保护判定", "联锁停机", "外层 continueLoop2"],
                ["18-7", "多源采样对齐", "半拍点抑制", "隐藏框 + DocumentListener"],
                ["18-8", "历史索引增量持久化", "续测不重插", "loadedPointCount"],
                ["18-9", "有序最近邻查询", "曲线读数", "findClosestDataItem"],
                ["18-10", "八从站时分轮询", "4拖1 测面", "queryIndex % 8"],
                ["18-11", "工位级隔离停机", "4拖1 控制面", "寄存器 i + Future i"],
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
      </Sub>

      <Sub title="5）通信与数值还原">
        <Algo
          no="18-2"
          title="Modbus CRC-16（反射多项式 0xA001）"
          inputs="字节序列 data[0..n−1]，校验长度 length（请求为前 6 字节，应答为除末 2 字节外的整帧）。"
          outputs="16 位校验和。写入或比较时低字节在前。"
          source="DianZu00000.calculateCRC、JiaoL.calculateCRC、SixMeterInstrumentReader.calculateCRC、ZzhejiPanel.calculateCRC，实现一致。"
          steps={[
            "令 crc ← 0xFFFF。",
            "对 j = 0 … length−1：crc ← crc ⊕ (data[j] & 0xFF)。",
            "重复 8 次：若 crc 最低位为 1，则 crc ← (crc >> 1) ⊕ 0xA001，否则 crc ← crc >> 1。",
            "请求帧把 crc 的低 8 位、高 8 位依次写入字节 6、7；应答则与帧尾两字节比较，不等即丢弃。",
          ]}
          complexity="时间 O(8·length)，空间 O(1)。一帧 8～十余字节，开销可忽略，必须在入表前完成。"
          correctness="该算法是多项式 0x8005 的 LSB-first 反射形式，与 Modbus-RTU 一致。正确性条件：参与计算的字节集合必须与对端相同。PLC 路径额外要求功能码字节等于 04H，否则 10ms 重问，避免把 03H 残帧当成凸轮沿。"
        />
        <Algo
          no="18-3"
          title="仪表定点测量值还原"
          inputs="原始整数 raw（data[3..4] 大端）、小数位 d（data[6]）、单位码 u（data[8]）。"
          outputs="工程量 x 及单位字符串。非法单位时 x = 0。"
          source="基础版 DianZu00000.parseResistanceValue：x = raw / 10^d，u∈{3,4,5}→mΩ/Ω/kΩ。4 拖 1 用 BigDecimal.valueOf(raw).movePointLeft(min(max(d,0),3))，RoundingMode.HALF_UP。"
          steps={[
            "若帧长 < 9 或 CRC 失败，返回 0，不更新缓存。",
            "raw ← (data[3]<<8) | data[4]，d ← data[6]，u ← data[8]。",
            "基础版：按 u 选择单位，x ← raw · 10^(−d)。",
            "4 拖 1：将 d 截断到 [0,3]，用十进制左移避免二进制浮点误差，再按地址写入 ConcurrentHashMap。",
            "界面显示带单位字符串；保护比较前用 extractNumber 去掉单位，得到纯标量。",
          ]}
          complexity="O(1)。"
          correctness="还原式是仪表寄存器的约定映射，不是物理标定。单位码越界必须回 0，否则脏数据会进入 2.1 A 或 dianzumax 比较。基础版三路单位写入同一 dianZUnit1，最后一路覆盖显示单位，判定仍用 extractNumber 后的标量。"
        />
        <CodeBlock title="extractNumber：显示串到标量">{`String number = text.replaceAll("[^\\\\d.\\\\-]", "");
if (number.isEmpty()) throw new NumberFormatException(...);
return Double.parseDouble(number);`}</CodeBlock>
      </Sub>

      <Sub title="6）状态机、沿闭锁与速度">
        <Algo
          no="18-1"
          title="状态驱动分时采集"
          inputs="PLC 输入字 X1、X2；沿旗标 processed2、processed555111。"
          outputs="本拍三路电阻与一路电流，或超时/超限停机。"
          source="ZzhejiPanel 开始测试后提交到 executorServicebingxing 的双层 while。"
          steps={[
            "外层 continueLoop2 为真时进入内层。",
            "调用 readAndProcessRegisters()：04H 读 2 字，功能码不符则重试。",
            "若 X1=1 且 ¬processed2：置位，关闭 PLC 口，DianZu00000 串行问从站 2/3/4，invokeLater 刷新电阻框并抄入隐藏电流，关闭仪表口，内层结束。",
            "若 X2=1 且 ¬processed555111：关闭 PLC 口，JiaoL 发一帧并等待约 200ms，结果写入 dianzu444NO，置位。",
            "X1 或 X2 为 0 时累计灭灯时间，落入 (12,10000) 秒则写停机/报警并结束外层。",
            "内层结束后外层比较电阻、电流、次数三道门禁，通过则延时 100ms 进入下一拍。",
          ]}
          complexity="每拍通信次数为常数：1 次 PLC 巡检 + 最多 3 次电阻 + 最多 1 次电流。占口时间由 50ms/200ms 等待主导。"
          correctness="互斥条件是“任一时刻至多一个 SerialPort 实例打开 COM3”。正确性依赖沿闭锁：电平保持为 1 不得重复占口。异常路径必须 close，否则后续 open 失败。"
        />
        <Algo
          no="18-4"
          title="上升沿闭锁"
          inputs="当前电平 s∈{0,1}，旗标 f（AtomicBoolean），可选时间基 t_last。"
          outputs="本周期是否允许进入处理分支。"
          source="processed2（电阻）、processed555111（电流）、processedjishi1（速度）；左右超时另用 processed111000 等。"
          steps={[
            "若 s=1 且 ¬f：视为上升沿，置 f←true，执行恰好一次处理（读表或积分速度）。",
            "若 s=1 且 f：保持闭锁，不处理。",
            "若 s=0：f←false，允许下一上升沿；超时分支在灭灯期间累计 Δt。",
          ]}
          complexity="O(1)，无锁争用（单写入线程）。"
          correctness="把电平语义改造成边沿语义。退出界面必须 resetForNextEnter() 清旗标与时间基，否则下次会跳过整拍或用 (now−0) 造成假超时。"
        />
        <Algo
          no="18-5"
          title="动作频次估算"
          inputs="X1 上升沿时刻 t，上一有效沿 t_prev。"
          outputs="界面速度 v̂（次/分钟）。"
          source="readAndProcessRegisters() 内：frequency = 120.0 / timeDifference，再 Math.round。"
          steps={[
            "仅当 X1 上升沿（算法 18-4）时计算 Δt = (t − t_prev)/1000。",
            "若 Δt ≤ 2：视为抖动，丢弃，不更新 t_prev。",
            "否则 v ← 120/Δt，v̂ ← round(v)，invokeLater 写入速度框，t_prev ← t。",
            "曲线横坐标每点 +2，保存成功后累计次数按新增点数×2 增加，与 120=60×2 共用同一计数约定。",
          ]}
          complexity="O(1)。不另开定时器。"
          correctness="Δt>2 是抗抖动死区，不是奈奎斯特采样定理的应用。系数 120 在源码中是与步进 2 一致的软件约定；缺少机构模型时不得解释为凸轮半周期或传动比。源码注释区曾出现窗口为 5 的滑动平均，现行主路径未启用。"
        />
      </Sub>

      <Sub title="7）保护、对齐与持久化">
        <Algo
          no="18-6"
          title="四类保护判定"
          inputs="本拍 pureValue1..3、dianliu1Value1、灭灯时长、timeSeconds；阈值 dianzumax、2.1 A、testcount。"
          outputs="是否停机，以及 policetime 记录。"
          source="外层循环与凸轮灭灯分支；写寄存器 1（运行）与 2（报警）。"
          steps={[
            "电阻：任一路标量 > dianzumax（库表 d_dianzumax，id=1）→ 停机，记“电阻值故障”。",
            "电流：dianliu1Value1 > 2.1 → 停机，记“电流值故障”。",
            "机构：X1 或 X2 持续为 0 且 Δt∈(12,10000) → 停机，记左右凸轮转动故障。",
            "次数：timeSeconds ≥ testcount → 停机，提示已达设定次数。",
            "处理顺序：先写线圈，再入库，再 continueLoop2←false。判定在循环内部完成，不是试验结束后批处理。",
          ]}
          complexity="每拍 O(1) 次比较。"
          correctness="(12,10000) 同时抑制正常节拍与未初始化时间基。电流阈值硬编码，电阻阈值走库。JOptionPane 在工作线程弹出，会阻塞该循环直至对话框关闭。"
        />
        <Algo
          no="18-7"
          title="多源采样对齐"
          inputs="三路电阻显示串、电流隐藏串 dianzu444NO。"
          outputs="同一横坐标上的四元采样点，或本拍不加点。"
          source="startListening() 注册的 DocumentListener；只处理 insertUpdate。"
          steps={[
            "电流事务只写隐藏框，不触发正式显示。",
            "电阻事务在 EDT 上将隐藏值抄到 dianzu444，并同时写入三路电阻框。",
            "当四框均非空：extractNumber 得四路标量，lockYAxisRange，series1/2/3 与 rightSeries 在同一 timeSeconds 各加一点，timeSeconds ← timeSeconds+2。",
            "加点前对完全相同的重复插入做微量扰动，避免 JFreeChart 叠点。",
          ]}
          complexity="加点 O(1)；Y 轴锁定扫描当前最大值，与点数成线性，但每拍只调用一次。"
          correctness="完整性谓词是“四框非空”，不是“四路都是本毫秒的新 CRC 通过值”。电流实际采自 X2 沿，电阻采自 X1 沿，对齐的是同一机械循环而非同一时刻。"
        />
        <Algo
          no="18-8"
          title="基于历史采样点索引的增量持久化"
          inputs="当前 XYSeries，编号 b，内存游标 loadedPointCount，绑定编号 loadedTestBianHao。"
          outputs="插入行数；成功后更新 allcount 与游标。"
          source="保存按钮 → isTestBianHaoExists → saveTestResultsBatch。"
          steps={[
            "校验编号、次数、曲线点数非空。",
            "exists ← 库中是否已有 b；append ← exists ∧ b = loadedTestBianHao。",
            "start ← append ? loadedPointCount : 0。若点数 ≤ start，提示无新增。",
            "收集 [start, n) 的四系列 X/Y，构造行（日期、编号、次数、8 个坐标）。",
            "关闭 autoCommit，addBatch 后 executeBatch；成功 commit，失败 rollback。",
            "成功则 allcount ← allcount + 2·(n−start)，游标推到 n，绑定编号改为 b。",
          ]}
          complexity="I/O 与新增点数成线性。回显时暂时关闭 series.notify，批量 add，避免 O(n) 次重绘。"
          correctness="表结构无游标列，正确性依赖进程内游标与编号绑定。新编号（¬exists）不得清空正在画的曲线；loadingHistory 防止下拉回填递归加载。"
        />
        <Algo
          no="18-9"
          title="有序序列最近邻查询"
          inputs="单调递增的 XYSeries，光标映射后的横坐标 x。"
          outputs="|X_i − x| 最小的数据项；空序列返回 null。"
          source="findClosestDataItem；ChartMouseListener 用于竖线读数。"
          steps={[
            "令 min←+∞，从 i=0 扫描。",
            "d ← |X_i − x|。若 d < min，更新最近项；否则立即 break。",
            "利用“数据按 X 有序存储，距离一旦变大即可停止”这一性质提前结束。",
          ]}
          complexity="最坏 O(n)，有序提前停止后平均接近目标邻域。点数为寿命试验的采样拍数，可接受。"
          correctness="提前停止仅当序列对 X 单调。回显按 series1_x 升序加载，正式加点按 timeSeconds 递增，满足该前提。乱序插入会得到错误近邻。"
        />
      </Sub>

      <Sub title="8）4拖1：轮询调度与隔离停机">
        <Algo
          no="18-10"
          title="八从站时分轮询与缓存更新"
          inputs="COM5 上地址 01～08 的仪表；节拍 100ms。"
          outputs="ConcurrentHashMap 中的 current_0k / resistance_0k。"
          source="SixMeterInstrumentReader.startReading / sendNextQuery / processSensorData。"
          steps={[
            "若已 running 则直接返回，保证调度器单例。",
            "打开 COM5，flushIOBuffers，注册唯一 SerialPortDataListener。",
            "每 100ms：address ← (queryIndex mod 8)+1，queryIndex←queryIndex+1，发送 03H（起始 1、数量 3）。",
            "监听线程 sleep 80ms 后取当前可用字节；长度 < 9 或 CRC 失败则丢弃，不覆盖旧值。",
            "按 data[0] 分流：01～04 走电流解析，05～08 走电阻解析，写入对应键。",
            "工位循环在凸轮沿上只调用 getCurrentValue(i)、getResistanceValue(i+4)，不再 open/close。",
          ]}
          complexity="一轮 8×100ms ≈ 800ms。单次解析 O(1)。读缓存对工位循环为 O(1)。"
          correctness="配对键是帧内地址而不是发送序号，迟到应答不会写入邻站。CRC 失败保留旧缓存，掉线时可能用过期值做超限比较。COM5 不得再被 RRu/Wone 打开。"
        />
        <Algo
          no="18-11"
          title="工位级隔离停机"
          inputs="工位 i 的本拍测量、本工位阈值、共享 PLC 对象。"
          outputs="仅工位 i 停止；邻站 Future 继续。"
          source="ZzhejiPanel-20260811 各 beginCLBtn i；readAndProcessRegisters 为 synchronized。"
          steps={[
            "四个循环调用同一 synchronized 方法读 8 个输入字，COM4 访问串行化。",
            "工位 i 只解释属于自己的凸轮字，只读自己的仪表键。",
            "超限或超时：交错写寄存器 5（报警）与寄存器 i（运行=1），灯改 N，isTestingStarted i←false，cancel Future i。",
            "不调用 instrumentReader.stopReading()。入库带 Shebeihao=i，查重走分设备方法。",
          ]}
          complexity="停机路径 O(1) 次写寄存器。读 PLC 的临界区长度等于一次 8 字事务加四路速度积分。"
          correctness="机构隔离成立的前提是运行线圈按工位号切开。报警线圈 5 仍共享，工位 i 清 0 可能抹掉工位 j 的报警。Java synchronized 保证的是监视器互斥，不是 PLC 事务原子性。"
        />
      </Sub>

      <Callout title="算法在闭环中的位置" tone="idea">
        算法 18-2、18-3 保证“进入系统的数是完整且可还原的”；18-1、18-4、18-7
        保证“这些数属于同一机械循环且不被重复处理”；18-5、18-6
        把循环映射为速度与停机决策；18-8、18-9
        解决续测与交互读数；18-10、18-11
        在两路半双工介质上把上述闭环复制为四套可独立停机的实例。模块框图给出结构，算法给出不变式。
      </Callout>
    </Section>
  );
}
