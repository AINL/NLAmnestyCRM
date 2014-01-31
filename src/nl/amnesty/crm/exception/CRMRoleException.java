package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMRoleException extends CRMException {

    public CRMRoleException() {
        super();
    }

    public CRMRoleException(String reason) {
        super(reason);
    }

    public CRMRoleException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
