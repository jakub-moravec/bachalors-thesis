package services.Impl;

import DAO.DaoFactory;
import utils.FileSystemUtils;
import model.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import services.KnimeNodeService;

import java.io.IOException;


/**
 * Třída implementuje {@link KnimeNodeService}
 * @author moravja8@fel.cvut.cz
 */
public class KnimeNodeServiceImpl implements KnimeNodeService {
    private static Logger log = LoggerFactory.getLogger(KnimeNodeServiceImpl.class);

    public KnimeNodeServiceImpl() {}

    public String getParameterValue(Document doc, String key){
        Node entryNode = DaoFactory.getKnimeNodeDao().readParameter(doc, "//entry[@key='"+key+"']");
        return entryNode.getAttributes().getNamedItem("value").getNodeValue();
    }

    public void setParameterValue(Document doc, String key, String value){
        Node output = DaoFactory.getKnimeNodeDao().readParameter(doc, "//entry[@key='"+key+"']");
        output.getAttributes().getNamedItem("value").setNodeValue(value);
    }

    public void restore(KnimeNode node) {
        if (node.getBackupFile().exists()){
            try {
                FileSystemUtils.moveFileSilently(node.getBackupFile(), node.getNodeSettings());
                log.info("Node " + node.getNodeRoot().getName() + " was restored.");
            } catch (IOException e) {
                log.error("Node " + node.getNodeRoot().getName() + " could not be restored.", e);
            }
        }else{
            log.debug("There is no backup for node " + node.toString());
        }
    }

    public void backup(KnimeNode node){ //File nodeSettings) {
        //create backup folder if not exists
        FileSystemUtils.createFolder(node.getBackupFolderName());

        //if there is an older backup file, delete it
        if(node.getBackupFile().exists()){
            node.getBackupFile().delete();
        }

        //do the backup, if fails return null
        try {
            FileSystemUtils.moveFileSilently(node.getNodeSettings(), node.getBackupFile());
            log.info("Node " + node.getNodeRoot().getName() + " was backuped.");
        } catch (IOException e) {
            log.error("Node " + node.getNodeRoot().getName() + " could not be backuped.", e);
        }
    }

}
