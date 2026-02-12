# Spring Boot CRUD Application

A simple Spring Boot application for performing CRUD operations on Items.

## Item Structure
```json
{
    "name": "apple",
    "price": 12
}
```

## Prerequisites
- Java 17 or higher
- Maven 3.6 or higher

## Running the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## API Endpoints

### Create Item
```bash
POST http://localhost:8080/api/items
Content-Type: application/json

{
    "name": "apple",
    "price": 12
}
```

### Get All Items
```bash
GET http://localhost:8080/api/items
```

### Get Item by ID
```bash
GET http://localhost:8080/api/items/{id}
```

### Update Item
```bash
PUT http://localhost:8080/api/items/{id}
Content-Type: application/json

{
    "name": "banana",
    "price": 15
}
```

### Delete Item
```bash
DELETE http://localhost:8080/api/items/{id}
```

## Testing with curl

### Create an item:
```bash
curl -X POST http://localhost:8080/api/items \
  -H "Content-Type: application/json" \
  -d '{"name":"apple","price":12}'
```

### Get all items:
```bash
curl http://localhost:8080/api/items
```

### Get item by ID:
```bash
curl http://localhost:8080/api/items/1
```

### Update an item:
```bash
curl -X PUT http://localhost:8080/api/items/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"orange","price":15}'
```

### Delete an item:
```bash
curl -X DELETE http://localhost:8080/api/items/1
```

## H2 Database Console

Access the H2 console at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:itemdb`
- Username: `sa`
- Password: (leave empty)
