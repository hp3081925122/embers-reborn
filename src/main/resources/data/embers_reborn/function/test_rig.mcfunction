# 面朝南，站在空地上执行：/function embers_reborn:test_rig
# 场地从脚下前方 6 格开始，向东、向南展开。

execute align xyz rotated 0 0 positioned ^ ^ ^6 run function embers_reborn:test_rig_build

give @s embers_reborn:tinker_hammer
give @s embers_reborn:codebreaking_slate
give @s embers_reborn:ancient_codex
give @s embers_reborn:flat_stamp
give @s embers_reborn:ember_jar
give @s embers_reborn:hearth_coil
give @s embers_reborn:ember_bore
give @s embers_reborn:ember_shard 16
give @s embers_reborn:ember_grit 64
give @s embers_reborn:copper_aspectus
give @s embers_reborn:iron_aspectus
give @s embers_reborn:ancient_motive_core
give @s embers_reborn:tinker_lens
give @s embers_reborn:molten_copper_bucket
give @s embers_reborn:molten_zinc_bucket
give @s minecraft:iron_ingot 64
give @s minecraft:copper_ore 16
give @s embers_reborn:melter
give @s embers_reborn:mixer_centrifuge
give @s minecraft:gravel 16
give @s minecraft:coal 16
give @s minecraft:cobblestone 64
give @s minecraft:iron_pickaxe
give @s minecraft:iron_helmet
give @s minecraft:water_bucket
give @s minecraft:bucket
give @s minecraft:redstone_block 4

tellraw @s {"text":"测试场在你脚下正南 6 格，向东、向南展开。朝向无所谓。","color":"gold"}
tellraw @s {"text":"1 熔炉  2 冲压  3 物品管  4 流体  5 钻孔  6 充能/灰烬/箱子  7 混料  8 活化  9 精炼  10 炼金  11 砧  12 亲手放炉心","color":"yellow"}
