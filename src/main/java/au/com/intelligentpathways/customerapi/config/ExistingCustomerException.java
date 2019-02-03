package au.com.intelligentpathways.customerapi.config;

import au.com.intelligentpathways.customerapi.api.ApiException;

public class ExistingCustomerException extends ApiException {

    public ExistingCustomerException(int code, String msg) {
        super(code, msg);
    }
}
