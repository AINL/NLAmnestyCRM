package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMNetworkException extends CRMException {

    public CRMNetworkException() {
        super();
    }

    public CRMNetworkException(String reason) {
        super(reason);
    }

    public CRMNetworkException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
