Feature: Conversión de monedas (POST /api/convert)

  Background:
    * url baseUrl

  @smoke
  Scenario: Convierte USD a EUR con la tasa del mock
    Given path 'api', 'convert'
    And request { from: 'USD', to: 'EUR', amount: 100 }
    When method post
    Then status 200
    And match response.from == 'USD'
    And match response.to == 'EUR'
    And match response.rate == 0.9
    And match response.convertedAmount == 90
    And match response.date == '#present'

  Scenario: Convierte EUR a USD con la tasa del mock
    Given path 'api', 'convert'
    And request { from: 'EUR', to: 'USD', amount: 50 }
    When method post
    Then status 200
    And match response.rate == 1.1
    And match response.convertedAmount == 55

  Scenario: Rechaza un monto igual a cero
    Given path 'api', 'convert'
    And request { from: 'USD', to: 'EUR', amount: 0 }
    When method post
    Then status 400
    And match response.error == 'amount: El monto debe ser mayor a 0'

  Scenario: Rechaza un monto negativo
    Given path 'api', 'convert'
    And request { from: 'USD', to: 'EUR', amount: -5 }
    When method post
    Then status 400
    And match response.error == 'amount: El monto debe ser mayor a 0'

  Scenario: Rechaza una solicitud sin moneda de origen
    Given path 'api', 'convert'
    And request { to: 'EUR', amount: 10 }
    When method post
    Then status 400
    And match response.error == 'from: La moneda de origen es obligatoria'

  Scenario: Rechaza una solicitud sin moneda de destino
    Given path 'api', 'convert'
    And request { from: 'USD', amount: 10 }
    When method post
    Then status 400
    And match response.error == 'to: La moneda de destino es obligatoria'

  Scenario: Rechaza una solicitud sin monto
    Given path 'api', 'convert'
    And request { from: 'USD', to: 'EUR' }
    When method post
    Then status 400
    And match response.error == 'amount: El monto es obligatorio'

  # HALLAZGO: una moneda no soportada por Frankfurter (404 aguas arriba) hoy llega al cliente
  # como 500 "Error inesperado". Es un error del usuario, debería ser 4xx.
  # Se deja documentado y excluido (@ignore) hasta que el equipo decida corregirlo.
  @ignore @known-issue
  Scenario: Moneda no soportada debería responder 4xx
    Given path 'api', 'convert'
    And request { from: 'USD', to: 'COP', amount: 100 }
    When method post
    Then assert responseStatus >= 400 && responseStatus < 500
