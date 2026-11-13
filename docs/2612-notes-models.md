# 26.1.2 模型迁移记录

## 灰烬护甲

- `AshenArmorModel` 已改为 `HumanoidModel<HumanoidRenderState>`，保留原有头、胸、腿、靴四套 `ModelLayerLocation` 和全部 MeshDefinition 几何。
- `IClientItemExtensions` 的旧版 `getHumanoidArmorModel(LivingEntity, ItemStack, EquipmentSlot, HumanoidModel, ...)` 已迁移为 26.1.2 的 `getHumanoidArmorModel(ItemStack, EquipmentClientInfo.LayerType, Model)`。槽位从 `DataComponents.EQUIPPABLE` 的 `Equippable.slot()` 读取，避免仅依赖 LayerType（该类型只区分 humanoid 与 leggings）。
- 宝石显隐保留：`updateGems` 逐个检查 `gem0` 到 `gem6`，非斗篷物品会隐藏全部宝石。
- 披风动态计算仍保留在 `setup(LivingEntity, ItemStack)` 旧辅助方法中；26.1 装备扩展没有实体参数，当前尚未接入等价的 `HumanoidRenderState` 动画挂钩，不能宣称披风动态已完成。
- `AshenArmorItem.initializeClient` 已去掉不存在于 26.1.2 `Item/IItemExtension` 的 `@Override`。26.1.2 应在客户端注册阶段调用其扩展对象（由总线注册代码统一接入）。

## 管道模型

26.1.2 已移除旧 `BakedModel`/`ModelData` 四参数取面接口，方块模型应输出 `BlockStateModel`，几何单元为 `BlockStateModelPart`。`BakedPipeModel` 已改为 `BlockStateModel`：

- 通过 `BlockAndTintGetter#getModelData(BlockPos)` 读取 `PipeBlockEntityBase.DATA_TYPE`；
- 继续按六面 `0/1/2` 连接状态选择 center、connection、end 部件；
- 使用 729 项索引缓存部件组合；
- 保留粒子材质和 material flags 聚合。

旧 `PipeModel`/`PipeModelBuilder` 仍依赖 1.21 的 `IGeometryLoader`、`IUnbakedGeometry` 和 `CustomLoaderBuilder`。26.1.2 的对应注册入口是 `ModelEvent.RegisterLoaders`（`UnbakedModelLoader`）以及方块状态自定义模型的 `RegisterBlockStateModels`（`CustomUnbakedBlockStateModel`）。要让动态连接模型真正接入新渲染链，必须将 datagen 生成的 loader JSON 改为新 block-state model codec，并在总线注册 `PipeModel` codec；仅把旧 loader 返回类型改名会退化为静态 QuadCollection，丢失 ModelData 连接状态，故未做空实现替换。

## 旧迁移 bug

1. 旧护甲扩展使用含实体参数的 1.21 签名，26.1.2 下不会被调用；现已改为装备数据组件取槽位。
2. 旧管道 `getQuads(..., ModelData, RenderType)` 在 26.1.2 不再属于模型契约；若继续保留会导致模型无法进入 BlockStateModelDispatcher。
3. 26.1.2 `IClientItemExtensions` 不再由 `Item.initializeClient` 自动发现，必须通过客户端扩展注册事件接入。
