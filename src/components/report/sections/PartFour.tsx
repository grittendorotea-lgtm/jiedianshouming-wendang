import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, KvTable, Section, Sub } from "../blocks";

export function PartFour() {
  return (
    <>
      <Section id="protocol" kicker="通信" title="Modbus-RTU 协议在本系统里的用法">
        <p>
          电阻、电流、PLC 都是 Modbus-RTU。写课题报告时建议单独开一节“通信协议设计”，把帧格式和地址分配写清楚。
        </p>
        <Sub title="统一的请求帧">
          <CodeBlock title="8 字节请求">{`[从站地址] [功能码] [起始地址高] [起始地址低]
[数量高] [数量低] [CRC低] [CRC高]`}</CodeBlock>
          <div className="overflow-x-auto rounded-lg border border-border bg-white">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="px-3 py-2">设备</th>
                  <th className="px-3 py-2">地址</th>
                  <th className="px-3 py-2">功能码</th>
                  <th className="px-3 py-2">起始</th>
                  <th className="px-3 py-2">数量</th>
                </tr>
              </thead>
              <tbody className="text-slate-700">
                <tr className="border-t">
                  <td className="px-3 py-2">电流表 JiaoL</td>
                  <td className="px-3 py-2">0x01</td>
                  <td className="px-3 py-2">0x03 读保持</td>
                  <td className="px-3 py-2">1</td>
                  <td className="px-3 py-2">3</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">电阻表 1/2/3</td>
                  <td className="px-3 py-2">2 / 3 / 4</td>
                  <td className="px-3 py-2">0x03 读保持</td>
                  <td className="px-3 py-2">1</td>
                  <td className="px-3 py-2">3</td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2">PLC</td>
                  <td className="px-3 py-2">0x08</td>
                  <td className="px-3 py-2">0x04 读输入</td>
                  <td className="px-3 py-2">0</td>
                  <td className="px-3 py-2">2</td>
                </tr>
              </tbody>
            </table>
          </div>
        </Sub>
        <Sub title="仪表应答的数据区">
          <p>电阻/电流表在功能码之后给出：</p>
          <KvTable
            rows={[
              { k: "data[3..4]", v: "16 位原始测量值，大端。" },
              { k: "data[6]", v: "小数点位数。" },
              { k: "data[8]", v: "单位码。电阻 3/4/5，电流 3/4。" },
              { k: "末 2 字节", v: "CRC，低字节在前。" },
            ]}
          />
        </Sub>
        <Sub title="CRC-16 算法（三类设备共用）">
          <CodeBlock title="calculateCRC">{`int crc = 0xFFFF;
for (int j = 0; j < length; j++) {
    crc ^= data[j] & 0xFF;
    for (int i = 8; i > 0; i--) {
        if ((crc & 0x0001) != 0) {
            crc >>= 1;
            crc ^= 0xA001;
        } else {
            crc >>= 1;
        }
    }
}`}</CodeBlock>
          <p>
            这是标准 Modbus CRC-16（IBM/Modbus 多项式 0x8005 的反射形式
            0xA001）。请求时对前 6 字节计算，应答时对除 CRC
            外的整帧计算。校验失败直接丢弃，避免把噪声画进曲线。
          </p>
        </Sub>
      </Section>

      <Section id="classes" kicker="源码" title="五个类分别承担什么">
        <div className="grid gap-3 md:grid-cols-2">
          <Card>
            <CardHeader>
              <Badge variant="chart">约 2750 行</Badge>
              <CardTitle className="pt-2 text-base">ZzhejiPanel</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              测试主面板。负责全部人机交互、测试状态机、曲线、保存、回显、保护停机、速度计算。内部还有
              StyledButton、GradientPanel 两个内部类做渐变按钮和背景。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="rust">电阻通信</Badge>
              <CardTitle className="pt-2 text-base">DianZu00000</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              同步轮询从站 2/3/4。打开、读取、解析、关闭。给主界面三个
              getter：getDianZu1/2/3。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="current">电流通信</Badge>
              <CardTitle className="pt-2 text-base">JiaoL</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              异步监听从站 1。缓存 jiaoLValue 和单位。主界面问一次就关口。
            </CardContent>
          </Card>
          <Card>
            <CardHeader>
              <Badge variant="db">数据访问</Badge>
              <CardTitle className="pt-2 text-base">ExecuteCommon</CardTitle>
            </CardHeader>
            <CardContent className="text-sm leading-7 text-slate-600">
              两份文件内容相同。参数读写 + 曲线批插 + 通用
              queryDatas/updateDatas。main 方法只是早期查 t_userinfo 的调试入口。
            </CardContent>
          </Card>
        </div>
        <Sub title="主界面还依赖、但不在这批文件里的类">
          <p>
            写报告时要承认系统不只有这 5 个文件。从 import
            可以列出周边模块：
          </p>
          <KvTable
            rows={[
              { k: "RRuANDWone / RRu", v: "COM3 上的 PLC 读写。" },
              { k: "JdbcDeal / DBConnection", v: "两套数据库连接。" },
              { k: "Result", v: "装着 X1、X2 两个整数的简单结果对象。" },
              { k: "IndexFrame", v: "主窗体，退出时 showCard(\"Home\")。" },
              {
                k: "DianZu001/002/003、DDianZuOnce",
                v: "更早的单路电阻类，本面板已改用 DianZu00000 一次读三路。",
              },
              { k: "Sleep / PDialog / TemporaryData", v: "延时、进度框、临时缓存。" },
            ]}
          />
        </Sub>
        <Sub title="界面上其余按钮在做什么">
          <KvTable
            rows={[
              {
                k: "开始测试 / 停止测试",
                v: "正式流程。开线程、读 PLC、采电阻电流、保护停机。",
              },
              {
                k: "保存",
                v: "按新编号/旧编号决定全量或追加，批插入 test_results。",
              },
              {
                k: "退出",
                v: "resetForNextEnter() 停任务、关串口、清曲线和标志，再回到 Home。",
              },
              {
                k: "清除（已布局未放置）",
                v: "清空四个显示框和曲线。按钮创建了，placeButton 被注释。",
              },
              {
                k: "充值/模拟测试 btn4",
                v: "用 Timer(1ms) 刷随机电阻，曾用于不接仪表时看界面。未放到当前布局。",
              },
            ]}
          />
        </Sub>
      </Section>

      <Section
        id="copy"
        kicker="可粘贴"
        title="课题报告里可以直接改写的表述"
      >
        <Sub title="1. 研究背景与系统目标（建议放第一章/第二章）">
          <p>
            接点接触电阻是衡量密封电器、继电器触点质量的重要参数。触点在多次通断后会氧化、磨损，接触电阻逐渐升高，严重时导致发热甚至失效。传统做法是试验结束后用微欧计抽测，无法看到电阻随动作次数变化的全过程，也不容易在超限时立即停机。
          </p>
          <p>
            本课题设计并实现了一套接点接触电阻在线监测软件。系统以工控计算机为上位机，通过
            RS-485/串口 Modbus-RTU 采集三路接触电阻和一路回路电流，同步监测左右凸轮到位信号，实时绘制“动作次数—电阻/电流”曲线，并在电阻超限、电流超限、凸轮卡滞或达到设定寿命次数时自动停机并记录故障，从而实现试验过程的可监测、可追溯、可保护。
          </p>
        </Sub>
        <Sub title="2. 总体设计（建议放第三章）">
          <p>
            软件采用 Java 语言开发，界面基于 Swing 与 FlatLaf，曲线采用
            JFreeChart 双 Y
            轴折线图，串口通信采用 jSerialComm，数据持久化采用 JDBC。系统在逻辑上分为人机交互层、设备通信层和数据访问层。
          </p>
          <p>
            由于 PLC、三块电阻表和电流表共用同一串口资源，通信层采用时分复用策略：平时由上位机循环读取
            PLC 输入寄存器，获取左右凸轮开关量；当右侧信号有效时释放串口并轮询三路电阻；当左侧信号有效时读取电流。采集结果写入界面文本框，由文档监听器驱动曲线刷新，从而把“设备节拍”和“数据可视化”解耦。
          </p>
        </Sub>
        <Sub title="3. 关键技术实现（建议放第四章）">
          <p>
            <strong>（1）多仪表 Modbus 采集。</strong>{" "}
            电阻表从站地址为 2、3、4，电流表从站地址为 1，统一使用功能码
            03H 读取 3
            个寄存器。应答帧中包含原始值、小数位数和单位代码，上位机按{" "}
            <code>实际值 = 原始值 / 10^小数位</code>{" "}
            还原工程量，并完成 CRC-16 校验。
          </p>
          <p>
            <strong>（2）基于凸轮节拍的测试状态机。</strong>{" "}
            以 PLC 输入位 X1、X2 作为同步信号，结合 AtomicBoolean
            做上升沿闭锁，保证每一拍只采集一次电阻、一次电流，避免在信号保持为
            1 时重复读表。
          </p>
          <p>
            <strong>（3）动作速度测算。</strong>{" "}
            记录相邻两次 X1 有效的时间间隔 Δt，按 v = 120/Δt
            计算每分钟动作次数，并滤除小于 2 秒的抖动间隔。
          </p>
          <p>
            <strong>（4）安全保护。</strong>{" "}
            电阻阈值和最大次数保存在数据表 d_dianzumax
            中，可在不停机程序的情况下调整。电流阈值按 2.1A
            判定。凸轮信号持续无效超过 12
            秒视为机械故障。上述条件触发后，上位机向 PLC
            写入停机和报警线圈，并向 policetime 表写入次数、故障类型和时间。
          </p>
          <p>
            <strong>（5）曲线存储与回放。</strong>{" "}
            每个采样点作为 test_results
            中的一行，包含试验日期、编号、次数以及四条曲线的坐标。保存时开启 JDBC
            事务做批量插入。选择已有编号可按 X
            轴升序回放历史曲线，并允许在历史曲线之后继续试验、只追加新点。
          </p>
        </Sub>
        <Sub title="4. 系统功能总结（建议放结论）">
          <p>
            本软件实现了接点接触电阻试验的在线采集、实时显示、超限保护、故障记录和历史追溯。经源码分析，系统已形成“PLC
            节拍采集—多路仪表解析—双轴曲线监控—数据库存储”的完整闭环。后续可在统一串口调度、单位换算到同一量纲、连接池管理等方面继续完善。
          </p>
        </Sub>
        <Callout title="答辩时如果被问“核心创新/核心工作是什么”" tone="idea">
          不要只回答“用 Java 画了折线图”。更准确的说法是：在单串口、多从站条件下，用凸轮开关量做节拍，把三路微欧电阻和一路电流对齐到同一次动作上，再按寿命次数画曲线，并在电阻、电流、机构、次数四个维度上实现自动停机。这才是这套程序真正解决的工程问题。
        </Callout>
      </Section>
    </>
  );
}
