param([string]$Err)
$lines = Get-Content $Err -Encoding UTF8
$errs = $lines | Select-String -Pattern '\.java:\d+: error: (.*)$'
"errors=$($errs.Count)"
"files=" + (($errs | ForEach-Object { $_.Line -replace ':\d+: error:.*$','' } | Sort-Object -Unique).Count)
"--- top files ---"
$errs | ForEach-Object { ($_.Line -replace '^.*\\com\\rekindled\\embers\\','' -replace ':\d+: error:.*$','') } | Group-Object | Sort-Object Count -Descending | Select-Object -First 25 | ForEach-Object { "{0,4} {1}" -f $_.Count, $_.Name }
"--- missing symbols ---"
$syms = foreach ($l in $lines) { if ($l -match '^\s*symbol:\s+(.*)$') { $Matches[1].Trim() } }
$syms | Group-Object | Sort-Object Count -Descending | Select-Object -First 70 | ForEach-Object { "{0,4} {1}" -f $_.Count, $_.Name }
"--- other messages ---"
$errs | ForEach-Object { $_.Matches[0].Groups[1].Value -replace '\b[a-z]\w*\(.*?\)','()' } | Where-Object { $_ -notmatch 'cannot find symbol' } | Group-Object | Sort-Object Count -Descending | Select-Object -First 30 | ForEach-Object { "{0,4} {1}" -f $_.Count, $_.Name }
