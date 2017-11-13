package DAO;

import java.util.Properties;

/**
 * DAO vrstva pro práci s konfiguračním .properties souborem.
 *
 * @author moravja8@fel.cvut.cz
 */
public interface PropertiesDao {
    /**
     * Načte a vrátí hodnotu parametru s daným názvem.
     * @param propertyName název parametru
     * @return hodnota parametru
     */
    String get(String propertyName);

    /**
     * Nastaví hodnotu parametru s daným názvem.
     * @param propertyName název parametru
     * @param propertyValue nová hodnota parametru
     */
    void set(String propertyName, String propertyValue);

    /**
     * Vrátí všechny parametry aplikace.
     * @return parametry aplikace
     */
    Properties getAll();

    /**
     * Načte všechny definované parametry aplikace.
     */
    void read();

    /**
     * Uloží všechny parametry aplikace.s
     */
    void save();
}
