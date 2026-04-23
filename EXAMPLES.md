## 1. CREATE TABLE

**Vanilla Java:**
```java
String sql = "CREATE TABLE IF NOT EXISTS `table` (" +
        "`firstKey` VARCHAR(16) NOT NULL, " +
        "`secondKey` INT NOT NULL, " +
        "`textValue` TEXT, " +
        "`longValue` BIGINT, " +
        "PRIMARY KEY(`firstKey`, `secondKey`));";

try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.executeUpdate();
}
```

**With query-lib:**
> **Примечание:** Текущая реализация `CreateTable` поддерживает первичные ключи на уровне колонки (`intKey`). Для составных ключей требуется доработка или использование raw SQL.

```java
import me.saharnooby.lib.query.query.impl.CreateTable;
import java.sql.SQLException;

// Построение запроса
CreateTable createTable = CreateTable.table("table")
        .ifNotExists()
        .varchar("firstKey", 16).notNull().endColumn()
        .intKey("id").endColumn() // Пример одиночного PK
        .text("textValue").endColumn()
        .bigint("longValue").endColumn();

// Выполнение DDL через стандартный JDBC (так как QueryExecutor ориентирован на DML)
try (var conn = source.getConnection();
     var ps = conn.prepareStatement(createTable.getSql())) {
    ps.executeUpdate();
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 2. SELECT

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

**With query-lib:**
```java
import me.saharnooby.lib.query.query.impl.SelectQuery;
import me.saharnooby.lib.query.query.executor.QueryExecutor;
import java.sql.SQLException;
import java.util.Optional;

SelectQuery query = SelectQuery.from("table")
        .select("value")
        .where("key", key);

try {
    Optional<String> result = QueryExecutor.queryOne(source, query, rs -> rs.getString("value"));
    result.ifPresent(System.out::print);
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 3. INSERT

**Vanilla Java:**
```java
String sql = "INSERT INTO `table` (`key`, `value`) VALUES (?, ?);";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.setInt(1, key);
    p.setString(2, value);
    p.executeUpdate();
}
```

**With query-lib:**
```java
import me.saharnooby.lib.query.query.impl.InsertQuery;
import me.saharnooby.lib.query.query.executor.QueryExecutor;
import java.sql.SQLException;

InsertQuery query = InsertQuery.into("table")
        .set("key", key)
        .set("value", value);

try {
    QueryExecutor.executeUpdate(source, query);
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 4. INSERT ON DUPLICATE KEY UPDATE

**Vanilla Java:**
```java
String sql = "INSERT INTO `table` (`key`, `value`) VALUES (?, ?) ON DUPLICATE KEY UPDATE `value` = ?;";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.setInt(1, key);
    p.setString(2, value);
    p.setString(3, value);
    p.executeUpdate();
}
```

**With query-lib:**
```java
import me.saharnooby.lib.query.query.impl.InsertQuery;
import me.saharnooby.lib.query.query.executor.QueryExecutor;
import java.sql.SQLException;

InsertQuery query = InsertQuery.into("table")
        .set("key", key)
        .set("value", value)
        .onDuplicateKeyUpdateExcept("key"); // Обновит все поля, кроме 'key'

try {
    QueryExecutor.executeUpdate(source, query);
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 5. UPDATE

**Vanilla Java:**
```java
String sql = "UPDATE `table` SET `value` = ? WHERE `key` = ?;";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.setString(1, value);
    p.setInt(2, key);
    p.executeUpdate();
}
```

**With query-lib:**
```java
import me.saharnooby.lib.query.query.impl.UpdateQuery;
import me.saharnooby.lib.query.query.executor.QueryExecutor;
import java.sql.SQLException;

UpdateQuery query = UpdateQuery.table("table")
        .set("value", value)
        .where("key", key);

try {
    QueryExecutor.executeUpdate(source, query);
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 6. DELETE

**Vanilla Java:**
```java
String sql = "DELETE FROM `table` WHERE `key` = ?;";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.setInt(1, key);
    p.executeUpdate();
}
```

**With query-lib:**
```java
import me.saharnooby.lib.query.query.impl.DeleteQuery;
import me.saharnooby.lib.query.query.executor.QueryExecutor;
import java.sql.SQLException;

DeleteQuery query = DeleteQuery.from("table")
        .where("key", key);

try {
    QueryExecutor.executeUpdate(source, query);
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 7. Raw Query

**Vanilla Java:**
```java
String sql = "SELECT `key` FROM (SELECT * FROM `table`) sub WHERE `key` = ?;";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    p.setInt(1, key);
    try (ResultSet rs = p.executeQuery()) {
        while (rs.next()) {
            System.out.print(rs.getInt(1));
        }
    }
}
```

**With query-lib:**
Используем анонимную реализацию интерфейса `SqlQuery` для сложных запросов.

```java
import me.saharnooby.lib.query.query.api.SqlQuery;
import me.saharnooby.lib.query.query.executor.QueryExecutor;
import java.sql.SQLException;
import java.util.List;

SqlQuery rawQuery = new SqlQuery() {
    @Override
    public String getSql() {
        return "SELECT `key` FROM (SELECT * FROM `table`) sub WHERE `key` = ?;";
    }

    @Override
    public List<Object> getParams() {
        return List.of(key);
    }
};

try {
    List<Integer> results = QueryExecutor.queryList(source, rawQuery, rs -> rs.getInt("key"));
    results.forEach(System.out::print);
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```

## 8. Batches

**Vanilla Java:**
```java
List<String> valuesToBeInsert = Arrays.asList("x", "y", "z");
String sql = "INSERT INTO `table` (`value`) VALUES (?);";
try (Connection con = source.getConnection(); 
     PreparedStatement p = con.prepareStatement(sql)) {
    for (String v : valuesToBeInsert) {
        p.setString(1, v);
        p.addBatch();
    }
    p.executeBatch();
}
```

**With query-lib:**
```java
import me.saharnooby.lib.query.query.impl.InsertQuery;
import me.saharnooby.lib.query.batch.BatchExecutor;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

List<String> valuesToBeInsert = Arrays.asList("x", "y", "z");

// Создаем образец запроса для определения структуры шаблона
InsertQuery sampleQuery = InsertQuery.into("table").set("value", "");

BatchExecutor batchExecutor = BatchExecutor.of(sampleQuery);

for (String v : valuesToBeInsert) {
    // Важно: каждый добавляемый запрос должен иметь идентичную SQL-структуру
    InsertQuery query = InsertQuery.into("table").set("value", v);
    batchExecutor.add(query);
}

try {
    int[] results = batchExecutor.execute(source);
    // Обработка результатов при необходимости
} catch (SQLException e) {
    throw new RuntimeException(e);
}
```