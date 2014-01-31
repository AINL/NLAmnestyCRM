package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMProductException extends CRMException {

    public CRMProductException() {
        super();
    }

    public CRMProductException(String reason) {
        super(reason);
    }

    public CRMProductException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
