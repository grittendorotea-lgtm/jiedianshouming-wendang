package com.ytzg.sealer.swing;

import com.formdev.flatlaf.FlatLightLaf;

import com.ytzg.sealer.entity.Result;
import com.ytzg.sealer.plc.PDialog.PDialog;
import com.ytzg.sealer.comm.Sleep;
import com.ytzg.sealer.db.ExecuteCommon;
import com.ytzg.sealer.db.JdbcDeal;
import com.ytzg.sealer.entity.TemporaryData;
import com.ytzg.sealer.plc.*;
import com.ytzg.sealer.plc.test.DianZu00000;
import com.ytzg.sealer.util.DateLabelFormatter;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.UtilDateModel;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.*;
import java.text.DecimalFormat;
import java.util.*;

import org.jfree.chart.ChartMouseListener;
import org.jfree.ui.RectangleEdge;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class ZzhejiPanel extends JPanel {

    //=======================
    // 用于标记是否处理过保存
    private boolean isDataUnsaved = false;

    private String loadedTestBianHao = "";

    private RRuANDWone ruAndwone    =   new RRuANDWone("COM3");   ;

    private   int  allcount111   = 0;

    private  long   roundedFrequency111 ;
    //=======================

    private IndexFrame parentFrame;
    private   String newXValue = "1";
    private JScrollBar horizontalScrollBar;
    private JScrollBar verticalScrollBar;




    private   long  lastTime111111 = 0;
    private   long  lastTime111000= 0;
    private   long  lastTime555111= 0;


    AtomicBoolean processed2 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processedjishi1 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed111000 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed555111 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1


    private double pureValue1; // 提前初始化
    private double pureValue2; // 提前初始化
    private double pureValue3; // 提前初始化

    private double dianliu1Value1; // 提前初始化


    private String averageInMOhm1; // 提前初始化
    private String averageInMOhm2; // 提前初始化
    private String averageInMOhm3; // 提前初始化


    private volatile long lastTriggerTime = 0; // 使用 volatile 保证可见性

    private  long   roundedFrequency ;

    private   long  lastTime   = 0;

    private final DecimalFormat frequencyFormat = new DecimalFormat("0.00");

    private XYPlot plot;

    private int timeSeconds = 0; // 时间秒作为X轴

    private JButton btn1,beginCLBtn,btn3,btn4,btn5,btn6,btn7  ,btn8 ,btnclear;
    private JTextField  dianzu111,dianzu222,dianzu333  ,dianzu444  ,dianzu444NO;


    private TemporaryData tempData = new TemporaryData(); // 类成员变量

    private Timer timer;
    private int count = 0;

    private   JLabel labeldianzumax;
    private double dianzumax;


    private JFreeChart chart;
    private XYSeries series1, series2, series3 ,rightSeries; // 创建三个 XYSeries


    private  GradientPanel gradientPanel;

    private double maxYValue = 0;

    private    double value111;
    private   double value222;
    private  double value333, value444;


    private  boolean isTestingStarted = false; // 类变量，跟踪开始测试是否已开始
    private PDialog pD;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    private Future<?> currentTestFuture = null;  /*保存当前提交的任务的 Future 对象。Future 对象可以用来检查任务是否完成、取消任务以及获取任务的执行结果*/

    private RRu rRu  ;

    private DDianZuOnce ddianzuonce = new DDianZuOnce();


    private DianZu00000 dianZu = new DianZu00000();

    private DianZu001 ddianzuonce1 = new DianZu001();
    private DianZu002 ddianzuonce2 = new DianZu002();
    private DianZu003 ddianzuonce3 = new DianZu003();
    private JiaoL JiaoL = new JiaoL();


    private    JTextField    countTest ,suduTest  ,allcountTest        ;
    private JDatePickerImpl testTime;
    private JComboBox<String> testBianHao;

    private int loadedPointCount = 0;  //加两个变量，用来记录“回显了多少历史点”，保存时避免重复保存：
    private boolean loadingHistory = false;


    private Timer testTimer;
    private Random random = new Random();

    private   boolean isTimerRunning = false;



    private  String formattedFrequenc ;

    // 添加变量来保存当前的输入框值
    private String currentValue111 = "";
    private String currentValue222 = "";
    private String currentValue333 = "";
    private String currentValue444 = "";


    //使用并行读数据
    private ExecutorService executorServicebingxing = Executors.newFixedThreadPool(3); // 三个线程的线程池


    AtomicBoolean processed = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1



    public ZzhejiPanel(IndexFrame parentFrame) {
        this.parentFrame = parentFrame;

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(null);
        setBounds(0, 0, 1620, 950);
        setVisible(false);

        rRu = new RRu("COM3");

        // 初始化 dianzumax 值
        dianzumax = ExecuteCommon.getDianzumaxValue();


        gradientPanel = new GradientPanel();
        gradientPanel.setLayout(null);
        gradientPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        add(gradientPanel);

        UIManager.put("OptionPane.messageFont", new Font("宋体", Font.BOLD, 24));
        UIManager.put("OptionPane.messageForeground",new Color(0, 0, 128));



        JSeparator separator2 = new JSeparator();
        separator2.setPreferredSize(new Dimension(0, 3));
        separator2.setBounds(0, 55, 1900, 3);
        gradientPanel.add(separator2);

        JSeparator separator3 = new JSeparator();

        separator3.setPreferredSize(new Dimension(0, 3));
        separator3.setBounds(0, 150, 1900, 3);
        gradientPanel.add(separator3);

        JSeparator separator4 = new JSeparator();
        separator4.setPreferredSize(new Dimension(0, 3));
        separator4.setBounds(0, 209, 1900, 3);
        gradientPanel.add(separator4);


        JSeparator separator7 = new JSeparator();
        separator7.setPreferredSize(new Dimension(0, 25));
        separator7.setBounds(0, 766, 1450, 5);
        separator7.setForeground(new Color(65, 59, 59, 216)); // 将分隔线的颜色设置为红色
        gradientPanel.add(separator7);

        Color labelColorDF = Color.BLACK;
        JLabel biaoti000 = new JLabel("<html>注意: 可设置使设备停止的电阻最大值，当前最大值为</html>");//<br>
        biaoti000.setBounds(20, 142, 600, 80);
        biaoti000.setFont( new Font("黑体", Font.BOLD, 15));
        biaoti000.setOpaque(false);  // 设置为透明背景
        biaoti000.setForeground(labelColorDF);
        gradientPanel.add(biaoti000);


        JLabel danwei = new JLabel("次/分钟");//<br>
        danwei.setBounds(676,163, 600, 40);
        danwei.setFont( new Font("黑体", Font.BOLD, 15));
        danwei.setOpaque(false);  // 设置为透明背景
        danwei.setForeground(labelColorDF);
        gradientPanel.add(danwei);


        // 从数据库中获取 dianzumax 值
        double dianzumax = ExecuteCommon.getDianzumaxValue();

        labeldianzumax = new JLabel(dianzumax+"mΩ");//<br>
        labeldianzumax.setBounds(413, 142, 600, 80);
        labeldianzumax.setFont( new Font("黑体", Font.BOLD, 22));
        labeldianzumax.setOpaque(false);  // 设置为透明背景
        labeldianzumax.setForeground( new Color(120, 31, 248));
        gradientPanel.add(labeldianzumax);


        Font labelFont = new Font("宋体", Font.BOLD, 18);
        Color labelColor = Color.RED;

        JLabel anzhaunglbel = new JLabel("测试编号:");

        JLabel shangclabel = new JLabel("测试日期:");
        JLabel  t444 = new JLabel("监测次数:");


        JLabel sudu = new JLabel("速度:");


        JLabel allcount = new JLabel("累计次数:");

        JLabel[] labels = { shangclabel,anzhaunglbel,t444,sudu ,allcount};
        for (JLabel label : labels) {
            label.setFont(labelFont);
            label.setForeground(labelColor);
        }

        anzhaunglbel.setBounds(20, 13, 200, 30);    gradientPanel.add(shangclabel);
        shangclabel.setBounds(305, 13, 200, 30);    gradientPanel.add(anzhaunglbel);
        t444.setBounds(600, 4, 206, 50);       gradientPanel.add(t444);
        sudu.setBounds(530, 157, 206, 50);       gradientPanel.add(sudu);

        allcount.setBounds(820, 4, 206, 50);       gradientPanel.add(allcount);



        // 初始化日期模型，并设置为当前日期
        UtilDateModel model = new UtilDateModel(new Date());
        Properties p = new Properties();
        Dimension preferredSize = new Dimension(300, 200);
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);  // 创建日期面板
        datePanel.setPreferredSize(preferredSize);
        JDatePickerImpl testTime = new JDatePickerImpl(datePanel, new DateLabelFormatter());// 创建日期选择器组件
        updateTextFieldFont(testTime, new Font("宋体", Font.BOLD, 24));// 设置 JFormattedTextField 的字体
        JFormattedTextField textFiel1 = testTime.getJFormattedTextField(); // 修改 JFormattedTextField 的背景颜色和边框
        textFiel1.setBackground(new Color(255, 255, 224));  // 这里设置背景色为面板的背景色
        testTime.setBackground(new Color(255, 255, 224));  // 这里设置背景色为面板的背景色  // 设置 JDatePickerImpl 的背景颜色和边框
        testTime.setBounds(402, 16, 180, 36);
        testTime.setOpaque(false);          //  重要重要重要重要重要重要重要重要重要重要重要重要重要重要重要重要重要
        gradientPanel.add(testTime);


        testBianHao = new JComboBox<>();
        testBianHao.setFont(new Font("楷体", Font.BOLD, 20));
        testBianHao.setBounds(110, 13, 180, 32);
        testBianHao.setBackground(new Color(255, 255, 224));
        testBianHao.setEditable(true);   // 允许新编号手动输入，也允许选择旧编号

        testBianHao.addItem("");

        String[] bianhaoArray = getComboBoxDataDanLie("test_results", "test_bianhao");
        for (String item : bianhaoArray) {
            testBianHao.addItem(item);
        }

        testBianHao.setSelectedIndex(0);
        gradientPanel.add(testBianHao);


        testBianHao.addActionListener(e -> {
            if (loadingHistory) {
                return;
            }

            Object selected = testBianHao.getSelectedItem();
            if (selected == null) {
                return;
            }

            String bianhao = selected.toString().trim();
            if (bianhao.isEmpty()) {
                return;
            }

            // 关键：手动输入的新编号，数据库里还不存在，不能加载，更不能清空曲线
            if (!isTestBianHaoExists(bianhao)) {
                loadedTestBianHao = "";
                loadedPointCount = 0;
                System.out.println("输入的是新测试编号，不清空当前曲线：" + bianhao);
                return;
            }

            // 只有数据库里存在的编号，才允许回显历史曲线
            loadSelectedTestResultToChart(bianhao);
        });

        countTest = new JTextField("");
        countTest.setFont(new Font("楷体", Font.BOLD, 24));
        countTest.setBounds(700, 10, 100, 36);
        countTest.setBackground(new Color(255, 255, 224));
        countTest.setHorizontalAlignment(JTextField.CENTER); // 设置文本居中对齐
        countTest.setEditable(false);
        gradientPanel.add(countTest);


        suduTest = new JTextField("");
        suduTest.setFont(new Font("楷体", Font.BOLD, 24));
        suduTest.setBounds(585, 165, 80, 36);
        suduTest.setBackground(new Color(255, 255, 224));
        suduTest.setHorizontalAlignment(JTextField.CENTER); // 设置文本居中对齐
        suduTest.setEditable(false);
        gradientPanel.add(suduTest);



        allcountTest = new JTextField("");

        allcountTest.setFont(new Font("楷体", Font.BOLD, 24));
        allcountTest.setBounds(908, 10, 80, 36);
        allcountTest.setBackground(new Color(255, 255, 224));
        allcountTest.setHorizontalAlignment(JTextField.CENTER); // 设置文本居中对齐
        allcountTest.setEditable(false);

        // 从数据库读取累计次数并显示到界面
        int currentTestcountmax1 = ExecuteCommon.getAllcountValue1(1);

        allcountTest.setText(String.valueOf(currentTestcountmax1));



        gradientPanel.add(allcountTest);






        Font buttonFont = new Font("楷体", Font.PLAIN, 22);

       btn4= createStyledButton("充值",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));


     //  placeButton(gradientPanel, btn4, 1400, 80, 200, 42);


        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 每次点击按钮时，更新 dianzumax 的值
                double dianzum1ax = ExecuteCommon.getDianzumaxValue(); // 从数据库中获取最新的 dianzumax 值

                if (isTimerRunning) {
                    // 如果计时器正在运行，停止计时器并将状态设置为未运行
                    if (testTimer != null) {
                        testTimer.stop();
                    }
                    isTimerRunning = false;
                    btn4.setText("开始测试"); // 恢复按钮的文本为 "开始开始测试"
                } else {
                    // 如果计时器未运行，启动计时器并将状态设置为运行中
                    testTimer = new Timer(1, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SwingUtilities.invokeLater(() -> {
                                value111 = generateRandomValue();
                                value222 = generateRandomValue();
                                value333 = generateRandomValue();
                                value444 = generateRandomValue();

                                dianzu111.setText(null);      dianzu222.setText(null);      dianzu333.setText(null);      dianzu444.setText(null);

                                dianzu111.setText(String.format("%.2f mΩ", value111));
                                dianzu222.setText(String.format("%.2f mΩ", value222));
                                dianzu333.setText(String.format("%.2f mΩ", value333));
                                dianzu444.setText(String.format("%.2f mΩ", value444));


                                if (value111 > dianzum1ax || value222 > dianzum1ax || value333 > dianzum1ax) {
                               //     System.out.println("测试数据中有大于"+dianzum1ax+"的值"+"该值为"+value111+"--------------"+value222+"--------------"+value333);
                                }

                            });
                        }
                    });

                    testTimer.start();
                    isTimerRunning = true;
                    btn4.setText("停止测试"); // 更新按钮的文本为 "停止测试开始测试"
                }
            }
        });








        beginCLBtn = createStyledButton("开始测试",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        btn8 = createStyledButton("退出",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        btn1 = createStyledButton("保存",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));

        btnclear = createStyledButton("清除",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));


        placeButton(gradientPanel, beginCLBtn, 1050, 5, 180, 42);
        placeButton(gradientPanel, btn1, 1245, 5, 100, 42);
        placeButton(gradientPanel, btn8, 1360, 5, 80, 42);

      //  placeButton(gradientPanel, btnclear, 1270, 5, 80, 42);

        btnclear.addActionListener(e -> {

            dianzu111.setText(null);
            dianzu222.setText(null);
            dianzu333.setText(null);
            dianzu444.setText(null);

            timeSeconds = 0;   // 每次开始开始测试时，将时间归零
            loadedPointCount = 0;
            resetChartData();   // 重置图表数据


        });


