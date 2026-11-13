# Embers Reborn：1.21.1 → 26.1.2 NeoForge 迁移交接文档

这份文档给接手的 AI。前半程的迁移已经提交，剩下的工作分成 4 个可以并行的任务，加 1 个收尾任务。每个任务写明了负责的文件、目标和验收标准，多个 AI 同时做时不会改到同一个文件。

## 1. 环境与路径

| 用途 | 路径 |
|---|---|
| 2612 工作树（在这里改） | `E:\模组\发cf的模组\Embers Reborn\Embers Reborn-2612-neoforge`，分支 `2612-neoforge`，当前提交 `887d10b` |
| 1.21.1 工作树（能编译、能玩，作为行为参考） | `E:\模组\发cf的模组\Embers Reborn\Embers Reborn-1211-neoforge`，分支 `master` |
| 1.20.1 原版源码（最终行为标准） | `E:\模组\发cf的模组\Embers Reborn\_source-1201` |
| 26.1.2 Minecraft 源码 | `E:\模组\mc源码\2612sources` |
| 26.1.2 NeoForge 源码 | `E:\模组\mc源码\2612neoforge-sources` |
| Java 源码根 | `src\main\java\com\rekindled\embers` |
| 资源根 | `src\main\resources\assets\embers_reborn` |

- MODID 是 `embers_reborn`，不要改。
- JDK 25，Gradle 用 ModDevGradle 2.0.140，NeoForge 26.1.2.116。`gradle.properties` 里已经配了本地代理 127.0.0.1:7892。
- 查 API 一律以上面两个源码目录为准，不要凭记忆写。26.1 和 1.21.1 差别很大，大量类已改名或删除。

### 编译

```powershell
pwsh -NoProfile -File tools\compile2612.ps1 -Log c6.log
pwsh -NoProfile -File tools\summarize_errors.ps1 -Err build\c6.log.err
```

- `compile2612.ps1` 会离线编译，并把 javac 输出写到 `build\<名字>.err`。`summarize_errors.ps1` 按文件、缺失符号和错误类型汇总。
- `build.gradle` 已加 `-Xmaxerrs 10000`，错误不会只显示前 100 个。
- 编译很吃内存，Gradle 堆设的是 6 GB。**多个 AI 并行时只让一个人编译**，其他人只改代码。
- Windows 路径带中文，用 cmd 调 `gradlew.bat` 会找不到文件，要用 PowerShell 调用。
- 项目文件里 CRLF 和 LF 混用，按字符串批量替换时要兼容两种换行。

### 最近一次编译结果

`build\c5.log.err`：**3058 个错误，分布在 270 个文件**。这次编译在批次 3 中途，之后又改了很多，实际错误数应该少于这个数，但这份日志仍可以用来定位每个文件的问题。错误最多的文件：

```
344 datagen\EmbersBlockStates.java
222 render\EmbersRenderTypes.java
211 datagen\EmbersRecipes.java
126 datagen\EmbersItemTags.java
104 Embers.java
 86 gui\GuiCodex.java
 84 datagen\EmbersItemModels.java
 56 RegistryManager.java
 37 render\PipeModel.java
 35 EmbersClientEvents.java
 32 render\BakedPipeModel.java
 29 research\ResearchManager.java
```

## 2. 已完成的部分（不要重做）

每一项都已提交，提交号见 `git log`。设计思路是**尽量保留 1.21.1 的机器逻辑，只在边界上加适配层**。

