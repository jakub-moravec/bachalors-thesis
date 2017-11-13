import GUI.MainFrame;
import utils.KnimeLogSetupUtil;
import dbConnectors.H2Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Main class aplikace, spouští kroky nutné před používáním aplikace a grafické prostředí aplikace.
 * @author moravja8@fel.cvut.cz
 */
class Main {
    private static Logger log = LoggerFactory.getLogger(Main.class);

    /**
     * Jediná spustitelná metoda aplikace.
     * @param args
     */
    public static void main(String[] args) {
        /**
         * načtení H2 databáze
         */
        H2Connector.getInstance();

        try {
            /**
             * úprava nastavení Log4J aplikace KNIME
             */
            KnimeLogSetupUtil.addBatchExecutorLogger();
        } catch (IOException e) {
            log.error("Could not load log settings of Knime", e);
        }

        /**
         * spuštění GUI
         */
        MainFrame mf = new MainFrame();
        mf.init();
    }
}




