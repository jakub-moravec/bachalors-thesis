package model;

import services.ServiceFactory;

import java.io.File;
import java.io.IOException;

/**
 * Třída implementující model takového uzlu KNIME workflow, který je vykonávat SQL (HiveQL) příkazy v připojené
 * databázi.
 * @author moravja8@fel.cvut.cz
 */
public class KnimeSqlExecutorNode extends KnimeNode{

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @throws IOException v případě, že není nalezeno nastavení uzlu
     */
    public KnimeSqlExecutorNode(File root) throws IOException {
        super(root);
    }

    /**
     * Vrací definovaný SQL (HiveQL) kód, který je aktuálně nastavený v konfiguraci uzlu.
     * @return aktuálně nastavený SQL (HiveQL) kód
     */
    public String getSQLCode() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "statement");
    }

    /**
     * Nastavuje SQL/HiveQL kód, který má být spuštěn databázovým serverem a který je uložený v konfiguraci uzlu.
     * @param code nový SQL (HiveQL) kód
     */
    public void setSQLCode(String code) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "statement", code);
        setChanged(true);
    }
}
