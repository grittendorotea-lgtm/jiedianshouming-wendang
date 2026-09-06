package com.ytzg.sealer.comm.caidanduqu;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 八个仪表统一读取器
 *
 * 地址分配：
 * 01、02、03、04：电流表
 * 05、06、07、08：电阻表
 *
 * 读取协议保持原程序不变：
 * 功能码：03
 * 起始寄存器：1
 * 寄存器数量：3
 */
public class SixMeterInstrumentReader {

    private final String portName;
    private final SerialPort comPort;

    // 数值缓存
    private final ConcurrentHashMap<String, Double> instrumentValues = new ConcurrentHashMap<>();

    // 文本缓存（带单位）
    private final ConcurrentHashMap<String, String> instrumentTextValues = new ConcurrentHashMap<>();

    private ScheduledExecutorService queryScheduler;
    private volatile boolean running = false;
    private int queryIndex = 0;

    public SixMeterInstrumentReader(String portName) {
        this.portName = portName;
        this.comPort = SerialPort.getCommPort(portName);

        this.comPort.setBaudRate(9600);
        this.comPort.setNumDataBits(8);
        this.comPort.setNumStopBits(SerialPort.ONE_STOP_BIT);
        this.comPort.setParity(SerialPort.NO_PARITY);
        this.comPort.setComPortTimeouts(
                SerialPort.TIMEOUT_NONBLOCKING,
                0,
                0
        );
    }

