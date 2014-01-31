package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMFlagException extends CRMException {

    public CRMFlagException() {
        super();
    }

    public CRMFlagException(String reason) {
        super(reason);
    }

    public CRMFlagException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
