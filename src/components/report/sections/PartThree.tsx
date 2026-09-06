import { Callout, CodeBlock, Flow, KvTable, Section, Sub } from "../blocks";
import {
  ArrowDown,
  ChartNode,
  Decision,
  Figure,
  Split,
  VChart,
} from "../flowchart";

export function PartThree() {
  return (
    <>
      <Section
        id="protect"
        kicker="第九节"
        title="保护停机机制"
      >
        <p>
          试验台会长时间连续动作，软件必须能自己停下来，否则触点烧蚀或机械卡死会损坏设备。实现上分成“数值保护”和“机构保护”两类。电阻上限来自数据库，电流上限写死为
          2.1 A，凸轮超时 12 秒，次数上限来自 <code>testcount</code>
          。任一类成立后都向 PLC 写停机和报警，并向 <code>policetime</code>{" "}
          记一条故障。
        </p>
        <Figure no="9-1" title="四类保护停机判定">
          <VChart>
            <ChartNode kind="start">本拍采集结束</ChartNode>
            <ArrowDown />
            <Decision>电阻 &gt; dianzumax？</Decision>
            <Split
              left={<ChartNode kind="end">停机，记电阻值故障</ChartNode>}
              right={<ChartNode>继续</ChartNode>}
            />
            <ArrowDown />
            <Decision>电流 &gt; 2.1 A？</Decision>
            <Split
              left={<ChartNode kind="end">停机，记电流值故障</ChartNode>}
              right={<ChartNode>继续</ChartNode>}
            />
            <ArrowDown />
            <Decision>凸轮灭灯 &gt; 12 s？</Decision>
            <Split
              left={<ChartNode kind="end">停机，记凸轮转动故障</ChartNode>}
              right={<ChartNode>继续</ChartNode>}
            />
            <ArrowDown />
            <Decision>次数 ≥ testcount？</Decision>
            <Split
              left={<ChartNode kind="end">停机，提示次数已到</ChartNode>}
              right={<ChartNode>进入下一拍</ChartNode>}
            />
          </VChart>
        </Figure>
        <Sub title="1）接触电阻超限">
          <p>
            上限不写死在代码里，而是{" "}
            <code>ExecuteCommon.getDianzumaxValue()</code> 从{" "}
            <code>d_dianzumax</code> 读。界面顶部也会显示“当前最大值为 xx
            mΩ”。外循环比较的是去掉单位后的 <code>pureValue1/2/3</code>。
          </p>
          <p>超限后连续做四件事：</p>
          <Flow
            steps={[
              "打开 PLC 串口，寄存器1写1停机，寄存器2写1报警。",
              "saveToPolicetimeTable(\"电阻值故障\") 记一条故障。",
              "拼出哪一路不合格、具体读数，JOptionPane 弹窗。",
              "continueLoop2 = false，整次试验结束。",
            ]}
          />
        </Sub>
        <Sub title="2）电流超限">
          <p>
            电流上限写死为 <code>2.1</code>{" "}
            安培。超过后同样停机、报警、写{" "}
            <code>policetime</code>（“电流值故障”），并结束外循环。另有一个未在主流程里调用的{" "}
            <code>saveToPolicetimeTabledianliu()</code>
            ，会把具体安培数写进故障描述，说明后来补过更细的电流报警格式。
          </p>
        </Sub>
        <Sub title="3）左右凸轮超时">
          <p>
            用“灭灯持续时间”判断机构是否还在转。X1 为 0 时，若{" "}
            <code>(now - lastTime111000) / 1000</code> 落在 (12, 10000)
            秒，报右侧凸轮故障；X2 为 0 时用{" "}
            <code>lastTime555111</code> 判断左侧。12
            秒是现场经验阈值：正常节拍远小于 12 秒，卡住才会一直灭灯。上限
            10000 是为了避开计时器未初始化时的巨大时间差。
          </p>
        </Sub>
        <Sub title="4）次数到达上限">
          <p>
            最大次数来自 <code>d_dianzumax.testcount</code>（id=1），方法是{" "}
            <code>getTestmaxValue1()</code>。曲线每加一个点，{" "}
            <code>timeSeconds</code> 加 2，<code>countTest</code>{" "}
            文本框同步显示当前次数。当{" "}
            <code>timeSeconds &gt;= countmax111</code>{" "}
            时停机报警，并提示“已达到设定测试次数”。
          </p>
        </Sub>
        <Sub title="报警记录怎么入库">
          <p>
            <code>saveToPolicetimeTable(String tingzhizhi)</code> 向{" "}
            <code>policetime</code> 插入三列：
          </p>
          <KvTable
            rows={[
              {
                k: "Cishu",
                v: "优先取监测次数框里的数字；没有就用 timeSeconds；再没有就记 1。",
              },
              {
                k: "Tingzhizhi",
                v: "故障原文，例如“设备右侧凸轮转动故障”“电阻值故障”。",
              },
              { k: "Tingtime", v: "当前系统时间戳 Timestamp。" },
            ]}
          />
          <p>
            用的是 <code>JdbcDeal.getConnection()</code>，和曲线保存走同一套连接工具。
          </p>
        </Sub>
      </Section>

      <Section
        id="chart"
        kicker="第十节"
        title="实时曲线监测"
      >
        <p>
          图表标题“接点接触电阻参数曲线”。左侧 Y 轴是电阻（mΩ），三条线：电阻1红、电阻2绿、电阻3蓝。右侧
          Y 轴是电流（A），黄褐色。X 轴是“次数”，不是时钟秒，虽然变量名叫{" "}
          <code>timeSeconds</code>。
        </p>
        <Sub title="1）数据集怎么挂到两根轴上">
          <p>
            <code>ChartFactory.createXYLineChart</code>{" "}
            先用三路电阻数据集建图。然后新建{" "}
            <code>NumberAxis(&quot;电流(A)&quot;)</code> 作为 range axis
            1，把 <code>rightDataset</code>{" "}
            映射到这根轴。渲染器线宽 1.5，不画数据点，避免点数很多时卡顿。
          </p>
        </Sub>
        <Sub title="2）文本框监听：四个值齐了才加点">
          <p>
            <code>startListening()</code> 给{" "}
            <code>dianzu111/222/333/444</code> 加同一个{" "}
            <code>DocumentListener</code>。只处理{" "}
            <code>insertUpdate</code>。四个框都非空时：
          </p>
          <Flow
            steps={[
              "extractNumber() 分别得到四路纯数值。",
              "用当前最大值抬高左侧 Y 轴上限 lockYAxisRange()。",
              "在同一个 timeSeconds 上给 series1/2/3 和 rightSeries 各加一个点。",
              "timeSeconds = timeSeconds + 2。",
              "updateXAxis()：窗口宽度固定 28 次，并回写监测次数框。",
            ]}
          />
          <p>
            加点前有一段随机微扰判断，目的是避免完全相同的重复插入把曲线点叠死。隐藏框{" "}
            <code>dianzu444NO</code> 专门缓存电流，正式显示框{" "}
            <code>dianzu444</code> 在读完电阻时再抄过来，保证四个框几乎同时变化，监听器能一次收齐。
          </p>
        </Sub>
        <Sub title="3）鼠标、滚轮、滚动条、复选框">
          <KvTable
            rows={[
              {
                k: "鼠标移动",
                v: "ChartMouseListener 把屏幕坐标换成 X 值，画一条黑色竖线，并在最近数据点上显示四路读数。",
              },
              {
                k: "最近邻查找",
                v: "findClosestDataItem() 按 X 距离扫描；数据有序，距离开始变大就停止。",
              },
              {
                k: "滚轮",
                v: "只改 X 轴下限，上限不动。视图宽时按 10% 缩放，已经很窄时按 1%。最少保持约 28 次窗口，下限不为负。",
              },
              {
                k: "水平滚动条",
                v: "一屏 28 个点。拖动后 X 轴显示 [value, value+28]。",
              },
              {
                k: "垂直滚动条",
                v: "上限从 2000 往下收。左右两条 Y 轴一起改，避免电阻和电流对不齐。",
              },
              {
                k: "四个复选框",
                v: "调用 renderer.setSeriesLinesVisible，单独关掉某一路，方便只看异常通道。",
              },
            ]}
          />
        </Sub>
      </Section>

      <Section
        id="save"
        kicker="第十一节"
        title="数据存储与回放"
      >
        <p>
          每个采样点对应 <code>test_results</code>{" "}
          中一行，包含日期、编号、次数和四条曲线的坐标。保存使用 JDBC
          批处理事务：全部成功则提交，失败则回滚。新编号从第 0
          点全量写入；已回显的旧编号只从 <code>loadedPointCount</code>{" "}
          起追加，从而实现同一试品的分段试验。
        </p>
        <Figure no="11-1" title="试验数据保存与回放">
          <VChart>
            <ChartNode kind="start">点“保存”</ChartNode>
            <ArrowDown />
            <Decision>编号、次数、曲线是否齐全？</Decision>
            <Split
              left={<ChartNode>继续</ChartNode>}
              right={<ChartNode kind="end">提示后返回</ChartNode>}
            />
            <ArrowDown />
            <Decision>编号已存在且等于回显编号？</Decision>
            <Split
              left={<ChartNode>从 loadedPointCount 追加</ChartNode>}
              right={<ChartNode>从第 0 点全量写入</ChartNode>}
            />
            <ArrowDown />
            <ChartNode kind="io">saveTestResultsBatch 事务提交</ChartNode>
            <ArrowDown />
            <ChartNode>累计次数增加新增点数×2</ChartNode>
            <ArrowDown />
            <ChartNode kind="end">可选：按编号回放历史曲线</ChartNode>
          </VChart>
        </Figure>
        <Sub title="1）保存前的校验">
          <p>点“保存”后先取日期、编号、监测次数：</p>
          <ul className="list-disc space-y-1 pl-5">
            <li>编号空：提示“请填写测试编号！”</li>
            <li>次数空：提示“请测试后再保存！”</li>
            <li>四条曲线点数为 0：提示没有曲线数据</li>
          </ul>
        </Sub>
        <Sub title="2）新编号全量保存，旧编号只存新增点">
          <p>
            这是保存逻辑里最用心的一段。先{" "}
            <code>isTestBianHaoExists()</code> 查{" "}
            <code>test_results</code> 里有没有这个编号。
          </p>
          <p>
            只有“编号已存在，并且等于当前回显编号{" "}
            <code>loadedTestBianHao</code>”才进入追加模式，从{" "}
            <code>loadedPointCount</code> 开始写；否则从第 0
            个点整段保存。这样手动输入一个新编号时，不会把历史曲线清掉，也不会把旧点再插一遍。
          </p>
          <CodeBlock title="每个点写成一行">{`dataToInsert.add(new Object[]{
    sqlTestTimeValue, testBianHaoValue, countTestText,
    series1X, series1Y, series2X, series2Y,
    series3X, series3Y, series4X, series4Y
});
int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);`}</CodeBlock>
          <p>
            也就是：<strong>曲线上的一个采样点 = 数据库里的一条记录</strong>
            ，四个系列的 X/Y 并排存放，而不是每种曲线一张表。
          </p>
        </Sub>
        <Sub title="3）批处理怎么保证要么全成功要么全失败">
          <p>
            <code>saveTestResultsBatch()</code> 用{" "}
            <code>JdbcDeal.getConnection()</code>，关掉 autoCommit，循环{" "}
            <code>addBatch()</code>，再 <code>executeBatch()</code> +{" "}
            <code>commit()</code>。异常就 rollback。count_test
            兼容 String 和 Integer，因为界面次数框给的是字符串。
          </p>
          <p>
            另有一个三系列版本 <code>saveTestResults()</code>
            ，只写 series1~3，是早期接口，主界面已经改用四系列批插。
          </p>
        </Sub>
        <Sub title="4）保存成功后更新累计次数">
          <p>
            <code>addAllCountBySaveCount(dataToInsert.size() * 2)</code>
            。乘 2 和 X 轴步进 2、速度公式里的 120
            一致：界面上的“一次采样”按两次动作计数。新累计值写回{" "}
            <code>allcount</code> 表 id=1，并刷新右上角“累计次数”。
          </p>
        </Sub>
        <Sub title="5）按编号回显历史曲线">
          <p>
            编号框是可编辑下拉框。下拉数据来自{" "}
            <code>getComboBoxDataDanLie(&quot;test_results&quot;, &quot;test_bianhao&quot;)</code>
            。选中已有编号时：
          </p>
          <Flow
            steps={[
              "新编号（库里没有）直接返回，绝不清空正在画的曲线。",
              "旧编号先清空四条 XYSeries。",
              "按 series1_x 升序查出该编号全部点。",
              "暂时关闭 series.notify，批量 add，避免每加一个点就重绘。",
              "loadedPointCount = 点数，timeSeconds = 最大 X + 1，下次接着画。",
              "回填测试日期和监测次数，并调整坐标轴。",
            ]}
          />
          <p>
            <code>loadingHistory</code>{" "}
            标志很重要：刷新下拉框或回填编号时会再触发 ActionListener，有了这个标志就不会递归加载、把曲线清掉。
          </p>
        </Sub>
      </Section>

      <Section
        id="db"
        kicker="第十二节"
        title="数据库设计"
      >
        <p>
          两份 ExecuteCommon 源码一致，都是{" "}
          <code>com.ytzg.sealer.db.ExecuteCommon</code>
          。它既有专用方法，也有通用 JDBC 封装。
        </p>
        <Sub title="从 SQL 还原出的四张表">
          <div className="overflow-x-auto rounded-lg border border-border bg-white">
            <table className="w-full text-left text-sm">
              <thead className="bg-slate-50 text-slate-600">
                <tr>
                  <th className="px-3 py-2">表</th>
                  <th className="px-3 py-2">字段</th>
                  <th className="px-3 py-2">用途</th>
                </tr>
              </thead>
              <tbody className="text-slate-700">
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">d_dianzumax</td>
                  <td className="px-3 py-2">
                    id, dianzumax, testcount
                  </td>
                  <td className="px-3 py-2">
                    电阻停机阈值、单次试验最大次数。程序固定读 id=1。
                  </td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">allcount</td>
                  <td className="px-3 py-2">id, allcount</td>
                  <td className="px-3 py-2">
                    设备累计动作次数。保存成功后累加。
                  </td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">test_results</td>
                  <td className="px-3 py-2">
                    test_time, test_bianhao, count_test, series1~4 的 x/y
                  </td>
                  <td className="px-3 py-2">
                    一条记录 = 一个采样点。按编号把整条曲线拼回来。
                  </td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">policetime</td>
                  <td className="px-3 py-2">
                    Cishu, Tingzhizhi, Tingtime
                  </td>
                  <td className="px-3 py-2">故障停机台账，供事后追溯。</td>
                </tr>
              </tbody>
            </table>
          </div>
        </Sub>
        <Sub title="专用方法和通用方法">
          <KvTable
            rows={[
              {
                k: "get/update Dianzumax",
                v: "读/写电阻上限。界面启动和刷新时调用。",
              },
              {
                k: "getTestmaxValue1",
                v: "读 testcount，作为本次试验允许的最大次数。",
              },
              {
                k: "get/update allcount",
                v: "累计次数。保存曲线后按本次新增点数×2 增加。",
              },
              {
                k: "updateDatas / queryDatas",
                v: "通用增删改查。参数用 List<Object> 绑定到 ?，查询结果转 List<Map>。",
              },
              {
                k: "异常处理",
                v: "专用方法失败会 JOptionPane 弹“更新数据库失败”，并 printStackTrace。",
              },
            ]}
          />
        </Sub>
        <Callout title="连接工具不统一" tone="warn">
          阈值查询走 <code>DBConnection.getConnection()</code>
          ，曲线批插走 <code>JdbcDeal.getConnection()</code>
          。功能上都能用，报告里可以写成“数据访问存在两套连接入口，后续可统一到一个连接池”。
        </Callout>
      </Section>
    </>
  );
}
