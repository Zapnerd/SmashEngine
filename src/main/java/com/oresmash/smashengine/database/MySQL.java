package com.oresmash.smashengine.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MySQL implements DatabaseInterface {
    private final HikariDataSource dataSource;

    public static DatabaseInterface initialize(FileConfiguration config) {
        String hostPort = config.getString("database.MySQL.host");
        String[] parts = hostPort.split(":");
        String host = parts[0];
        int port = Integer.parseInt(parts[1]);
        String database = config.getString("database.MySQL.database");
        String username = config.getString("database.MySQL.username");
        String password = config.getString("database.MySQL.password");

        return new MySQL(host, database, username, password, port);
    }

    private MySQL(String host, String database, String username, String password, int port) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + database);
        config.setUsername(username);
        config.setPassword(password);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.setLeakDetectionThreshold(300000); // 5 minutes
        config.setMaximumPoolSize(5); // Small pool size for better concurrency
        config.setMinimumIdle(1); // Allow some idle connections
        config.setIdleTimeout(30000); // 30 seconds
        config.setMaxLifetime(1800000); // 30 minutes
        config.setConnectionTimeout(30000); // 30 seconds
        dataSource = new HikariDataSource(config);
    }

    @Override
    public void connect() throws DatabaseException {
        try {
            if (dataSource != null && !isConnected()) {
                dataSource.getConnection();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to the database", e);
        }
    }

    @Override
    public void disconnect() {
        if (dataSource != null) {
            dataSource.close();
        }
    }

    @Override
    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }

    @Override
    public void prepared(String query, PreparedStatementConsumer preparer) throws DatabaseException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.accept(stmt);
            stmt.executeUpdate();
        } catch (SQLIntegrityConstraintViolationException e) {
            // System.out.println("Duplicate entry or constraint violation: " + e.getMessage());
        } catch (SQLException e) {
            throw new DatabaseException("Error executing prepared statement", e);
        }
    }

    @Override
    public List<Map<String, Object>> query(String query, PreparedStatementConsumer preparer) throws DatabaseException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            preparer.accept(stmt);
            return executeQueryAndGetResults(stmt);
        } catch (SQLException e) {
            throw new DatabaseException("Error executing query", e);
        }
    }

    private List<Map<String, Object>> executeQueryAndGetResults(PreparedStatement stmt) throws SQLException {
        try (ResultSet rs = stmt.executeQuery()) {
            List<Map<String, Object>> results = new ArrayList<>();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
            return results;
        }
    }
}
