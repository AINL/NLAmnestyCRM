/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.amnesty.crm.exception;

/**
 *
 * @author bmenting
 */
public class CRMOrganizationException extends CRMException {

    public CRMOrganizationException() {
        super();
    }

    public CRMOrganizationException(String reason) {
        super(reason);
    }

    public CRMOrganizationException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
