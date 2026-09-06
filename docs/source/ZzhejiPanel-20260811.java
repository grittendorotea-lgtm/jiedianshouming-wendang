package com.ytzg.sealer.swing;

import com.formdev.flatlaf.FlatLightLaf;

import com.ytzg.sealer.comm.caidanduqu.SixMeterInstrumentReader;
import com.ytzg.sealer.entity.Result;
import com.ytzg.sealer.plc.PDialog.PDialog;
import com.ytzg.sealer.comm.Sleep;
import com.ytzg.sealer.db.ExecuteCommon;
import com.ytzg.sealer.db.JdbcDeal;
import com.ytzg.sealer.entity.TemporaryData;
import com.ytzg.sealer.plc.*;
import com.ytzg.sealer.plc.test.DianZu00000;
import com.ytzg.sealer.tongxun.GetAclAndDianzu;
import com.ytzg.sealer.tongxun.RRu;
import com.ytzg.sealer.tongxun.RRuANDWone;
import com.ytzg.sealer.tongxun.Wone;
import com.ytzg.sealer.tool.LoadingGifDialog;
import com.ytzg.sealer.tool.ValidationTool;
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
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

import org.jfree.chart.ChartMouseListener;
import org.jfree.ui.RectangleEdge;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import java.util.function.Supplier;

public class ZzhejiPanel extends JPanel {

    private final Map<String, Long> lastUpdateTimes = new HashMap<>();
    private final Map<String, JTextField> labelMap = new HashMap<>();
    private final Map<String, Queue<Double>> frequencyBufferMap = new HashMap<>();
    private static final int MOVING_AVG_WINDOW = 5;

    private   int  allcount111   = 0;
    private   int  allcount222   = 0;
    private   int  allcount333   = 0;
    private   int  allcount444   = 0;



    private   long  lastTime111   = 0;
    private   long  lastTime222   = 0;
    private   long  lastTime333   = 0;
    private   long  lastTime444   = 0;


    private   long  lastTime111111 = 0;
    private   long  lastTime111000= 0;
    private   long  lastTime555111= 0;


    private   long  lastTime222111   = 0;
    private   long  lastTime222000= 0;
    private   long  lastTime666111= 0;



    private   long  lastTime333111  = 0;
    private   long  lastTime333000= 0;
    private   long  lastTime777111= 0;


    private   long  lastTime444111   = 0;
    private   long  lastTime444000= 0;
    private   long  lastTime888111= 0;






    private   long  lastTime555   = 0;
    private   long  lastTime666   = 0;
    private   long  lastTime777  = 0;
    private   long  lastTime888   = 0;




    private  long   roundedFrequency111 ;
    private  long   roundedFrequency222 ;
    private  long   roundedFrequency333 ;
    private  long   roundedFrequency444 ;


    private double dianzu1Value1; // 提前初始化
    private double dianzu2Value2; // 提前初始化
    private double dianzu3Value3; // 提前初始化
    private double dianzu4Value4; // 提前初始化

    private double dianliu1Value1; // 提前初始化
    private double dianliu2Value2; // 提前初始化
    private double dianliu3Value3; // 提前初始化
    private double dianliu4Value4; // 提前初始化



    // 用于标记是否处理过保存
    private boolean isDataUnsaved = false;
    private boolean isDataUnsaved2 = false;
    private boolean isDataUnsaved3 = false;
    private boolean isDataUnsaved4 = false;



    private boolean isJiejinChaoshi1 = false;
    private boolean isJiejinChaoshi2 = false;
    private boolean isJiejinChaoshi3 = false;
    private boolean isJiejinChaoshi4 = false;







    AtomicBoolean processed2 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed22 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed222 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed2222 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1



    AtomicBoolean processedjishi1 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processedjishi2  = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processedjishi3  = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processedjishi4  = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1



    AtomicBoolean processed111000 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed222000 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed333000 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed444000 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1




    AtomicBoolean processed555111 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed666111 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed777111 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1
    AtomicBoolean processed888111 = new AtomicBoolean(false);  // 用于标记是否处理过 value == 1





    private boolean dushu1 = false;
    private boolean dushu2 = false; // 记录是否执行过第一次条件

    private boolean dushu11 = false;
    private boolean dushu22 = false; // 记录是否执行过第一次条件

    private boolean dushu111 = false;
    private boolean dushu222 = false; // 记录是否执行过第一次条件

    private boolean dushu1111 = false;
    private boolean dushu2222 = false; // 记录是否执行过第一次条件


    private boolean bothConditionsProcessed = false;

    private boolean firstConditionExecuted = false; // 记录是否执行过第一次条件





    private IndexFrame parentFrame;
    private   String newXValue = "1"  ,    newXValue2 = "1"   ,    newXValue3 = "1"   ,    newXValue4 = "1"        ;
    private JScrollBar horizontalScrollBar1 , horizontalScrollBar2, horizontalScrollBar3, horizontalScrollBar4;

    private JScrollBar verticalScrollBar1 ,verticalScrollBar2,verticalScrollBar3,verticalScrollBar4        ;

    private XYPlot plot ,plot2,plot3,plot4    ;

    private int timeSeconds1 = 0; // 时间秒作为X轴
    private int timeSeconds2 = 0; // 时间秒作为X轴
    private int timeSeconds3 = 0; // 时间秒作为X轴
    private int timeSeconds4 = 0; // 时间秒作为X轴





    // 类成员变量声明
    private double minYValue1 = Double.MAX_VALUE;
    private double maxYValue1 = Double.MIN_VALUE;

    private double minYValue2 = Double.MAX_VALUE;
    private double maxYValue2 = Double.MIN_VALUE;

    private double minYValue3 = Double.MAX_VALUE;
    private double maxYValue3 = Double.MIN_VALUE;


    private double minYValue4 = Double.MAX_VALUE;
    private double maxYValue4 = Double.MIN_VALUE;


    private JButton     btn4, btn44,btn444,btn4444,    baoCunBtn1,beginCLBtn1  ,exitbtn1,     baoCunBtn2,beginCLBtn2 ,exitbtn2,  baoCunBtn3,beginCLBtn3 ,exitbtn3  ,  baoCunBtn4,beginCLBtn4  ,exitbtn4   ,btnclear1,btnclear2,btnclear3,btnclear4   ;
    private JTextField  dianzu1 , dianzu2 , dianzu3 , dianzu4                           ;

    private JTextField  dianzu111,dianzu222,dianzu333,dianzu444  ,dianliu111  ,dianliu222 , dianliu333  ,dianliu444   ,dianliu111NO, dianliu222NO , dianliu333NO  ,dianliu444NO  ,sudu111,sudu222,sudu333,sudu444 ;


    private TemporaryData tempData = new TemporaryData(); // 类成员变量

    private Timer timer;


    private   JLabel labeldianzumax1   ,labeldianzumax2    ,labeldianzumax3    ,labeldianzumax4;

    private double   dianzumax1,dianzumax2,dianzumax3,dianzumax4;
    private int  testcount1,testcount2,testcount3,testcount4;


    private JFreeChart chart ,chart2,chart3,chart4                ;
    private XYSeries series1 ,rightSeries1, series2 ,rightSeries2 ,  series3 ,rightSeries3, series4 ,rightSeries4               ; // 创建三个 XYSeries


    private  GradientPanel gradientPanel;

    private    double valueR111, valueL111, valueR222, valueL222 , valueR333, valueL333, valueR444, valueL444 ;



    private static boolean isTestingStarted1 = false; // 类变量，跟踪测试是否已开始
    private static boolean isTestingStarted2 = false; // 类变量，跟踪测试是否已开始
    private static boolean isTestingStarted3 = false; // 类变量，跟踪测试是否已开始
    private static boolean isTestingStarted4 = false; // 类变量，跟踪测试是否已开始


    private ExecutorService executorService = Executors.newSingleThreadExecutor();


    private Future<?> currentTestFuture1 = null;  /*保存当前提交的任务的 Future 对象。Future 对象可以用来检查任务是否完成、取消任务以及获取任务的执行结果*/
    private Future<?> currentTestFuture2 = null;  /*保存当前提交的任务的 Future 对象。Future 对象可以用来检查任务是否完成、取消任务以及获取任务的执行结果*/
    private Future<?> currentTestFuture3 = null;  /*保存当前提交的任务的 Future 对象。Future 对象可以用来检查任务是否完成、取消任务以及获取任务的执行结果*/
    private Future<?> currentTestFuture4 = null;  /*保存当前提交的任务的 Future 对象。Future 对象可以用来检查任务是否完成、取消任务以及获取任务的执行结果*/




    private RRuANDWone ruAndwone    =   new RRuANDWone("COM4");   ;
    // COM5统一读取：01~04电流表，05~08电阻表
    private final SixMeterInstrumentReader instrumentReader = new SixMeterInstrumentReader("COM5");


/*
    private RRu rRu = new RRu("COM5");  ;
    private Wone wone = new Wone("COM5");
*/



    private DDianZuOnce ddianzuonce = new DDianZuOnce();

    private DianZu00000 dianZu = new DianZu00000();

    private DianZu001 ddianzuonce1 = new DianZu001();
    private DianZu002 ddianzuonce2 = new DianZu002();
    private DianZu003 ddianzuonce3 = new DianZu003();
    private JiaoL JiaoL = new JiaoL();


    private    JTextField  countTest1,  testBianHao1  , testBianHao2 , testBianHao3 , testBianHao4   ,                countTest2  ,          countTest3  ,          countTest4   ,allcountTest1,allcountTest2 , allcountTest3  ,  allcountTest4   ;
    private JTextField  testBianHao111  , testBianHao222 , testBianHao333 , testBianHao444                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           ;

    private JDatePickerImpl testTime1,testTime2, testTime3 ,  testTime4   ;



    private Timer testTimer;
    private Random random = new Random();

    private   boolean isTimerRunning1 = false;
    private   boolean isTimerRunning2 = false;
    private   boolean isTimerRunning3 = false;
    private   boolean isTimerRunning4 = false;

    // 添加变量来保存当前的输入框值
    private String currentValue111 = "";
    private String currentValue222 = "";
    private String currentValue333 = "";
    private String currentValue444 = "";


    //使用并行读数据
//    private ExecutorService executorServicebingxing = Executors.newFixedThreadPool(8);
/*
    private ExecutorService executorServicebingxing1 = Executors.newFixedThreadPool(2);
    private ExecutorService executorServicebingxing2 = Executors.newFixedThreadPool(2);
    private ExecutorService executorServicebingxing3 = Executors.newFixedThreadPool(2);
    private ExecutorService executorServicebingxing4 = Executors.newFixedThreadPool(2);
*/


    ExecutorService executorServicebingxing3 = Executors.newSingleThreadExecutor();
    ExecutorService executorServicebingxing4 = Executors.newSingleThreadExecutor();

    ExecutorService executorServicebingxing1 = Executors.newSingleThreadExecutor();
    ExecutorService executorServicebingxing2 = Executors.newSingleThreadExecutor();



    private  JTabbedPane tabbedPane   ;

    private   JPanel panel1 , panel2 , panel3,panel4;


    // 全局变量，决定显示哪张图片
    private static boolean isImageStop1Displayed = false;
    private static boolean isImageY2Displayed = false;
    private static boolean isImageN2Displayed = false;



    private static ImageIcon imageStop = new ImageIcon(ValidationTool.class.getResource("/com/ytzg/sealer/swing/img2/anlv2.png"));
    private static ImageIcon imageY = new ImageIcon(ValidationTool.class.getResource("/com/ytzg/sealer/swing/img2/liangLv2.png"));
    private static ImageIcon imageN = new ImageIcon(ValidationTool.class.getResource("/com/ytzg/sealer/swing/img2/hong2.png"));

    private JLabel label11111  ,label22222    ,label33333   ,label44444         ;


    public ZzhejiPanel(IndexFrame parentFrame) {
        this.parentFrame = parentFrame;

        // 启动COM5统一仪表读取器。
        // startReading()内部有running判断，重复调用也不会重复打开串口。
        instrumentReader.startReading();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setLayout(null);
        setBounds(0, 0, 1620, 950);
        setVisible(false);



        // 初始化 dianzumax 值

        // 从数据库中获取 dianzumax 值
        dianzumax1 = ExecuteCommon.getDianzumaxValue1();
        dianzumax2= ExecuteCommon.getDianzumaxValue2();
        dianzumax3 = ExecuteCommon.getDianzumaxValue3();
        dianzumax4 = ExecuteCommon.getDianzumaxValue4();




        gradientPanel = new GradientPanel();
        gradientPanel.setLayout(null);
        gradientPanel.setBounds(0, 0, this.getWidth(), this.getHeight());
        add(gradientPanel);


        //    gradientPanel.add(指示器);


        UIManager.put("OptionPane.messageFont", new Font("宋体", Font.BOLD, 24));
        UIManager.put("OptionPane.messageForeground",new Color(0, 0, 128));




        JLabel label1 = addLabel("接点1状态", 730, 10);
        gradientPanel.add(label1);


        JLabel label2 = addLabel("接点2状态", 900, 10);
        gradientPanel.add(label2);


        JLabel label3 = addLabel("接点3状态", 1070, 10);
        gradientPanel.add(label3);


        JLabel label4 = addLabel("接点4状态", 1240, 10);
        gradientPanel.add(label4);



        label11111 = new JLabel();
        label11111.setBounds(824, 0, 50, 50);
        updateImage(label11111, "stop");
        gradientPanel.add(label11111);


        label22222 = new JLabel();
        label22222.setBounds(994, 0, 50, 50);
        updateImage(label22222, "stop");
        gradientPanel.add(label22222);


        label33333 = new JLabel();
        label33333.setBounds(1164, 0, 50, 50);
        updateImage(label33333,"stop");
        gradientPanel.add(label33333);


        label44444 = new JLabel();
        label44444.setBounds(1334, 0, 50, 50);
        updateImage(label44444, "stop");
        gradientPanel.add(label44444);










        // 创建 JTabbedPane
        tabbedPane = new JTabbedPane();

// 设置 JTabbedPane 的位置和大小
        tabbedPane.setBounds(2, 20, 1620, 910);  // 根据您的界面调整大小和位置


// 创建选项卡的面板
        panel1= new GradientPanel();
        panel1.setLayout(null);  // 根据需要设置布局

        panel2 = new GradientPanel();
        panel2.setLayout(null);

        panel3= new GradientPanel();
        panel3.setLayout(null);  // 根据需要设置布局

        panel4 = new GradientPanel();
        panel4.setLayout(null);





        // 创建自定义标签用于选项卡标题
        JLabel tabTitle1 = new JLabel("监测设备1");
        tabTitle1.setFont(new Font("楷体", Font.BOLD, 15));  // 设置字体样式和大小
        tabTitle1.setForeground(new Color(18, 78, 225));  // 设置文字颜色


        JLabel tabTitle2 = new JLabel("监测设备2");
        tabTitle2.setFont(new Font("楷体", Font.BOLD, 15));  // 同上
        tabTitle2.setForeground(new Color(79, 192, 177));   // 改变颜色


        // 创建自定义标签用于选项卡标题
        JLabel tabTitle3 = new JLabel("监测设备3");
        tabTitle3.setFont(new Font("楷体", Font.BOLD, 15));  // 设置字体样式和大小
        tabTitle3.setForeground(new Color(160, 79, 183));  // 设置文字颜色


        JLabel tabTitle4 = new JLabel("监测设备4");
        tabTitle4.setFont(new Font("楷体", Font.BOLD, 15));  // 同上
        tabTitle4.setForeground(new Color(77, 175, 82));   // 改变颜色




        tabbedPane.addTab(null, panel1);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabTitle1);  // 设置自定义标题组件

        tabbedPane.addTab(null, panel2);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabTitle2);

        tabbedPane.addTab(null, panel3);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabTitle3);  // 设置自定义标题组件

        tabbedPane.addTab(null, panel4);
        tabbedPane.setTabComponentAt(tabbedPane.getTabCount() - 1, tabTitle4);


        gradientPanel.add(tabbedPane);





        //---------------------------------------------------------------监测设备1------------------------------------------------------------------------------------------------------------------------------------





        // 1. 定义面板集合和分隔线参数
        List<JPanel> panels = Arrays.asList(panel1, panel2, panel3, panel4);
        int[][] separators = {{53,1900,3}, {133,1900,3}, {169,1900,3}, {766,1450,5}};
        // 2. 单行流式操作创建所有分隔线
        panels.forEach(p -> Arrays.stream(separators).forEach(s -> p.add(new JSeparator() {{
            setBounds(0, s[0], s[1], s[2]);
        }})));




        //    JLabel biaoti000 = new JLabel("<html>注意: 可设置使设备停止的电阻最大值，当前最大值为</html>");   // 改为：
        //最大值的文字
        Arrays.asList(panel1, panel2, panel3, panel4).forEach(panel -> panel.add(createNoteLabel()));




        //电阻最大值的值
        labeldianzumax1 = new JLabel(dianzumax1+"mΩ");//<br>
        maxRTextField(labeldianzumax1);
        panel1.add(labeldianzumax1);

        labeldianzumax2 = new JLabel(dianzumax2+"mΩ");//<br>
        maxRTextField(labeldianzumax2);
        panel2.add(labeldianzumax2);

        labeldianzumax3 = new JLabel(dianzumax3+"mΩ");//<br>
        maxRTextField(labeldianzumax3);
        panel3.add(labeldianzumax3);


        labeldianzumax4 = new JLabel(dianzumax4+"mΩ");//<br>
        maxRTextField(labeldianzumax4);
        panel4.add(labeldianzumax4);





        // 1. 定义标签配置参数
        Object[][] labelConfig = {
                {"测试日期:", 285, 13, 200, 30},
                {"测试编号:", 20, 13, 200, 30},
                {"监测次数:", 580, 13, 200, 30},
                {"累计次数:", 792, 4, 206, 50}


        };

// 3. 批量添加至所有面板
        List<JPanel> panels2 = Arrays.asList(panel1, panel2, panel3, panel4);
        panels2.forEach(panel -> {
            Arrays.stream(labelConfig).forEach(config ->
                    panel.add(createLabel(
                            (String)config[0],
                            (int)config[1],
                            (int)config[2],
                            (int)config[3],
                            (int)config[4]
                    ))
            );
        });






// 测试编号
        testBianHao1 = createBianhaoTextField();
        testBianHao2 = createBianhaoTextField();
        testBianHao3 = createBianhaoTextField();
        testBianHao4 = createBianhaoTextField();
        panel1.add(testBianHao1);
        panel2.add(testBianHao2);
        panel3.add(testBianHao3);
        panel4.add(testBianHao4);




// 测试时间
        testTime1 = createDatePicker(375, 16);
        testTime2 = createDatePicker(375, 16);
        testTime3 = createDatePicker(375, 16);
        testTime4 = createDatePicker(375, 16);



// 手动添加到面板
        panel1.add(testTime1);
        panel2.add(testTime2);
        panel3.add(testTime3);
        panel4.add(testTime4);


