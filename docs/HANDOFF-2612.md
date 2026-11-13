# Embers Reborn 26.1.2 迁移交接文档（第二版）

本文档取代第一版。接手者从这里继续，不要参考旧版的任务 A 到 F 划分：那部分已经完成或改了做法。

## 1. 现状一句话

2612 分支已提交到 `2612: remove dangling annotations...`（`git log -1` 查看）。`compileJava` 还剩 **957 个错误，分布在 209 个文件**，日志在 `build\c11.log.err`。数据生成、渲染类型、粒子、注册和 30 个方块实体渲染器都已改完，剩下的主要是零散 API 改名和三块需要手写的东西（古代典籍、管道动态模型、灰烬护甲模型）。从未进过游戏。

## 2. 路径与工具

| 用途 | 路径 |
|---|---|
| 2612 工作树（在这里改） | `E:\模组\发cf的模组\Embers Reborn\Embers Reborn-2612-neoforge`，分支 `2612-neoforge` |
| 1.21.1 工作树（能编译，行为参考，也要同步修 bug） | `E:\模组\发cf的模组\Embers Reborn\Embers Reborn-1211-neoforge`，分支 `master` |
| 1.20.1 原版源码（最终行为标准） | `E:\模组\发cf的模组\Embers Reborn\_source-1201` |
| 26.1.2 Minecraft 源码 | `E:\模组\mc源码\2612sources` |
| 26.1.2 NeoForge 源码 | `E:\模组\mc源码\2612neoforge-sources` |

- MODID `embers_reborn`。JDK 25，ModDevGradle 2.0.140，NeoForge 26.1.2.116，Gradle 堆 6 GB。
- **查 API 一律看上面两个源码目录**，26.1 和 1.21.1 差别极大，不要凭记忆写。

### 编译和汇总

```powershell
pwsh -NoProfile -File tools\compile2612.ps1 -Log c12.log
pwsh -NoProfile -File tools\summarize_errors.ps1 -Err build\c12.log.err
```

1.21.1 编译：在 1.21.1 目录用 `Start-Process gradlew.bat -ArgumentList compileJava,--offline` 并重定向输出到 `build\*.log`。

### 已知坑

- **语法错误会遮住其他错误**：javac 遇到 `illegal start of type` 这类解析错误时只报这几个，其余全不报。错误数突然掉到个位数，先看是不是语法错误。
- 路径含中文，cmd 调 `gradlew.bat` 会失败，用 PowerShell。
- 文件 CRLF 和 LF 混用，脚本做字符串替换时两种都要匹配（正则写 `\r?\n`）。
- 在 PowerShell 里不要写内联 `python -c`，写成 `.py` 文件再运行。PowerShell 的 `foreach(){...} | ...` 管道会报"不允许空管道"，先存进数组。
- `tools\fix_by_error.py` 可以按编译日志里报错的行号定点替换，适合同一个 API 散落在很多文件里的情况。
- `tools\probe_api.ps1` 是临时探查脚本，每次覆盖重写。

## 3. 已完成的设计（不要推翻）

设计原则：**保留 1.21.1 的机器逻辑，只在边界加适配层**。

