package dbConnectors;

import DAO.DaoFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;

/**
 * Implementuje {@link DbConnector}.
 *
 * @author moravja8@fel.cvut.cz
 */
public class H2Connector extends DbConnector{
    private static H2Connector instance;

    private H2Connector() {
        super.driverName = "org.h2.Driver";
        super.currentDatabase = "bp;AUTO_SERVER=TRUE";
        super.dbUser = "sa";
        super.dbPassword = "";
        super.dbServer = "jdbc:h2:" + DaoFactory.getPropertiesDao().get("H2DestinationFolder");

        connect();
        init();
    }

    public static DbConnector getInstance() {
        if(instance == null){
            instance = new H2Connector();
        }

        return instance;
    }

    public ResultSet getDatabases() {
        throw new NotImplementedException();
    }

    public ResultSet getTables() {
        throw new NotImplementedException();
    }

    public String getBasicSelectSQL(String tableName) {
        return "select * from " + tableName + ";";
    }

    protected void setDb(String dbName) {
        throw new NotImplementedException();
    }

    /**
     * Vytvoří tabulku potřebnou pro ukládání metadat o spuštěných analýzách. 
     */
    private void init( ){
        String sql = "create table if not exists costs(\n" +
                        "executedAt varchar2(22),\n" +
                        "database_name varchar2(50),\n" +
                        "table_name varchar2(50),\n" +
                        "workflow_name varchar2(100),\n" +
                        "node_name varchar2(50),\n" +
                        "node_nbr integer,\n" +
                        "duration integer)";

        execute(sql);
    }
}
