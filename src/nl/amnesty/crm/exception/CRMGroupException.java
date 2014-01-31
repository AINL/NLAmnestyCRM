package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMGroupException extends CRMException {

    public CRMGroupException() {
        super();
    }

    public CRMGroupException(String reason) {
        super(reason);
    }

    public CRMGroupException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
