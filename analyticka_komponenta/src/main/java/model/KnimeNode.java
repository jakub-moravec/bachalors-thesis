package model;

import DAO.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import services.ServiceFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.HashMap;

/**
 * Třída implementující model uzlu KNIME workflow.
 * @author moravja8@fel.cvut.cz
 */
public class KnimeNode {
    private static Logger log = LoggerFactory.getLogger(KnimeNode.class);

    /**
     * Adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     */
    private File nodeRoot;

    /**
     * Načtené nastavení uzlu
     */
    private File nodeSettings = null;

    /**
     * Naparsované nastavení uzlu
     */
    private Document xmlSettings = null;

    /**
     * Uživatelem definovaný název uzlu
     */
    private String label = null;

    /**
     * Všechny parametry uzlu
     */
    private HashMap<String, String> parameters = null;

    /**
     * Název XML konfiguračního souboru
     */
    private String nodeSettingsFileName = "settings.xml";

    /** Příznak udává, jestli se nastavení nodu změnilo od posledního uložení */
    private boolean changed = false;

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @throws IOException v případě, že není nalezeno nastavení uzlu
     */
    public KnimeNode(File root) throws IOException {
        nodeRoot = root;
        loadSettings();
    }

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @param settings název souboru s XML nastavením uzlu
     * @throws IOException v případě, že není nalezeno nastavení uzlu
     */
    public KnimeNode(File root, String settings) throws IOException{
        nodeRoot = root;
        this.nodeSettingsFileName = settings;
        loadSettings();
    }

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @param settings název souboru s XML nastavením uzlu
     * @param parsedSettings v případě, že není nalezeno nastavení uzlu
     * @throws IOException v případě, že není nalezeno nastavení uzlu
     */
    public KnimeNode(File root, String settings, Document parsedSettings) throws IOException {
        this(root, settings);
        this.xmlSettings = parsedSettings;
    }

    private void loadSettings() throws IOException {
        File[] settingFiles = nodeRoot.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().equals(nodeSettingsFileName);
            }
        });

        if(settingFiles.length != 1){
            throw new IOException("Settings file not found.");// fail fast
        }else{
            nodeSettings = settingFiles[0];
        }
    }

    /**
     * Funkce naparsuje obsah XML souboru do DOM dokumentu.
     * @return naparsovaný DOM dokument
     */
    public Document getXmlSettings() {
        if(xmlSettings == null){
            xmlSettings = DaoFactory.getKnimeNodeDao().createDOM(nodeSettings);
            setChanged(false);
        }
        return xmlSettings;
    }

    /**
     * Funkce uloží změněné nastavení uzlu.
     */
    public void save(){
        if(xmlSettings != null && changed){
            DaoFactory.getKnimeNodeDao().update(this);
            setChanged(false);
        }
    }

    /**
     * Funkce obnoví dříve zálohovanou verzi nastavení uzlu.
     */
    public void restore(){
        ServiceFactory.getKnimeNodeService().restore(this);
        setChanged(false);
    }

    /**
     * Funkce načte aktuální XML soubor s nastavením bez ohledu na to, zda je či není v souladu s nastavením
     * aktuálně načteným.
     */
    public void reload(){
        this.xmlSettings = null;
        this.nodeSettings = null;
        this.parameters = null;

        try {
            loadSettings();
        } catch (IOException e) {
            log.error("Node " + this + " could not be reloaded.", e);
        }
    }

    /**
     * Funkce získává pomocí XPath název uzlu.
     * @return uživatelem definovaný název uzlu
     */
    public String getLabel(){
        if(label == null){
            Node labelNode = DaoFactory.getKnimeNodeDao().readParameter(this.getXmlSettings(), "//entry[@key='text']");
            try {
                label = labelNode.getAttributes().getNamedItem("value").getNodeValue();
            } catch (NullPointerException e) {
                log.info("Node "+ nodeRoot.getName() + " does not have a label.");
            }
        }
        return label;
    }

    /**
     * Funkce určuje, ve které složce bude uložena záloha uzlu.
     * @return složka, kde bude uložena záloha uzlu
     */
    public String getBackupFolderName(){
        String workflowBackupFolder = this.getNodeRoot().getParentFile().getAbsolutePath() + File.separator + "backup";
        return workflowBackupFolder + File.separator + this.getNodeRoot().getName();
    }

    /**
     * Funkce vrací soubor se zálohou nastavení uzlu.
     * @return soubor se zálohou uzlu
     */
    public File getBackupFile() {
        return new File (getBackupFolderName() + File.separator + this.getNodeSettings().getName());
    }

    /**
     * Nastavuje hodnotu proměnné, která určuje zda byl uzel změněn od posledního uložení.
     * @param changed true pokud byl uzel změněn od posledního uložení
     */
    void setChanged(boolean changed) {
        this.changed = changed;
    }

    /**
     * @return true, pokud byl uzel změněn od posledního uložení
     */
    boolean isChanged() {
        return changed;
    }

    /**
     * @return adresář, kterým je uzel definován
     */
    public File getNodeRoot() {
        return nodeRoot;
    }

    /**
     * @return soubor s nastavením uzlu
     */
    public File getNodeSettings() {
        return nodeSettings;
    }

    /**
     * @return všechny parametry uzlu a jejich hodnoty
     */
    public HashMap<String, String> getParameters() {
        if (parameters == null){
            parameters = DaoFactory.getKnimeNodeDao().readParameters(this.getXmlSettings());
        }
        return parameters;
    }

    /**
     * Funkce mění hodnotu některého z parametrů uzlu.
     * @param key identifikátor parametru, který má být změněn.
     * @param value nová hodnota parametru.
     */
    public void updateParametres(String key, String value){
        this.getParameters().remove(key);
        this.getParameters().put(key, value);

        ServiceFactory.getKnimeNodeService().setParameterValue(this.getXmlSettings(), key, value);
        setChanged(true);
    }

    @Override
    public String toString() {
        return nodeRoot.getName();
    }
}
