# Nequi Franchises API

API REST para administrar **franquicias**, **sucursales** y **productos** (con stock).  
Incluye arquitectura limpia (use cases + ports/adapters), persistencia con **Spring Data JPA**, **Swagger/OpenAPI**, manejo de errores y respuestas estandarizadas.

## Stack
- Java 17
- Spring Boot 3
- Maven
- Spring Web
- Spring Data JPA
- PostgreSQL (RDS) / H2 (local)
- springdoc-openapi (Swagger UI)

---

## Modelo
- **Franchise** → tiene muchas **Branch**
- **Branch** → tiene muchos **Product**
- **Product** → `name`, `stock` y **soft delete** (borrado lógico)

---

## Estructura de respuesta
Todos los endpoints responden con:

```json
{
  "status": 200,
  "message": "OK",
  "data": {}
}
```

# Endpoints
- Franquicias

1. POST /api/franchises → crear franquicia

2. PATCH /api/franchises/{franchiseId} → actualizar nombre (extra)

- Sucursales

1. POST /api/franchises/{franchiseId}/branches → crear sucursal

2. PATCH /api/franchises/{franchiseId}/branches/{branchId} → actualizar nombre (extra)

# Productos

1. POST /api/franchises/{franchiseId}/branches/{branchId}/products → crear producto

2. PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId} → actualizar nombre (extra)

3. PATCH /api/franchises/{franchiseId}/branches/{branchId}/products/{productId}/stock → actualizar stock

4. DELETE /api/franchises/{franchiseId}/branches/{branchId}/products/{productId} → soft delete (borrado lógico)

- Reporte

1. GET /api/franchises/{franchiseId}/top-products-by-branch
Retorna el producto con más stock por cada sucursal de la franquicia.


# Swagger / OpenAPI

- Swagger UI: http://localhost:8080/swagger-ui

- OpenAPI JSON: http://localhost:8080/v3/api-docs


 # Notas de diseño

- Arquitectura basada en use cases y ports/adapters para desacoplar negocio de infraestructura.
- Persistencia con JPA/Hibernate.
- Borrado lógico de productos (soft delete) para auditabilidad y consistencia.
- Respuestas estandarizadas {status, message, data} para consumo uniforme en clientes.


# Cómo correr la aplicación
```bash
mvn -Dspring.profiles.active=postgres spring-boot:run
```
o Tambien
```bash
mvn -Dspring-boot.run.profiles=postgres spring-boot:run
```



# Con docker (No comprobado por licencia :c)
```bash
docker compose -f docker-compose.rds.yml up --build
```