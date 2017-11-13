package services;

import model.KnimeWorkflowNode;
import java.util.ArrayList;

/**
 * Rozhraní pro služby poskytující metody pro práci s KNIME workflow.
 * @author moravja8@fel.cvut.cz
 */
public interface KnimeWorkflowService {

    /**
     * Funkce vratí seznam názvů všech definovaných KNIME workflow.
     * @return seznam názvů všech definovaných KNIME workflow
     */
    ArrayList<KnimeWorkflowNode> getWorkflows();

    /**
     * Spustí zvolené workflow formou procesu pomocí KNIME batch application.
     * Funkce by měla zapsat metadata o průběhu workflow do zvolené databáze.
     * @param workflowNode workflow, které má být spuštěno
     * @param db databáze, kam budou zapsány metadata o průběhu workflow
     * @param table tabulka databáze, kam budou zapsány metadata o průběhu workflow
     * @return časové výsledky jednotlivých uzlů workflow
     */
    String runWorkflow(KnimeWorkflowNode workflowNode, String db, String table);

    /**
     * Načte nastavení všech uzlů vybraného workflow.
     * @param knimeWorkflowNode workflow, jehož uzly mají být načteny.
     */
    void loadNodes(KnimeWorkflowNode knimeWorkflowNode);
}
