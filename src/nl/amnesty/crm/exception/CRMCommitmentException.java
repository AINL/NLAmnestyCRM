package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMCommitmentException extends CRMException {

    public CRMCommitmentException() {
        super();
    }

    public CRMCommitmentException(String reason) {
        super(reason);
    }

    public CRMCommitmentException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
