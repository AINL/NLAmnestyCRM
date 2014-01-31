package nl.amnesty.crm.config;

import java.util.ArrayList;
import java.util.List;
import nl.amnesty.crm.sql.NetworkSQL;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author evelzen
 */
public class NetworkSAXHandler extends DefaultHandler {

    private List<String> qnamelist;
    private int index;
    private String value;
    private boolean inelement = false;
    //
    public static List<NetworkDef> networkdeflist;
    private Long network_networkid;
    private String network_name;
    private String network_description;
    private String network_mediatype;
    private List<String> network_sql_read;
    private List<String> network_sql_add;
    private List<String> network_sql_remove;
    private List<String> network_sql_partof;

    public NetworkSAXHandler() throws org.xml.sax.SAXException {
        super();
    }

    /**
     * Receive notification of the start of the document.
     */
    @Override
    public void startDocument() {
        networkdeflist = new ArrayList();
        value = "";
        qnamelist = new ArrayList();
        index = 0;
    }

    /**
     * Receive notification of the end of the document.
     */
    @Override
    public void endDocument() {
        NetworkSQL.networkdeflist = networkdeflist;
    }

    /**
     * Receive notification of the start of an element.
     * @param uri
     * @param localname
     * @param qname
     * @param attributes
     */
    @Override
    public void startElement(String uri, String localname, String qname, Attributes attributes) {
        if (qname.equals("network")) {
            network_sql_read = new ArrayList();
            network_sql_add = new ArrayList();
            network_sql_remove = new ArrayList();
            network_sql_partof = new ArrayList();
        }

        qnamelist.add(qname);
        index++;
        inelement = true;
    }

    /**
     * Receive notification of the end of an element.
     * @param uri
     * @param localname
     * @param qname
     */
    @Override
    public void endElement(String uri, String localname, String qname) {
        if (qname.equals("network")) {
            NetworkDef networkdef = new NetworkDef();
            networkdef.setNetworkid(network_networkid);
            networkdef.setName(network_name);
            networkdef.setDescription(network_description);
            networkdef.setMediatype(network_mediatype);
            networkdef.setSql_read(network_sql_read);
            networkdef.setSql_add(network_sql_add);
            networkdef.setSql_remove(network_sql_remove);
            networkdef.setSql_partof(network_sql_partof);
            networkdeflist.add(networkdef);
        }

        if (qname.equals("networkid")) {
            if (qnameParent().equals("network")) {
                if (isInteger(value)) {
                    network_networkid = Long.valueOf(value);
                }
            }
        }
        if (qname.equals("name")) {
            if (qnameParent().equals("network")) {
                network_name = value;
            }
        }
        if (qname.equals("description")) {
            if (qnameParent().equals("network")) {
                network_description = value;
            }
        }
        if (qname.equals("mediatype")) {
            if (qnameParent().equals("network")) {
                network_mediatype = value;
            }
        }
        if (qname.equals("statement")) {
            if (qnameParent().equals("read")) {
                network_sql_read.add(value);
            }
            if (qnameParent().equals("add")) {
                network_sql_add.add(value);
            }
            if (qnameParent().equals("remove")) {
                network_sql_remove.add(value);
            }
            if (qnameParent().equals("partof")) {
                network_sql_partof.add(value);
            }
        }

        qnamelist.remove(index - 1);
        index--;
        value = "";
        inelement = false;
    }

    /**
     * Receive notification of character data inside an element.
     * @param ch
     * @param start
     * @param length
     */
    @Override
    public void characters(char[] ch, int start, int length) {
        if (inelement) {
            String charactervalue = "";
            for (int i = 0; i < length; i++) {
                char c = ch[start + i];
                if (c != '\r' && c != '\t' && c != '\f' && c != '\n') {
                    charactervalue = charactervalue.concat(String.valueOf(c));
                }
            }
            //value = charactervalue;
            value = value.concat(charactervalue).trim();
            // DEBUG
            //System.out.println("characters() start: " + start + " lenght: " + length + " value: [" + charactervalue + "]");
        }
    }

    private boolean isInteger(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String qnameParent() {
        return qnamelist.get(index - 2);
    }
}
