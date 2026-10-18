Feature: Variación histórica (GET /api/variation)

  Background:
    * url baseUrl

  Scenario: Devuelve la serie de tasas USD a EUR para un rango de fechas
    Given path 'api', 'variation'
    And param from = 'USD'
    And param to = 'EUR'
    And param start = '2026-01-01'
    And param end = '2026-01-03'
    When method get
    Then status 200
    And match response.from == 'USD'
    And match response.to == 'EUR'
    And match response.points == '#[3]'
    And match each response.points == { date: '#string', rate: '#number' }
