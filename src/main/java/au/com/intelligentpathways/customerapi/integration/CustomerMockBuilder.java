package au.com.intelligentpathways.customerapi.integration;

import au.com.intelligentpathways.customerapi.model.Address;
import au.com.intelligentpathways.customerapi.model.Customer;
import org.springframework.util.StringUtils;
import org.threeten.bp.LocalDate;

import java.util.Arrays;

public class CustomerMockBuilder {

    private static Address getAddressWithSuffix(Address.AddressTypeEnum addressType, String customerId) {
        Address address = new Address();
        address.setAddress1("Address1-" + customerId);
        address.setAddress2("Address2-" + customerId);
        address.setAddressType(addressType);
        address.setEmail("email@address-" + customerId + ".com");
        return address;
    }

    public static Customer getCustomerWithId(String customerId) {
        if (StringUtils.isEmpty(customerId)) {
            throw new IllegalArgumentException("Customer ID must be supplied");
        }
        Address homeAddress = getAddressWithSuffix(Address.AddressTypeEnum.HOME, customerId);
        Address officeAddress = getAddressWithSuffix(Address.AddressTypeEnum.OFFICE, customerId);
        Customer customer = new Customer();
        customer.setId(customerId);
        customer.setAddresses(Arrays.asList(homeAddress, officeAddress));
        customer.setFirstName("firstName-" + customerId);
        customer.setLastName("lastName-" + customerId);
        customer.setDateOfBirth(LocalDate.of(1980,1,1));
        return customer;
    }

}