| 内容 | 做法 | 关键文件 |
|---|---|---|
| 构建脚本 | 换成 26.1.2 ModDevGradle 模板 | `build.gradle`、`gradle.properties` |
| 机械改名 | `ResourceLocation`→`Identifier`、`isClientSide`→`isClientSide()`、`.location()`→`.identifier()` 等 1388 处 | — |
| 物品/流体能力 | 机器内部仍用 `ItemStackHandler`/`FluidTank`/`IItemHandler`；注册时包成 26.1 的 `ResourceHandler`，查邻居时包回旧接口 | `util\LegacyHandlers.java`、`util\LegacyCaps.java`、`blockentity\BlockEntityCapabilities.java` |
| 方块实体存读档 | 26.1 的 `saveAdditional(ValueOutput)`/`loadAdditional(ValueInput)` 只在每个继承链最顶层写一次，内部转成 CompoundTag 调用旧逻辑 `saveAdditionalNbt`/`loadAdditionalNbt`，存档格式不变 | `util\NbtBridge.java` |
| NBT 读取 | `getInt` 等返回 Optional，已改成 `getIntOr(key, 默认值)`，默认值和 1.21.1 一致 | — |
| INBTSerializable | NeoForge 删了，本模组自己复制了一份 | `util\INBTSerializable.java` |
| 研究进度/移鳞护盾 | Data Attachment 改用自定义序列化器 | `NbtBridge.attachment(...)`、`RegistryManager` |
| 右键交互 | `ItemInteractionResult`/`InteractionResultHolder` 统一成 `InteractionResult` | — |
| `updateShape` | 51 处改成 26.1 参数顺序 | — |
| 方块被拆 | 26.1 拆掉 `onRemove`。改成 `LegacyRemoveBlock.onRemoveLegacy`：有方块实体的方块由 mixin 在 `BlockEntity.preRemoveSideEffects` 里调用（方块实体还在，能掉落物品）；没有方块实体的在 `affectNeighborsAfterRemoval` 里调用 | `util\LegacyRemoveBlock.java`、`mixin\BlockEntityRemoveMixin.java` |
| 注册 ID | 26.1 要求 `Item.Properties`/`BlockBehaviour.Properties` 创建前就带 ID。所有 `ITEMS.register`/`BLOCKS.register` 改成 `RegCtx.item/block`，mixin 在 Properties 构造时自动写 ID | `util\RegCtx.java`、`mixin\ItemPropertiesIdMixin.java`、`mixin\BlockPropertiesIdMixin.java` |
| Mixin 配置 | 新增，已在 `neoforge.mods.toml` 声明 | `src\main\resources\embers_reborn.mixins.json` |
| 工具 | `ToolMaterial` + `Item.Properties.sword/pickaxe/tool`；发条工具耐久改成 1 并带 UNBREAKABLE 组件 | `util\EmbersTiers.java`、`RegistryManager.ToolSet` |
| 灰烬护甲 | `ArmorMaterial` 记录 + `humanoidArmor`，贴图改用装备资源 `equipment/ashen.json` | `util\AshenArmorMaterial.java` |
| 粒子 | `SingleQuadParticle` + `getLayer()`，Provider 拿 `SpriteSet`；暂时用原版粒子着色器 | `particle\*`、`render\EmbersParticleLayers.java` |

