# Entorno de aceptación (local)

Levanta Postgres y el mock de Frankfurter, arranca la API apuntando a ellos y corre Karate.
Requiere Docker.

```powershell
# 1) Dependencias (desde backend/)
docker compose -f acceptance/docker-compose.yml up -d --wait

# 2) Terminal A: la API contra el entorno de aceptación
$env:SPRING_DATASOURCE_URL      = "jdbc:postgresql://localhost:5433/conversor"
$env:SPRING_DATASOURCE_USERNAME = "conversor"
$env:SPRING_DATASOURCE_PASSWORD = "conversor"
$env:SPRING_JPA_HIBERNATE_DDL_AUTO = "update"
$env:FRANKFURTER_BASE_URL       = "http://localhost:8081/v1"
gradle bootRun

# 3) Terminal B: las pruebas
gradle acceptanceTest

# 4) Limpiar
docker compose -f acceptance/docker-compose.yml down -v
```

Reportes: `build/karate-reports/karate-summary.html`

Los stubs de Frankfurter están en `acceptance/wiremock/mappings/`. Para añadir un caso,
copia un JSON de esa carpeta y reinicia el contenedor `wiremock`.
