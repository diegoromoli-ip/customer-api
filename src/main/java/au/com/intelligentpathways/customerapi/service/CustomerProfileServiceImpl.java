package au.com.intelligentpathways.customerapi.service;

import au.com.intelligentpathways.customerapi.api.NotFoundException;
import au.com.intelligentpathways.customerapi.config.ExistingCustomerException;
import au.com.intelligentpathways.customerapi.integration.CustomerCrmIntegration;
import au.com.intelligentpathways.customerapi.model.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerProfileServiceImpl implements CustomerProfileService {

    private CustomerCrmIntegration customerCrmIntegration;

    @Autowired
    public CustomerProfileServiceImpl(CustomerCrmIntegration customerCrmIntegration) {
        this.customerCrmIntegration = customerCrmIntegration;
    }

    @Override
    public void createProfile(String username, Customer customer) throws ExistingCustomerException {
        try {
            Customer existingProfile = getProfile(username);
            if (existingProfile != null) {
                throw new ExistingCustomerException(400, "Customer profile already exists");
            }
        } catch (NotFoundException e) {
            // do nothing
        }
        customer.setId(username);
        customerCrmIntegration.addProfile(customer);
    }

    @Override
    public void deleteProfile(String customerId) throws NotFoundException {
        Customer deletedCustomer = customerCrmIntegration.deleteProfile(customerId);
        if (deletedCustomer == null) {
            throw new NotFoundException(404, "Customer profile not found");
        }
    }

    @Override
    public Customer getProfile(String customerId)throws NotFoundException {
        Customer retrievedCustomer = customerCrmIntegration.getProfile(customerId);
        if (retrievedCustomer == null) {
            throw new NotFoundException(404, "Customer profile not found");
        }
        return retrievedCustomer;
    }

    @Override
    public void updateProfile(Customer customer)throws NotFoundException {
        Customer updatedCustomer = customerCrmIntegration.updateProfile(customer);
        if (updatedCustomer == null) {
            throw new NotFoundException(404, "Customer profile not found");
        }
    }
}
