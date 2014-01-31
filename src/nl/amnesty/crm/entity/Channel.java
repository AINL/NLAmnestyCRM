/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

/**
 *
 * @author evelzen
 */
public class Channel {

    public final static int MEDIATYPE_PAPER = 1;
    public final static int MEDIATYPE_EMAIL = 2;
    public final static int MEDIATYPE_SMS = 3;
    public final static int MEDIATYPE_PHONE = 4;
    public final static int MEDIATYPE_FACEBOOK = 5;
    public final static int MEDIATYPE_TWITTER = 6;
    public final static String MEDIATYPE_NAME_PAPER = "PAPER";
    public final static String MEDIATYPE_NAME_EMAIL = "EMAIL";
    public final static String MEDIATYPE_NAME_PHONE = "PHONE";
    public final static String MEDIATYPE_NAME_SMS = "TEXTMESSAGING";
    public final static String MEDIATYPE_NAME_FACEBOOK = "FACEBOOK";
    public final static String MEDIATYPE_NAME_TWITTER = "TWITTER";
    private int channelid;
    private int mediatype;
    private String addressidentifier;

    public Channel() {
    }

    public Channel(int channelid, int mediatype, String addressidentifier) {
        this.channelid = channelid;
        this.mediatype = mediatype;
        this.addressidentifier = addressidentifier;
    }

    public String getAddressidentifier() {
        return addressidentifier;
    }

    public void setAddressidentifier(String addressidentifier) {
        this.addressidentifier = addressidentifier;
    }

    public int getChannelid() {
        return channelid;
    }

    public void setChannelid(int channelid) {
        this.channelid = channelid;
    }

    public int getMediatype() {
        return mediatype;
    }

    public void setMediatype(int mediatype) {
        this.mediatype = mediatype;
    }

    public String getMediatypeName() {
        switch (this.mediatype) {
            case Channel.MEDIATYPE_PAPER:
                return Channel.MEDIATYPE_NAME_PAPER;
            case Channel.MEDIATYPE_EMAIL:
                return Channel.MEDIATYPE_NAME_EMAIL;
            case Channel.MEDIATYPE_PHONE:
                return Channel.MEDIATYPE_NAME_PHONE;
            case Channel.MEDIATYPE_SMS:
                return Channel.MEDIATYPE_NAME_SMS;
            case Channel.MEDIATYPE_FACEBOOK:
                return Channel.MEDIATYPE_NAME_FACEBOOK;
            case Channel.MEDIATYPE_TWITTER:
                return Channel.MEDIATYPE_NAME_TWITTER;
            default:
                return "";
        }
    }
    
}
