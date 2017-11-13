package dbConnectors;

import DAO.DaoFactory;
import java.sql.ResultSet;
import java.text.MessageFormat;

/**
 * Implementuje {@link DbConnector}.
 *
 * @author moravja8@fel.cvut.cz
 */
public class HiveConnector extends DbConnector{

    private static DbConnector instance;

    private HiveConnector() {
        super.driverName = DaoFactory.getPropertiesDao().get("HiveDriver");
        super.currentDatabase = DaoFactory.getPropertiesDao().get("HiveDefaultDatabase");
        super.dbUser = DaoFactory.getPropertiesDao().get("HiveUser");
        super.dbPassword = DaoFactory.getPropertiesDao().get("HivePassword");
        super.dbServer = DaoFactory.getPropertiesDao().get("HiveServer");

        connect();
    }

    public static DbConnector getInstance() {
        if(instance == null){
            instance = new HiveConnector();
        }

        return instance;
    }

    public ResultSet getDatabases() {
        return callQuery("show databases", true);
    }

    public ResultSet getTables(){
        return callQuery("show tables in " + super.getCurrentDatabase(), true);
    }

    /**
     * Generuje transformační dotaz pro sémanticky uložená data.
     * @param tableName název tabulky, pro kterou je vytvořen transformační dotaz
     * @return transformační dotazs
     */
    public String getBasicSelectSQL(String tableName) {

        String ontologyTableSelect =
                "SELECT " +
                    "sensors.subject as sensor, " +
                    "regexp_replace(substr(s2.object, 2, 19), \'T\', \' \') as creationDate, " +
                    "cast(regexp_extract(regexp_replace(s4.object, \'\\\"\', \'\'), \'^([0-9]+\\.[0-9]{1,3})\', 0) as decimal(18,6)) as value, " +
                        "s5.object as unit\n" +
                "FROM " + super.getCurrentDatabase() + "." +tableName + " sensors\n" +
                "JOIN " + super.getCurrentDatabase() + "." +tableName + " s1 ON (" +
                        "s1.object = sensors.subject AND " +
                        "s1.predicate = '<http://purl.oclc.org/NET/ssnx/ssn#isProducedBy>'" +
                        ")\n" +
                "JOIN " + super.getCurrentDatabase() + "." +tableName + " s2 ON (" +
                        "s2.subject = s1.subject AND " +
                        "s2.predicate = '<http://www.rockwellautomation.com/radic/bigdata/ssnx/shs#hasDateTime>')\n" +
                "JOIN " + super.getCurrentDatabase() + "." +tableName + " s3 ON (" +
                        "s3.subject = s2.subject AND " +
                        "s3.predicate = '<http://purl.oclc.org/NET/ssnx/ssn#hasValue>')\n" +
                "JOIN " + super.getCurrentDatabase() + "." +tableName + " s4 ON (" +
                        "s4.subject = s3.object AND " +
                        "s4.predicate = '<http://www.rockwellautomation.com/radic/bigdata/ssnx/shs#hasQuantityValue>')\n" +
                "JOIN " + super.getCurrentDatabase() + "." +tableName + " s5 ON (" +
                        "s5.subject = s3.object AND " +
                        "s5.predicate = '<http://www.rockwellautomation.com/radic/bigdata/ssnx/shs#hasQuantityUnitOfMeasurement>')\n";

        return ontologyTableSelect;
    }

    protected void setDb(String dbName) {
        callQuery("use "+dbName, false);
    }
}
