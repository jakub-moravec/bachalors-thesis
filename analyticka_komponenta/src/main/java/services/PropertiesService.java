package services;

import java.util.ArrayList;

/**
 * Rozhraní pro služby poskytující metody pro práci s properties souborem.
 * @author moravja8@fel.cvut.cz
 */
public interface PropertiesService {

    /**
     * Metoda vrací seznam všech parametrů definovaných v properties soubory
     * @return seznam všech parametrů
     */
    ArrayList<String> getPropertiesList();


}