//批量插入，可以将所有数据一次性插入
        btn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JdbcDeal jdbcDeal = new JdbcDeal();

                // 获取 testTime、testBianHao、countTest 组件中的数据
                Date utilTestTimeValue = (Date) testTime.getModel().getValue();
                Object editorItem = testBianHao.getEditor().getItem();
                String testBianHaoValue = editorItem == null ? "" : editorItem.toString().trim();
                String countTestText = countTest.getText();

                // 将 java.util.Date 转换为 java.sql.Date
                java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());


                // 检查编号是否为空
                if (testBianHaoValue == null || testBianHaoValue.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return;
                }

                // 检查 countTest 是否为空
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return;
                }
//                // 检查编号是否已存在
//                if (jdbcDeal.isProductNumberExists06(testBianHaoValue)) {
//                    JOptionPane.showMessageDialog(null, "添加的继电器编号已存在，请输入不同的编号！");
//                    return;
//                }

                // 批量保存数据
                SwingUtilities.invokeLater(() -> {
                    long startTime = System.currentTimeMillis();

                    // 收集数据
                    List<Object[]> dataToInsert = new ArrayList<>();

                    int itemCount = Math.min(
                            Math.min(series1.getItemCount(), series2.getItemCount()),
                            Math.min(series3.getItemCount(), rightSeries.getItemCount())
                    );

                    if (itemCount <= 0) {
                        JOptionPane.showMessageDialog(null, "当前没有曲线数据需要保存！");
                        return;
                    }

// 判断当前输入的编号是不是数据库里已有编号
                    boolean exists = isTestBianHaoExists(testBianHaoValue);


                    // 只有“当前保存编号 = 当前回显编号”时，才按追加保存
// 其他情况，包括新编号、手动输入编号，都从第0个点保存
                    boolean appendMode = exists && testBianHaoValue.equals(loadedTestBianHao);


// 如果是已有编号：只保存新增点
// 如果是新编号：从第0个点开始全部保存
                    int saveStartIndex = appendMode ? loadedPointCount : 0;

                    if (itemCount <= saveStartIndex) {
                        JOptionPane.showMessageDialog(null, "当前没有新增曲线数据需要保存！");
                        return;
                    }

                    for (int i = saveStartIndex; i < itemCount; i++) {
                        double series1X = series1.getX(i).doubleValue();
                        double series1Y = series1.getY(i).doubleValue();

                        double series2X = series2.getX(i).doubleValue();
                        double series2Y = series2.getY(i).doubleValue();

                        double series3X = series3.getX(i).doubleValue();
                        double series3Y = series3.getY(i).doubleValue();

                        double series4X = rightSeries.getX(i).doubleValue();
                        double series4Y = rightSeries.getY(i).doubleValue();

                        dataToInsert.add(new Object[]{
                                sqlTestTimeValue, testBianHaoValue, countTestText,
                                series1X, series1Y,
                                series2X, series2Y,
                                series3X, series3Y,
                                series4X, series4Y
                        });
                    }

                    // 调用批量插入方法
                    int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);

                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    if (rowsInserted > 0) {

                        // 本次真正保存了多少条曲线数据
                        int saveCount = dataToInsert.size() * 2;

                        // 点击保存成功后，才更新累计次数
                        addAllCountBySaveCount(saveCount);

                        loadedPointCount = itemCount;
                        loadedTestBianHao = testBianHaoValue;

                        refreshTestBianHaoComboBox(testBianHaoValue);

                        String msg = appendMode
                                ? "数据已保存，并已合并到编号：" + testBianHaoValue
                                : "数据已保存到编号：" + testBianHaoValue;

                        JOptionPane.showMessageDialog(
                                null,
                                msg + "\n本次累计次数增加：" + saveCount,
                                "成功",
                                JOptionPane.INFORMATION_MESSAGE
                        );

                    } else {
                        JOptionPane.showMessageDialog(null, "数据保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });


//每次调用 saveTestResults 都执行一次数据库插入操作


        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

        //   保存 4个值到policetime表中,表的字段都有Id,Cishu,Tingzhizhi,Tingtime,Shebeihao,id是主键自增的 Cishu是从 JTextField  countTest1中获取的,Tingzhizhi是dianzu1Value1,Tingtime获取当时的系统时间,Shebeihao  固定为1,
        beginCLBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                isTestingStarted = !isTestingStarted;
                if (isTestingStarted) {
                    isDataUnsaved = true; // 标记数据未保存

                    ruAndwone.openSerialPort();
                    ruAndwone.writeSingleRegister( 1, 0);


                    processed2.set(false);


                    //最大电阻值
                    double dianzumax111 = ExecuteCommon.getDianzumaxValue(); // 从数据库中获取最新的 dianzumax 值


                    //最大值
                    double  countmax111 = ExecuteCommon.getTestmaxValue1(); // 从数据库中获取最新的  最大次数值
                    System.out.println(" double  countmax111 = ExecuteCommon.getTestmaxValue1();"+  countmax111 );

                    //最大值
                    allcount111 = ExecuteCommon.getTestmaxValue1(); // 从数据库中获取最新的  最大次数值



                    dianzu111.setText(null);
                    dianzu222.setText(null);
                    dianzu333.setText(null);
                    suduTest.setText(null);


                    beginCLBtn.setText("停止测试");
                    beginCLBtn.setFont(new Font("楷体", Font.PLAIN, 22)); // 更小的字体
                    if (beginCLBtn instanceof StyledButton) {
                        ((StyledButton) beginCLBtn).setGradientColor(
                                new Color(211, 6, 77),
                                new Color(150, 0, 45)
                        );
                    }
                    beginCLBtn.setForeground(Color.WHITE); // 白色文字



                    startListening();     // 开始监听文本框值的变化



                    currentTestFuture = executorServicebingxing.submit(() -> {

                        boolean continueLoop2 = true;  // 控制退出循环

                        while (continueLoop2) {

                            boolean continueLoop = true;  // 控制退出循环

                            while (continueLoop) {

                                Result result = readAndProcessRegisters();

                                if (result.value  == 1 && !processed2.get()) {        // 为了 先读取电阻 再读取电流  再读取电阻...   防止 一直读取电流

                                    processed2.set(true);  // 标记已经处理过

                                    ruAndwone.closeSerialPort();

                                    // 打开串口
                                    dianZu.startReading();
                                    // 读取并存储电阻值
                                    dianZu.readAndStoreValues();

                                    // 获取电阻值并打印
                                    averageInMOhm1 = dianZu.getDianZu1();
                                    averageInMOhm2 = dianZu.getDianZu2();
                                    averageInMOhm3 = dianZu.getDianZu3();

                                    System.out.println("电阻1: " + averageInMOhm1);
                                    System.out.println("电阻2: " + averageInMOhm2);
                                    System.out.println("电阻3: " + averageInMOhm3);
                                    // 关闭串口
                                    dianZu.stopReading();



                                    // 去掉单位并转换为纯数值
                                    pureValue1 = extractNumber(averageInMOhm1);
                                    pureValue2 = extractNumber(averageInMOhm2);
                                    pureValue3 = extractNumber(averageInMOhm3);


                                    SwingUtilities.invokeLater(() -> {
                                        dianzu111.setText(null);
                                        dianzu222.setText(null);
                                        dianzu333.setText(null);
                                        dianzu444.setText(null);
                                        String value = dianzu444NO.getText();

                                        //改前
                                        dianzu111.setText(averageInMOhm1);
                                        dianzu222.setText(averageInMOhm2);
                                        dianzu333.setText(averageInMOhm3);
                                        dianzu444.setText(value);

                                    });


                                    revalidate();
                                    repaint();



                                    continueLoop = false;
                                    processed111000.set(false);  // 标记已经处理过


                                    long currentTime = System.currentTimeMillis();
                                    lastTime111000 =   currentTime ;



                                }  else if (result.value == 0) {

                                    //j计时  报警
                                    long currentTime = System.currentTimeMillis();

                                    if( !processed111000.get()) {
                                        lastTime111000 =   currentTime ;
                                    }

                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime111000) / 1000.0;

                                    System.out.println(
                                            "==================X1 =====================  X1  的间隔时间  : "  +   timeDifference
                                    );



                                    if (  timeDifference > 12    &&   timeDifference< 10000   ){



                                        Sleep.sleepTh(100);
                                       ruAndwone.writeSingleRegister( 2, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 1, 1);

                                        JOptionPane.showMessageDialog(null, "设备右侧凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);

                                        saveToPolicetimeTable("设备右侧凸轮转动故障");

                                        Sleep.sleepTh(100);


                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    processed111000.set(true);  // 标记已经处理过           注意位置

                                    System.out.println("------------------------------------------------------------------------------------------111111111111111  111  灭了");

                                    processed2.set(false);  // 标记已经处理过                   注意位置

                                }


                                //灯灭为0
                                if (result.value2 == 1   && ! processed555111.get()  ) {

                                    System.out.println("------------------------------------------------------------------------------------------111111111111111  555  亮了");



                                    long currentTime = System.currentTimeMillis();
                                    lastTime555111=   currentTime ;




                                    ruAndwone.closeSerialPort();


                                    JiaoL.startReading();
                                    Sleep.sleepTh(200);
                                    String  iac04JiaoL3_1 = JiaoL.getJiaoLZValueWithUnit();
                                    JiaoL.stopReading();


                                    dianliu1Value1 = Double.parseDouble(iac04JiaoL3_1.split(" ")[0]);


                                    System.out.println("=============================读取了电流值=============================="  +  dianliu1Value1 );
                                    SwingUtilities.invokeLater(() -> {
                                        dianzu444NO.setText(iac04JiaoL3_1);
                                        revalidate();
                                        repaint();

                                    });





                                    processed555111.set(true);  // 标记已经处理过

                                }else if (result.value2 == 0  ) {

                                    System.out.println("------------------------------------------------------------------------------------------111111111111111  555  灭了");


                                    long currentTime = System.currentTimeMillis();


                                    if( processed555111.get()) {

                                        lastTime555111 =   currentTime ;

                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime555111) / 1000.0;

                                    System.out.println(
                                            "=====================X2====================  X2  : "  +   timeDifference
                                    );

                                    if (  timeDifference > 12  &&   timeDifference< 10000   ){


                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 2, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 1, 1);

                                        JOptionPane.showMessageDialog(null, "设备左侧凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);

                                        saveToPolicetimeTable("设备左侧凸轮转动故障");

                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    processed555111.set(false);  // 标记已经处理过

                                }



                                //    Sleep.sleepTh(100);

                            }
                            Sleep.sleepTh(100);


                            //置于  continueLoop2  上即可

                            if (pureValue1 > dianzumax111 || pureValue2 > dianzumax111 || pureValue3 > dianzumax111) {
                                // 执行相应操作，例如显示警告或停止开始测试




                                ruAndwone.openSerialPort();
                                ruAndwone.writeSingleRegister(1, 1);
                                ruAndwone.writeSingleRegister(2, 1); //报警
                                ruAndwone.closeSerialPort();

                                saveToPolicetimeTable("电阻值故障");



                                StringBuilder errorMsg = new StringBuilder();
                                errorMsg.append("测试数据中有大于")
                                        .append(dianzumax111)
                                        .append("的值：\n");

                                if (pureValue1 > dianzumax111) {
                                    errorMsg.append("接触电阻1不合格，值为：")
                                            .append(averageInMOhm1)
                                            .append("\n");
                                }

                                if (pureValue2 > dianzumax111) {
                                    errorMsg.append("接触电阻2不合格，值为：")
                                            .append(averageInMOhm2)
                                            .append("\n");
                                }

                                if (pureValue3 > dianzumax111) {
                                    errorMsg.append("接触电阻3不合格，值为：")
                                            .append(averageInMOhm3)
                                            .append("\n");
                                }

                                errorMsg.append("已停止！");

                                JOptionPane.showMessageDialog(
                                        null,
                                        errorMsg.toString(),
                                        "错误",
                                        JOptionPane.ERROR_MESSAGE
                                );


                                continueLoop2 = false; // 停止外部循环
                            }
                            if (dianliu1Value1 > 2.1 ) {

                                System.out.println("电流大于2.1"+"该值为"+dianliu1Value1);
                                ruAndwone.openSerialPort();
                                ruAndwone.writeSingleRegister(1, 1);
                                ruAndwone.writeSingleRegister(2, 1); //报警
                                ruAndwone.closeSerialPort();

                                saveToPolicetimeTable("电流值故障");

                                continueLoop2 = false; // 停止外部循环
                            }


                            if (timeSeconds  >= countmax111 ) {
                                ruAndwone.openSerialPort();
                                ruAndwone.writeSingleRegister(1, 1);
                                ruAndwone.writeSingleRegister(2, 1); //报警
                                ruAndwone.closeSerialPort();

                                continueLoop2 = false; // 停止外部循环


                                JOptionPane.showMessageDialog(null, "已达到设定测试次数", "正确", JOptionPane.ERROR_MESSAGE);
                                Sleep.sleepTh(200);
                            }


                        }


                    });

                } else {

                    //把别的测试程序干停了
                    // 停止测试
                    if (currentTestFuture != null    && !currentTestFuture.isDone()  ) {
                        currentTestFuture.cancel(true); // 尝试中断执行中的任务
                    }


                    lastTime111000=0;
                    lastTime555111=0;

                    ruAndwone.openSerialPort();
                    ruAndwone.writeSingleRegister( 2, 0);
                   // ruAndwone.writeSingleRegister( 1, 0);
                    ruAndwone.closeSerialPort();


                    beginCLBtn.setText("开始测试");
                    beginCLBtn.setFont(new Font("楷体", Font.PLAIN, 22));
                    beginCLBtn.setForeground(Color.WHITE);

                    if (beginCLBtn instanceof StyledButton) {
                        ((StyledButton) beginCLBtn).setGradientColor(
                                new Color(3, 39, 206),
                                new Color(3, 39, 206, 197)
                        );
                    }

                    stopListening();



                    isTestingStarted = false;

                }
            }
        });



        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------





        JLabel  t1 = new JLabel("接触电阻1:");
        t1.setFont(new Font("宋体", Font.BOLD, 20));
        t1.setForeground(new Color(0, 0, 0));
        t1.setBounds(20, 80, 206, 50);   //+- 1px
        gradientPanel.add(t1);

        JLabel  t2 = new JLabel("接触电阻2:");
        t2.setFont(new Font( "宋体", Font.BOLD, 20));
        t2.setForeground(new Color(0, 0, 0));
        t2.setBounds(370, 80, 206, 50);   //+- 1px
        gradientPanel.add(t2);

        JLabel  t3 = new JLabel("接触电阻3:");
        t3.setFont(new Font("宋体", Font.BOLD, 20));
        t3.setForeground(new Color(0, 0, 0));
        t3.setBounds(727, 80, 206, 50);   //+- 1px
        gradientPanel.add(t3);



        JLabel  t4 = new JLabel("电流:");
        t4.setFont(new Font("宋体", Font.BOLD, 20));
        t4.setForeground(new Color(0, 0, 0));
        t4.setBounds(1107, 80, 206, 50);   //+- 1px
        gradientPanel.add(t4);


        dianzu111 = createCustomTextField(130, 66, 200, 75);
        gradientPanel.add(dianzu111);

        dianzu222 = createCustomTextField(480, 66, 200, 75);
        gradientPanel.add(dianzu222);

        dianzu333 = createCustomTextField(840, 66, 200, 75);
        gradientPanel.add(dianzu333);

        dianzu444 = createCustomTextField(1170, 66, 200, 75);
        gradientPanel.add(dianzu444);

        dianzu444NO = createCustomTextFieldNO(430, 56, 60, 75);
        gradientPanel.add(dianzu444NO);





//        刷新对话框或面板

//        gradientPanel.revalidate();
//        gradientPanel.repaint();


//---------------------------------------折线图------------------------------------------------------------------------------------------------------


        //   电阻X轴  坐标
        JLabel RedXY = new JLabel("");
        RedXY.setFont(new Font("华文仿宋", Font.BOLD, 20));
        RedXY.setForeground( Color.RED);
        RedXY.setBounds(760, 165, 300, 30);
        gradientPanel.add(RedXY);

        // 电阻2（绿色）的坐标显示
        JLabel GreenXY = new JLabel("");
        GreenXY.setFont(new Font("华文仿宋", Font.BOLD, 20));
        GreenXY.setForeground(new Color(62, 175, 81)); // 绿色字体
        GreenXY.setBounds(940, 165, 300, 30); // 根界面调据实际位整置
        gradientPanel.add(GreenXY);

        // 电阻3（蓝色）的坐标显示
        JLabel BlueXY = new JLabel("");
        BlueXY.setFont(new Font("华文仿宋", Font.BOLD, 20));
        BlueXY.setForeground(Color.BLUE); // 蓝色字体
        BlueXY.setBounds(1120, 165, 300, 30); // 根据实际界面调整位置
        gradientPanel.add(BlueXY);

        // 电流（黄色）的坐标显示
        JLabel YellowXY = new JLabel("");
        YellowXY.setFont(new Font("华文仿宋", Font.BOLD, 20));
        YellowXY.setForeground(new Color(115, 95, 1, 255)); //
        YellowXY.setBounds(1295, 165, 300, 30); // 根据实际界面调整位置
        gradientPanel.add(YellowXY);


        // 创建  左侧Y轴  序列数据集（电阻）
        series1 = new XYSeries("电阻1");
        series2 = new XYSeries("电阻2");
        series3 = new XYSeries("电阻3");


        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series1);
        dataset1.addSeries(series2);
        dataset1.addSeries(series3);


        // 创建 右侧Y轴 的数据集（电流）
        rightSeries = new XYSeries("电流");
        XYSeriesCollection rightDataset = new XYSeriesCollection();
        rightDataset.addSeries(rightSeries);




// 创建图表
// 创建一个时间序列图表。

        chart = ChartFactory.createXYLineChart(
                "接点接触电阻参数曲线", // 图表标题
                "次数 ", // x 轴标签
                "电阻(mΩ)", // y 轴标签
                dataset1, // 数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 生成工具提示
                false // 不生成URL
        );


        // 设置图表标题和其颜色
        chart.getTitle().setPaint(Color.BLUE);
        Font titleFont = new Font("宋体", Font.BOLD, 18);
        chart.getTitle().setFont(titleFont);

        plot = (XYPlot) chart.getPlot();
        plot.getDomainAxis().setLabelFont(titleFont);
        NumberAxis xAxis = new NumberAxis("次数 (次)");
        xAxis.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        xAxis.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));
        xAxis.setLowerBound(0);// 设置 X 轴的最小值为 0，防止出现负数
        xAxis.setRange(0, 28);  // 初始X轴范围设置为 0到20
        xAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        plot.setDomainAxis(xAxis);