// 监测次数
        countTest1 = createCountTextField();
        countTest2 = createCountTextField();
        countTest3 = createCountTextField();
        countTest4 = createCountTextField();
        panel1.add(countTest1);
        panel2.add(countTest2);
        panel3.add(countTest3);
        panel4.add(countTest4);



        allcountTest1 = allcreateCountTextField();
        allcountTest2 = allcreateCountTextField();
        allcountTest3 = allcreateCountTextField();
        allcountTest4 = allcreateCountTextField();
        panel1.add(allcountTest1);
        panel2.add(allcountTest2);
        panel3.add(allcountTest3);
        panel4.add(allcountTest4);


        allcountTest1.setText(String.valueOf(ExecuteCommon.getAllcountValue1(1)));
        allcountTest2.setText(String.valueOf(ExecuteCommon.getAllcountValue1(2)));
        allcountTest3.setText(String.valueOf(ExecuteCommon.getAllcountValue1(3)));
        allcountTest4.setText(String.valueOf(ExecuteCommon.getAllcountValue1(4)));







        Font buttonFont = new Font("华文楷体", Font.PLAIN, 22);

        btn4= createStyledButton("充值",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));

        //      placeButton(panel1, btn4, 1100, 80, 100, 42);
        btn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 每次点击按钮时，更新 dianzumax 的值
                double dianzum1ax = ExecuteCommon.getDianzumaxValue1(); // 从数据库中获取最新的 dianzumax 值

                if (isTimerRunning1) {
                    // 如果计时器正在运行，停止计时器并将状态设置为未运行
                    if (testTimer != null) {
                        testTimer.stop();
                    }
                    isTimerRunning1 = false;
                    btn4.setText("充值"); // 恢复按钮的文本为 "开始测试"
                } else {
                    // 如果计时器未运行，启动计时器并将状态设置为运行中
                    testTimer = new Timer((int) 0.05, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SwingUtilities.invokeLater(() -> {
                                valueR111 = generateRandomValue();

                                valueL111 = generateRandomValue();

                                dianzu111.setText(null);          dianliu111.setText(null);

                                dianzu111.setText(String.format("%.2f mΩ", valueR111));

                                dianliu111.setText(String.format("%.2f mΩ", valueL111));


                                if (valueR111 > dianzum1ax ) {
                                    System.out.println("测试数据中有大于"+dianzum1ax+"的值"+"该值为"+valueR111);
                                }

                            });
                        }
                    });

                    testTimer.start();
                    isTimerRunning1 = true;
                    btn4.setText("停止测试"); // 更新按钮的文本为 "停止测试"
                }
            }
        });


        btn44= createStyledButton("充值",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        //    placeButton(panel2, btn44, 1100, 80, 100, 42);
        btn44.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 每次点击按钮时，更新 dianzumax 的值
                double dianzum1ax = ExecuteCommon.getDianzumaxValue2(); // 从数据库中获取最新的 dianzumax 值

                if (isTimerRunning2) {
                    // 如果计时器正在运行，停止计时器并将状态设置为未运行
                    if (testTimer != null) {
                        testTimer.stop();
                    }
                    isTimerRunning2 = false;
                    btn44.setText("开始测试"); // 恢复按钮的文本为 "开始测试"
                } else {
                    // 如果计时器未运行，启动计时器并将状态设置为运行中
                    testTimer = new Timer(1, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SwingUtilities.invokeLater(() -> {
                                valueR222 = generateRandomValue();

                                valueL222 = generateRandomValue();

                                dianzu222.setText(null);          dianliu222.setText(null);

                                dianzu222.setText(String.format("%.2f mΩ", valueR222));

                                dianliu222.setText(String.format("%.2f mΩ", valueL222));

                                if (valueR222 > dianzum1ax ) {
                                    System.out.println("测试数据中有大于"+dianzum1ax+"的值"+"该值为"+valueR222);
                                }

                            });
                        }
                    });

                    testTimer.start();
                    isTimerRunning2 = true;
                    btn44.setText("停止测试"); // 更新按钮的文本为 "停止测试"
                }
            }
        });


        btn444= createStyledButton("充值",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        //  placeButton(panel3, btn444, 1100, 80, 100, 42);
        btn444.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 每次点击按钮时，更新 dianzumax 的值
                double dianzum1ax = ExecuteCommon.getDianzumaxValue3(); // 从数据库中获取最新的 dianzumax 值

                if (isTimerRunning3) {
                    // 如果计时器正在运行，停止计时器并将状态设置为未运行
                    if (testTimer != null) {
                        testTimer.stop();
                    }
                    isTimerRunning3 = false;
                    btn444.setText("开始测试"); // 恢复按钮的文本为 "开始测试"
                } else {
                    // 如果计时器未运行，启动计时器并将状态设置为运行中
                    testTimer = new Timer(1, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SwingUtilities.invokeLater(() -> {
                                valueR333 = generateRandomValue();

                                valueL333 = generateRandomValue();

                                dianzu333.setText(null);          dianliu333.setText(null);

                                dianzu333.setText(String.format("%.2f mΩ", valueR333));

                                dianliu333.setText(String.format("%.2f mΩ", valueL333));


                                if (valueR333 > dianzum1ax ) {
                                    System.out.println("测试数据中有大于"+dianzum1ax+"的值"+"该值为"+valueR333);
                                }

                            });
                        }
                    });

                    testTimer.start();
                    isTimerRunning3 = true;
                    btn444.setText("停止测试"); // 更新按钮的文本为 "停止测试"
                }
            }
        });


        btn4444= createStyledButton("充值",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        //   placeButton(panel4, btn4444, 1100, 80, 100, 42);
        btn4444.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 每次点击按钮时，更新 dianzumax 的值
                double dianzum1ax = ExecuteCommon.getDianzumaxValue4(); // 从数据库中获取最新的 dianzumax 值

                if (isTimerRunning4) {
                    // 如果计时器正在运行，停止计时器并将状态设置为未运行
                    if (testTimer != null) {
                        testTimer.stop();
                    }
                    isTimerRunning4 = false;
                    btn4444.setText("开始测试"); // 恢复按钮的文本为 "开始测试"
                } else {
                    // 如果计时器未运行，启动计时器并将状态设置为运行中
                    testTimer = new Timer(1, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            SwingUtilities.invokeLater(() -> {
                                valueR444= generateRandomValue();

                                valueL444= generateRandomValue();

                                dianzu444.setText(null);          dianliu444.setText(null);

                                dianzu444.setText(String.format("%.2f mΩ", valueR444));

                                dianliu444.setText(String.format("%.2f mΩ", valueL444));


                                if (valueR444> dianzum1ax ) {
                                    System.out.println("测试数据中有大于"+dianzum1ax+"的值"+"该值为"+valueR444);
                                }

                            });
                        }
                    });

                    testTimer.start();
                    isTimerRunning4 = true;
                    btn4444.setText("停止测试"); // 更新按钮的文本为 "停止测试"
                }
            }
        });









        beginCLBtn1 = createStyledButton("开始测试",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        exitbtn1 = createStyledButton("退出",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        baoCunBtn1 = createStyledButton("保存",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));

        placeButton(panel1, beginCLBtn1, 1000, 5, 150, 42);
        placeButton(panel1, baoCunBtn1, 1165, 5, 100, 42);
        placeButton(panel1, exitbtn1, 1280, 5, 100, 42);


        beginCLBtn2 = createStyledButton("开始测试",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        exitbtn2 = createStyledButton("退出",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        baoCunBtn2 = createStyledButton("保存",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));

        placeButton(panel2, beginCLBtn2, 1000, 5, 150, 42);
        placeButton(panel2, baoCunBtn2, 1165, 5, 100, 42);
        placeButton(panel2, exitbtn2, 1280, 5, 100, 42);


        beginCLBtn3 = createStyledButton("开始测试",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        exitbtn3 = createStyledButton("退出",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        baoCunBtn3 = createStyledButton("保存",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));

        placeButton(panel3, beginCLBtn3, 1000, 5, 150, 42);
        placeButton(panel3, baoCunBtn3, 1165, 5, 100, 42);
        placeButton(panel3, exitbtn3, 1280, 5, 100, 42);


        beginCLBtn4 = createStyledButton("开始测试",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        exitbtn4 = createStyledButton("退出",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        baoCunBtn4 = createStyledButton("保存",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));

        placeButton(panel4, beginCLBtn4, 1000, 5, 150, 42);
        placeButton(panel4, baoCunBtn4, 1165, 5, 100, 42);
        placeButton(panel4, exitbtn4, 1280, 5, 100, 42);






        //批量插入，可以将所有数据一次性插入
        baoCunBtn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                // ---------- 原有保存逻辑 ----------
                JdbcDeal jdbcDeal = new JdbcDeal();

                String allcountTest1Text = allcountTest1.getText();
                ExecuteCommon.updateAllcountValues(allcountTest1Text ,1);


                Date utilTestTimeValue = (Date) testTime1.getModel().getValue();
                String testBianHaoValue1 = testBianHao1.getText();
                String countTestText = countTest1.getText();

                // 数据校验
                if (testBianHaoValue1 == null || testBianHaoValue1.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return ;
                }
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return ;
                }
                if (jdbcDeal.isProductNumberExists06shebeihao01(testBianHaoValue1)) {
                    JOptionPane.showMessageDialog(null, "继电器编号已存在！");
                    return ;
                }


                // 创建并显示 GIF 加载对话框
                LoadingGifDialog loadingDialog = new LoadingGifDialog(
                        (Frame) SwingUtilities.getWindowAncestor(baoCunBtn1)
                );

                loadingDialog.showDialog();

                // 使用 SwingWorker 执行后台保存任务
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {

                            // 批量插入数据
                            java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());
                            List<Object[]> dataToInsert = new ArrayList<>();
                            for (int i = 0; i < series1.getItemCount(); i++) {
                                dataToInsert.add(new Object[]{
                                        sqlTestTimeValue,
                                        testBianHaoValue1,
                                        countTestText,
                                        series1.getX(i).doubleValue(),
                                        series1.getY(i).doubleValue(),
                                        rightSeries1.getX(i).doubleValue(),
                                        rightSeries1.getY(i).doubleValue(),
                                        1
                                });
                            }
                            int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);
                            return rowsInserted > 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            // 关闭加载对话框
                            loadingDialog.hideDialog();

                            // 处理保存结果
                            boolean isSuccess = get();
                            if (isSuccess) {
                                JOptionPane.showMessageDialog(null, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                isDataUnsaved = false;
                            } else {
                                JOptionPane.showMessageDialog(null, "保存失败，请检查数据！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "发生异常: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                worker.execute(); // 启动后台任务
            }
        });


        baoCunBtn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                // ---------- 原有保存逻辑 ----------
                JdbcDeal jdbcDeal = new JdbcDeal();


                String allcountTest2Text = allcountTest2.getText();
                ExecuteCommon.updateAllcountValues(allcountTest2Text ,2);



                Date utilTestTimeValue = (Date) testTime2.getModel().getValue();
                String testBianHaoValue2 = testBianHao2.getText();
                String countTestText = countTest2.getText();

                // 数据校验
                if (testBianHaoValue2 == null || testBianHaoValue2.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return ;
                }
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return ;
                }
                if (jdbcDeal.isProductNumberExists06shebeihao02(testBianHaoValue2)) {
                    JOptionPane.showMessageDialog(null, "继电器编号已存在！");
                    return ;
                }

                // 创建并显示 GIF 加载对话框
                LoadingGifDialog loadingDialog = new LoadingGifDialog(
                        (Frame) SwingUtilities.getWindowAncestor(baoCunBtn2)
                );

                loadingDialog.showDialog();

                // 使用 SwingWorker 执行后台保存任务
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {



                            // 批量插入数据
                            java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());
                            List<Object[]> dataToInsert = new ArrayList<>();
                            for (int i = 0; i < series2.getItemCount(); i++) {
                                dataToInsert.add(new Object[]{
                                        sqlTestTimeValue,
                                        testBianHaoValue2,
                                        countTestText,
                                        series2.getX(i).doubleValue(),
                                        series2.getY(i).doubleValue(),
                                        rightSeries2.getX(i).doubleValue(),
                                        rightSeries2.getY(i).doubleValue(),
                                        2
                                });
                            }
                            int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);
                            return rowsInserted > 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            // 关闭加载对话框
                            loadingDialog.hideDialog();

                            // 处理保存结果
                            boolean isSuccess = get();
                            if (isSuccess) {
                                JOptionPane.showMessageDialog(null, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                isDataUnsaved2 = false;
                            } else {
                                JOptionPane.showMessageDialog(null, "保存失败，请检查数据！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "发生异常: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                worker.execute(); // 启动后台任务
            }
        });



        baoCunBtn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {



                // ---------- 原有保存逻辑 ----------
                JdbcDeal jdbcDeal = new JdbcDeal();

                int allcountTest3Text = Integer.parseInt(allcountTest3.getText());
                ExecuteCommon.updateAllcountValue(allcountTest3Text ,3);



                Date utilTestTimeValue = (Date) testTime3.getModel().getValue();
                String testBianHaoValue2 = testBianHao3.getText();
                String countTestText = countTest3.getText();

                // 数据校验
                if (testBianHaoValue2 == null || testBianHaoValue2.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return ;
                }
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return ;
                }
                if (jdbcDeal.isProductNumberExists06shebeihao03(testBianHaoValue2)) {
                    JOptionPane.showMessageDialog(null, "继电器编号已存在！");
                    return ;
                }


                // 创建并显示 GIF 加载对话框
                LoadingGifDialog loadingDialog = new LoadingGifDialog(
                        (Frame) SwingUtilities.getWindowAncestor(baoCunBtn3)
                );

                loadingDialog.showDialog();


                // 使用 SwingWorker 执行后台保存任务
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {


                            // 批量插入数据
                            java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());
                            List<Object[]> dataToInsert = new ArrayList<>();
                            for (int i = 0; i < series3.getItemCount(); i++) {
                                dataToInsert.add(new Object[]{
                                        sqlTestTimeValue,
                                        testBianHaoValue2,
                                        countTestText,
                                        series3.getX(i).doubleValue(),
                                        series3.getY(i).doubleValue(),
                                        rightSeries3.getX(i).doubleValue(),
                                        rightSeries3.getY(i).doubleValue(),
                                        3
                                });
                            }
                            int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);
                            return rowsInserted > 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            // 关闭加载对话框
                            loadingDialog.hideDialog();

                            // 处理保存结果
                            boolean isSuccess = get();
                            if (isSuccess) {
                                JOptionPane.showMessageDialog(null, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                isDataUnsaved3 = false;
                            } else {
                                JOptionPane.showMessageDialog(null, "保存失败，请检查数据！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "发生异常: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                worker.execute(); // 启动后台任务
            }
        });


        baoCunBtn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                // ---------- 原有保存逻辑 ----------
                JdbcDeal jdbcDeal = new JdbcDeal();


                //保存 累计次数值
                int allcountTest4Text = Integer.parseInt(allcountTest4.getText());
                ExecuteCommon.updateAllcountValue(allcountTest4Text ,4);



                Date utilTestTimeValue = (Date) testTime4.getModel().getValue();
                String testBianHaoValue2 = testBianHao4.getText();
                String countTestText = countTest4.getText();

                // 数据校验
                if (testBianHaoValue2 == null || testBianHaoValue2.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return ;
                }
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return ;
                }
                if (jdbcDeal.isProductNumberExists06shebeihao04(testBianHaoValue2)) {
                    JOptionPane.showMessageDialog(null, "继电器编号已存在！");
                    return ;
                }



                // 创建并显示 GIF 加载对话框
                LoadingGifDialog loadingDialog = new LoadingGifDialog(
                        (Frame) SwingUtilities.getWindowAncestor(baoCunBtn4)
                );

                loadingDialog.showDialog();

                // 使用 SwingWorker 执行后台保存任务
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        try {
                            // 批量插入数据
                            java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());
                            List<Object[]> dataToInsert = new ArrayList<>();
                            for (int i = 0; i < series4.getItemCount(); i++) {
                                dataToInsert.add(new Object[]{
                                        sqlTestTimeValue,
                                        testBianHaoValue2,
                                        countTestText,
                                        series4.getX(i).doubleValue(),
                                        series4.getY(i).doubleValue(),
                                        rightSeries4.getX(i).doubleValue(),
                                        rightSeries4.getY(i).doubleValue(),
                                        4
                                });
                            }
                            int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);
                            return rowsInserted > 0;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            // 关闭加载对话框
                            loadingDialog.hideDialog();

                            // 处理保存结果
                            boolean isSuccess = get();
                            if (isSuccess) {
                                JOptionPane.showMessageDialog(null, "保存成功！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                isDataUnsaved4 = false;
                            } else {
                                JOptionPane.showMessageDialog(null, "保存失败，请检查数据！", "错误", JOptionPane.ERROR_MESSAGE);
                            }
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(null, "发生异常: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                worker.execute(); // 启动后台任务
            }
        });

/*
        baoCunBtn2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                JdbcDeal jdbcDeal = new JdbcDeal();

                String allcountTest2Text = allcountTest2.getText();
                ExecuteCommon.updateAllcountValue(allcountTest2Text ,2);



                Date utilTestTimeValue = (Date) testTime2.getModel().getValue();
                String testBianHaoValue1 = testBianHao2.getText();
                String countTestText = countTest2.getText();


                // 将 java.util.Date 转换为 java.sql.Date
                java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());


                // 检查编号是否为空
                if (testBianHaoValue1 == null || testBianHaoValue1.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return;
                }

                // 检查 countTest 是否为空
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return;
                }
                // 检查编号是否已存在
                if (jdbcDeal.isProductNumberExists06shebeihao02(testBianHaoValue1)) {
                    JOptionPane.showMessageDialog(null, "添加的继电器编号已存在，请输入不同的编号！");
                    return;
                }

                // 批量保存数据
                SwingUtilities.invokeLater(() -> {
                    long startTime = System.currentTimeMillis();

                    // 收集数据
                    List<Object[]> dataToInsert = new ArrayList<>();

                    // 获取 series1 中的所有数据
                    for (int i = 0; i < series2.getItemCount(); i++) {
                        double series1X = series2.getX(i).doubleValue();
                        double series1Y = series2.getY(i).doubleValue();


                        double series4X = rightSeries2.getX(i).doubleValue();
                        double series4Y = rightSeries2.getY(i).doubleValue();



                        // 将每一行数据添加到列表中
                        dataToInsert.add(new Object[]{
                                sqlTestTimeValue, testBianHaoValue1, countTestText,
                                series1X, series1Y,series4X, series4Y,    2
                        });
                    }

                    // 调用批量插入方法
                    int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);

                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "数据保存成功！用时: " + elapsedTime + " ms", "成功", JOptionPane.INFORMATION_MESSAGE);
                        isDataUnsaved2 = false; // 数据已保存
                    } else {
                        JOptionPane.showMessageDialog(null, "数据保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });

*/

/*
        baoCunBtn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JdbcDeal jdbcDeal = new JdbcDeal();

                Date utilTestTimeValue = (Date) testTime3.getModel().getValue();
                String testBianHaoValue1 = testBianHao3.getText();
                String countTestText = countTest3.getText();


                // 将 java.util.Date 转换为 java.sql.Date
                java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());


                // 检查编号是否为空
                if (testBianHaoValue1 == null || testBianHaoValue1.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return;
                }

                // 检查 countTest 是否为空
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return;
                }
                // 检查编号是否已存在
                if (jdbcDeal.isProductNumberExists06shebeihao03(testBianHaoValue1)) {
                    JOptionPane.showMessageDialog(null, "添加的继电器编号已存在，请输入不同的编号！");
                    return;
                }

                // 批量保存数据
                SwingUtilities.invokeLater(() -> {
                    long startTime = System.currentTimeMillis();

                    // 收集数据
                    List<Object[]> dataToInsert = new ArrayList<>();

                    // 获取 series1 中的所有数据
                    for (int i = 0; i < series3.getItemCount(); i++) {
                        double series1X = series3.getX(i).doubleValue();
                        double series1Y = series3.getY(i).doubleValue();


                        double series4X = rightSeries3.getX(i).doubleValue();
                        double series4Y = rightSeries3.getY(i).doubleValue();



                        // 将每一行数据添加到列表中
                        dataToInsert.add(new Object[]{
                                sqlTestTimeValue, testBianHaoValue1, countTestText,
                                series1X, series1Y,series4X, series4Y,    3
                        });
                    }

                    // 调用批量插入方法
                    int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);

                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "数据保存成功！用时: " + elapsedTime + " ms", "成功", JOptionPane.INFORMATION_MESSAGE);
                        isDataUnsaved3 = false; // 数据已保存
                    } else {
                        JOptionPane.showMessageDialog(null, "数据保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });
*/


