kill @e[type=marker,tag=er_t]
fill ~-2 ~-1 ~-2 ~16 ~6 ~92 air
fill ~-2 ~-1 ~-2 ~16 ~-1 ~92 stone

# 1 熔炉。余烬链故意不连，留给锤子。
setblock ~5 ~1 ~1 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"1 熔炉"}','{"text":"锤子连这一条"}','{"text":"顶上放铁锭"}','{"text":"东边是分离器"}']}}
setblock ~0 ~0 ~4 embers_reborn:creative_ember_source
setblock ~1 ~0 ~4 embers_reborn:ember_emitter[facing=south]
setblock ~1 ~1 ~4 minecraft:redstone_block
setblock ~4 ~0 ~4 embers_reborn:ember_receiver[facing=north]
setblock ~5 ~0 ~4 embers_reborn:melter[half=lower]
setblock ~5 ~1 ~4 embers_reborn:melter[half=upper]
setblock ~6 ~0 ~4 embers_reborn:geologic_separator[facing=west]
setblock ~5 ~0 ~6 embers_reborn:mini_boiler[facing=north]
setblock ~7 ~0 ~6 embers_reborn:fluid_vessel
setblock ~6 ~0 ~6 embers_reborn:fluid_extractor
setblock ~6 ~1 ~6 minecraft:redstone_block

# 2 冲压。余烬已经连到接收器。
setblock ~5 ~1 ~9 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"2 冲压"}','{"text":"底面放平印模"}','{"text":"底座放砂砾"}','{"text":"箱子在最底下"}']}}
setblock ~5 ~0 ~12 embers_reborn:bin
setblock ~5 ~1 ~12 embers_reborn:stamp_base
setblock ~5 ~3 ~12 embers_reborn:stamper
setblock ~0 ~3 ~12 embers_reborn:creative_ember_source
setblock ~1 ~3 ~12 embers_reborn:ember_emitter[facing=south]
setblock ~1 ~4 ~12 minecraft:redstone_block
setblock ~4 ~3 ~12 embers_reborn:ember_receiver[facing=north]
summon marker ~4 ~3 ~12 {Tags:["er_t"]}
execute positioned ~1 ~3 ~12 run function embers_reborn:test_rig_link

# 3 物品管
setblock ~0 ~1 ~16 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"3 物品管"}','{"text":"铁会进右边箱子"}','{"text":"塞满再看堵塞"}','{"text":""}']}}
setblock ~0 ~0 ~18 chest[facing=north]{Items:[{Slot:0b,id:"minecraft:iron_ingot",count:16}]}
setblock ~1 ~0 ~18 embers_reborn:item_extractor
setblock ~1 ~1 ~18 minecraft:redstone_block
setblock ~2 ~0 ~18 embers_reborn:item_pipe
setblock ~3 ~0 ~18 embers_reborn:item_pipe
setblock ~4 ~0 ~18 chest[facing=north]

# 4 流体。从侧面右键灌水，不要点顶面。
setblock ~0 ~1 ~20 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"4 流体"}','{"text":"侧面灌左边罐子"}','{"text":"看右边罐子"}','{"text":""}']}}
setblock ~0 ~0 ~22 embers_reborn:fluid_vessel
setblock ~1 ~0 ~22 embers_reborn:fluid_extractor
setblock ~1 ~1 ~22 minecraft:redstone_block
setblock ~2 ~0 ~22 embers_reborn:fluid_pipe
setblock ~3 ~0 ~22 embers_reborn:fluid_pipe
setblock ~4 ~0 ~22 embers_reborn:fluid_vessel

# 5 钻孔。燃料从正上方进来。
setblock ~5 ~1 ~25 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"5 钻孔"}','{"text":"灰烬砂从顶上进"}','{"text":"看它转起来"}','{"text":""}']}}
setblock ~5 ~0 ~28 embers_reborn:ember_bore[axis=z]
setblock ~5 ~0 ~27 embers_reborn:ember_bore_edge[axis=z,edge=north]
setblock ~6 ~0 ~27 embers_reborn:ember_bore_edge[axis=z,edge=northeast]
setblock ~6 ~0 ~28 embers_reborn:ember_bore_edge[axis=z,edge=east]
setblock ~6 ~0 ~29 embers_reborn:ember_bore_edge[axis=z,edge=southeast]
setblock ~5 ~0 ~29 embers_reborn:ember_bore_edge[axis=z,edge=south]
setblock ~4 ~0 ~29 embers_reborn:ember_bore_edge[axis=z,edge=southwest]
setblock ~4 ~0 ~28 embers_reborn:ember_bore_edge[axis=z,edge=west]
setblock ~4 ~0 ~27 embers_reborn:ember_bore_edge[axis=z,edge=northwest]
setblock ~5 ~1 ~28 embers_reborn:item_pipe
setblock ~5 ~2 ~28 embers_reborn:item_extractor
setblock ~6 ~2 ~28 minecraft:redstone_block
setblock ~5 ~3 ~28 chest[facing=east]{Items:[{Slot:0b,id:"embers_reborn:ember_grit",count:32}]}

