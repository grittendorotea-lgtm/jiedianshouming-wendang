package com.ytzg.sealer.plc.test;

import com.fazecast.jSerialComm.SerialPort;

public class DianZu00000 {

    private SerialPort comPort;
    private double dianZValue1 = 0.0; // 电阻1
    private double dianZValue2 = 0.0; // 电阻2
    private double dianZValue3 = 0.0; // 电阻3
    private String dianZUnit1 = "mΩ"; // 单位1
    private String dianZUnit2 = "mΩ"; // 单位2
    private String dianZUnit3 = "mΩ"; // 单位3

    public DianZu00000() {

        comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudRate(9600);

    }

    public void startReading() {
        while (true) {
            if (!comPort.isOpen()) {
                if (comPort.openPort()) {
                    System.out.println("串口已打开");
                    break; // 成功打开串口后退出循环
                } else {
                    System.out.println("串口未打开！请检查！");
                    waitForPort();
                }
            } else {
                System.out.println("串口可能被占用，等待中...");
                waitForPort();
            }
        }
    }

    private void waitForPort() {
        try {
            Thread.sleep(50); // 等待 2 秒后重试
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void stopReading() {
        if (comPort.isOpen()) {
            comPort.closePort();
            System.out.println("串口关闭");
        }
    }

    public void readAndStoreValues() {
        for (int address = 2; address <= 4; address++) {
            byte[] response = readRegisters((byte) address);
            if (response.length > 0) {
                double resistanceValue = parseSensorData(response);
                switch (address) {
                    case 2:
                        dianZValue1 = resistanceValue;
                        dianZUnit1 = getDianZUnit();
                        break;
                    case 3:
                        dianZValue2 = resistanceValue;
                        dianZUnit2 = getDianZUnit();
                        break;
                    case 4:
                        dianZValue3 = resistanceValue;
                        dianZUnit3 = getDianZUnit();
                        break;
                }
            }
        }
    }

    public String getDianZu1() {
        return String.format("%.2f %s", dianZValue1, dianZUnit1);
    }

    public String getDianZu2() {
        return String.format("%.2f %s", dianZValue2, dianZUnit2);
    }

    public String getDianZu3() {
        return String.format("%.2f %s", dianZValue3, dianZUnit3);
    }

    public byte[] readRegisters(byte address) {
        System.out.println("请求地址: " + address);
        byte[] request = buildModbusRequest((byte) address, (byte) 0x03, 1, 3);
        comPort.writeBytes(request, request.length);

        try {
            Thread.sleep(50); // 等待数据返回
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int availableBytes = comPort.bytesAvailable();
        if (availableBytes < 9) { // 根据协议判断响应数据长度
            System.out.println("响应数据太短，无法解析");
            return new byte[0];
        }

        byte[] response = new byte[availableBytes];
        comPort.readBytes(response, response.length);
        return response;
    }

    public double parseSensorData(byte[] data) {
        if (data.length < 9) {
            System.out.println("响应数据太短，无法解析");
            return 0;
        }

        // 检查 CRC 校验
        int crc = calculateCRC(data, data.length - 2);
        int responseCrc = ((data[data.length - 1] & 0xff) << 8) | (data[data.length - 2] & 0xff);
        if (crc != responseCrc) {
            System.out.println("CRC 校验失败");
            return 0;
        }

        // 解析电阻值（原始值）
        int rawValue = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);

        // 解析小数点位置
        int decimalPlaces = data[6] & 0xFF;

        // 解析单位
        int unitCode = data[8] & 0xFF;
        return parseResistanceValue(rawValue, decimalPlaces, unitCode);
    }

    private double parseResistanceValue(int rawValue, int decimalPlaces, int unitCode) {
        double resistanceValue;

        switch (unitCode) {
            case 3:
                dianZUnit1 = "mΩ";
                resistanceValue = rawValue / Math.pow(10, decimalPlaces);
                break;
            case 4:
                dianZUnit1 = "Ω";
                resistanceValue = rawValue / Math.pow(10, decimalPlaces);
                break;
            case 5:
                dianZUnit1 = "KΩ";
                resistanceValue = rawValue / Math.pow(10, decimalPlaces);
                break;
            default:
                dianZUnit1 = "未知单位";
                resistanceValue = 0;
                break;
        }

        return resistanceValue;
    }

    public String getDianZUnit() {
        return dianZUnit1;
    }

    private byte[] buildModbusRequest(byte address, byte functionCode, int startAddress, int numRegisters) {
        byte[] request = new byte[8];
        request[0] = address;
        request[1] = functionCode;
        request[2] = (byte) (startAddress >> 8);
        request[3] = (byte) (startAddress & 0xFF);
        request[4] = (byte) (numRegisters >> 8);
        request[5] = (byte) (numRegisters & 0xFF);
        int crc = calculateCRC(request, 6);
        request[6] = (byte) (crc & 0xFF);
        request[7] = (byte) (crc >> 8);
        return request;
    }

    private int calculateCRC(byte[] data, int length) {
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

    public static void main(String[] args) {
        DianZu00000 dianZu = new DianZu00000();

        // 打开串口
        dianZu.startReading();

        while (true) {
            dianZu.readAndStoreValues();

            System.out.println("电阻1: " + dianZu.getDianZu1());
            System.out.println("电阻2: " + dianZu.getDianZu2());
            System.out.println("电阻3: " + dianZu.getDianZu3());
            System.out.println("----------------------------");

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }

        // 关闭串口
        dianZu.stopReading();
    }
}
