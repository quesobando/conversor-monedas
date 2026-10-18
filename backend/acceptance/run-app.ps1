# Arranca la API contra el entorno de aceptación (Postgres local + mock de Frankfurter).
# Ejecutar desde backend/:   .\acceptance\run-app.ps1
# Las variables de entorno tienen prioridad sobre application.yml y sobre tu .env,
# y solo viven en esta ventana de PowerShell.

$env:SPRING_DATASOURCE_URL         = "jdbc:postgresql://localhost:5433/conversor"
$env:SPRING_DATASOURCE_USERNAME    = "conversor"
$env:SPRING_DATASOURCE_PASSWORD    = "conversor"
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = "update"
$env:FRANKFURTER_BASE_URL          = "http://localhost:8081/v1"

Write-Host "BD:         $env:SPRING_DATASOURCE_URL"
Write-Host "Frankfurter: $env:FRANKFURTER_BASE_URL"
gradle bootRun
