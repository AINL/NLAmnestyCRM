package nl.amnesty.crm.exception;

/**
 *
 * @author evelzen
 */
public class CRMBankaccountException extends CRMException {

    public CRMBankaccountException() {
        super();
    }

    public CRMBankaccountException(String reason) {
        super(reason);
    }

    public CRMBankaccountException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
