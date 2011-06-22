/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diwan;

import com.diwan.OCR.OCRPages;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ahmed Kharrufa
 */
public class OCRThread  extends Thread {

    OCRBook outer;

    OCRThread(OCRBook outer) {
        this.outer = outer;
    }

    // This method is called when the thread runs
    public void run() {
        try {
            OCRPages ocrPgs = new OCRPages();
            System.out.println("OCRPages...");
            //(String url, String pid, String inFacet, String inLang,  String outFacet)
            ocrPgs.doOCR(outer.sourceUrl, outer.ticketId, outer.inputFacet, outer.inputLang, outer.outputFacet);
        } catch (Exception ex) {
            Logger.getLogger(OCRBook.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