// 配置图表的左侧 Y 轴（电阻）
        NumberAxis yAxis1 = (NumberAxis) plot.getRangeAxis();
        //  yAxis1.setRange(0, 150);  // 设置Y轴范围
        //  yAxis1.setLabelFont(titleFont);
        //    yAxis1.setTickLabelFont(new Font("SimHei", Font.PLAIN, 12));
        yAxis1.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        xAxis.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));


// 创建并配置左侧电阻的渲染器
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.RED); // 第一条曲线为红色
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        float lineWidth = 1.5f;              // 曲线的粗细
        renderer.setSeriesStroke(0, new BasicStroke(lineWidth));

        renderer.setSeriesPaint(1, Color.GREEN); // 第二条曲线为绿色
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, false);
        renderer.setSeriesStroke(1, new BasicStroke(lineWidth));

        renderer.setSeriesPaint(2, Color.BLUE); // 第三条曲线为蓝色
        renderer.setSeriesLinesVisible(2, true);
        renderer.setSeriesShapesVisible(2, false);
        renderer.setSeriesStroke(2, new BasicStroke(lineWidth));



// 配置右侧折线图的渲染器
        XYLineAndShapeRenderer rightRenderer = new XYLineAndShapeRenderer();
        rightRenderer.setSeriesPaint(0, new Color(115, 95, 1, 255)); // 右侧折线颜色
        rightRenderer.setSeriesStroke(0, new BasicStroke(1.5f)); // 设置右侧折线粗细




        Font legendFont = new Font("SimHei", Font.PLAIN, 12);
        chart.getLegend().setItemFont(legendFont);



        //---------------------------------------------------- 创建右侧Y轴----------------------------------------------------
        NumberAxis rightYAxis = new NumberAxis("电流(A)");

      //  rightYAxis.setAutoRange(true);  // 自动根据数据调整范围
        // 设置右侧 Y 轴的范围为 [0, 1]
   //     rightYAxis.setRange(0, 7);
   //     rightYAxis.setAutoRange(false);  // 禁止自动调整范围

     //   rightYAxis.setAutoRangeIncludesZero(false); // 设置右侧Y轴属性
        rightYAxis.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 字体设置
        rightYAxis.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));

