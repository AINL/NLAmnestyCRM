package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMPersonException extends CRMException {

    public CRMPersonException() {
        super();
    }

    public CRMPersonException(String reason) {
        super(reason);
    }

    public CRMPersonException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