| 内容 | 做法 | 关键文件 |
|---|---|---|
| 物品/流体能力 | 内部仍用 `ItemStackHandler`/`FluidTank`；注册时包成 `ResourceHandler`，查邻居时包回旧接口 | `util\LegacyHandlers.java`、`util\LegacyCaps.java`、`blockentity\BlockEntityCapabilities.java` |
| 方块实体存读档 | 继承链最顶层实现 `saveAdditional(ValueOutput)`/`loadAdditional(ValueInput)`，转成 CompoundTag 调 `saveAdditionalNbt`/`loadAdditionalNbt`，存档格式不变 | `util\NbtBridge.java` |
| 研究/移鳞附加数据 | `NbtBridge.attachment(...)` 自定义序列化 | `RegistryManager` |
| `INBTSerializable` | NeoForge 删了，本模组自带一份 | `util\INBTSerializable.java` |
| 方块被拆 | `LegacyRemoveBlock.onRemoveLegacy`；有方块实体时由 mixin 在 `preRemoveSideEffects` 调用 | `util\LegacyRemoveBlock.java`、`mixin\BlockEntityRemoveMixin.java` |
| 注册 ID | `RegCtx.item/block` + Properties 构造 mixin 自动写 ID | `util\RegCtx.java`、`mixin\ItemPropertiesIdMixin.java`、`mixin\BlockPropertiesIdMixin.java` |
| 工具/护甲 | `ToolMaterial`、`ArmorMaterial` 记录、装备资源 `equipment/ashen.json` | `util\EmbersTiers.java`、`util\AshenArmorMaterial.java` |
| 模型、方块状态、物品定义 | **静态 JSON**：把 1.21.1 生成结果复制进 `src/main/resources`，并生成 26.1 物品定义 `assets/embers_reborn/items/*.json`。模型 datagen 类已删 | `tools\convert_assets_2612.py` |
| 标签/配方 datagen | 26.1 API；方块和物品同名标签放在共享类 | `datagen\EmbersBlockItemTags.java`、`datagen\BlockToItemTagAppender.java`、`datagen\EmbersRecipes.java`（`Runner` 内部类） |
| 渲染类型 | `RenderPipeline` + `RenderSetup`，字段名沿用旧名；管线在 `RegisterRenderPipelinesEvent` 注册 | `render\EmbersRenderTypes.java` |
| 粒子 | `SingleQuadParticle` + `Layer`；余烬着色器用 26.1 GLSL，`FAB`/`ALPHA_CUTOFF` 宏 | `render\EmbersParticleLayers.java`、`shaders/core/particle_ember.*` |
| 流体客户端 | 流体模型和客户端扩展集中注册，雾颜色查询改走这里 | `fluidtypes\EmbersFluidClient.java` |
| 物品颜色/属性 | `ItemTintSource`（余烬充能色、Tyrfing 光效）和条件属性（苦痛宝石 charged），物品定义 JSON 引用它们 | `client\EmbersItemClient.java` |
| 方块实体渲染器 | 30 个都继承桥接类：`submit` 时调用旧 `render`，把顶点记录下来交给 `submitCustomGeometry`；物品用 `item(...)`、模型部件用 `model(...)` | `render\LegacyBlockEntityRenderer.java` |
| 机器活动部件模型 | 18 个独立模型经 `ModelEvent.RegisterStandalone` 注册 | `render\EmbersStandaloneModels.java` |

### 明确放弃的效果（26.1 做不到，已写在代码注释里）
- 秘银方块反光着色器：26.1 没有自定义方块渲染层，按普通实心方块显示。
- 余烬粒子贴近方块时的柔和渐隐：粒子层无法绑定深度缓冲副本。
- 古代魔像刷怪蛋：需要单独画一张贴图（26.1 删了通用模板和蛋颜色）。
- 炼金笔记物品模型：`separate_transforms` 加载器没了，暂用普通模型。

## 4. 剩余工作

### 4.1 零散 API（大部分错误在这里）
缺失符号频次最高的几项，每项先去源码查 26.1 对应写法，再用脚本或 `fix_by_error.py` 批量改：

| 旧符号 | 次数 | 线索 |
|---|---|---|
| `getNormal()` | 42 | 多半是 `Direction.getNormal()` → 26.1 改名，查 `Direction.java` |
| `GuiGraphics` | 25 | 换 `GuiGraphicsExtractor`，`drawString`→`text`，`renderFakeItem`→`item`，`blit` 要传 `RenderPipelines.GUI_TEXTURED`（`gui\SlateScreen.java` 是已改好的例子） |
| `CODEC_NONEMPTY` / `EMPTY` | 17/17 | `ItemStack`/`FluidStack` 编解码器改名 |
| `TagValue` | 13 | 配料 / 标签值类型改了 |
| `addTooltipCallback` | 12 | 物品提示回调 API |
| `getTimer()` | 12 | 改用 `DeltaTracker` |
| `bakedmodel` 变量 | 11 | 渲染器脚本删掉了 `BakedModel x = itemRenderer.getModel(...)`，但后面还有别处用它；改成调用 `LegacyBlockEntityRenderer.item(...)` |
| `saveOptional(Frozen)`、`parse(Frozen,CompoundTag)` | 9/7 | ItemStack NBT 读写改为 codec + `NbtBridge` 类似做法 |
| `InteractionResult.sidedSuccess`/`CONSUME_PARTIAL` | 6/6 | 改 `InteractionResult.SUCCESS`/`CONSUME` |
| 爆炸（`ToolExplosion`、`BlastingExplosion`、`getToBlow`） | ~50 | 26.1 爆炸 API 重写，对照 `ServerExplosion` |
| 战利品条件（`MatchCurioLootCondition`、`getParam/hasParam`） | ~24 | `LootContext` 参数 API 改了 |
| `appendHoverText` | 多处 | 新签名 `(ItemStack, TooltipContext, TooltipDisplay, Consumer<Component>, TooltipFlag)`，`tooltip.add` → `tooltip.accept` |
| `inventoryTick` | 多处 | 新签名 `(ItemStack, ServerLevel, Entity, EquipmentSlot)` |
| 物品流体能力 | 多处 | `Capabilities.Fluid.ITEM`，类型 `ResourceHandler<FluidResource>`，上下文 `ItemAccess` |
| `EmbersClientEvents` | 30 | 世界渲染事件（`RenderLevelStageEvent` 阶段改名）、方块高亮事件、深度缓冲副本（可整段删掉，粒子已不用）、`Inventory.selected` 改私有 |