`tools\` 下是做这些改动用的脚本，`docs\migration-2612.md` 有逐批记录。

**这些改动都还没编译通过过**，有可能写错。接手后第一次编译出的错误如果落在这些文件里，可以直接修。

## 3. 剩余任务

任务 A 到 D 互不重叠，可以同时做。任务 E 要等 A 写完接口文档后再做。最后由一个人做任务 F 收尾。

每个任务的共同规则：
- **只改自己负责的文件。** 需要在 `Embers.java`、`EmbersClientEvents.java`、`RegistryManager.java` 里加注册代码时，不要直接改，把完整代码片段写进本任务的笔记文件，由任务 F 统一合并。
- 并行时不要编译、不要运行 git。单独做时可以编译。
- 保持 1.21.1 的游戏效果：外观、混合方式、数值、手感都不变。
- **顺带审查旧迁移 bug**（见第 4 节）：对照 1.20.1 原版，如果发现 1.20.1→1.21.1 迁移时改坏的逻辑，在 2612 里修好，并在笔记里写清楚文件和行号，方便同步修 1.21.1。

### 任务 A：渲染核心和着色器

- **负责文件**：`render\EmbersRenderTypes.java`、`render\EmbersParticleLayers.java`、`render\FluidRenderer.java`、`render\FluidCuboid.java`、`render\SneakyBufferSourceWrapper.java`、`render\AlchemicalNoteItemRenderer.java`，以及 `assets\embers_reborn\shaders` 下的 14 个着色器文件。可以在 `render\` 下新建文件。
- **目标**：
  - 26.1 删除了 `RenderStateShard`、`ShaderInstance`、`GlStateManager` 以及 `RenderSystem` 的大部分状态调用。渲染类型要改用 `RenderPipeline`，参考 `RenderPipelines` 里的各个 `*_SNIPPET`、`withColorTargetState(new ColorTargetState(BlendFunction.xxx))`、`withDepthStencilState(...)`，以及源码里 26.1 的 `RenderType` 创建方法。
  - 自定义着色器要改成 26.1 的写法，uniform 用 uniform buffer 声明。可以参考 2612sources 里原版 core 着色器和原版管线声明 uniform 的方式。
  - 余烬粒子着色器（`EMBER_PARTICLE_SHADER`、`EMBER_PARTICLE_FAB_SHADER`，带 `applyParticleUniforms` 设置的自定义参数）移植好后，接进 `EmbersParticleLayers`。现在那里用的是原版粒子着色器，混合是 `SRC_ALPHA, ONE`（`BlendFunction.LIGHTNING`），不写深度，部分关深度测试。
  - `EmbersRenderTypes` 现有的公开字段和方法名尽量保留，方块实体渲染器还会引用它们。
- **交付**：
  - 代码改完；
  - 接口文档 `docs\2612-render-contract.md`：列出所有公开的渲染类型、管线和辅助方法，写明名字、Java 类型、画什么，以及 26.1 的方块实体渲染器该怎么用它们提交几何体。任务 E 只看这份文档干活，所以要写清楚；
  - 注册笔记 `docs\2612-notes-render.md`，比如 `RegisterRenderPipelinesEvent`、着色器注册。

### 任务 B：数据生成

- **负责文件**：`datagen\` 下所有文件，可以新建。
- **目标**：
  - 26.1 删除了 `BlockStateProvider`、`ItemModelProvider`、`ExistingFileHelper`、`ModelFile`、`ConfiguredModel`，改用原版 `ModelProvider`，配合 `BlockModelGenerators`、`ItemModelGenerators`，物品还要额外生成 `assets/<ns>/items/` 下的物品定义；
  - 标签提供器改了，参考 `IntrinsicHolderTagsProvider`，物品标签从方块标签复制的写法也变了；
  - 配方提供器改成 `RecipeProvider.Runner`，配料要通过 `HolderGetter` 获取；
  - `GatherDataEvent` 分成了客户端和服务端两个。
- **验收**：生成结果和 1.21.1 的 `src\generated\resources` 一致，包括模型、方块状态、标签、配方、战利品表和语言文件。
- **另外补两个物品标签**：
  - `embers_reborn:repairs_ashen_armor`，内容是 `embers_reborn:ashen_fabric`，灰烬护甲的修复材料；
  - `embers_reborn:no_repair_materials`，空标签，给发条工具用。
- **交付**：代码，以及注册笔记 `docs\2612-notes-datagen.md`（`GatherDataEvent` 的监听写法）。

### 任务 C：古代典籍和解码石板界面

- **负责文件**：`gui\GuiCodex.java`、`gui\SlateMenu.java`、`gui\SlateScreen.java`、`research\` 下所有文件，以及只负责界面绘制的 HUD、提示框类（在报告里列出是哪些）。
- **目标**：
  - 26.1 把 `GuiGraphics` 换成了 `GuiGraphicsExtractor`，以源码为准。`Tesselator`/`BufferBuilder` 立即绘制被删除，界面、控件和提示框的 API 都改了。
  - 典籍里有自定义几何：用 `TRIANGLE_STRIP` 画的波浪余烬连线、用 `TRIANGLES` 画的光圈和高亮。要移植到 26.1 的界面渲染状态或管线 API，外观不变。
  - 1.21.1 里已经修过两件事，要保留：每轮循环单独开一个缓冲；空缓冲直接跳过（`drawIfNotEmpty`）。
  - 保留解码石板的对错提示：鼠标移到某次尝试的要素图标上，显示"这个是对的/这个是错的"。数据读自放进去的炼金失败产物。
- **规则**：需要新管线时，在 `gui\` 下新建文件定义，不要改 `render\EmbersRenderTypes.java`（属于任务 A）。
- **交付**：代码，以及注册笔记 `docs\2612-notes-gui.md`。

### 任务 D：管道动态模型和灰烬护甲模型

- **负责文件**：
  - `render\PipeModel.java`、`render\BakedPipeModel.java`、`render\PipeModelBuilder.java`；
  - 灰烬护甲模型类（搜 `AshenArmorModel` 和 `ARMOR_MODEL_GETTER`），以及 `item\AshenArmorItem` 里的 `initializeClient`；
  - 其他自定义模型加载器、烘焙模型、物品模型类，可以搜 `BakedModel`、`IGeometryLoader`、`IUnbakedGeometry`、`ModelData`、`IClientItemExtensions` 找到。
  - 不包括任务 A 的文件和所有 `*BlockEntityRenderer`。
- **目标**：
  - 管道靠 `ModelData` 按每个面的连接状态显示不同模型。26.1 把 `BakedModel` 换成了 `BlockStateModel`/`BlockModelPart` 体系，`ModelData`、几何加载器和物品模型（物品定义）也改了。移植后，每个面的连接要照样正确显示。
  - 灰烬护甲的自定义人形模型，要接到 26.1 的护甲或装备模型挂钩上，原来用的是 `IClientItemExtensions`。
- **交付**：代码，以及注册笔记 `docs\2612-notes-models.md`（模型加载器、层定义、客户端扩展的注册）。

### 任务 E：30 个方块实体渲染器（等任务 A 完成）

- **负责文件**：所有 `implements BlockEntityRenderer` 的类，共 30 个，约 2000 行，可以用 `grep "implements BlockEntityRenderer"` 找全。
- **目标**：26.1 的方块实体渲染器改成三步：
  1. `createRenderState()` 创建渲染状态对象；
  2. `extractRenderState(...)` 从方块实体里读出要画的数据，存进状态对象；
  3. `submit(state, poseStack, SubmitNodeCollector, camera)` 只用状态对象里的数据提交绘制。

  绘制时不能再直接读方块实体，所有要用的数据都得先在第 2 步存好。渲染类型按 `docs\2612-render-contract.md` 来用。流体部分用任务 A 移植好的 `FluidRenderer`。
- **注册**：`EntityRenderersEvent.RegisterRenderers.registerBlockEntityRenderer` 的签名也改了，上次编译这里报了 41 处错。写进 `docs\2612-notes-ber.md`。
- 30 个类可以分给 2 到 3 个 AI 分头做，每人负责一部分。

### 任务 F：收尾（单人）

1. 把 `docs\2612-notes-*.md` 里的注册代码合进 `Embers.java`、`EmbersClientEvents.java`、`RegistryManager.java`。
2. 修剩下的零散错误。已知还有这些：
   - `appendHoverText` 的新签名：多了 `TooltipDisplay` 参数，`List<Component>` 改成 `Consumer<Component>`。之前的脚本没匹配上，要手动改；
   - `inventoryTick` 的新签名：`(ItemStack, ServerLevel, Entity, EquipmentSlot)`；
   - 物品流体能力：`Capabilities.FluidHandler.ITEM` 改成了 `Capabilities.Fluid.ITEM`，类型是 `ResourceHandler<FluidResource>`，上下文是 `ItemAccess`。涉及流体储罐物品、桶交互、`FluidVesselBlockItem`；
   - `Explosion` 和伤害相关 API。
3. 反复编译直到通过：`tools\compile2612.ps1`。
4. 删掉 `src\generated\resources`，运行 `runData`，确认生成结果和 1.21.1 一致。
5. 运行 `runClient` 冒烟测试：进入世界；放置并拆掉几台机器，确认会掉出里面的物品；管道传送物品和流体；打开古代典籍；看余烬粒子；穿灰烬护甲；退出存档再进，确认数据还在。
6. 把所有笔记里列出的"1.21.1 旧迁移 bug"同步修到 `Embers Reborn-1211-neoforge`，并在 1.21.1 编译验证。

## 4. 已知的旧迁移 bug（1.20.1 → 1.21.1）

这个模组的 1.21.1 版本是另一个 AI 迁移的，质量很差，已经修了几十处：命名空间错误、能力没注册、datagen 占位 ID、空壳编解码器、渲染缓冲用错等。所以 2612 迁移时**每改一个文件都要对照 1.20.1 原版**，确认逻辑没被改坏。常见的坏法：

- 方法签名没跟着版本变化，又没加 `@Override`，编译能过，但游戏永远不会调用这个方法。
- 原版逻辑被整段注释掉，留一句"TODO"或"迁移推迟"。
- 原版有具体值的地方被替换成空值、`Ingredient.EMPTY` 或 `return null`/`return false`。
- 贴图 UV、坐标单位、混合函数没随 API 一起换，比如 1.21 的 `sprite.getU()` 改成了按 0 到 1 的比例传参。
- 传错同名常量或标签：比如工具等级本该传"挖不出掉落的方块"标签，结果传成了"需要某级工具"标签。

目前已经在这次迁移中顺带修掉、并同步到 1.21.1 的：
- **工具挖掘等级**（1.21.1 提交 `165b2b8`）：已改回 1.20.1 的等级，铅、银、黎明石工具和 Tyrfing 为铁级，发条镐和发条斧为钻石级，发条锤为石级。

## 5. 交付清单

接手方完成后需要提供：
- 2612 能通过 `compileJava` 和 `runData`；
- 冒烟测试的结果，包括有没有崩溃、哪些功能正常、哪些不正常；
- 所有新发现的旧迁移 bug，每条写清文件和行号，说明 1.21.1 修了没有；
- 还没移植完的部分，以及原因。