/*
        baoCunBtn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JdbcDeal jdbcDeal = new JdbcDeal();

                Date utilTestTimeValue = (Date) testTime4.getModel().getValue();
                String testBianHaoValue1 = testBianHao4.getText();
                String countTestText = countTest4.getText();


                // 将 java.util.Date 转换为 java.sql.Date
                java.sql.Date sqlTestTimeValue = new java.sql.Date(utilTestTimeValue.getTime());


                // 检查编号是否为空
                if (testBianHaoValue1 == null || testBianHaoValue1.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请填写测试编号！");
                    return;
                }

                // 检查 countTest 是否为空
                if (countTestText == null || countTestText.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "请测试后再保存！");
                    return;
                }
                // 检查编号是否已存在
                if (jdbcDeal.isProductNumberExists06shebeihao04(testBianHaoValue1)) {
                    JOptionPane.showMessageDialog(null, "添加的继电器编号已存在，请输入不同的编号！");
                    return;
                }

                // 批量保存数据
                SwingUtilities.invokeLater(() -> {
                    long startTime = System.currentTimeMillis();

                    // 收集数据
                    List<Object[]> dataToInsert = new ArrayList<>();

                    // 获取 series1 中的所有数据
                    for (int i = 0; i < series4.getItemCount(); i++) {
                        double series1X = series4.getX(i).doubleValue();
                        double series1Y = series4.getY(i).doubleValue();


                        double series4X = rightSeries4.getX(i).doubleValue();
                        double series4Y = rightSeries4.getY(i).doubleValue();



                        // 将每一行数据添加到列表中
                        dataToInsert.add(new Object[]{
                                sqlTestTimeValue, testBianHaoValue1, countTestText,
                                series1X, series1Y,series4X, series4Y,   4
                        });
                    }

                    // 调用批量插入方法
                    int rowsInserted = ExecuteCommon.saveTestResultsBatch(dataToInsert);

                    long endTime = System.currentTimeMillis();
                    long elapsedTime = endTime - startTime;

                    if (rowsInserted > 0) {
                        JOptionPane.showMessageDialog(null, "数据保存成功！用时: " + elapsedTime + " ms", "成功", JOptionPane.INFORMATION_MESSAGE);
                        isDataUnsaved4 = false; // 数据已保存
                    } else {
                        JOptionPane.showMessageDialog(null, "数据保存失败！", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                });
            }
        });


*/




        //   保存 4个值到policetime表中,表的字段都有Id,Cishu,Tingzhizhi,Tingtime,Shebeihao,id是主键自增的 Cishu是从 JTextField  countTest1中获取的,Tingzhizhi是dianzu1Value1,Tingtime获取当时的系统时间,Shebeihao  固定为1,
        beginCLBtn1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                isTestingStarted1 = !isTestingStarted1;
                if (isTestingStarted1) {
                    isDataUnsaved = true; // 标记数据未保存

                    ruAndwone.openSerialPort();
                    ruAndwone.writeSingleRegister( 1, 0);


                    processed2.set(false);


                    //最大电阻值
                    double dianzumax111 = ExecuteCommon.getDianzumaxValue1(); // 从数据库中获取最新的 dianzumax 值



                    //最大值
                    double  countmax111 = ExecuteCommon.getTestmaxValue1(); // 从数据库中获取最新的  最大次数值
                    System.out.println(" double  countmax111 = ExecuteCommon.getTestmaxValue1();"+  countmax111 );

                    allcount111 = ExecuteCommon.getAllcountValue1(1); // 从数据库中获取最新的  最大次数值



         /*           //获得当前所有   累计次数值
                    String valueal1 = allcountTest1.getText();
                    allcount111 = Integer.parseInt(valueal1.split(" ")[0]);
*/



                    updateImage(label11111, "Y");
                    dianzu111.setText(null);
                    dianliu111.setText(null);



                    beginCLBtn1.setText("停止检测");
                    beginCLBtn1.setFont(new Font("楷体", Font.PLAIN, 22)); // 更小的字体
                    beginCLBtn1.setBackground(new Color(156, 149, 241)); // 深红色背景
                    beginCLBtn1.setForeground(Color.WHITE); // 白色文字


//                    timeSeconds1 = 0;   // 每次开始测试时，将时间归零
//                    resetChartData();   // 重置图表数据

                    startListening1();     // 开始监听文本框值的变化


                    executorServicebingxing1.submit(() -> {

                        //    AtomicBoolean continueLoop2 = new AtomicBoolean(true);  // 控制退出循环
                        boolean continueLoop2 = true;  // 控制退出循环

                        //   AtomicLong lastReadTime = new AtomicLong(0);  // 记录上次读取到 value == 1 的时间戳

                        System.out.println(" ===============进入到    1111111111111    currentTestFuture1 ");

                        while (continueLoop2) {
                            System.out.println(" ===============进入到    1111111111111     while (continueLoop2.get()) {  ");


                            boolean continueLoop = true;  // 控制退出循环

                            while (continueLoop) {

                                Result result = readAndProcessRegisters();

                                if (result.value  == 1 && !processed2.get()) {        // 为了 先读取电阻 再读取电流  再读取电阻...   防止 一直读取电流

                                    processed2.set(true);  // 标记已经处理过

                                    // 设备1电阻表 = 地址05
                                    // 统一读取器一直在后台轮询，这里直接取缓存，不再手动发请求、不再等待串口返回
                                    String dianzu2 = instrumentReader.getResistanceTextValue(5);
                                    dianzu1Value1 = instrumentReader.getResistanceValue(5);


                                    //填值
                                    SwingUtilities.invokeLater(() -> {

                                        dianliu111.setText(null);
                                        dianzu111.setText(null);

                                        String value = dianliu111NO.getText();

                                        dianliu111.setText(value);
                                        dianzu111.setText(dianzu2);

                                        dianliu111NO.setText(null);


                                    });



/*

                                    aclAndDianzu.sendReadRequest((byte) 0x05);
                                    Sleep.sleepTh(300);
                                    String dianzu2 = aclAndDianzu.getResistance1();

                                    dianzu1Value1 = Double.parseDouble(dianzu2.split(" ")[0]);

                                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------111111  电阻值  电阻值:: " + dianzu2);





                                    //填值
                                    SwingUtilities.invokeLater(() -> {

                                        dianliu111.setText(null);
                                        dianzu111.setText(null);

                                        String value = dianliu111NO.getText();

                                        dianliu111.setText(value);
                                        dianzu111.setText(dianzu2);

                                        dianliu111NO.setText(null);


                                    });
*/


                                    // 设置continueLoop为false，退出内层循环
                                    //    continueLoop.set(false);


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
                                            "==================计算时间差111000======================  111000 的间隔时间  : "  +   timeDifference
                                    );



                                    if (  timeDifference > 12    &&   timeDifference< 10000   ){
                                        updateImage(label11111, "N");

                                        //执行了
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 1, 1);

                                        JOptionPane.showMessageDialog(null, "设备1凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);
                                        saveToPolicetimeTableStop(1,countTest1.getText());

                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);

                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    processed111000.set(true);  // 标记已经处理过           注意位置

                                    System.out.println("------------------------------------------------------------------------------------------111111111111111  111  灭了");

                                    processed2.set(false);  // 标记已经处理过                   注意位置

                                }


                                //灯灭为0
                                if (result.value5 == 0   && ! processed555111.get()  ) {

                                    System.out.println("------------------------------------------------------------------------------------------111111111111111  555  亮了");



                                    long currentTime = System.currentTimeMillis();
                                    lastTime555111=   currentTime ;




                                    // 设备1电流表 = 地址01
                                    String acl1 = instrumentReader.getCurrentTextValue(1);
                                    dianliu1Value1 = instrumentReader.getCurrentValue(1);


                                    // 在主线程中更新UI
                                    SwingUtilities.invokeLater(() -> {

                                        System.out.println("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000   444    ---          "+ acl1 );
                                        dianliu111NO.setText(acl1);
                                        revalidate();
                                        repaint();

                                        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------电流 1   测量完毕");

                                    });




                                    processed555111.set(true);  // 标记已经处理过

                                }else if (result.value5 == 1  ) {

                                    System.out.println("------------------------------------------------------------------------------------------111111111111111  555  灭了");


                                    long currentTime = System.currentTimeMillis();


                                    if( processed555111.get()) {

                                        lastTime555111 =   currentTime ;

                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime555111) / 1000.0;

                                    System.out.println(
                                            "=====================计算时间差555111====================  555111的间隔时间  : "  +   timeDifference
                                    );

                                    if (  timeDifference > 12  &&   timeDifference< 10000   ){

                                        updateImage(label11111, "N");


                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 1, 1);

                                        JOptionPane.showMessageDialog(null, "设备1凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);
                                        saveToPolicetimeTableStop(1,countTest1.getText());

                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);

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
                            if (dianzu1Value1 > dianzumax111) {

                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 1, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 1, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label11111, "N");

                                Sleep.sleepTh(100);
                                saveToPolicetimeTable();

                                JOptionPane.showMessageDialog(null, "接点1电阻值为"+dianzu1Value1+"大于"+dianzumax111, "错误", JOptionPane.ERROR_MESSAGE);
/*                               Sleep.sleepTh(200);
// 用户点击确认后执行以下代码
                               ruAndwone.writeSingleRegister(5, 0);*/
                                dianzu111.setText(null);
                                dianzu1Value1=0.0;

                                continueLoop2 = false;  // 控制退出循环

                            }



                            if (dianliu1Value1 > 2.1 ) {

                                ruAndwone.writeSingleRegister( 1, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 1, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label11111, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);
                                saveToPolicetimeTabledianliu();


                                JOptionPane.showMessageDialog(null, "接点1电流值为"+dianliu1Value1+"A 大于"+" 2.1 A", "错误", JOptionPane.ERROR_MESSAGE);
                          /*      Sleep.sleepTh(200);
                                ruAndwone.writeSingleRegister(5, 0);
*/

                            }


                            //      if (timeSeconds1+allcount111+2 >= countmax111 ) {


                            if (timeSeconds1    >= countmax111 ) {
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 1, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 1, 1);

                                System.out.println("======================================================================================================================到1的次数了");


                                updateImage(label11111, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);

                                JOptionPane.showMessageDialog(null, "设备1检测已达到设定测试次数", "正确", JOptionPane.ERROR_MESSAGE);
                                Sleep.sleepTh(200);
                            }


                        }


                    });

                } else {

                    //把别的测试程序干停了
                    // 停止测试
                    if (currentTestFuture1 != null    && !currentTestFuture1.isDone()  ) {
                        currentTestFuture1.cancel(true); // 尝试中断执行中的任务
                    }

                    updateImage(label11111, "stop");


                    lastTime111000=0;
                    lastTime555111=0;
                    ruAndwone.writeSingleRegister( 5, 0);



                    beginCLBtn1.setText("开始检测");
                    beginCLBtn1.setFont(new Font("楷体", Font.PLAIN, 22)); // 还原原始的字体大小
                    beginCLBtn1.setBackground(new Color(15, 48, 182)); // 还原原始的背景色

                    stopListening1();



                    isTestingStarted1 = false;

                }
            }
        });


        beginCLBtn2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isTestingStarted2 = !isTestingStarted2;
                if (isTestingStarted2) {
                    isDataUnsaved2 = true; // 标记数据未保存

                    ruAndwone.openSerialPort();
                    ruAndwone.writeSingleRegister(2, 0);

                    processed22.set(false);


                    double dianzumax222 = ExecuteCommon.getDianzumaxValue2();

                    double  countmax222 = ExecuteCommon.getTestmaxValue2(); // 从数据库中获取最新的  最大次数值

                    System.out.println("+++++++++++++++++++++++++++++++++++++++++++"+  countmax222);


                    allcount222 = ExecuteCommon.getAllcountValue1(2); // 从数据库中获取最新的  最大次数值




                    System.out.println(" ===========================================进入到           if (isTestingStarted2) {   了   ");
                    updateImage(label22222, "Y");
                    dianzu222.setText(null);
                    dianliu222.setText(null);


                    beginCLBtn2.setText("停止检测");
                    beginCLBtn2.setFont(new Font("楷体", Font.PLAIN, 22)); // 更小的字体
                    beginCLBtn2.setBackground(new Color(156, 149, 241)); // 深红色背景
                    beginCLBtn2.setForeground(Color.WHITE); // 白色文字


                    startListening2();     // 开始监听文本框值的变化


                    executorServicebingxing2.submit(() -> {

                        //   AtomicBoolean continueLoop2 = new AtomicBoolean(true);  // 控制退出循环
                        boolean continueLoop2 = true;  // 控制退出循环

                        //AtomicLong lastReadTime = new AtomicLong(0);  // 记录上次读取到 value == 1 的时间戳
                        System.out.println(" ===============进入到    2222222222222    currentTestFuture2 ");


                        while (continueLoop2) {

                            //     AtomicBoolean continueLoop = new AtomicBoolean(true);  // 控制退出循环
                            System.out.println(" ===============进入到    2222222222222     while (continueLoop2.get()) {  ");

                            boolean continueLoop = true;  // 控制退出循环

                            while (continueLoop) {




                                System.out.println(" ===============进入到    2222222222222     while (continueLoop.get()) {  ");


                                Result result = readAndProcessRegisters();

                                System.out.println(

                                        "99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999"  +"设备2的   值         "  +result.value2+"       "+result.value6
                                );

                                if (result.value2 == 1 && !processed22.get()) {

                                    processed22.set(true);  // 标记已经处理过

                                    System.out.println("------------------------------------------------------------------------------------------2222222222222   222 亮了");

                                    //      Sleep.sleepTh(100);          //   接通之后等1s再测试




                                    // 设备2电阻表 = 地址06
                                    String dianzu1 = instrumentReader.getResistanceTextValue(6);
                                    dianzu2Value2 = instrumentReader.getResistanceValue(6);
                                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------222  电阻值  电阻值: " + dianzu1);

                                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------222  电阻值   测量完毕");



                                    SwingUtilities.invokeLater(() -> {

                                        dianliu222.setText(null);
                                        dianzu222.setText(null);

                                        String value = dianliu222NO.getText();


                                        dianliu222.setText(value);
                                        dianzu222.setText(dianzu1);

                                        dianliu222NO.setText(null);


                                    });

                                    revalidate();
                                    repaint();

                                    //   continueLoop.set(false); // 设置循环终止条件

                                    continueLoop = false;
                                    processed222000.set(false);  // 标记已经处理过


                                    long currentTime = System.currentTimeMillis();
                                    lastTime222000 = currentTime;


                                } else if (result.value2 == 0) {


                                    //j计时  报警
                                    long currentTime = System.currentTimeMillis();
                                    if (!processed222000.get()) {
                                        lastTime222000 = currentTime;
                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime222000) / 1000.0;

                                    System.out.println(
                                            "==================计算时间差222000====================== 222000 的间隔时间  : " + timeDifference
                                    );

                                    if (  timeDifference > 12  && timeDifference < 10000) {

                                        updateImage(label22222, "N");


                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 2, 1);

                                        JOptionPane.showMessageDialog(null, "设备2凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);

                                        saveToPolicetimeTableStop(2,countTest2.getText());

                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);

                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    System.out.println("-----------------------------------------------------------------------------------------22222222222222222222  111灭了");

                                    processed22.set(false);  // 标记已经处理过
                                    processed222000.set(true);  // 标记已经处理过

                                }


                                //灯灭为1
                                if (result.value6 == 0  && ! processed666111.get()     ) {

                                    System.out.println("-----------------------------------------------------------------------------------------22222222222222222   111 亮了");


                                    long currentTime = System.currentTimeMillis();
                                    lastTime666111 = currentTime;


                                    // 设备2电流表 = 地址02
                                    String acl2 = instrumentReader.getCurrentTextValue(2);
                                    dianliu2Value2 = instrumentReader.getCurrentValue(2);

                                    // 在主线程中更新UI
                                    SwingUtilities.invokeLater(() -> {

                                        System.out.println("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000   444    ---          "+ acl2 );

                                        dianliu222NO.setText(acl2);
                                        revalidate();
                                        repaint();

                                        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------电流    测量完毕");

                                    });




                                    processed666111.set(true);  // 标记已经处理过


                                } else if (result.value6 == 1) {

                                    System.out.println("-----------------------------------------------------------------------------------------2222222222222222 666  灭了");



                                    long currentTime = System.currentTimeMillis();


                                    if (processed666111.get()) {

                                        lastTime666111 = currentTime;

                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime666111) / 1000.0;

                                    System.out.println(
                                            "=====================计算时间差666111====================  666111的间隔时间  : " + timeDifference
                                    );

                                    if (  timeDifference > 12 && timeDifference < 10000) {

                                        updateImage(label22222, "N");

                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 2, 1);

                                        JOptionPane.showMessageDialog(null, "设备2凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);

                                        saveToPolicetimeTableStop(2,countTest2.getText());

                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);


                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    processed666111.set(false);  // 标记已经处理过


                                }


                                Sleep.sleepTh(100);



                            }

                            Sleep.sleepTh(100);


                            //置于  continueLoop2  上即可
                            if (dianzu2Value2 > dianzumax222) {

                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 2, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 2, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label22222, "N");

                                Sleep.sleepTh(100);
                                saveToPolicetimeTable2();

                                JOptionPane.showMessageDialog(null, "接点2电阻值为"+dianzu2Value2+"大于"+dianzumax222, "错误", JOptionPane.ERROR_MESSAGE);

                                dianzu222.setText(null);
                                dianzu2Value2=0.0;

                                continueLoop2 = false;  // 控制退出循环

                            }

                            if (timeSeconds2   >= countmax222 ) {
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 2, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 2, 1);

                                System.out.println("======================================================================================================================到2的次数了");


                                updateImage(label22222, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);

                                JOptionPane.showMessageDialog(null, "设备2检测已达到设定测试次数", "正确", JOptionPane.ERROR_MESSAGE);
                                Sleep.sleepTh(200);
                            }

                            if (dianliu2Value2 > 2.1 ) {

                                ruAndwone.writeSingleRegister( 2, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 2, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label22222, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);
                                saveToPolicetimeTabledianliu2();


                                JOptionPane.showMessageDialog(null, "接点2电流值为"+dianliu2Value2+"A 大于"+" 2.1 A", "错误", JOptionPane.ERROR_MESSAGE);
                           /*     Sleep.sleepTh(200);
                                ruAndwone.writeSingleRegister(5, 0);
*/

                            }


                        }


                    });
                } else {
                    // 停止测试
                    if (currentTestFuture2 != null  && !currentTestFuture2.isDone()  ) {
                        currentTestFuture2.cancel(true); // 取消任务
                    }

                    lastTime222000 = 0;
                    lastTime666111 = 0;
                    ruAndwone.writeSingleRegister(5, 0);


                    beginCLBtn2.setText("开始检测");
                    beginCLBtn2.setFont(new Font("楷体", Font.PLAIN, 22)); // 还原原始的字体大小
                    beginCLBtn2.setBackground(new Color(15, 48, 182)); // 还原原始的背景色

                    updateImage(label22222, "stop");
                    stopListening2();


                    isTestingStarted2 = false;

                }
            }
        });



        beginCLBtn3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTestingStarted3 = !isTestingStarted3;
                if (isTestingStarted3) {
                    isDataUnsaved3 = true; // 标记数据未保存

                    ruAndwone.openSerialPort();
                    ruAndwone.writeSingleRegister( 3, 0);


                    processed222.set(false);


                    double dianzumax333 = ExecuteCommon.getDianzumaxValue3();

                    double  countmax333 = ExecuteCommon.getTestmaxValue3(); // 从数据库中获取最新的  最大次数值

                    allcount333 = ExecuteCommon.getAllcountValue1(3); // 从数据库中获取最新的  最大次数值




                    updateImage(label33333, "Y");
                    dianzu333.setText(null);
                    dianliu333.setText(null);


                    beginCLBtn3.setText("停止检测");
                    beginCLBtn3.setFont(new Font("楷体", Font.PLAIN, 22)); // 更小的字体
                    beginCLBtn3.setBackground(new Color(156, 149, 241)); // 深红色背景
                    beginCLBtn3.setForeground(Color.WHITE); // 白色文字


                    startListening3();     // 开始监听文本框值的变化



                    executorServicebingxing3.submit(() -> {

                        boolean continueLoop2 = true;  // 控制退出循环

                        System.out.println(" ===============进入到   333333333333333   currentTestFuture3  ");


                        while (continueLoop2) {

                            System.out.println(" ===============进入到   333333333333333     while (continueLoop2.get()) {  ");



                            boolean continueLoop = true;  // 控制退出循环

                            while (continueLoop) {



                                Result result = readAndProcessRegisters();
                                if (result.value3  == 1 && !processed222.get()) {

                                    processed222.set(true);  // 标记已经处理过



                                    // 设备3电阻表 = 地址07
                                    String dianzu1 = instrumentReader.getResistanceTextValue(7);
                                    dianzu3Value3 = instrumentReader.getResistanceValue(7);
                                    System.out.println("电阻值: " + dianzu1);
                                    System.out.println("--------------------------------------------------------------------------------------------333333333333--------------------------------电阻值  电阻值:: " + dianzu1);

                                    System.out.println("---------------------------------------------------------------------------------------------333333333333--------------------------------电阻值   测量完毕");


                                    SwingUtilities.invokeLater(() -> {

                                        dianliu333.setText(null);
                                        dianzu333.setText(null);


                                        String value = dianliu333NO.getText();

                                        dianliu333.setText(value);
                                        dianzu333.setText(dianzu1);


                                        dianliu333NO.setText(null);               //111


                                    });

                                    revalidate();
                                    repaint();




                                    continueLoop = false;
                                    processed333000.set(false);  // 标记已经处理过

                                    long currentTime = System.currentTimeMillis();
                                    lastTime333000 =   currentTime ;


                                }   else    if (result.value3 == 0) {



                                    //计时  报警
                                    long currentTime = System.currentTimeMillis();

                                    if( !processed333000.get()) {
                                        lastTime333000 =   currentTime ;
                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime333000) / 1000.0;

                                    System.out.println(
                                            "==================计算时间差333 333======================  333 33333 的间隔时间  : "  +   timeDifference
                                    );

                                    if (  timeDifference > 12    &&   timeDifference< 10000   ){
                                        updateImage(label33333, "N");


                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 3, 1);

                                        JOptionPane.showMessageDialog(null, "设备3凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);
                                        saveToPolicetimeTableStop(3,countTest3.getText());
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);


                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }


                                    System.out.println("------------------------------------------------------------------------------------------33333333333333333   3333 灭了");

                                    processed222.set(false);  // 标记已经处理过
                                    processed333000.set(true);  // 标记已经处理过



                                }


                                //灯灭为1
                                if (result.value7 == 0  && ! processed777111.get()     ) {

                                    System.out.println("-----------------------------------------------------------------------------------------3333333333333333  777    亮了");

                                    long currentTime = System.currentTimeMillis();
                                    lastTime777111=   currentTime ;
                                    Sleep.sleepTh(100);  // 等待数据返回

                                    // 设备3电流表 = 地址03
                                    String acl3 = instrumentReader.getCurrentTextValue(3);
                                    dianliu3Value3 = instrumentReader.getCurrentValue(3);

                                    // 在主线程中更新UI
                                    SwingUtilities.invokeLater(() -> {
                                        System.out.println("设备3 --- " + acl3);
                                        dianliu333NO.setText(acl3);
                                        revalidate();
                                        repaint();
                                    });


// 创建一个新的任务来读取电流
/*                                    Runnable readCurrentTask = new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                // 发送读取请求
                                                aclAndDianzu.sendReadRequest((byte)0x03);
                                                Sleep.sleepTh(300);  // 等待数据返回

                                                // 获取电流值
                                                String acl3 = aclAndDianzu.getCurrent3();
                                                dianliu3Value3 = Double.parseDouble(acl3.split(" ")[0]);

                                                // 在主线程中更新UI
                                                SwingUtilities.invokeLater(() -> {
                                                    System.out.println("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000   设备3      ---          "+ acl3 );

                                                    dianliu333NO.setText(acl3);
                                                    revalidate();
                                                    repaint();
                                                });

                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    };

// 提交读取电流任务到ExecutorService
                                    executorServicebingxing3.submit(readCurrentTask);*/

              /*                      aclAndDianzu.sendReadRequest((byte)0x03);
                                    Sleep.sleepTh(300);
                                    String  acl3= aclAndDianzu.getCurrent3();
                                    dianliu3Value3 = Double.parseDouble(acl3.split(" ")[0]);


                                    System.out.println("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000   设备3      ---          "+ acl3 );
                                    dianliu333NO.setText(acl3);

                                    revalidate();
                                    repaint();*/





                                    //          System.out.println("-----------------------------------------------------------------------------------------777111111111111111111亮了");
                                    processed777111.set(true);  // 标记已经处理过


                                }else if (result.value7 == 1   ) {

                                    System.out.println("-----------------------------------------------------------------------------------------333333333333333333333  777  灭了");

                                    long currentTime = System.currentTimeMillis();


                                    if( processed777111.get()) {

                                        lastTime777111 =   currentTime ;

                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime777111) / 1000.0;

                                    System.out.println(
                                            "=====================计算时间差333333 777 ====================  333333 777的间隔时间  : "  +   timeDifference
                                    );

                                    if (  timeDifference > 12   &&   timeDifference< 10000   ){
                                        updateImage(label33333, "N");

                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 3, 1);

                                        JOptionPane.showMessageDialog(null, "设备3凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);
                                        saveToPolicetimeTableStop(3,countTest3.getText());
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);


                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    processed777111.set(false);  // 标记已经处理过
                                }
                                // Sleep.sleepTh(500);
                            }
                            Sleep.sleepTh(100);




                            //置于  continueLoop2  上即可
                            if (dianzu3Value3 > dianzumax333) {

                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 3, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 3, 1);

                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);

                                updateImage(label33333, "N");



                                Sleep.sleepTh(100);
                                saveToPolicetimeTable3();


                                JOptionPane.showMessageDialog(null, "接点3电阻值为"+dianzu3Value3+"大于"+dianzumax333, "错误", JOptionPane.ERROR_MESSAGE);
                     /*           Sleep.sleepTh(200);
                                ruAndwone.writeSingleRegister(5, 0);*/
                                dianzu333.setText(null);
                                dianzu3Value3=0.0;

                                continueLoop2=false;


                            }



                            //  if (timeSeconds3+allcount333 >= countmax333 ) {

                            if (timeSeconds3   >= countmax333 ) {
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 3, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 3, 1);
                                System.out.println("======================================================================================================================到3的次数了");


                                updateImage(label33333, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);

                                JOptionPane.showMessageDialog(null, "设备3检测已达到设定测试次数", "正确", JOptionPane.ERROR_MESSAGE);
                                Sleep.sleepTh(200);
                            }

                            if (dianliu3Value3 > 2.1 ) {

                                ruAndwone.writeSingleRegister( 3, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 3, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label33333, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);
                                saveToPolicetimeTabledianliu3();


                                JOptionPane.showMessageDialog(null, "接点3电流值为"+dianliu3Value3+"A 大于 2.1A", "错误", JOptionPane.ERROR_MESSAGE);
                            /*    Sleep.sleepTh(200);
                                ruAndwone.writeSingleRegister(5, 0);
*/


                            }


                        }


                       /* SwingUtilities.invokeLater(() -> {
                            if (isTestingStarted3) {
                                beginCLBtn3.setText("开始测量");
                                beginCLBtn3.setFont(new Font("楷体", Font.PLAIN, 22)); // 还原原始的字体大小
                                beginCLBtn3.setBackground(new Color(15, 48, 182)); // 还原原始的背景色

                                stopListening3();

                                isTestingStarted3 = false;
                            }
                        });*/
                    });
                } else {
                    // 停止测试
                    // 停止测试
                    if (currentTestFuture3 != null  && !currentTestFuture3.isDone()  ) {
                        currentTestFuture3.cancel(true); // 取消任务
                    }


                    lastTime333000=0;
                    lastTime777111=0;
                    ruAndwone.writeSingleRegister( 5, 0);


                    beginCLBtn3.setText("开始检测");
                    beginCLBtn3.setFont(new Font("楷体", Font.PLAIN, 22)); // 还原原始的字体大小
                    beginCLBtn3.setBackground(new Color(15, 48, 182)); // 还原原始的背景色

                    updateImage(label33333, "stop");
                    stopListening3();


                    isTestingStarted3 = false;


                }
            }
        });




        beginCLBtn4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isTestingStarted4 = !isTestingStarted4;
                if (isTestingStarted4) {
                    isDataUnsaved4 = true; // 标记数据未保存

                    ruAndwone.openSerialPort();
                    ruAndwone.writeSingleRegister( 4, 0);

                    processed2222.set(false);



                    double dianzumax444 = ExecuteCommon.getDianzumaxValue4(); // 从数据库中获取最新的 dianzumax 值

                    double  countmax444 = ExecuteCommon.getTestmaxValue4(); // 从数据库中获取最新的  最大次数值


                    //获得当前所有次数值
              /*      String valueall = allcountTest4.getText();
                    allcount444 = Integer.parseInt(valueall.split(" ")[0]);
*/
                    allcount444 = ExecuteCommon.getAllcountValue1(4); // 从数据库中获取最新的  最大次数值





                    updateImage(label44444, "Y");
                    dianzu444.setText(null);
                    dianliu444.setText(null);


                    beginCLBtn4.setText("停止检测");
                    beginCLBtn4.setFont(new Font("楷体", Font.PLAIN, 22)); // 更小的字体
                    beginCLBtn4.setBackground(new Color(156, 149, 241)); // 深红色背景
                    beginCLBtn4.setForeground(Color.WHITE); // 白色文字

                    //        timeSeconds4 = 0;   // 每次开始测试时，将时间归零
                    //        resetChartData4();   // 重置图表数据
                    startListening4();     // 开始监听文本框值的变化

                    //     pD = new PDialog();

                    //

                    executorServicebingxing4.submit(() -> {
                        System.out.println(" ===============进入到   44444444444444444    currentTestFuture4  ");

                        //   AtomicBoolean continueLoop2 = new AtomicBoolean(true);  // 控制退出循环
                        AtomicLong lastReadTime = new AtomicLong(0);  // 记录上次读取到 value == 1 的时间戳

                        boolean continueLoop2 = true;  // 控制退出循环

                        while (continueLoop2) {
                            System.out.println(" ===============进入到   44444444444444444     while (continueLoop2.get()) {  ");

                            //    AtomicBoolean continueLoop = new AtomicBoolean(true);  // 控制退出循环
                            boolean continueLoop = true;  // 控制退出循环

                            //    SwingUtilities.invokeLater(() -> pD.showMessage("断开中"));

                            while (continueLoop) {
                                System.out.println(" ===============进入到   44444444444444444     while (continueLoop.get()) {  ");


                                Result result = readAndProcessRegisters();
                                System.out.println(

                                        "99999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999999"  +"设备4的   值         "  +result.value4+"       "+result.value8
                                );

                                if (result.value4  == 1 && !processed2222.get()) {

                                    processed2222.set(true);  // 标记已经处理过

                                    System.out.println("-----------------------------------------------------------------------------------------44444444444444444444   111亮了");


                                    // 设备4电阻表 = 地址08
                                    String dianzu1 = instrumentReader.getResistanceTextValue(8);
                                    dianzu4Value4 = instrumentReader.getResistanceValue(8);
                                    System.out.println("电阻值: " + dianzu1);
                                    System.out.println("---------------------------------------------------------------------------------------------4444444444--------------------------------电阻值 电阻值:: " + dianzu4Value4);

                                    System.out.println("---------------------------------------------------------------------------------------------4444444444--------------------------------电阻值   测量完毕");



                                    //填值
                                    SwingUtilities.invokeLater(() -> {

                                        dianliu444.setText(null);
                                        dianzu444.setText(null);

                                        String value = dianliu444NO.getText();


                                        dianliu444.setText(value);
                                        dianzu444.setText(dianzu1);

                                        dianliu444NO.setText(null);               //111


                                    });

                                    revalidate();
                                    repaint();


/*

                                    aclAndDianzu.sendReadRequest((byte) 0x08);
                                    Sleep.sleepTh(300);
                                    System.out.println("电阻值: " + aclAndDianzu.getResistance4());
                                    String dianzu1 = aclAndDianzu.getResistance4();
                                    dianzu4Value4 = Double.parseDouble(dianzu1.split(" ")[0]);
                                    System.out.println("---------------------------------------------------------------------------------------------4444444444--------------------------------电阻值 电阻值:: " + dianzu4Value4);

                                    System.out.println("---------------------------------------------------------------------------------------------4444444444--------------------------------电阻值   测量完毕");



                                    //填值
                                    SwingUtilities.invokeLater(() -> {

                                        dianliu444.setText(null);
                                        dianzu444.setText(null);

                                        String value = dianliu444NO.getText();


                                        dianliu444.setText(value);
                                        dianzu444.setText(dianzu1);

                                       dianliu444NO.setText(null);               //111




                                    });

                                    revalidate();
                                    repaint();
*/

                                    // 设置continueLoop为false，退出内层循环
                                    //       continueLoop.set(false);

                                    continueLoop = false;
                                    processed444000.set(false);  // 标记已经处理过


                                    long currentTime = System.currentTimeMillis();
                                    lastTime444000 =   currentTime ;



                                }  else    if (result.value4 == 0) {
                                    //j计时  报警
                                    long currentTime = System.currentTimeMillis();

                                    if( !processed444000.get()) {

                                        lastTime444000 =   currentTime ;

                                    }

                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime444000) / 1000.0;

                                    System.out.println(
                                            "==================计算时间差444000======================  444000 的间隔时间  : "  +   timeDifference
                                    );


                                    if (  timeDifference > 12    &&   timeDifference< 10000   ){

                                        updateImage(label44444, "N");


                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 4, 1);

                                        JOptionPane.showMessageDialog(null, "设备4左凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);
                                        saveToPolicetimeTableStop(4,countTest4.getText());
                                        Sleep.sleepTh(100);


                                        ruAndwone.writeSingleRegister( 5, 0);
                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }


                                    System.out.println("------------------------------------------------------------------------------------------44444444444444444444  灭了");

                                    processed2222.set(false);  // 标记已经处理过

                                    processed444000.set(true);  // 标记已经处理过

                                }


                                //灯灭为1
                                if (result.value8 == 0   && ! processed888111.get()     ) {

                                    System.out.println("-----------------------------------------------------------------------------------------444444444444444   8888  亮了");


                                    long currentTime = System.currentTimeMillis();
                                    lastTime888111=   currentTime ;



                                    // 设备4电流表 = 地址04
                                    String acl4 = instrumentReader.getCurrentTextValue(4);
                                    dianliu4Value4 = instrumentReader.getCurrentValue(4);

                                    // 在主线程中更新UI
                                    SwingUtilities.invokeLater(() -> {
                                        System.out.println("设备3 --- " + acl4);

                                        dianliu444NO.setText(acl4);
                                        revalidate();
                                        repaint();

                                        System.out.println("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000   444    ---          "+ acl4 );


                                    });


/*
                                    Thread readCurrentThread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {



                                            } catch (Exception ex) {
                                                ex.printStackTrace();
                                            }
                                        }
                                    });

// 启动线程
                                    readCurrentThread.start();*/

/*

                                    aclAndDianzu.sendReadRequest((byte) 0x04);
                                    Sleep.sleepTh(300);
                                    String acl4 = aclAndDianzu.getCurrent4();
                                    dianliu4Value4  = Double.parseDouble(acl4.split(" ")[0]);



                                    dianliu444NO.setText(acl4);
                                    revalidate();
                                    repaint();
*/




                                    processed888111.set(true);  // 标记已经处理过


                                }else if (result.value8 == 1  ) {

                                    System.out.println("----------------------------------------------------------------------------------------444444444444444444444   888灭了");




                                    long currentTime = System.currentTimeMillis();


                                    if( processed888111.get()) {

                                        lastTime888111 =   currentTime ;

                                    }


                                    // 计算时间差（秒）
                                    double timeDifference = (currentTime - lastTime888111) / 1000.0;

                                    System.out.println(
                                            "=====================计算时间差444444   888==================== 444444   888的间隔时间  : "  +   timeDifference
                                    );

                                    if (  timeDifference > 12   &&   timeDifference< 10000   ){
                                        updateImage(label44444, "N");


                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 1);
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 4, 1);

                                        JOptionPane.showMessageDialog(null, "设备4右凸轮转动故障 ", "错误", JOptionPane.ERROR_MESSAGE);
                                        saveToPolicetimeTableStop(4 , countTest4.getText());
                                        Sleep.sleepTh(100);
                                        ruAndwone.writeSingleRegister( 5, 0);

                                        continueLoop=false;
                                        // 同时停止外部循环
                                        continueLoop2 = false; // 停止外部循环

                                    }

                                    processed888111.set(false);  // 标记已经处理过

                                }


                                // Sleep.sleepTh(500);
                            }

                            Sleep.sleepTh(100);


                            //置于  continueLoop2  上即可
                            if (dianzu4Value4 > dianzumax444 ) {
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 4, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 4, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label44444, "N");

                                Sleep.sleepTh(100);
                                saveToPolicetimeTable4();


                                JOptionPane.showMessageDialog(null, "接点4电阻值为"+dianzu4Value4+"大于"+dianzumax444, "错误", JOptionPane.ERROR_MESSAGE);
                                //  Sleep.sleepTh(200);
                                //  ruAndwone.writeSingleRegister(5, 0);

                                dianzu444.setText(null);
                                dianzu4Value4=0.0;

                                continueLoop2=false;



                            }

                            if (timeSeconds4   >= countmax444 ) {

                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 4, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 4, 1);

                                System.out.println("======================================================================================================================到4的次数了");

                                updateImage(label44444, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);

                                JOptionPane.showMessageDialog(null, "设备4检测已达到设定测试次数", "正确", JOptionPane.ERROR_MESSAGE);
                                Sleep.sleepTh(200);
                            }

                            if (dianliu4Value4 > 2.1 ) {


                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 4, 1);
                                Sleep.sleepTh(100);
                                ruAndwone.writeSingleRegister( 5, 1);


                                updateImage(label44444, "N");
                                continueLoop2=false;


                                Sleep.sleepTh(100);
                                saveToPolicetimeTabledianliu4();


                                JOptionPane.showMessageDialog(null, "接点4电流值为"+dianliu4Value4+"A 大于"+" 2.1A", "错误", JOptionPane.ERROR_MESSAGE);
                                Sleep.sleepTh(200);
                                //   ruAndwone.writeSingleRegister(5, 0);


                            }


                        }


           /*             SwingUtilities.invokeLater(() -> {
                            if (isTestingStarted4) {
                                beginCLBtn4.setText("开始测量");
                                beginCLBtn4.setFont(new Font("楷体", Font.PLAIN, 22)); // 还原原始的字体大小
                                beginCLBtn4.setBackground(new Color(15, 48, 182)); // 还原原始的背景色

                                stopListening4();
                                //    count3 = 0;//1111111111111111111111111111


                                isTestingStarted4 = false;
                            }
                        });*/
                    });


                } else {
                    // 停止测试
                    // 停止测试

                    if (currentTestFuture4 != null  && !currentTestFuture4.isDone()  ) {
                        currentTestFuture4.cancel(true); // 取消任务
                    }


                    lastTime444000=0;
                    lastTime888111=0;
                    ruAndwone.writeSingleRegister( 5, 0);



                    beginCLBtn4.setText("开始检测");
                    beginCLBtn4.setFont(new Font("楷体", Font.PLAIN, 22)); // 还原原始的字体大小
                    beginCLBtn4.setBackground(new Color(15, 48, 182)); // 还原原始的背景色
                    updateImage(label44444, "stop");
                    stopListening4();


                    isTestingStarted4 = false;


                }
            }
        });


        //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


        JLabel  s1 = new JLabel("速度:        次/分钟");
        s1.setFont(new Font("宋体", Font.BOLD, 20));
        s1.setForeground(new Color(0, 0, 0));
        s1.setBounds(977, 70, 306, 50);   //+- 1px
        panel1.add(s1);

        JLabel  s2 = new JLabel("速度:        次/分钟");
        s2.setFont(new Font("宋体", Font.BOLD, 20));
        s2.setForeground(new Color(0, 0, 0));
        s2.setBounds(977, 70, 306, 50);   //+- 1px
        panel2.add(s2);





        JLabel  s3 = new JLabel("速度:        次/分钟");
        s3.setFont(new Font("宋体", Font.BOLD, 20));
        s3.setForeground(new Color(0, 0, 0));
        s3.setBounds(977, 70, 306, 50);   //+- 1px
        panel3.add(s3);




        JLabel  s4 = new JLabel("速度:        次/分钟");
        s4.setFont(new Font("宋体", Font.BOLD, 20));
        s4.setForeground(new Color(0, 0, 0));
        s4.setBounds(977, 70, 306, 50);   //+- 1px
        panel4.add(s4);



        sudu111 = createCustomTextFieldsudu(1030, 76, 80, 40);
        panel1.add(sudu111);

        //  registerLabel("111", sudu111);

        sudu222 = createCustomTextFieldsudu(1030, 76, 80, 40);
        panel2.add(sudu222);
        //  registerLabel("222", sudu222);

        sudu333 = createCustomTextFieldsudu(1030, 76, 80, 40);
        panel3.add(sudu333);
        //    registerLabel("333", sudu333);
        sudu444 = createCustomTextFieldsudu(1030, 76, 80, 40);
        panel4.add(sudu444);
        //    registerLabel("444", sudu444);

        JLabel  t1 = new JLabel("接触电阻1:");
        t1.setFont(new Font("宋体", Font.BOLD, 20));
        t1.setForeground(new Color(0, 0, 0));
        t1.setBounds(580, 70, 206, 50);   //+- 1px
        panel1.add(t1);
        JLabel  t4 = new JLabel("电流1:");
        t4.setFont(new Font("宋体", Font.BOLD, 20));
        t4.setForeground(new Color(0, 0, 0));
        t4.setBounds(150, 70, 206, 50);   //+- 1px
        panel1.add(t4);




        JLabel  t11 = new JLabel("接触电阻2:");
        t11.setFont(new Font("宋体", Font.BOLD, 20));
        t11.setForeground(new Color(0, 0, 0));
        t11.setBounds(580, 70, 206, 50);   //+- 1px
        panel2.add(t11);
        JLabel  t44 = new JLabel("电流2:");
        t44.setFont(new Font("宋体", Font.BOLD, 20));
        t44.setForeground(new Color(0, 0, 0));
        t44.setBounds(150, 70, 206, 50);   //+- 1px
        panel2.add(t44);



        JLabel  t111 = new JLabel("接触电阻3:");
        t111.setFont(new Font("宋体", Font.BOLD, 20));
        t111.setForeground(new Color(0, 0, 0));
        t111.setBounds(580, 70, 206, 50);   //+- 1px
        panel3.add(t111);
        JLabel  t444 = new JLabel("电流3:");
        t444.setFont(new Font("宋体", Font.BOLD, 20));
        t444.setForeground(new Color(0, 0, 0));
        t444.setBounds(150, 70, 206, 50);   //+- 1px
        panel3.add(t444);



        JLabel  t1111 = new JLabel("接触电阻4:");
        t1111.setFont(new Font("宋体", Font.BOLD, 20));
        t1111.setForeground(new Color(0, 0, 0));
        t1111.setBounds(580, 70, 206, 50);   //+- 1px
        panel4.add(t1111);
        JLabel  t4444 = new JLabel("电流4:");
        t4444.setFont(new Font("宋体", Font.BOLD, 20));
        t4444.setForeground(new Color(0, 0, 0));
        t4444.setBounds(150, 70, 206, 50);   //+- 1px
        panel4.add(t4444);




        dianzu111 = createCustomTextField(700, 56, 180, 75);
        panel1.add(dianzu111);

        dianliu111 = createCustomTextField(230, 56, 180, 75);
        panel1.add(dianliu111);




        dianzu222 = createCustomTextField(700, 56, 180, 75);
        panel2.add(dianzu222);

        dianliu222 = createCustomTextField(230, 56, 180, 75);
        panel2.add(dianliu222);



        dianzu333 = createCustomTextField(700, 56, 180, 75);
        panel3.add(dianzu333);

        dianliu333= createCustomTextField(230, 56, 180, 75);
        panel3.add(dianliu333);



        dianzu444 = createCustomTextField(700, 56, 180, 75);
        panel4.add(dianzu444);

        dianliu444 = createCustomTextField(230, 56, 180, 75);
        panel4.add(dianliu444);




        dianliu111NO = createCustomTextFieldNO(430, 56, 60, 75);
        panel1.add(dianliu111NO);
        dianliu222NO = createCustomTextFieldNO(430, 56, 60, 75);
        panel2.add(dianliu222NO);
        dianliu333NO = createCustomTextFieldNO(430, 56, 60, 75);
        panel3.add(dianliu333NO);
        dianliu444NO = createCustomTextFieldNO(430, 56, 60, 75);
        panel4.add(dianliu444NO);




