package au.com.intelligentpathways.customerapi.integration;

import au.com.intelligentpathways.customerapi.model.Customer;

public interface CustomerCrmIntegration {

    void addProfile(Customer customer);

    Customer deleteProfile(String customerId);

    Customer getProfile(String customerId);

    Customer updateProfile(Customer customer);

}
