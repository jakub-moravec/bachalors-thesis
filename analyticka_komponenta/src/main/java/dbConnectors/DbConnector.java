package dbConnectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

/**
 * Abstraktní třída, je předkem všech databázových konektorů používaných v aplikaci.
 *
 * @author moravja8@fel.cvut.cz
 */
public abstract class DbConnector {
    private Logger log = LoggerFactory.getLogger(DbConnector.class);
    String dbServer = null;
    String currentDatabase = null;
    String driverName = null;
    private Statement statement = null;
    String dbUser = null;
    String dbPassword = null;

    /**
     * Zjistí a načte seznam všech databází, které jsou vytvořené na databázovém serveru.
     * @return seznam všech databází, které jsou vytvořené na databázovém serveru
     */
    public abstract ResultSet getDatabases();

    /**
     * Zjistí a načte seznam všech tabulek, které jsou vytvořené v aktuální databázi.
     * @return seznam všech tabulek, které jsou vytvořené v aktuální databázi
     */
    public abstract ResultSet getTables();

    /**
     * Vygeneruje transformační dotaz pro tabulku.
     * @param tableName plně kvalifikovaný název tabulky, pro kterou je dotaz generovaný
     * @return transformační dotaz
     */
    public abstract String getBasicSelectSQL(String tableName);

    /**
     * Nastaví aktuální databázi pomocí definovaného SQL (HiveQL) skriptu, který může být pro každý databázový
     * server jiný.
     * @param dbName nová aktuální databáze
     */
    protected abstract void setDb(String dbName);

    /**
     * Spustí SQL (HiveQL) dotaz na databázovém serveru.
     * @param sql dotaz
     * @param expectResult určuje, zda má funkce očekávat nějaký výsledek dotazu
     * @return výsledek dotazu, pokud je očekáván
     */
    ResultSet callQuery(String sql, boolean expectResult) {
        log.info("Running query '"+sql+"'");
        try {
            if(expectResult){
                ResultSet resultSet = statement.executeQuery(sql);
                log.info("Fetched result has " + resultSet.getMetaData().getColumnCount() + " columns and " );
                return resultSet;
            }else{
                try {
                    statement.executeQuery(sql);
                } catch (SQLException e) {
                    log.error("Called query that does not expects a result set.");
                }
                return null;
            }
        }catch (NullPointerException e){
            log.error("Connection to db is not working.", e);
            return  null;
        }catch (SQLException e) {
            log.error("SQL query failed: " + sql);
            return null;
        }
    }

    /**
     * Spustí SQL (HiveQL) skript na databázovém serveru.
     * @param sql skript
     */
    public void execute(String sql) {
        log.info("Executing sql command '"+sql+"'");
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("SQL query failed: " + sql, e);
        }
    }

    /**
     * @return aktuální databázi
     */
    public String getCurrentDatabase() {
        return currentDatabase;
    }

    /**
     * Nastaví aktuální databázi.
     * @param currentDatabase název nové aktuální databáze
     */
    public void setCurrentDatabase(String currentDatabase) {
        this.currentDatabase = currentDatabase;
        setDb(currentDatabase);
    }

    /**
     * Připojí se na databázový server definovaný implementací abstraktní třídy.
     */
    void connect() {
        String conn = dbServer + File.separator + getCurrentDatabase();
        try {
            Class.forName(driverName);
        }catch (NullPointerException e){
            log.error("DB driver is not set up properly", e);
        }catch (ClassNotFoundException e) {
            log.error("Could not find driver class " + driverName, e);
        }
        try {
            Connection connection = DriverManager.getConnection(conn, dbUser, dbPassword);
            statement = connection.createStatement();
        } catch (SQLException e) {
            log.error("Could not create connection to database server " + conn, e);
        }
    }
}
