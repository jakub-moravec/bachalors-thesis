package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;

/**
 * Třída pro práce se souborovým systémem operačního systému, na které je aplikace spouštěna.
 *
 * @author moravja8@fel.cvut.cz
 */
public class FileSystemUtils {
    private static Logger log = LoggerFactory.getLogger(FileSystemUtils.class);

    /**
     * Metoda vytvoří novou složku. Cílová cesta složky je difinována absolutně, pokud některé z adresářů
     * definovaných v absolutní cestě neexistují, automaticky se vytvoří. Pokud nelze vytvořit složku s definovaným
     * názvem, proběhne několik pokusů o vytvoření složky s podobným, upraveným názvem.
     * @param folderAbsolutePath cílová cesta složky definovaná absolutně
     * @return vytvořenou složku
     */
    public static File createFolder(String folderAbsolutePath){
        File folder = new File(folderAbsolutePath);

        //pokud neexistuje parent složka, rekurzivně se vytvoří
        if(!folder.getParentFile().exists()){
            createFolder(folder.getParentFile().getAbsolutePath());
        }

        //pokusí se 5krát vytvořit složku
        for (int archiveId = 1; archiveId <= 5; archiveId++) {
            if(!folder.exists()){
                folder.mkdir();
                break;
            }else if(folder.exists() && !folder.isDirectory()){
                folder = new File (folderAbsolutePath + archiveId);
            }
        }

        //pokud i tak selže, vrátí null
        if(!(folder.exists() && folder.isDirectory())){
            log.error("Archiv folder could not be created, function returns null");
            return null;
        }

        return folder;
    }

    /**
     * Metoda přesune soubor v souborovém systému.
     * @param sourceFile zrojový soubor
     * @param resultFile cílový soubor
     * @throws IOException v případě, že přesunutí souboru neproběhne
     */
    public static void moveFileSilently(File sourceFile, File resultFile) throws IOException {
        try{
            if(sourceFile.renameTo(resultFile)){
                resultFile.createNewFile();
                log.info("File " + sourceFile.getName() + " was moved to " + resultFile.getParentFile().getAbsolutePath());
            }else{
                throw new IOException();
            }
        }catch(IOException e){
            log.error("File " + sourceFile.getName() + " could not be moved to " + resultFile.getParentFile().getAbsolutePath(), e);
            throw new IOException();
        }
    }
}
