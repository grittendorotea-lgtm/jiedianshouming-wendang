# 继电器接点寿命测试软件 · 功能实现与关键技术分析

对照 Java 源码整理的课题报告。第十六节为 4 拖 1，第十七节为重难点，第十八节按论文「软件系统设计」稿给出七层模块框图、公式与关键算法。

**以后所有内容都放在 GitHub 上**：网站用 GitHub Pages 打开，Word、插图、Markdown 和 `docs/source/` 里的 Java 源码都在同一个仓库里。

## GitHub Pages（长期访问）

1. 在 Cursor 里点 **Create repo**，把这个项目建成你自己的 GitHub 仓库。不要把 GitHub 密码发给任何人。
2. 推送到 GitHub 后，Actions 会构建静态网站并发布到 Pages。
3. 项目页地址（仓库名不是 `用户名.github.io` 时）为：

   `https://<GitHub用户名>.github.io/<仓库名>/`

4. 若第一次没有网页，打开仓库 **Settings → Pages**，Build and deployment 的 Source 选 **GitHub Actions**。

同一站点里还有：

| 内容 | 路径 |
| --- | --- |
| 阅读版 | `/` |
| 打印稿 / 导出 PDF | `/print/` |
| 手机 Markdown | `/md.html` |
| Word | `/downloads/relay-life-test-report.docx` |
| 插图 | `/figures/` |
| Java 源码 | 仓库内 `docs/source/` |
| 可粘贴正文 | 仓库内 `docs/课题报告-功能实现分析.md` |
| Word 副本 | 仓库内 `docs/继电器接点寿命测试软件-课题报告.docx` |

## 本地运行

```bash
npm install
npm run dev
```

浏览器打开 [http://127.0.0.1:43217](http://127.0.0.1:43217)。

预览 GitHub Pages 用的静态导出：

```bash
npm run build
npm start
```

重新生成 Word（需先启动 `npm run dev`）：

```bash
python3 scripts/build-docx.py
```
