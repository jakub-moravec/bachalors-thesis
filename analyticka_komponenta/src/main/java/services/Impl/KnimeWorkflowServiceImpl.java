package services.Impl;

import DAO.DaoFactory;
import utils.ShellUtil;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.KnimeCostsMapperService;
import services.KnimeWorkflowService;
import services.ServiceFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Třída implementuje {@link KnimeWorkflowService}
 * @author moravja8@fel.cvut.cz
 */
public class KnimeWorkflowServiceImpl implements KnimeWorkflowService {
    private static Logger log = LoggerFactory.getLogger(KnimeWorkflowServiceImpl.class);

    private File knimeWorkingFolder = null;
    private ArrayList<KnimeWorkflowNode> workFlows = null;

    public KnimeWorkflowServiceImpl(){
        try {
            knimeWorkingFolder = new File(DaoFactory.getPropertiesDao().get("knimeWorkingFolder"));
        } catch (NullPointerException e) {
            log.error("Application properties are not set correctly.", e);
        }
    }

    private File[] findWorkFlows(){
        if(knimeWorkingFolder != null && knimeWorkingFolder.isDirectory()){
            return knimeWorkingFolder.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden();
                }
            });
        }

        return new File[0];
    }

    private ArrayList<KnimeWorkflowNode> loadWorkFlows(){
        workFlows = new ArrayList<KnimeWorkflowNode>();
        File[] roots = findWorkFlows();
        for (File root: roots) {
            try {
                workFlows.add(new KnimeWorkflowNode(root));
            } catch (IOException e) {
                log.error("Workflow "+ root.getName() + " could not be loaded.", e);
            }
        }

        return workFlows;
    }

    public ArrayList<KnimeWorkflowNode> getWorkflows() {
        if(workFlows != null){
            return workFlows;
        }
        return loadWorkFlows();
    }

    public String runWorkflow(KnimeWorkflowNode workflowNode, String db, String table) throws NullPointerException{
        String[] commands = new String[7];
        commands[0] = DaoFactory.getPropertiesDao().get("knimeHome") + File.separator + "knime";
        commands[1] = "-consoleLog";
        commands[2] = "-nosplash";
        commands[3] = "-application";
        commands[4] = "org.knime.product.KNIME_BATCH_APPLICATION";
        commands[5] = "-workflowDir="+ workflowNode.getNodeRoot().getPath();
        commands[6] = "-reset";

        StringBuilder comSb = new StringBuilder();
        for (String command: commands) {
            comSb.append(command);
            comSb.append(" ");
        }
        ArrayList<String> inputLog = ShellUtil.executeCommand(comSb.toString());

        KnimeCostsMapperService costsMapper = ServiceFactory.getKnimeCostsMapperService();
        return costsMapper.mapCostsFromLog(inputLog, db, table, workflowNode.getWorkflowName());
    }

    private File[] findNodes(File root){
        if(root != null && root.isDirectory()){
            return root.listFiles(new FileFilter() {
                public boolean accept(File pathname) {
                    return pathname.isDirectory() && !pathname.isHidden() && !pathname.getName().equals("backup");
                }
            });
        }
        return new File[0];
    }

    public void loadNodes(KnimeWorkflowNode knimeWorkflowNode){
        knimeWorkflowNode.setNodes(new ArrayList<KnimeNode>());
        knimeWorkflowNode.setWriters(new ArrayList<KnimeWriterNode>());

        File[] roots = findNodes(knimeWorkflowNode.getNodeRoot());
        for (File root: roots) {
            try {
                KnimeNode node = new KnimeNode(root);

                if ("hive_connector".equals(node.getLabel())) {
                    KnimeHiveConnectorNode hiveConnector = new KnimeHiveConnectorNode(root);
                    node = hiveConnector;
                    knimeWorkflowNode.setHiveConnector(hiveConnector);
                }else if("sql_executor".equals(node.getLabel())) {
                    KnimeSqlExecutorNode sqlExecutor = new KnimeSqlExecutorNode(root);
                    node = sqlExecutor;
                    knimeWorkflowNode.setSqlExecutor(sqlExecutor);
                }else if(node.getLabel() != null && node.getLabel().contains("export_")) {
                    KnimeWriterNode writerNode = new KnimeWriterNode(root);
                    node = writerNode;
                    knimeWorkflowNode.getWriters().add(writerNode);
                }
                knimeWorkflowNode.getNodes().add(node);
            } catch (IOException e) {
                log.error("Node " + root.getName() + " could not be loaded - settings not found.", e);
            }
        }
    }
}