# 6 充能、灰烬台、箱子。两条余烬已经连好。
setblock ~1 ~1 ~30 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"6 充能/灰烬/箱"}','{"text":"罐子放进充能器"}','{"text":"煤放上灰烬台"}','{"text":"铁放进箱子再拆"}']}}
setblock ~1 ~0 ~31 embers_reborn:creative_ember_source
setblock ~1 ~0 ~32 embers_reborn:ember_emitter[facing=south]
setblock ~1 ~1 ~32 minecraft:redstone_block
setblock ~1 ~0 ~33 embers_reborn:ember_receiver[facing=north]
setblock ~1 ~0 ~34 embers_reborn:copper_charger
summon marker ~1 ~0 ~33 {Tags:["er_t"]}
execute positioned ~1 ~0 ~32 run function embers_reborn:test_rig_link
setblock ~5 ~0 ~34 embers_reborn:bin
setblock ~5 ~1 ~31 embers_reborn:creative_ember_source
setblock ~5 ~1 ~32 embers_reborn:ember_emitter[facing=south]
setblock ~5 ~2 ~32 minecraft:redstone_block
setblock ~5 ~1 ~33 embers_reborn:ember_receiver[facing=north]
setblock ~5 ~1 ~34 embers_reborn:cinder_plinth
summon marker ~5 ~1 ~33 {Tags:["er_t"]}
execute positioned ~5 ~1 ~32 run function embers_reborn:test_rig_link
setblock ~9 ~0 ~34 embers_reborn:bin
setblock ~10 ~0 ~34 chest[facing=west]{Items:[{Slot:0b,id:"minecraft:iron_ingot",count:64}]}

# 7 混料。余烬进上层。两边罐子分别灌铜和锌。
setblock ~4 ~1 ~36 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"7 混料"}','{"text":"北罐熔铜"}','{"text":"南罐熔锌"}','{"text":"看上层出黄铜"}']}}
setblock ~4 ~0 ~42 embers_reborn:mixer_centrifuge[half=lower]
setblock ~4 ~1 ~42 embers_reborn:mixer_centrifuge[half=upper]
setblock ~4 ~1 ~38 embers_reborn:creative_ember_source
setblock ~4 ~1 ~39 embers_reborn:ember_emitter[facing=south]
setblock ~4 ~2 ~39 minecraft:redstone_block
setblock ~4 ~1 ~41 embers_reborn:ember_receiver[facing=north]
summon marker ~4 ~1 ~41 {Tags:["er_t"]}
execute positioned ~4 ~1 ~39 run function embers_reborn:test_rig_link
setblock ~4 ~0 ~40 embers_reborn:fluid_vessel
setblock ~4 ~0 ~41 embers_reborn:fluid_extractor
setblock ~3 ~0 ~41 minecraft:redstone_block
setblock ~4 ~0 ~43 embers_reborn:fluid_extractor
setblock ~3 ~0 ~43 minecraft:redstone_block
setblock ~4 ~0 ~44 embers_reborn:fluid_vessel

# 8 活化。发射器朝南，从北边的创造源抽，接收器把余烬推进上层。
setblock ~4 ~2 ~45 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"8 活化"}','{"text":"余烬碎片放进去"}','{"text":"看上层余烬"}','{"text":""}']}}
setblock ~4 ~0 ~50 embers_reborn:ember_activator[half=lower]
setblock ~4 ~1 ~50 embers_reborn:ember_activator[half=upper]
setblock ~4 ~1 ~46 embers_reborn:creative_ember_source
setblock ~4 ~1 ~47 embers_reborn:ember_emitter[facing=south]
setblock ~4 ~2 ~47 minecraft:redstone_block
setblock ~4 ~1 ~49 embers_reborn:ember_receiver[facing=north]
setblock ~6 ~0 ~50 chest[facing=west]{Items:[{Slot:0b,id:"embers_reborn:ember_shard",count:16}]}
summon marker ~4 ~1 ~49 {Tags:["er_t"]}
execute positioned ~4 ~1 ~47 run function embers_reborn:test_rig_link

