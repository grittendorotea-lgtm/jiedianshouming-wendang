package com.ytzg.sealer.plc;
import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;

import java.util.Arrays;


public class JiaoL {

    private SerialPort comPort;
    private double jiaoLValue = 0.0; // 新增一个字段用于保存当前值
    private String jiaoLUnit ="A"; // 保存当前单位


    public JiaoL() {
        comPort = SerialPort.getCommPort("COM3");
        comPort.setBaudRate(9600);
    }


    public void startReading() {
        if (!comPort.openPort()) {
            System.out.println("串口未打开！请检查！！！！!");
            return;
        }
        System.out.println("串口已打开");

        // 发送请求
        byte[] request = buildModbusRequest((byte) 0x01, (byte) 0x03, 1, 3);
        comPort.writeBytes(request, request.length);

        comPort.addDataListener(new SerialPortDataListener() {
            @Override
            public int getListeningEvents() {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
            }

            @Override
            public void serialEvent(SerialPortEvent event) {
                if (event.getEventType() != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return;
                }
                try {
                    Thread.sleep(100); // 等待数据完整
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (comPort.bytesAvailable() > 0) {
                    byte[] newData = new byte[comPort.bytesAvailable()];
                    comPort.readBytes(newData, newData.length);
                    processSensorData(newData);
                }
            }
        });
    }

    public void stopReading() {
        if (comPort.isOpen()) {
            comPort.removeDataListener();
            comPort.closePort();
            System.out.println("串口关闭");
        }
    }

    private void processSensorData(byte[] data) {
        System.out.println("响应数据: " + Arrays.toString(data));

        if (data.length < 9) {
            System.out.println("响应数据太短，无法解析");
            return;
        }
        // 检查 CRC 校验
        int crc = calculateCRC(data, data.length - 2);
        int responseCrc = ((data[data.length - 1] & 0xff) << 8) | (data[data.length - 2] & 0xff);
        if (crc != responseCrc) {
            System.out.println("CRC 校验失败");
            return;
        }

        // 解析电阻值（原始值）
        int rawValue = ((data[3] & 0xFF) << 8) | (data[4] & 0xFF);

        // 解析小数点位置
        int decimalPlaces = data[6] & 0xFF;

        // 解析单位
        int unitCode = data[8] & 0xFF;
        jiaoLValue = parseResistanceValue(rawValue, decimalPlaces, unitCode);
    }
    private double parseResistanceValue(int rawValue, int decimalPlaces, int unitCode) {
        double resistanceValue;

        switch (unitCode) {
            case 3:
                jiaoLUnit = "mA"; // 设置单位
                resistanceValue = rawValue / Math.pow(10, decimalPlaces);
                break;
            case 4:
                jiaoLUnit = "A"; // 设置单位
                resistanceValue = rawValue / Math.pow(10, decimalPlaces);
                break;
            default:
                System.out.println("未知单-------------------------------位代码：" + unitCode);
                jiaoLUnit = "未知单位"; // 设置一个默认值
                return 0;
        }

        return resistanceValue;
    }



    private static byte[] buildModbusRequest(byte address, byte functionCode, int startAddress, int numRegisters) {

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


    private static int calculateCRC(byte[] data, int length) {
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

    public String getJiaoLZValueWithUnit() {
        return jiaoLValue + " " + jiaoLUnit;
    }
    public double getJiaoLValue () {

        return jiaoLValue ;
    }

    public static void main(String[] args) {
        JiaoL jiaoL = new JiaoL();
        jiaoL.startReading();
        // 运行一段时间后停止读取（例如 10 秒）
        try {
            Thread.sleep(500); // 运行 10 秒
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String  jiaoLValue1 = jiaoL.getJiaoLZValueWithUnit();
        jiaoL.stopReading();
        System.out.println(jiaoLValue1);
    }



}