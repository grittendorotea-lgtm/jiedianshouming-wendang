import { Callout, CodeBlock, Flow, KvTable, Section, Sub } from "../blocks";

export function PartFive() {
  return (
    <>
      <Section
        id="workflow"
        kicker="操作"
        title="操作员从进界面到保存，整条链路怎么走"
      >
        <p>
          课题报告里除了“类怎么写”，还要写清“人怎么用”。下面按{" "}
          <code>ZzhejiPanel</code> 真实按钮顺序还原。
        </p>
        <Flow
          steps={[
            "进入折线界面。构造函数从 d_dianzumax 读电阻上限，从 allcount 读累计次数，从 test_results 去重填充编号下拉框。日期默认今天。",
            "填测试编号：可选手动输入新号，也可选旧号。旧号会清空曲线并回显历史点；新号不清空当前曲线。",
            "点“开始测试”。PLC 寄存器1写0，设备转起来；后台线程开始盯 X1/X2。",
            "右侧凸轮到位 → 读三路电阻 → 文本框刷新。左侧凸轮到位 → 读电流 → 隐藏框再抄到电流框。四个框齐了，曲线加一个点，次数 +2，速度按 120/Δt 更新。",
            "若电阻超限、电流>2.1A、凸轮超过12秒不动、或次数到上限：停机、报警、弹窗，必要时写 policetime。",
            "点“停止测试”打断后台任务、消报警。再点“保存”：校验编号和次数，按新号全量 / 旧号追加写入 test_results，累计次数加 新增点数×2。",
            "点“退出”走 resetForNextEnter()：停定时器、取消任务、关串口、清曲线和沿标志，回到 Home。下次进来是干净状态。",
          ]}
        />
        <Callout title="为什么退出必须复位这么多标志" tone="note">
          <code>processed2</code>、<code>processed111000</code>、
          <code>processed555111</code>、<code>processedjishi1</code>{" "}
          都是 AtomicBoolean，用来做上升沿闭锁。如果不清，下一次进界面会认为“这一拍已经处理过”，电阻或速度不再更新。
          <code>lastTime111000 / lastTime555111</code>{" "}
          不清，会把两次试验之间的漫长间隔误判成凸轮超时。
        </Callout>
      </Section>

      <Section
        id="seq"
        kicker="时序"
        title="一拍里串口上到底发生了什么"
      >
        <p>
          假设设备地址、功能码按源码组帧，CRC 用同一套 0xA001 算法现场算出来，请求帧是确定的。下面是“右侧到位再左侧到位”这一拍的时间顺序。
        </p>
        <div className="overflow-x-auto rounded-lg border border-border bg-white">
          <table className="w-full text-left text-sm">
            <thead className="bg-slate-50 text-slate-600">
              <tr>
                <th className="px-3 py-2">顺序</th>
                <th className="px-3 py-2">谁占用 COM3</th>
                <th className="px-3 py-2">帧（十六进制）</th>
                <th className="px-3 py-2">目的</th>
              </tr>
            </thead>
            <tbody className="text-slate-700">
              <tr className="border-t">
                <td className="px-3 py-2">1 循环</td>
                <td className="px-3 py-2">PLC</td>
                <td className="px-3 py-2 font-mono text-xs">
                  08 04 00 00 00 02 71 52
                </td>
                <td className="px-3 py-2">读 2 路输入。功能码必须是 04，否则睡 10ms 重问。</td>
              </tr>
              <tr className="border-t">
                <td className="px-3 py-2">2</td>
                <td className="px-3 py-2">电阻表 地址2</td>
                <td className="px-3 py-2 font-mono text-xs">
                  02 03 00 01 00 03 54 38
                </td>
                <td className="px-3 py-2">X1=1 后关 PLC 口，读接触电阻1。</td>
              </tr>
              <tr className="border-t">
                <td className="px-3 py-2">3</td>
                <td className="px-3 py-2">电阻表 地址3</td>
                <td className="px-3 py-2 font-mono text-xs">
                  03 03 00 01 00 03 55 E9
                </td>
                <td className="px-3 py-2">读接触电阻2，间隔约 50ms。</td>
              </tr>
              <tr className="border-t">
                <td className="px-3 py-2">4</td>
                <td className="px-3 py-2">电阻表 地址4</td>
                <td className="px-3 py-2 font-mono text-xs">
                  04 03 00 01 00 03 54 5E
                </td>
                <td className="px-3 py-2">读接触电阻3，然后关电阻仪口。</td>
              </tr>
              <tr className="border-t">
                <td className="px-3 py-2">5 循环</td>
                <td className="px-3 py-2">PLC</td>
                <td className="px-3 py-2 font-mono text-xs">
                  08 04 00 00 00 02 71 52
                </td>
                <td className="px-3 py-2">重新打开 PLC 口，继续盯 X2。</td>
              </tr>
              <tr className="border-t">
                <td className="px-3 py-2">6</td>
                <td className="px-3 py-2">电流表 地址1</td>
                <td className="px-3 py-2 font-mono text-xs">
                  01 03 00 01 00 03 54 0B
                </td>
                <td className="px-3 py-2">X2=1 后关 PLC 口，发一帧，等约 200ms 取缓存值。</td>
              </tr>
            </tbody>
          </table>
        </div>
        <p className="pt-2">
          PLC 读输入的帧是按主界面参数（地址 08、功能 04、起始 0、数量
          2）用同一 CRC 算法推出来的；电阻和电流帧则与{" "}
          <code>buildModbusRequest</code> 完全一致。写报告时可以附这张表，证明协议是可复现的，不是“大概用了串口”。
        </p>
        <Sub title="应答怎么还原成 12.34 mΩ（举例）">
          <p>
            假设电阻表回：字节 3、4 为 <code>04 D2</code>（1234），字节 6 为{" "}
            <code>02</code>（两位小数），字节 8 为 <code>03</code>（mΩ）。则
          </p>
          <CodeBlock>{`rawValue = 0x04D2 = 1234
value   = 1234 / 10^2 = 12.34
unit    = mΩ`}</CodeBlock>
          <p>
            主界面 <code>extractNumber(&quot;12.34 mΩ&quot;)</code>{" "}
            得到 12.34，再和 <code>dianzumax</code> 比较。电流字符串{" "}
            <code>&quot;1.85 A&quot;</code> 则按空格拆第一段转 double。
          </p>
        </Sub>
      </Section>

      <Section
        id="ui-map"
        kicker="对照"
        title="界面上每个框对应源码里的哪一个变量"
      >
        <p>
          写“界面设计”这一节时，用这张表最省事。布局是绝对坐标，面板大约
          1620×950。
        </p>
        <KvTable
          rows={[
            { k: "测试编号", v: "可编辑 JComboBox testBianHao。选项来自 test_results.test_bianhao 去重。" },
            { k: "测试日期", v: "JDatePickerImpl testTime，默认今天，保存时转 java.sql.Date。" },
            { k: "监测次数", v: "只读 countTest。updateXAxis() 里写成当前 timeSeconds 上限。" },
            { k: "累计次数", v: "只读 allcountTest。启动和退出时 getAllcountValue1(1)。" },
            { k: "速度", v: "只读 suduTest，单位标签“次/分钟”，值为 roundedFrequency111。" },
            { k: "电阻上限提示", v: "labeldianzumax，文案“当前最大值为 xx mΩ”。" },
            { k: "接触电阻1/2/3", v: "dianzu111/222/333，深绿底绿字大号框，DocumentListener 驱动加点。" },
            { k: "电流", v: "dianzu444 显示；dianzu444NO 隐藏缓存。先写隐藏框，读完电阻再抄到显示框。" },
            { k: "开始测试", v: "beginCLBtn。测试中改红字“停止测试”。" },
            { k: "保存 / 退出", v: "btn1 / btn8。" },
            { k: "曲线复选框", v: "电阻1/2/3、电流，控制 setSeriesLinesVisible。" },
            { k: "红绿蓝黄坐标", v: "RedXY 等四个 JLabel，鼠标移动时显示最近点。" },
          ]}
        />
      </Section>

      <Section
        id="threads"
        kicker="并发"
        title="线程模型和串口调度"
      >
        <p>
          界面在 Swing EDT 上。正式采集不在 EDT 里跑，否则读串口一卡，窗口就假死。
        </p>
        <KvTable
          rows={[
            {
              k: "EDT",
              v: "按钮、setText、series.add、弹窗。采集结果一律 SwingUtilities.invokeLater 回界面。",
            },
            {
              k: "executorServicebingxing",
              v: "3 线程池。开始测试只 submit 一个 while 任务，Future 存在 currentTestFuture，停止时 cancel(true)。",
            },
            {
              k: "executorService",
              v: "另有单线程池，本面板几乎没用上，是早期留下的。",
            },
            {
              k: "testTimer",
              v: "1ms 的 Swing Timer，给未上屏的“模拟测试”按钮刷随机电阻。正式流程不用它。",
            },
            {
              k: "JiaoL 监听线程",
              v: "jSerialComm 自己的回调线程。主界面只睡 200ms 再读缓存，所以必须先发请求再等。",
            },
          ]}
        />
        <p>
          串口互斥完全靠“先 close 再 open”，没有锁文件，也没有统一的 PortScheduler。
          <code>readAndProcessRegisters</code> 加了{" "}
          <code>synchronized</code>，避免速度计算和主循环同时打开 PLC
          口，但电阻/电流对象各有自己的 <code>SerialPort</code>{" "}
          实例，真正防冲突的还是调用顺序。
        </p>
        <Callout title="曲线监听里的随机数并没有真正去重" tone="warn">
          <code>updateChart()</code> 拿当前电阻去和 0.0001～0.001
          的随机因子比大小，几乎每次插入都会加点。真正防止重复画的，是“四个框同时被
          setText 才进分支”，以及开始测试时先把框清空。写报告时不要把这段随机数写成“滤波算法”。
        </Callout>
      </Section>

      <Section
        id="limits"
        kicker="展望"
        title="源码里能写进“不足与改进”的点"
      >
        <p>
          课题报告最后一章通常要写局限。下面这些都是对照源码能指出来的，比空泛的“界面不够美观”有说服力。
        </p>
        <ul className="list-disc space-y-2 pl-5">
          <li>
            <strong>单串口时分复用没有统一调度器。</strong>{" "}
            PLC、电阻、电流各持一个 COM3 对象，靠人工 close/open。总线一忙或关口失败，整拍会丢。
          </li>
          <li>
            <strong>两套数据库连接。</strong>{" "}
            <code>DBConnection</code> 和 <code>JdbcDeal</code>{" "}
            并存，没有连接池配置可见于这批文件。
          </li>
          <li>
            <strong>三路电阻单位共用一个字段。</strong>{" "}
            最后一路的单位会覆盖前两路。应改为每路独立 unit。
          </li>
          <li>
            <strong>电流阈值写死 2.1A。</strong>{" "}
            电阻阈值已经进库，电流却没有，现场改门槛要改代码。
          </li>
          <li>
            <strong>绝对布局 + 固定 1620×950。</strong>{" "}
            换分辨率会挤在一起。滚动条和曲线区域也是写死坐标。
          </li>
          <li>
            <strong>模拟测试、清除按钮创建了但没放到当前布局。</strong>{" "}
            说明界面还在改，正式路径是“开始测试 + 保存 + 退出”。
          </li>
        </ul>
        <p>
          改进方向可以写成：抽一个串口调度器；参数（电阻上限、电流上限、凸轮超时、次数上限）全部进配置表；曲线保存改成“试验头表
          + 点表”两级，避免每个点重复存日期和编号。
        </p>
      </Section>

      <Section
        id="copy2"
        kicker="可粘贴 · 续"
        title="再给三节可直接改写的正文"
      >
        <Sub title="5. 系统操作流程（建议放第四章前半）">
          <p>
            操作员进入接点接触电阻试验界面后，系统自动加载电阻停机阈值、累计动作次数和历史试验编号。操作员输入或选择试验编号后启动测试，上位机向
            PLC 发出运行指令，设备凸轮开始转动。每当右侧行程开关有效，系统释放串口并依次读取三路接触电阻；每当左侧行程开关有效，系统读取回路电流。四路数据同时刷新到界面后，折线图在当前动作次数处增加一个采样点，并按相邻右侧信号间隔计算动作速度。试验过程中若出现电阻超限、电流超限、凸轮超时或次数到达设定值，系统立即下达停机和报警指令。试验结束后，操作员可将本次曲线按编号保存；若编号已存在，则只追加新采样点，从而实现同一试品的分段试验与连续回放。
          </p>
        </Sub>
        <Sub title="6. 通信时序设计（建议放第四章）">
          <p>
            系统在 RS-485
            总线上采用主从问答。PLC 使用功能码 04H
            循环读取两路开关量；电阻表、电流表使用功能码 03H
            读取测量值、小数位和单位。由于多设备共享同一串口，上位机在开关量有效时主动关闭
            PLC 通道，再分别访问从站 2、3、4 和从站
            1，完成后再交还 PLC。每一帧均附带 CRC-16
            校验，校验失败的数据不进入曲线，避免干扰点污染寿命曲线。
          </p>
        </Sub>
        <Sub title="7. 不足与展望（建议放第六章）">
          <p>
            当前实现已经能够完成在线监测、保护停机和历史追溯，但仍存在单串口调度依赖调用顺序、电流阈值硬编码、电阻单位字段共用、界面采用固定分辨率绝对布局等问题。后续工作可将串口访问收敛到统一调度组件，将全部保护阈值参数化入库，并将曲线存储拆分为试验主表与采样点表，以提高大数据量试验时的查询效率和可维护性。
          </p>
        </Sub>
      </Section>
    </>
  );
}
