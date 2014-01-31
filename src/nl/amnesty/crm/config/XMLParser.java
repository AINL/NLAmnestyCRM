/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.config;

import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 *
 * @author evelzen
 */
public class XMLParser {

    public void parseNetworkConfig(URL url) {
        try {
            if (url == null) {
                url = new URL("http://zeebaars.amnesty.nl:8080/nlamnestyconfig/network.xml");
            }

            //DEBUG
            //java.util.logging.Logger.getLogger(XMLParser.class.getName()).log(Level.INFO, "about to open {0}", url.toString());

            InputStream in = url.openStream();
            SAXParserFactory factorySAX = SAXParserFactory.newInstance();
            SAXParser sax = factorySAX.newSAXParser();
            NetworkSAXHandler saxhandler = new NetworkSAXHandler();
            sax.parse(in, saxhandler);
        } catch (Exception e) {
            java.util.logging.Logger.getLogger(XMLParser.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