//---------------------------------------折线图------------------------------------------------------------------------------------------------------


        //---------------------------------------------------------------折线图------------------------------------------------------------------------------------------------------------------------------------


        //   电阻X轴  坐标实时显示
        JLabel RedXY1 = new JLabel("");
        XzhoushsihTextField(RedXY1);
        panel1.add(RedXY1);

        JLabel RedXY2 = new JLabel("");
        XzhoushsihTextField(RedXY2);
        panel2.add(RedXY2);

        JLabel RedXY3= new JLabel("");
        XzhoushsihTextField(RedXY3);
        panel3.add(RedXY3);

        JLabel RedXY4 = new JLabel("");
        XzhoushsihTextField(RedXY4);
        panel4.add(RedXY4);



        // 电流（黄色） 坐标实时显示
        JLabel YellowXY1 = new JLabel("");
        YzhoushsihTextField(YellowXY1);
        panel1.add(YellowXY1);

        JLabel YellowXY2 = new JLabel("");
        YzhoushsihTextField(YellowXY2);
        panel2.add(YellowXY2);

        JLabel YellowXY3 = new JLabel("");
        YzhoushsihTextField(YellowXY3);
        panel3.add(YellowXY3);

        JLabel YellowXY4 = new JLabel("");
        YzhoushsihTextField(YellowXY4);
        panel4.add(YellowXY4);





        // 创建  左侧Y轴  序列数据集（电阻）
        series1 = new XYSeries("电阻1");
        XYSeriesCollection dataset1 = new XYSeriesCollection();
        dataset1.addSeries(series1);

        series2 = new XYSeries("电阻2");
        XYSeriesCollection dataset2 = new XYSeriesCollection();
        dataset2.addSeries(series2);

        series3 = new XYSeries("电阻3");
        XYSeriesCollection dataset3 = new XYSeriesCollection();
        dataset3.addSeries(series3);

        series4 = new XYSeries("电阻4");
        XYSeriesCollection dataset4 = new XYSeriesCollection();
        dataset4.addSeries(series4);



        // 创建 右侧Y轴 的数据集（电流）
        rightSeries1 = new XYSeries("电流1");
        XYSeriesCollection rightDataset1 = new XYSeriesCollection();
        rightDataset1.addSeries(rightSeries1);

        rightSeries2 = new XYSeries("电流2");
        XYSeriesCollection rightDataset2 = new XYSeriesCollection();
        rightDataset2.addSeries(rightSeries2);

        rightSeries3 = new XYSeries("电流3");
        XYSeriesCollection rightDataset3 = new XYSeriesCollection();
        rightDataset3.addSeries(rightSeries3);

        rightSeries4 = new XYSeries("电流4");
        XYSeriesCollection rightDataset4 = new XYSeriesCollection();
        rightDataset4.addSeries(rightSeries4);







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

        chart2 = ChartFactory.createXYLineChart(
                "接点接触电阻参数曲线", // 图表标题
                "次数 ", // x 轴标签
                "电阻(mΩ)", // y 轴标签
                dataset2, // 数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 生成工具提示
                false // 不生成URL
        );

        chart3 = ChartFactory.createXYLineChart(
                "接点接触电阻参数曲线", // 图表标题
                "次数 ", // x 轴标签
                "电阻(mΩ)", // y 轴标签
                dataset3, // 数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 生成工具提示
                false // 不生成URL
        );

        chart4 = ChartFactory.createXYLineChart(
                "接点接触电阻参数曲线", // 图表标题
                "次数 ", // x 轴标签
                "电阻(mΩ)", // y 轴标签
                dataset4, // 数据集
                PlotOrientation.VERTICAL,
                true, // 显示图例
                true, // 生成工具提示
                false // 不生成URL
        );


        // 设置图表标题和其颜色
        chart.getTitle().setPaint(Color.BLUE);
        Font titleFont = new Font("宋体", Font.BOLD, 18);
        chart.getTitle().setFont(titleFont);

        chart2.getTitle().setPaint(Color.BLUE);
        Font titleFont2 = new Font("宋体", Font.BOLD, 18);
        chart2.getTitle().setFont(titleFont2);


        chart3.getTitle().setPaint(Color.BLUE);
        Font titleFont3 = new Font("宋体", Font.BOLD, 18);
        chart3.getTitle().setFont(titleFont3);

        chart4.getTitle().setPaint(Color.BLUE);
        Font titleFont4 = new Font("宋体", Font.BOLD, 18);
        chart4.getTitle().setFont(titleFont4);







        plot = (XYPlot) chart.getPlot();
        plot.getDomainAxis().setLabelFont(titleFont);
        NumberAxis xAxis = new NumberAxis("次数 (次)");
        creatxAxischart(xAxis);
        plot.setDomainAxis(xAxis);


        plot2 = (XYPlot) chart2.getPlot();
        plot2.getDomainAxis().setLabelFont(titleFont);
        NumberAxis xAxis2 = new NumberAxis("次数 (次)");
        creatxAxischart(xAxis2);
        plot2.setDomainAxis(xAxis2);


        plot3 = (XYPlot) chart3.getPlot();
        plot3.getDomainAxis().setLabelFont(titleFont);
        NumberAxis xAxis3 = new NumberAxis("次数 (次)");
        creatxAxischart(xAxis3);
        plot3.setDomainAxis(xAxis3);


        plot4 = (XYPlot) chart4.getPlot();
        plot4.getDomainAxis().setLabelFont(titleFont);
        NumberAxis xAxis4 = new NumberAxis("次数 (次)");
        creatxAxischart(xAxis4);
        plot4.setDomainAxis(xAxis4);





        // 配置图表的左侧 Y 轴（电阻）
        NumberAxis yAxis1 = (NumberAxis) plot.getRangeAxis();
        yAxis1.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        yAxis1.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));


        NumberAxis yAxis2 = (NumberAxis) plot2.getRangeAxis();
        yAxis2.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        yAxis2.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));


        NumberAxis yAxis3 = (NumberAxis) plot3.getRangeAxis();
        yAxis3.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        yAxis3.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));


        NumberAxis yAxis4 = (NumberAxis) plot4.getRangeAxis();
        yAxis4.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        yAxis4.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));



