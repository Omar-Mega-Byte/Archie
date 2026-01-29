package com.archie.config;

/**
 * Supported database types for code generation
 */
public enum DatabaseType {
    H2("H2 In-Memory", "org.h2.Driver", "jdbc:h2:mem:testdb", "org.hibernate.dialect.H2Dialect"),
    POSTGRESQL("PostgreSQL", "org.postgresql.Driver", "jdbc:postgresql://localhost:5432/mydb",
            "org.hibernate.dialect.PostgreSQLDialect"),
    MYSQL("MySQL", "com.mysql.cj.jdbc.Driver", "jdbc:mysql://localhost:3306/mydb",
            "org.hibernate.dialect.MySQLDialect"),
    MONGODB("MongoDB", "mongodb", "mongodb://localhost:27017/mydb", ""),
    SQLITE("SQLite", "org.sqlite.JDBC", "jdbc:sqlite:mydb.db", "org.hibernate.community.dialect.SQLiteDialect");

    private final String displayName;
    private final String driverClass;
    private final String defaultUrl;
    private final String hibernateDialect;

    DatabaseType(String displayName, String driverClass, String defaultUrl, String hibernateDialect) {
        this.displayName = displayName;
        this.driverClass = driverClass;
        this.defaultUrl = defaultUrl;
        this.hibernateDialect = hibernateDialect;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDriverClass() {
        return driverClass;
    }

    public String getDefaultUrl() {
        return defaultUrl;
    }

    public String getHibernateDialect() {
        return hibernateDialect;
    }
}
