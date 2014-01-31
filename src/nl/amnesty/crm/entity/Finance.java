/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.entity;

import java.util.Date;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.Source;

/**
 *
 * @author ed
 */
public class Finance {

    public final static int STATUS_NEW = 1;
    public final static int STATUS_MATCHED_ID = 2;
    public final static int STATUS_MATCHED_NONE = 3;
    //
    public final static String FIELD_FINANCE_ID = "PAYNO";
    public final static String FIELD_FINANCE_ADPTKEY = "ADPTKEY";
    public final static String FIELD_FINANCE_GAKEY = "GAKEY";
    public final static String FIELD_FINANCE_INVOICENO = "INVOICENO";
    public final static String FIELD_FINANCE_LEGDETKEY = "LEGDETKEY";
    public final static String FIELD_FINANCE_PFKEY = "PFKEY";
    public final static String FIELD_FINANCE_RELPAYNO = "RELPAYNO";
    public final static String FIELD_FINANCE_UDKEY = "UDKEY";
    public final static String FIELD_FINANCE_BANKKEY = "BANKKEY";
    public final static String FIELD_FINANCE_BATCHNO = "BATCHNO";
    public final static String FIELD_FINANCE_CCARDKEY = "CCARDKEY";
    public final static String FIELD_FINANCE_CONVRATE = "CONVRATE";
    public final static String FIELD_FINANCE_COVKEY = "COVKEY";
    public final static String FIELD_FINANCE_EVENT_ID = "EVENT_ID";
    public final static String FIELD_FINANCE_IPDETKEY = "IPDETKEY";
    public final static String FIELD_FINANCE_MEMKEY = "MEMKEY";
    public final static String FIELD_FINANCE_ORDNO = "ORDNO";
    public final static String FIELD_FINANCE_PEOPLEKEY = "PEOPLEKEY";
    public final static String FIELD_FINANCE_PERIOD = "PERIOD";
    public final static String FIELD_FINANCE_PPKEY = "PPKEY";
    public final static String FIELD_FINANCE_ROLE_ID = "PVKEY";
    public final static String FIELD_FINANCE_QTY = "QTY";
    public final static String FIELD_FINANCE_RELCNKEY = "RELCNKEY";
    public final static String FIELD_FINANCE_RELEVREG = "RELEVREG";
    public final static String FIELD_FINANCE_RELPVKEY = "RELPVKEY";
    public final static String FIELD_FINANCE_WEEK = "WEEK";
    public final static String FIELD_FINANCE_YEAR = "YEAR";
    public final static String FIELD_FINANCE_AMOUNT = "AMOUNT";
    public final static String FIELD_FINANCE_AMOUNTDUE = "AMOUNTDUE";
    public final static String FIELD_FINANCE_AMTCLAIMED = "AMTCLAIMED";
    public final static String FIELD_FINANCE_CONVDUE = "CONVDUE";
    public final static String FIELD_FINANCE_CONVPAID = "CONVPAID";
    public final static String FIELD_FINANCE_PREIPAMT = "PREIPAMT";
    public final static String FIELD_FINANCE_TAXRATE = "TAXRATE";
    public final static String FIELD_FINANCE_VATAMT = "VATAMT";
    public final static String FIELD_FINANCE_CONVDATE = "CONVDATE";
    public final static String FIELD_FINANCE_DATEADDED = "DATEADDED";
    public final static String FIELD_FINANCE_DATEDUE = "DATEDUE";
    public final static String FIELD_FINANCE_DATEPAID = "DATEPAID";
    public final static String FIELD_FINANCE_GAPROCDT = "GAPROCDT";
    public final static String FIELD_FINANCE_INVDATE = "INVDATE";
    public final static String FIELD_FINANCE_POSTDATE = "POSTDATE";
    public final static String FIELD_FINANCE_R190RETURN = "R190RETURN";
    public final static String FIELD_FINANCE_R190SENT = "R190SENT";
    public final static String FIELD_FINANCE_RXXXRETURN = "RXXXRETURN";
    public final static String FIELD_FINANCE_RXXXSENT = "RXXXSENT";
    public final static String FIELD_FINANCE_TRANSDATE = "TRANSDATE";
    public final static String FIELD_FINANCE_CRIND = "CRIND";
    public final static String FIELD_FINANCE_DOCPRT = "DOCPRT";
    public final static String FIELD_FINANCE_GACLOSECMP = "GACLOSECMP";
    public final static String FIELD_FINANCE_GACOMPANY = "GACOMPANY";
    public final static String FIELD_FINANCE_GALINKFLAG = "GALINKFLAG";
    public final static String FIELD_FINANCE_IPSTATUS = "IPSTATUS";
    public final static String FIELD_FINANCE_MATCHED = "MATCHED";
    public final static String FIELD_FINANCE_PARTOFBAL = "PARTOFBAL";
    public final static String FIELD_FINANCE_POSTED = "POSTED";
    public final static String FIELD_FINANCE_RENEWMEMB = "RENEWMEMB";
    public final static String FIELD_FINANCE_RXXXOK = "RXXXOK";
    public final static String FIELD_FINANCE_STATUS = "STATUS";
    public final static String FIELD_FINANCE_TAXRECLAIM = "TAXRECLAIM";
    public final static String FIELD_FINANCE_TIMEADDED = "TIMEADDED";
    public final static String FIELD_FINANCE_ACTUALCP = "ACTUALCP";
    public final static String FIELD_FINANCE_GENDATE = "GENDATE";
    public final static String FIELD_FINANCE_TARGETCP = "TARGETCP";
    public final static String FIELD_FINANCE_CCAUTHCODE = "CCAUTHCODE";
    public final static String FIELD_FINANCE_CAMPAIGN = "CAMPAIGN";
    public final static String FIELD_FINANCE_CONVCURR = "CONVCURR";
    public final static String FIELD_FINANCE_DEPT = "DEPT";
    public final static String FIELD_FINANCE_DOCNO = "DOCNO";
    public final static String FIELD_FINANCE_DOCREQ = "DOCREQ";
    public final static String FIELD_FINANCE_FUND = "FUND";
    public final static String FIELD_FINANCE_GASTATUS = "GASTATUS";
    public final static String FIELD_FINANCE_ORGBANK = "ORGBANK";
    public final static String FIELD_FINANCE_PAYMETHOD = "PAYMETHOD";
    public final static String FIELD_FINANCE_PAYSOURCE = "PAYSOURCE";
    public final static String FIELD_FINANCE_PAYTYPE = "PAYTYPE";
    public final static String FIELD_FINANCE_RXXXTYPE = "RXXXTYPE";
    public final static String FIELD_FINANCE_SEGMENT = "SEGMENT";
    public final static String FIELD_FINANCE_USERID = "USERID";
    public final static String FIELD_FINANCE_VATRATE = "VATRATE";
    public final static String FIELD_FINANCE_DKEY = "DKEY";
    public final static String FIELD_FINANCE_INVSTATUS = "INVSTATUS";
    public final static String FIELD_FINANCE_MATCHREF = "MATCHREF";
    public final static String FIELD_FINANCE_UDTAB = "UDTAB";
    public final static String FIELD_FINANCE_VOUCHER = "VOUCHER";
    public final static String FIELD_FINANCE_PAIDBY = "PAIDBY";
    public final static String FIELD_FINANCE_PAYREF = "PAYREF";
    public final static String FIELD_FINANCE_POSTREF = "POSTREF";
    public final static String FIELD_FINANCE_RELCNNOTE = "RELCNNOTE";
    public final static String FIELD_FINANCE_RELNOTE = "RELNOTE";
    public final static String FIELD_FINANCE_RELPYCOM = "RELPYCOM";
    public final static String FIELD_FINANCE_COMMENTS = "COMMENTS";
    public final static String FIELD_FINANCE_SYSTEXT = "SYSTEXT";
    public final static String FIELD_FINANCE_LTRTEXT1 = "LTRTEXT1";
    public final static String FIELD_FINANCE_LTRTEXT2 = "LTRTEXT2";
    public final static String FIELD_FINANCE_LTRTEXT3 = "LTRTEXT3";
    //
    private long payno;
    private int adptkey;
    private int gakey;
    private int invoiceno;
    private int legdetkey;
    private int pfkey;
    private int relpayno;
    private int udkey;
    private long bankkey;
    private float batchno;
    private float ccardkey;
    private float convrate;
    private float covkey;
    private float event_id;
    private float ipdetkey;
    private float memkey;
    private float ordno;
    private float peoplekey;
    private float period;
    private float ppkey;
    private long pvkey;
    private float qty;
    private float relcnkey;
    private float relevreg;
    private float relpvkey;
    private float week;
    private float year;
    private float amount;
    private float amountdue;
    private float amtclaimed;
    private float convdue;
    private float convpaid;
    private float preipamt;
    private float taxrate;
    private float vatamt;
    private Date convdate;
    private Date dateadded;
    private Date datedue;
    private Date datepaid;
    private Date gaprocdt;
    private Date invdate;
    private Date postdate;
    private Date r190return;
    private Date r190sent;
    private Date rxxxreturn;
    private Date rxxxsent;
    private Date transdate;
    private char crind;
    private char docprt;
    private char gaclosecmp;
    private char gacompany;
    private char galinkflag;
    private char ipstatus;
    private char matched;
    private char partofbal;
    private char posted;
    private char renewmemb;
    private char rxxxok;
    private char status;
    private char taxreclaim;
    private String timeadded;
    private String actualcp;
    private String gendate;
    private String targetcp;
    private String ccauthcode;
    private String campaign;
    private String convcurr;
    private String dept;
    private String docno;
    private String docreq;
    private String fund;
    private String gastatus;
    private String orgbank;
    private String paymethod;
    private String paysource;
    private String paytype;
    private String rxxxtype;
    private String segment;
    private String userid;
    private String vatrate;
    private String dkey;
    private String invstatus;
    private String matchref;
    private String udtab;
    private String voucher;
    private String paidby;
    private String payref;
    private String postref;
    private String relcnnote;
    private String relnote;
    private String relpycom;
    private String comments;
    private String sysString;
    private String ltrString1;
    private String ltrString2;
    private String ltrString3;
    private int statusRecord;

