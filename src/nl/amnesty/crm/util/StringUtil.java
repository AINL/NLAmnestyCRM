/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.util;

import java.util.StringTokenizer;

/**
 *
 * @author evelzen
 */
public class StringUtil {

    public static String removeSpaces(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, " ", false);
        String nospaces = "";
        while (tokenizer.hasMoreElements()) {
            nospaces = nospaces.concat(tokenizer.nextToken());
        }
        return nospaces;
    }

    public static String removeDots(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, ".", false);
        String nodots = "";
        while (tokenizer.hasMoreElements()) {
            nodots = nodots.concat(tokenizer.nextToken());
        }
        return nodots;
    }

    public static String properCase(String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, " ", false);
        String propercase = "";
        while (tokenizer.hasMoreElements()) {
            String token = tokenizer.nextToken();
            if (token.length() > 1) {
                propercase = propercase.concat(token.substring(0, 1).toUpperCase().concat(token.substring(1).toLowerCase()));
                propercase = propercase.concat(" ");
            }
        }
        return propercase.trim();
    }
}
