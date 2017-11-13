package DAO;

import DAO.Impl.KnimeNodeDaoImpl;
import DAO.Impl.PropertiesDaoImpl;

/**
 * Třída implementuje návrhový vzor Factory, poskytuje implementace DAO vrstvy.
 * Třída není thread-safe.
 * @author moravja8@fel.cvut.cz
 */
public class DaoFactory {
    private static PropertiesDao propertiesDao = null;
    private static KnimeNodeDao knimeNodeDao = null;

    /**
     * @return instanci implementace {@link PropertiesDao}
     */
    public static PropertiesDao getPropertiesDao() {
        if(propertiesDao == null){
            propertiesDao = new PropertiesDaoImpl();
        }
        return propertiesDao;
    }

    /**
     * @return instanci impolementace {@link KnimeNodeDao}
     */
    public static KnimeNodeDao getKnimeNodeDao() {
        if(knimeNodeDao == null){
            knimeNodeDao = new KnimeNodeDaoImpl();
        }
        return knimeNodeDao;
    }
}
