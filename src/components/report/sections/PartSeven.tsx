import { Algo, Callout, Formula, KvTable, PaperImg, Section, Sub } from "../blocks";

export function PartSeven() {
  return (
    <Section id="algo" kicker="第十八节" title="模块框图与关键算法">
      <p>
        本节对应论文「软件系统设计 / 测控系统设计」章节，按 Word 稿《继电器寿命测试软件系统：模块框图与关键算法分析》整理。软件平台为
        Java 桌面端测控系统；核心对象是继电器或往复机构寿命试验中的接触电阻、工作电流与动作状态；主要技术为 Java
        Swing、JFreeChart、jSerialComm、Modbus RTU、JDBC 与多线程。文中所称「算法」属于实时测控、工业通信与故障诊断，不是机器学习。
      </p>
      <p>
        稿中框图与流程图按基础版单工位源码绘制。4 拖 1 拓展版的仪器轮询、八字状态同步读取与分站 Future
        调度列在 18.8，不并入基础版七层叙述。
      </p>

      <Sub id="p18-1" title="18.1 软件系统总体设计">
        <p>
          该软件面向继电器或具有往复动作机构的寿命试验过程，采用「上位机界面—串口通信—PLC/IO
          状态采集—电参数采集—实时判定—联锁控制—曲线显示—数据库追溯」的闭环结构。软件不仅完成测试数据的显示与保存，还承担动作状态识别、速度计算、接触电阻与电流越限判定、机构超时诊断以及异常停机控制等实时测控任务。
        </p>
        <p>
          从软件工程角度看，系统划分为七个相互协同的功能层：人机交互与参数管理层、测试控制与并发调度层、设备通信与协议层、数据采集与信号解析层、实时控制与故障诊断算法层、实时曲线与交互可视化层，以及数据持久化与历史回放层。各层之间通过状态量、测量量和控制命令形成闭环数据流。
        </p>
        <PaperImg
          src="/figures/fig1-module-architecture.png"
          no="18-1"
          title="软件总体模块框图（论文图 1）"
        />
        <p>
          图中七层自顶向下对应人机交互与参数管理、测试控制与并发调度、设备通信与协议、数据采集与信号解析、实时控制与故障诊断、实时曲线与交互可视化、数据持久化与历史回放。诊断层发现故障后回写通信层停机，曲线层与数据层双向回放。
        </p>
        <p className="text-sm font-semibold text-slate-900">
          表 1　图 1 各层级的技术与算法对应关系
        </p>
        <KvTable
          rows={[
            {
              k: "① 人机交互层",
              v: "参数管理、操作控制、实时量显示。技术：Swing、FlatLaf、JDatePicker。机制：事件驱动、参数合法性校验。输入编号/日期/阈值/用户命令，输出测试配置与界面状态。",
            },
            {
              k: "② 控制调度层",
              v: "测试状态、多线程、UI 线程协调。技术：ExecutorService、Future、AtomicBoolean。机制：任务调度、状态去重、线程安全更新。输入测试命令与状态标志，输出后台采集任务与 UI 刷新任务。",
            },
            {
              k: "③ 通信协议层",
              v: "串口、寄存器读写、帧校验。技术：jSerialComm、Modbus RTU。机制：0x04/0x06、CRC-16/Modbus。输入地址/寄存器/控制值，输出状态响应与控制输出。",
            },
            {
              k: "④ 数据采集层",
              v: "PLC 状态、电阻、电流采集。类：RRuANDWone、DianZu00000、JiaoL。机制：寄存器解析、正则数值提取。输入原始字节/测量字符串，输出 value/value2、R1–R3、I。",
            },
            {
              k: "⑤ 诊断算法层",
              v: "速度、越限、超时、终止判定。技术：System.currentTimeMillis、阈值参数。机制：120/Δt、Rmax 判定、I>2.1 A、12 s 看门狗。输入状态量、时间、电参数，输出正常/故障/停机条件。",
            },
            {
              k: "⑥ 可视化层",
              v: "实时曲线、双 Y 轴、光标查询。技术：JFreeChart、XYPlot、XYSeries。机制：动态窗口、最近邻点检索。输入 R1–R3、I、采样序号，输出趋势曲线与光标数值。",
            },
            {
              k: "⑦ 数据层",
              v: "结果保存、历史回放、故障追溯。技术：JDBC、PreparedStatement。机制：批量入库、增量续写、按编号升序回放。输出 test_results、policetime、历史曲线。",
            },
          ]}
        />
      </Sub>

      <Sub id="p18-11" title="18.1.1 软件开发技术与运行机制">
        <p className="text-sm font-semibold text-slate-900">
          表 2　软件主要开发技术及其功能
        </p>
        <KvTable
          rows={[
            {
              k: "界面开发",
              v: "Java Swing / JPanel / SwingUtilities。构建桌面端操作界面；事件分发线程负责安全刷新界面。",
            },
            {
              k: "界面风格",
              v: "FlatLaf。实现轻量化、现代化 Swing 外观。",
            },
            {
              k: "日期控件",
              v: "JDatePicker。测试日期选择与历史记录回显。",
            },
            {
              k: "实时曲线",
              v: "JFreeChart / XYPlot / XYSeries。显示三路接触电阻与一路电流的实时变化曲线。",
            },
            {
              k: "串口通信",
              v: "jSerialComm。访问 COM3 串口，波特率 9600 bit/s。",
            },
            {
              k: "工业协议",
              v: "Modbus RTU。0x04 读取输入寄存器；0x06 写单个保持寄存器。",
            },
            {
              k: "报文校验",
              v: "CRC-16/Modbus。初值 0xFFFF，多项式 0xA001，保障通信帧完整性。",
            },
            {
              k: "并发调度",
              v: "ExecutorService / Future。将实时测试循环放入后台线程执行，可中断任务。",
            },
            {
              k: "并发安全",
              v: "AtomicBoolean / volatile。状态防重复触发、跨线程可见性与流程同步。",
            },
            {
              k: "数据库",
              v: "JDBC / PreparedStatement。测试结果、累计次数、阈值参数及故障记录持久化。",
            },
            {
              k: "数据驱动",
              v: "DocumentListener。测量文本发生变化时触发曲线采样与刷新。",
            },
          ]}
        />
      </Sub>

      <Sub id="p18-2" title="18.2 软件功能模块设计">
        <p className="text-sm font-semibold text-slate-900">
          18.2.1 人机交互与参数管理模块
        </p>
        <p>
          该模块负责测试编号、测试日期、监测次数、速度、累计次数、最大允许接触电阻等信息的显示与管理，并提供开始测试、停止测试、保存、退出等操作入口。界面基于
          Java Swing 构建，并通过 FlatLaf 优化视觉风格。测试编号采用可编辑下拉框形式，既可输入新编号，也可选择数据库中的历史编号。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>输入：测试编号、测试参数、用户操作命令。</li>
          <li>处理：参数合法性检查、按钮事件分发、历史编号识别、界面状态切换。</li>
          <li>输出：启动/停止命令、参数配置、实时测试状态。</li>
        </ul>

        <p className="text-sm font-semibold text-slate-900">
          18.2.2 测试控制与并发调度模块
        </p>
        <p>
          正式测试采用后台任务执行，以避免串口等待、设备采集和循环判定阻塞 Swing 事件分发线程。系统使用 ExecutorService
          提交测试任务，并用 Future 保存当前任务句柄，在用户主动停止测试或退出页面时可取消执行中的任务。多个
          AtomicBoolean 状态变量用于标记某个触发状态是否已经处理，从而避免同一输入信号在持续高电平期间被重复采样。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>后台测试循环与 UI 线程解耦。</li>
          <li>AtomicBoolean 实现状态去重和线程安全标志。</li>
          <li>SwingUtilities.invokeLater 将测量结果安全回写到界面。</li>
          <li>测试退出时统一取消任务、关闭串口并重置状态变量。</li>
        </ul>

        <p className="text-sm font-semibold text-slate-900">
          18.2.3 设备通信与 Modbus RTU 协议模块
        </p>
        <p>
          通信层使用 jSerialComm 管理串口，并在 COM3、9600 bit/s 参数下与下位设备通信。软件直接构造 Modbus RTU
          请求帧：功能码 0x04 用于读取输入寄存器，功能码 0x06 用于写单个保持寄存器。控制报文在发送前计算 CRC16
          校验码，响应帧读取后再依据功能码和数据区进行解析。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>0x04：采集 PLC/IO 的输入状态。</li>
          <li>0x06：写控制寄存器，实现试验机构启停与报警联锁。</li>
          <li>CRC-16/Modbus：对请求帧执行循环异或与右移运算，降低通信误码风险。</li>
        </ul>

        <p className="text-sm font-semibold text-slate-900">
          18.2.4 数据采集与信号解析模块
        </p>
        <p>
          系统包含三类核心采集对象：PLC/IO 状态、三路接触电阻以及一路工作电流。首先轮询设备地址 0x08
          的两个输入寄存器，得到动作相关状态量；当检测到指定状态后，软件切换到对应采集对象，读取 R1、R2、R3 或电流
          I。测量字符串通过正则表达式提取数值部分并转换为 double，以便参与后续阈值计算和曲线绘制。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>状态量：value、value2。</li>
          <li>电阻量：R1、R2、R3，显示单位 mΩ。</li>
          <li>电流量：I，显示单位 A。</li>
          <li>数据清洗：去除单位字符与非数值符号，统一转为 double。</li>
        </ul>

        <p className="text-sm font-semibold text-slate-900">
          18.2.5 实时控制与故障诊断模块
        </p>
        <p>
          该模块是软件的核心逻辑层。系统持续分析状态量、时间间隔、接触电阻、电流以及累计测试次数。当任一异常条件满足时，软件通过
          Modbus 0x06 写控制寄存器执行停机/报警，并将故障原因保存到故障表中。与一般数据采集软件相比，该模块形成了「检测—诊断—执行—记录」的闭环控制链。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>接触电阻越限：任一路 R &gt; Rmax。</li>
          <li>电流越限：I &gt; 2.1 A。</li>
          <li>机构超时：相关状态持续超过 12 s 未出现有效变化。</li>
          <li>次数终止：当前测试次数达到数据库配置的最大测试次数。</li>
        </ul>

        <p className="text-sm font-semibold text-slate-900">
          18.2.6 实时曲线与交互可视化模块
        </p>
        <p>
          曲线采用 JFreeChart 实现。三路接触电阻共享左侧 Y 轴，电流使用右侧 Y
          轴，从而解决不同物理量量纲差异导致的显示问题。程序根据新数据动态追加 XYSeries，并维护 X
          轴滑动显示窗口。鼠标在图表上移动或点击时，系统依据横坐标距离寻找最近数据点，实时显示 R1、R2、R3 和 I 的对应值。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>多序列同步绘制：R1 / R2 / R3 / I。</li>
          <li>双 Y 轴：电阻与电流分轴显示。</li>
          <li>动态 X 轴：仅显示最近一段数据，便于观察实时趋势。</li>
          <li>最近邻检索：查找与鼠标横坐标距离最小的数据点。</li>
        </ul>

        <p className="text-sm font-semibold text-slate-900">
          18.2.7 数据持久化与历史回放模块
        </p>
        <p>
          系统通过 JDBC 对测试数据进行持久化。实时曲线由四组 X/Y 序列组成，保存时将同一采样序号下的四组曲线数据组织成批量插入记录。为了支持「历史编号继续测试」，程序保存
          loadedPointCount 与 loadedTestBianHao：当当前编号与已回放编号一致时，仅保存新增点；若为新编号，则从第 0
          个点开始保存。历史加载时按横坐标升序读取曲线，并恢复测试日期、测试次数和下一次绘图起点。
        </p>
        <ul className="my-3 list-disc space-y-1 pl-5 text-sm leading-7 text-slate-700">
          <li>批量写入 test_results，提高连续曲线保存效率。</li>
          <li>增量保存，避免历史曲线重复入库。</li>
          <li>按测试编号回放历史曲线。</li>
          <li>policetime 保存异常类型、次数及故障发生时间。</li>
        </ul>
      </Sub>

      <Sub id="p18-3" title="18.3 关键算法设计">
        <p>
          本系统中的「算法」主要属于实时测控、工业通信和故障诊断算法，而非机器学习算法。其设计目标是确保测量数据可采集、试验状态可识别、故障条件可判定、控制输出可及时执行，并保证历史数据可追溯。
        </p>

        <p className="text-sm font-semibold text-slate-900">
          18.3.1 Modbus RTU CRC16 校验算法
        </p>
        <p>
          Modbus RTU 报文采用 CRC16 校验。程序将 CRC 初值设为 0xFFFF，依次与数据字节异或；随后针对每个字节执行 8
          次最低位判断。当最低位为 1 时，右移后再与多项式 0xA001 异或；否则仅右移。最终得到的 16
          位 CRC 低字节先发送，高字节后发送。
        </p>
        <Formula no="1">CRC₀ = 0xFFFF</Formula>
        <Formula no="2">CRC ← (CRC ≫ 1) ⊕ 0xA001　　（LSB = 1）</Formula>
        <Formula no="3">CRC ← CRC ≫ 1　　（LSB = 0）</Formula>
        <p>
          其中 LSB 表示当前 CRC 的最低有效位。每个数据字节均进行 8 次移位运算。源码实现见{" "}
          <code>RRuANDWone.calculateCRC16</code>。
        </p>
        <Algo
          no="18-A"
          title="CRC-16/Modbus"
          inputs="请求帧字节 buf[0..n−1]（不含 CRC）"
          outputs="crcLo、crcHi；发送顺序低字节在前"
          source="RRuANDWone.calculateCRC16"
          steps={[
            "crc ← 0xFFFF。",
            "对每个字节：crc ← crc ⊕ byte；重复 8 次：若 LSB=1 则 crc ← (crc≫1) ⊕ 0xA001，否则 crc ← crc≫1。",
            "crcLo = crc & 0xFF，crcHi = (crc≫8) & 0xFF。",
          ]}
          complexity="O(n)。单帧 n≤8，耗时可忽略。"
          correctness="与 Modbus 规范多项式 0xA001、初值 0xFFFF 一致；低字节先发。"
        />

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.2 状态边沿识别与防重复触发算法
        </p>
        <p>
          PLC 输入状态在实际运行中可能连续保持为 1。若程序每轮循环都把高电平视为一次新动作，将导致同一机械动作被重复计数或重复采样。因此系统为不同状态配置
          AtomicBoolean 标志。当状态首次由未处理进入有效状态时执行采集，并立即将标志置为已处理；待状态恢复后再重新开放下一次触发。该方法本质上相当于软件状态机中的边沿检测与去抖式去重。
        </p>
        <Algo
          no="18-B"
          title="上升沿闩锁"
          inputs="本拍 X，上一有效标志 processed（AtomicBoolean）"
          outputs="是否执行本拍采集"
          source="ZzhejiPanel 测试循环"
          steps={[
            "若 X=1 且 processed=false：置 processed=true，执行采集。",
            "若 X=0：置 processed=false，开放下一拍。",
            "若 X=1 且 processed=true：本拍不采集。",
          ]}
          complexity="O(1)。"
          correctness="持续高电平只产生一次有效边沿；复位条件写在 X=0 分支。"
        />

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.3 基于状态触发时间差的速度估计算法
        </p>
        <p>
          软件记录相邻有效触发事件的时间戳，通过时间差估计动作速度。源码中采用常数 120
          与触发周期相除，并对结果进行四舍五入，因此可以将其表述为基于周期测量的频率/速度估计算法。tₖ 和 tₖ₋₁
          为相邻两次有效触发的系统时间戳，单位为 ms；Δt 转换为 s。当 Δt 大于 2 s
          时执行速度计算，并将结果显示在界面速度字段中。常数 120 的机械含义源码未注释，论文只写公式本身。
        </p>
        <Formula no="4">Δt = (tₖ − tₖ₋₁) / 1000</Formula>
        <Formula no="5">v = round(120 / Δt)</Formula>

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.4 接触电阻阈值诊断算法
        </p>
        <p>
          最大允许接触电阻 Rmax 从数据库读取，三路实测接触电阻分别记为 R1、R2、R3。当任一路超过设定上限时，系统判定当前测试异常，并执行停机与报警。该判据采用「任一路越限即故障」的保守策略，可防止单个触点性能劣化被平均值掩盖。故障发生后，系统进一步拼接具体超限通道及实测值，便于后续质量追溯。
        </p>
        <Formula no="6">Rfault = 1，　若 max(R₁, R₂, R₃) &gt; Rmax</Formula>
        <Formula no="7">Rfault = 0，　否则</Formula>

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.5 电流越限判定算法
        </p>
        <p>
          电流通道的故障阈值在当前程序中设置为 2.1 A。当采集电流 I
          大于该阈值时，软件立即写入控制寄存器并结束当前测试循环，同时记录「电流值故障」。
        </p>
        <Formula no="8">Ifault = 1，　若 I &gt; 2.1 A</Formula>

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.6 基于时间窗口的机构超时故障诊断
        </p>
        <p>
          系统分别跟踪两个关键动作状态的持续时间。如果某一动作状态在预期切换阶段持续超过 12
          s，则认为对应机构可能出现卡滞、无转动或反馈丢失。程序随后执行报警输出、停机并写入故障日志。该算法属于典型的看门狗式时间约束诊断。与单纯判断
          0/1 状态不同，它利用「状态持续时间」识别机械机构是否在合理时间内完成动作，因此更适合寿命试验过程中的卡滞和失步故障检测。源码窗口写为
          12 s &lt; Δt &lt; 10000 s，上界用于避开计时未初始化的伪超时。
        </p>
        <Formula no="9">Tfault = 1，　若 Δt_no-signal &gt; 12 s</Formula>

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.7 测试次数终止判定
        </p>
        <p>
          测试次数上限由数据库参数提供。系统在循环过程中比较当前计数坐标 timeSeconds
          与目标次数，一旦达到或超过设定值，即写控制寄存器使试验停止。
        </p>
        <Formula no="10">Stop = 1，　若 Ncurrent ≥ Nset</Formula>

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.8 曲线最近邻点检索算法
        </p>
        <p>
          为了支持图表交互，程序将鼠标位置转换为数据坐标 x，并在按横坐标有序存储的 XYSeries 中查找距离 |Xᵢ −
          x| 最小的数据点。由于数据按 X 递增，当距离开始增大时即可提前终止搜索。该方法计算简单，能够在实时曲线浏览时快速返回光标附近的电阻和电流值。
        </p>
        <Formula no="11">i* = arg minᵢ |Xᵢ − xmouse|</Formula>
        <Algo
          no="18-C"
          title="有序最近邻早停"
          inputs="鼠标数据坐标 xmouse，有序序列 Xᵢ"
          outputs="最近下标 i* 及对应 R1/R2/R3/I"
          source="ZzhejiPanel 图表鼠标监听"
          steps={[
            "best ← +∞。",
            "按 i 递增扫描；d = |Xᵢ − xmouse|。",
            "若 d < best：更新 best 与 i*；若 d 开始增大：提前终止。",
            "用 i* 回读四条曲线的 Y 值。",
          ]}
          complexity="最坏 O(n)，有序早停后通常远小于 n。"
          correctness="X 单调递增时，距离先减后增，早停不漏最近邻。"
        />

        <p className="pt-4 text-sm font-semibold text-slate-900">
          18.3.9 历史数据增量保存算法
        </p>
        <p>
          为了避免在历史编号续测后重复写入已有曲线，系统将历史加载后的点数记录为 loadedPointCount。如果当前保存编号与已加载编号一致，则从
          loadedPointCount 开始遍历新增点；否则视为新试验编号，从 0
          开始保存。该设计实现了历史测试「加载—继续采集—仅追加新增点」的数据连续性，同时避免重复数据导致曲线和累计次数失真。
        </p>
        <Formula no="12">saveStartIndex = loadedPointCount，　若 IDcurrent = IDloaded</Formula>
        <Formula no="13">saveStartIndex = 0，　否则</Formula>
      </Sub>

      <Sub id="p18-4" title="18.4 核心测试控制流程">
        <p>
          软件的主控制流程以实时循环为核心：开始测试后读取阈值和次数参数，初始化控制寄存器并进入后台采集线程；随后持续读取
          PLC/IO 状态，根据状态触发电阻或电流采集，执行速度估计、阈值判断和超时判断；若发生异常，则通过 0x06
          功能码写入控制寄存器实施联锁停机，同时记录故障；若未达到终止条件，则继续下一轮循环。
        </p>
        <PaperImg
          src="/figures/fig2-control-flow.png"
          no="18-2"
          title="核心测试控制算法流程图（论文图 2）"
        />
        <p>
          初始化后进入 0x04 轮询；X1 支路测电阻并做 Rmax / 12 s 诊断，X2 支路测电流并做 2.1 A / 12 s
          诊断；异常走联锁停机并写入 policetime，正常走次数比较与增量保存。
        </p>
      </Sub>

      <Sub id="p18-5" title="18.5 模块输入—处理—输出关系">
        <p className="text-sm font-semibold text-slate-900">
          表 3　软件模块输入、处理与输出关系
        </p>
        <KvTable
          rows={[
            {
              k: "参数管理",
              v: "输入编号、日期、Rmax、目标次数。处理：合法性校验、参数读取/回显。输出测试配置、界面状态。",
            },
            {
              k: "PLC/IO 通信",
              v: "输入设备地址、寄存器地址。处理：Modbus 0x04 轮询、CRC16。输出 value、value2。",
            },
            {
              k: "电阻采集",
              v: "输入有效动作触发。处理：读取 R1/R2/R3 并解析。输出三路接触电阻。",
            },
            {
              k: "电流采集",
              v: "输入第二状态触发。处理：读取电流并解析。输出工作电流 I。",
            },
            {
              k: "速度计算",
              v: "输入相邻触发时刻。处理：Δt 与 120/Δt。输出速度/频率估计值。",
            },
            {
              k: "故障诊断",
              v: "输入 R1/R2/R3/I/状态持续时间。处理：阈值判定、超时判定。输出正常/故障类型。",
            },
            {
              k: "联锁控制",
              v: "输入故障类型/终止条件。处理：Modbus 0x06 写寄存器。输出停机、报警。",
            },
            {
              k: "实时可视化",
              v: "输入 R1/R2/R3/I、采样序号。处理：XYSeries、双 Y 轴、动态窗口。输出实时趋势曲线。",
            },
            {
              k: "数据存储",
              v: "输入四组 XY 数据、试验信息。处理：批量插入、增量保存。输出 test_results。",
            },
            {
              k: "故障追溯",
              v: "输入故障原因、次数、时间。处理：PreparedStatement 插入。输出 policetime。",
            },
            {
              k: "历史回放",
              v: "输入测试编号。处理：SQL 查询、升序恢复 XY 点。输出历史曲线、日期、次数。",
            },
          ]}
        />
      </Sub>

      <Sub id="p18-6" title="18.6 可直接用于论文的系统设计表述">
        <p>
          本研究采用 Java 语言开发寿命试验上位机软件，并基于 Swing
          构建人机交互界面。系统通过 jSerialComm 实现上位机与下位控制器之间的串口通信，在 Modbus RTU
          协议框架下分别采用 0x04 功能码读取输入寄存器、采用 0x06
          功能码写单个保持寄存器，实现状态监测与试验机构控制。为提高通信可靠性，软件在请求帧中嵌入 CRC-16/Modbus 校验。
        </p>
        <p>
          在实时测试阶段，软件采用 ExecutorService 将设备轮询与数据采集任务放置于后台线程中执行，并利用
          AtomicBoolean 对关键状态进行防重复触发控制。系统根据 PLC
          输入状态选择性采集三路接触电阻和一路电流，结合相邻动作触发时间差计算运行速度。针对寿命试验过程中可能出现的异常，构建了基于接触电阻阈值、电流阈值和动作超时的多条件故障判定策略。当检测到任一路接触电阻超过设定上限、电流超过
          2.1 A，或关键动作状态持续超过 12
          s 时，系统立即发送控制指令执行停机和报警，并将故障类型及发生时刻写入数据库。
        </p>
        <p>
          测试数据采用 JFreeChart 进行动态可视化，其中三路接触电阻共用左侧 Y 轴，电流采用独立右侧 Y
          轴。各通道数据以 XYSeries 形式实时追加，并通过滑动时间窗口实现连续趋势显示。为支持历史试验追溯，软件以测试编号为索引保存曲线数据，并设计增量保存机制：对已加载的历史编号仅保存后续新增数据点，从而避免重复记录。历史记录重新加载后，可恢复原有曲线、测试日期及累计次数，并在历史最大横坐标之后继续绘制，实现寿命试验数据的连续记录。
        </p>
      </Sub>

      <Sub id="p18-7" title="18.7 附录：源码对照与算法命名">
        <p className="text-sm font-semibold text-slate-900">
          表 A1　源码类与软件功能模块的对应关系
        </p>
        <KvTable
          rows={[
            {
              k: "ZzhejiPanel",
              v: "主界面/主控制器。界面组件、开始/停止测试、数据采集调度、曲线更新、故障判断、保存/回放。",
            },
            {
              k: "RRuANDWone",
              v: "PLC/IO 通信层。串口打开/关闭、Modbus 0x04 读取、0x06 写寄存器、CRC16。",
            },
            {
              k: "DianZu00000",
              v: "接触电阻采集。读取并保存三路电阻值。",
            },
            {
              k: "JiaoL",
              v: "电流采集。读取工作电流及单位化输出。",
            },
            {
              k: "JdbcDeal",
              v: "数据库连接/查询。建立连接、执行编号查询等。",
            },
            {
              k: "ExecuteCommon",
              v: "数据库业务访问。阈值参数读取、累计次数更新、批量保存测试结果。",
            },
            {
              k: "JFreeChart / XYSeries",
              v: "可视化。实时曲线、多序列、双 Y 轴、历史曲线恢复。",
            },
            {
              k: "ExecutorService / Future",
              v: "并发执行。后台测试任务、停止时取消任务。",
            },
            {
              k: "AtomicBoolean",
              v: "状态同步。避免持续高电平重复处理，协调测试状态。",
            },
            {
              k: "DocumentListener",
              v: "数据驱动刷新。测量框数据变化后触发 XY 序列更新。",
            },
          ]}
        />
        <Callout title="附录 B　论文撰写时的算法命名建议" tone="idea">
          为保证论文表述与源码一致，建议将本系统中的算法称为「实时测控与故障诊断算法」「基于状态触发的速度估计算法」「基于阈值与时间窗口的故障判定算法」「Modbus
          RTU 报文校验算法」「历史曲线增量存储算法」。当前源码并未体现机器学习、神经网络、模糊控制、PID
          自适应控制等算法，因此论文中不建议写入这些未实际实现的技术，以免答辩或审稿时出现源码与文字不一致。
        </Callout>
      </Sub>

      <Sub id="p18-8" title="18.8 4 拖 1 拓展版源码中的补充算法">
        <p>
          下列算法只存在于 <code>SixMeterInstrumentReader</code> 与{" "}
          <code>ZzhejiPanel-20260811</code>
          ，不出现在论文图 1、图 2 的基础版框图中。答辩时若被问到四工位，用本节；写基础版论文时不要把它们写进 18.1–18.6。
        </p>
        <Algo
          no="18-D"
          title="八仪表时分复用轮询"
          inputs="COM5 上从站 01–08"
          outputs="ConcurrentHashMap 中的最新电流/电阻"
          source="SixMeterInstrumentReader.run / readCurrentValue / readResistanceValue"
          steps={[
            "独立线程每 100 ms 启动一轮。",
            "顺序读 01–04 电流、05–08 电阻；每帧后 listener 等待约 80 ms。",
            "合法响应写入 ConcurrentHashMap；工位循环只读缓存，不再开仪表串口。",
          ]}
          complexity="一轮约 800 ms，受串口等待主导。"
          correctness="单口互斥由轮询线程独占；工位线程不争用 COM5。"
        />
        <Algo
          no="18-E"
          title="八字输入寄存器同步快照"
          inputs="COM4、PLC 地址 0x01、功能码 0x04、数量 8"
          outputs="8 个工位凸轮字的一致快照"
          source="ZzhejiPanel-20260811.readAndProcessRegisters"
          steps={[
            "方法加 synchronized，同时只允许一拍读。",
            "一次读回 8 个输入字，再按工位切开。",
            "各工位用本站 AtomicBoolean 做边沿闩锁。",
          ]}
          complexity="O(1) 次事务 / 拍。"
          correctness="同步保证四站看到同一拍快照，避免半新半旧。"
        />
        <Algo
          no="18-F"
          title="分站 Future 独立启停"
          inputs="工位 i 的开始/停止命令"
          outputs="仅工位 i 的测试任务生命周期"
          source="ZzhejiPanel-20260811 四套 executor / Future / isTestingStarted"
          steps={[
            "每站独立线程池与 Future。",
            "开始：置本站 isTestingStarted，提交本站循环。",
            "停止：cancel 本站 Future，写本站运行寄存器为 0；其他站不受影响。",
          ]}
          complexity="O(1) / 站。"
          correctness="启停键绑定本站 Future，不共享单一 running 标志。"
        />
      </Sub>
    </Section>
  );
}
