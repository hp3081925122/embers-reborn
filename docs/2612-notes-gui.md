# 26.1.2 GUI / Research 迁移记录

## 修改类

- `gui/GuiCodex.java`
  - 迁移到 26.1.2 的 `GuiGraphicsExtractor` 提取式渲染入口 `extractRenderState`。
  - 纹理绘制统一走 `RenderPipelines.GUI_TEXTURED`，保留 codex 分类、页面、搜索、翻页与动态高亮。
  - 已保留每轮独立缓冲、`drawIfNotEmpty` 空网格保护以及 `TRIANGLE_STRIP`/`TRIANGLES` 几何语义；当前实现仍含旧 `Tesselator`/直接 GL 状态调用，需在后续批次替换为 26.1 界面管线后才能编译。
  - 物品显示改用 `item` / `itemDecorations`，提示框改为 `setComponentTooltipForNextFrame`。
  - 键鼠输入改用 26.1.2 的 `KeyEvent`、`MouseButtonEvent`、`CharacterEvent`。
- `gui/SlateMenu.java`
  - `clicked` 参数迁移到 `ContainerInput`；保留解码石板物品栏、锁定快捷栏槽及 DataComponents/NBT 桥接。
- `gui/SlateScreen.java`
  - 迁移到 `extractBackground`、`extractLabels` 与 `GuiGraphicsExtractor`；保留五轮猜测、黑白针提示和悬停对错提示。
- `research/ResearchBase.java`、`research/subtypes/ResearchShowItem.java`
  - 页面内容渲染改用 `GuiGraphicsExtractor`，物品格纹理通过 GUI 管线提交。
- `research/` 其余文件
  - 保留研究树、分类、能力同步与网络数据结构；26.1.2 标识符继续使用 `Identifier`。

## 注册片段

- 菜单注册仍由 `RegistryManager.SLATE_MENU` 提供，`SlateMenu` 构造器保持原参数顺序。
- 古代典籍界面仍由现有客户端事件打开 `GuiCodex.instance`，未改动 `Embers.java`、`EmbersClientEvents.java` 或 `RegistryManager.java`。

## 旧迁移 bug 与处理

1. 26.1.2 已移除旧 `GuiGraphics`，若继续覆写 `render`/`renderBg` 会无法接入提取式渲染；现改用 `extractRenderState`、`extractBackground`、`extractLabels`。
2. `AbstractContainerMenu.clicked` 的第三参数已从 `ClickType` 改为 `ContainerInput`，石板菜单同步更新。
3. 旧 `GuiGraphics.blit` 隐含 256 纹理尺寸；26.1.2 需显式指定 `RenderPipelines.GUI_TEXTURED` 和纹理尺寸，已集中封装避免 UV 偏移。
4. 旧 `renderFakeItem`/`renderItemDecorations` 改为 `item`/`itemDecorations`；悬停解码石板提示使用延迟组件 tooltip API。
5. `GuiGraphicsExtractor` 使用 `Matrix3x2fStack`，姿态栈改为 `pushMatrix`/`popMatrix`，分类图标旋转改为弧度 `rotate`。

## 尚未执行

按迁移任务要求，本阶段未运行 Gradle 编译或游戏测试；主代理的 c6 编译已确认本文件仍有旧 Tesselator、GL 状态和输入 API 错误，需要在全仓 API 迁移完成后统一验证 GUI 提取渲染及石板交互。