// 添加右侧Y轴到图表中
        plot.setRangeAxis(1, rightYAxis);
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, true);



// 将右侧数据集映射到右侧Y轴
        plot.setDataset(1, rightDataset);
        plot.setRenderer(1, rightRenderer);
        plot.mapDatasetToRangeAxis(1, 1); // 将数据集映射到右侧Y轴


        // 获取图表的 XYPlot
        XYPlot plot = chart.getXYPlot();

// 将右侧 Y 轴添加到图表中
        plot.setRangeAxis(1, rightYAxis);

// 将右侧数据集映射到右侧 Y 轴
        plot.setDataset(1, rightDataset);  // rightDataset 是右侧的电流数据集
        plot.mapDatasetToRangeAxis(1, 1);  // 将数据集映射到第二个 Y 轴 (右侧)


// 设置右侧数据集对应的渲染器
        plot.setRenderer(1, rightRenderer);



        //--------------------------------------------------------------------------------------------------------------

        // 创建折线图
        ChartPanel chartPanel = new ChartPanel(chart);

        // 关键：防止 ChartPanel 缩放导致标题、坐标轴字体变扁    就这4个
        chartPanel.setMinimumDrawWidth(0);
        chartPanel.setMinimumDrawHeight(0);
        chartPanel.setMaximumDrawWidth(3000);
        chartPanel.setMaximumDrawHeight(2000);


        // 禁用鼠标缩放功能
        chartPanel.setMouseZoomable(false);
        chartPanel.setMouseZoomable(false, false);

            chartPanel.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                int notches = e.getWheelRotation();  // 获取鼠标滚轮滚动的刻度数。如果为正数，表示向下滚动；负数表示向上滚动。
                XYPlot plot = (XYPlot) chart.getPlot();  // 获取图表的XYPlot对象，用于操作X轴的范围。
                ValueAxis xAxis = plot.getDomainAxis();  // 获取X轴对象，以便调整X轴的范围。

                // 获取当前X轴的下限和上限
                double lowerBoundX = xAxis.getLowerBound();  // 获取X轴当前的下限值。
                double upperBoundX = xAxis.getUpperBound();  // 获取X轴当前的上限值。

                // 当前视图的范围
                double currentRange = upperBoundX - lowerBoundX;

                // 最大视图范围
                double totalRange = xAxis.getRange().getLength();

                // 根据当前视图范围大小动态调整缩放比例
                double decrement;
                if (currentRange > totalRange * 0.1) {
                    // 如果当前视图范围大于总范围的 10%，使用 10% 的缩放比例
                    decrement = totalRange * 0.1;
                } else {
                    // 否则使用 1% 的缩放比例
                    decrement = totalRange * 0.01;
                }

                // 根据滚轮方向调整X轴范围
                if (notches > 0) {  // 向下滚动，X轴的下限减小
                    lowerBoundX -= decrement;  // 减少下限值
                    if (lowerBoundX < 0) {  // 如果下限值小于0，设置为0
                        lowerBoundX = 0;
                    }
                } else if (notches < 0) {  // 向上滚动，X轴的下限增大
                    lowerBoundX += decrement;  // 增加下限值

                    // 保证X轴的范围至少是 (当前值 - 28) 到 28
                    if (upperBoundX - lowerBoundX < 28) {
                        lowerBoundX = upperBoundX - 28;
                    }
                    if (lowerBoundX < 0) {
                        lowerBoundX = 0;
                    }
                }

                // 设置X轴范围，保持上限不变，仅调整下限
                xAxis.setRange(lowerBoundX, upperBoundX);  // 将新的下限和固定的上限设置到X轴上。
            }
        });



//
//        chartPanel.addMouseWheelListener(new MouseWheelListener() {
//            @Override
//            public void mouseWheelMoved(MouseWheelEvent e) {
//
//                int notches = e.getWheelRotation();  // 获取鼠标滚轮滚动的刻度数。如果为正数，表示向下滚动；负数表示向上滚动。
//                XYPlot plot = (XYPlot) chart.getPlot();  // 获取图表的XYPlot对象，用于操作X轴的范围。
//                ValueAxis xAxis = plot.getDomainAxis();  // 获取X轴对象，以便调整X轴的范围。
//
//                // 获取当前X轴的下限和上限
//                double lowerBoundX = xAxis.getLowerBound();  // 获取X轴当前的下限值。
//                double upperBoundX = xAxis.getUpperBound();  // 获取X轴当前的上限值。
//
//                /*当放大范围时，先以最大值的100分之1开始增加   当到达最大值的十分之1后再以十分之1方法*/
//
//
//                double decrement = upperBoundX * 0.1;
//
//
//
//                // 根据滚轮方向调整X轴范围
//                if (notches > 0) {  // 向下滚动，X轴的下限减小
//                    lowerBoundX -= decrement;  // 减少下限值
//                    if (lowerBoundX < 0) {  // 如果下限值小于0，设置为0
//                        lowerBoundX = 0;
//                    }
//                } else if (notches < 0) {  // 向上滚动，X轴的下限增大
//                    lowerBoundX += decrement;  // 增加下限值
//                    if (lowerBoundX >= upperBoundX) {  // 防止下限超过上限
//                        lowerBoundX = upperBoundX - decrement;
//                    }
//                }
//
//                // 设置X轴范围，保持上限不变，仅调整下限
//                xAxis.setRange(lowerBoundX, upperBoundX);  // 将新的下限和固定的上限设置到X轴上。
//
//            }
//        });





