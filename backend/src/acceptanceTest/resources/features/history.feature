Feature: Historial de conversiones (GET /api/history)

  Background:
    * url baseUrl

  Scenario: Una conversión realizada queda registrada en el historial
    # Monto aleatorio para identificar nuestra conversión aunque otras corran en paralelo
    * def amount = Math.floor(Math.random() * 9000) + 1000

    Given path 'api', 'convert'
    And request { from: 'USD', to: 'EUR', amount: '#(amount)' }
    When method post
    Then status 200

    # Primero se pregunta cuántos registros hay, para pedir TODOS en una sola página.
    # Así la prueba no depende del orden del historial ni del tamaño de la base de datos.
    Given path 'api', 'history'
    And param page = 0
    And param size = 1
    When method get
    Then status 200
    * def total = response.totalElements

    Given path 'api', 'history'
    And param page = 0
    And param size = total + 50
    When method get
    Then status 200
    And match response.content[*].amount contains amount

  Scenario: La paginación respeta el tamaño solicitado
    # Garantiza que haya al menos un registro
    Given path 'api', 'convert'
    And request { from: 'EUR', to: 'USD', amount: 10 }
    When method post
    Then status 200

    Given path 'api', 'history'
    And param page = 0
    And param size = 1
    When method get
    Then status 200
    And match response.content == '#[1]'
    And match response.totalElements == '#number'
