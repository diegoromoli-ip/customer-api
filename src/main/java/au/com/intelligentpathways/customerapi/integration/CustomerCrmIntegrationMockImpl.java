package au.com.intelligentpathways.customerapi.integration;

import au.com.intelligentpathways.customerapi.model.Customer;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomerCrmIntegrationMockImpl implements CustomerCrmIntegration {

    private static Map<String, Customer> customers;

    @PostConstruct
    public void initialize() {
        customers = new HashMap<String, Customer>() {{
            put("customer1", CustomerMockBuilder.getCustomerWithId("customer1"));
            put("customer2", CustomerMockBuilder.getCustomerWithId("customer2"));
            put("customer3", CustomerMockBuilder.getCustomerWithId("customer3"));
            put("customer4", CustomerMockBuilder.getCustomerWithId("customer4"));
        }};
    }

    @Override
    public void addProfile(Customer customer) {
        customers.put(customer.getId(), customer);
    }

    @Override
    public Customer deleteProfile(String customerId) {
        return customers.remove(customerId);
    }

    @Override
    public Customer getProfile(String customerId) {
        return customers.get(customerId);
    }

    @Override
    public Customer updateProfile(Customer customer) {
        return customers.replace(customer.getId(), customer);
    }

}
