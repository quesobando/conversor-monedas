function fn() {
  // La URL base llega desde AcceptanceTest (-DbaseUrl=...). Por defecto, la app local.
  var baseUrl = karate.properties['baseUrl'] || 'http://localhost:8080';
  karate.configure('connectTimeout', 5000);
  karate.configure('readTimeout', 15000);
  return { baseUrl: baseUrl };
}
