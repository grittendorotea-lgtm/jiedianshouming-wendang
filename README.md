# 继电器接点寿命测试软件 · 功能实现与关键技术分析

对照 Java 源码整理的课题报告。第十六节为 4 拖 1，第十七节为重难点，第十八节按论文「软件系统设计」稿给出七层模块框图、公式与关键算法。

分析的源码在 `docs/source/`：

- `ZzhejiPanel.java` 基础版测试主界面与状态机
- `DianZu00000.java` 三路接触电阻采集
- `JiaoL.java` 电流采集
- `ExecuteCommon.java` 数据库访问
- `ZzhejiPanel-20260811.java` 4 拖 1 拓展面板（第十六节）
- `SixMeterInstrumentReader.java` COM5 八表统一读取器（第十六节）

## 本地运行

```bash
npm install
npm run dev
```

浏览器打开 [http://127.0.0.1:43217](http://127.0.0.1:43217)。打印稿在 [http://127.0.0.1:43217/print](http://127.0.0.1:43217/print)。可粘贴进 Word 的正文在 `docs/课题报告-功能实现分析.md`。
