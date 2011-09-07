/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.diwan.OCR;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
/**
 *
 * @author Ahmed Kharrufa
 */
public class AltoDoc {

    public static Document parserXML(String uri) throws SAXException, IOException, ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(uri);
    }

    public static ArrayList<String> getPageIds(String sourceURL,String pid) {
        ArrayList<String> pageIds = null;
        try {
            Document doc = parserXML(sourceURL + "/objects/" + pid + "/datastreams/RELS-EXT/content");
            if (doc != null) {
                NodeList hasPage = doc.getElementsByTagName("hasPageStruct");
                if (hasPage != null) {
                    Node node = hasPage.item(0);
                    if (node != null) {
                        NamedNodeMap attrList = node.getAttributes();
                        if (attrList != null) {
                            Node attr = attrList.getNamedItem("rdf:resource");
                            if (attr != null) {
                                String value = attr.getNodeValue();
                                if (value != null) {
                                    String pageId = value.split("/")[1];
                                    Document doc2 = parserXML(sourceURL + "/objects/" + pageId + "/datastreams/RELS-EXT/content");
                                    if (doc2 != null) {
                                        NodeList desc = doc2.getElementsByTagName("rdf:Description");
                                        if (desc != null) {
                                            Node descNode = desc.item(0);
                                            if (descNode != null) {
                                                NodeList partList = descNode.getChildNodes();
                                                if (partList != null) {
                                                    pageIds = new ArrayList<String>();
                                                    for (int i = 0; i < partList.getLength(); i++) {
                                                        Node part = partList.item(i);
                                                        if (part != null && part.getNodeType() == Node.ELEMENT_NODE) {
                                                            if (part.getNodeName().indexOf("hasPart") > -1) {
                                                                NamedNodeMap partAttrList = part.getAttributes();
                                                                if (partAttrList != null) {
                                                                    Node partAttr = partAttrList.getNamedItem("rdf:resource");
                                                                    if (partAttr != null) {
                                                                        String partValue = partAttr.getNodeValue();
                                                                        if (partValue != null) {
                                                                            String partId = partValue.split("/")[1];
                                                                            pageIds.add(partId);
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } catch (SAXException e) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } catch (IOException e) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, e);
            return null;
        } catch (ParserConfigurationException e) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, e);
            return null;
        }
        return pageIds;
    }

    public static void writeOCR(String sourceUrl, String sourceFileName, String pageId, String outputFacet) {
        HttpURLConnection httpCon = null;
        try {
            FileInputStream fis = new FileInputStream(sourceFileName);
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buf = new byte[5120];
            int n = 0;
            while (-1!=(n=fis.read(buf)))
            {
                outStream.write(buf, 0, n);
            }
            outStream.close();
            fis.close();
            //URL outputUrl = new URL(sourceUrl + "/objects/iqra/obj/" + pageId + "/datastreams/" + outputFacet+"/alto?controlGroup=X");
            URL outputUrl = new URL(sourceUrl + "/objects/iqra/obj/" + pageId + "/datastreams/" + outputFacet+"/alto");
            System.out.println("url call="+outputUrl.toString());
            httpCon = (HttpURLConnection) outputUrl.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setUseCaches(false);
            httpCon.setRequestProperty("Authorization", "Basic " + BasicAuth.encode("IQRAUser", "!QraUs3r"));
            httpCon.setRequestProperty("Content-Type", "text/xml");
            httpCon.setRequestMethod("POST");
            byte[] ocrByteArray = outStream.toByteArray();
            httpCon.setRequestProperty("Content-Length", "" + Integer.toString(ocrByteArray.length));
            DataOutputStream wr = new DataOutputStream(httpCon.getOutputStream());
            wr.write(ocrByteArray);
            wr.flush();
            wr.close();
            System.out.println(httpCon.getResponseCode());
            System.out.println(httpCon.getResponseMessage());
        }catch (Exception ex) {
            //nothing to do here move along
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (httpCon != null) {
                httpCon.disconnect();
            }
        }
    }
     public static byte[] getImage(String sourceUrl, String inFacet, String pid) {
        InputStream in = null;
        byte[] imageArray = null;
        try {
            URL u = new URL(sourceUrl + "/objects/" + pid + "/datastreams/"+inFacet+"/content");
            in = u.openStream();
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            byte[] buf = new byte[5120];
            int n = 0;
            while (-1!=(n=in.read(buf)))
            {
                outStream.write(buf, 0, n);
            }
            outStream.close();
            in.close();
            imageArray = outStream.toByteArray();
        } catch (Exception ex) {
            Logger.getLogger(com.diwan.OCRBook.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    System.err.println(e.toString());
                }
            }
        }
        return imageArray;
    }
}

class BasicAuth {

    private BasicAuth() {
    }
    // conversion table
    private static byte[] cvtTable = {
        (byte) 'A', (byte) 'B', (byte) 'C', (byte) 'D', (byte) 'E',
        (byte) 'F', (byte) 'G', (byte) 'H', (byte) 'I', (byte) 'J',
        (byte) 'K', (byte) 'L', (byte) 'M', (byte) 'N', (byte) 'O',
        (byte) 'P', (byte) 'Q', (byte) 'R', (byte) 'S', (byte) 'T',
        (byte) 'U', (byte) 'V', (byte) 'W', (byte) 'X', (byte) 'Y',
        (byte) 'Z',
        (byte) 'a', (byte) 'b', (byte) 'c', (byte) 'd', (byte) 'e',
        (byte) 'f', (byte) 'g', (byte) 'h', (byte) 'i', (byte) 'j',
        (byte) 'k', (byte) 'l', (byte) 'm', (byte) 'n', (byte) 'o',
        (byte) 'p', (byte) 'q', (byte) 'r', (byte) 's', (byte) 't',
        (byte) 'u', (byte) 'v', (byte) 'w', (byte) 'x', (byte) 'y',
        (byte) 'z',
        (byte) '0', (byte) '1', (byte) '2', (byte) '3', (byte) '4',
        (byte) '5', (byte) '6', (byte) '7', (byte) '8', (byte) '9',
        (byte) '+', (byte) '/'
    };

    /**
     * Encode a name/password pair appropriate to
     * use in an HTTP header for Basic Authentication.
     *    name     the user's name
     *    passwd   the user's password
     *    returns  String   the base64 encoded name:password
     */
    static String encode(String name,
            String passwd) {
        byte input[] = (name + ":" + passwd).getBytes();
        byte[] output = new byte[((input.length / 3) + 1) * 4];
        int ridx = 0;
        int chunk = 0;

        /**
         * Loop through input with 3-byte stride. For
         * each 'chunk' of 3-bytes, create a 24-bit
         * value, then extract four 6-bit indices.
         * Use these indices to extract the base-64
         * encoding for this 6-bit 'character'
         */
        for (int i = 0; i < input.length; i += 3) {
            int left = input.length - i;

            // have at least three bytes of data left
            if (left > 2) {
                chunk = (input[i] << 16)
                        | (input[i + 1] << 8)
                        | input[i + 2];
                output[ridx++] = cvtTable[(chunk & 0xFC0000) >> 18];
                output[ridx++] = cvtTable[(chunk & 0x3F000) >> 12];
                output[ridx++] = cvtTable[(chunk & 0xFC0) >> 6];
                output[ridx++] = cvtTable[(chunk & 0x3F)];
            } else if (left == 2) {
                // down to 2 bytes. pad with 1 '='
                chunk = (input[i] << 16)
                        | (input[i + 1] << 8);
                output[ridx++] = cvtTable[(chunk & 0xFC0000) >> 18];
                output[ridx++] = cvtTable[(chunk & 0x3F000) >> 12];
                output[ridx++] = cvtTable[(chunk & 0xFC0) >> 6];
                output[ridx++] = '=';
            } else {
                // down to 1 byte. pad with 2 '='
                chunk = input[i] << 16;
                output[ridx++] = cvtTable[(chunk & 0xFC0000) >> 18];
                output[ridx++] = cvtTable[(chunk & 0x3F000) >> 12];
                output[ridx++] = '=';
                output[ridx++] = '=';
            }
        }
        return new String(output);
    }
}