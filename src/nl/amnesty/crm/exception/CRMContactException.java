package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMContactException extends CRMException {

    public CRMContactException() {
        super();
    }

    public CRMContactException(String reason) {
        super(reason);
    }

    public CRMContactException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