// 创建并配置左侧电阻的渲染器                图例部分的代码
        float lineWidth = 1.5f;                // 曲线的粗细
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesPaint(0, Color.RED); // 第一条曲线为红色
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, false);
        renderer.setSeriesStroke(0, new BasicStroke(lineWidth));

        XYLineAndShapeRenderer renderer2 = (XYLineAndShapeRenderer) plot2.getRenderer();
        renderer2.setSeriesPaint(0, Color.RED); // 第一条曲线为红色
        renderer2.setSeriesLinesVisible(0, true);
        renderer2.setSeriesShapesVisible(0, false);
        renderer2.setSeriesStroke(0, new BasicStroke(lineWidth));

        XYLineAndShapeRenderer renderer3 = (XYLineAndShapeRenderer) plot3.getRenderer();
        renderer3.setSeriesPaint(0, Color.RED); // 第一条曲线为红色
        renderer3.setSeriesLinesVisible(0, true);
        renderer3.setSeriesShapesVisible(0, false);
        renderer3.setSeriesStroke(0, new BasicStroke(lineWidth));


        XYLineAndShapeRenderer renderer4 = (XYLineAndShapeRenderer) plot4.getRenderer();
        renderer4.setSeriesPaint(0, Color.RED); // 第一条曲线为红色
        renderer4.setSeriesLinesVisible(0, true);
        renderer4.setSeriesShapesVisible(0, false);
        renderer4.setSeriesStroke(0, new BasicStroke(lineWidth));





// 配置右侧折线图的渲染器
        XYLineAndShapeRenderer rightRenderer = new XYLineAndShapeRenderer();
        rightRenderer.setSeriesPaint(0, new Color(115, 95, 1, 255)); // 右侧折线颜色
        rightRenderer.setSeriesStroke(0, new BasicStroke(1.5f)); // 设置右侧折线粗细
        rightRenderer.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3, 3));  // 设置数据点为圆形（半径为3）

        XYLineAndShapeRenderer rightRenderer2 = new XYLineAndShapeRenderer();
        rightRenderer2.setSeriesPaint(0, new Color(115, 95, 1, 255)); // 右侧折线颜色
        rightRenderer2.setSeriesStroke(0, new BasicStroke(1.5f)); // 设置右侧折线粗细
        rightRenderer2.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3, 3));  // 设置数据点为圆形（半径为3）      rightRenderer.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3, 3));  // 设置数据点为圆形（半径为3）

        XYLineAndShapeRenderer rightRenderer3 = new XYLineAndShapeRenderer();
        rightRenderer3.setSeriesPaint(0, new Color(115, 95, 1, 255)); // 右侧折线颜色
        rightRenderer3.setSeriesStroke(0, new BasicStroke(1.5f)); // 设置右侧折线粗细
        rightRenderer3.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3, 3));  // 设置数据点为圆形（半径为3）

        XYLineAndShapeRenderer rightRenderer4 = new XYLineAndShapeRenderer();
        rightRenderer4.setSeriesPaint(0, new Color(115, 95, 1, 255)); // 右侧折线颜色
        rightRenderer4.setSeriesStroke(0, new BasicStroke(1.5f)); // 设置右侧折线粗细
        rightRenderer4.setSeriesShape(0, new Ellipse2D.Double(-1.5, -1.5, 3, 3));  // 设置数据点为圆形（半径为3）








        Font legendFont = new Font("SimHei", Font.PLAIN, 12);
        chart.getLegend().setItemFont(legendFont);
        chart2.getLegend().setItemFont(legendFont);
        chart3.getLegend().setItemFont(legendFont);
        chart4.getLegend().setItemFont(legendFont);


        //---------------------------------------------------- 创建右侧Y轴----------------------------------------------------


        NumberAxis rightYAxis = new NumberAxis("电流(A)");
        rightYAxis.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 字体设置
        rightYAxis.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));


        NumberAxis rightYAxis2 = new NumberAxis("电流(A)");
        rightYAxis2.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 字体设置
        rightYAxis2.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));

        NumberAxis rightYAxis3 = new NumberAxis("电流(A)");
        rightYAxis3.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 字体设置
        rightYAxis3.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));

        NumberAxis rightYAxis4 = new NumberAxis("电流(A)");
        rightYAxis4.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 字体设置
        rightYAxis4.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));





// 添加右侧Y轴到图表中
        plot.setRangeAxis(1, rightYAxis);
        plot.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, true);

        plot2.setRangeAxis(1, rightYAxis2);
        plot2.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, true);

        plot3.setRangeAxis(1, rightYAxis3);
        plot3.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, true);

        plot4.setRangeAxis(1, rightYAxis4);
        plot4.setRangeAxisLocation(AxisLocation.TOP_OR_LEFT, true);




// 将右侧数据集映射到右侧Y轴
        plot.setDataset(1, rightDataset1);
        plot.setRenderer(1, rightRenderer);
        plot.mapDatasetToRangeAxis(1, 1); // 将数据集映射到右侧Y轴

        plot2.setDataset(1, rightDataset2);
        plot2.setRenderer(1, rightRenderer2);
        plot2.mapDatasetToRangeAxis(1, 1);  // 将数据集映射到第二个 Y 轴 (右侧)

        plot3.setDataset(1, rightDataset3);
        plot3.setRenderer(1, rightRenderer3);
        plot3.mapDatasetToRangeAxis(1, 1);  // 将数据集映射到第二个 Y 轴 (右侧)

        plot4.setDataset(1, rightDataset4);
        plot4.setRenderer(1, rightRenderer4);
        plot4.mapDatasetToRangeAxis(1, 1);  // 将数据集映射到第二个 Y 轴 (右侧)






        // 获取图表的 XYPlot
        XYPlot plot = chart.getXYPlot();

        // 将右侧 Y 轴添加到图表中
        plot.setRangeAxis(1, rightYAxis);

        // 将右侧数据集映射到右侧 Y 轴
        plot.setDataset(1, rightDataset1);  // rightDataset1 是右侧的电流数据集
        plot.mapDatasetToRangeAxis(1, 1);  // 将数据集映射到第二个 Y 轴 (右侧)

// 设置右侧数据集对应的渲染器
        plot.setRenderer(1, rightRenderer);


        XYPlot plot2 = chart2.getXYPlot();
        plot2.setRangeAxis(1, rightYAxis2);
        plot2.setDataset(1, rightDataset2);
        plot2.mapDatasetToRangeAxis(1, 1);
        plot2.setRenderer(1, rightRenderer2);

        XYPlot plot3 = chart3.getXYPlot();
        plot3.setRangeAxis(1, rightYAxis3);
        plot3.setDataset(1, rightDataset3);
        plot3.mapDatasetToRangeAxis(1, 1);
        plot3.setRenderer(1, rightRenderer3);

        XYPlot plot4 = chart4.getXYPlot();
        plot4.setRangeAxis(1, rightYAxis4);
        plot4.setDataset(1, rightDataset4);
        plot4.mapDatasetToRangeAxis(1, 1);
        plot4.setRenderer(1, rightRenderer4);






        //--------------------------------------------------------------------------------------------------------------

        // 创建折线图
        ChartPanel chartPanel = new ChartPanel(chart);

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


        ChartPanel chartPanel2 = new ChartPanel(chart2);
        chartPanel2.setMouseZoomable(false);
        chartPanel2.setMouseZoomable(false, false);
        chartPanel2.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                int notches = e.getWheelRotation();  // 获取鼠标滚轮滚动的刻度数。如果为正数，表示向下滚动；负数表示向上滚动。
                XYPlot plot = (XYPlot) chart2.getPlot();  // 获取图表的XYPlot对象，用于操作X轴的范围。
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


        ChartPanel chartPanel3 = new ChartPanel(chart3);
        chartPanel3.setMouseZoomable(false);
        chartPanel3.setMouseZoomable(false, false);
        chartPanel3.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                int notches = e.getWheelRotation();  // 获取鼠标滚轮滚动的刻度数。如果为正数，表示向下滚动；负数表示向上滚动。
                XYPlot plot = (XYPlot) chart3.getPlot();  // 获取图表的XYPlot对象，用于操作X轴的范围。
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


        ChartPanel chartPanel4 = new ChartPanel(chart4);
        chartPanel4.setMouseZoomable(false);
        chartPanel4.setMouseZoomable(false, false);
        chartPanel4.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {

                int notches = e.getWheelRotation();  // 获取鼠标滚轮滚动的刻度数。如果为正数，表示向下滚动；负数表示向上滚动。
                XYPlot plot = (XYPlot) chart4.getPlot();  // 获取图表的XYPlot对象，用于操作X轴的范围。
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






// 创建水平滚动条

        horizontalScrollBar1 = new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScrollBar1.setMaximum(Integer.parseInt(newXValue)); // 设置X轴的最大值为当前的最大X值
        horizontalScrollBar1.setVisibleAmount(28); // 设置可视范围为28
        horizontalScrollBar1.setUnitIncrement(1); // 设置滚动增量
        horizontalScrollBar1.setValue(Integer.parseInt(newXValue)-28); // 初始位置设置为最右边，确保拖动条位于最右边


        horizontalScrollBar2= new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScrollBar2.setMaximum(Integer.parseInt(newXValue2)); // 设置X轴的最大值为当前的最大X值
        horizontalScrollBar2.setVisibleAmount(28); // 设置可视范围为28
        horizontalScrollBar2.setUnitIncrement(1); // 设置滚动增量
        horizontalScrollBar2.setValue(Integer.parseInt(newXValue2)-28); // 初始位置设置为最右边，确保拖动条位于最右边


        horizontalScrollBar3= new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScrollBar3.setMaximum(Integer.parseInt(newXValue3)); // 设置X轴的最大值为当前的最大X值
        horizontalScrollBar3.setVisibleAmount(28); // 设置可视范围为28
        horizontalScrollBar3.setUnitIncrement(1); // 设置滚动增量
        horizontalScrollBar3.setValue(Integer.parseInt(newXValue3)-28); // 初始位置设置为最右边，确保拖动条位于最右边


        horizontalScrollBar4= new JScrollBar(JScrollBar.HORIZONTAL);
        horizontalScrollBar4.setMaximum(Integer.parseInt(newXValue4)); // 设置X轴的最大值为当前的最大X值
        horizontalScrollBar4.setVisibleAmount(28); // 设置可视范围为28
        horizontalScrollBar4.setUnitIncrement(1); // 设置滚动增量
        horizontalScrollBar4.setValue(Integer.parseInt(newXValue4)-28); // 初始位置设置为最右边，确保拖动条位于最右边





// 创建垂直滚动条
        verticalScrollBar1 = new JScrollBar(JScrollBar.VERTICAL);
        verticalScrollBar1.setUnitIncrement(1); // 设置滚动增量
        verticalScrollBar1.setMaximum(2000); // 设置最大值为2000
        verticalScrollBar1.setVisibleAmount(10); // 设置可视范围为60
        verticalScrollBar1.setValue(2000 - 10); // 初始值设置为最大值减去可视范围    // 初始位置设置为最大值减去可见范围，这样拖动条会在最底部


        verticalScrollBar2 = new JScrollBar(JScrollBar.VERTICAL);
        verticalScrollBar2.setUnitIncrement(1); // 设置滚动增量
        verticalScrollBar2.setMaximum(2000); // 设置最大值为2000
        verticalScrollBar2.setVisibleAmount(10); // 设置可视范围为60
        verticalScrollBar2.setValue(2000 - 10); // 初始值设置为最大值减去可视范围    // 初始位置设置为最大值减去可见范围，这样拖


        verticalScrollBar3 = new JScrollBar(JScrollBar.VERTICAL);
        verticalScrollBar3.setUnitIncrement(1); // 设置滚动增量
        verticalScrollBar3.setMaximum(2000); // 设置最大值为2000
        verticalScrollBar3.setVisibleAmount(10); // 设置可视范围为60
        verticalScrollBar3.setValue(2000 - 10); // 初始值设置为最大值减去可视范围    // 初始位置设置为最大值减去可见范围，这样拖


        verticalScrollBar4 = new JScrollBar(JScrollBar.VERTICAL);
        verticalScrollBar4.setUnitIncrement(1); // 设置滚动增量
        verticalScrollBar4.setMaximum(2000); // 设置最大值为2000
        verticalScrollBar4.setVisibleAmount(10); // 设置可视范围为60
        verticalScrollBar4.setValue(2000 - 10); // 初始值设置为最大值减去可视范围    // 初始位置设置为最大值减去可见范围，这样拖





        horizontalScrollBar1.setBounds(0, 694, 1400, 20); // 设置水平滚动条的位置和大小
        verticalScrollBar1.setBounds(1400, 172, 20, 545); // 设置垂直滚动条的位置和大小



        horizontalScrollBar2.setBounds(0, 694, 1400, 20); // 设置水平滚动条的位置和大小
        verticalScrollBar2.setBounds(1400, 172, 20, 545); // 设置垂直滚动条的位置和大小


        horizontalScrollBar3.setBounds(0, 694, 1400, 20); // 设置水平滚动条的位置和大小
        verticalScrollBar3.setBounds(1400, 172, 20, 545); // 设置垂直滚动条的位置和大小

        horizontalScrollBar4.setBounds(0, 694, 1400, 20); // 设置水平滚动条的位置和大小
        verticalScrollBar4.setBounds(1400, 172, 20, 545); // 设置垂直滚动条的位置和大小





        // 拖拽水平滚动条的监听器
        horizontalScrollBar1.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = horizontalScrollBar1.getValue();

            // 计算新的X轴显示范围
            double newLowerBound = value;
            double newUpperBound = value + 28;

            // 更新X轴的显示范围
            plot.getDomainAxis().setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel.repaint();
        });
        horizontalScrollBar2.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = horizontalScrollBar2.getValue();

            // 计算新的X轴显示范围
            double newLowerBound = value;
            double newUpperBound = value + 28;

            // 更新X轴的显示范围
            plot2.getDomainAxis().setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel.repaint();
        });
        horizontalScrollBar3.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = horizontalScrollBar3.getValue();

            // 计算新的X轴显示范围
            double newLowerBound = value;
            double newUpperBound = value + 28;

            // 更新X轴的显示范围
            plot3.getDomainAxis().setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel.repaint();
        });
        horizontalScrollBar4.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = horizontalScrollBar4.getValue();

            // 计算新的X轴显示范围
            double newLowerBound = value;
            double newUpperBound = value + 28;

            // 更新X轴的显示范围
            plot4.getDomainAxis().setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel.repaint();
        });










        // 拖拽垂直滚动条的监听器
        verticalScrollBar1.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = verticalScrollBar1.getValue();

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

        verticalScrollBar2.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = verticalScrollBar2.getValue();

            // 计算新的Y轴显示范围
            double newLowerBound = 0; // 下界固定为0
            double newUpperBound = 2000 - value; // 上界逐渐从2000减小到60

            // 更新Y轴的显示范围
            plot2.getRangeAxis().setRange(newLowerBound, newUpperBound);


            // 更新右侧Y轴的显示范围
            rightYAxis2.setRange(newLowerBound, newUpperBound);


            // 重新绘制图表
            chartPanel2.repaint();
        });

        verticalScrollBar3.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = verticalScrollBar3.getValue();

            // 计算新的Y轴显示范围
            double newLowerBound = 0; // 下界固定为0
            double newUpperBound = 2000 - value; // 上界逐渐从2000减小到60

            // 更新Y轴的显示范围
            plot3.getRangeAxis().setRange(newLowerBound, newUpperBound);

            // 更新右侧Y轴的显示范围
            rightYAxis3.setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel3.repaint();

        });

        verticalScrollBar4.addAdjustmentListener(e -> {
            // 获取当前滚动条的值
            int value = verticalScrollBar4.getValue();

            // 计算新的Y轴显示范围
            double newLowerBound = 0; // 下界固定为0
            double newUpperBound = 2000 - value; // 上界逐渐从2000减小到60

            // 更新Y轴的显示范围
            plot4.getRangeAxis().setRange(newLowerBound, newUpperBound);

            // 更新右侧Y轴的显示范围
            rightYAxis4.setRange(newLowerBound, newUpperBound);

            // 重新绘制图表
            chartPanel4.repaint();

        });




        panel1.add(horizontalScrollBar1);// 将滚动条添加到 panel1
        panel1.add(verticalScrollBar1);
        chartPanel.setLayout(null);// 设置 ChartPanel 的布局为 null（绝对布局），然后手动设置其位置和大小
        chartPanel.setBounds(0, 171, 1400, 522);
        panel1.add(chartPanel);




        panel2.add(horizontalScrollBar2);// 将滚动条添加到 panel1
        panel2.add(verticalScrollBar2);
        chartPanel2.setLayout(null);// 设置 ChartPanel 的布局为 null（绝对布局），然后手动设置其位置和大小
        chartPanel2.setBounds(0, 171, 1400, 522);
        panel2.add(chartPanel2);


        panel3.add(horizontalScrollBar3);// 将滚动条添加到 panel1
        panel3.add(verticalScrollBar3);
        chartPanel3.setLayout(null);// 设置 ChartPanel 的布局为 null（绝对布局），然后手动设置其位置和大小
        chartPanel3.setBounds(0, 171, 1400, 522);
        panel3.add(chartPanel3);

        panel4.add(horizontalScrollBar4);// 将滚动条添加到 panel1
        panel4.add(verticalScrollBar4);
        chartPanel4.setLayout(null);// 设置 ChartPanel 的布局为 null（绝对布局），然后手动设置其位置和大小
        chartPanel4.setBounds(0, 171, 1400, 522);
        panel4.add(chartPanel4);






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
                XYSeries series1 = ((XYSeriesCollection) plot.getDataset()).getSeries(0);

                XYSeries rightSeries1 = ((XYSeriesCollection) plot.getDataset(1)).getSeries(0); // 获取右侧 Y 轴的电流数据集


                XYDataItem closestItem1 = findClosestDataItem(series1, x);

                XYDataItem closestItemRight = findClosestDataItem(rightSeries1, x); // 查找电流曲线的最近点


                if (closestItem1 != null) {
                    RedXY1.setText(String.format("电阻1: %.2f mΩ", closestItem1.getYValue()));
                    RedXY1.repaint();
                }

                if (closestItemRight != null) {
                    YellowXY1.setText(String.format("电流2: %.2f A", closestItemRight.getYValue())); // 显示电流值
                    YellowXY1.repaint();
                }

            }
        });

        chartPanel2.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                // 这里可以处理点击事件
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {

                int mouseX = event.getTrigger().getX();
                Point2D p2 = chartPanel2.translateScreenToJava2D(new Point(mouseX, event.getTrigger().getY()));
                XYPlot plot = (XYPlot) chart2.getPlot();
                ChartRenderingInfo info = chartPanel2.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();
                ValueAxis xAxis = plot.getDomainAxis();

                double x = xAxis.java2DToValue(p2.getX(), dataArea, RectangleEdge.BOTTOM);

                plot.clearDomainMarkers();
                ValueMarker marker = new ValueMarker(x);
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);


                // 查找最近的点
                XYSeries series1 = ((XYSeriesCollection) plot.getDataset()).getSeries(0);

                XYSeries rightSeries1 = ((XYSeriesCollection) plot.getDataset(1)).getSeries(0); // 获取右侧 Y 轴的电流数据集


                XYDataItem closestItem1 = findClosestDataItem(series1, x);

                XYDataItem closestItemRight = findClosestDataItem(rightSeries1, x); // 查找电流曲线的最近点


                if (closestItem1 != null) {
                    RedXY2.setText(String.format("电阻2: %.2f mΩ", closestItem1.getYValue()));
                    RedXY2.repaint();
                }

                if (closestItemRight != null) {
                    YellowXY2.setText(String.format("电流2: %.2f A", closestItemRight.getYValue())); // 显示电流值
                    YellowXY2.repaint();
                }

            }
        });

        chartPanel3.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                // 这里可以处理点击事件
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {

                int mouseX = event.getTrigger().getX();
                Point2D p2 = chartPanel3.translateScreenToJava2D(new Point(mouseX, event.getTrigger().getY()));
                XYPlot plot = (XYPlot) chart3.getPlot();
                ChartRenderingInfo info = chartPanel3.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();
                ValueAxis xAxis = plot.getDomainAxis();

                double x = xAxis.java2DToValue(p2.getX(), dataArea, RectangleEdge.BOTTOM);

                plot.clearDomainMarkers();
                ValueMarker marker = new ValueMarker(x);
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);


                // 查找最近的点
                XYSeries series1 = ((XYSeriesCollection) plot.getDataset()).getSeries(0);

                XYSeries rightSeries1 = ((XYSeriesCollection) plot.getDataset(1)).getSeries(0); // 获取右侧 Y 轴的电流数据集


                XYDataItem closestItem1 = findClosestDataItem(series1, x);

                XYDataItem closestItemRight = findClosestDataItem(rightSeries1, x); // 查找电流曲线的最近点


                if (closestItem1 != null) {
                    RedXY3.setText(String.format("电阻3: %.2f mΩ", closestItem1.getYValue()));
                    RedXY3.repaint();
                }

                if (closestItemRight != null) {
                    YellowXY3.setText(String.format("电流3: %.2f A", closestItemRight.getYValue())); // 显示电流值
                    YellowXY3.repaint();
                }

            }
        });

        chartPanel4.addChartMouseListener(new ChartMouseListener() {
            @Override
            public void chartMouseClicked(ChartMouseEvent chartMouseEvent) {
                // 这里可以处理点击事件
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent event) {

                int mouseX = event.getTrigger().getX();
                Point2D p2 = chartPanel4.translateScreenToJava2D(new Point(mouseX, event.getTrigger().getY()));
                XYPlot plot = (XYPlot) chart4.getPlot();
                ChartRenderingInfo info = chartPanel4.getChartRenderingInfo();
                Rectangle2D dataArea = info.getPlotInfo().getDataArea();
                ValueAxis xAxis = plot.getDomainAxis();

                double x = xAxis.java2DToValue(p2.getX(), dataArea, RectangleEdge.BOTTOM);

                plot.clearDomainMarkers();
                ValueMarker marker = new ValueMarker(x);
                marker.setPaint(Color.BLACK);
                plot.addDomainMarker(marker);


                // 查找最近的点
                XYSeries series1 = ((XYSeriesCollection) plot.getDataset()).getSeries(0);

                XYSeries rightSeries1 = ((XYSeriesCollection) plot.getDataset(1)).getSeries(0); // 获取右侧 Y 轴的电流数据集


                XYDataItem closestItem1 = findClosestDataItem(series1, x);

                XYDataItem closestItemRight = findClosestDataItem(rightSeries1, x); // 查找电流曲线的最近点


                if (closestItem1 != null) {
                    RedXY4.setText(String.format("电阻4: %.2f mΩ", closestItem1.getYValue()));
                    RedXY4.repaint();
                }

                if (closestItemRight != null) {
                    YellowXY4.setText(String.format("电流4: %.2f A", closestItemRight.getYValue())); // 显示电流值
                    YellowXY4.repaint();
                }

            }
        });







        btnclear1= createStyledButton("清除",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        placeButton(panel1, btnclear1, 1280, 74, 100, 42);
        btnclear1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                resetChartData();

                timeSeconds1=0;



                // 清空第一个数据集中的所有系列数据
                for (int i = 0; i < dataset1.getSeriesCount(); i++) {
                    XYSeries series = dataset1.getSeries(i);
                    series.clear();
                }


                for (int i = 0; i < rightDataset1.getSeriesCount(); i++) {
                    XYSeries series = rightDataset1.getSeries(i);
                    series.clear();
                }


                // 清空所有组件的读取值
                JTextField[] allTextFields = {
                        dianzu111 , dianliu111     ,  countTest1  ,  testBianHao1  , sudu111
                };
                for (JTextField textField : allTextFields) {
                    textField.setText("");
                }

            }
        });



        btnclear2= createStyledButton("清除",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        placeButton(panel2, btnclear2, 1280, 74, 100, 42);
        btnclear2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                resetChartData2();

                timeSeconds2=0;

                // 清空第一个数据集中的所有系列数据
                for (int i = 0; i < dataset2.getSeriesCount(); i++) {
                    XYSeries series = dataset2.getSeries(i);
                    series.clear();
                }


                for (int i = 0; i < rightDataset2.getSeriesCount(); i++) {
                    XYSeries series = rightDataset2.getSeries(i);
                    series.clear();
                }

                // 清空所有组件的读取值
                JTextField[] allTextFields = {
                        dianzu222 ,dianliu222     ,countTest2,testBianHao2      , sudu222
                };
                for (JTextField textField : allTextFields) {
                    textField.setText("");
                }


            }
        });




        btnclear3= createStyledButton("清除",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        placeButton(panel3, btnclear3, 1280, 74, 100, 42);
        btnclear3.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                resetChartData3();

                timeSeconds3=0;

                // 清空第一个数据集中的所有系列数据
                for (int i = 0; i < dataset3.getSeriesCount(); i++) {
                    XYSeries series = dataset3.getSeries(i);
                    series.clear();
                }


                for (int i = 0; i < rightDataset3.getSeriesCount(); i++) {
                    XYSeries series = rightDataset3.getSeries(i);
                    series.clear();
                }

                // 清空所有组件的读取值
                JTextField[] allTextFields = {
                        dianzu333 ,dianliu333     ,countTest3,testBianHao3       ,sudu333
                };
                for (JTextField textField : allTextFields) {
                    textField.setText("");
                }


            }
        });




        btnclear4= createStyledButton("清除",  buttonFont,new Color(3, 39, 206) ,new Color(3, 39, 206, 197));
        placeButton(panel4, btnclear4, 1280, 74, 100, 42);
        btnclear4.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                resetChartData4();

                timeSeconds4=0;

                // 清空第一个数据集中的所有系列数据
                for (int i = 0; i < dataset4.getSeriesCount(); i++) {
                    XYSeries series = dataset4.getSeries(i);
                    series.clear();
                }


                for (int i = 0; i < rightDataset4.getSeriesCount(); i++) {
                    XYSeries series = rightDataset4.getSeries(i);
                    series.clear();
                }

                // 清空所有组件的读取值
                JTextField[] allTextFields = {
                        dianzu444 ,dianliu444     ,countTest4,testBianHao4    ,sudu444
                };
                for (JTextField textField : allTextFields) {
                    textField.setText("");
                }


            }
        });




