# Embers Reborn 1.21.1 NeoForge 共性 Bug 同步修复记录

- 项目路径：`E:\模组\发cf的模组\Embers Reborn\Embers Reborn-1211-neoforge`
- 分支：`master`（Minecraft `1.21.1` / NeoForge `21.1.265`）
- 对照基线：`Embers Reborn-2612-neoforge/docs/BUGFIX-2612.md` 与 `_source-1201`（Minecraft `1.20.1` 原版实现）
- 临时审计目录：`D:\AI临时文件\EmbersReborn\1211\`

---

## 一、版本差异说明（仅存在于 26.1.2、不适用于 1.21.1 的条目）

经全量代码与字节码比对，以下条目属于 Minecraft / NeoForge `26.1.2` 专属机制变更或驱动问题，`1.21.1` 原生不受影响：
- **`BUG-01`**：1.21.1 不存在 `assets/<namespace>/items/*.json` 客户端物品定义机制。
- **`BUG-04`**：1.21.1 仍保留 `minecraft:item/template_spawn_egg` 与双层染色注册，`ancient_golem_spawn_egg` 正常显示。
- **`BUG-05`**：1.21.1 的 `StampingRecipe` 使用 `.optionalFieldOf("input", Ingredient.EMPTY)`，`getDisplayInput()` 恒不为 `null`。
- **`BUG-06`**：1.21.1 的线段顶点格式不包含 26.1.2 新增的 `LineWidth` 顶点属性。
- **`BUG-07`**：1.21.1 的 `BlockItem` 自动委托 `Block.getDescriptionId()`，无需 `useBlockDescriptionPrefix()`。
- **`BUG-08`**：1.21.1 原版 `RenderType.eyes` 仍使用加法混合（`ONE, ONE`），`AncientGolemEyeLayer` 不会遮黑机身。
- **`BUG-16`**：1.21.1 的 `FluidType` 仍使用四参 `move(FluidState, LivingEntity, Vec3, double)`，与 1.20.1 签名及触发链路一致。
- **`ENV-K2`**：1.21.1 无 `MappableRingBuffer` / `GlFence` 机制。

---

## 二、1.21.1 共性 Bug 修复清单（共 16 项，保持原编号与 26.1.2 对齐）

### BUG-02：4 个模型文件缺失 `"particle"` 贴图引用导致模型烘焙粒子贴图警告
- **问题现象**：`blazing_ray`、`cinder_staff`、`pipe_inventory`（含流体管道与物品管道）、`ember_bore_blades` 未声明 `"particle"` 贴图键，客户端加载模型时产生 `#particle` 缺失引用警告。
- **修复内容**：
  - `src/main/resources/assets/embers_reborn/models/item/blazing_ray.json`：补齐 `"particle": "embers_reborn:item/tool_parts_one"`
  - `src/main/resources/assets/embers_reborn/models/item/cinder_staff.json`：补齐 `"particle": "embers_reborn:item/tool_parts_one"`
  - `src/main/resources/assets/embers_reborn/models/block/pipe_inventory.json`：补齐 `"textures": { "particle": "#pipe" }`
  - `src/main/resources/assets/embers_reborn/models/block/ember_bore_blades.json`：补齐 `"particle": "#bore"`
- **验证状态**：JSON 语法与贴图键校验通过，`compileJava` 通过。

### BUG-03：6 个模型文件面 UV 超出 `[0, 16]` 标准范围
- **问题现象**：`activator_bottom`、`clockwork_axe`、`alchemy_tablet`、`hearth_coil_corner`、`mech_edge_corner`、`stamp_base` 存在 `17.0`、`18.0`、`22.0` 等超出 `[0, 16]` 的越界 UV 坐标。
- **修复内容**：将以下 6 个模型中的越界 UV 收敛至 `[0, 16]` 对应像素区域：
  - `src/main/resources/assets/embers_reborn/models/block/activator_bottom.json`
  - `src/main/resources/assets/embers_reborn/models/item/clockwork_axe.json`
  - `src/main/resources/assets/embers_reborn/models/block/alchemy_tablet.json`
  - `src/main/resources/assets/embers_reborn/models/block/hearth_coil_corner.json`
  - `src/main/resources/assets/embers_reborn/models/block/mech_edge_corner.json`
  - `src/main/resources/assets/embers_reborn/models/block/stamp_base.json`
- **验证状态**：全量模型 UV 范围扫描 `[0.0, 16.0]` 零越界。

### BUG-09：全局战利品修改器（`grandhammer` / `superheater` / `ashenamulet`）条件被简化为空谓词导致全服方块挖掘零掉落
- **问题现象**：前期迁移将 `EmbersLootModifiers` 与 `src/generated/resources/data/embers_reborn/loot_modifiers/*.json` 中的工具/增强/饰品匹配谓词简化为空 `{}`，`AugmentPredicate` 留空注释，且 `CuriosCompat.checkForCurios` 未过滤空槽位。挖掘任意方块时 `LootContextParams.TOOL`（空手为 `ItemStack.EMPTY`）恒满足空 `ItemPredicate`，无条件触发 `GrandhammerLootModifier` 的 `generatedLoot.clear()` 清空全服所有方块掉落。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/util/AugmentPredicate.java`：完整实现 1.21.1 `ItemSubPredicate`、`CODEC` 与 `ItemSubPredicate.Type<AugmentPredicate> TYPE`（支持增强 ID 与等级判定）
  - `src/main/java/com/rekindled/embers/Embers.java`：在 `Registries.ITEM_SUB_PREDICATE_TYPE` 注册 `embers_reborn:augment`
  - `src/main/java/com/rekindled/embers/datagen/EmbersLootModifiers.java`：恢复 `GRANDHAMMER`、`AugmentPredicate`（`SUPERHEATER`）、`ASHEN_AMULET` 完整条件构建
  - `src/generated/resources/data/embers_reborn/loot_modifiers/grandhammer.json`、`superheater.json`、`ashenamulet.json`：更新为 1.21.1 标准 `ItemPredicate` JSON（`"items"` 与 `"predicates": {"embers_reborn:augment": {"augment": "embers_reborn:superheater"}}`；优先级顺序由 `data/neoforge/loot_modifiers/global_loot_modifiers.json` 中的 `grandhammer` -> `superheater` -> `ashenamulet` 保证）
  - `src/main/java/com/rekindled/embers/compat/curios/CuriosCompat.java`：`checkForCurios` 增加 `!stack.isEmpty()` 判空过滤
- **验证状态**：`compileJava` 与 JSON 结构核验通过。

### BUG-10：地下遗迹（`CaveStructure` / `CaveStructurePiece`）缺失 `JigsawPlacement.addPieces`、`StructurePieceType` 注册及包围盒同步下移
- **问题现象**：
  1. `CaveStructure.java` 中 `JigsawPlacement.addPieces` 调用被注释删除，且 `else` 分支 `list.forEach(p_227237_::addPiece)` 被注释，导致地下遗迹完全无法生成任何拼图结构块。
  2. `CaveStructurePiece` 沿用 `StructurePieceType.JIGSAW` 未注册独立 `StructurePieceType` 与 NBT 序列化构造器；`postProcess` 直接覆盖 `this.position` 而未调用 `this.move(0, dy, 0)` 下移 `boundingBox`，且跨区块生成时重复扫描 `getCaveFloor` 导致高度撕裂（外加内层循环漏重置 `pos.setY(i)`）。
- **修复内容**：
  - `src/main/resources/META-INF/accesstransformer.cfg`：更新为 1.21.1 的 13 参数 `JigsawPlacement.addPieces` 描述符（含 `PoolAliasLookup` 与 `LiquidSettings`）
  - `src/main/java/com/rekindled/embers/worldgen/CaveStructure.java`：恢复完整 `JigsawPlacement.addPieces(..., PoolAliasLookup.EMPTY, LiquidSettings.APPLY_WATERLOGGING)` 与 `else` 分支 `addPiece`
  - `src/main/java/com/rekindled/embers/RegistryManager.java` & `Embers.java`：新增 `STRUCTURE_PIECE_TYPES` 并注册 `embers_reborn:cave_structure_piece`
  - `src/main/java/com/rekindled/embers/worldgen/CaveStructurePiece.java`：实现 NBT 构造器与 `addAdditionalSaveData`、缓存 `resolvedCaveHeight`、使用 `this.move(0, dy, 0)` 同步移动包围盒、修复 `pos.setY(i)` 并在 `pContext == null` 时提供群系判定回退
- **验证状态**：AccessTransformer 重构源码与 `compileJava` 编译通过。

### BUG-11：6 个自定义 `RecipeInput` 默认 `isEmpty() == true` 导致配方短路无法匹配
- **问题现象**：1.21.1 的 `RecipeManager.getRecipeFor` 在 `input.isEmpty()` 为 `true` 时直接返回 `Optional.empty()`。6 个仅携带流体/方块状态/环境或允许无底物冲压的自定义 `RecipeInput` 未重写 `isEmpty()`，默认按物品槽判空返回 `true`，导致混料离心机、迷你锅炉（含气态燃料）、无底物冲压、余烬钻机、炼金台、加压精炼器金属系数全部无法匹配配方。
- **修复内容**：在以下 6 个上下文类中重写 `@Override public boolean isEmpty() { return false; }`：
  - `src/main/java/com/rekindled/embers/recipe/AlchemyContext.java`
  - `src/main/java/com/rekindled/embers/recipe/BlockStateContext.java`
  - `src/main/java/com/rekindled/embers/recipe/BoringContext.java`
  - `src/main/java/com/rekindled/embers/recipe/FluidHandlerContext.java`
  - `src/main/java/com/rekindled/embers/recipe/MixingContext.java`
  - `src/main/java/com/rekindled/embers/recipe/StampingContext.java`
- **验证状态**：全量 `RecipeInput` 实现类扫描与 `compileJava` 通过。

### BUG-12：`CatalysisCombustionContext` 构造器漏赋 `this.items = inv` 导致燃烧室与催化室无法工作
- **问题现象**：`CatalysisCombustionContext` 构造器未将入参 `inv` 赋值给父类字段 `this.items`，导致 `items == null`、`size() == 0`，催化室与燃烧室无法识别投入的材料。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/recipe/CatalysisCombustionContext.java`：构造器补齐 `this.items = inv;`
- **验证状态**：`compileJava` 通过。

### BUG-13：`MixerCentrifugeTopBlockEntity` 与 `PressureRefineryBottomBlockEntity` 储罐（`tank`）缺失 NBT 存档持久化
- **问题现象**：脱离 1.20.1 `FluidHandlerBlockEntity` 基类后，`MixerCentrifugeTopBlockEntity.saveAdditional` 漏存 `"tank"`，`PressureRefineryBottomBlockEntity` 的 `loadAdditional` 与 `saveAdditional` 均漏读写 `"tank"`，重载区块或重进存档后液体丢失。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/MixerCentrifugeTopBlockEntity.java`：`saveAdditional` 补齐 `nbt.put("tank", tank.writeToNBT(provider, new CompoundTag()));`
  - `src/main/java/com/rekindled/embers/blockentity/PressureRefineryBottomBlockEntity.java`：`loadAdditional` 与 `saveAdditional` 补齐 `"tank"` 的读取与写出
- **验证状态**：`compileJava` 通过。

### BUG-14：炉心线圈（`HearthCoilBlockEntity`）配方缓存死分支且误用自定义 `SingleRecipeInput`
- **问题现象**：
  1. `HearthCoilBlockEntity` 删掉了 `!cachedRecipes.containsKey(type)` 时的初始查找分支，初始空 `HashMap` 导致 `Misc.getRecipe` 永远不执行，炉心线圈无法熔炼任何物品。
  2. 将 `wrapper` 声明为 `com.rekindled.embers.recipe.SingleRecipeInput` 并强转喂给要求原版 `net.minecraft.world.item.crafting.SingleRecipeInput` 的 `AbstractCookingRecipe`，一旦触发即抛 `ClassCastException`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/HearthCoilBlockEntity.java`：改用原版 `net.minecraft.world.item.crafting.SingleRecipeInput`，并无条件执行 `blockEntity.cachedRecipes.put(type, Misc.getRecipe(blockEntity.cachedRecipes.get(type), type, wrapper, level));`
- **验证状态**：`compileJava` 无受检/非受检强转编译通过。

### BUG-15：4 个子类 `BlockEntity` 缺失 NeoForge 能力注册及机械泵顶罐缺失 NBT 持久化/同步
- **问题现象**：
  1. `MechanicalPumpTopBlockEntity` 未重写 NBT 保存读取与区块同步方法，且未在 `BlockEntityCapabilities` 注册流体能力，导致流体管道与桶无法从机械泵顶罐取水且重启丢水。
  2. `AlchemyPedestalTopBlockEntity`（物品能力）、`EmberFunnelBlockEntity` 与 `EmberEjectorBlockEntity`（余烬能力）在 1.20.1 依赖父类继承能力，迁移至 NeoForge `RegisterCapabilitiesEvent` 时漏绑各自的 `BlockEntityType`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/MechanicalPumpTopBlockEntity.java`：补齐 `loadAdditional`、`saveAdditional`、`getUpdateTag`、`getUpdatePacket`、`setChanged`
  - `src/main/java/com/rekindled/embers/blockentity/BlockEntityCapabilities.java`：补齐 `ALCHEMY_PEDESTAL_TOP_ENTITY`（`ITEM`）与 `MECHANICAL_PUMP_TOP_ENTITY`（`FLUID`）注册
  - `src/main/java/com/rekindled/embers/Embers.java`：补齐 `EMBER_FUNNEL_ENTITY` 与 `EMBER_EJECTOR_ENTITY` 的 `EMBER_CAPABILITY` 注册
- **验证状态**：`compileJava` 通过。

### BUG-18：`neoforge.mods.toml` 可选依赖与 `gradle.properties` 版本上限清理
- **问题现象**：`neoforge.mods.toml` 中包含 `jei`、`curios` 两个 `type="optional"` 依赖块；`gradle.properties` 中设置了最高版本限制（`[1.21.1,1.22)` 等）。
- **修复内容**：
  - `src/main/resources/META-INF/neoforge.mods.toml`：移除 `jei` 与 `curios` 可选依赖块
  - `gradle.properties`：统一改为无最高版本限制与 `0.0.1` 默认最低版本（`minecraft_version_range=[1.21.1,)`、`neo_version_range=[0.0.1,)`、`loader_version_range=[0.0.1,)`）
- **验证状态**：机械审计脚本校验通过。

### BUG-19：多方块配对 `BlockEntity` 未做 `instanceof` 校验导致半截方块残留时 `ClassCastException` 崩服
- **问题现象**：`MelterBottomBlockEntity`、`MixerCentrifugeBottomBlockEntity`、`MechanicalPumpBottomBlockEntity`、`StamperBlockEntity` 及升级类直接强转相邻 `BlockEntity`，半截方块残留时每 tick 抛 `ClassCastException` 或 `NullPointerException` 锁死存档。
- **修复内容**：在以下 6 个类中统一改为 `instanceof` 模式匹配安全获取：
  - `src/main/java/com/rekindled/embers/blockentity/MelterBottomBlockEntity.java`
  - `src/main/java/com/rekindled/embers/blockentity/MixerCentrifugeBottomBlockEntity.java`
  - `src/main/java/com/rekindled/embers/blockentity/MechanicalPumpBottomBlockEntity.java`
  - `src/main/java/com/rekindled/embers/blockentity/StamperBlockEntity.java`
  - `src/main/java/com/rekindled/embers/upgrade/EntropicEnumeratorUpgrade.java`
  - `src/main/java/com/rekindled/embers/upgrade/ExcavationBucketsUpgrade.java`
- **验证状态**：`compileJava` 通过。

### BUG-20：音乐唱片 `music_disc_7f_patterns` 缺失 1.21.1 `jukebox_song` 定义、`JUKEBOX_PLAYABLE` 组件与发光曲名提示
- **问题现象**：`EmberRecordItem` 被简化为空壳 `Item` 且无 `data/embers_reborn/jukebox_song/7f_patterns.json`，导致唱片无法放入原版唱片机播放，也丢失了 `GlowingTextTooltip` 余烬发光曲名显示。
- **修复内容**：
  - 新增 `src/main/resources/data/embers_reborn/jukebox_song/7f_patterns.json`（绑定 `embers_reborn:music_disc.7f_patterns`，时长 `222.2s`，比较器输出 `7`）
  - 重写 `src/main/java/com/rekindled/embers/item/EmberRecordItem.java`：挂载 `DataComponents.JUKEBOX_PLAYABLE`（`new JukeboxPlayable(new EitherHolder<>(SONG_7F_PATTERNS), false)` 隐藏原版重复灰字行）并恢复 `getTooltipImage` 返回 `GlowingTextTooltip`
- **验证状态**：`compileJava` 通过。

### BUG-21：`ItemDialBlockEntity` 与 `OpenTankBlockEntity` 对空 `ItemStack` / `FluidStack` 直接调用 `.save(provider)` 引发崩溃
- **问题现象**：1.21.1 中 `ItemStack.EMPTY.save(provider)` 与 `FluidStack.EMPTY.save(provider)` 均直接抛出 `IllegalStateException`（禁止编码空栈/空流体）。`ItemDialBlockEntity.getUpdateTag` 在同步含空槽容器时崩溃锁死存档；全工程 `.save(` 机械扫描同时发现 `OpenTankBlockEntity.saveAdditional` 在 `lastEscaped` 为 `FluidStack.EMPTY`（仅 `!= null`）时同样调用 `lastEscaped.save(provider)` 抛错。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/blockentity/ItemDialBlockEntity.java`：改为 `stack.saveOptional(provider)` 与 `ItemStack.parseOptional(provider, items.getCompound(i))`
  - `src/main/java/com/rekindled/embers/blockentity/OpenTankBlockEntity.java`：增加 `&& !lastEscaped.isEmpty()` 判定并改用 `lastEscaped.saveOptional(provider)`
- **验证状态**：全工程 `.save(` 机械审计与 `compileJava` 通过。

### BUG-22：`ExcavationBucketsUpgrade` 外层类暴露客户端专属字段 `BakedModel` 导致专用服务器潜在 `NoClassDefFoundError`
- **问题现象**：服务端升级类 `ExcavationBucketsUpgrade` 顶层声明了 `public static BakedModel buckets;` 并导入了 `Minecraft` 与 `BakedModel`，在专用服务器加载该类时存在触发客户端类缺失异常的风险。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/upgrade/ExcavationBucketsUpgrade.java`：移除外层客户端 `import`，将 `public static BakedModel buckets;` 移入 `CLientStuff` 内部类
  - `src/main/java/com/rekindled/embers/EmbersClientEvents.java`：同步更新引用为 `ExcavationBucketsUpgrade.CLientStuff.buckets`
- **验证状态**：`compileJava` 通过。

### BUG-23：灰烬护甲苦痛宝石（`AshenArmorGemItem`）拆卸未写回 `CUSTOM_DATA`（无限刷宝石）及未调谐宝石受击 NPE
- **问题现象**：
  1. `AshenArmorGemItem.clearGems` 在 `CustomData.copyTag()` 副本上删除宝石键后未调用 `_setTag(holder, tagCompound)` 写回，工作台拆卸苦痛宝石后护甲仍保留原宝石，造成无限复制。
  2. `getTotalDamageResistance` 对未调谐宝石（`getAttunedSource(stack) == null`）直接调用 `.equals(...)` 抛出 `NullPointerException`。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/item/AshenArmorGemItem.java`：`clearGems` 补齐 `_setTag(holder, tagCompound);`；`getTotalDamageResistance` 改为 `java.util.Objects.equals(gem.getAttunedSource(stack), source.type().msgId())`
- **验证状态**：全工程 `_getOrCreateTag` 写回机械审计与 `compileJava` 通过。

### BUG-24：绕卷齿轮增强（`WindingGearsAugment`）修改 `copyTag()` 后未写回 `CUSTOM_DATA` 导致无法蓄力
- **问题现象**：`WindingGearsAugment.setCharge` 与 `addCharge` 在 `_getTag(stack)` 返回的 `copyTag()` 副本上写入蓄力值后未调用 `_setTag(stack, tagCompound)`，发条蓄力恒为 `0`，所有绕卷齿轮功能失效。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/augment/WindingGearsAugment.java`：`setCharge` 改用 `_getOrCreateTag(stack)` 并调用 `_setTag(stack, tagCompound)` 持久化写回，`addCharge` 复用 `setCharge`
- **验证状态**：全工程 `_getTag` / `_setTag` 机械审计与 `compileJava` 通过。

### BUG-25：爆破核心战斗爆炸（`BlastingExplosion`）丢失生物爆炸击退计算
- **问题现象**：1.21.1 迁移时移除了 `ProtectionEnchantment.getExplosionKnockbackAfterDampener` 调用，将 `LivingEntity` 分支的 `d11` 留为 `0`（仅对非生物赋 `d11 = d10`），导致爆破核心战斗爆炸对生物完全没有击退。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/augment/BlastingExplosion.java`：恢复按原版 `Attributes.EXPLOSION_KNOCKBACK_RESISTANCE` 属性衰减的生物击退计算 `d11 = d10 * (1.0D - livingentity.getAttributeValue(Attributes.EXPLOSION_KNOCKBACK_RESISTANCE));`（同时保留 `hurtTime = 0` 与使用者免伤），并清理该段残留英文注释
- **验证状态**：`compileJava` 通过。

### BUG-26：爆破核心挖掘爆炸（`ToolExplosion`）未过滤不可破坏方块且漏掉方块掉落合并逻辑
- **问题现象**：`ToolExplosion.finalizeExplosion` 挖掘爆炸未检查 `blockstate.getDestroySpeed(myLevel, blockpos) >= 0`，紧贴基岩、末地传送门框架挖掘时会直接炸毁不可破坏方块；同时迁移时注释掉了原版爆炸掉落物合并生成逻辑。
- **修复内容**：
  - `src/main/java/com/rekindled/embers/augment/ToolExplosion.java`：增加 `&& blockstate.getDestroySpeed(myLevel, blockpos) >= 0` 过滤保护，恢复与 1.21.1 `Explosion.addOrAppendStack` 完全一致的 `ItemEntity.areMergable` / `ItemEntity.merge(..., 16)` 掉落物合并生成，并清理残留英文注释
- **验证状态**：`compileJava` 通过。
