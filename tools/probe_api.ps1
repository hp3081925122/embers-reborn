$w = Split-Path $PSScriptRoot -Parent
$j = "$w\src\main\java\com\rekindled\embers"
"--shader/mithril/depthBuffer usages"
Get-ChildItem $j -Recurse -Filter *.java | Select-String -Pattern 'RegisterShadersEvent|ShaderInstance|mithrilShader|additiveShader|emberParticle\w*Shader|translucentParticleShader|EmbersRenderTypes\.MITHRIL\b|depthBuffer|applyParticleUniforms' | ForEach-Object { $_.Filename + ':' + $_.LineNumber + ': ' + $_.Line.Trim() }
"--SamplerCache"
$sc = Get-ChildItem "E:\模组\mc源码\2612sources\com\mojang\blaze3d" -Recurse -Filter 'SamplerCache.java' | Select-Object -First 1
$sc.FullName
Select-String $sc.FullName -Pattern 'public GpuSampler \w+\(' | ForEach-Object { $_.Line.Trim() -replace '\s*\{.*', '' }
Get-ChildItem "E:\模组\mc源码\2612sources\com\mojang\blaze3d" -Recurse -Filter 'FilterMode.java' | ForEach-Object { (Select-String $_.FullName -Pattern '^package').Line }
"--RenderSystem.getSamplerCache"
Select-String "E:\模组\mc源码\2612sources\com\mojang\blaze3d\systems\RenderSystem.java" -Pattern 'getSamplerCache' | ForEach-Object { $_.Line.Trim() }
"--TextureAtlas locations"
Select-String "E:\模组\mc源码\2612sources\net\minecraft\client\renderer\texture\TextureAtlas.java" -Pattern 'public static final Identifier' | ForEach-Object { $_.Line.Trim() }
"--vanilla particle layer pipelines in SingleQuadParticle"
Select-String "E:\模组\mc源码\2612sources\net\minecraft\client\particle\SingleQuadParticle.java" -Pattern 'Layer\(' | ForEach-Object { $_.Line.Trim() }