/*        exitbtn1.addActionListener(e -> {

            double dianzumax111 = ExecuteCommon.getDianzumaxValue1(); // 从数据库中获取最新的 dianzumax 值
            String text = dianzu111.getText().trim();  // 先去除首尾空格

            // 提取数字部分（假设文本框内容类似 "10.5Ω" 或 "15 mΩ"）
            double value =extractNumber(text);

            if (value > dianzumax111) {



                saveToPolicetimeTable();

                JOptionPane.showMessageDialog(null, "接点1电阻值为"+value+"大于"+dianzumax111, "错误", JOptionPane.ERROR_MESSAGE);
            }     else {
                JOptionPane.showMessageDialog(null, "接点1电阻值为"+value+"小于"+dianzumax111);
            }


        });*/




        exitbtn1.addActionListener(e -> {

            if (isTestingStarted1) {
                JOptionPane.showMessageDialog(null, "正在测试中，请停止测试并保存后再退出！");
                return;
            }

            if (isDataUnsaved) {
                int option = JOptionPane.showConfirmDialog(null, "存在未保存的数据，点击确定将退出！",
                        "提示", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.CANCEL_OPTION) {
                    return;  // 用户选择取消，不退出
                }
                // 如果用户点击了"确定"，则继续执行退出操作
            }




            //还差 开始测试和 连续测试的 监听器没有移除


            // 清空第一个数据集中的所有系列数据
            for (int i = 0; i < dataset1.getSeriesCount(); i++) {
                XYSeries series = dataset1.getSeries(i);
                series.clear();
            }


            for (int i = 0; i < rightDataset1.getSeriesCount(); i++) {
                XYSeries series = rightDataset1.getSeries(i);
                series.clear();
            }

            // 清空所有组件的读取值
            JTextField[] allTextFields = {
                    dianzu111 ,dianliu111     ,countTest1,testBianHao1       ,sudu111
            };
            for (JTextField textField : allTextFields) {
                textField.setText("");
            }


            updateImage(label11111, "stop");

            parentFrame.showCard("Home");  // 切换到“Home”界面
        });




        exitbtn2.addActionListener(e -> {
            if (isTestingStarted2) {
                JOptionPane.showMessageDialog(null, "正在测试中，请停止测试并保存后再退出！");
                return;
            }

            if (isDataUnsaved2) {
                int option = JOptionPane.showConfirmDialog(null, "存在未保存的数据，点击确定将退出！",
                        "提示", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.CANCEL_OPTION) {
                    return;  // 用户选择取消，不退出
                }
                // 如果用户点击了"确定"，则继续执行退出操作
            }


            // 清空第一个数据集中的所有系列数据
            for (int i = 0; i < dataset2.getSeriesCount(); i++) {
                XYSeries series = dataset2.getSeries(i);
                series.clear();
            }

            for (int i = 0; i < rightDataset2.getSeriesCount(); i++) {
                XYSeries series = rightDataset2.getSeries(i);
                series.clear();
            }

            // 清空所有组件的读取值
            JTextField[] allTextFields = {
                    dianzu222  ,dianliu222    ,countTest2  ,   testBianHao2   ,sudu222
            };
            for (JTextField textField : allTextFields) {
                textField.setText("");
            }

            updateImage(label22222, "stop");

            parentFrame.showCard("Home");  // 切换到“Home”界面
        });



        exitbtn3.addActionListener(e -> {

            if (isTestingStarted3) {
                JOptionPane.showMessageDialog(null, "正在测试中，请停止测试并保存后再退出！");
                return;
            }

            if (isDataUnsaved3) {
                int option = JOptionPane.showConfirmDialog(null, "存在未保存的数据，点击确定将退出！",
                        "提示", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.CANCEL_OPTION) {
                    return;  // 用户选择取消，不退出
                }
                // 如果用户点击了"确定"，则继续执行退出操作
            }


            // 清空第一个数据集中的所有系列数据
            for (int i = 0; i < dataset3.getSeriesCount(); i++) {
                XYSeries series = dataset3.getSeries(i);
                series.clear();
            }

            for (int i = 0; i < rightDataset3.getSeriesCount(); i++) {
                XYSeries series = rightDataset3.getSeries(i);
                series.clear();
            }


            // 清空所有组件的读取值
            JTextField[] allTextFields = {
                    dianzu333  ,dianliu333   ,countTest3 ,   testBianHao3  ,sudu333
            };
            for (JTextField textField : allTextFields) {
                textField.setText("");
            }

            updateImage(label33333, "stop");

            parentFrame.showCard("Home");  // 切换到“Home”界面
        });


        exitbtn4.addActionListener(e -> {

            if (isTestingStarted4) {
                JOptionPane.showMessageDialog(null, "正在测试中，请停止测试并保存后再退出！" );
                return;
            }

            if (isDataUnsaved4) {
                int option = JOptionPane.showConfirmDialog(null, "存在未保存的数据，点击确定将退出！",
                        "提示", JOptionPane.OK_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                if (option == JOptionPane.CANCEL_OPTION) {
                    return;  // 用户选择取消，不退出
                }
                // 如果用户点击了"确定"，则继续执行退出操作
            }


            // 清空第一个数据集中的所有系列数据
            for (int i = 0; i < dataset4.getSeriesCount(); i++) {
                XYSeries series = dataset4.getSeries(i);
                series.clear();
            }

            for (int i = 0; i < rightDataset4.getSeriesCount(); i++) {
                XYSeries series = rightDataset4.getSeries(i);
                series.clear();
            }


            // 清空所有组件的读取值
            JTextField[] allTextFields = {
                    dianzu444  ,dianliu444  ,countTest4 ,   testBianHao4     ,sudu444
            };
            for (JTextField textField : allTextFields) {
                textField.setText("");
            }

            updateImage(label44444, "stop");
            parentFrame.showCard("Home");  // 切换到“Home”界面
        });







        // 创建复选框
        JCheckBox redBox = new JCheckBox("电阻1", true);
        redBox.setBounds(1198, 137, 70, 30);  // 设置复选框位置和大小
        redBox.addActionListener(e -> {
            boolean isSelected = redBox.isSelected();
            renderer.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏线条

            panel1.revalidate();
            panel1.repaint();
        });
        // 创建复选框
        JCheckBox redBox2 = new JCheckBox("电阻2", true);
        redBox2.setBounds(1198, 137, 70, 30);  // 设置复选框位置和大小
        redBox2.addActionListener(e -> {
            boolean isSelected = redBox2.isSelected();
            renderer2.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏线条

            panel2.revalidate();
            panel2.repaint();
        });


        // 创建复选框
        JCheckBox redBox3 = new JCheckBox("电阻3", true);
        redBox3.setBounds(1198, 137, 70, 30);  // 设置复选框位置和大小
        redBox3.addActionListener(e -> {
            boolean isSelected = redBox3.isSelected();
            renderer3.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏线条

            panel3.revalidate();
            panel3.repaint();
        });



        // 创建复选框
        JCheckBox redBox4 = new JCheckBox("电阻4", true);
        redBox4.setBounds(1198, 137, 70, 30);  // 设置复选框位置和大小
        redBox4.addActionListener(e -> {
            boolean isSelected = redBox4.isSelected();
            renderer4.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏线条

            panel4.revalidate();
            panel4.repaint();
        });




// 为右侧Y轴控制电流显示的复选框
        JCheckBox rightSeries1Box = new JCheckBox("电流1", true);
        rightSeries1Box.setBounds(1254, 137, 70, 30);  // 设置复选框位置和大小
        rightSeries1Box.addActionListener(e -> {
            boolean isSelected = rightSeries1Box.isSelected();

            rightRenderer.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏电流线条
            rightRenderer.setSeriesShapesVisible(0, isSelected); // 控制点显示/隐藏

            panel1.revalidate();
            panel1.repaint();
        });

        JCheckBox rightSeries2Box = new JCheckBox("电流2", true);
        rightSeries2Box.setBounds(1254, 137, 70, 30);  // 设置复选框位置和大小
        rightSeries2Box.addActionListener(e -> {
            boolean isSelected = rightSeries2Box.isSelected();

            rightRenderer2.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏电流线条
            rightRenderer2.setSeriesShapesVisible(0, isSelected); // 控制点显示/隐藏

            panel2.revalidate();
            panel2.repaint();
        });


        JCheckBox rightSeries3Box = new JCheckBox("电流3", true);
        rightSeries3Box.setBounds(1254, 137, 70, 30);  // 设置复选框位置和大小
        rightSeries3Box.addActionListener(e -> {
            boolean isSelected = rightSeries3Box.isSelected();

            rightRenderer3.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏电流线条
            rightRenderer3.setSeriesShapesVisible(0, isSelected); // 控制点显示/隐藏

            panel3.revalidate();
            panel3.repaint();
        });



        JCheckBox rightSeries4Box = new JCheckBox("电流4", true);
        rightSeries4Box.setBounds(1254, 137, 70, 30);  // 设置复选框位置和大小
        rightSeries4Box.addActionListener(e -> {
            boolean isSelected = rightSeries4Box.isSelected();

            rightRenderer4.setSeriesLinesVisible(0, isSelected);  // 根据复选框状态显示或隐藏电流线条
            rightRenderer4.setSeriesShapesVisible(0, isSelected); // 控制点显示/隐藏

            panel4.revalidate();
            panel4.repaint();
        });




// 添加图表面板和复选框到 panel1
        panel1.add(chartPanel);
        panel1.add(redBox);
        panel1.add(rightSeries1Box);


        panel2.add(chartPanel2);
        panel2.add(redBox2);
        panel2.add(rightSeries2Box);


        panel3.add(chartPanel3);
        panel3.add(redBox3);
        panel3.add(rightSeries3Box);


        panel4.add(chartPanel4);
        panel4.add(redBox4);
        panel4.add(rightSeries4Box);


// 确保容器更新和重绘
        revalidate();
        repaint();


    }


