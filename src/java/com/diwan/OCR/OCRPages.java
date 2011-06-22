/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diwan.OCR;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed Kharrufa
 */
public class OCRPages {
    static String ABBY_HOME ="C:\\LOGHATI_OCR_IO\\";
    static String IN_PICT_HOME="OCRInFolder\\";
    static String OUT_OCR_HOME="OCROutFolder\\";
    static String CLI_IN_OPTIONS="";
    static String CLI_OUT_OPTIONS="-f ALTO -afm Full";

    public void doOCR(String url, String pid, String inFacet, String inLang,  String outFacet) {
        ArrayList<String> pageId = AltoDoc.getPageIds(url, pid);
        for(int i=0;i< pageId.size(); i++) {
            System.out.println("OCR Page "+pid+", index "+i +" started.");
            ocrPage(url, pageId.get(i),inFacet,inLang,outFacet);
            System.out.println("OCR Page "+pid+", index "+i +" completed.");
        }
    }

    public void ocrPage(String url, String pageID, String inFacet, String inLang,  String outFacet) {
        byte[] imageArray = AltoDoc.getImage(url,inFacet, pageID);
        String imgFileName = ABBY_HOME+IN_PICT_HOME+pageID.replace(':', '-');
        String tmpOcrFileName = ABBY_HOME+OUT_OCR_HOME+pageID.replace(':', '-')+"tmp.xml";
        String ocrFileName = ABBY_HOME+OUT_OCR_HOME+pageID.replace(':', '-')+".xml";
        //String ocrFileName = ABBY_HOME+OUT_OCR_HOME+pageID.replace(':', '-')+".gif"; for testing
        try {
            FileOutputStream fos = new FileOutputStream(imgFileName);
            fos.write(imageArray);
            fos.close();
            Runtime runtime = Runtime.getRuntime();
            Process process = null;
            String commandLine = ABBY_HOME+"cli "+CLI_IN_OPTIONS+" -if "+imgFileName+ " "+ CLI_OUT_OPTIONS+" -of "+tmpOcrFileName;
            //String commandLine = ABBY_HOME+"convert "+imgFileName+ " "+ ocrFileName; for testing
            System.out.println("Executing>"+commandLine);
            process = runtime.exec(commandLine);
            System.out.println("Operation completed...");
            BufferedReader inBuf = new BufferedReader(new InputStreamReader(process.getInputStream())); // Read and print the output
            String line = null;
            System.out.println("CLI output...");
            while ((line = inBuf.readLine()) != null)
                {
                System.out.println(line);
                }
            System.out.println("Going to write the output to the facet");
            FixAltoOutput fixAlto = new FixAltoOutput();
            fixAlto.fixFile(tmpOcrFileName,ocrFileName);
            AltoDoc.writeOCR(url, ocrFileName, pageID, outFacet );
            System.out.println("Facet written.");
        } catch (Exception ex) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
