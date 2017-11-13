package services;

import services.Impl.KnimeLogCostsMapperService;
import services.Impl.KnimeNodeServiceImpl;
import services.Impl.KnimeWorkflowServiceImpl;
import services.Impl.PropertiesServiceImpl;

/**
 * Třída implementuje návrhový vzor Factory, poskytuje implementace služeb.
 * Třída není thread-safe.
 * @author moravja8@fel.cvut.cz
 */
public class ServiceFactory {

    private static KnimeNodeService knimeNodeService = null;
    private static KnimeWorkflowService knimeWorkflowService = null;
    private static KnimeCostsMapperService knimeCostsMapperService = null;
    private static PropertiesService propertiesService = null;

    /**
     * @return instanci implementace {@link KnimeNodeService}
     */
    public static KnimeNodeService getKnimeNodeService() {
        if(knimeNodeService == null){
            knimeNodeService = new KnimeNodeServiceImpl();
        }
        return knimeNodeService;
    }

    /**
     * @return instanci implementace {@link KnimeWorkflowService}
     */
    public static KnimeWorkflowService getKnimeWorkflowService() {
        if(knimeWorkflowService == null){
            knimeWorkflowService = new KnimeWorkflowServiceImpl();
        }
        return knimeWorkflowService;
    }

    /**
     * @return instanci implementace {@link KnimeCostsMapperService}
     */
    public static KnimeCostsMapperService getKnimeCostsMapperService() {
        if(knimeCostsMapperService == null){
            knimeCostsMapperService = new KnimeLogCostsMapperService();
        }
        return knimeCostsMapperService;
    }

    /**
     * @return instanci implementace {@link PropertiesService}
     */
    public static PropertiesService getPropertiesService() {
        if(propertiesService == null){
            propertiesService = new PropertiesServiceImpl();
        }
        return propertiesService;
    }
}
