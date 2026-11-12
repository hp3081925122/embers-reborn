# 26.1.2 迁移进度

基线：1.21.1 分支 master @ 2169233。分支 2612-neoforge，工作树 `Embers Reborn-2612-neoforge`。

编译：`pwsh -NoProfile -File tools\compile2612.ps1 -Log cN.log`，汇总：`tools\summarize_errors.ps1 -Err build\cN.log.err`。

| 批次 | 内容 | 提交 | 编译错误 |
|---|---|---|---|
| 1 | 构建脚本换 26.1.2 ModDevGradle 模板 | 66299d7 | 5027 |
| 2 | 机械改名 Identifier / isClientSide() | 47cdff1 | 3741 |
| 3 | 存档 ValueInput/ValueOutput、能力注册、INBTSerializable | | |
| 4 | 方块/物品交互：InteractionResult、onRemove→affectNeighborsAfterRemoval、updateShape、DirectionProperty→EnumProperty | | |
| 5 | 工具/护甲材料：Tier→ToolMaterial、ArmorMaterial | | |
| 6 | 粒子：TextureSheetParticle→SingleQuadParticle | | |
| 7 | 渲染：RenderType/RenderPipeline、BakedModel、GUI GuiGraphicsExtractor | | |
| 8 | datagen：模型/方块状态改 ModelProvider，标签、配方 | | |
| 9 | runData + runClient 冒烟 | | |

## 批次 3 已查明的 API

- 存档：`saveAdditional(ValueOutput)` / `loadAdditional(ValueInput)`，没有 Provider 参数。`ItemStackHandler`、`FluidTank` 仍在，改用 `serialize(ValueOutput)` / `deserialize(ValueInput)`。`getUpdateTag(Provider)` 不变。
- 能力：`Capabilities.ItemHandler/FluidHandler` 改成 `Capabilities.Item.BLOCK` / `Capabilities.Fluid.BLOCK`，类型是 `ResourceHandler<ItemResource>` / `ResourceHandler<FluidResource>`。`IItemHandler`、`IFluidHandler` 标了废弃但仍可用，查询结果可用 `IItemHandler.of(handler)` / `IFluidHandler.of(handler)` 包回旧接口。
- 做法：内部继续用 `ItemStackHandler`/`FluidTank`，注册能力时包成 `ResourceHandler`（需找到旧接口转新接口的包装类），调用邻居时用 `of(...)` 包回旧接口，尽量少动机器逻辑。

## 在 1.21.1 上同步修复的迁移 bug

（发现一条记一条，两边都改）
