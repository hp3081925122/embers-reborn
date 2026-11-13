$s = "E:\模组\mc源码\2612sources\net\minecraft\client"
$n = "E:\模组\mc源码\2612neoforge-sources\net\neoforged\neoforge"
"--Sheets"
Select-String "$s\renderer\Sheets.java" -Pattern 'public static RenderType \w+\(' | ForEach-Object { '  ' + ($_.Line.Trim() -replace '\s*\{.*', '') }
"--QuadInstance"
$q = Get-ChildItem "$s" -Recurse -Filter 'QuadInstance.java' | Select-Object -First 1
$q.FullName
Get-Content $q.FullName | Select-String 'public (QuadInstance|void|static|\S+ set\w*)\b' | ForEach-Object { '  ' + ($_.Line.Trim() -replace '\s*\{.*', '') }
"--CustomGeometryRenderer"
Select-String "$s\renderer\SubmitNodeCollector.java" -Pattern 'interface \w+|void \w+\(' | ForEach-Object { '  ' + $_.Line.Trim() }
"--submitBlockModel"
Select-String "$s\renderer\OrderedSubmitNodeCollector.java" -Pattern 'void submitBlockModel\(' -Context 0,10 | ForEach-Object { '  ' + (($_.Line + ' ' + ($_.Context.PostContext -join ' ')) -replace '\s+', ' ') }
"--BER provider context"
Select-String "$s\renderer\blockentity\BlockEntityRendererProvider.java" -Pattern 'record Context|public \S+ \w+\(\)' -Context 0,12 | Select-Object -First 1 | ForEach-Object { $_.Line.Trim(); $_.Context.PostContext | ForEach-Object { '  ' + $_.Trim() } }
"--registerBlockEntityRenderer"
Select-String "$n\client\event\EntityRenderersEvent.java" -Pattern 'void registerBlockEntityRenderer\(' -Context 0,3 | ForEach-Object { '  ' + (($_.Line + ' ' + ($_.Context.PostContext -join ' ')) -replace '\s+', ' ') }
"--ModelState identity"
Get-ChildItem "$s\resources\model" -Recurse -Filter 'BlockModelRotation.java' | ForEach-Object { $_.FullName.Substring(40); Select-String $_.FullName -Pattern 'IDENTITY|X0_Y0|public static' | Select-Object -First 4 | ForEach-Object { '  ' + $_.Line.Trim() } }
