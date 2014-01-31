package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMURLException extends CRMException {

    public CRMURLException() {
        super();
    }

    public CRMURLException(String reason) {
        super(reason);
    }

    public CRMURLException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