# 9 加压精炼
setblock ~4 ~2 ~51 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"9 精炼"}','{"text":"碎片放进去"}','{"text":"西边罐子灌水"}','{"text":"看上层余烬"}']}}
setblock ~4 ~0 ~56 embers_reborn:pressure_refinery[half=lower]
setblock ~4 ~1 ~56 embers_reborn:pressure_refinery[half=upper]
setblock ~4 ~1 ~52 embers_reborn:creative_ember_source
setblock ~4 ~1 ~53 embers_reborn:ember_emitter[facing=south]
setblock ~4 ~2 ~53 minecraft:redstone_block
setblock ~4 ~1 ~55 embers_reborn:ember_receiver[facing=north]
setblock ~2 ~0 ~56 embers_reborn:fluid_vessel
setblock ~3 ~0 ~56 embers_reborn:fluid_extractor
setblock ~3 ~1 ~56 minecraft:redstone_block
setblock ~6 ~0 ~56 chest[facing=west]{Items:[{Slot:0b,id:"embers_reborn:ember_shard",count:16}]}
summon marker ~4 ~1 ~55 {Tags:["er_t"]}
execute positioned ~4 ~1 ~53 run function embers_reborn:test_rig_link

# 10 炼金。炮先不要通红石。
setblock ~6 ~1 ~62 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"10 炼金"}','{"text":"四座底座按告示"}','{"text":"炮满了再贴红石"}','{"text":"看石板进度"}']}}
setblock ~6 ~0 ~66 embers_reborn:alchemy_tablet
setblock ~4 ~0 ~66 embers_reborn:alchemy_pedestal[half=lower]
setblock ~4 ~1 ~66 embers_reborn:alchemy_pedestal[half=upper]
setblock ~8 ~0 ~66 embers_reborn:alchemy_pedestal[half=lower]
setblock ~8 ~1 ~66 embers_reborn:alchemy_pedestal[half=upper]
setblock ~6 ~0 ~64 embers_reborn:alchemy_pedestal[half=lower]
setblock ~6 ~1 ~64 embers_reborn:alchemy_pedestal[half=upper]
setblock ~6 ~0 ~68 embers_reborn:alchemy_pedestal[half=lower]
setblock ~6 ~1 ~68 embers_reborn:alchemy_pedestal[half=upper]
setblock ~6 ~3 ~66 embers_reborn:beam_cannon[facing=down]
setblock ~6 ~4 ~66 embers_reborn:creative_ember_source
setblock ~10 ~0 ~66 chest[facing=west]{Items:[{Slot:0b,id:"minecraft:cobblestone",count:64},{Slot:1b,id:"embers_reborn:copper_aspectus",count:1},{Slot:2b,id:"embers_reborn:iron_aspectus",count:1},{Slot:3b,id:"embers_reborn:ember_grit",count:16},{Slot:4b,id:"minecraft:redstone_block",count:1}]}

# 11 砧。箱子在砧下面。
setblock ~4 ~2 ~72 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"11 黎明石砧"}','{"text":"镐+古代核心"}','{"text":"锤子敲到出货"}','{"text":"成品在下面箱子"}']}}
setblock ~4 ~0 ~74 embers_reborn:bin
setblock ~4 ~1 ~74 embers_reborn:dawnstone_anvil
setblock ~6 ~0 ~74 chest[facing=west]{Items:[{Slot:0b,id:"minecraft:iron_pickaxe",count:1},{Slot:1b,id:"embers_reborn:ancient_motive_core",count:1},{Slot:2b,id:"embers_reborn:tinker_lens",count:1},{Slot:3b,id:"minecraft:iron_helmet",count:1}]}

# 12 亲手放置
setblock ~4 ~1 ~80 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"12 炉心"}','{"text":"右键橙色框中心"}','{"text":"石头的顶面"}','{"text":"再拆一根边"}']}}
fill ~2 ~-1 ~82 ~6 ~-1 ~86 minecraft:orange_concrete
setblock ~4 ~-1 ~84 minecraft:stone
setblock ~12 ~1 ~80 oak_sign[rotation=8]{is_waxed:1b,front_text:{messages:['{"text":"12b 混料器"}','{"text":"亲手放在绿框"}','{"text":"看上层有没有生成"}','{"text":""}']}}
fill ~10 ~-1 ~82 ~14 ~-1 ~86 minecraft:lime_concrete
setblock ~12 ~-1 ~84 minecraft:stone
