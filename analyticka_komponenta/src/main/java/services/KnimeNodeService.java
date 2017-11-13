package services;

import model.KnimeNode;
import org.w3c.dom.Document;


/**
 * Rozhraní pro služby poskytující metody pro práci s KNIME uzly.
 * @author moravja8@fel.cvut.cz
 */
public interface KnimeNodeService {
    /**
     * Načte hodnotu definovaného parametru uzlu.
     * @param doc parsovaný DOM dokument, který je funkcí upraven
     * @param key identifikátor parametru, který má být načten
     * @return hodnota definovaného parametru
     */
    String getParameterValue(Document doc, String key);

    /**
     * Nastaví hodnotu definovaného parametru uzlu.
     * @param doc parsovaný DOM dokument, který je funkcí upraven
     * @param key identifikátor parametru, který má být načten
     * @param value hodnota, která bude nastavena
     */
    void setParameterValue(Document doc, String key, String value);

    /**
     * Funkce obnoví zálohu nastavení KNIME uzlu.
     * @param node uzel, který má být obnoven
     */
    void restore(KnimeNode node);

    /**
     * Funkce vytvoří zálohu nastavení KNIME uzlu.
     * @param node uzel, který má být zálohován
     */
    void backup(KnimeNode node);
}
