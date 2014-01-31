package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMAddressException extends CRMException {

    public CRMAddressException() {
        super();
    }

    public CRMAddressException(String reason) {
        super(reason);
    }

    public CRMAddressException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