    public Finance() {
    }

    public long getFinanceid() {
        return payno;
    }

    public void setFinanceid(long payno) {
        this.payno = payno;
    }

    public int getAdptkey() {
        return adptkey;
    }

    public void setAdptkey(int adptkey) {
        this.adptkey = adptkey;
    }

    public int getGakey() {
        return gakey;
    }

    public void setGakey(int gakey) {
        this.gakey = gakey;
    }

    public int getInvoiceno() {
        return invoiceno;
    }

    public void setInvoiceno(int invoiceno) {
        this.invoiceno = invoiceno;
    }

    public int getLegdetkey() {
        return legdetkey;
    }

    public void setLegdetkey(int legdetkey) {
        this.legdetkey = legdetkey;
    }

    public int getPfkey() {
        return pfkey;
    }

    public void setPfkey(int pfkey) {
        this.pfkey = pfkey;
    }

    public int getRelpayno() {
        return relpayno;
    }

    public void setRelpayno(int relpayno) {
        this.relpayno = relpayno;
    }

    public int getUdkey() {
        return udkey;
    }

    public void setUdkey(int udkey) {
        this.udkey = udkey;
    }

    public long getBankkey() {
        return bankkey;
    }

    public void setBankkey(long bankkey) {
        this.bankkey = bankkey;
    }

