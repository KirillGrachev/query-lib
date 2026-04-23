# query-lib

Tired of writing endless boilerplate code for `Connection`s, `PreparedStatement`s, and `ResultSet`s? Want to generate SQL safely without risking injection vulnerabilities?

**query-lib** is a lightweight, zero-dependency Java library that simplifies database interactions. It allows you to:
- **Generate type-safe SQL queries** using a fluent Builder API.
- **Execute updates and queries** with automatic resource management (no more leaked connections!).
- **Map results** directly to objects using functional interfaces.
- **Execute batches** efficiently and safely.

All of this with clean, readable code. `SQLException`s remain checked, ensuring you handle errors explicitly.

## Example

**Vanilla Java:**
```java
Optional<String> result;
String sql = "SELECT `value` FROM `table` WHERE `key` = ?";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.setInt(1, key);
    try (ResultSet set = p.executeQuery()) {
        result = set.next() ? Optional.ofNullable(set.getString(1)) : Optional.empty();
    }
}
result.ifPresent(System.out::print);
```

**With query-lib (New API):**
```java
import me.saharnooby.lib.query.query.SelectQuery;
import me.saharnooby.lib.query.executor.QueryExecutor;

// ...

QueryExecutor.queryOne(source, 
    SelectQuery.from("table")
        .select("value")
        .where("key", key),
    rs -> rs.getString("value")
).ifPresent(System.out::print);
```

Cleaner, safer, and less error-prone. See [EXAMPLES.md](EXAMPLES.md) for more advanced usage.

## Key Features & Changes in v2.0

*   **Modern Architecture:** Moved from inheritance to composition. Queries are built using independent components (`WhereClause`, `AssignmentClause`).
*   **Enhanced Security:** Strict validation of SQL identifiers (table/column names) prevents SQL injection via identifiers.
*   **Fluent API:** Intuitive builders for `SELECT`, `INSERT`, `UPDATE`, `DELETE`, and `CREATE TABLE`.
*   **Centralized Execution:** All queries are executed via `QueryExecutor`, ensuring consistent resource handling.
*   **Safe Batching:** `BatchExecutor` ensures all batched queries share the same structure, preventing runtime errors.

## How to Use

### Add as a dependency

*(Note: Update version number accordingly)*

#### Maven
```xml
<dependency>
    <groupId>me.saharnooby</groupId>
    <artifactId>query-lib</artifactId>
    <version>2.0.0-SNAPSHOT</version>
</dependency>
```

#### Gradle
```groovy
dependencies {
  implementation 'me.saharnooby:query-lib:2.0.0-SNAPSHOT'
}
```

### Usage Basics

1.  **Create a Query:** Use static factory methods in query classes (e.g., `SelectQuery.from()`, `InsertQuery.into()`).
2.  **Build it:** Chain methods like `.select()`, `.where()`, `.set()` to define your SQL.
3.  **Execute it:** Pass the query object to `QueryExecutor` along with your `DataSource`.

**Example: Inserting Data**
```java
InsertQuery query = InsertQuery.into("users")
    .set("username", "grachev_ke")
    .set("email", "emperor@example.com");

long id = QueryExecutor.insertWithKey(dataSource, query);
```

**Example: Batch Insert**
```java
InsertQuery template = InsertQuery.into("logs").set("msg", "x").set("lvl", "y");
BatchExecutor batch = BatchExecutor.of(template);

for (String msg : messages) {
    batch.add(InsertQuery.into("logs").set("msg", msg).set("lvl", "INFO"));
}
batch.execute(dataSource);
```

## Building

You will need Git, Maven, and JDK 8 or higher.

```bash
git clone https://github.com/KirillGrachev/query-lib.git
cd query-lib
mvn clean install
```

## Migration from v1.x

If you are upgrading from version 1.x, note that the API has changed significantly:
*   `AbstractQuery` is replaced by the `SqlQuery` interface.
*   Factory class `Query` is removed. Use specific query builders (`SelectQuery`, `InsertQuery`, etc.).
*   `BatchBuilder` is replaced by `BatchExecutor`.
*   `ResultSetWrapper` is removed. Use `QueryExecutor` methods for mapping.
*   `CreateTable` API has changed to use explicit column builders instead of state-dependent modifiers.

Check the source code for detailed examples of the new patterns.