### 4.2 手写部分
1. **古代典籍** `gui\GuiCodex.java`（44 错）和 `research\ResearchManager.java`（29 错）：另一个 AI 已改了一半（`ResearchBase`、`ResearchShowItem` 已用 `GuiGraphicsExtractor`）。剩下的是 `GlStateManager`/`Tesselator` 画的波浪连线（`TRIANGLE_STRIP`）、光圈（`TRIANGLES`）和高亮，要改成 26.1 GUI 自定义几何。保留 1.21.1 已修的两点：每轮循环单独开缓冲；空缓冲跳过。
2. **管道动态模型** `render\PipeModel.java`（37 错）、`render\PipeModelBuilder.java`（10 错）：`render\BakedPipeModel.java` 已改成 `BlockStateModel`（另一个 AI 写的，审过），要把加载器改成 26.1 `UnbakedModelLoader`，在 `ModelEvent.RegisterLoaders` 注册（`Embers.java` 里已改了事件名）。
3. **灰烬护甲模型** `model\AshenArmorModel.java`（11 错）：改用 `IClientItemExtensions.getHumanoidArmorModel(ItemStack, EquipmentClientInfo.LayerType, Model)`，通过 `RegisterClientExtensionsEvent.registerItem` 注册。`item\AshenArmorItem.initializeClient` 要删掉。
4. **炼金笔记渲染器** `render\AlchemicalNoteItemRenderer.java`（12 错）：`BlockEntityWithoutLevelRenderer` 没了，改成 26.1 特殊物品模型（`RegisterSpecialModelRendererEvent`）。

### 4.3 收尾
1. 编译通过。
2. 删掉 `src\generated\resources`，跑 `runData`，确认配方、标签、战利品表和 1.21.1 的生成结果一致（模型不用比，已是静态文件）。
3. `runClient` 冒烟：进世界；放置、拆机器看是否掉物品；管道送物品和流体；矿钻钻头、冲压器臂等活动部件是否显示；典籍；余烬粒子；灰烬护甲；退出再进看数据是否保留。测试客户端要用隐藏窗口。

## 5. 旧迁移 bug（1.20.1 → 1.21.1）

1.21.1 版本质量很差，**每改一个文件都对照 1.20.1**。常见坏法：签名没跟上又没 `@Override`，方法永远不被调用；整段逻辑被注释掉；具体值换成空值或 `return null`；传错同名常量或标签。

发现后在 2612 修，并**同步修 1.21.1、编译验证、单独提交**。本次已修并同步：

| 问题 | 1.21.1 提交 |
|---|---|
| 工具挖掘等级错：传了空的"需要某级工具"标签，铅/银/黎明石工具什么都能挖出掉落 | `165b2b8` |
| 矿石高炉配方全被生成成熔炉配方 | `df54acd` |
| 18 个机器活动部件模型没注册、路径少 `block/`，钻头/活塞/冲压臂等从不显示 | `ad897cb` |

以上三项都只编译通过，未进游戏验证。

## 6. 交付标准

- 2612 通过 `compileJava` 和 `runData`；
- 冒烟测试结果：是否崩溃、哪些功能正常、哪些不正常；
- 新发现的旧迁移 bug 列表（文件、行号、1.21.1 是否已修）；
- 未完成的部分及原因。
