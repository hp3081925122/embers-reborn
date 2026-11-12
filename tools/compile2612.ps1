param([string]$Log = "c.log")
$w = Split-Path $PSScriptRoot -Parent
$env:JAVA_TOOL_OPTIONS = "-Duser.language=en -Dfile.encoding=UTF-8"
$p = Start-Process -FilePath "$w\gradlew.bat" -ArgumentList "compileJava","--console=plain","--offline" -WorkingDirectory $w -RedirectStandardOutput "$w\build\$Log" -RedirectStandardError "$w\build\$Log.err" -Wait -PassThru -NoNewWindow
"exit=$($p.ExitCode)"
$lines = Get-Content "$w\build\$Log.err" -Encoding UTF8
$errs = $lines | Select-String -Pattern '\.java:\d+: error: (.*)$'
"errors=$($errs.Count)"
"--- by file ---"
$errs | ForEach-Object { ($_.Line -replace '^\s*.*\\java\\com\\rekindled\\embers\\','' -replace ':\d+: error:.*$','') } | Group-Object | Sort-Object Count -Descending | Select-Object -First 40 | ForEach-Object { "{0,4} {1}" -f $_.Count, $_.Name }
"--- missing symbols ---"
$syms = for ($i = 0; $i -lt $lines.Count; $i++) { if ($lines[$i] -match 'symbol:\s+(.*)$') { $Matches[1].Trim() } }
$syms | Group-Object | Sort-Object Count -Descending | Select-Object -First 60 | ForEach-Object { "{0,4} {1}" -f $_.Count, $_.Name }
"--- other messages ---"
$errs | ForEach-Object { $_.Matches[0].Groups[1].Value } | Where-Object { $_ -notmatch 'cannot find symbol' } | Group-Object | Sort-Object Count -Descending | Select-Object -First 30 | ForEach-Object { "{0,4} {1}" -f $_.Count, $_.Name }
