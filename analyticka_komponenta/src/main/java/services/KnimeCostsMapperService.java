package services;

import java.util.ArrayList;

/**
 * Rozhraní pro služby poskytující metody pro vyhodnocení časové náročnosti průběhu jednotlivých částí KNIME workflow.
 * @author moravja8@fel.cvut.cz
 */
public interface KnimeCostsMapperService {

    /**
     * Funkce předzpracovává zdroj dat pro analýzu časové náročnosti části workflow.
     * @param inputLog log, z kterého jsou vytěžovány informace o čásové náročnosti průběhu částí workflow
     * @param db databáze, do které mají být získané informace uložené
     * @param table tabulka databáze, do které mají být získané informace uložené
     * @param wf název workflow, jehož průběh je analyzován
     * @return text obsahující informace o časové náročnosti částí workflow
     */
    String mapCostsFromLog(ArrayList<String> inputLog, String db, String table, String wf);


    /**
     * Funkce vytěžuje / vypočítává časovou náročnost jednotlivých částí workflow.
     * @param filteredLog předzpracovaný log, z které jsou vytěžovány informace o čásové náročnosti průběhu částí workflow
     * @param db databáze, do které mají být získané informace uložené
     * @param table tabulka databáze, do které mají být získané informace uložené
     * @param wf název workflow, jehož průběh je analyzován
     * @return čas průběhu jednotlivých částí workflow předformátovaný pro zobrazení v grafickém rozhraní aplikace
     */
    ArrayList<String[]> createCostMap(ArrayList<String> filteredLog, String db, String table, String wf);
}
