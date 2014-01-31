package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMInvolvementException extends CRMException {

    public CRMInvolvementException() {
        super();
    }

    public CRMInvolvementException(String reason) {
        super(reason);
    }

    public CRMInvolvementException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
