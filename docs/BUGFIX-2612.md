# Embers Reborn 26.1.2 NeoForge Bug 修复记录

- 项目路径：`E:\模组\发cf的模组\Embers Reborn\Embers Reborn-2612-neoforge`
- 测试基线文档：`docs/TEST-2612.md`
- 临时测试目录：`D:\AI临时文件\EmbersReborn\2612\test\`
- 分工模式：主代理负责指挥与源码/资源修复并记录本文件；`Gemini 4 Hydrogen` 测试子代理负责执行服务端/客户端测试。

---

## 第一轮修复（2026-11-14 09:30，静态审计 + P0 资源加载与代码缺陷修复）

### BUG-01：冗余/无效的客户端物品定义文件（`assets/embers_reborn/items/*.json`）触发加载报错与多余烘焙
- **发现阶段**：P0 客户端资源加载日志审计
- **报错现象**：
  - `handheld_model_base.json`（仅作为父级显示变换模板）和 `flamethrower.json`（1.20.1 残留未注册物品模型，含 `#missing` 贴图）在 MC 26.1.2 中被 `ClientItemInfoLoader` 当作独立客户端物品定义加载并烘焙，产生贴图缺失警告。
  - `inflictor_gem_charged.json` 仅作为 `items/inflictor_gem.json` 条件分支引用的子模型，却多余地放在 `items/` 下被重复解析。
- **根因确认**：MC 26.1.2 的 `ClientItemInfoLoader` 会无条件扫描并烘焙 `assets/<namespace>/items/*.json` 下的全部文件，即便对应注册名并不存在 `Item` 实例。
- **修复内容**：
  - 删除 `src/main/resources/assets/embers_reborn/items/handheld_model_base.json`
  - 删除 `src/main/resources/assets/embers_reborn/items/flamethrower.json`
  - 删除 `src/main/resources/assets/embers_reborn/items/inflictor_gem_charged.json`
- **验证状态**：已通过客户端实机复测（`latest.log` 本模组模型/贴图缺失警告为 `0`）。

### BUG-02：5 个方块/物品模型缺失 `"particle"` 贴图引用导致 7 条 `Missing texture references` 警告
- **发现阶段**：P0 客户端资源加载日志审计
- **报错现象**：客户端资源重载时输出 `Missing texture references in model ... #particle` 警告（涉及 `blazing_ray`、`cinder_staff`、`fluid_pipe`、`item_pipe`、`ember_bore_blades` 等）。
- **根因确认**：对应模型文件未声明 `"particle"` 贴图键，MC 26.1.2 模型烘焙器在解析粒子贴图时产生缺失引用警告。
- **修复内容**：
  - `src/main/resources/assets/embers_reborn/models/item/blazing_ray.json`：补充 `"particle": "embers_reborn:item/tool_parts_one"`
  - `src/main/resources/assets/embers_reborn/models/item/cinder_staff.json`：补充 `"particle": "embers_reborn:item/tool_parts_one"`
  - `src/main/resources/assets/embers_reborn/models/block/pipe_inventory.json`：补充 `"textures": { "particle": "#pipe" }`（同时覆盖 `item/fluid_pipe`、`item/item_pipe`、`block/fluid_pipe_inventory`、`block/item_pipe_inventory`）
  - `src/main/resources/assets/embers_reborn/models/block/ember_bore_blades.json`：补充 `"particle": "#bore"`
- **验证状态**：已通过客户端实机复测（`latest.log` 0 条 `#particle` 警告）。

### BUG-03：模型面 UV 超出 `[0, 16]` 范围触发 MC 26.1.2 `NativeImage.computeTransparency` 越界异常
- **发现阶段**：P0 客户端模型烘焙报错 + 全量 550 个模型脚本审计
- **报错现象**：
  - `ember_activator[half=lower]`（`block/activator_bottom.json`）与 `clockwork_axe`（`item/clockwork_axe.json`）在模型烘焙阶段抛出 `java.lang.IllegalArgumentException: Cannot compute translucency out of bounds`，导致模型退化为紫黑缺失模型。
- **根因确认**：MC 26.1.2 新增 `FaceBakery.computeMaterialTransparency` -> `SpriteContents.computeTransparency` -> `NativeImage.computeTransparency`，当贴图含透明/半透明像素且模型面 UV 超出 `[0.0, 16.0]`（如 `17.0`、`18.0`、`22.0`）时直接抛 `IllegalArgumentException`。全量扫描还发现另外 4 个使用不透明贴图的方块模型同样存在越界 UV。
- **修复内容**：
  - `src/main/resources/assets/embers_reborn/models/block/activator_bottom.json`：将第 8 号元素四侧面 UV 从 `[0.0, 8.0, 8.0, 17.0]` 修正为 `[0.0, 7.0, 8.0, 16.0]`（保持 9px 高度对应 `crystal_activator.png`）
  - `src/main/resources/assets/embers_reborn/models/item/clockwork_axe.json`：将越界 UV `[3.0, 14.0, 17.0, 16.0]`、`[3.0, 3.0, 4.0, 17.0]`、`[2.0, 4.0, 3.0, 18.0]` 分别修正收敛至 `[0, 16]` 对应像素区域（`[2.0, 14.0, 16.0, 16.0]`、`[3.0, 2.0, 4.0, 16.0]`、`[2.0, 2.0, 3.0, 16.0]`）
  - `src/main/resources/assets/embers_reborn/models/block/alchemy_tablet.json`：`[0.0, 13.0, 3.0, 17.0]` -> `[0.0, 12.0, 3.0, 16.0]`
  - `src/main/resources/assets/embers_reborn/models/block/hearth_coil_corner.json`：`[0, 14, 2, 18]` -> `[0, 12, 2, 16]`，`[0, 14, 2, 22]` -> `[0, 8, 2, 16]`
  - `src/main/resources/assets/embers_reborn/models/block/mech_edge_corner.json`：同 `hearth_coil_corner.json` 收敛至 `[0, 16]`
  - `src/main/resources/assets/embers_reborn/models/block/stamp_base.json`：`[7.0, 9.0, 9.0, 17.0]` -> `[7.0, 8.0, 9.0, 16.0]`
- **验证状态**：已通过客户端实机复测（模型烘焙 0 越界异常，`ember_activator` 与 `clockwork_axe` 渲染正常）。

### BUG-04：K1 `ancient_golem_spawn_egg` 引用已移除的 `minecraft:item/template_spawn_egg` 导致刷怪蛋紫黑块
- **发现阶段**：P0 日志报错 + `TEST-2612.md` 已知问题 K1
- **报错现象**：日志报缺失父模型 `minecraft:item/template_spawn_egg`，`embers_reborn:ancient_golem_spawn_egg` 在物品栏显示为紫黑缺失模型。
- **根因确认**：MC 26.1.2 移除了旧版双层染色模板 `minecraft:item/template_spawn_egg`，原版刷怪蛋全部改为 `minecraft:item/generated` 搭配独立 `16x16` 像素贴图。
- **修复内容**：
  - 按 `blockpixel-artist` 规范与 `textures/entity/golem.png`（远古砖石机身 + 炽热余烬核心独眼）原生绘制 `16x16` 二值透明贴图：`src/main/resources/assets/embers_reborn/textures/item/ancient_golem_spawn_egg.png`（通过 `validate_pixel_art.py` 校验）
  - 更新 `src/main/resources/assets/embers_reborn/models/item/ancient_golem_spawn_egg.json` 为 `parent: "minecraft:item/generated"` + `layer0: "embers_reborn:item/ancient_golem_spawn_egg"`
- **验证状态**：已通过客户端实机截图与使用生成验证（物品栏显示正常、右键正常生成远古魔像）。

### BUG-05：JEI 冲压分类（`StampingCategory`）与炼金分类（`AlchemyCategory`）空原料崩溃
- **发现阶段**：JEI 兼容代码静态审计
- **报错现象**：14 条仅含流体输入、省略了物品输入字段的 `stamping` 配方在打开或加载 JEI 冲压配方时，`recipe.getDisplayInput()` 返回 `null`，传入 `builder.addSlot(...).addIngredients(null)` 触发空指针异常。
- **根因确认**：26.1.2 迁移后 `StampingRecipe` 中无物品输入的配方其 `getDisplayInput()` 为 `null`（而非 `Ingredient.EMPTY`），`StampingCategory` 与 `AlchemyCategory` 未做判空保护。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/compat/jei/StampingCategory.java`：对 `recipe.getDisplayInput()` 和 `recipe.getDisplayStamp()` 增加判空保护，仅非空时调用 `addIngredients(...)`，空值时输出英文 `Embers.LOGGER.debug(...)` 调试日志
  - `src/main/java/com/rekindled/embers/compat/jei/AlchemyCategory.java`：对 `recipe.getCenterInput()` 增加判空保护
- **验证状态**：已通过客户端实机 JEI 校验（冲压 27 条、炼金 30 条正常加载显示）。

### BUG-06：`EmbersClientEvents.onLevelRender` 绘制连接线缺失 `LineWidth` 顶点属性导致崩溃
- **发现阶段**：渲染管线静态审计
- **报错现象**：手持余烬锤（`tinker_hammer`）或瞄准存在连线的余烬收发器/机器触发 `EmbersRenderTypes.GLOW_LINES` 绘制时，顶点缓冲因缺少 `LineWidth` 元素抛出运行时异常。
- **根因确认**：MC 26.1.2 中 `RenderPipelines.LINES` 的 `VertexFormat` 强制包含 `LineWidth` 属性；`EmbersClientEvents.java` 中两处 `DoubleLineConsumer` 仅写入了 `addVertex`、`setColor`、`setNormal`，漏掉了 `.setLineWidth(EmbersRenderTypes.GLOW_LINE_WIDTH)`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/EmbersClientEvents.java`：在两处 `DoubleLineConsumer` 的起止顶点构建链末尾均补齐 `.setLineWidth(EmbersRenderTypes.GLOW_LINE_WIDTH)`
- **验证状态**：已通过客户端实机验证（余烬锤绑定发射器-接收器发光连线与方块高亮正常渲染、零崩溃）。

---

## K2 专项排查与测试环境兼容处理（2026-11-14 10:00）

### ENV-K2：本机 AMD 核显驱动下原版 `GlFence.awaitCompletion(Long.MAX_VALUE)` 触发 `GL_WAIT_FAILED`
- **发现阶段**：P0 客户端启动与 `TEST-2612.md` 已知问题 K2 对照隔离（由 `tester` 执行 7 组对照实验）
- **报错现象**：客户端在初始资源加载（`GuiRenderer.draw`）或世界渲染（`LevelRenderer.prepareChunkRenders`）中随机/高频崩溃：`java.lang.IllegalStateException: Failed to complete GPU fence: 0`（位于 `com.mojang.blaze3d.opengl.GlFence.awaitCompletion(GlFence.java:29)` <- `MappableRingBuffer.currentBuffer(MappableRingBuffer.java:42)`）。
- **对照隔离证据**：
  1. **纯原版 + NeoForge 26.1.2.116 基线（Run 7a，无 `embers_reborn`、无任何附属模组）**：同样在 `GuiRenderer.draw` 崩溃于 `GlFence.awaitCompletion(GlFence.java:29)`（`D:\AI临时文件\EmbersReborn\2612\test\crash-run7a.txt`）。
  2. **独立对照工程 `Render Preview`（NeoForge 26.1.2.95，无 `embers_reborn`）**：复测同样在 `GuiRenderer.draw` 崩溃于同一行（`crash-rp-0959.txt`）。
  3. **同机 1.21.1 基线**：无 `MappableRingBuffer`/`GlFence` 机制，稳定运行 0 崩溃。
- **根因确认**（非本模组缺陷）：
  - MC 26.1.2 原版 `MappableRingBuffer.currentBuffer()` 向 `GlFence.awaitCompletion` 传入 `Long.MAX_VALUE`（`0x7FFFFFFFFFFFFFFFL` 纳秒），`GlFence` 将该值直接传给 `GL32.glClientWaitSync(handle, GL_SYNC_FLUSH_COMMANDS_BIT, Long.MAX_VALUE)`。
  - 在本机 `AMD Ryzen 7 7730U with Radeon Graphics`（OpenGL 驱动 `23.19.23.03.250611` / `atio6axx.dll`）上：当前 3 帧前的围栏若已完成（`GL_ALREADY_SIGNALED`）会直接快速返回；一旦遇到 CPU/核显繁忙导致围栏尚未完成、进入驱动阻塞等待路径时，`0x7FFFFFFFFFFFFFFFL` 在驱动内部叠加时间戳发生有符号 64 位整数溢出，直接返回 `GL_WAIT_FAILED`（`37149`）且 `glGetError() == 0`。
- **测试环境疏通方案（不污染被测模组源码）**：
  - `Embers Reborn` 源码保持零侵入修改。
  - 在 `D:\AI临时文件\EmbersReborn\2612\test\glfence_helper\` 编译纯测试期辅助模组 `glfence_test_helper-0.1.0.jar`（随 `self_mcp` 一并在测试期间放入 `run/mods/`，全部测试结束后一并移除），将 `GlFence.awaitCompletion` 的超大超时拆分为 `50ms`（`50_000_000ns`）分段轮询并在驱动异常返回 `GL_WAIT_FAILED` 时回退 `glFinish()`，以彻底解除本机客户端测试阻塞。

---

## 第二轮修复（2026-11-14 11:20，P1~P15 实机测试缺陷 + 全量源码机械比对修复）

### BUG-07：全部 141 个 `BlockItem` 物品栏名称显示为未翻译键 `item.embers_reborn.<id>`
- **发现阶段**：P1-4 语言文件与物品名称核对
- **报错现象**：所有方块对应物品在英文与简体中文界面下均显示原始键名 `item.embers_reborn.lead_ore` 等（共 141 个方块物品），而放置后的方块本体翻译正常（`block.embers_reborn.*`）。
- **根因确认**：MC 26.1.2 的 `Item.<init>(Properties)` 在构造期即根据 `properties.effectiveDescriptionId()` 生成默认的 `DataComponents.ITEM_NAME` 组件；未调用 `Item.Properties.useBlockDescriptionPrefix()` 时前缀默认为 `item.`，而语言文件中方块翻译统一使用 `block.embers_reborn.*`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/RegistryManager.java`：为全部 `BlockItem`、`StoneDecoBlocks.makeItems`、`MetalCrystalSeed.makeItem` 的 `new Item.Properties()` 补齐 `.useBlockDescriptionPrefix()`
  - `src/main/java/com/rekindled/embers/compat/curios/CuriosCompat.java`：为 `EXPLOSION_PEDESTAL_ITEM` 补齐 `.useBlockDescriptionPrefix()`
  - `src/main/java/com/rekindled/embers/item/UpgradeItem.java`、`CopperCellBlockItem.java`、`FluidVesselBlockItem.java`、`CaminiteRingBlockItem.java`：在 `super(block, properties.useBlockDescriptionPrefix())` 中统一启用方块描述前缀（通过 `apply_bug07.py` 机械核验 141/141 全覆盖）
- **验证状态**：已通过客户端实机全量扫描验证（`zh_cn` 与 `en_us` 下全部 141 个 `BlockItem` 与 130 个非方块物品缺失翻译数为 `0`）。

### BUG-08：古代魔像发光眼部图层（`AncientGolemEyeLayer`）将全身渲染为纯黑
- **发现阶段**：P1/P12 实体渲染测试
- **报错现象**：生成 `embers_reborn:ancient_golem` 后，魔像除眼部红色像素外全身被纯黑贴图覆盖，遮挡了底层 `golem.png` 砖石纹理。
- **根因确认**：`textures/entity/golem_overlay.png` 非眼部区域像素为不透明纯黑 `(0, 0, 0, 255)`，1.20.1/1.21.1 依赖 `RenderType.eyes` 的加法混合（`ONE, ONE`）使黑色像素贡献为 `0`。MC 26.1.2 原版将 `RenderPipelines.EYES` 改为了 `BlendFunction.TRANSLUCENT`（`SRC_ALPHA, ONE_MINUS_SRC_ALPHA`），导致 `alpha=255` 的黑色像素完全覆盖底层模型。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/render/EmbersRenderTypes.java`：新增使用 `BlendFunction.ADDITIVE` 且无深度写入的自发光管线 `ADDITIVE_EYES_PIPELINE` 并注册，提供缓存方法 `EmbersRenderTypes.eyes(Identifier)`
  - `src/main/java/com/rekindled/embers/entity/render/AncientGolemEyeLayer.java`：改为调用 `EmbersRenderTypes.eyes(GOLEM_OVERLAY)`
- **验证状态**：已通过客户端实机渲染与截图验证（`shots_r3/bug08_ancient_golem.png`：魔像砖石机身与橙红发光独眼正常叠加）。

### BUG-09：战利品修改器（`grandhammer` / `superheater` / `ashenamulet`）条件被简化为空谓词导致全服所有方块零掉落
- **发现阶段**：P2-2 / P9-3 生存挖掘掉落测试 + 源码审计
- **报错现象**：生存模式下无论空手或持任意工具挖掘本模组矿石/机器乃至原版石头、泥土，均完全不掉落任何方块物品（仅矿石经验球与生物击杀掉落保留）。
- **根因确认**：
  1. 前期迁移者在 `EmbersLootModifiers.java` 中简化了实现，将 `grandhammer`、`superheater`、`ashenamulet` 三个全局战利品修改器的 `MatchTool` / `MatchCurioLootCondition` 条件全部写成了空 `ItemPredicate.Builder.item()`（序列化为 `"predicate": {}`），且丢掉了优先级（1.20.1 中 `grandhammer` 为 `20`、`superheater` 为 `10`）。
  2. NeoForge 26.1.2 的 `LootModifierManager` 直接加载 `data/embers_reborn/loot_modifiers/*.json` 下的全部修改器。挖掘任意方块时 `LootContextParams.TOOL` 恒不为 `null`（空手时为 `ItemStack.EMPTY`），空 `ItemPredicate` 对任意 `ItemStack`（含空堆叠）均返回 `true`，导致每次挖掘方块都无条件触发 `GrandhammerLootModifier.doApply` 执行 `generatedLoot.clear()` 清空全部掉落！
  3. 同时 `CuriosCompat.checkForCurios` 对空饰品槽未过滤 `!stack.isEmpty()`，空谓词同样误判通过。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/util/AugmentPredicate.java`：完整实现 MC 26.1.2 的 `DataComponentPredicate` 与 `DataComponentPredicate.ConcreteType<AugmentPredicate>`（支持增强 ID 与等级判定，绝不简化）
  - `src/main/java/com/rekindled/embers/Embers.java`：在 `Registries.DATA_COMPONENT_PREDICATE_TYPE` 注册 `embers_reborn:augment`
  - `src/main/java/com/rekindled/embers/datagen/EmbersLootModifiers.java`：恢复完整工具/增强/饰品匹配谓词与 1.20.1 优先级（`grandhammer=20`、`superheater=10`、`ashenamulet=0`）
  - `src/generated/resources/data/embers_reborn/loot_modifiers/grandhammer.json`、`superheater.json`、`ashenamulet.json`：同步更新为精确匹配 `embers_reborn:grandhammer`、`predicates: {"embers_reborn:augment": {"augment": "embers_reborn:superheater"}}`、`embers_reborn:ashen_amulet` 及对应 `priority`
  - `src/main/java/com/rekindled/embers/compat/curios/CuriosCompat.java`：`checkForCurios` 增加 `!stack.isEmpty()` 校验
- **验证状态**：已通过生存实机挖掘验证（普通挖掘正常掉落原矿/圆石/泥土，`grandhammer` 清空掉落、`superheater` 自动熔炼掉锭且耗余烬、`ashen_amulet` 转化为灰烬均精确触发）。

### BUG-10：地下遗迹（`CaveStructure` / `CaveStructurePiece`）包围盒未下移、跨区块高度撕裂及缺失 `StructurePieceType` 与 `JigsawPlacement.addPieces`
- **发现阶段**：P2-4 结构定位与世界生成测试 + 1.20.1 对照审计
- **报错现象**：`CaveStructurePiece` 在定位地下洞穴地面时只修改了 `this.position` 而未同步移动 `this.boundingBox`，跨区块生成时重复调用 `getCaveFloor` 导致后生成区块读到已被覆盖的方块而发生高度突变或判空跳过；同时未注册专属 `StructurePieceType`（区块卸载重载后退化为地表浮空 `JIGSAW`），且 `CaveStructure` 遗漏了 `JigsawPlacement.addPieces` 调用。
- **根因确认**：
  1. `CaveStructure` 每 7 区块放置一个候选起点（`OCEAN_FLOOR_WG - 10`），仅当下方 `[minY+9, min(endPos.y, 64)]` 存在有效洞穴层时才实际放置遗迹方块（无洞穴列不生成方块属原版 1.20.1 设计）。
  2. 有洞穴的候选点在 `CaveStructurePiece.postProcess` 中直接赋值 `this.position` 而未调用 `this.move(0, dy, 0)`，导致 `boundingBox` 仍留在山顶高处（破坏 `StructureSpawnOverride` 魔像结构内刷怪范围），且跨多个区块边界调用 `postProcess` 时每次重算 `getCaveFloor` 造成半截遗迹错位或丢失。
  3. `CaveStructurePiece` 沿用 `StructurePieceType.JIGSAW` 未持久化 `maxHeight`/`resolvedCaveHeight`，且 `CaveStructure.java` 因 26.1.2 `JigsawPlacement.addPieces` 方法签名新增参数而在前期迁移中被删掉。
- **修复内容**：
  - `src/main/resources/META-INF/accesstransformer.cfg` & `src/main/java/com/rekindled/embers/worldgen/CaveStructure.java`：更新 26.1.2 `JigsawPlacement.addPieces` 访问转换器描述符并恢复完整调用
  - `src/main/java/com/rekindled/embers/RegistryManager.java` & `Embers.java`：新增 `STRUCTURE_PIECE_TYPES` 并注册 `embers_reborn:cave_structure_piece`
  - `src/main/java/com/rekindled/embers/worldgen/CaveStructurePiece.java`：重写 `getType()` 与 NBT 序列化构造器，单次解析并缓存 `resolvedCaveHeight`，通过 `this.move(0, dy, 0)` 同步下移 `boundingBox` 与 `position`，并补齐重载后 `context == null` 时的群系过滤回退
- **验证状态**：已通过默认世界 4 处坐标实机定位与核查验证（完整生成于地下洞穴层，含 331 个远古砖块、魔像及铜/铁/铅金属晶体种子，`shots_worldgen_p15/bug10_ruin_spectator_nightvision.png`）。

### BUG-11：6 个自定义 `RecipeInput` 默认 `isEmpty() == true` 导致冲压、混料、沸腾、钻孔、炼金、金属系数配方全部无法匹配
- **发现阶段**：P4/P7 机器配方实机测试 + 全量 `RecipeInput` 机械盘点
- **报错现象**：冲压机（仅流体+模具无底物时）、混料离心机、迷你锅炉（及催化塞/野火斯特林气态燃料）、余烬钻机（含挖掘桶）、炼金台、加压精炼器下方金属系数全部无法匹配任何配方。
- **根因确认**：MC 26.1.2 的 `RecipeInput.isEmpty()` 默认实现遍历 `0..size()-1` 检查槽位物品是否全空；当 `size() == 0`（流体/方块状态/环境/炼金上下文）或物品槽为空（无底物冲压）时返回 `true`，而 `RecipeMap.getRecipesFor` 遇到 `input.isEmpty()` 直接短路返回空流，根本不调用配方的 `matches(...)`！
- **修复内容**：在全部 6 个自定义上下文类中重写 `@Override public boolean isEmpty() { return false; }`：
  - `src/main/java/com/rekindled/embers/recipe/AlchemyContext.java`
  - `src/main/java/com/rekindled/embers/recipe/BlockStateContext.java`
  - `src/main/java/com/rekindled/embers/recipe/BoringContext.java`
  - `src/main/java/com/rekindled/embers/recipe/FluidHandlerContext.java`
  - `src/main/java/com/rekindled/embers/recipe/MixingContext.java`
  - `src/main/java/com/rekindled/embers/recipe/StampingContext.java`
- **验证状态**：已通过实机逐台机器验证（无底物冲压、混料离心、迷你锅炉、催化塞/野火斯特林气态燃料、余烬钻机、炼金台、加压精炼器金属系数全部正常工作）。

### BUG-12：`CatalysisCombustionContext` 构造器漏赋 `this.items = inv` 导致燃烧室与催化室无法工作
- **发现阶段**：P4-16 火焰反应堆（燃烧室 + 催化室）实机测试
- **报错现象**：向 `catalysis_chamber` 与 `combustion_chamber` 投入配方材料后机器完全不消耗材料、不工作。
- **根因确认**：`CatalysisCombustionContext(IItemHandlerModifiable inv, ItemStack machine)` 构造器只写了 `this.machine = machine;`，漏掉了 `this.items = inv;`，导致 `size()` 恒为 `0`、`getItem(0)` 恒为 `ItemStack.EMPTY`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/recipe/CatalysisCombustionContext.java`：在构造器中补齐 `this.items = inv;`
- **验证状态**：已通过实机验证（3 组催化/燃烧配方均正常驱动 `ignem_reactor` 运转并产出高倍率余烬）。

### BUG-13：`MixerCentrifugeTopBlockEntity` 与 `PressureRefineryBottomBlockEntity` 流体储罐（`tank`）缺失存档持久化
- **发现阶段**：P4-5 区块重载数据保持测试 + 全量 `BlockEntity` NBT 机械比对（`diff_be_nbt.py`）
- **报错现象**：混料离心机顶部输出流体罐与加压精炼器底部水罐在退出重进存档后液体清零丢失。
- **根因确认**：1.20.1 中二者继承自 `FluidHandlerBlockEntity` 自动读写 `"tank"` NBT；26.1.2 改为直接继承 `BlockEntity` 时，`MixerCentrifugeTopBlockEntity.saveAdditionalNbt` 漏写了 `"tank"`，`PressureRefineryBottomBlockEntity` 的 `loadAdditionalNbt` 与 `saveAdditionalNbt` 均漏掉了 `"tank"`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/MixerCentrifugeTopBlockEntity.java`：`saveAdditionalNbt` 补齐 `nbt.put("tank", NbtBridge.writeTank(...))`
  - `src/main/java/com/rekindled/embers/blockentity/PressureRefineryBottomBlockEntity.java`：`loadAdditionalNbt` 与 `saveAdditionalNbt` 补齐 `"tank"` 的读取与保存
- **验证状态**：已通过存档退出重进与重启客户端实机验证（`mixer_centrifuge` 顶部 `2000mB molten_dawnstone` 与 `pressure_refinery` 底部水罐完整保留）。

### BUG-14：炉心线圈（`HearthCoilBlockEntity`）缓存配方分支残缺且错误包装 `com.rekindled.embers.recipe.SingleRecipeInput`
- **发现阶段**：P4-14 炉心线圈实机测试 + 编译类型校验
- **报错现象**：炉心线圈通入余烬正常升温耗能，但上方掉落的可熔炼物品（如生铁）永远不被熔炼。
- **根因确认**：
  1. 1.20.1 原逻辑在 `cachedRecipes.containsKey(type)` 为真/假两个分支都会调用 `Misc.getRecipe` 并写入 `cachedRecipes`；26.1.2 迁移后删掉了 `else` 分支，由于初始 `cachedRecipes` 为空映射，`containsKey(type)` 永远为 `false`，导致 `Misc.getRecipe` 一次都不会执行。
  2. 此外迁移代码误将 `wrapper` 声明为自定义的 `com.rekindled.embers.recipe.SingleRecipeInput`，而原版 `AbstractCookingRecipe`（`SMELTING` / `SMOKING` / `BLASTING`）要求 `net.minecraft.world.item.crafting.SingleRecipeInput`，若强转传入会在运行时抛出 `ClassCastException`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/HearthCoilBlockEntity.java`：改用原版 `net.minecraft.world.item.crafting.SingleRecipeInput`，并无条件执行 `blockEntity.cachedRecipes.put(cookingType, Misc.getRecipe(blockEntity.cachedRecipes.get(cookingType), cookingType, wrapper, level));`
- **验证状态**：已通过实机验证（`hearth_coil` 升温后 4.5s 将上方 `raw_copper` 熔炼为 `copper_ingot`）。

### BUG-15：4 个继承型 `BlockEntity` 缺失 NeoForge 能力注册及机械泵顶部储罐缺失 NBT 持久化/同步
- **发现阶段**：P4-21 机械泵实机测试 + 全量 `BlockEntity` 继承链与能力注册机械比对
- **报错现象**：
  1. 机械泵（`mechanical_pump`）抽取下方水源后，连接在顶部的流体管道无法抽出任何水，且机械泵顶部储罐重启后数据丢失。
  2. 全量继承链比对同时发现：1.20.1 中通过 Java 父类继承 `getCapability` 的 `AlchemyPedestalTopBlockEntity`（继承 `AlchemyPedestalBlockEntity` 物品能力）、`EmberEjectorBlockEntity`（继承 `EmberEmitterBlockEntity` 余烬能力）、`EmberFunnelBlockEntity`（继承 `EmberReceiverBlockEntity` 余烬能力）在 NeoForge 按 `BlockEntityType` 注册时均被遗漏。
- **根因确认**：NeoForge `RegisterCapabilitiesEvent` 必须逐个 `BlockEntityType` 显式绑定，不能自动继承 Java 父类注册；同时 `MechanicalPumpTopBlockEntity` 脱离 `FluidHandlerBlockEntity` 后漏写了 NBT 读写与区块同步方法。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/MechanicalPumpTopBlockEntity.java`：补齐 `loadAdditional`、`saveAdditional`、`getUpdateTag`、`getUpdatePacket`、`setChanged`
  - `src/main/java/com/rekindled/embers/blockentity/BlockEntityCapabilities.java`：补齐 `MECHANICAL_PUMP_TOP_ENTITY` 流体能力与 `ALCHEMY_PEDESTAL_TOP_ENTITY` 物品能力注册
  - `src/main/java/com/rekindled/embers/Embers.java`：补齐 `EMBER_FUNNEL_ENTITY` 与 `EMBER_EJECTOR_ENTITY` 的 `EMBER_CAPABILITY` 注册
- **验证状态**：已通过实机与存档重启验证（`mechanical_pump` 顶罐经流体管道输出 `4000mB` 水、铁桶直取 `1000mB` 水、重启保留 `2520mB` 水；炼金台基座漏斗输入与余烬漏斗/喷射器传输均正常）。

### BUG-16：熔融金属、蒸汽/矮人气与粘稠流体在 26.1.2 中丢失流体阻力物理且对生存玩家无点燃/伤害
- **发现阶段**：P5-4 流体实体交互测试 + NeoForge 26.1.2 `FluidType` / `LavaFluid` 源码核查
- **报错现象**：生存模式玩家走入熔融金属（`molten_iron` 等）或蒸汽（`steam`）/矮人气（`dwarven_gas`）中既不受火焰/熔岩伤害也不被点燃，且在熔融金属、灵魂原油、矮人油中无粘稠流体移动物理。
- **根因确认**：
  1. NeoForge 26.1 将 `FluidType.move(FluidState, LivingEntity, Vec3, double)` 标记废弃并改为由 `ILivingEntityExtension.moveInFluid` 调用新的三参重载 `FluidType.move(LivingEntity, Vec3, double)`（默认返回 `false`）；本模组三个 `FluidType` 子类仅重写了旧四参方法，导致新三参方法直接返回 `false`，自定义流体物理与其中的伤害逻辑完全未执行。
  2. 同时在 MC 26.1.2 中，玩家客户端权威移动不会在服务端 `ServerPlayer` 上触发 `LivingEntity.travel`，原版 `LavaFluid` 改为通过 `Fluid.entityInside(Level, BlockPos, Entity, InsideBlockEffectApplier)` 施加 `CLEAR_FREEZE`、`LAVA_IGNITE`（点燃 15 秒，26.1.2 中 `Entity.lavaHurt()` 已拆出点火逻辑）与 `Entity::lavaHurt`（4 点熔岩伤害）。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/fluidtypes/EmbersFluidType.java` & `RegistryManager.java`：新增 `EmbersFluidType.Source` / `EmbersFluidType.Flowing` 继承 `BaseFlowingFluid` 并转发 `entityInside` 至 `EmbersFluidType.entityInside`
  - `src/main/java/com/rekindled/embers/fluidtypes/MoltenMetalFluidType.java`：重写三参 `move(LivingEntity, Vec3, double)` 恢复熔岩粘滞物理，并重写 `entityInside` 完整执行 `CLEAR_FREEZE` + `LAVA_IGNITE` + `Entity::lavaHurt`
  - `src/main/java/com/rekindled/embers/fluidtypes/ViscousFluidType.java`：重写三参 `move(LivingEntity, Vec3, double)` 恢复灵魂原油与矮人油粘滞物理
  - `src/main/java/com/rekindled/embers/fluidtypes/SteamFluidType.java`：重写三参 `move(LivingEntity, Vec3, double)` 恢复蒸汽跳跃/摩擦物理，并重写 `entityInside` 对服务端 `LivingEntity` 造成 `1.0F` `inFire` 伤害
- **验证状态**：已通过生存模式实机验证（进入 `molten_iron_block` 正常扣血并点燃 `fireTicks=300`；`steam_block` 点燃受伤；`dwarven_oil` 与 `soul_crude` 粘滞物理生效）。

### BUG-17（核查确认）：`steam_block` 与 `dwarven_gas_block` 世界内流动方式与 1.20.1 / 1.21.1 基线一致
- **发现阶段**：P5-5 蒸汽与矮人气测试对照 1.20.1 / 1.21.1 源码核查
- **核查结论**：对照 `_source-1201/.../RegistryManager.java:830-884` 与 `Embers Reborn-1211-neoforge/.../RegistryManager.java:869-922`，原版 1.20.1 与 1.21.1 基线均直接使用 `ForgeFlowingFluid` / `BaseFlowingFluid` + `LiquidBlock::new` 并设置负数 `FluidType` 密度（`steam=-1000`、`dwarven_gas=-100`）：负数密度用于储罐渲染自顶向下显示（`FluidRenderer`）及气态判定（`Misc.isGaseousFluid` -> 敞口流体罐逸散蒸汽粒子），而桶装放置在世界中的流体方块在 1.20.1、1.21.1、26.1.2 三版本中行为完全一致，并非迁移退化。

### BUG-18：`neoforge.mods.toml` 残留可选依赖声明与版本上限不符合工程规范
- **发现阶段**：`META-INF/neoforge.mods.toml` 与 `gradle.properties` 规范审查
- **问题现象**：`neoforge.mods.toml` 中写入了 `jei` 与 `curios` 两个 `type="optional"` 依赖块且描述残留 `1.21.1`，`gradle.properties` 中设置了最高版本上限（`[26.1.2,27)`、`[26.1.2.95,26.2)`）。
- **修复内容**：
  - `src/main/resources/META-INF/neoforge.mods.toml`：移除 `jei` 与 `curios` 可选依赖声明，更新版本说明为 `26.1.2 NeoForge`
  - `gradle.properties`：将版本范围调整为无上限格式（`minecraft_version_range=[26.1.2,)`、`neo_version_range=[0.0.1,)`、`loader_version_range=[0.0.1,)`）
- **验证状态**：`.\gradlew.bat classes` 资源处理通过。


---

## 第三轮修复（2026-11-14）

> 注：第二轮实机复验中，`BUG-07`（141 个 `BlockItem` 中英翻译）、`BUG-11`（6 个自定义 `RecipeInput`）、`BUG-12`（催化/燃烧室）、`BUG-13`（顶罐/底罐流体持久化）、`BUG-15`（机械泵顶罐流体能力与子类能力）、`BUG-16`（生存玩家熔融金属/蒸汽伤害与流体物理）均已实机验证通过。本轮修复第二阶段深入测试中暴露的 3 个问题（`BUG-19` ~ `BUG-21`）及专用服务端静态审查发现的 1 个隐患（`BUG-22`）。

### BUG-19：双格/配对机器方块实体未做 `instanceof` 类型检查导致半截方块残留时抛出 `ClassCastException` 崩溃锁死存档
- **发现阶段**：P4 多方块异常状态与世界重进测试 + 全量 `BlockEntity` 强转扫描
- **报错现象**：当世界中存在上半截或底座被指令/测试替换的残缺 `melter` 时，服务端每 tick 强转 `level.getBlockEntity(pos.above())` 直接崩溃并锁死存档：
  `java.lang.ClassCastException: class com.rekindled.embers.blockentity.MelterBottomBlockEntity cannot be cast to class com.rekindled.embers.blockentity.MelterTopBlockEntity at MelterBottomBlockEntity.serverTick(MelterBottomBlockEntity.java:123)`。
- **根因确认**：`MelterBottomBlockEntity`、`MixerCentrifugeBottomBlockEntity`、`MechanicalPumpBottomBlockEntity`、`StamperBlockEntity` 以及 `EntropicEnumeratorUpgrade` / `ExcavationBucketsUpgrade` 直接对相邻坐标或关联 `BlockEntity` 进行强转而未做 `instanceof` 校验（其中机械泵在顶格缺失时还会触发 NPE）。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/MelterBottomBlockEntity.java`：改为 `instanceof MelterTopBlockEntity` 安全获取
  - `src/main/java/com/rekindled/embers/blockentity/MixerCentrifugeBottomBlockEntity.java`：改为 `instanceof MixerCentrifugeTopBlockEntity` 安全获取
  - `src/main/java/com/rekindled/embers/blockentity/MechanicalPumpBottomBlockEntity.java`：改为 `level.getBlockEntity(worldPosition.above()) instanceof MechanicalPumpTopBlockEntity t`
  - `src/main/java/com/rekindled/embers/blockentity/StamperBlockEntity.java`：改为 `if (!(level.getBlockEntity(pos.below(2)) instanceof StampBaseBlockEntity stamp)) return;`
  - `src/main/java/com/rekindled/embers/upgrade/EntropicEnumeratorUpgrade.java` & `ExcavationBucketsUpgrade.java`：补齐关联 `tile` 的 `instanceof` 防护
- **验证状态**：已通过直接重进原崩溃存档 `test_p0_2612` 实机验证（无崩溃解锁恢复正常，半截残缺机器零异常）。

### BUG-20：音乐唱片 `music_disc_7f_patterns` 缺失 MC 26.1.2 `jukebox_song` 注册与 `JUKEBOX_PLAYABLE` 组件及原版发光曲名悬停提示
- **发现阶段**：P1-6 音乐唱片实机测试 + `_source-1201` 原版 `EmberRecordItem` 对照审查
- **报错现象**：手持 `embers_reborn:music_disc_7f_patterns` 右键原版唱片机（`minecraft:jukebox`）无任何响应，无法塞入播放；且悬停提示丢失了 1.20.1 原版的余烬发光曲名显示（`GlowingTextTooltip`）。
- **根因确认**：MC 1.21+/26.1.2 移除了旧 `RecordItem` 播放逻辑，唱片必须通过 `Item.Properties.jukeboxPlayable(ResourceKey<JukeboxSong>)` 绑定数据包 `data/<namespace>/jukebox_song/<id>.json`；迁移后 `EmberRecordItem` 仅保留了空壳 `super(builder.stacksTo(1))`，既未定义 `jukebox_song` JSON，也删掉了 `getTooltipImage` 的 `GlowingTextTooltip`。
- **修复内容**：
  - 新增 `src/main/resources/data/embers_reborn/jukebox_song/7f_patterns.json`（绑定 `embers_reborn:music_disc.7f_patterns`，时长 `222.2s` 对应原 `4444 ticks`，比较器输出 `7`，描述键 `item.embers_reborn.music_disc_7f_patterns.desc`）
  - 重写 `src/main/java/com/rekindled/embers/item/EmberRecordItem.java`：挂载 `.jukeboxPlayable(SONG_7F_PATTERNS)`，通过 `TooltipDisplay.DEFAULT.withHidden(DataComponents.JUKEBOX_PLAYABLE, true)` 隐藏重复灰色曲名行，并完整恢复 `getTooltipImage` 返回 `GlowingTextTooltip` 发光曲名效果
- **验证状态**：已通过实机验证（右键塞入 `minecraft:jukebox` 正常播放音乐、比较器输出信号 `7`、`GlowingTextTooltip` 发光曲名悬停显示正常）。

### BUG-21：物品表盘（`ItemDialBlockEntity`）同步含空槽容器时调用 `NbtBridge.save(ItemStack.EMPTY)` 导致服务端崩溃锁死存档
- **发现阶段**：P3-6 物品表盘实机测试 + 全量 `NbtBridge.save` 扫描
- **报错现象**：将 `embers_reborn:item_dial` 贴在含空槽位的容器上时，区块同步包构建直接抛出异常导致服务器崩溃并锁死存档：
  `java.lang.IllegalStateException: Value must be within range [1;99]: 0; Item must not be minecraft:air at NbtBridge.save(NbtBridge.java:65) at ItemDialBlockEntity.getUpdateTag(ItemDialBlockEntity.java:71)`。
- **根因确认**：1.20.1 中 `ItemStack.EMPTY.save(new CompoundTag())` 允许写出空栈而不抛错；MC 26.1.2 的 `ItemStack.CODEC` 严格禁止编码空栈（`count == 0` 或 `minecraft:air`），`ItemDialBlockEntity.getUpdateTag` 未改用 `saveOptional` 直接调用了 `NbtBridge.save`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/ItemDialBlockEntity.java`：`getUpdateTag` 改用 `NbtBridge.saveOptional(stack, provider)`，`loadAdditionalNbt` 改用对应的 `NbtBridge.parseOptional`
  - `src/main/java/com/rekindled/embers/util/NbtBridge.java`：在 `NbtBridge.save(ItemStack, Provider)` 入口补充 `if (stack.isEmpty()) return new CompoundTag();` 兜底防护，杜绝任何空栈编码崩溃
- **验证状态**：已通过直接重进原崩溃存档 `test_r2_machines` 及实机贴在空 27 格箱子上同步显示验证（零崩溃）。

### BUG-22：`ExcavationBucketsUpgrade` 外层服务端类暴露客户端专属字段 `BlockStateModelPart` 导致专用服务器潜在 `NoClassDefFoundError`
- **发现阶段**：P13 专用服务器联机前置全量服务端类客户端引用扫描
- **问题现象**：`ExcavationBucketsUpgrade` 虽设有 `CLientStuff` 内部类隔离渲染逻辑，但迁移时将 `public static net.minecraft.client.renderer.block.dispatch.BlockStateModelPart buckets;` 静态字段留在了外层服务端升级类上，且残留了 `import net.minecraft.client.Minecraft;`；在无客户端类的专用服务器（`runServer`）加载挖掘桶升级时存在触发 `NoClassDefFoundError` 的风险。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/upgrade/ExcavationBucketsUpgrade.java`：移除客户端 import，将 `buckets` 字段移入 `ExcavationBucketsUpgrade.CLientStuff` 内部类
  - `src/main/java/com/rekindled/embers/EmbersClientEvents.java`：同步更新模型烘焙赋值为 `ExcavationBucketsUpgrade.CLientStuff.buckets`
- **验证状态**：已通过实机与专用服务器验证（`runServer` 启动与玩家联机交互全程 `0` `NoClassDefFoundError` / `ERROR`）。

---

## 第四轮修复（2026-11-14，P8 增强全量源码机械比对与 `CUSTOM_DATA` 写回审计修复）

> 注：第三轮实机与专用服务器联机复验中，`BUG-07` ~ `BUG-22` 已全部在客户端/专用服务器实测通过（含原崩溃存档 `test_p0_2612`、`test_r2_machines` 直接重进恢复、唱片机播放与比较器信号 `7`、地下洞穴遗迹生成与魔像眼部发光渲染、`P13` 专用服务器联机、`P16` 性能与百格管道压测零丢包）。本轮在对 `P8` 全部增强类及全工程 `DataComponents.CUSTOM_DATA` 读写做全量机械审计时，进一步捕获并修复以下 4 个问题（`BUG-23` ~ `BUG-26`）。

### BUG-23：灰烬护甲苦痛宝石槽（`AshenArmorGemItem`）拆卸宝石未写回 `CUSTOM_DATA`（无限刷宝石）且镶嵌未充能宝石受击抛 `NullPointerException`
- **发现阶段**：P8-14 苦痛宝石（`inflictor_gem`）源码与全工程 `_getOrCreateTag` 写回机械审计
- **问题现象**：
  1. 在工作台中对镶嵌了苦痛宝石的 `ashen_cloak` 执行拆卸配方（`GemUnsocketRecipe`）时，剩余栏返还了宝石，但合成产出的斗篷仍保留全部已镶嵌宝石，导致无法清空槽位并可无限复制苦痛宝石。
  2. 将尚未吸收伤害类型（未调谐，`getAttunedSource(stack) == null`）的苦痛宝石镶嵌进 `ashen_cloak` 后穿戴受击时，直接抛出 `NullPointerException`（`gem.getAttunedSource(stack).equals(...)` 空指针解引用）。
- **根因确认**：
  1. `AshenArmorGemItem.clearGems(ItemStack holder)` 调用 `_getOrCreateTag(holder)` 获取的是 `CustomData.copyTag()` 副本，循环 `remove("gem" + i)` 后漏掉了 `_setTag(holder, tagCompound)` 写回组件。
  2. `AshenArmorGemItem.getTotalDamageResistance` 未对 `gem.getAttunedSource(stack)` 返回 `null` 做判空保护。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/item/AshenArmorGemItem.java`：`clearGems` 末尾补齐 `_setTag(holder, tagCompound);`；`getTotalDamageResistance` 改为 `java.util.Objects.equals(gem.getAttunedSource(stack), source.type().msgId())`
- **验证状态**：已通过第四轮 P8-14 实机验证（未调谐宝石镶嵌受击不抛 NPE；调谐 `inFire` 后切换充能模型并减免 `35%` 火焰伤害 `10 -> 6.5`；`GemUnsocketRecipe` 拆卸后斗篷 `gemCount=0` 无复制并返还已调谐宝石）。

### BUG-24：绕卷齿轮增强（`WindingGearsAugment`）蓄力修改 `copyTag()` 后未写回 `CUSTOM_DATA` 导致蓄力值恒为 `0`
- **发现阶段**：P8-4 绕卷齿轮（`winding_gears`）与全工程 `_getTag` / `_setTag` 机械审计
- **问题现象**：为工具/靴子安装 `winding_gears` 后右键蓄力只播放挥手动作，发条蓄力值（`charge`）始终停留在 `0`：弹簧 HUD 进度条不增长、挖掘加速与满蓄力伤害不生效、跳跃/落地弹跳与自动攻击均无法触发。
- **根因确认**：`WindingGearsAugment.setCharge` 与 `addCharge` 调用 `_getTag(stack)` 拿到的是 `DataComponents.CUSTOM_DATA` 的 `copyTag()` 副本，写入 `TAG_CHARGE` 与 `TAG_CHARGE_TIME` 后从未调用 `_setTag(stack, tagCompound)` 保存回 `ItemStack`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/augment/WindingGearsAugment.java`：`setCharge` 改用 `_getOrCreateTag(stack)` 并在写入 `TAG_CHARGE`、`TAG_CHARGE_TIME` 后调用 `_setTag(stack, tagCompound)`；精简 `addCharge` 直接复用 `setCharge`
- **验证状态**：已通过第四轮 P8-4 实机验证（右键蓄力正常写入 `49.60`、HUD 弹簧条增长至 `clientSpool=71`、靴子跳跃消耗蓄力并提升垂直位移 `3.706` vs `1.252`）。

### BUG-25：爆破核心战斗爆炸（`BlastingExplosion`）在 26.1.2 迁移中被简化删除了对生物的爆炸击退效果
- **发现阶段**：P8-11 爆破核心（`blasting_core`）对照 `_source-1201` 源码逐行比对
- **问题现象**：武器或护甲触发 `blasting_core` 爆炸时，被命中的 `LivingEntity` 完全没有爆炸击退（`knockback = 0.0D`），与 1.20.1 原版按爆炸击退抗性衰减后施加击退的行为不一致。
- **根因确认**：1.20.1 原版 `BlastingExplosion.explode()` 通过 `ProtectionEnchantment.getExplosionKnockbackAfterDampener(livingentity, d10)` 计算生物击退；前期迁移至 26.1.2 时因旧方法移除而直接简化为 `if (entity instanceof LivingEntity living) living.hurtTime = 0; else knockback = power;`（`LivingEntity` 击退恒为 `0`）。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/augment/BlastingExplosion.java`：恢复与 1.20.1 及 26.1.2 原版 `ServerExplosion` 一致的生物爆炸击退计算 `knockback = power * (1.0D - living.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE))`（同时保留 `living.hurtTime = 0` 与免伤使用者逻辑），并清理残留英文注释
- **验证状态**：已通过第四轮 P8-11 实机战斗验证（剑镶嵌 `blasting_core` 攻击触发范围爆伤与击退 `golemSpeed=0.758`、`huskSpeed=0.638`，使用者免伤）。

### BUG-26：爆破核心挖掘爆炸（`ToolExplosion`）缺失不可破坏方块过滤与 `EventHooks.onExplosionDetonate` 事件派发
- **发现阶段**：P8-11 爆破核心（`blasting_core`）源码审查
- **问题现象**：使用带 `blasting_core` 的工具紧贴基岩、末地传送门框架等不可破坏方块（`destroySpeed < 0`）挖掘普通方块时，`getBlastCube` 选中的相邻基岩会被 `state.onBlockExploded` 直接炸除；且自定义 `explode()` 未触发 `EventHooks.onExplosionDetonate`。
- **根因确认**：26.1.2 `ToolExplosion` 重写 `ServerExplosion.explode()` 时未派发 `EventHooks.onExplosionDetonate(level, this, ...)`，也未过滤 `state.getDestroySpeed(level, pos) < 0` 的不可破坏方块。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/augment/ToolExplosion.java`：在破坏循环前补齐 `EventHooks.onExplosionDetonate(level, this, new ArrayList<>(), positions)`，跳过 `state.isAir() || state.getDestroySpeed(level, pos) < 0` 的方块并按实际破坏数量返回 `brokenCount`，同时清理残留英文注释
- **验证状态**：已通过第四轮 P8-11 实机挖掘验证（紧贴 `minecraft:bedrock` 挖掘石头爆破额外 7 格石块，`bedrock` 保持完好且玩家无伤）。

