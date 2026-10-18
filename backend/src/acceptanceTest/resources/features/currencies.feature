@smoke
Feature: Catálogo de monedas (GET /api/currencies)

  Background:
    * url baseUrl

  Scenario: Devuelve el catálogo de monedas
    Given path 'api', 'currencies'
    When method get
    Then status 200
    And match response contains { USD: '#string', EUR: '#string', GBP: '#string' }

  Scenario: El backend está conectado al mock de Frankfurter y no a la API real
    # El mock devuelve nombres con el sufijo "(mock)". Si esto falla, la URL de Frankfurter
    # no se está tomando de FRANKFURTER_BASE_URL y el resto de pruebas no sería determinista.
    Given path 'api', 'currencies'
    When method get
    Then status 200
    And match response.USD == 'United States Dollar (mock)'