    /**
     * 启动统一读取
     */
    public void startReading() {

        if (running) {
            return;
        }

        if (!comPort.isOpen()) {

            if (!comPort.openPort()) {
                System.out.println(
                        "SixMeterInstrumentReader 串口打开失败：" + portName
                );
                return;
            }
        }

        System.out.println(
                "SixMeterInstrumentReader 串口已打开：" + portName
        );

        comPort.flushIOBuffers();

        addDataListenerOnce();

        running = true;

        queryScheduler =
                Executors.newScheduledThreadPool(1);

        /*
         * 每100ms查询一个仪表：
         *
         * 01 电流表
         * 02 电流表
         * 03 电流表
         * 04 电流表
         * 05 电阻表
         * 06 电阻表
         * 07 电阻表
         * 08 电阻表
         *
         * 一整轮约800ms
         */
        queryScheduler.scheduleAtFixedRate(() -> {

            try {

                sendNextQuery();

            } catch (Exception e) {

                e.printStackTrace();
            }

        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * 停止读取
     */
    public void stopReading() {

        running = false;

        if (queryScheduler != null
                && !queryScheduler.isShutdown()) {

            queryScheduler.shutdownNow();
        }

        if (comPort != null
                && comPort.isOpen()) {

            comPort.removeDataListener();

            comPort.closePort();

            System.out.println(
                    "SixMeterInstrumentReader 串口关闭"
            );
        }
    }

    /**
     * 轮询01~08地址
     *
     * 01~04 = 电流表
     * 05~08 = 电阻表
     */
    private void sendNextQuery() {

        if (!running || !comPort.isOpen()) {
            return;
        }

        /*
         * queryIndex:
         *
         * 0 -> 地址01
         * 1 -> 地址02
         * 2 -> 地址03
         * 3 -> 地址04
         * 4 -> 地址05
         * 5 -> 地址06
         * 6 -> 地址07
         * 7 -> 地址08
         */
        int addressValue =
                (queryIndex % 8) + 1;

        byte address =
                (byte) addressValue;

        queryIndex++;

        /*
         * 保持你原程序的读取方式：
         *
         * 功能码03
         * 起始寄存器1
         * 读取3个寄存器
         */
        byte[] request =
                buildModbusRequest(
                        address,
                        (byte) 0x03,
                        1,
                        3
                );

        comPort.writeBytes(
                request,
                request.length
        );
    }

    /**
     * 串口监听器只添加一次
     */
    private void addDataListenerOnce() {

        comPort.removeDataListener();

        comPort.addDataListener(
                new SerialPortDataListener() {

                    @Override
                    public int getListeningEvents() {

                        return SerialPort
                                .LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(
                            SerialPortEvent event) {

                        if (event.getEventType()
                                != SerialPort
                                .LISTENING_EVENT_DATA_AVAILABLE) {

                            return;
                        }

                        try {

                            /*
                             * 等待一帧数据基本接收完整
                             */
                            Thread.sleep(80);

                        } catch (
                                InterruptedException e) {

                            Thread.currentThread()
                                    .interrupt();

                            return;
                        }

                        int available =
                                comPort.bytesAvailable();

                        if (available <= 0) {
                            return;
                        }

                        byte[] data =
                                new byte[available];

                        comPort.readBytes(
                                data,
                                data.length
                        );

                        processSensorData(data);
                    }
                }
        );
    }

    /**
     * 统一解析仪表响应
     *
     * 01~04 -> 电流表解析
     * 05~08 -> 电阻表解析
     */
    private void processSensorData(
            byte[] data) {

        if (data == null
                || data.length < 9) {

            System.out.println(
                    "响应数据太短，无法解析"
            );

            return;
        }

        /*
         * CRC校验
         */
        int crc =
                calculateCRC(
                        data,
                        data.length - 2
                );

        int receivedCrc =
                (data[data.length - 2] & 0xFF)
                        |
                        ((data[data.length - 1]
                                & 0xFF) << 8);

        if (crc != receivedCrc) {

            System.out.println(
                    "CRC 校验失败"
            );

            return;
        }

        /*
         * data[0] = Modbus从站地址
         */
        int address =
                data[0] & 0xFF;

        /*
         * 保持原程序的数据格式
         */
        int rawValue =
                ((data[3] & 0xFF) << 8)
                        |
                        (data[4] & 0xFF);

        int decimalPlaces =
                data[6] & 0xFF;

        int unitCode =
                data[8] & 0xFF;

        /*
         * 地址01~04：
         * 全部作为电流表
         */
        if (address >= 0x01
                && address <= 0x04) {

            parseCurrent(
                    address,
                    rawValue,
                    decimalPlaces,
                    unitCode
            );

            return;
        }

        /*
         * 地址05~08：
         * 全部作为电阻表
         */
        if (address >= 0x05
                && address <= 0x08) {

            parseResistance(
                    address,
                    rawValue,
                    decimalPlaces,
                    unitCode
            );

            return;
        }

        System.out.println(
                "未知仪表地址："
                        + String.format(
                        "%02X",
                        address
                )
        );
    }

    /**
     * 按小数位格式化数值
     */
    private String formatByDecimalPlaces(
            int rawValue,
            int decimalPlaces) {

        int scale =
                Math.max(
                        0,
                        Math.min(
                                decimalPlaces,
                                3
                        )
                );

        BigDecimal value =
                BigDecimal
                        .valueOf(rawValue)
                        .movePointLeft(scale)
                        .setScale(
                                scale,
                                RoundingMode.HALF_UP
                        );

        return value.toPlainString();
    }

    /**
     * 转double
     */
    private double parseNumericValue(
            int rawValue,
            int decimalPlaces) {

        int scale =
                Math.max(
                        0,
                        Math.min(
                                decimalPlaces,
                                3
                        )
                );

        return BigDecimal
                .valueOf(rawValue)
                .movePointLeft(scale)
                .doubleValue();
    }

    /**
     * 电流单位解析
     */
    private String resolveCurrentUnit(
            int unitCode) {

        switch (unitCode) {

            case 3:
                return "mA";

            case 4:
                return "A";

            case 5:
                return "kA";

            default:
                return "";
        }
    }

    /**
     * 电阻单位解析
     */
    private String resolveResistanceUnit(
            int unitCode) {

        switch (unitCode) {

            case 3:
                return "mΩ";

            case 4:
                return "Ω";

            case 5:
                return "KΩ";

            case 6:
                return "MΩ";

            default:
                return "Ω";
        }
    }

    // =====================================================
    // 电流表：地址01 02 03 04
    // =====================================================

    private void parseCurrent(
            int address,
            int rawValue,
            int decimalPlaces,
            int unitCode) {

        double value =
                parseNumericValue(
                        rawValue,
                        decimalPlaces
                );

        String number =
                formatByDecimalPlaces(
                        rawValue,
                        decimalPlaces
                );

        String unit =
                resolveCurrentUnit(
                        unitCode
                );

        String text =
                unit.isEmpty()
                        ? number
                        : number + " " + unit;

        /*
         * 地址01：
         * current_01
         *
         * 地址02：
         * current_02
         *
         * 地址03：
         * current_03
         *
         * 地址04：
         * current_04
         */
        String key =
                buildCurrentKey(
                        address
                );

        instrumentValues.put(
                key,
                value
        );

        instrumentTextValues.put(
                key,
                text
        );
    }

    // =====================================================
    // 电阻表：地址05 06 07 08
    // =====================================================

    private void parseResistance(
            int address,
            int rawValue,
            int decimalPlaces,
            int unitCode) {

        double value =
                parseNumericValue(
                        rawValue,
                        decimalPlaces
                );

        String number =
                formatByDecimalPlaces(
                        rawValue,
                        decimalPlaces
                );

        String unit =
                resolveResistanceUnit(
                        unitCode
                );

        String text =
                unit.isEmpty()
                        ? number
                        : number + " " + unit;

        /*
         * 地址05：
         * resistance_05
         *
         * 地址06：
         * resistance_06
         *
         * 地址07：
         * resistance_07
         *
         * 地址08：
         * resistance_08
         */
        String key =
                buildResistanceKey(
                        address
                );

        instrumentValues.put(
                key,
                value
        );

        instrumentTextValues.put(
                key,
                text
        );
    }

    /**
     * 生成电流表缓存Key
     */
    private String buildCurrentKey(
            int address) {

        return String.format(
                "current_%02d",
                address
        );
    }

    /**
     * 生成电阻表缓存Key
     */
    private String buildResistanceKey(
            int address) {

        return String.format(
                "resistance_%02d",
                address
        );
    }

    // =====================================================
    // 通用取值接口
    // =====================================================

    public double getInstrumentValue(
            String key) {

        return instrumentValues
                .getOrDefault(
                        key,
                        0.0
                );
    }

    public String getInstrumentTextValue(
            String key) {

        return instrumentTextValues
                .getOrDefault(
                        key,
                        "--"
                );
    }

    // =====================================================
    // 电流表取值接口 01~04
    // =====================================================

    public double getCurrentValue(
            int address) {

        if (address < 1
                || address > 4) {

            return 0.0;
        }

        return getInstrumentValue(
                buildCurrentKey(address)
        );
    }

    public String getCurrentTextValue(
            int address) {

        if (address < 1
                || address > 4) {

            return "--";
        }

        return getInstrumentTextValue(
                buildCurrentKey(address)
        );
    }

    public double getCurrent01Value() {
        return getCurrentValue(1);
    }

    public double getCurrent02Value() {
        return getCurrentValue(2);
    }

    public double getCurrent03Value() {
        return getCurrentValue(3);
    }

    public double getCurrent04Value() {
        return getCurrentValue(4);
    }

    public String getCurrent01TextValue() {
        return getCurrentTextValue(1);
    }

    public String getCurrent02TextValue() {
        return getCurrentTextValue(2);
    }

    public String getCurrent03TextValue() {
        return getCurrentTextValue(3);
    }

    public String getCurrent04TextValue() {
        return getCurrentTextValue(4);
    }

    // =====================================================
    // 电阻表取值接口 05~08
    // =====================================================

    public double getResistanceValue(
            int address) {

        if (address < 5
                || address > 8) {

            return 0.0;
        }

        return getInstrumentValue(
                buildResistanceKey(address)
        );
    }

    public String getResistanceTextValue(
            int address) {

        if (address < 5
                || address > 8) {

            return "--";
        }

        return getInstrumentTextValue(
                buildResistanceKey(address)
        );
    }

    public double getResistance05Value() {
        return getResistanceValue(5);
    }

    public double getResistance06Value() {
        return getResistanceValue(6);
    }

    public double getResistance07Value() {
        return getResistanceValue(7);
    }

    public double getResistance08Value() {
        return getResistanceValue(8);
    }

    public String getResistance05TextValue() {
        return getResistanceTextValue(5);
    }

    public String getResistance06TextValue() {
        return getResistanceTextValue(6);
    }

    public String getResistance07TextValue() {
        return getResistanceTextValue(7);
    }

    public String getResistance08TextValue() {
        return getResistanceTextValue(8);
    }

    // =====================================================
    // Modbus RTU工具
    // =====================================================

    private static byte[] buildModbusRequest(
            byte address,
            byte functionCode,
            int startAddress,
            int numRegisters) {

        byte[] request =
                new byte[8];

        request[0] =
                address;

        request[1] =
                functionCode;

        request[2] =
                (byte)
                        (startAddress >> 8);

        request[3] =
                (byte)
                        (startAddress & 0xFF);

        request[4] =
                (byte)
                        (numRegisters >> 8);

        request[5] =
                (byte)
                        (numRegisters & 0xFF);

        int crc =
                calculateCRC(
                        request,
                        6
                );

        request[6] =
                (byte)
                        (crc & 0xFF);

        request[7] =
                (byte)
                        (crc >> 8);

        return request;
    }

    /**
     * Modbus CRC16
     */
    private static int calculateCRC(
            byte[] data,
            int length) {

        int crc =
                0xFFFF;

        for (int j = 0;
             j < length;
             j++) {

            crc ^=
                    data[j] & 0xFF;

            for (int i = 8;
                 i > 0;
                 i--) {

                if ((crc & 0x0001)
                        != 0) {

                    crc >>= 1;

                    crc ^= 0xA001;

                } else {

                    crc >>= 1;
                }
            }
        }

        return crc;
    }

    // =====================================================
    // 测试显示
    // =====================================================

    public void testDisplayMeters() {

        startReading();

        ScheduledExecutorService
                printScheduler =
                Executors
                        .newScheduledThreadPool(1);

        printScheduler
                .scheduleAtFixedRate(
                        () -> {

                            try {

                                System.out.println(
                                        "======================================"
                                );

                                System.out.println(
                                        "电流表01: "
                                                + getCurrentTextValue(1)
                                );

                                System.out.println(
                                        "电流表02: "
                                                + getCurrentTextValue(2)
                                );

                                System.out.println(
                                        "电流表03: "
                                                + getCurrentTextValue(3)
                                );

                                System.out.println(
                                        "电流表04: "
                                                + getCurrentTextValue(4)
                                );

                                System.out.println(
                                        "电阻表05: "
                                                + getResistanceTextValue(5)
                                );

                                System.out.println(
                                        "电阻表06: "
                                                + getResistanceTextValue(6)
                                );

                                System.out.println(
                                        "电阻表07: "
                                                + getResistanceTextValue(7)
                                );

                                System.out.println(
                                        "电阻表08: "
                                                + getResistanceTextValue(8)
                                );

                            } catch (
                                    Exception e) {

                                e.printStackTrace();
                            }

                        },
                        0,
                        500,
                        TimeUnit.MILLISECONDS
                );
    }

    public static void main(
            String[] args) {

        SixMeterInstrumentReader reader =
                new SixMeterInstrumentReader(
                        "COM1"
                );

        reader.testDisplayMeters();
    }
}