    public float getBatchno() {
        return batchno;
    }

    public void setBatchno(float batchno) {
        this.batchno = batchno;
    }

    public float getCcardkey() {
        return ccardkey;
    }

    public void setCcardkey(float ccardkey) {
        this.ccardkey = ccardkey;
    }

    public float getConvrate() {
        return convrate;
    }

    public void setConvrate(float convrate) {
        this.convrate = convrate;
    }

    public float getCovkey() {
        return covkey;
    }

    public void setCovkey(float covkey) {
        this.covkey = covkey;
    }

    public float getEvent_id() {
        return event_id;
    }

    public void setEvent_id(float event_id) {
        this.event_id = event_id;
    }

    public float getIpdetkey() {
        return ipdetkey;
    }

    public void setIpdetkey(float ipdetkey) {
        this.ipdetkey = ipdetkey;
    }

    public float getMemkey() {
        return memkey;
    }

    public void setMemkey(float memkey) {
        this.memkey = memkey;
    }

    public float getOrdno() {
        return ordno;
    }

    public void setOrdno(float ordno) {
        this.ordno = ordno;
    }

    public float getPeoplekey() {
        return peoplekey;
    }

    public void setPeoplekey(float peoplekey) {
        this.peoplekey = peoplekey;
    }

    public float getPeriod() {
        return period;
    }

