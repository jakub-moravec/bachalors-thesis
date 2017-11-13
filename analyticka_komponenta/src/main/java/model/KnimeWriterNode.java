package model;

import DAO.DaoFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;

/**
 * Třída implementující model takového uzlu KNIME workflow, který slouží k exportu výsledků KNIME workflow.
 * @author moravja8@fel.cvut.cz
 */
public class KnimeWriterNode extends KnimeNode {
    private static Logger log = LoggerFactory.getLogger(KnimeNode.class);

    private String outputFile = null;

    /**
     * Uzel je vytvořen na základě jeho nastavení, definovaného pomocí XML souboru.
     * @param root adresář, kterým je uzel definován obsahující mimo jiné XML nastavení uzlu
     * @throws IOException v případě, že není nalezeno nastavení uzlu
     */
    public KnimeWriterNode(File root) throws IOException {
        super(root);
    }

    @Nullable
    private String getOutputFile() {
        if(outputFile == null){
            String[] labelParts = super.getLabel().split("_");
            if(labelParts.length == 2){
                return DaoFactory.getPropertiesDao().get("OutputFolder") +
                        File.separator + labelParts[1];
            }else{
                log.error("Label of export node is not in correct format. " +
                        "Correct format is 'export_nodeName.extension'.", new IllegalArgumentException());
                return null;
            }
        }
        return  outputFile;
    }

    /**
     * Obnovuje nastavení uzlu, pokud bylo změněno v XML konfiguraci.
     */
    public void refreshSettigs(){
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "filename", this.getOutputFile());
        super.setChanged(true);
    }
}
