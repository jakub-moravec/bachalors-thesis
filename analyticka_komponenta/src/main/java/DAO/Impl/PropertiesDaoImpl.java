package DAO.Impl;

import DAO.PropertiesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * Implementuje {@link PropertiesDao}.
 *
 * @author moravja8@fel.cvut.cz
 */
public class PropertiesDaoImpl implements PropertiesDao{
    private static Logger log = LoggerFactory.getLogger(PropertiesDaoImpl.class);
    private Properties properties = null;
    private String propertiesFile = File.separator + "config.properties";



    public String get(String propertyName){
        if (properties == null){
            read();
        }
        try {
            return properties.getProperty(propertyName);
        } catch (Exception e) {
            log.error("Applicaton properties are not set up correctly.", e);
            return null;
        }
    }

    public void set(String key, String value) {
        if(key == null || value == null){
            return;
        }

        if (properties == null){
            read();
        }

        properties.setProperty(key, value);

        save();
    }

    public Properties getAll() {
        return properties;
    }

    public void read(){

        InputStream input = null;
        properties = new Properties();

        try {
            input = getClass().getResourceAsStream(propertiesFile);
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    log.error("Cannot not close properties file.", e);
                }
            }
        }
    }

    public void save(){

        OutputStream output = null;

        try {
            output = new FileOutputStream(getClass().getResource(propertiesFile).getPath());
            properties.store(output, null);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    log.error("Cannot not close properties file.", e);
                }
            }
        }

        read();
    }

}
