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
    static String ABBY_HOME ="ABBYY\\";
    static String IN_PICT_HOME="OCRInFolder\\";
    static String OUT_OCR_HOME="OCROutFolder\\";
    static String CLI_IN_OPTIONS="";
    static String CLI_OUT_OPTIONS="";

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
        String ocrFileName = ABBY_HOME+OUT_OCR_HOME+pageID.replace(':', '-')+".xml";
        //String ocrFileName = ABBY_HOME+OUT_OCR_HOME+pageID.replace(':', '-')+".gif"; for testing
        try {
            FileOutputStream fos = new FileOutputStream(imgFileName);
            fos.write(imageArray);
            fos.close();
            Runtime runtime = Runtime.getRuntime();
            Process process = null;
            String commandLine = ABBY_HOME+"cli "+CLI_IN_OPTIONS+" -if "+imgFileName+ " "+ CLI_OUT_OPTIONS+" -of "+ocrFileName;
            //String commandLine = ABBY_HOME+"convert "+imgFileName+ " "+ ocrFileName; for testing
            System.out.println("Executing>"+commandLine+" <br/>");
            process = runtime.exec(commandLine);
            System.out.println("Operation completed...<br/>");
            BufferedReader inBuf = new BufferedReader(new InputStreamReader(process.getInputStream())); // Read and print the output
            String line = null;
            while ((line = inBuf.readLine()) != null)
                {
                System.out.println(line);
                //out.println("<br/>");
                }
            //AltoDoc.writeOCR(url, outFacet, pageID);
        } catch (Exception ex) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
