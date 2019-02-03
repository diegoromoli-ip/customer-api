package au.com.intelligentpathways.customerapi.service;

import au.com.intelligentpathways.customerapi.api.NotFoundException;
import au.com.intelligentpathways.customerapi.model.Customer;

public interface CustomerProfileService {

    void addProfile(Customer customer);

    void deleteProfile(String customerId) throws NotFoundException;

    Customer getProfile(String customerId) throws NotFoundException;

    void updateProfile(Customer customer) throws NotFoundException;

}
