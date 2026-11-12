# 在发射器的位置执行。目标必须是带 er_t 的标记。
execute store result block ~ ~ ~ targetX int 1 run data get entity @e[type=marker,tag=er_t,limit=1] Pos[0]
execute store result block ~ ~ ~ targetY int 1 run data get entity @e[type=marker,tag=er_t,limit=1] Pos[1]
execute store result block ~ ~ ~ targetZ int 1 run data get entity @e[type=marker,tag=er_t,limit=1] Pos[2]
kill @e[type=marker,tag=er_t]
