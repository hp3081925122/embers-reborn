$n = "E:\模组\mc源码\2612neoforge-sources\net\neoforged\neoforge"
$s = "E:\模组\mc源码\2612sources\net\minecraft"
"--FluidType.initializeClient?"
Select-String "$n\fluids\FluidType.java" -Pattern 'initializeClient' | ForEach-Object { $_.Line.Trim() }
"--RegisterClientExtensionsEvent"
Select-String "$n\client\extensions\common\RegisterClientExtensionsEvent.java" -Pattern 'public void register\w*\(' | ForEach-Object { $_.Line.Trim() }
"--FogData"
Get-ChildItem "$s\client\renderer\fog" -Filter FogData.java | ForEach-Object { Get-Content $_.FullName | Select-String 'public \w+ \w+;|class' | ForEach-Object { '  ' + $_.Line.Trim() } }
"--Material.Baked"
$m = Get-ChildItem "$s\client\resources\model\sprite" -Filter Material.java | Select-Object -First 1
Select-String $m.FullName -Pattern 'record|public Material\(|TextureAtlasSprite sprite' | ForEach-Object { '  ' + $_.Line.Trim() }
"--FluidModel accessors"
$fm = Get-ChildItem "$s\client\renderer\block" -Recurse -Filter FluidModel.java | Select-Object -First 1
Get-Content $fm.FullName | Select-Object -First 30 | Select-String 'Material|layer|Tint' | ForEach-Object { '  ' + $_.Line.Trim() }
"--IClientItemExtensions armor model hook"
Select-String "$n\client\extensions\common\IClientItemExtensions.java" -Pattern 'default \S+ \w+\(' | ForEach-Object { '  ' + ($_.Line.Trim() -replace '\s*\{.*$', '') }
