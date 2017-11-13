package DAO;

import model.KnimeNode;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;

/**
 * DAO vrstva pro práci s uzly KNIME workflow.
 *
 * @author moravja8@fel.cvut.cz
 */
public interface KnimeNodeDao {
    /**
     * Vytvoří DOM dokument z XML nastavení uzlu.
     * @param xmlFile xml nastavení uzlu
     * @return vytvořený DOM dokument
     */
    Document createDOM(File xmlFile);

    /**
     * Načte a vrátí hodnotu specifikovaného parametru nastavení uzlu.
     * @param doc DOM dokument definující nastavení uzlu
     * @param xpathString xPath, pomocí které je hledán parametr
     * @return hodnota parametru
     */
    Node readParameter(Document doc, String xpathString);

    /**
     * Uloží změny nastavení uzlu KNIME workflow, pokud k nějakým došlo.
     * @param node uzel, jehož nastavení má být uloženo.
     */
    void update(KnimeNode node);

    /**
     * Načte a vrátí všechny parametry nastavení uzlu KNIME workflow.
     * @param doc DOM dokument definující nastavení uzlu
     * @return seznam všech parametrů uzlu a jejich hodnot
     */
    HashMap<String, String> readParameters(Document doc);
}
