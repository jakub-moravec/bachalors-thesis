package utils;

import DAO.DaoFactory;
import model.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;

/**
 * Nastavení log4j loggeru aplikace KNIME musí být upraveno pro účely této aplikace. Při vyhodnocování rychlosti uzlů
 * jsou používány informace, které KNIME poskytuje, tyto informace jsou přístupné bohužel pomocí logů aplikace.
 * Toho může být docíleno přidáním uzlu 'batchexec' do nastavení.
 * Cesta ke konfiguračnímuj souboru log4j je definována staticky, tento přístup ale není bezdůvodný.
 * Je předpoklad, že pokud bude soubor přesunut nebo přejmenová, bude to v důsledku vydání nové verze KNIME (případně
 * log4j) a v tom případě musí být zkontrolováno, zda se nezměnila také struktura souboru.
 *
 * @author moravja8@fel.cvut.cz
 */
public class KnimeLogSetupUtil {
    private static File knimeLogSettingsFile = null;
    private static Logger log = LoggerFactory.getLogger(KnimeLogSetupUtil.class);
    private static Document parsedSettings = null;

    /**
     * Metoda vyhledá a načte konfigurační soubor log4j. Pokud soubor není nalezen, vrátí NullPointerException.
     * @throws NullPointerException v případě, že hledaný konfigurační soubor neexistuje.
     */
    private static void setUp() throws NullPointerException{
        StringBuilder logFilePath = new StringBuilder();
        String knimeWorkingFolder = DaoFactory.getPropertiesDao().get("knimeWorkingFolder");
        logFilePath.append(knimeWorkingFolder);
        logFilePath.append(File.separator);
        logFilePath.append(".metadata");
        logFilePath.append(File.separator);
        logFilePath.append("knime");
        logFilePath.append(File.separator);
        logFilePath.append("log4j3.xml");

        knimeLogSettingsFile = new File(logFilePath.toString());

        if(!knimeLogSettingsFile.isFile()){
            throw new NullPointerException();
        }
    }

    /**
     * Metoda přidá uzel appender-ref[@ref='batchexec'] no kořene konfiguračního souboru,
     * vytvoří zálohu původního nastavení a uloží nové nastavení.
     */
    public static void addBatchExecutorLogger() throws IOException {
        try {
            setUp();
        } catch (NullPointerException e) {
            log.error("Log4j settings file for Knime was not found. Cost estimation will not work.", e);
            return;
        }

        parsedSettings = DaoFactory.getKnimeNodeDao().createDOM(knimeLogSettingsFile);

        Node root = DaoFactory.getKnimeNodeDao().readParameter(parsedSettings, "//root");

        Node batchExecutorLogger = DaoFactory.getKnimeNodeDao().readParameter(parsedSettings, "//root/appender-ref[@ref='batchexec']");

        if(batchExecutorLogger == null) { // batchExecutorLogger is not set yet to root
            batchExecutorLogger = parsedSettings.createElement("appender-ref");
            Attr attr = parsedSettings.createAttribute("ref");
            attr.setValue("batchexec");
            batchExecutorLogger.getAttributes().setNamedItem(attr);
            root.appendChild(batchExecutorLogger);

            DaoFactory.getKnimeNodeDao().update(new KnimeNode(knimeLogSettingsFile.getParentFile(), "log4j3.xml", parsedSettings));
        }
    }

}
