package com.oresmash.smashengine.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.Map;

public abstract class DatabaseHandler implements DatabaseInterface {
    private final DatabaseInterface databaseInterface;
    private final String dbType;

    public DatabaseHandler(JavaPlugin plugin) {
        this.databaseInterface = DatabaseInterface.initializeFromConfig(plugin);
        this.dbType = plugin.getConfig().getString("database.type");
    }

    @Override
    public void connect() throws DatabaseException {
        databaseInterface.connect();
    }

    @Override
    public void disconnect() {
        databaseInterface.disconnect();
    }

    @Override
    public boolean isConnected() {
        return databaseInterface.isConnected();
    }

    @Override
    public void prepared(String query, PreparedStatementConsumer preparer) throws DatabaseException {
        databaseInterface.prepared(query, preparer);
    }

    @Override
    public List<Map<String, Object>> query(String query, PreparedStatementConsumer preparer) throws DatabaseException {
        return databaseInterface.query(query, preparer);
    }

    protected boolean isMySQL() {
        return "MySQL".equalsIgnoreCase(dbType);
    }
}
