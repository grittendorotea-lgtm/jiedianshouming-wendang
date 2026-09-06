import { Callout, CodeBlock, ExtBox, Flow, KvTable, Section, Sub } from "../blocks";

export function PartThree() {
  return (
    <>
      <Section
        id="protect"
        kicker="第九节"
        title="保护停机机制"
      >
        <p>
          试验台会长时间连续动作，软件必须能自己停下来，否则触点烧蚀或机械卡死会损坏设备。实现上分成“数值保护”和“机构保护”两类。
        </p>
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
          <ExtBox
            title="F7 拓展：四套电阻阈值，只停本工位"
            tech="getDianzumaxValue1～4、分线圈停机、状态灯 N、故障带设备号入库"
          >
            拓展后每个工位只比较本工位那一路电阻：设备 1 用{" "}
            <code>getResistanceValue(5)</code> 对{" "}
            <code>getDianzumaxValue1()</code>，设备 2/3/4 分别对阈值 2/3/4。超限后写本工位运行寄存器为
            1，再写公共报警寄存器 5，并把该工位状态灯改成红色{" "}
            <code>N</code>。邻站的 <code>isTestingStarted</code>{" "}
            保持不变。难点是四套循环同时越过阈值时，停机写口必须交错延时，否则寄存器 5
            的置位/复位会互相覆盖。
          </ExtBox>
        </Sub>
        <Sub title="2）电流超限">
          <p>
            电流上限写死为 <code>2.1</code>{" "}
            安培。超过后同样停机、报警、写{" "}
            <code>policetime</code>（“电流值故障”），并结束外循环。另有一个未在主流程里调用的{" "}
            <code>saveToPolicetimeTabledianliu()</code>
            ，会把具体安培数写进故障描述，说明后来补过更细的电流报警格式。
          </p>
          <ExtBox
            title="F8 拓展：四路电流各自与 2.1 A 比较"
            tech="getCurrentValue(1～4)、通道隔离、缓存一致性"
          >
            电流上限仍写死为 2.1 A，但比较对象改成四个缓存通道。设备 1
            取地址 01，设备 2/3/4 取 02/03/04。超限同样只停本工位、写寄存器 5
            报警，并调用对应的{" "}
            <code>saveToPolicetimeTabledianliu()</code>{" "}
            系列方法，描述写成“电流值故障：x A”。难点是后台轮询约
            800ms 才刷新一轮：保护判断读到的可能是上一拍缓存，四个工位必须各自持有一份{" "}
            <code>dianliuNValueN</code>，绝不能共用一个电流变量。
          </ExtBox>
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
          <ExtBox
            title="F9 拓展：四工位独立 12 s 凸轮超时"
            tech="8 路输入字、四套 lastTime、saveToPolicetimeTableStop(shebeihao)"
          >
            一次读取 8 个输入字后，工位 1 用第 1/5 字、工位 2 用第 2/6
            字，依此类推。某一侧持续无效超过 12 秒，只停该工位：状态灯改{" "}
            <code>N</code>，写寄存器 5 和本工位运行位，弹“设备 i
            凸轮转动故障”，再{" "}
            <code>saveToPolicetimeTableStop(i, countTest)</code>{" "}
            把 <code>Shebeihao</code> 写成 1～4。四套{" "}
            <code>lastTime</code> 绝不能共用，否则邻站一次边沿会把本站超时计时清掉。
          </ExtBox>
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
          <ExtBox
            title="F10 拓展：四套次数上限独立截止"
            tech="timeSeconds1～4、getTestmaxValue1～4、横坐标与累计次数分离"
          >
            每个页签有自己的 <code>timeSeconds</code> 和{" "}
            <code>getTestmaxValue1/2/3/4()</code>
            。曲线每加一个点仍把该工位横坐标加 2。先到上限的工位先停，其余工位继续做寿命试验。累计次数则按工位调用{" "}
            <code>getAllcountValue1(1～4)</code> /{" "}
            <code>updateAllcountValues(text, shebeihao)</code>
            ，与单次试验上限分开存放。
          </ExtBox>
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
          <ExtBox
            title="F15 拓展：故障台账增加设备号"
            tech="INSERT … Shebeihao、电阻/电流/凸轮三类分方法、公共报警寄存器 5"
          >
            <code>policetime</code> 插入语句改为四列：次数、故障原文、时间、{" "}
            <code>Shebeihao</code>
            。电阻故障、电流故障各有四套方法，凸轮故障走统一的{" "}
            <code>saveToPolicetimeTableStop(shebeihao, count)</code>
            。事后可以按设备号查出是哪一台、哪一类保护停的机。
          </ExtBox>
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
        <ExtBox
          title="F11 拓展：四个页签、四张独立双轴图"
          tech="JTabbedPane、四套 XYSeries、四套 DocumentListener、EDT 批量加点"
        >
          拓展后每个工位只绑一路电阻和一路电流，所以每张图是两条线，不再是三红绿蓝加一条电流。页签标题为“监测设备
          1～4”。工位 1 监听 <code>dianzu111 / dianliu111</code>，两框都非空才往{" "}
          <code>series1</code> 和 <code>rightSeries1</code>{" "}
          加点。切换页签只改变可见性，后台四路监听器仍继续工作。难点是四套数据集同时{" "}
          <code>invokeLater</code> 重绘时不能堵塞 EDT，也要避免把设备 2
          的点加进设备 1 的系列。
        </ExtBox>
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
          <ExtBox
            title="F12 拓展：按工位批插，SwingWorker 避免卡死"
            tech="SwingWorker、LoadingGifDialog、行末设备号、按工位查重"
          >
            每个页签各有保存按钮。校验通过后弹出{" "}
            <code>LoadingGifDialog</code>，在{" "}
            <code>SwingWorker.doInBackground()</code>{" "}
            里组批：一行写成日期、编号、次数、电阻
            X/Y、电流 X/Y，最后一列是设备号 1～4。查重改走{" "}
            <code>isProductNumberExists06shebeihao01～04</code>
            ，同一编号在不同工位可以并存，同一工位重复编号则提示“继电器编号已存在”。保存前还按工位{" "}
            <code>updateAllcountValues(..., shebeihao)</code>
            。四工位同时保存时，事务必须在后台跑，否则 EDT 会被 JDBC 堵住。
          </ExtBox>
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
          <ExtBox
            title="F13 拓展：编号与曲线按设备号隔离"
            tech="四套 testBianHao / XYSeries、按 Shebeihao 查重、避免串台回放"
          >
            四个页签各自持有编号框和曲线对象。历史批次靠行末设备号区分，回放或继续试验时只动本页签的{" "}
            <code>series / rightSeries / timeSeconds</code>
            。若查询不带设备号，设备 2 的点会画进设备 1
            的图。本拓展快照把查重下沉到工位级方法，重点从“同一编号追加”转为“按台账隔离批次”。
          </ExtBox>
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
                    电阻停机阈值、单次试验最大次数。基础版固定读
                    id=1；拓展后按工位读{" "}
                    <code>getDianzumaxValue1～4</code> /{" "}
                    <code>getTestmaxValue1～4</code>。
                  </td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">allcount</td>
                  <td className="px-3 py-2">id, allcount</td>
                  <td className="px-3 py-2">
                    设备累计动作次数。拓展后{" "}
                    <code>getAllcountValue1(1～4)</code> /{" "}
                    <code>updateAllcountValues(text, shebeihao)</code>{" "}
                    分台维护。
                  </td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">test_results</td>
                  <td className="px-3 py-2">
                    test_time, test_bianhao, count_test, 电阻/电流 x/y，行末设备号
                  </td>
                  <td className="px-3 py-2">
                    一条记录 = 一个采样点。拓展后按编号+设备号拼回本工位曲线。
                  </td>
                </tr>
                <tr className="border-t">
                  <td className="px-3 py-2 font-medium">policetime</td>
                  <td className="px-3 py-2">
                    Cishu, Tingzhizhi, Tingtime, Shebeihao
                  </td>
                  <td className="px-3 py-2">
                    故障停机台账。拓展后用设备号区分哪一台停机。
                  </td>
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
          <ExtBox
            title="F14 拓展：四套参数与累计次数"
            tech="getDianzumaxValue1～4、getTestmaxValue1～4、getAllcountValue1(shebeihao)"
          >
            启动和点“开始检测”时，四个页签分别加载本工位电阻上限、次数上限和累计次数。界面顶部也会分别显示四套当前最大值。这批源码里的{" "}
            <code>ExecuteCommon.java</code>{" "}
            仍是单记录接口；四工位面板已经按分设备方法调用，说明数据访问层需要同步扩展，否则四套阈值会读成同一行。
          </ExtBox>
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
