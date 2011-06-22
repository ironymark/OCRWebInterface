/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diwan.OCR;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed Kharrufa
 */
public class FixAltoOutput {
    private int currentBlockIndex;
    private int currentLineIndex;
    private int currentStringIndex;
    private String currentBlockID;
    private String currentLineID;
    private String currentStringID;

    public void fixFile(String inFileName, String outFileName) {
        currentBlockIndex = -1;
        currentLineIndex = -1;
        currentStringIndex =-1;
        currentBlockID = null;
        currentLineID = null;
        currentStringID = null;
        try{
            FileInputStream fis = new FileInputStream(inFileName);
            BufferedInputStream bis = new BufferedInputStream(fis);
            BufferedReader reader = new BufferedReader(new InputStreamReader(bis));

            FileOutputStream fos = new FileOutputStream(outFileName);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(bos));

            ArrayList<String> lineArray;
            String lineFromFile;
            String outputLine;
            while((lineFromFile = reader.readLine())!=null) {
                lineArray = breakIntoLines(lineFromFile);
                for(int lineIndex = 0 ; lineIndex< lineArray.size() ; lineIndex++) {
                    outputLine = fixLine(lineArray.get(lineIndex));
                    writer.write(outputLine);
                    writer.newLine();
                }
            }
            writer.close();
            bos.close();
            fos.close();

            reader.close();
            bis.close();
            fis.close();

        }catch(Exception e) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private String fixLine(String inLine) {
        StringBuilder fixedLine = new StringBuilder();
        if(inLine.startsWith("<textBlock")) {
            currentBlockIndex++;
            currentLineIndex = -1;
            currentStringIndex = -1;
            fixedLine.append("<TextBlock");
            int IDIndex = inLine.indexOf("ID=");
            currentBlockID = inLine.substring(IDIndex+4,inLine.indexOf('\"', IDIndex+4));
            fixedLine.append(inLine.substring(10));
        }
        else if(inLine.startsWith("<TextLine")) {
            currentLineIndex++;
            currentStringIndex = -1;
            currentLineID = currentBlockID+"_"+currentLineIndex;
            fixedLine.append("<TextLine ID=\"");
            fixedLine.append(currentLineID);
            fixedLine.append("\" ");
            fixedLine.append(inLine.substring(10));
        }
        else if(inLine.startsWith("<String")) {
            currentStringIndex++;
            currentStringID = currentLineID+"_"+currentStringIndex;
            fixedLine.append("<String ID=\"");
            fixedLine.append(currentStringID);
            fixedLine.append("\" ");
            fixedLine.append(inLine.substring(8));
        }
        else if(inLine.equals("</textBlock>"))
            return "</TextBlock>";
        else
            return inLine;
        return fixedLine.toString();
    }

    private ArrayList<String> breakIntoLines(String inLine) {
        ArrayList<String> lines = new ArrayList<String>();
        String tmpLine;
        int fromIndex = 0;
        int toIndex;
        while((toIndex= inLine.indexOf("><",fromIndex))!=-1) {
            tmpLine = inLine.substring(fromIndex,toIndex+1);
            lines.add(tmpLine);
            fromIndex = toIndex+1;
        }
        lines.add(inLine.substring(fromIndex));
        return lines;
    }
}