/*
    // 初始化字段时调用这个，注册组件和TAG绑定
    public void registerLabel(String tag, JTextField field) {
        labelMap.put(tag, field);
    }
*/

    private void saveToPolicetimeTable() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest1.getText();
            psmt.setString(1, cishu);

            // 获取 Tingzhizhi 值

            String tingzhizhi = "电阻值故障： "+ dianzu1Value1 + " mΩ"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);


            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 1);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveToPolicetimeTable2() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest2.getText();
            psmt.setString(1, cishu);


            String tingzhizhi ="电阻值故障： "+ dianzu2Value2 + " mΩ"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);


            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 2);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveToPolicetimeTable3() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest3.getText();
            psmt.setString(1, cishu);

            String tingzhizhi = "电阻值故障： "+dianzu3Value3 + " mΩ"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);


            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 3);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveToPolicetimeTable4() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest4.getText();
            psmt.setString(1, cishu);


            String tingzhizhi = "电阻值故障： "+dianzu4Value4 + " mΩ"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);


            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 4);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    private void saveToPolicetimeTabledianliu() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest1.getText();
            psmt.setString(1, cishu);

            // 获取 Tingzhizhi 值
            // 获取 Tingzhizhi 值并添加单位
            String tingzhizhi = "电流值故障： "+dianliu1Value1 + " A"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);

            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 1);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void saveToPolicetimeTabledianliu2() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest2.getText();
            psmt.setString(1, cishu);

            // 获取 Tingzhizhi 值
            String tingzhizhi = "电流值故障： "+dianliu2Value2 + " A"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);

            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 2);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void saveToPolicetimeTabledianliu3() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest3.getText();
            psmt.setString(1, cishu);

            // 获取 Tingzhizhi 值
            String tingzhizhi ="电流值故障： "+dianliu3Value3 + " A"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);

            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 3);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void saveToPolicetimeTabledianliu4() {
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String cishu = countTest4.getText();
            psmt.setString(1, cishu);

            // 获取 Tingzhizhi 值
            String tingzhizhi = "电流值故障： "+dianliu4Value4 + " A"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);

            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, 4);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private void saveToPolicetimeTableStop(int shebeihao,String countTest1 ){
        JdbcDeal jdbcDeal = new JdbcDeal();
        try {
            Connection connection = null;
            try {
                connection = jdbcDeal.getConnection();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            String sql = "INSERT INTO policetime (Cishu, Tingzhizhi, Tingtime, Shebeihao) VALUES (?, ?, ?, ?)";
            PreparedStatement psmt = connection.prepareStatement(sql);

            // 获取 Cishu 值
            String  cishu = countTest1;
            psmt.setString(1, cishu);

            // 获取 Tingzhizhi 值
            // 获取 Tingzhizhi 值并添加单位
            String tingzhizhi = "凸轮转动故障"; // 将电流值和单位连接成一个字符串
            psmt.setString(2, tingzhizhi);

            // 获取当前系统时间作为 Tingtime
            Timestamp tingtime = new Timestamp(System.currentTimeMillis());
            psmt.setTimestamp(3, tingtime);

            // Shebeihao 固定为 1
            psmt.setInt(4, shebeihao);

            // 执行插入操作
            psmt.executeUpdate();

            // 关闭资源
            psmt.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }



/*
    public synchronized Result readAndProcessRegisters() {
        ruAndwone.openSerialPort();
        Sleep.sleepTh(100);

        byte deviceAddress = 0x01;
        int startRegister = 0;
        int registerCount = 8;

        byte[] response = ruAndwone.readInputRegisters(deviceAddress, startRegister, registerCount);

        if (response == null || response.length < 19) {
            System.out.println("未能读取到数据或读取错误");
            return null;
        }

        System.out.println("读取到的响应数据: " + bytesToHex(response));

        int[] values = new int[8];
        for (int i = 0; i < 8; i++) {
            values[i] = ((response[3 + i * 2] & 0xFF) << 8) | (response[4 + i * 2] & 0xFF);
        }

        processValue(values[0], "111");
        processValue(values[1], "222");
        processValue(values[2], "333");
        processValue(values[3], "444");

        return new Result(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
    }

    private void processValue(int value, String tag) {
        if (value != 1) return;

        long currentTime = System.currentTimeMillis();
        long lastTime = lastUpdateTimes.getOrDefault(tag, 0L);
        double timeDiff = (currentTime - lastTime) / 1000.0;

        if (timeDiff >= 4.0) {
            double frequency = 60.0 / timeDiff;

            Queue<Double> buffer = frequencyBufferMap.computeIfAbsent(tag, k -> new LinkedList<>());
            buffer.add(frequency);
            if (buffer.size() > MOVING_AVG_WINDOW) {
                buffer.poll();
            }

            double averageFreq = buffer.stream().mapToDouble(f -> f).average().orElse(frequency);
            long roundedFreq = Math.round(averageFreq);

            System.out.printf(">>> [TAG %s] Δt=%.2fs, raw=%.2fHz, avg=%.2fHz, rounded=%d%n",
                    tag, timeDiff, frequency, averageFreq, roundedFreq);

            JTextField label = labelMap.get(tag);
            if (label != null) {
                SwingUtilities.invokeLater(() -> label.setText(String.valueOf(roundedFreq)));
            }

            lastUpdateTimes.put(tag, currentTime);
        }
    }

*/



/*
    public synchronized Result readAndProcessRegisters() {
        ruAndwone.openSerialPort();
        Sleep.sleepTh(100); // 等待串口响应

        byte deviceAddress = 0x01;
        int startRegister = 0;
        int registerCount = 8;

        byte[] response = ruAndwone.readInputRegisters(deviceAddress, startRegister, registerCount);

        if (response == null || response.length < 19) {
            System.out.println("未能读取到数据或读取错误");
            return null;
        }

        System.out.println("读取到的响应数据: " + bytesToHex(response));

        int[] values = new int[8];
        for (int i = 0; i < 8; i++) {
            values[i] = ((response[3 + i * 2] & 0xFF) << 8) | (response[4 + i * 2] & 0xFF);
        }

        processValue(values[0], "111", sudu111, () -> lastTime111111 = System.currentTimeMillis(), () -> lastTime111111);
        processValue(values[1], "222", sudu222, () -> lastTime222111 = System.currentTimeMillis(), () -> lastTime222111);
        processValue(values[2], "333", sudu333, () -> lastTime333111 = System.currentTimeMillis(), () -> lastTime333111);
        processValue(values[3], "444", sudu444, () -> lastTime444111 = System.currentTimeMillis(), () -> lastTime444111);

        return new Result(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
    }

    private void processValue(int value, String tag, JTextField label, Runnable updateTime, Supplier<Long> getLastTime) {
        if (value != 1) return;

        long currentTime = System.currentTimeMillis();
        double timeDiff = (currentTime - getLastTime.get()) / 1000.0;

        if (timeDiff >= 4) {
            double frequency = 60.0 / timeDiff;
            long roundedFreq = Math.round(frequency);

            System.out.printf("-------------------------frequency%s-----%.2f%n", tag, frequency);
            System.out.printf("-------------------------roundedFrequency%s-----%d%n", tag, roundedFreq);

            SwingUtilities.invokeLater(() -> {
                label.setText(String.valueOf(roundedFreq));
                System.out.printf("-------------------------cishuTest%s-----%d%n", tag, roundedFreq);
            });

            revalidate();
            repaint();
            updateTime.run();

            System.out.printf("-------------------------lastTime%s-----%d%n", tag, currentTime);
        }
    }
*/

/*

    public synchronized  Result readAndProcessRegisters() {

        ruAndwone.openSerialPort();

    //  Sleep.sleepTh(10);       //解决了 读取不到模块的响应码导致的 某个开始按钮监听器失效

   //     response      应数据: 01  04  08    00 00  00  01  00  00  00  00  19  CD

        byte deviceAddress = 0x01; // 设备地址
        int startRegister = 0; // 起始寄存器地址
        int registerCount = 8; // 读取寄存器数量
        byte[] response = ruAndwone.readInputRegisters(deviceAddress, startRegister, registerCount);

        int value00 = ((response[3] & 0xFF) << 8) | (response[4] & 0xFF);
        // 处理响应数据
        if (response.length > 0) {
            System.out.println("读取到输入输出的响应数据: " + bytesToHex(response));
        } else {
            System.out.println("未能读取到数据或读取错误");
        }
        // 检查第二个字节是否为0x04
        if ((response[1] & 0xFF) != 0x04) {
            System.out.println("功能码不符，期望0x04，实际0x等待重试..." +
             String.format("%02X", response[1] & 0xFF) + "，");
            Sleep.sleepTh(10);
        }

            // 提取寄存器值
            int value = ((response[3] & 0xFF) << 8) | (response[4] & 0xFF); // 高字节和低字节组合成寄存器值
            int value2 = ((response[5] & 0xFF) << 8) | (response[6] & 0xFF);
            int value3 = ((response[7] & 0xFF) << 8) | (response[8] & 0xFF);
            int value4 = ((response[9] & 0xFF) << 8) | (response[10] & 0xFF);


            int value5 = ((response[11] & 0xFF) << 8) | (response[12] & 0xFF); // 高字节和低字节组合成寄存器值
            int value6 = ((response[13] & 0xFF) << 8) | (response[14] & 0xFF);
            int value7 = ((response[15] & 0xFF) << 8) | (response[16] & 0xFF);
            int value8 = ((response[17] & 0xFF) << 8) | (response[18] & 0xFF);


            if (value == 1 && !processedjishi1.get()) {
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     亮了  ");


                long currentTime = System.currentTimeMillis();

                // 计算时间差（秒）
                double timeDifference = (currentTime - lastTime111111) / 1000.0;

                if (timeDifference > 2) {


                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     时间差  " + timeDifference);

                    // 计算频率值
                    double frequency = 60.0 / timeDifference;

                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     次数值" + frequency);

                    // 直接四舍五入
                    roundedFrequency111 = Math.round(frequency);


                    // 更新到界面
                    SwingUtilities.invokeLater(() -> {
                        sudu111.setText(String.valueOf(roundedFrequency111)); //
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
            if (value2 == 1 && !processedjishi2.get()) {
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------222     亮了  ");

                long currentTime = System.currentTimeMillis();

                // 计算时间差（秒）
                double timeDifference = (currentTime - lastTime222111) / 1000.0;


                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------122     时间差  " + timeDifference);


                // 计算频率值
                double frequency = 60.0 / timeDifference;

                System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------- 222    次数值" + frequency);

                // 直接四舍五入
                roundedFrequency222 = Math.round(frequency);


                // 更新到界面
                SwingUtilities.invokeLater(() -> {
                    sudu222.setText(String.valueOf(roundedFrequency222)); //
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------222     四舍五入后" + roundedFrequency222);
                    revalidate();
                    repaint();

                });

                revalidate();
                repaint();

                lastTime222111 = currentTime;

                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------222     lastTime222111" + lastTime222111);


                System.out.println(
                        "RRu 2 状态: " + (value2 == 1 ? "1" : "0")
                );
                processedjishi2.set(true);

            } else if (value2 == 0) {
                processedjishi2.set(false);
            }


            if (value3 == 1 && !processedjishi3.get()) {
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     亮了  ");

                long currentTime = System.currentTimeMillis();
                // 计算时间差（秒）
                double timeDifference = (currentTime - lastTime333111) / 1000.0;

                //System.out.println("--------------3333333333333333333333333333333333---------- 计算时间差----"+ timeDifference    );


                if (timeDifference > 2) {

                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     时间差  " + timeDifference);


                    // 计算频率值
                    double frequency = 60.0 / timeDifference;
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     次数值" + frequency);

                    // 直接四舍五入
                    roundedFrequency333 = Math.round(frequency);

                    // 更新到界面
                    SwingUtilities.invokeLater(() -> {
                        sudu333.setText(String.valueOf(roundedFrequency333)); //
                        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     四舍五入后" + roundedFrequency111);
                        revalidate();
                        repaint();

                    });
                    revalidate();
                    repaint();


                    lastTime333111 = currentTime;
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     lastTime333111" + lastTime333111);
                } else {

                }

                System.out.println(
                        "RRu 3 状态: " + (value3 == 1 ? "1" : "0")
                );

                processedjishi3.set(true);

            } else if (value3 == 0) {
                processedjishi3.set(false);
            }

            if (value4 == 1 && !processedjishi4.get()) {
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     亮了  ");

                long currentTime = System.currentTimeMillis();


                // 计算时间差（秒）
                double timeDifference = (currentTime - lastTime444111) / 1000.0;

                if (timeDifference > 2) {

                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     时间差  " + timeDifference);

                    // 计算频率值
                    double frequency = 60.0 / timeDifference;

                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     次数值" + frequency);


                    // 直接四舍五入
                    roundedFrequency444 = Math.round(frequency);

                    System.out.println("-------------------------roundedFrequency444-----" + roundedFrequency444);


                    // 更新到界面
                    SwingUtilities.invokeLater(() -> {
                        sudu444.setText(String.valueOf(roundedFrequency444)); //
                        System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     四舍五入后" + roundedFrequency444);
                        revalidate();
                        repaint();

                    });


                    revalidate();
                    repaint();

                    lastTime444111 = currentTime;
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     lastTime444" + lastTime444111);


                } else {

                }

                processedjishi4.set(true);

            } else if (value4 == 0) {
                processedjishi4.set(false);
            }


            // 返回封装的结果
            return new Result(value, value2, value3, value4, value5, value6, value7, value8);

    }
*/


    public synchronized Result readAndProcessRegisters() {
        byte deviceAddress = 0x01; // 设备地址
        int startRegister = 0; // 起始寄存器地址
        int registerCount = 8; // 读取寄存器数量
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
        int value2 = ((response[5] & 0xFF) << 8) | (response[6] & 0xFF);
        int value3 = ((response[7] & 0xFF) << 8) | (response[8] & 0xFF);
        int value4 = ((response[9] & 0xFF) << 8) | (response[10] & 0xFF);
        int value5 = ((response[11] & 0xFF) << 8) | (response[12] & 0xFF);
        int value6 = ((response[13] & 0xFF) << 8) | (response[14] & 0xFF);
        int value7 = ((response[15] & 0xFF) << 8) | (response[16] & 0xFF);
        int value8 = ((response[17] & 0xFF) << 8) | (response[18] & 0xFF);


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
                    sudu111.setText(String.valueOf(roundedFrequency111)); //
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
        if (value2 == 1 && !processedjishi2.get()) {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------222     亮了  ");

            long currentTime = System.currentTimeMillis();

            // 计算时间差（秒）
            double timeDifference = (currentTime - lastTime222111) / 1000.0;


            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------122     时间差  " + timeDifference);


            // 计算频率值
            double frequency = 120.0 / timeDifference;

            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------- 222    次数值" + frequency);

            // 直接四舍五入
            roundedFrequency222 = Math.round(frequency);


            // 更新到界面
            SwingUtilities.invokeLater(() -> {
                sudu222.setText(String.valueOf(roundedFrequency222)); //
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------222     四舍五入后" + roundedFrequency222);
                revalidate();
                repaint();

            });

            revalidate();
            repaint();

            lastTime222111 = currentTime;

            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------222     lastTime222111" + lastTime222111);


            System.out.println(
                    "RRu 2 状态: " + (value2 == 1 ? "1" : "0")
            );
            processedjishi2.set(true);

        } else if (value2 == 0) {
            processedjishi2.set(false);
        }


        if (value3 == 1 && !processedjishi3.get()) {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     亮了  ");

            long currentTime = System.currentTimeMillis();
            // 计算时间差（秒）
            double timeDifference = (currentTime - lastTime333111) / 1000.0;

            //System.out.println("--------------3333333333333333333333333333333333---------- 计算时间差----"+ timeDifference    );


            if (timeDifference > 2) {

                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     时间差  " + timeDifference);


                // 计算频率值
                double frequency = 120.0 / timeDifference;
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     次数值" + frequency);

                // 直接四舍五入
                roundedFrequency333 = Math.round(frequency);

                // 更新到界面
                SwingUtilities.invokeLater(() -> {
                    sudu333.setText(String.valueOf(roundedFrequency333)); //
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     四舍五入后" + roundedFrequency111);
                    revalidate();
                    repaint();

                });
                revalidate();
                repaint();


                lastTime333111 = currentTime;
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------333     lastTime333111" + lastTime333111);
            } else {

            }

            System.out.println(
                    "RRu 3 状态: " + (value3 == 1 ? "1" : "0")
            );

            processedjishi3.set(true);

        } else if (value3 == 0) {
            processedjishi3.set(false);
        }

        if (value4 == 1 && !processedjishi4.get()) {
            System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     亮了  ");

            long currentTime = System.currentTimeMillis();


            // 计算时间差（秒）
            double timeDifference = (currentTime - lastTime444111) / 1000.0;

            if (timeDifference > 2) {

                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     时间差  " + timeDifference);

                // 计算频率值
                double frequency = 120.0 / timeDifference;

                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     次数值" + frequency);


                // 直接四舍五入
                roundedFrequency444 = Math.round(frequency);

                System.out.println("-------------------------roundedFrequency444-----" + roundedFrequency444);


                // 更新到界面
                SwingUtilities.invokeLater(() -> {
                    sudu444.setText(String.valueOf(roundedFrequency444)); //
                    System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------111     四舍五入后" + roundedFrequency444);
                    revalidate();
                    repaint();

                });


                revalidate();
                repaint();

                lastTime444111 = currentTime;
                System.out.println("----------------------------------------------------------------------------------------------------------------------------------------------------------------444     lastTime444" + lastTime444111);


            } else {

            }

            processedjishi4.set(true);

        } else if (value4 == 0) {
            processedjishi4.set(false);
        }

        // Return the result
        return new Result(value, value2, value3, value4, value5, value6, value7, value8);
    }



    private JLabel addLabel(String text, int x, int y) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("宋体", Font.BOLD, 19));
        label.setForeground(Color.BLACK);
        label.setSize(180, 30); // 指定标签大小
        label.setLocation(x, y); // 指定标签位置
        return label;
    }

    private NumberAxis  creatxAxischart (    NumberAxis axis) {

        axis.setLabelFont(new Font("SimHei", Font.BOLD, 18)); // 设置字体样式为黑体，加粗，字号为22
        axis.setTickLabelFont(new Font("SimHei", Font.PLAIN, 14));
        axis.setLowerBound(0);// 设置 X 轴的最小值为 0，防止出现负数
        axis.setRange(0, 28);  // 初始X轴范围设置为 0到20
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return axis;
    }


    // 编号
    private JTextField createBianhaoTextField() {
        JTextField tf = new JTextField("");
        tf.setFont(new Font("楷体", Font.BOLD, 24));
        tf.setBounds(110, 13, 150, 32);
        tf.setBackground(new Color(255, 255, 224));
        tf.setHorizontalAlignment(JTextField.CENTER);
        return tf;
    }




    private JLabel XzhoushsihTextField( JLabel tf) {

        tf.setFont(new Font("华文仿宋", Font.BOLD, 20));
        tf.setForeground( Color.RED);
        tf.setBounds(850, 135, 300, 30);
        return tf;
    }



    private JLabel YzhoushsihTextField( JLabel tf) {

        tf.setFont(new Font("华文仿宋", Font.BOLD, 20));
        tf.setForeground(new Color(115, 95, 1, 255)); //
        tf.setBounds(1025, 135, 300, 30); // 根据实际界面调整位置
        return tf;
    }




    private JLabel maxRTextField( JLabel tf) {

        tf.setFont(new Font("黑体", Font.BOLD, 22));
        tf.setBounds(413, 112, 600, 80);
        tf.setForeground( new Color(199, 69, 81));
        tf.setOpaque(false);  // 设置为透明背景
        return tf;
    }


    // 监测次数
    private JTextField createCountTextField() {
        JTextField tf = new JTextField("");
        tf.setFont(new Font("楷体", Font.BOLD, 20));
        tf.setBounds(672, 10, 100, 36);
        tf.setBackground(new Color(255, 255, 224));
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setEditable(false);
        return tf;
    }




    // 监测次数
    private JTextField allcreateCountTextField() {
        JTextField tf = new JTextField("");
        tf.setFont(new Font("楷体", Font.BOLD, 20));
        tf.setBounds(885, 10, 100, 36);
        tf.setBackground(new Color(255, 255, 224));
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setEditable(false);
        tf.setText("0");
        return tf;
    }

    //  日期选择器创建工厂方法
    private JDatePickerImpl createDatePicker(int x, int y) {
        UtilDateModel model = new UtilDateModel(new Date());
        JDatePanelImpl datePanel = new JDatePanelImpl(model, new Properties());
        datePanel.setPreferredSize(new Dimension(300, 200));

        JDatePickerImpl picker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        picker.setBounds(x, y, 180, 36);
        picker.setOpaque(false);

        // 样式设置
        updateTextFieldFont(picker, new Font("宋体", Font.BOLD, 24));
        JFormattedTextField tf = picker.getJFormattedTextField();
        tf.setBackground(new Color(255, 255, 224));
        picker.setBackground(new Color(255, 255, 224));

        return picker;
    }

    // 注意: 可设置使设备停止的电阻最大值，当前最大值
    private JLabel createNoteLabel() {
        JLabel label = new JLabel("<html>注意: 可设置使设备停止的电阻最大值，当前最大值为</html>");
        label.setBounds(20, 112, 600, 80);
        label.setFont(new Font("黑体", Font.BOLD, 15));
        label.setOpaque(false);
        label.setForeground( Color.BLACK); // 使用已定义的Color变量
        return label;
    }



    private JLabel createLabel(String text, int x, int y, int w, int h) {
        JLabel label = new JLabel(text);
        Font labelFont = new Font("宋体", Font.BOLD, 18);
        Color labelColor = Color.RED;
        label.setFont(labelFont);         // 使用已定义的字体
        label.setForeground(labelColor);  // 使用已定义的颜色
        label.setBounds(x, y, w, h);
        return label;
    }






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

            rightSeries1.clear();

            // 重置 timeSeconds 和 maxYValue
            timeSeconds1 = 0;
            maxYValue1 = 1;

            updateXAxis();
            lockYAxisRange();
        });
    }

    private void resetChartData2() {
        SwingUtilities.invokeLater(() -> {
            series2.clear();

            rightSeries2.clear();

            // 重置 timeSeconds 和 maxYValue
            timeSeconds2 = 0;
            maxYValue2 = 1;

            updateXAxis2();
            lockYAxisRange2();
        });
    }

    private void resetChartData3() {
        SwingUtilities.invokeLater(() -> {
            series3.clear();

            rightSeries3.clear();

            // 重置 timeSeconds 和 maxYValue
            timeSeconds3 = 0;
            maxYValue3 = 1;

            updateXAxis3();
            lockYAxisRange3();
        });
    }

    private void resetChartData4() {
        SwingUtilities.invokeLater(() -> {
            series4.clear();

            rightSeries4.clear();

            // 重置 timeSeconds 和 maxYValue
            timeSeconds4 = 0;
            maxYValue4 = 1;

            updateXAxis4();
            lockYAxisRange4();
        });
    }




    private void stopListening1() {
        dianzu111.getDocument().removeDocumentListener(dianzuListener);
        dianliu111 .getDocument().removeDocumentListener(dianzuListener);
    }
    private void startListening1() {
        dianzu111.getDocument().addDocumentListener(dianzuListener);
        dianliu111 .getDocument().addDocumentListener(dianzuListener);
    }

    private void stopListening2() {
        dianzu222.getDocument().removeDocumentListener(dianzuListener2);
        dianliu222 .getDocument().removeDocumentListener(dianzuListener2);
    }
    private void startListening2() {
        dianzu222.getDocument().addDocumentListener(dianzuListener2);
        dianliu222 .getDocument().addDocumentListener(dianzuListener2);
    }


    private void stopListening3() {
        dianzu333.getDocument().removeDocumentListener(dianzuListener3);
        dianliu333 .getDocument().removeDocumentListener(dianzuListener3);
    }
    private void startListening3() {
        dianzu333.getDocument().addDocumentListener(dianzuListener3);
        dianliu333 .getDocument().addDocumentListener(dianzuListener3);
    }

    private void stopListening4() {
        dianzu444.getDocument().removeDocumentListener(dianzuListener4);
        dianliu444 .getDocument().removeDocumentListener(dianzuListener4);
    }
    private void startListening4() {
        dianzu444.getDocument().addDocumentListener(dianzuListener4);
        dianliu444 .getDocument().addDocumentListener(dianzuListener4);
    }




    private DocumentListener dianzuListener = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) { updateChart(); }
        @Override
        public void removeUpdate(DocumentEvent e) {}
        @Override
        public void changedUpdate(DocumentEvent e) {}

        private void updateChart() {
            try {
                String text111 = dianzu111.getText().trim();
                String text444 = dianliu111.getText().trim();

                if (!text111.isEmpty() && !text444.isEmpty()) {
                    double newValue111 = extractNumber(text111);
                    double newValue444 = extractNumber(text444);

                    // 更新全局极值
                    //加----------------------
                    updateMinMaxValues(newValue111);

                    // 添加随机波动（保留原始逻辑）
                    double randomFactor = 0.0001 * Math.floor(Math.random() * 10 + 1);
                    double randomOffset = Math.round(randomFactor * 10000.0) / 10000.0;

                    if (Math.abs(newValue111 - randomOffset) > 0.0000001) {

                        //加----------------------
                        lockYAxisRange();

                        SwingUtilities.invokeLater(() -> {
                            series1.add(timeSeconds1, newValue111);
                            rightSeries1.add(timeSeconds1, newValue444);

                            timeSeconds1 =    timeSeconds1+2;

                            updateXAxis();
                            panel1.revalidate();
                            panel1.repaint();
                        });

                        currentValue111 = text111;
                        currentValue444 = text444;
                    }
                }
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    };

    private DocumentListener dianzuListener2 = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateChart2();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }

        private void updateChart2() {
            try {
                String text111 = dianzu222.getText().trim();

                String text444 = dianliu222 .getText().trim();

                // 确保所有文本框都有值
                if (!text111.isEmpty() && !text444.isEmpty()) {
                    double newValue111 = extractNumber(text111);

                    double newValue444 = extractNumber(text444);


                    // 更新全局极值
                    updateMinMaxValues2(newValue111);


                    // 为每个值添加一个微小的随机数

                    // 生成一个0.0001到0.0010之间的数值，并四舍五入到小数点后4位
                    double randomFactor = 0.0001 * Math.floor(Math.random() * 10 + 1);
                    double      rand1omFactor = Math.round(randomFactor * 10000.0) / 10000.0;// 保留小数点后4位

                    // 比较当前值与之前的值+随机因子的差异
                    if (Math.abs(newValue111 -   rand1omFactor) > 0.0000001 ) {

                        //------------------------- ---------- ---------- ----------     没有右侧Y轴的范围改变    ------------------ ----------
                        // 更新 maxYValue 确保 Y 轴范围足够
                        maxYValue2 = Math.max(maxYValue2, newValue111);


                        lockYAxisRange2(); // 调整 Y 轴范围

                        SwingUtilities.invokeLater(() -> {

                            // 在相同的 timeSeconds 值下更新三个曲线
                            series2.add(timeSeconds2, newValue111);

                            // 更新电流的右侧Y轴
                            rightSeries2 .add(timeSeconds2, newValue444);


                            // 更新完成后，x 轴增加一个单位
                            timeSeconds2 =    timeSeconds2+2;

                            updateXAxis2(); // 更新 x 轴范围
                            panel2.revalidate();
                            panel2.repaint();
                        });

                        // 记录当前值为最新的输入值
                        currentValue111 = text111;
                        currentValue444 = text444;

                    }
                }
            } catch (NumberFormatException ex) {
                // 忽略非数字输入
            }
        }
    };

    private DocumentListener dianzuListener3 = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateChart3();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }

        private void updateChart3() {
            try {
                String text111 = dianzu333.getText().trim();

                String text444 = dianliu333 .getText().trim();

                // 确保所有文本框都有值
                if (!text111.isEmpty() && !text444.isEmpty()) {
                    double newValue111 = extractNumber(text111);
                    double newValue444 = extractNumber(text444);


                    // 更新 maxYValue 确保 Y 轴范围足够
                    maxYValue3 = Math.max(maxYValue3, newValue111);



                    // 更新全局极值
                    updateMinMaxValues3(newValue111);


                    // 为每个值添加一个微小的随机数

                    // 生成一个0.0001到0.0010之间的数值，并四舍五入到小数点后4位
                    double randomFactor = 0.0001 * Math.floor(Math.random() * 10 + 1);
                    double      rand1omFactor = Math.round(randomFactor * 10000.0) / 10000.0;// 保留小数点后4位

                    // 比较当前值与之前的值+随机因子的差异
                    if (Math.abs(newValue111 -   rand1omFactor) > 0.0000001 ) {

                        //------------------------- ---------- ---------- ----------     没有右侧Y轴的范围改变    ------------------ ----------
                        // 更新 maxYValue 确保 Y 轴范围足够
                        maxYValue3= Math.max(maxYValue3, newValue111);

                        lockYAxisRange3(); // 调整 Y 轴范围

                        SwingUtilities.invokeLater(() -> {

                            // 在相同的 timeSeconds 值下更新三个曲线
                            series3.add(timeSeconds3, newValue111);

                            // 更新电流的右侧Y轴
                            rightSeries3 .add(timeSeconds3, newValue444);


                            // 更新完成后，x 轴增加一个单位

                            timeSeconds3 =    timeSeconds3+2;
                            updateXAxis3(); // 更新 x 轴范围
                            panel3.revalidate();
                            panel3.repaint();
                        });

                        // 记录当前值为最新的输入值
                        currentValue111 = text111;
                        currentValue444 = text444;

                    }
                }
            } catch (NumberFormatException ex) {
                // 忽略非数字输入
            }
        }
    };

    private DocumentListener dianzuListener4 = new DocumentListener() {
        @Override
        public void insertUpdate(DocumentEvent e) {
            updateChart4();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {

        }

        @Override
        public void changedUpdate(DocumentEvent e) {

        }

        private void updateChart4() {
            try {
                String text111 = dianzu444.getText().trim();

                String text444 = dianliu444 .getText().trim();

                // 确保所有文本框都有值
                if (!text111.isEmpty() && !text444.isEmpty()) {
                    double newValue111 = extractNumber(text111);

                    double newValue444 = extractNumber(text444);


                    //加----------------------
                    // ----------------------更改Y轴范围-----------------------------------------------------------------------
                    updateMinMaxValues4(newValue111);

                    // 为每个值添加一个微小的随机数

                    // 生成一个0.0001到0.0010之间的数值，并四舍五入到小数点后4位
                    double randomFactor = 0.0001 * Math.floor(Math.random() * 10 + 1);
                    double      rand1omFactor = Math.round(randomFactor * 10000.0) / 10000.0;// 保留小数点后4位

                    // 比较当前值与之前的值+随机因子的差异
                    if (Math.abs(newValue111 -   rand1omFactor) > 0.0000001 ) {

                        //------------------------- ---------- ---------- ----------     没有右侧Y轴的范围改变    ------------------ ----------
                        // 更新 maxYValue 确保 Y 轴范围足够
                        maxYValue4= Math.max(maxYValue4, newValue111);

                        //加----------------------
                        // ----------------------更改Y轴范围-----------------------------------------------------------------------
                        lockYAxisRange4(); // 调整 Y 轴范围

                        SwingUtilities.invokeLater(() -> {

                            // 在相同的 timeSeconds 值下更新三个曲线
                            series4.add(timeSeconds4, newValue111);

                            // 更新电流的右侧Y轴
                            rightSeries4 .add(timeSeconds4, newValue444);


                            // 更新完成后，x 轴增加一个单位
                            timeSeconds4 =    timeSeconds4+2;
                            updateXAxis4(); // 更新 x 轴范围
                            panel4.revalidate();
                            panel4.repaint();
                        });

                        // 记录当前值为最新的输入值
                        currentValue111 = text111;
                        currentValue444 = text444;

                    }
                }
            } catch (NumberFormatException ex) {
                // 忽略非数字输入
            }
        }
    };



/*
    private void lockYAxisRange() {


        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // 确保 maxYValue 是正数
        double adjustedMaxYValue = maxYValue1 > 0 ? maxYValue1 : 1;

        //左侧Y轴
        // 设置 Y 轴范围 [0, adjustedMaxYValue]
        yAxis.setRange(0, adjustedMaxYValue * 1.1); // 增加10%的顶部空间yAxis.setRange(0, adjustedMaxYValue * 1.1); // 增加10%的顶部空间
    }

*/


    private void updateMinMaxValues(double newValue) {
        // 初始化极值
        if (minYValue1 == Double.MAX_VALUE) minYValue1 = newValue;
        if (maxYValue1 == Double.MIN_VALUE) maxYValue1 = newValue;

        // 更新极值
        minYValue1 = Math.min(minYValue1, newValue);
        maxYValue1 = Math.max(maxYValue1, newValue);
    }


    private void updateMinMaxValues2(double newValue) {
        // 初始化极值
        if (minYValue2 == Double.MAX_VALUE) minYValue2 = newValue;
        if (maxYValue2 == Double.MIN_VALUE) maxYValue2 = newValue;

        // 更新极值
        minYValue2 = Math.min(minYValue2, newValue);
        maxYValue2 = Math.max(maxYValue2, newValue);
    }


    private void updateMinMaxValues3(double newValue) {
        // 初始化极值
        if (minYValue3 == Double.MAX_VALUE) minYValue3 = newValue;
        if (maxYValue3 == Double.MIN_VALUE) maxYValue3 = newValue;

        // 更新极值
        minYValue3 = Math.min(minYValue3, newValue);
        maxYValue3 = Math.max(maxYValue3, newValue);
    }


    private void updateMinMaxValues4(double newValue) {
        // 初始化极值
        if (minYValue4 == Double.MAX_VALUE) minYValue4 = newValue;
        if (maxYValue4 == Double.MIN_VALUE) maxYValue4 = newValue;

        // 更新极值
        minYValue4 = Math.min(minYValue4, newValue);
        maxYValue4 = Math.max(maxYValue4, newValue);
    }


    private void lockYAxisRange() {
        XYPlot plot = (XYPlot) chart.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // 处理初始无数据状态
        if (minYValue1 == Double.MAX_VALUE || maxYValue1 == Double.MIN_VALUE) {
            yAxis.setRange(0, 100); // 默认初始范围
            return;
        }

        // 计算动态范围
//        double adjustedMinY = minYValue1 / 2;
//        double adjustedMaxY = maxYValue1 + 50;

        double adjustedMinY = minYValue1 ;
        double adjustedMaxY = maxYValue1 +2; // 保持+10缓冲逻辑

        // 异常值处理
        adjustedMinY = Math.max(adjustedMinY, 0); // 确保不小于0
        adjustedMaxY = Math.max(adjustedMaxY, adjustedMinY + 1); // 确保有效范围

        yAxis.setRange(adjustedMinY, adjustedMaxY);
    }


    private void lockYAxisRange2() {
        XYPlot plot = (XYPlot) chart2.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // 处理初始无数据状态（修正变量名）
        if (minYValue2 == Double.MAX_VALUE || maxYValue2 == Double.MIN_VALUE) {
            yAxis.setRange(0, 100); // 与第一个图表保持一致的默认范围
            return;
        }

        // 动态范围计算（使用当前最新需求参数）
        double adjustedMinY = minYValue2;  // 直接使用最小值（非折半）
        double adjustedMaxY = maxYValue2  +2; // 保持+10缓冲逻辑

        // 异常值处理（与第一个图表保持一致）
        adjustedMinY = Math.max(adjustedMinY, 0); // 强制最小为0
        if (adjustedMaxY <= adjustedMinY) {
            adjustedMaxY = adjustedMinY + 1; // 确保有效范围
        }

        yAxis.setRange(adjustedMinY, adjustedMaxY);
    }


    private void lockYAxisRange3() {
        XYPlot plot = (XYPlot) chart3.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // 处理初始无数据状态（修正变量名）
        if (minYValue3 == Double.MAX_VALUE || maxYValue3 == Double.MIN_VALUE) {
            yAxis.setRange(0, 100); // 与第一个图表保持一致的默认范围
            return;
        }

        // 动态范围计算（使用当前最新需求参数）
        double adjustedMinY = minYValue3;  // 直接使用最小值（非折半）
        double adjustedMaxY = maxYValue3  +2; // 保持+10缓冲逻辑

        // 异常值处理（与第一个图表保持一致）
        adjustedMinY = Math.max(adjustedMinY, 0); // 强制最小为0
        if (adjustedMaxY <= adjustedMinY) {
            adjustedMaxY = adjustedMinY + 1; // 确保有效范围
        }

        yAxis.setRange(adjustedMinY, adjustedMaxY);
    }


    private void lockYAxisRange4() {
        XYPlot plot = (XYPlot) chart4.getPlot();
        NumberAxis yAxis = (NumberAxis) plot.getRangeAxis();

        // 处理初始无数据状态（修正变量名）
        if (minYValue4 == Double.MAX_VALUE || maxYValue4 == Double.MIN_VALUE) {
            yAxis.setRange(0, 100); // 与第一个图表保持一致的默认范围
            return;
        }

        // 动态范围计算（使用当前最新需求参数）
        double adjustedMinY = minYValue4;  // 直接使用最小值（非折半）
        double adjustedMaxY = maxYValue4 +2; // 保持+10缓冲逻辑

        // 异常值处理（与第一个图表保持一致）
        adjustedMinY = Math.max(adjustedMinY, 0); // 强制最小为0
        if (adjustedMaxY <= adjustedMinY) {
            adjustedMaxY = adjustedMinY + 1; // 确保有效范围
        }

        yAxis.setRange(adjustedMinY, adjustedMaxY);
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
        double lowerBound = Math.max(0, timeSeconds1 - windowSize);
        double upperBound = timeSeconds1 ;

        // 确保 upperBound 总是大于 lowerBound，避免异常
        if (upperBound - lowerBound < 1) {
            // 在这种情况下，我们只能直接使用 upperBound + 1 作为范围，不需要再声明它为 final
            plot.getDomainAxis().setRange(lowerBound, lowerBound + 1);
        } else {
            plot.getDomainAxis().setRange(lowerBound, upperBound);
        }


        // 将 X 轴的最大值 实时显示在输入框中
        SwingUtilities.invokeLater(() -> countTest1.setText(String.valueOf((int) upperBound)));

        SwingUtilities.invokeLater(() -> {
            String allcount1 = allcountTest1.getText();
            try {
                int value = Integer.parseInt(allcount1);
                allcountTest1.setText(String.valueOf(value + 2));
            } catch (NumberFormatException e) {
                // 处理文本非数字的情况，例如设置为0或提示错误
                //     allcountTest3.setText("0");
            }
        });

        newXValue = countTest1.getText();
        if (newXValue.isEmpty()) {
            newXValue = "0"; // 如果输入为空，则默认为 0
        }

        // 实时更新 X 轴的最大值
        updateXAxisMaxValue(Integer.parseInt(newXValue));
    }


    private void updateXAxis2() {
        XYPlot plot = (XYPlot) chart2.getPlot();
        double windowSize = 28;  // 设置可视窗口的大小，例如28秒      x轴范围
        double lowerBound = Math.max(0, timeSeconds2 - windowSize);
        double upperBound = timeSeconds2   ;

        // 确保 upperBound 总是大于 lowerBound，避免异常
        if (upperBound - lowerBound < 1) {
            plot.getDomainAxis().setRange(lowerBound, lowerBound + 1);
        } else {
            plot.getDomainAxis().setRange(lowerBound, upperBound);
        }


        // 将 X 轴的最大值 实时显示在输入框中

        SwingUtilities.invokeLater(() -> countTest2.setText(String.valueOf((int) upperBound)));



        //然后获取contTest2d的值 加到allcountTest2中

        SwingUtilities.invokeLater(() -> {
            String allcount2 = allcountTest2.getText();
            try {
                int value = Integer.parseInt(allcount2);
                allcountTest2.setText(String.valueOf(value + 2));
            } catch (NumberFormatException e) {
                // 处理文本非数字的情况，例如设置为0或提示错误
                //      allcountTest2.setText("0");
            }
        });


        newXValue2 = countTest2.getText();

        if (newXValue2.isEmpty()) {
            newXValue2 = "0"; // 如果输入为空，则默认为 0
        }

        // 实时更新 X 轴的最大值
        updateXAxisMaxValue2(Integer.parseInt(newXValue2));
    }


    private void updateXAxis3() {
        XYPlot plot = (XYPlot) chart3.getPlot();
        double windowSize = 28;  // 设置可视窗口的大小，例如28秒      x轴范围
        double lowerBound = Math.max(0, timeSeconds3 - windowSize);
        double upperBound = timeSeconds3 ;

        // 确保 upperBound 总是大于 lowerBound，避免异常
        if (upperBound - lowerBound < 1) {
            // 在这种情况下，我们只能直接使用 upperBound + 1 作为范围，不需要再声明它为 final
            plot.getDomainAxis().setRange(lowerBound, lowerBound + 1);
        } else {
            plot.getDomainAxis().setRange(lowerBound, upperBound);
        }


        // 将 X 轴的最大值 实时显示在输入框中
        SwingUtilities.invokeLater(() -> countTest3.setText(String.valueOf((int) upperBound)));

        SwingUtilities.invokeLater(() -> {
            String allcount3 = allcountTest3.getText();
            try {
                int value = Integer.parseInt(allcount3);
                allcountTest3.setText(String.valueOf(value + 2));
            } catch (NumberFormatException e) {
                // 处理文本非数字的情况，例如设置为0或提示错误
                //     allcountTest3.setText("0");
            }
        });

        newXValue3= countTest3.getText();

        if (newXValue3.isEmpty()) {
            newXValue3 = "0"; // 如果输入为空，则默认为 0
        }
        // 实时更新 X 轴的最大值
        updateXAxisMaxValue3(Integer.parseInt(newXValue3));
    }


    private void updateXAxis4() {
        XYPlot plot = (XYPlot) chart4.getPlot();
        double windowSize = 28;  // 设置可视窗口的大小，例如28秒      x轴范围
        double lowerBound = Math.max(0, timeSeconds4 - windowSize);
        double upperBound = timeSeconds4 ;

        // 确保 upperBound 总是大于 lowerBound，避免异常
        if (upperBound - lowerBound < 1) {
            // 在这种情况下，我们只能直接使用 upperBound + 1 作为范围，不需要再声明它为 final
            plot.getDomainAxis().setRange(lowerBound, lowerBound + 1);
        } else {
            plot.getDomainAxis().setRange(lowerBound, upperBound);
        }


        // 将 X 轴的最大值 实时显示在输入框中
        SwingUtilities.invokeLater(() -> countTest4.setText(String.valueOf((int) upperBound)));


        SwingUtilities.invokeLater(() -> {
            String allcount4 = allcountTest4.getText();
            try {
                int value = Integer.parseInt(allcount4);
                allcountTest4.setText(String.valueOf(value + 2));
            } catch (NumberFormatException e) {
                // 处理文本非数字的情况，例如设置为0或提示错误
                //     allcountTest3.setText("0");
            }
        });

        newXValue4= countTest4.getText();
        if (newXValue4.isEmpty()) {
            newXValue4 = "0"; // 如果输入为空，则默认为 0
        }

        // 实时更新 X 轴的最大值
        updateXAxisMaxValue4(Integer.parseInt(newXValue4));
    }



    // 假设你有一个方法来更新 countTest 的值
    private void updateXAxisMaxValue(int maxXValue) {
        // 更新水平滚动条的最大值和当前值
        horizontalScrollBar1.setMaximum(maxXValue);
        horizontalScrollBar1.setVisibleAmount(28);
        horizontalScrollBar1.setValue(maxXValue - 28);  // 确保滚动条显示在最右侧

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
        horizontalScrollBar1.revalidate();
        horizontalScrollBar1.repaint();
    }

    private void updateXAxisMaxValue2(int maxXValue) {
        // 更新水平滚动条的最大值和当前值
        horizontalScrollBar2.setMaximum(maxXValue);
        horizontalScrollBar2.setVisibleAmount(28);
        horizontalScrollBar2.setValue(maxXValue - 28);  // 确保滚动条显示在最右侧

        // 更新图表的 X 轴范围
        XYPlot plot = (XYPlot) chart2.getPlot();
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
        horizontalScrollBar2.revalidate();
        horizontalScrollBar2.repaint();
    }

    private void updateXAxisMaxValue3(int maxXValue) {
        // 更新水平滚动条的最大值和当前值
        horizontalScrollBar3.setMaximum(maxXValue);
        horizontalScrollBar3.setVisibleAmount(28);
        horizontalScrollBar3.setValue(maxXValue - 28);  // 确保滚动条显示在最右侧

        // 更新图表的 X 轴范围
        XYPlot plot = (XYPlot) chart3.getPlot();
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
        horizontalScrollBar3.revalidate();
        horizontalScrollBar3.repaint();
    }

    private void updateXAxisMaxValue4(int maxXValue) {
        // 更新水平滚动条的最大值和当前值
        horizontalScrollBar4.setMaximum(maxXValue);
        horizontalScrollBar4.setVisibleAmount(28);
        horizontalScrollBar4.setValue(maxXValue - 28);  // 确保滚动条显示在最右侧

        // 更新图表的 X 轴范围
        XYPlot plot = (XYPlot) chart4.getPlot();
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
        horizontalScrollBar4.revalidate();
        horizontalScrollBar4.repaint();
    }






    // 根据传入的显示状态更新图片的方法
    private static void updateImage(JLabel label, String status) {
        ImageIcon iconToShow;

        // 根据状态选择不同的图片
        switch (status) {
            case "stop":
                iconToShow = imageStop;
                break;
            case "Y":
                iconToShow = imageY;
                break;
            case "N":
                iconToShow = imageN;
                break;
            default:
                iconToShow = imageStop;  // 如果传入的状态不符合预期，默认为imageN
                break;
        }

        // 缩放图片
        Image image = iconToShow.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(image);

        // 设置缩放后的图标到标签
        label.setIcon(scaledIcon);
    }

    // 辅助方法：格式化平均值结果
    private String formatResult(double averageInMOhm) {
        String unit;
        double value;

        unit = " mΩ";
        value = averageInMOhm;

        return String.format("%.2f", value) + unit;
    }


    public void refreshDianzumaxValueid1() {
        dianzumax1 = ExecuteCommon.getDianzumaxValue1();
        labeldianzumax1.setText(dianzumax1 + " mΩ");
        labeldianzumax1.repaint();
    }
    public void refreshDianzumaxValueid2() {

        dianzumax2 = ExecuteCommon.getDianzumaxValue2();
        labeldianzumax2.setText(dianzumax2 + " mΩ");
        labeldianzumax2.repaint();
    }



    public void refreshDianzumaxValueid3() {

        dianzumax3 = ExecuteCommon.getDianzumaxValue3();
        labeldianzumax3.setText(dianzumax3 + " mΩ");
        labeldianzumax3.repaint();

    }

    public void refreshDianzumaxValueid4() {

        dianzumax4 = ExecuteCommon.getDianzumaxValue4();
        labeldianzumax4.setText(dianzumax4 + " mΩ");
        labeldianzumax4.repaint();
    }



    public void refreshDianzumaxValue() {
        dianzumax1 = ExecuteCommon.getDianzumaxValue1();
        labeldianzumax1.setText(dianzumax1 + " mΩ");
        labeldianzumax1.repaint();

        dianzumax2 = ExecuteCommon.getDianzumaxValue2();
        labeldianzumax2.setText(dianzumax2 + " mΩ");
        labeldianzumax2.repaint();

        dianzumax3 = ExecuteCommon.getDianzumaxValue3();
        labeldianzumax3.setText(dianzumax3 + " mΩ");
        labeldianzumax3.repaint();

        dianzumax4 = ExecuteCommon.getDianzumaxValue4();
        labeldianzumax4.setText(dianzumax4 + " mΩ");
        labeldianzumax4.repaint();


    }


    public void refreshtestCountValueid1() {
        testcount1 = ExecuteCommon.getAllcountValue1(1);
        allcountTest1.setText(String.valueOf(testcount1));
        allcountTest1.repaint();
    }


    public void refreshtestCountValueid2() {

        testcount2 = ExecuteCommon.getAllcountValue1(2);
        allcountTest2.setText(String.valueOf(testcount2));
        allcountTest2.repaint();
    }

    public void refreshtestCountValueid3() {

        testcount3 = ExecuteCommon.getAllcountValue1(3);
        allcountTest3.setText(String.valueOf(testcount3));
        allcountTest3.repaint();

    }

    public void refreshtestCountValueid4() {
        testcount4 = ExecuteCommon.getAllcountValue1(4);
        allcountTest4.setText(String.valueOf(testcount4));
        allcountTest4.repaint();
    }



    public void refreshtestCountValue() {
        testcount1 = ExecuteCommon.getAllcountValue1(1);
        allcountTest1.setText(String.valueOf(testcount1));
        allcountTest1.repaint();

        testcount2 = ExecuteCommon.getAllcountValue1(2);
        allcountTest2.setText(String.valueOf(testcount2));
        allcountTest2.repaint();

        testcount3 = ExecuteCommon.getAllcountValue1(3);
        allcountTest3.setText(String.valueOf(testcount3));
        allcountTest3.repaint();

        testcount4 = ExecuteCommon.getAllcountValue1(4);
        allcountTest4.setText(String.valueOf(testcount4));
        allcountTest4.repaint();
    }




    // 生成一个随机的电阻值
    private double generateRandomValue() {
        return 40 + random.nextDouble() * 10; // 生成 40 到 50 之间的随机值
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



/*

    private static JTextField createCustomTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setForeground(Color.GREEN);
        textField.setBackground(Color.DARK_GRAY);
        textField.setHorizontalAlignment(JTextField.LEFT);  // 修改这里
        textField.setFont(new Font("楷体", Font.PLAIN, 36));
        textField.setBounds(x, y, width, height);
        textField.setBorder(new LineBorder(Color.GREEN, 2));
        return textField;
    }
*/



    //很完美的  超过范围后左对齐
    private static JTextField createCustomTextField(int x, int y, int width, int height) {
        JTextField textField = new JTextField() {
            // 重写绘制方法
            @Override
            protected void paintComponent(Graphics g) {
                // 强制滚动偏移量为0（始终从最左侧开始绘制）
                setScrollOffset(0);
                super.paintComponent(g);
            }
        };

        textField.setForeground(Color.GREEN);
        textField.setBackground(Color.DARK_GRAY);
        textField.setHorizontalAlignment(JTextField.CENTER); // 保持左对齐
        textField.setFont(new Font("楷体", Font.PLAIN, 36));
        textField.setBounds(x, y, width, height);
        textField.setBorder(new LineBorder(Color.GREEN, 2));

        // 添加文本监听器（应对动态输入）
        textField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { updateScroll(); }
            public void removeUpdate(DocumentEvent e) { updateScroll(); }
            public void changedUpdate(DocumentEvent e) {}

            private void updateScroll() {
                // 强制在文本变化后重置滚动
                SwingUtilities.invokeLater(() -> {
                    textField.setScrollOffset(0);
                });
            }
        });

        return textField;
    }




    private static JTextField createCustomTextFieldsudu(int x, int y, int width, int height) {
        JTextField textField = new JTextField();
        textField.setForeground(Color.GREEN);
        textField.setBackground(Color.DARK_GRAY);
        textField.setHorizontalAlignment(JTextField.CENTER);
        textField.setFont(new Font("楷体", Font.PLAIN, 22));
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
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);      //调整按钮的圆角大小
            super.paintComponent(g);
            g2.dispose();
        }

        public void setMouseOver(boolean mouseOver) {
            this.mouseOver = mouseOver;
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
