$s = "E:\模组\mc源码\2612sources\net\minecraft"
"--TagEntry optional"
Select-String "$s\tags\TagEntry.java" -Pattern 'public static TagEntry \w+\(' | ForEach-Object { $_.Line.Trim() }
"--TagsProvider appender methods"
Select-String "$s\data\tags\TagsProvider.java" -Pattern 'protected .*\(TagKey<T>' | ForEach-Object { $_.Line.Trim() }
"--ItemPredicate package"
Get-ChildItem "$s\advancements" -Recurse -Filter ItemPredicate.java | ForEach-Object { $_.FullName.Substring($s.Length) }
"--SpawnerData"
Get-ChildItem "$s\world\level\biome" -Filter MobSpawnSettings.java | Select-String 'record SpawnerData|public SpawnerData\(' | ForEach-Object { $_.Line.Trim() }
"--WeightedList"
Get-ChildItem "$s\util\random" -Filter *.java | ForEach-Object Name
Select-String "$s\util\random\WeightedList.java" -Pattern 'public static <E> WeightedList<E> (of|create)\(|public static <E> Builder<E> builder' | ForEach-Object { $_.Line.Trim() }
"--AddSpawnsBiomeModifier"
Get-ChildItem "E:\模组\mc源码\2612neoforge-sources" -Recurse -Filter BiomeModifiers.java | Select-String 'record AddSpawnsBiomeModifier|public AddSpawnsBiomeModifier\(' | ForEach-Object { $_.Line.Trim() }
"--StructureSpawnOverride"
Get-ChildItem "$s\world\level\levelgen\structure" -Filter StructureSpawnOverride.java | Select-String 'record' | ForEach-Object { $_.Line.Trim() }
"--chain blocks"
Select-String "$s\world\level\block\Blocks.java" -Pattern 'public static final Block \w*CHAIN\b' | ForEach-Object { $_.Line.Trim() }
