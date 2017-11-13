package model;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Třída implementující model takového uzlu KNIME workflow, který představuje celé workflow (metanode). Alternativně
 * může třída představovat implementaci modelu samotného KNIME workflow, které je strukturou téměř shodné se
 * strukturou jednotlivých uzlů.
 * @author moravja8@fel.cvut.cz
 */
public class KnimeWorkflowNode extends KnimeNode{

    private static Logger log = LoggerFactory.getLogger(KnimeWorkflowNode.class);

    private ArrayList<KnimeNode> nodes = null;

    private KnimeSqlExecutorNode sqlExecutor = null;

    private KnimeHiveConnectorNode hiveConnector = null;

    private ArrayList<KnimeWriterNode> writers = null;

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @throws IOException v případě, že není nalezeno nastavení uzlu
     */
    public KnimeWorkflowNode(File root) throws IOException {
        super(root, "workflow.knime");
    }

    /**
     * @return seznam všech uzlů, které workflow/metanode zahrnuje
     */
    public ArrayList<KnimeNode> getNodes() {
        if(nodes == null){
            ServiceFactory.getKnimeWorkflowService().loadNodes(this);
        }
        return nodes;
    }

    /**
     * @return uzel třídy {@link KnimeSqlExecutorNode}, který definuje SQL (HiveQL) příkaz, který bude spuštěn
     * databázovým serverem. Hledaný uzel musí mít název 'sql_executor'.
     * @throws NullPointerException, pokud takový uzel není nalezen.
     */
    public KnimeSqlExecutorNode getSqlExecutor() throws NullPointerException{
        if (sqlExecutor == null) {
            ServiceFactory.getKnimeWorkflowService().loadNodes(this);
        }
        return sqlExecutor;
    }

    /**
     * @return název workflow / metanodu
     */
    public String getWorkflowName(){
        return super.getNodeRoot().getName();
    }

    /**
     * Uloží všechny uzly workflow, jejichž nastavení se změnilo od posledního uložení - včetně sebe sama.
     */
    @Override
    public void save(){
        //smazání starých záloh
        log.info("Deleting old backups in " + getBackupFolderName());
        try {
            FileUtils.deleteDirectory(new File(this.getBackupFolderName()));
        } catch (IOException e) {
            log.error("Folder " + getBackupFolderName() + " could not be deleted", e);
        }

        //uložení uzlů
        for (KnimeNode node: nodes) {
            if(node.isChanged()){
                node.save();
            }
        }
        //uložení sebe sama
        if(super.isChanged()){
            super.save();
        }
    }

    /**
     * Obnoví zálohu nastavení všech uzlů, které jsou zálohovány - včetně sebe sama.
     */
    @Override
    public void restore(){
        //obnovení uzlů
        for (KnimeNode node: nodes) {
            node.restore();
        }

        //obnovení sebe sama
        super.restore();
    }

    /**
     * Přenačte nastavení všech uzlů - včetně sebe sama.
     */
    @Override
    public void reload(){
        //znovunačtení uzlů
        for (KnimeNode node: nodes) {
            node.reload();
        }

        //obnovení sebe sama
        super.reload();
    }

    /**
     * @return složka, kde bude uložena záloha uzlu
     */
    @Override
    public String getBackupFolderName() {
        String workflowBackupFolder = this.getNodeRoot().getAbsolutePath() + File.separator + "backup";
        return workflowBackupFolder + File.separator + this.getNodeRoot().getName();
    }

    @Override
    public String toString() {
        return getWorkflowName();
    }

    public KnimeHiveConnectorNode getHiveConnector() {
        return hiveConnector;
    }

    public ArrayList<KnimeWriterNode> getWriters() {
        return writers;
    }

    public void setNodes(ArrayList<KnimeNode> nodes) {
        this.nodes = nodes;
    }

    public void setSqlExecutor(KnimeSqlExecutorNode sqlExecutor) {
        this.sqlExecutor = sqlExecutor;
    }

    public void setHiveConnector(KnimeHiveConnectorNode hiveConnector) {
        this.hiveConnector = hiveConnector;
    }

    public void setWriters(ArrayList<KnimeWriterNode> writers) {
        this.writers = writers;
    }
}
