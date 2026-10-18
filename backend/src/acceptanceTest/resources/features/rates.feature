Feature: Tasas de cambio (GET /api/rates)

  Background:
    * url baseUrl

  Scenario: Devuelve las tasas con base USD
    Given path 'api', 'rates'
    And param base = 'USD'
    When method get
    Then status 200
    And match response contains { EUR: 0.9, GBP: 0.8 }
