Add-Type -AssemblyName System.Net.Http

$baseUrl = "http://localhost:8080"
$requestsCount = 200

Write-Host "Начинаем тестирование. Подготовка данных..." -ForegroundColor Cyan

$registerBody = @{ name="Load Tester"; email="load@test.com"; password="pass"; phone="123456789" } | ConvertTo-Json
try { Invoke-RestMethod -Uri "$baseUrl/auth/register" -Method Post -Body $registerBody -ContentType "application/json" -ErrorAction SilentlyContinue | Out-Null } catch {}

$loginBody = @{ email="load@test.com"; password="pass" } | ConvertTo-Json
$loginResponse = Invoke-RestMethod -Uri "$baseUrl/auth/login" -Method Post -Body $loginBody -ContentType "application/json"
$token = $loginResponse.token
$userId = $loginResponse.user.id

Write-Host "Успешная авторизация! User ID: $userId" -ForegroundColor Green

$orderBody = @{
    userId = $userId
    items = @( @{ menuItemId = 1; quantity = 2; price = 500.0 } )
    totalPrice = 1000.0
    address = "Test Street, 42"
} | ConvertTo-Json -Depth 5 -Compress

$httpClient = [System.Net.Http.HttpClient]::new()
$httpClient.DefaultRequestHeaders.Add("Authorization", "Bearer $token")
$content = [System.Net.Http.StringContent]::new($orderBody, [System.Text.Encoding]::UTF8, "application/json")
$tasks = [System.Collections.Generic.List[System.Threading.Tasks.Task]]::new()

Write-Host "Отправка $requestsCount параллельных запросов в БД..." -ForegroundColor Yellow

$stopwatch = [System.Diagnostics.Stopwatch]::StartNew()

for ($i = 0; $i -lt $requestsCount; $i++) {
    $tasks.Add($httpClient.PostAsync("$baseUrl/orders", $content))
}

try {
    [System.Threading.Tasks.Task]::WaitAll($tasks.ToArray())
} catch {
    Write-Host "Сервер не выдержал часть запросов (ожидаемо при высокой нагрузке)." -ForegroundColor DarkYellow
}

$stopwatch.Stop()
$successCount = 0
$failCount = 0

foreach ($task in $tasks) {
    if ($task.Status -eq [System.Threading.Tasks.TaskStatus]::RanToCompletion) {
        if ($task.Result.IsSuccessStatusCode) {
            $successCount++
        } else {
            $failCount++
        }
    } else {
        $failCount++
    }
}

$totalSeconds = $stopwatch.Elapsed.TotalSeconds
$rps = [math]::Round($requestsCount / $totalSeconds, 2)

Write-Host "`n=== Результаты тестирования ===" -ForegroundColor Cyan
Write-Host "Всего отправлено: $requestsCount"
Write-Host "Успешных (2xx):   $successCount" -ForegroundColor Green
Write-Host "Ошибок/Отвалов:   $failCount" -ForegroundColor Red
Write-Host "Затраченное время: $totalSeconds секунд"
Write-Host "Скорость:         $rps запросов в секунду (RPS)"
Write-Host "===============================" -ForegroundColor Cyan

$httpClient.Dispose()