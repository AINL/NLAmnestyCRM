package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMException extends java.lang.Exception {

    public CRMException() {
        super();
    }

    public CRMException(String reason) {
        super(reason);
    }

    public CRMException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
