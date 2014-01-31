/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.exception;

/**
 *
 * @author bmenting
 */
public class CRMFinanceException extends CRMException {
      public CRMFinanceException() {
        super();
    }

    public CRMFinanceException(String reason) {
        super(reason);
    }

    public CRMFinanceException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
