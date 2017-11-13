package model;

import DAO.DaoFactory;
import services.ServiceFactory;

import java.io.File;
import java.io.IOException;

/**
 * Třída implementující model takového uzlu KNIME workflow, který je schopen se připojit na Hive databázi.
 * @author moravja8@fel.cvut.cz
 */
public class KnimeHiveConnectorNode extends KnimeNode {

    private String database = null;
    private String password = null;
    private String driver = null;
    private String user = null;

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru. Funkce také načítá nastavení
     * Hive serveru definoné v konfiguračním souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @throws IOException
     */
    public KnimeHiveConnectorNode(File root) throws IOException {
        super(root);
        loadSettings();
    }

    private void loadSettings(){
        this.database = getDatabase();
        this.driver = getDriver();
        this.user = getUser();
        this.password = getPassword();
    }


    /**
     * Funkce obnovuje nastavení Hive serveru na základě změn, provedených v konfiguračním souboru.
     */
    public void refreshSettings() {
        loadSettings();

        String appDriver = DaoFactory.getPropertiesDao().get("HiveDriver");
        String appDefaultDatabase = DaoFactory.getPropertiesDao().get("HiveDefaultDatabase");
        String appUser = DaoFactory.getPropertiesDao().get("HiveDriver");
        String appPassword = DaoFactory.getPropertiesDao().get("HivePassword");
        String appDatabase = DaoFactory.getPropertiesDao().get("HiveServer") + "/" + appDefaultDatabase;

        if(!driver.equals(appDriver)){
            setDriver(appDriver);
            setChanged(true);
        }

        if(!database.equals(appDatabase)){
            setDatabase(appDatabase);
            setChanged(true);
        }

        if(!user.equals(appUser)){
            setUser(appUser);
            setChanged(true);
        }

        if(!password.equals(appPassword)){
            setPassword(appPassword);
            setChanged(true);
        }
    }


    private void setDatabase(String database) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "database", database);
    }

    private void setUser(String user) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "user", user);
    }

    private void setPassword(String password) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "password", password);
    }

    private void setDriver(String driver) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "driver", driver);
    }

    private String getDatabase() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "database");
    }

    private String getPassword() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "password");
    }

    private String getDriver() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "driver");
    }

    private String getUser() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "user");
    }
}
