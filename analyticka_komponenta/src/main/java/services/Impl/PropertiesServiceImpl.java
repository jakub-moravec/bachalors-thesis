package services.Impl;

import DAO.DaoFactory;
import services.PropertiesService;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Třída implementuje {@link PropertiesService}
 * @author moravja8@fel.cvut.cz
 */
public class PropertiesServiceImpl implements PropertiesService {

    public ArrayList<String> getPropertiesList() {
        ArrayList<String> propertyList = new ArrayList<String>();
        for(Object key : DaoFactory.getPropertiesDao().getAll().keySet()){
            propertyList.add(key.toString());
        }

        return propertyList;
    }
}