// 创建水平滚动条

        horizontalScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScrollBar.setMaximum(Integer.parseInt(newXValue)); // 设置X轴的最大值为当前的最大X值
        horizontalScrollBar.setVisibleAmount(28); // 设置可视范围为28
        horizontalScrollBar.setUnitIncrement(1); // 设置滚动增量
        horizontalScrollBar.setValue(Integer.parseInt(newXValue)-28); // 初始位置设置为最右边，确保拖动条位于最右边


// 创建垂直滚动条
        verticalScrollBar = new JScrollBar(JScrollBar.VERTICAL);
        verticalScrollBar.setUnitIncrement(1); // 设置滚动增量
        verticalScrollBar.setMaximum(2000); // 设置最大值为2000
        verticalScrollBar.setVisibleAmount(60); // 设置可视范围为60
        verticalScrollBar.setValue(2000 - 60); // 初始值设置为最大值减去可视范围    // 初始位置设置为最大值减去可见范围，这样拖动条会在最底部



        horizontalScrollBar.setBounds(0, 746, 1400, 20); // 设置水平滚动条的位置和大小
        verticalScrollBar.setBounds(1400, 212, 20, 555); // 设置垂直滚动条的位置和大小


        // 拖拽水平滚动条的监听器
        horizontalScrollBar.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = horizontalScrollBar.getValue();

            // 计算新的X轴显示范围
            double newLowerBound = value;
            double newUpperBound = value + 28;

            // 更新X轴的显示范围
            plot.getDomainAxis().setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel.repaint();
        });

        // 拖拽垂直滚动条的监听器
        verticalScrollBar.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = verticalScrollBar.getValue();

            // 计算新的Y轴显示范围
            double newLowerBound = 0; // 下界固定为0
            double newUpperBound = 2000 - value; // 上界逐渐从2000减小到60

            // 更新Y轴的显示范围
            plot.getRangeAxis().setRange(newLowerBound, newUpperBound);

            // 更新右侧Y轴的显示范围
            rightYAxis.setRange(newLowerBound, newUpperBound);


            // 重新绘制图表
            chartPanel.repaint();
        });


        gradientPanel.add(horizontalScrollBar);// 将滚动条添加到 GradientPanel
        gradientPanel.add(verticalScrollBar);

        chartPanel.setLayout(null);// 设置 ChartPanel 的布局为 null（绝对布局），然后手动设置其位置和大小
        chartPanel.setBounds(0, 212, 1400, 522);

        gradientPanel.add(chartPanel);



        chartPanel.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                // 这里可以处理点击事件
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {

                int mouseX = event.getTrigger().getX();
                Point2D p2 = chartPanel.translateScreenToJava2D(new Point(mouseX, event.getTrigger().getY()));
                XYPlot plot = (XYPlot) chart.getPlot();
                ChartRenderingInfo info = chartPanel.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();
                ValueAxis xAxis = plot.getDomainAxis();

                double x = xAxis.java2DToValue(p2.getX(), dataArea, RectangleEdge.BOTTOM);

                plot.clearDomainMarkers();
                ValueMarker marker = new ValueMarker(x);
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);

                // 查找最近的点
                // 查找最近的点
                XYSeries series1 = ((XYSeriesCollection) plot.getDataset()).getSeries(0);
                XYSeries series2 = ((XYSeriesCollection) plot.getDataset()).getSeries(1);
                XYSeries series3 = ((XYSeriesCollection) plot.getDataset()).getSeries(2);
                XYSeries rightSeries = ((XYSeriesCollection) plot.getDataset(1)).getSeries(0); // 获取右侧 Y 轴的电流数据集


                XYDataItem closestItem1 = findClosestDataItem(series1, x);
                XYDataItem closestItem2 = findClosestDataItem(series2, x);
                XYDataItem closestItem3 = findClosestDataItem(series3, x);
                XYDataItem closestItemRight = findClosestDataItem(rightSeries, x); // 查找电流曲线的最近点


                if (closestItem1 != null) {
                    RedXY.setText(String.format("电阻1: %.2f mΩ", closestItem1.getYValue()));
                    RedXY.repaint();
                }
                if (closestItem2 != null) {
                    GreenXY.setText(String.format("电阻2: %.2f mΩ", closestItem2.getYValue()));
                    GreenXY.repaint();
                }
                if (closestItem3 != null) {
                    BlueXY.setText(String.format("电阻3: %.2f mΩ", closestItem3.getYValue()));
                    BlueXY.repaint();
                }
                if (closestItemRight != null) {
                    YellowXY.setText(String.format("电流: %.2f A", closestItemRight.getYValue())); // 显示电流值
                    YellowXY.repaint();
                }

            }
        });




        btn8.addActionListener(e -> {

            resetForNextEnter();

            parentFrame.showCard("Home");  // 切换到“Home”界面
        });



        // 创建复选框
        JCheckBox redBox = new JCheckBox("电阻1", true);
        redBox.setBounds(1190, 185, 70, 30);  // 设置复选框位置和大小
        redBox.addActionListener(e -> {
            boolean isSelected = redBox.isSelected();
            renderer.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏线条

            gradientPanel.revalidate();
            gradientPanel.repaint();
        });

// 创建复选框以控制绿色（电阻2）的显示
        JCheckBox greenBox = new JCheckBox("电阻2", true);
        greenBox.setBounds(1260, 185, 70, 30);  // 设置复选框位置和大小
        greenBox.addActionListener(e -> {
            boolean isSelected = greenBox.isSelected();
            renderer.setSeriesLinesVisible(1, isSelected);  // 根据复选框状态显示或隐藏绿色线条

            gradientPanel.revalidate();
            gradientPanel.repaint();
        });

// 创建复选框以控制蓝色（电阻3）的显示
        JCheckBox blueBox = new JCheckBox("电阻3", true);
        blueBox.setBounds(1330, 185, 70, 30);  // 设置复选框位置和大小
        blueBox.addActionListener(e -> {
            boolean isSelected = blueBox.isSelected();
            renderer.setSeriesLinesVisible(2, isSelected);  // 根据复选框状态显示或隐藏蓝色线条

            gradientPanel.revalidate();
            gradientPanel.repaint();
        });


// 为右侧Y轴控制电流显示的复选框
        JCheckBox rightSeriesBox = new JCheckBox("电流", true);
        rightSeriesBox.setBounds(1395, 185, 70, 30);  // 设置复选框位置和大小
        rightSeriesBox.addActionListener(e -> {
            boolean isSelected = rightSeriesBox.isSelected();

            rightRenderer.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏电流线条
            rightRenderer.setSeriesShapesVisible(0, isSelected); // 控制点显示/隐藏

            gradientPanel.revalidate();
            gradientPanel.repaint();
        });

// 添加图表面板和复选框到 gradientPanel
        gradientPanel.add(chartPanel);
        gradientPanel.add(redBox);
        gradientPanel.add(blueBox);
        gradientPanel.add(greenBox);
        gradientPanel.add(rightSeriesBox);

