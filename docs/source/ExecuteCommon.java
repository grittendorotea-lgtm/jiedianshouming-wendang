package com.ytzg.sealer.db;

import org.jfree.data.xy.XYSeries;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.sql.Date;

public class ExecuteCommon {

    // 更新单条dianzumax值的方法
    public static void updateDianzumaxValueForId(int id, double newValue) {
        String query = "UPDATE d_dianzumax SET dianzumax = ? WHERE id = ?";
        executeSingleUpdate(query, id, newValue);
    }

    // 更新单条testcount值的方法
    public static void updatetestcountValueForId(int id, double newValue) {
        String query = "UPDATE d_dianzumax SET testcount = ? WHERE id = ?";
        executeSingleUpdate(query, id, newValue);
    }

    // 更新单条allcount值的方法
    public static void updatealltestcountValueForId(int id, int newValue) {
        String query = "UPDATE allcount SET allcount = ? WHERE id = ?";
        executeSingleUpdate(query, id, newValue);
    }

    // 通用的单条记录更新方法
    private static void executeSingleUpdate(String query, int id, double value) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, value);
            statement.setInt(2, id);
            statement.executeUpdate();

        } catch (Exception e) {
            handleException("更新数据库失败", e);
        }
    }
    // 异常处理封装
    private static void handleException(String message, Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
                message + ": " + e.getMessage(),
                "错误",
                JOptionPane.ERROR_MESSAGE);
    }



    //实验最大次数
    public static int getTestmaxValue1() {
        int dianzumax = 0;

        String query = "SELECT testcount FROM d_dianzumax WHERE id = 1";  // 获取 id = 1 的值

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                dianzumax = resultSet.getInt("testcount");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法从数据库中获取数据：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dianzumax;
    }


    // 查询 累计次数 值的方法
    public static int getAllcountValue1(int id) {
        int allcount = 0;  // 默认值为 0
        String query = "SELECT allcount FROM allcount WHERE id = ?";  // 获取指定 id 的值

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            // 设置参数
            statement.setInt(1, id);

            // 执行查询
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    allcount = resultSet.getInt("allcount");  // 获取 int 类型的值
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法从数据库中获取数据：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return allcount;  // 返回 int 类型的值
    }



    // 查询dianzumax值的方法
    public static double getDianzumaxValue() {
        double dianzumax = 0.0;
        String query = "SELECT dianzumax FROM d_dianzumax LIMIT 1";  // 获取表中的第一个值

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                dianzumax = resultSet.getDouble("dianzumax");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法从数据库中获取数据：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dianzumax;
    }

    // 更新dianzumax值的方法
    public static void updateDianzumaxValue(double newValue) {
        String query = "UPDATE d_dianzumax SET dianzumax = ? WHERE id = 1";  // 更新表中的第一个值

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDouble(1, newValue);
            statement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "无法更新数据库中的数据：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //--批量----------保存折线图的数据---------------
    public static int saveTestResultsBatch(List<Object[]> dataToInsert) {
        String sql = "INSERT INTO test_results (test_time, test_bianhao, count_test, series1_x, series1_y, series2_x, series2_y, series3_x, series3_y, series4_x, series4_y) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rowsInserted = 0;
        Connection conn = null;

        try {
            conn = JdbcDeal.getConnection(); // 获取数据库连接
            PreparedStatement pstmt = conn.prepareStatement(sql);

            conn.setAutoCommit(false); // 关闭自动提交，开启事务

            for (Object[] params : dataToInsert) {
                pstmt.setDate(1, (java.sql.Date) params[0]);
                pstmt.setString(2, (String) params[1]);

                // 如果 params[2] 是 String 类型，先将其转换为 Integer
                if (params[2] instanceof String) {
                    pstmt.setInt(3, Integer.parseInt((String) params[2]));
                } else {
                    pstmt.setInt(3, (Integer) params[2]);
                }

                pstmt.setDouble(4, (Double) params[3]);
                pstmt.setDouble(5, (Double) params[4]);
                pstmt.setDouble(6, (Double) params[5]);
                pstmt.setDouble(7, (Double) params[6]);
                pstmt.setDouble(8, (Double) params[7]);
                pstmt.setDouble(9, (Double) params[8]);
                pstmt.setDouble(10, (Double) params[9]);
                pstmt.setDouble(11,  (Double) params[10]);
                pstmt.addBatch();  // 将参数添加到批处理
            }


            int[] batchResults = pstmt.executeBatch();  // 执行批处理
            conn.commit();  // 提交事务
            rowsInserted = batchResults.length;

        } catch (SQLException e) {
            e.printStackTrace();
            // 如果出现异常，进行回滚
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            // 关闭连接
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return rowsInserted;
    }

/*
    public static int saveTestResultsBatch(java.util.Date testTimeValue, String testBianHaoValue, String countTestText, XYSeries series1, XYSeries series2, XYSeries series3) {
        // 将 java.util.Date 转换为 java.sql.Date
        java.sql.Date sqlTestTimeValue = new java.sql.Date(testTimeValue.getTime());

        // 检查 countTestText 是否为空并将其转换为整数
        int countTestValue = 0;
        if (countTestText != null && !countTestText.trim().isEmpty()) {
            try {
                countTestValue = Integer.parseInt(countTestText);
            } catch (NumberFormatException ex) {
                countTestValue = 0; // 默认值
                JOptionPane.showMessageDialog(null, "无效的数字输入，已将countTest设置为0。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 准备要插入的数据
        List<Object[]> dataToInsert = new ArrayList<>();
        for (int i = 0; i < series1.getItemCount(); i++) {
            double series1X = series1.getX(i).doubleValue();
            double series1Y = series1.getY(i).doubleValue();
            double series2X = series2.getX(i).doubleValue();
            double series2Y = series2.getY(i).doubleValue();
            double series3X = series3.getX(i).doubleValue();
            double series3Y = series3.getY(i).doubleValue();

            dataToInsert.add(new Object[]{
                    sqlTestTimeValue, testBianHaoValue, countTestValue,
                    series1X, series1Y, series2X, series2Y, series3X, series3Y
            });
        }

        // 定义 SQL 语句
        String sql = "INSERT INTO test_results (test_time, test_bianhao, count_test, series1_x, series1_y, series2_x, series2_y, series3_x, series3_y) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 调用批量插入方法
        return updateBatchDataCountTest(sql, dataToInsert);
    }
*/


    //------------保存折线图的数据---------------
    public static int saveTestResults(java.util.Date testTimeValue, String testBianHaoValue, String countTestText, XYSeries series1, XYSeries series2, XYSeries series3) {
        // 将 java.util.Date 转换为 java.sql.Date
        java.sql.Date sqlTestTimeValue = new java.sql.Date(testTimeValue.getTime());

        // 检查 countTest 是否为空
        int countTestValue = 0;
        if (countTestText != null && !countTestText.trim().isEmpty()) {
            try {
                countTestValue = Integer.parseInt(countTestText);
            } catch (NumberFormatException ex) {
                // 如果转换失败，可以选择显示错误信息或设置默认值
                countTestValue = 0; // 默认值，或者弹出错误对话框
                JOptionPane.showMessageDialog(null, "无效的数字输入，已将countTest设置为0。", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }

        // 获取 series1、series2、series3 中的所有数据
        List<Object[]> dataToInsert = new ArrayList<>();
        for (int i = 0; i < series1.getItemCount(); i++) {
            double series1X = series1.getX(i).doubleValue();
            double series1Y = series1.getY(i).doubleValue();
            double series2X = series2.getX(i).doubleValue();
            double series2Y = series2.getY(i).doubleValue();
            double series3X = series3.getX(i).doubleValue();
            double series3Y = series3.getY(i).doubleValue();

            // 将每一行数据添加到列表中
            dataToInsert.add(new Object[]{
                    sqlTestTimeValue, testBianHaoValue, countTestValue,
                    series1X, series1Y, series2X, series2Y, series3X, series3Y
            });
        }

        // 定义 SQL 语句
        String sql = "INSERT INTO test_results (test_time, test_bianhao, count_test, series1_x, series1_y, series2_x, series2_y, series3_x, series3_y) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        // 调用 updateBatchDatas 方法批量插入数据
        return updateBatchDataCountTest(sql, dataToInsert);
    }


    public static int updateBatchDataCountTest(String sql, List<Object[]> values) {
        int num = 0;
        Connection conn = null;
        try {
            conn = com.ytzg.sealer.db.DBConnection.getConnection();
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                conn.setAutoCommit(false); // 开始事务

                if (values != null) {
                    for (Object[] row : values) {
                        for (int j = 0; j < row.length; j++) {
                            ps.setObject(j + 1, row[j]);
                        }
                        ps.addBatch();  // 添加到批处理中
                    }
                }
                num = ps.executeBatch().length;  // 执行批处理
                conn.commit(); // 提交事务
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.rollback(); // 回滚事务
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();  // 关闭连接
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        return num;
    }


    // 从数据库加载数据的方法
/*
    public static List<XYSeries> loadTestResults(int recordId) {
        List<XYSeries> seriesList = new ArrayList<>();
        XYSeries series1 = new XYSeries("电阻1");
        XYSeries series2 = new XYSeries("电阻2");
        XYSeries series3 = new XYSeries("电阻3");

        String sql = "SELECT series1_x, series1_y, series2_x, series2_y, series3_x, series3_y FROM test_results WHERE id = ?";
        try {
            Connection con = com.ytzg.sealer.db.DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, recordId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                series1.add(rs.getDouble("series1_x"), rs.getDouble("series1_y"));
                series2.add(rs.getDouble("series2_x"), rs.getDouble("series2_y"));
                series3.add(rs.getDouble("series3_x"), rs.getDouble("series3_y"));
            }

            com.ytzg.sealer.db.DBConnection.closeConnection(rs, ps, con);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        seriesList.add(series1);
        seriesList.add(series2);
        seriesList.add(series3);
        return seriesList;
    }

*/




	public static int updateDatas(String sql, List<Object> values) {
		int num = 0;
		try {
			Connection con = com.ytzg.sealer.db.DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					ps.setObject((i + 1), values.get(i));
				}
			}
			num = ps.executeUpdate();
			com.ytzg.sealer.db.DBConnection.closeConnection(null, ps, con);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return num;
	}

	public static List<Map<String, Object>> queryDatas(String sql, List<Object> values) {
		List<Map<String, Object>> ret = new ArrayList<Map<String, Object>>();
		Connection con = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			con = com.ytzg.sealer.db.DBConnection.getConnection();
			ps = con.prepareStatement(sql);
			if (values != null) {
				for (int i = 0; i < values.size(); i++) {
					ps.setObject((i + 1), values.get(i));
				}
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsm = rs.getMetaData();
			int col = rsm.getColumnCount();
			if (rs != null) {
				while (rs.next()) {
					Map<String, Object> obj = new HashMap<String, Object>();
					for (int i = 0; i < col; i++) {
						String colName = rsm.getColumnLabel(i + 1);
						obj.put(colName, rs.getObject(colName));
					}
					ret.add(obj);
				}
			}
			com.ytzg.sealer.db.DBConnection.closeConnection(rs, ps, con);
		} catch (SQLException e) {
			com.ytzg.sealer.db.DBConnection.closeConnection(rs, ps, con);
			e.printStackTrace();
		} catch (Exception e) {
			com.ytzg.sealer.db.DBConnection.closeConnection(rs, ps, con);
			e.printStackTrace();
		}

		return ret;
	}

	public static void main(String[] args) {

		String sql = null;
		List<Object> datas = new ArrayList<Object>();
		datas.add("张");
		datas.add(2222.46);
		datas.add(8);
		sql = "SELECT * FROM t_userinfo";
		try {
			List<Map<String, Object>> ret = ExecuteCommon.queryDatas(sql, null);
			for (Map<String, Object> map : ret) {
				for (Iterator<Entry<String, Object>> ite = map.entrySet().iterator(); ite.hasNext();) {
					Entry<String, Object> entry = ite.next();
					System.out.println(entry.getKey() + "-----" + entry.getValue());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
