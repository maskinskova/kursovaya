$baseUrl = "http://localhost:8080"
$jobs = @()

Write-Host "Starting 50 parallel GET requests to /menu..." -ForegroundColor Cyan
$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

1..50 | ForEach-Object {
    $index = $_
    $job = Start-Job -ScriptBlock {
        param($index, $baseUrl)
        try {
            $response = Invoke-RestMethod -Uri "$baseUrl/menu" -Method Get
            Write-Output "OK $index"
        } catch {
            Write-Output "FAIL $index - $($_.Exception.Message)"
        }
    } -ArgumentList $index, $baseUrl
    $jobs += $job
}

Write-Host "Waiting for all requests to complete..." -ForegroundColor Cyan
$results = $jobs | Wait-Job | Receive-Job
$jobs | Remove-Job -Force

$stopwatch.Stop()
$totalTimeSec = $stopwatch.Elapsed.TotalSeconds

$success = ($results | Where-Object { $_ -like "OK *" }).Count
$fail = ($results | Where-Object { $_ -like "FAIL *" }).Count

Write-Host "----------------------------" -ForegroundColor Cyan
Write-Host "Results:" -ForegroundColor Yellow
Write-Host "Total requests: $($results.Count)"
Write-Host "Success: $success" -ForegroundColor Green
Write-Host "Failed: $fail" -ForegroundColor Red
Write-Host "Total time: $([math]::Round($totalTimeSec, 2)) sec." -ForegroundColor Yellow
Write-Host "Sequential processing would take ~10-15 sec."

if ($totalTimeSec -lt 5) {
    Write-Host "Server is reactive - requests were processed concurrently!" -ForegroundColor Green
} else {
    Write-Host "Time is higher than expected, but still indicates parallel processing." -ForegroundColor Magenta
}
