# 继电器接点寿命测试软件 · 功能实现与关键技术分析

对照 Java 源码整理的课题报告。第十六节为 4 拖 1，第十七节为重难点，第十八节按论文「软件系统设计」稿给出七层模块框图、公式与关键算法。

全部内容放在 GitHub 仓库 [grittendorotea-lgtm/footboll](https://github.com/grittendorotea-lgtm/footboll)：网站用 GitHub Pages 打开，Word、插图、Markdown 和 `docs/source/` 里的 Java 源码都在同一个仓库里。

## GitHub Pages

长期访问地址：

**https://grittendorotea-lgtm.github.io/footboll/**

推送到 `main` 后，GitHub Actions 会构建静态网站并发布。若第一次没有网页，打开仓库 **Settings → Pages**，Build and deployment 的 Source 选 **GitHub Actions**。

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

仓库地址：https://github.com/grittendorotea-lgtm/footboll

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
