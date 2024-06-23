package com.oresmash.smashengine.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLite implements DatabaseInterface {
    private final Connection connection;

    public static DatabaseInterface initialize(FileConfiguration config, JavaPlugin plugin) {
        String file = config.getString("database.SQLite.file");
        String url = "jdbc:sqlite:" + plugin.getDataFolder() + "/" + file;
        try {
            return new SQLite(url);
        } catch (SQLException e) {
            throw new DatabaseException("Unable to initialize SQLite database", e);
        }
    }

    private SQLite(String url) throws SQLException {
        this.connection = DriverManager.getConnection(url);
    }

    @Override
    public void connect() throws DatabaseException {
        try {
            if (connection.isClosed()) {
                throw new SQLException("Connection is closed");
            }
        } catch (SQLException e) {
            throw new DatabaseException("Unable to connect to the database", e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (!connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            throw new DatabaseException("Unable to disconnect from the database", e);
        }
    }

    @Override
    public boolean isConnected() {
        try {
            return !connection.isClosed();
        } catch (SQLException e) {
            throw new DatabaseException("Error checking connection status", e);
        }
    }

    @Override
    public void prepared(String query, PreparedStatementConsumer preparer) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            preparer.accept(stmt);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Error executing prepared statement", e);
        }
    }

    @Override
    public List<Map<String, Object>> query(String query, PreparedStatementConsumer preparer) throws DatabaseException {
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
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