    public void setPeriod(float period) {
        this.period = period;
    }

    public float getPpkey() {
        return ppkey;
    }

    public void setPpkey(float ppkey) {
        this.ppkey = ppkey;
    }

    public long getRoleid() {
        return pvkey;
    }

    public void setRoleid(long pvkey) {
        this.pvkey = pvkey;
    }

    public float getQty() {
        return qty;
    }

    public void setQty(float qty) {
        this.qty = qty;
    }

    public float getRelcnkey() {
        return relcnkey;
    }

    public void setRelcnkey(float relcnkey) {
        this.relcnkey = relcnkey;
    }

    public float getRelevreg() {
        return relevreg;
    }

    public void setRelevreg(float relevreg) {
        this.relevreg = relevreg;
    }

    public float getRelpvkey() {
        return relpvkey;
    }

    public void setRelpvkey(float relpvkey) {
        this.relpvkey = relpvkey;
    }

    public float getWeek() {
        return week;
    }

    public void setWeek(float week) {
        this.week = week;
    }

    public float getYear() {
        return year;
    }

    public void setYear(float year) {
        this.year = year;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public float getAmountdue() {
        return amountdue;
    }

    public void setAmountdue(float amountdue) {
        this.amountdue = amountdue;
    }

    public float getAmtclaimed() {
        return amtclaimed;
    }

    public void setAmtclaimed(float amtclaimed) {
        this.amtclaimed = amtclaimed;
    }

    public float getConvdue() {
        return convdue;
    }

    public void setConvdue(float convdue) {
        this.convdue = convdue;
    }

    public float getConvpaid() {
        return convpaid;
    }

    public void setConvpaid(float convpaid) {
        this.convpaid = convpaid;
    }

    public float getPreipamt() {
        return preipamt;
    }

    public void setPreipamt(float preipamt) {
        this.preipamt = preipamt;
    }

    public float getTaxrate() {
        return taxrate;
    }

    public void setTaxrate(float taxrate) {
        this.taxrate = taxrate;
    }

    public float getVatamt() {
        return vatamt;
    }

    public void setVatamt(float vatamt) {
        this.vatamt = vatamt;
    }

    public Date getConvdate() {
        return convdate;
    }

    public void setConvdate(Date convdate) {
        this.convdate = convdate;
    }

    public Date getDateadded() {
        return dateadded;
    }

    public void setDateadded(Date dateadded) {
        this.dateadded = dateadded;
    }

    public Date getDatedue() {
        return datedue;
    }

    public void setDatedue(Date datedue) {
        this.datedue = datedue;
    }

    public Date getDatepaid() {
        return datepaid;
    }

    public void setDatepaid(Date datepaid) {
        this.datepaid = datepaid;
    }

    public Date getGaprocdt() {
        return gaprocdt;
    }

    public void setGaprocdt(Date gaprocdt) {
        this.gaprocdt = gaprocdt;
    }

    public Date getInvdate() {
        return invdate;
    }

    public void setInvdate(Date invdate) {
        this.invdate = invdate;
    }

    public Date getPostdate() {
        return postdate;
    }

    public void setPostdate(Date postdate) {
        this.postdate = postdate;
    }

    public Date getR190return() {
        return r190return;
    }

    public void setR190return(Date r190return) {
        this.r190return = r190return;
    }

    public Date getR190sent() {
        return r190sent;
    }

    public void setR190sent(Date r190sent) {
        this.r190sent = r190sent;
    }

    public Date getRxxxreturn() {
        return rxxxreturn;
    }

    public void setRxxxreturn(Date rxxxreturn) {
        this.rxxxreturn = rxxxreturn;
    }

    public Date getRxxxsent() {
        return rxxxsent;
    }

    public void setRxxxsent(Date rxxxsent) {
        this.rxxxsent = rxxxsent;
    }

    public Date getTransdate() {
        return transdate;
    }

    public void setTransdate(Date transdate) {
        this.transdate = transdate;
    }

    public char getCrind() {
        return crind;
    }

    public void setCrind(char crind) {
        this.crind = crind;
    }

    public char getDocprt() {
        return docprt;
    }

    public void setDocprt(char docprt) {
        this.docprt = docprt;
    }

    public char getGaclosecmp() {
        return gaclosecmp;
    }

    public void setGaclosecmp(char gaclosecmp) {
        this.gaclosecmp = gaclosecmp;
    }

    public char getGacompany() {
        return gacompany;
    }

    public void setGacompany(char gacompany) {
        this.gacompany = gacompany;
    }

    public char getGalinkflag() {
        return galinkflag;
    }

    public void setGalinkflag(char galinkflag) {
        this.galinkflag = galinkflag;
    }

    public char getIpstatus() {
        return ipstatus;
    }

    public void setIpstatus(char ipstatus) {
        this.ipstatus = ipstatus;
    }

    public char getMatched() {
        return matched;
    }

    public void setMatched(char matched) {
        this.matched = matched;
    }

    public char getPartofbal() {
        return partofbal;
    }

    public void setPartofbal(char partofbal) {
        this.partofbal = partofbal;
    }

    public char getPosted() {
        return posted;
    }

    public void setPosted(char posted) {
        this.posted = posted;
    }

    public char getRenewmemb() {
        return renewmemb;
    }

    public void setRenewmemb(char renewmemb) {
        this.renewmemb = renewmemb;
    }

    public char getRxxxok() {
        return rxxxok;
    }

    public void setRxxxok(char rxxxok) {
        this.rxxxok = rxxxok;
    }

    public char getStatus() {
        return status;
    }

    public void setStatus(char status) {
        this.status = status;
    }

    public char getTaxreclaim() {
        return taxreclaim;
    }

    public void setTaxreclaim(char taxreclaim) {
        this.taxreclaim = taxreclaim;
    }

    public String getTimeadded() {
        return timeadded;
    }

    public void setTimeadded(String timeadded) {
        this.timeadded = timeadded;
    }

    public String getActualcp() {
        return actualcp;
    }

    public void setActualcp(String actualcp) {
        this.actualcp = actualcp;
    }

    public String getGendate() {
        return gendate;
    }

    public void setGendate(String gendate) {
        this.gendate = gendate;
    }

    public String getTargetcp() {
        return targetcp;
    }

    public void setTargetcp(String targetcp) {
        this.targetcp = targetcp;
    }

    public String getCcauthcode() {
        return ccauthcode;
    }

    public void setCcauthcode(String ccauthcode) {
        this.ccauthcode = ccauthcode;
    }

    public String getCampaign() {
        return campaign;
    }

    public void setCampaign(String campaign) {
        this.campaign = campaign;
    }

    public String getConvcurr() {
        return convcurr;
    }

    public void setConvcurr(String convcurr) {
        this.convcurr = convcurr;
    }

    public String getDept() {
        return dept;
    }

    public void setDept(String dept) {
        this.dept = dept;
    }

    public String getDocno() {
        return docno;
    }

    public void setDocno(String docno) {
        this.docno = docno;
    }

    public String getDocreq() {
        return docreq;
    }

    public void setDocreq(String docreq) {
        this.docreq = docreq;
    }

    public String getFund() {
        return fund;
    }

    public void setFund(String fund) {
        this.fund = fund;
    }

    public String getGastatus() {
        return gastatus;
    }

    public void setGastatus(String gastatus) {
        this.gastatus = gastatus;
    }

    public String getOrgbank() {
        return orgbank;
    }

    public void setOrgbank(String orgbank) {
        this.orgbank = orgbank;
    }

    public String getPaymethod() {
        return paymethod;
    }

    public void setPaymethod(String paymethod) {
        this.paymethod = paymethod;
    }

    public String getPaysource() {
        return paysource;
    }

    public void setPaysource(String paysource) {
        this.paysource = paysource;
    }

    public String getPaytype() {
        return paytype;
    }

    public void setPaytype(String paytype) {
        this.paytype = paytype;
    }

    public String getRxxxtype() {
        return rxxxtype;
    }

    public void setRxxxtype(String rxxxtype) {
        this.rxxxtype = rxxxtype;
    }

    public String getSegment() {
        return segment;
    }

    public void setSegment(String segment) {
        this.segment = segment;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getVatrate() {
        return vatrate;
    }

    public void setVatrate(String vatrate) {
        this.vatrate = vatrate;
    }

    public String getDkey() {
        return dkey;
    }

    public void setDkey(String dkey) {
        this.dkey = dkey;
    }

    public String getInvstatus() {
        return invstatus;
    }

    public void setInvstatus(String invstatus) {
        this.invstatus = invstatus;
    }

    public String getMatchref() {
        return matchref;
    }

    public void setMatchref(String matchref) {
        this.matchref = matchref;
    }

    public String getUdtab() {
        return udtab;
    }

    public void setUdtab(String udtab) {
        this.udtab = udtab;
    }

    public String getVoucher() {
        return voucher;
    }

    public void setVoucher(String voucher) {
        this.voucher = voucher;
    }

    public String getPaidby() {
        return paidby;
    }

    public void setPaidby(String paidby) {
        this.paidby = paidby;
    }

    public String getPayref() {
        return payref;
    }

    public void setPayref(String payref) {
        this.payref = payref;
    }

    public String getPostref() {
        return postref;
    }

    public void setPostref(String postref) {
        this.postref = postref;
    }

    public String getRelcnnote() {
        return relcnnote;
    }

    public void setRelcnnote(String relcnnote) {
        this.relcnnote = relcnnote;
    }

    public String getRelnote() {
        return relnote;
    }

    public void setRelnote(String relnote) {
        this.relnote = relnote;
    }

    public String getRelpycom() {
        return relpycom;
    }

    public void setRelpycom(String relpycom) {
        this.relpycom = relpycom;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getSysString() {
        return sysString;
    }

    public void setSysString(String sysString) {
        this.sysString = sysString;
    }

    public String getLtrString1() {
        return ltrString1;
    }

    public void setLtrString1(String ltrString1) {
        this.ltrString1 = ltrString1;
    }

    public String getLtrString2() {
        return ltrString2;
    }

    public void setLtrString2(String ltrString2) {
        this.ltrString2 = ltrString2;
    }

    public String getLtrString3() {
        return ltrString3;
    }

    public void setLtrString3(String ltrString3) {
        this.ltrString3 = ltrString3;
    }

    public void mapPropertyValue(Properties mapping) {
        // amount_euro is other_amount is dus afwijkend, zelf opgegeven bedrag. kan een komma in zitten, of een punt, of...
        parseAmount((mapping.getProperty("finance_amount") == null) ? "" : mapping.getProperty("finance_amount"));
        String valueeuro = (mapping.getProperty("finance_amount_euro") == null) ? "" : mapping.getProperty("finance_amount_euro");
        String valueeurocent = (mapping.getProperty("finance_amount_eurocent") == null) ? "" : mapping.getProperty("finance_amount_eurocent");
        this.amount = 0;
        if (!valueeuro.trim().isEmpty()) {
            //if (IntUtil.isInteger(valueeuro.trim())) {
            parseAmountEuro(valueeuro.trim());
            //}
        } else {
            if (!valueeurocent.trim().isEmpty()) {
                //if (IntUtil.isInteger(valueeurocent.trim())) {
                parseAmountEurocent(valueeurocent.trim());
                //}
            }
        }
        paysource = (mapping.getProperty("finance_source") == null) ? "" : mapping.getProperty("finance_source");
        orgbank = (mapping.getProperty("finance_aibankrekening") == null) ? "" : mapping.getProperty("finance_aibankrekening");
        paysource = (mapping.getProperty("finance_source") == null) ? "" : mapping.getProperty("finance_source");
        paytype =  (mapping.getProperty("finance_transtype") == null) ? "" : mapping.getProperty("finance_transtype");
        paymethod =  (mapping.getProperty("finance_paymethod") == null) ? "" : mapping.getProperty("finance_paymethod");
    }

    public void parseAmount(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        parseAmountGeneric(value, true);
    }

    public void parseAmountEuro(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        parseAmountGeneric(value, false);
    }

    public void parseAmountEurocent(String value) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        parseAmountGeneric(value, true);
    }

    public void parseAmountGeneric(String value, boolean iseurocents) {
        if (value == null) {
            return;
        }
        if (value.isEmpty()) {
            return;
        }
        String character;
        String numeric = "";
        String euro = "";
        String cent = "";
        boolean done = true;
        try {
            if (!iseurocents) {
                if (!value.contains(",") && !value.contains(".")) {
                    // geen eurocenten, dit lijkt niet nodig!
                    // value = value.concat("00");
                } else {
                    if (value.contains(",") && !done) {
                        euro = value.substring(0, value.indexOf(","));
                        cent = value.substring(value.indexOf(",") + 1);
                        done = true;
                    }
                    if (value.contains(".") && !done) {
                        euro = value.substring(0, value.indexOf("."));
                        cent = value.substring(value.indexOf(".") + 1);
                        done = true;
                    }
                    value = euro.concat(cent);
                }
            }
            Logger.getLogger(Commitment.class.getName()).log(Level.SEVERE, "DEBUG: "+value.toString());
            for (int i = 0; i < value.length(); i++) {
                character = String.valueOf(value.charAt(i));
                if ("0123456789".contains(character)) {
                    numeric = numeric.concat(character);
                }
            }
            Logger.getLogger(Commitment.class.getName()).log(Level.SEVERE, "DEBUG: "+numeric);
            if (isInteger(numeric)) {
                this.amountdue = Integer.parseInt(numeric);
            } else {
                this.amountdue = 0;
            }
        } catch (Exception e) {
            Logger.getLogger(Commitment.class.getName()).log(Level.SEVERE, e.getMessage(), e);
            this.amountdue = 0;
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

    public String getFormattedAmount() {
        int euro = (int) (amountdue / 100);
        int cent = (int) (amountdue % 100);
        String value = "Euro ".concat(String.valueOf(euro)).concat(",").concat(String.valueOf(cent));
        if (cent == 0) {
            value = value.concat("0");
        }
        return value;
    }

    public boolean isAcceptableMatch() {
        switch (this.getStatus()) {
            case STATUS_MATCHED_ID:
                // Finance is matched via id, so we are pretty sure we have got the right commitment.
                return true;
            default:
                return false;
        }

    }

    public int getStatusRecord() {
        return statusRecord;
    }

    public void setStatusRecord(int statusRecord) {
        this.statusRecord = statusRecord;
    }
}
