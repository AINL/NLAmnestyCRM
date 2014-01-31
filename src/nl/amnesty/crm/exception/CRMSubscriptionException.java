package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMSubscriptionException extends CRMException {

    public CRMSubscriptionException() {
        super();
    }

    public CRMSubscriptionException(String reason) {
        super(reason);
    }

    public CRMSubscriptionException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
