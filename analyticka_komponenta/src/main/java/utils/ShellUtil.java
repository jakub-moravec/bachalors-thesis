package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Třída pro spouštení příkazů pomocí Bash. Tato třída je využívána především při spouštění KNIME workflow.
 * @author moravja8@fel.cvut.cz
 */
public class ShellUtil {
    private static Logger log = LoggerFactory.getLogger(ShellUtil.class);

    /**
     * Metoda spouští nový proces na úrovni operačního systému a čte logy, které jsou procesem vráceny.
     * Metado je ukončena po ukončení procesu.
     * @param command bash příkaz, který má být spuštěn
     * @return log vygenerovaný procesem
     */
    public static ArrayList<String> executeCommand(String command) {

        log.info("Executing command: "+ command +"");

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();


            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            ArrayList<String> output = new ArrayList<String>();

            String line;
            while ((line = reader.readLine())!= null) {
                log.trace(line);
                output.add(line);
            }

            log.info("Command execution terminated "+ command);
            return output;

        } catch (Exception e) {
            log.error("Error occured during command execution", e);
            return null;
        }
    }
}
