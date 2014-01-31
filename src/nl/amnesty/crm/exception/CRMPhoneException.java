package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMPhoneException extends CRMException {

    public CRMPhoneException() {
        super();
    }

    public CRMPhoneException(String reason) {
        super(reason);
    }

    public CRMPhoneException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
