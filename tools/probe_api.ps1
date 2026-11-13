$s = "E:\模组\mc源码\2612sources\net\minecraft"
$n = "E:\模组\mc源码\2612neoforge-sources\net\neoforged\neoforge"
$w = Split-Path $PSScriptRoot -Parent
"--IConditionBuilder"
Get-ChildItem $n -Recurse -Filter 'IConditionBuilder.java' | ForEach-Object { $_.FullName }
"--slime tags"
Select-String "$n\common\Tags.java" -Pattern 'SLIME' | ForEach-Object { $_.Line.Trim() }
"--cooking serializers"
Select-String "$s\world\item\crafting\RecipeSerializer.java" -Pattern 'SMELT|BLAST' | ForEach-Object { $_.Line.Trim() }
Select-String "$s\data\recipes\RecipeProvider.java" -Pattern 'void oreCooking|CookingBookCategory' | ForEach-Object { $_.Line.Trim() }
"--receivers of identifier() errors"
$l = Get-Content "$w\build\c7.log.err" -Encoding UTF8
$out = @()
for ($i = 0; $i -lt $l.Count; $i++) {
    if ($l[$i] -match 'symbol:\s+method identifier\(\)') { $out += $l[$i + 1].Trim() }
}
$out | Group-Object | ForEach-Object { '{0,4} {1}' -f $_.Count, $_.Name }
