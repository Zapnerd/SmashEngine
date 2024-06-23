package com.oresmash.smashengine.database;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseInterface {
    void connect() throws DatabaseException;
    void disconnect();
    boolean isConnected();
    void prepared(String query, PreparedStatementConsumer preparer) throws DatabaseException;
    List<Map<String, Object>> query(String query, PreparedStatementConsumer preparer) throws DatabaseException;

    static DatabaseInterface initializeFromConfig(JavaPlugin plugin) {
        FileConfiguration config = plugin.getConfig();
        String dbType = config.getString("database.type");

        if ("MySQL".equalsIgnoreCase(dbType)) {
            return MySQL.initialize(config);
        } else if ("SQLite".equalsIgnoreCase(dbType)) {
            return SQLite.initialize(config, plugin);
        } else {
            throw new IllegalArgumentException("Invalid database type specified in the configuration.");
        }
    }

    @FunctionalInterface
    interface PreparedStatementConsumer {
        void accept(PreparedStatement preparedStatement) throws SQLException;
    }
}
