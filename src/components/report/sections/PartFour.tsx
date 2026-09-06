import { Badge } from "@/components/ui/badge";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Callout, CodeBlock, KvTable, Section, Sub } from "../blocks";

export function PartFour() {
  return (
    <>
      <Section id="protocol" kicker="第十三节" title="通信协议设计">
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
            该算法为标准 Modbus CRC-16（多项式 0x8005 的反射形式 0xA001）。请求对前 6
            字节计算，应答对除 CRC 外的整帧计算。校验失败的数据不进入曲线。
          </p>
        </Sub>
        <Sub title="一拍访问顺序与请求帧">
          <p>
            按源码参数与同一 CRC 算法，一拍内的请求帧如下：PLC 循环读取{" "}
            <code>08 04 00 00 00 02 71 52</code>；X1 有效后依次访问电阻从站 2/3/4，例如{" "}
            <code>02 03 00 01 00 03 54 38</code>；交还 PLC 后再于 X2 有效时访问电流从站{" "}
            <code>01 03 00 01 00 03 54 0B</code>。PLC
            应答功能码须为 04H，否则延时 10ms 重试。
          </p>
        </Sub>
      </Section>

      <Section id="classes" kicker="第十四节" title="软件结构">
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
        <Sub title="线程模型">
          <p>
            界面更新位于 Swing EDT。正式采集提交至 3 线程池中的单一循环任务，停止时通过
            Future 取消。电流监听运行于 jSerialComm 回调线程，主界面以约 200ms
            延时读取缓存。串口互斥依赖关闭后再打开的调用顺序，尚未抽象为独立调度器。
          </p>
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
        id="conclusion"
        kicker="第十五节"
        title="总结与展望"
      >
        <p>
          本系统实现了接点接触电阻试验的在线采集、实时监测、保护停机、故障记录与历史回放，形成“节拍同步采集—多仪表解析—双坐标曲线—数据库存储”的闭环。其主要工作在于：在单串口、多从站条件下，以凸轮开关量为同步信号，将三路接触电阻与一路电流对齐至同一动作周期，并在电阻、电流、机构状态和试验次数四个维度实施自动停机。
        </p>
        <p>
          现有实现仍存在不足：串口访问依赖调用顺序而非统一调度；数据访问并存
          <code> DBConnection </code> 与 <code> JdbcDeal </code>{" "}
          两套入口；三路电阻单位共用同一字段；电流阈值 2.1A
          硬编码；界面采用固定分辨率绝对布局。后续可将串口访问收敛为调度组件，将全部保护阈值参数化入库，并将曲线存储拆分为试验主表与采样点表，以提高可维护性与查询效率。
        </p>
        <Callout title="核心工作表述" tone="idea">
          系统解决的工程问题是节拍对齐与安全停机，而不是单独完成折线绘图。答辩与结论中建议强调：单总线时分复用、凸轮同步采集，以及电阻、电流、机构、次数四类保护。
        </Callout>
      </Section>
    </>
  );
}