// 确保容器更新和重绘
        gradientPanel.revalidate();
        gradientPanel.repaint();


    }

    private void saveToPolicetimeTabledianliu(double dianliuValue, double maxLimit) {

        // 没超过设定值，不保存
        if (dianliuValue <= maxLimit) {
            System.out.println("电流未超过设定值，不保存报警记录。电流="
                    + dianliuValue + "，设定值=" + maxLimit);
            return;
        }

        int cishu;

        try {
            String cishuText = countTest.getText() == null ? "" : countTest.getText().trim();
            String numberText = cishuText.replaceAll("[^0-9]", "");
            cishu = numberText.isEmpty() ? timeSeconds : Integer.parseInt(numberText);
        } catch (Exception e) {
            cishu = timeSeconds;
        }

        if (cishu <= 0) {
            cishu = 1;
        }

        String faultInfo = "电流值故障：电流的值为："
                + String.format("%.2f", dianliuValue)
                + "A；";

        String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime) VALUES (?, ?, ?)";

        try (Connection connection = JdbcDeal.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setInt(1, cishu);
            psmt.setString(2, faultInfo);
            psmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            int rows = psmt.executeUpdate();

            if (rows > 0) {
                System.out.println("电流报警记录保存成功：" + faultInfo);
            } else {
                System.out.println("电流报警记录保存失败");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "电流报警记录保存失败：" + e.getMessage(),
                        "数据库错误",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }

    private void saveToPolicetimeTable(String tingzhizhi) {

        String cishuText = countTest.getText() == null ? "" : countTest.getText().trim();

        if (cishuText.isEmpty()) {
            cishuText = String.valueOf(timeSeconds);
        }

        int cishu;
        try {
            String numberText = cishuText.replaceAll("[^0-9]", "");
            cishu = numberText.isEmpty() ? timeSeconds : Integer.parseInt(numberText);
        } catch (Exception e) {
            cishu = timeSeconds;
        }

        if (cishu <= 0) {
            cishu = 1;
        }

        if (tingzhizhi == null || tingzhizhi.trim().isEmpty()) {
            tingzhizhi = "未知故障";
        }

        String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime) VALUES (?, ?, ?)";

        try (Connection connection = JdbcDeal.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setInt(1, cishu);
            psmt.setString(2, tingzhizhi);
            psmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            int rows = psmt.executeUpdate();

            if (rows > 0) {
                System.out.println("报警记录保存成功：" + tingzhizhi);
            } else {
                System.out.println("报警记录保存失败：" + tingzhizhi);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "报警记录保存失败：" + e.getMessage(),
                        "数据库错误",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }

/*
    private void saveToPolicetimeTable() {

        String cishuText = countTest.getText() == null ? "" : countTest.getText().trim();

        if (cishuText.isEmpty()) {
            cishuText = String.valueOf(timeSeconds);
        }

        int cishu;
        try {
            String numberText = cishuText.replaceAll("[^0-9]", "");
            cishu = numberText.isEmpty() ? timeSeconds : Integer.parseInt(numberText);
        } catch (Exception e) {
            cishu = timeSeconds;
        }

        if (cishu <= 0) {
            cishu = 1;
        }

        String dz1Text = dianzu111.getText() == null ? "" : dianzu111.getText().trim();
        String dz2Text = dianzu222.getText() == null ? "" : dianzu222.getText().trim();
        String dz3Text = dianzu333.getText() == null ? "" : dianzu333.getText().trim();

        double dz1 = extractNumber(dz1Text);
        double dz2 = extractNumber(dz2Text);
        double dz3 = extractNumber(dz3Text);

        double maxLimit = ExecuteCommon.getDianzumaxValue();

        StringBuilder faultInfo = new StringBuilder("电阻值故障：");

        boolean hasFault = false;

        if (dz1 > maxLimit) {
            faultInfo.append("电阻1的值为：")
                    .append(String.format("%.2f", dz1))
                    .append("mΩ；");
            hasFault = true;
        }

        if (dz2 > maxLimit) {
            faultInfo.append("电阻2的值为：")
                    .append(String.format("%.2f", dz2))
                    .append("mΩ；");
            hasFault = true;
        }

        if (dz3 > maxLimit) {
            faultInfo.append("电阻3的值为：")
                    .append(String.format("%.2f", dz3))
                    .append("mΩ；");
            hasFault = true;
        }

        // 关键：三个电阻都没有超过设定值，直接返回，不保存
        if (!hasFault) {
            System.out.println("三个电阻值均未超过设定值，不保存报警记录。"
                    + " 电阻1=" + dz1
                    + "，电阻2=" + dz2
                    + "，电阻3=" + dz3
                    + "，设定值=" + maxLimit);
            return;
        }

        String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime) VALUES (?, ?, ?)";

        try (Connection connection = JdbcDeal.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setInt(1, cishu);
            psmt.setString(2, faultInfo.toString());
            psmt.setTimestamp(3, new Timestamp(System.currentTimeMillis()));

            int rows = psmt.executeUpdate();

            if (rows > 0) {
                System.out.println("报警记录保存成功：" + faultInfo);
            } else {
                System.out.println("报警记录保存失败");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(
                        this,
                        "报警记录保存失败：" + e.getMessage(),
                        "数据库错误",
                        JOptionPane.ERROR_MESSAGE
                );
            });
        }
    }
*/

    private void addAllCountBySaveCount(int saveCount) {
        if (saveCount <= 0) {
            return;
        }

        try {
            int currentAllCount;

            String allCountText = allcountTest.getText() == null ? "" : allcountTest.getText().trim();

            if (allCountText.isEmpty()) {
                currentAllCount = ExecuteCommon.getAllcountValue1(1);
            } else {
                currentAllCount = Integer.parseInt(allCountText);
            }

            int newAllCount = currentAllCount + saveCount;

            // 更新数据库累计次数
            ExecuteCommon.updatealltestcountValueForId(1, newAllCount);

            // 更新界面累计次数
            allcountTest.setText(String.valueOf(newAllCount));

            System.out.println("保存成功，累计次数增加：" + saveCount + "，当前累计次数：" + newAllCount);

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "累计次数更新失败：" + ex.getMessage(),
                    "错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void refreshTestBianHaoComboBox(String selectedValue) {
        loadingHistory = true;

        try {
            testBianHao.removeAllItems();
            testBianHao.addItem("");

            String[] bianhaoArray = getComboBoxDataDanLie("test_results", "test_bianhao");
            for (String item : bianhaoArray) {
                testBianHao.addItem(item);
            }

            if (selectedValue != null && !selectedValue.trim().isEmpty()) {
                testBianHao.setSelectedItem(selectedValue.trim());
            } else {
                testBianHao.setSelectedIndex(0);
            }
        } finally {
            loadingHistory = false;
        }
    }

    private void loadSelectedTestResultToChart(String testBianHaoValue) {
        if (testBianHaoValue == null || testBianHaoValue.trim().isEmpty()) {
            return;
        }

// 关键：如果数据库没有这个编号，说明是新编号，不允许清空当前曲线
        if (!isTestBianHaoExists(testBianHaoValue)) {
            loadedTestBianHao = "";
            loadedPointCount = 0;
            System.out.println("数据库中没有该编号，不回显，不清空曲线：" + testBianHaoValue);
            return;
        }

// 到这里才说明是旧编号，可以清空后回显
        series1.clear();
        series2.clear();
        series3.clear();
        rightSeries.clear();

        String query =
                "SELECT test_time, count_test, " +
                        "series1_x, series1_y, " +
                        "series2_x, series2_y, " +
                        "series3_x, series3_y, " +
                        "series4_x, series4_y " +
                        "FROM test_results " +
                        "WHERE test_bianhao = ? " +
                        "ORDER BY series1_x ASC";

        List<XYDataItem> series1Data = new ArrayList<>();
        List<XYDataItem> series2Data = new ArrayList<>();
        List<XYDataItem> series3Data = new ArrayList<>();
        List<XYDataItem> series4Data = new ArrayList<>();

        int maxX = 0;
        java.sql.Date dbTestTime = null;
        int dbCountTest = 0;

        try (Connection connection = JdbcDeal.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, testBianHaoValue);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (!resultSet.isBeforeFirst()) {
                    loadedPointCount = 0;
                    timeSeconds = 0;
                    return;
                }

                while (resultSet.next()) {
                    dbTestTime = resultSet.getDate("test_time");
                    dbCountTest = resultSet.getInt("count_test");

                    double series1X = resultSet.getDouble("series1_x");
                    double series1Y = resultSet.getDouble("series1_y");

                    double series2X = resultSet.getDouble("series2_x");
                    double series2Y = resultSet.getDouble("series2_y");

                    double series3X = resultSet.getDouble("series3_x");
                    double series3Y = resultSet.getDouble("series3_y");

                    double series4X = resultSet.getDouble("series4_x");
                    double series4Y = resultSet.getDouble("series4_y");

                    series1Data.add(new XYDataItem(series1X, series1Y));
                    series2Data.add(new XYDataItem(series2X, series2Y));
                    series3Data.add(new XYDataItem(series3X, series3Y));
                    series4Data.add(new XYDataItem(series4X, series4Y));

                    maxX = Math.max(maxX, (int) series1X);
                    maxX = Math.max(maxX, (int) series2X);
                    maxX = Math.max(maxX, (int) series3X);
                    maxX = Math.max(maxX, (int) series4X);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据加载失败：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int finalMaxX = maxX;
        java.sql.Date finalDbTestTime = dbTestTime;
        int finalDbCountTest = dbCountTest;

        SwingUtilities.invokeLater(() -> {
            loadingHistory = true;

            try {
                series1.setNotify(false);
                series2.setNotify(false);
                series3.setNotify(false);
                rightSeries.setNotify(false);

                for (XYDataItem item : series1Data) {
                    series1.add(item);
                }
                for (XYDataItem item : series2Data) {
                    series2.add(item);
                }
                for (XYDataItem item : series3Data) {
                    series3.add(item);
                }
                for (XYDataItem item : series4Data) {
                    rightSeries.add(item);
                }

                series1.setNotify(true);
                series2.setNotify(true);
                series3.setNotify(true);
                rightSeries.setNotify(true);

                loadedPointCount = series1.getItemCount();
                loadedTestBianHao = testBianHaoValue;
                // 关键：让后续点击开始测试后，接着历史最大次数继续画
                timeSeconds = finalMaxX + 1;

                // 回显日期和监测次数
                if (finalDbTestTime != null && testTime != null) {
                    testTime.getModel().setDate(
                            finalDbTestTime.toLocalDate().getYear(),
                            finalDbTestTime.toLocalDate().getMonthValue() - 1,
                            finalDbTestTime.toLocalDate().getDayOfMonth()
                    );
                    testTime.getModel().setSelected(true);
                }

                if (countTest != null) {
                    countTest.setText(String.valueOf(finalDbCountTest));
                }

                updateXAxis();
                lockYAxisRange();

                revalidate();
                repaint();

            } finally {
                loadingHistory = false;
            }
        });
    }

    // 当查询一列数据时  先去重 再查
// 从数据库中获取某一列数据，参考 CXPanel 中 getComboBoxDataDanLie 的写法
    public String[] getComboBoxDataDanLie(String tableName, String columnName) {
        List<String> data = new ArrayList<>();

        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            ResultSet rs = jdbcDeal.querybianhao(tableName, columnName);
            while (rs.next()) {
                String value = rs.getString(columnName);
                if (value != null && !value.trim().isEmpty()) {
                    data.add(value.trim());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return data.toArray(new String[0]);
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }


    public synchronized Result readAndProcessRegisters() {
        byte deviceAddress = 0x08; // 设备地址
        int startRegister = 0; // 起始寄存器地址
        int registerCount = 2; // 读取寄存器数量
        byte[] response;

        while (true) {

            ruAndwone.openSerialPort();

            response = ruAndwone.readInputRegisters(deviceAddress, startRegister, registerCount);

            if ((response[1] & 0xFF) == 0x04) {
                break;
            } else {
                System.out.println("功能码不符，期望0x04，实际：" + String.format("%02X", response[1] & 0xFF));
                Sleep.sleepTh(10);
            }
        }


        if (response.length > 0) {
            System.out.println("读取到输入输出的响应数据: " + bytesToHex(response));
        } else {
            System.out.println("未能读取到数据或读取错误");
        }

        // Extract register values
        int value = ((response[3] & 0xFF) << 8) | (response[4] & 0xFF);
        int value5 = ((response[5] & 0xFF) << 8) | (response[6] & 0xFF);


        if (value == 1 && !processedjishi1.get()) {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     亮了  ");


            long currentTime = System.currentTimeMillis();

            // 计算时间差（秒）
            double timeDifference = (currentTime - lastTime111111) / 1000.0;

            if (timeDifference > 2) {


                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     时间差  " + timeDifference);

                // 计算频率值
                double frequency = 120.0 / timeDifference;

                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     次数值" + frequency);

                // 直接四舍五入
                roundedFrequency111 = Math.round(frequency);


                // 更新到界面
                SwingUtilities.invokeLater(() -> {
                    suduTest.setText(String.valueOf(roundedFrequency111)); //
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     四舍五入后" + roundedFrequency111);
                    revalidate();
                    repaint();

                });

                revalidate();
                repaint();

                lastTime111111 = currentTime;
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     lastTime111" + lastTime111111);


            } else {

            }
            System.out.println(
                    "RRu 1 状态: " + (value == 1 ? "1" : "0")
            );

            processedjishi1.set(true);

        } else if (value == 0) {
            processedjishi1.set(false);
        }


        // Return the result
        return new Result(value,  value5);
    }



    private void updateScrollBarAndChart(int totalMeasurements, double maxYValue) {
        // 更新水平滚动条
        horizontalScrollBar.setMaximum(totalMeasurements + horizontalScrollBar.getVisibleAmount());
        horizontalScrollBar.setValue(Math.max(0, totalMeasurements - horizontalScrollBar.getVisibleAmount()));

        // 更新垂直滚动条
        verticalScrollBar.setMaximum((int) maxYValue);
        verticalScrollBar.setValue((int) maxYValue);

        // 更新X轴和Y轴的范围
        plot.getDomainAxis().setRange(horizontalScrollBar.getValue(), horizontalScrollBar.getValue() + horizontalScrollBar.getVisibleAmount());
        plot.getRangeAxis().setRange(verticalScrollBar.getValue() - 28, verticalScrollBar.getValue());

        gradientPanel.revalidate();
        gradientPanel.repaint();
    }

    private double calculateMaxYValue(XYSeries... series) {
        double max = 0;
        for (XYSeries s : series) {
            for (int i = 0; i < s.getItemCount(); i++) {
                max = Math.max(max, s.getY(i).doubleValue());
            }
        }
        return max;
    }


    public void refreshDianzumaxValue() {
        dianzumax = ExecuteCommon.getDianzumaxValue();
        labeldianzumax.setText(dianzumax + " mΩ");
        labeldianzumax.repaint();
    }

    public void refreshAllcountValue() {
        int currentTestcountmax1 = ExecuteCommon.getAllcountValue1(1);
        allcountTest.setText(String.valueOf(currentTestcountmax1));
        allcountTest.repaint();
    }


    // 获取所有需要插入的数据
    private List<Object[]> getDataToInsert(Date testTimeValue, String testBianHaoValue, String countTestText) {
        List<Object[]> dataToInsert = new ArrayList<>();

        // 遍历所有点
        for (int i = 0; i < series1.getItemCount(); i++) {
            dataToInsert.add(new Object[]{testTimeValue, testBianHaoValue, countTestText, series1.getX(i), series1.getY(i)});
        }

        // 对于 series2 和 series3，进行相同的操作
        for (int i = 0; i < series2.getItemCount(); i++) {
            dataToInsert.add(new Object[]{testTimeValue, testBianHaoValue, countTestText, series2.getX(i), series2.getY(i)});
        }

        for (int i = 0; i < series3.getItemCount(); i++) {
            dataToInsert.add(new Object[]{testTimeValue, testBianHaoValue, countTestText, series3.getX(i), series3.getY(i)});
        }

        return dataToInsert;
    }
    // 生成一个随机的电阻值
    private double generateRandomValue() {
        return random.nextDouble() * 60; // 生成 0 到 100 之间的随机值
    }

    private double convertToMOhm(String iac01WithUnit) {

        String valuePart = iac01WithUnit.replaceAll("[^\\d.]", "");  // 提取数值部分

        double iac01 = Double.parseDouble(valuePart);

        return iac01; // 默认已经是 mΩ

    }

/*
    // 辅助方法：将电阻值转换为 mΩ
    private double convertToMOhm(String iac01WithUnit) {
        String valuePart = iac01WithUnit.replaceAll("[^\\d.]", "");  // 提取数值部分
        String unitPart = iac01WithUnit.replaceAll("[\\d.]", "");   // 提取单位部分

        double iac01 = Double.parseDouble(valuePart);

        if (unitPart.equals(" Ω")) {
            return iac01 * 1000; // 1Ω = 1000mΩ
        } else if (unitPart.equals(" KΩ")) {
            return iac01 * 1000000; // 1KΩ = 1000000mΩ
        } else {
            return iac01; // 默认已经是 mΩ
        }
    }*/

    // 辅助方法：格式化平均值结果
    private String formatResult(double averageInMOhm) {
        String unit;
        double value;

            unit = " mΩ";
            value = averageInMOhm;

        return String.format("%.2f", value) + unit;
    }


/*    private String formatResult(double averageInMOhm) {
        String unit;
        double value;

        if (averageInMOhm >= 1000000) {
            unit = " KΩ";
            value = averageInMOhm / 1000000;
        } else if (averageInMOhm >= 1000) {
            unit = " Ω";
            value = averageInMOhm / 1000;
        } else {
            unit = " mΩ";
            value = averageInMOhm;
        }

        return String.format("%.2f", value) + unit;
    }*/


    private XYDataItem findClosestDataItem(XYSeries series, double xValue) {
        XYDataItem closestItem = null;
        double minDistance = Double.MAX_VALUE;

        for (int i = 0; i < series.getItemCount(); i++) {
            XYDataItem item = series.getDataItem(i);
            double distance = Math.abs(item.getXValue() - xValue);
            if (distance < minDistance) {
                minDistance = distance;
                closestItem = item;
            } else {
                break; // 数据是按顺序存储的，一旦距离增加，就可以停止搜索
            }
        }
        return closestItem;
    }


    //修改 resetChartData() 方法，添加一个步骤来计算整个数据集的最大值，然后将 Y 轴的范围锁定在 [0, maxYValue]。
    private void resetChartData() {
        SwingUtilities.invokeLater(() -> {
            series1.clear();
            series2.clear();
            series3.clear();
            rightSeries.clear();

            // 重置 timeSeconds 和 maxYValue
            timeSeconds = 0;
            maxYValue = 1;

            updateXAxis();
            lockYAxisRange();
        });
    }



    private void stopListening() {
        dianzu111.getDocument().removeDocumentListener(dianzuListener);
        dianzu222.getDocument().removeDocumentListener(dianzuListener);
        dianzu333.getDocument().removeDocumentListener(dianzuListener);

        dianzu444.getDocument().removeDocumentListener(dianzuListener);
    }


    private void startListening() {
        dianzu111.getDocument().addDocumentListener(dianzuListener);
        dianzu222.getDocument().addDocumentListener(dianzuListener);
        dianzu333.getDocument().addDocumentListener(dianzuListener);

        dianzu444.getDocument().addDocumentListener(dianzuListener);
    }

    public void resetForNextEnter() {

        // 停止模拟测试定时器
        if (testTimer != null && testTimer.isRunning()) {
            testTimer.stop();
        }

        // 停止正式测试任务
        if (currentTestFuture != null && !currentTestFuture.isDone()) {
            currentTestFuture.cancel(true);
        }

        // 尝试停止监听，避免重复监听
        try {
            stopListening();
        } catch (Exception ignored) {
        }

        // 尝试关闭串口
        try {
            ruAndwone.closeSerialPort();
        } catch (Exception ignored) {
        }

        loadedTestBianHao = "";

        // 重置运行状态
        isTestingStarted = false;
        isDataUnsaved = false;
        isTimerRunning = false;
        loadingHistory = false;

        processed.set(false);
        processed2.set(false);
        processedjishi1.set(false);
        processed111000.set(false);
        processed555111.set(false);

        lastTime = 0;
        lastTime111111 = 0;
        lastTime111000 = 0;
        lastTime555111 = 0;
        lastTriggerTime = 0;

        roundedFrequency = 0;
        roundedFrequency111 = 0;

        pureValue1 = 0;
        pureValue2 = 0;
        pureValue3 = 0;
        dianliu1Value1 = 0;

        averageInMOhm1 = "";
        averageInMOhm2 = "";
        averageInMOhm3 = "";

        value111 = 0;
        value222 = 0;
        value333 = 0;
        value444 = 0;

        currentValue111 = "";
        currentValue222 = "";
        currentValue333 = "";
        currentValue444 = "";

        timeSeconds = 0;
        loadedPointCount = 0;
        maxYValue = 1;

        SwingUtilities.invokeLater(() -> {

            if (dianzu111 != null) dianzu111.setText("");
            if (dianzu222 != null) dianzu222.setText("");
            if (dianzu333 != null) dianzu333.setText("");
            if (dianzu444 != null) dianzu444.setText("");
            if (dianzu444NO != null) dianzu444NO.setText("");

            if (countTest != null) countTest.setText("");
            if (suduTest != null) suduTest.setText("");

            if (testBianHao != null) {
                testBianHao.setSelectedIndex(0);
            }

            if (beginCLBtn != null) {
                beginCLBtn.setText("开始测试");
                beginCLBtn.setFont(new Font("楷体", Font.PLAIN, 22));
                if (beginCLBtn instanceof StyledButton) {    ((StyledButton) beginCLBtn).setGradientColor(            new Color(3, 39, 206),            new Color(3, 39, 206, 197)    );}
                beginCLBtn.setForeground(Color.WHITE);
            }

            if (btn4 != null) {
                btn4.setText("开始测试");
            }

            if (series1 != null) series1.clear();
            if (series2 != null) series2.clear();
            if (series3 != null) series3.clear();
            if (rightSeries != null) rightSeries.clear();

            if (plot != null) {
                plot.getDomainAxis().setRange(0, 28);
                plot.getRangeAxis().setRange(0, 1);
            }

            if (horizontalScrollBar != null) {
                horizontalScrollBar.setValue(0);
            }

            if (verticalScrollBar != null) {
                verticalScrollBar.setValue(verticalScrollBar.getMaximum() - verticalScrollBar.getVisibleAmount());
            }

            refreshAllcountValue();
            refreshDianzumaxValue();

            revalidate();
            repaint();
        });
    }


    private DocumentListener dianzuListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateChart();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }

        private void updateChart() {
            try {
                String text111 = dianzu111.getText().trim();
                String text222 = dianzu222.getText().trim();
                String text333 = dianzu333.getText().trim();

                String text444 = dianzu444.getText().trim();

                // 确保所有文本框都有值
                if (!text111.isEmpty() && !text222.isEmpty() && !text333.isEmpty()&& !text444.isEmpty()) {
                    double newValue111 = extractNumber(text111);
                    double newValue222 = extractNumber(text222);
                    double newValue333 = extractNumber(text333);
                    double newValue444 = extractNumber(text444);


                    // 为每个值添加一个微小的随机数

                    // 生成一个0.0001到0.0010之间的数值，并四舍五入到小数点后4位
                    double randomFactor = 0.0001 * Math.floor(Math.random() * 10 + 1);
                    double      rand1omFactor = Math.round(randomFactor * 10000.0) / 10000.0;// 保留小数点后4位

                    // 比较当前值与之前的值+随机因子的差异
                    if (Math.abs(newValue111 -   rand1omFactor) > 0.0000001 ||
                            Math.abs(newValue222 -  rand1omFactor) > 0.0000001 ||
                            Math.abs(newValue333 - rand1omFactor) > 0.0000001) {

        /*            // 检查新值是否与之前的值不同
                    if (!text111.equals(currentValue111 +rand1omFactor) &&
                            !text222.equals(currentValue222+rand1omFactor) &&
                            !text333.equals(currentValue333  +rand1omFactor)) {*/

   /*                     System.out.println("Comparing Group 1: text111 = " + text111 + ", currentValue111 + rand1omFactor = " + (currentValue111 + rand1omFactor));
                        System.out.println("Comparing Group 2: text222 = " + text222 + ", currentValue222 + rand1omFactor = " + (currentValue222 + rand1omFactor));
                        System.out.println("Comparing Group 3: text333 = " + text333 + ", currentValue333 = " + currentValue333);
*/

                        //------------------------- ---------- ---------- ----------     没有右侧Y轴的范围改变    ------------------ ----------
                        // 更新 maxYValue 确保 Y 轴范围足够
                        maxYValue = Math.max(maxYValue, Math.max(newValue111, Math.max(newValue222, newValue333)));
                        lockYAxisRange(); // 调整 Y 轴范围

                        SwingUtilities.invokeLater(() -> {

                            // 在相同的 timeSeconds 值下更新三个曲线
                            series1.add(timeSeconds, newValue111);
                            series2.add(timeSeconds, newValue222);
                            series3.add(timeSeconds, newValue333);

                            // 更新电流的右侧Y轴
                            rightSeries .add(timeSeconds, newValue444);


                            // 更新完成后，x 轴增加一个单位
                            timeSeconds = timeSeconds +2;

                            updateXAxis(); // 更新 x 轴范围
                            gradientPanel.revalidate();
                            gradientPanel.repaint();
                        });

                        // 记录当前值为最新的输入值
                        currentValue111 = text111;
                        currentValue222 = text222;
                        currentValue333 = text333;
                        currentValue444 = text444;

                    }
                }
            } catch (NumberFormatException ex) {
                // 忽略非数字输入
            }
        }
    };


    private void lockYAxisRange() {
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // 确保 maxYValue 是正数
        double adjustedMaxYValue = maxYValue > 0 ? maxYValue : 1;

        // 设置 Y 轴范围 [0, adjustedMaxYValue]
        yAxis.setRange(0, adjustedMaxYValue);
    }


    private boolean isTestBianHaoExists(String testBianHaoValue) {
        if (testBianHaoValue == null || testBianHaoValue.trim().isEmpty()) {
            return false;
        }

        String sql = "SELECT COUNT(*) FROM test_results WHERE test_bianhao = ?";

        try (Connection connection = JdbcDeal.getConnection();
             PreparedStatement psmt = connection.prepareStatement(sql)) {

            psmt.setString(1, testBianHaoValue.trim());

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(
                    this,
                    "检查测试编号是否存在失败：" + ex.getMessage(),
                    "数据库错误",
                    JOptionPane.ERROR_MESSAGE
            );
        }

        return false;
    }


    // 提取文本中的数字部分
    private double extractNumber(String text) {
        //  String number = text.replaceAll("[^\\d.]", "");  // 移除非数字和小数点的字符
        String number = text.replaceAll("[^\\d\\.\\-]", "");  // 移除除数字、点、小数点外的字符

        if (number.isEmpty()) {
            throw new NumberFormatException("No numeric value found");
        }
        return Double.parseDouble(number);
    }

    private void updateXAxis() {
        XYPlot plot = (XYPlot) chart.getPlot();
        double windowSize = 28;  // 设置可视窗口的大小，例如28秒      x轴范围
        double lowerBound = Math.max(0, timeSeconds - windowSize);
        final   double upperBound = timeSeconds;

        // 确保 upperBound 总是大于 lowerBound，避免异常
        if (upperBound - lowerBound < 1) {
            // 在这种情况下，我们只能直接使用 upperBound + 1 作为范围，不需要再声明它为 final
            plot.getDomainAxis().setRange(lowerBound, lowerBound + 1);
        } else {
            plot.getDomainAxis().setRange(lowerBound, upperBound);
        }


        // 将 X 轴的最大值 实时显示在输入框中
        SwingUtilities.invokeLater(() -> countTest.setText(String.valueOf((int) upperBound)));


        // 获取输入框的值并安全转换
        newXValue = countTest.getText();
        int parsedValue = 0;  // 默认值

        // 安全解析数字
        if (newXValue != null && !newXValue.trim().isEmpty()) {
            try {
                parsedValue = Integer.parseInt(newXValue.trim());
            } catch (NumberFormatException e) {
                // 记录错误或使用默认值
                System.err.println("无效的数字格式: " + newXValue);
                parsedValue = (int) upperBound;  // 使用当前时间值作为后备
            }
        } else {
            // 处理空值情况
            parsedValue = (int) upperBound;  // 使用当前时间值作为默认
        }

        // 实时更新 X 轴的最大值
        updateXAxisMaxValue(parsedValue);
    }

    //25.6.20 修改  获取的值为空
 /*        newXValue = countTest.getText();

        // 实时更新 X 轴的最大值
        updateXAxisMaxValue(Integer.parseInt(newXValue));
    }*/


    // 假设你有一个方法来更新 countTest 的值

    private void updateXAxisMaxValue(int maxXValue) {
        // 更新水平滚动条的最大值和当前值
        horizontalScrollBar.setMaximum(maxXValue);
        horizontalScrollBar.setVisibleAmount(28);
        horizontalScrollBar.setValue(maxXValue - 28);  // 确保滚动条显示在最右侧

        // 更新图表的 X 轴范围
        XYPlot plot = (XYPlot) chart.getPlot();
        double windowSize = 28;  // 设置可视窗口的大小，例如28次测量
        double lowerBound = Math.max(0, maxXValue - windowSize);
        double upperBound = maxXValue;

        // 确保 upperBound 总是大于 lowerBound，避免异常
        if (upperBound <= lowerBound) {
            upperBound = lowerBound + 1.0; // 人为增加 upperBound 确保范围长度为正数
        }


        // 设置 X 轴的范围
        plot.getDomainAxis().setRange(lowerBound, upperBound);

        // 刷新图表和滚动条
        horizontalScrollBar.revalidate();
        horizontalScrollBar.repaint();
    }

    public boolean responseIsValid(byte[] response) {
        if (response.length < 5) { // 基本长度检查
            return false;
        }
        int responseCrc = ((response[response.length - 1] & 0xff) << 8) | (response[response.length - 2] & 0xff);
        int calcCrc = calculateCRC(response, response.length - 2);
        if (calcCrc != responseCrc) {
            System.out.println("CRC 校验失败");
        }
        return responseCrc == calcCrc;
    }

    public static int calculateCRC(byte[] data, int length) {
        int crc = 0xFFFF;
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
        }
        return crc;
    }


    private static JTextField createCustomTextFieldNO(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setBounds(x, y, width, height);
        textField.setVisible(false);  // 设置文本框不可见
        return textField;
    }


    private static JTextField createCustomTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setForeground(Color.GREEN);
        textField.setBackground(Color.DARK_GRAY);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("楷体", Font.PLAIN, 36));
        textField.setBounds(x, y, width, height);
        textField.setBorder(new LineBorder(Color.GREEN, 2));
        return textField;
    }


    // 现在在您的其他代码中，可以使用StyledButton类来创建按钮
    public JButton createStyledButton(String text, Font font, Color startColor, Color endColor) {
        StyledButton button = new StyledButton(text, startColor, endColor);

        button.setFont(font);
        button.setForeground(new Color(234, 239, 234, 255));
        button.setFocusPainted(false);
        //  button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setContentAreaFilled(false);
        button.setOpaque(false);

        // 创建自定义边框
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setMouseOver(true);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setMouseOver(false);
            }
        });
        return button;
    }

    // 将StyledButton定义为独立的公有类
    public class StyledButton extends JButton {
        private boolean mouseOver = false;
        private Color currentUpperColor;
        private Color currentLowerColor;

        public StyledButton(String text, Color startColor, Color endColor) {
            super(text);
            this.currentUpperColor = startColor;
            this.currentLowerColor = endColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            GradientPaint gp = new GradientPaint(
                    0, 0, mouseOver ? currentUpperColor.brighter() : currentUpperColor,
                    0, getHeight(), mouseOver ? currentLowerColor.darker() : currentLowerColor);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            super.paintComponent(g);
            g2.dispose();
        }

        public void setMouseOver(boolean mouseOver) {
            this.mouseOver = mouseOver;
            repaint();
        }

        public void setGradientColor(Color startColor, Color endColor) {
            this.currentUpperColor = startColor;
            this.currentLowerColor = endColor;
            repaint();
        }
    }


    private void placeButton(JPanel panel, JButton button, int x, int y, int width, int height) {
        button.setBounds(x, y, width, height);
        panel.add(button);
    }


    //  日期选择器
    private static void updateTextFieldFont(JDatePickerImpl datePicker, Font font) {
        JFormattedTextField textField = datePicker.getJFormattedTextField();
        if (textField != null) {
            textField.setFont(font);
        }
    }


    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int height = getHeight();
            Color color1 = new Color(90, 237, 243);
            Color color2 = new Color(173, 216, 230, 0);
            GradientPaint gp1 = new GradientPaint(0, 100, color1, 0, height / 4, color2);
            Color color3 = new Color(72, 231, 231, 52);
            GradientPaint gp2 = new GradientPaint(0, height / 2, color2, 0, height, color3);
            g2d.setPaint(gp1);
            g2d.fillRect(0, 0, getWidth(), height / 4);
            g2d.setPaint(gp2);
            g2d.fillRect(0, height / 2, getWidth(), height / 2);
        }

    }

}
