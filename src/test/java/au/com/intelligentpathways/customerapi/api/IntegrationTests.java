package au.com.intelligentpathways.customerapi.api;

import au.com.intelligentpathways.customerapi.integration.CustomerCrmIntegrationMockImpl;
import au.com.intelligentpathways.customerapi.integration.CustomerMockBuilder;
import au.com.intelligentpathways.customerapi.model.Customer;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class IntegrationTests {

    private static final String URI = "/profile";
    private static final String CUSTOMER_1 = "customer1";
    private static final String PASSWORD_1 = "password1";
    private static final String CUSTOMER_4 = "customer4";
    private static final String PASSWORD_4 = "password4";

    @Autowired
    private WebApplicationContext webApplicationContext;
    @Autowired
    private CustomerCrmIntegrationMockImpl customerCrmIntegrationMockImpl;
    @Autowired
    private ObjectMapper objectMapper;
    private MockMvc mvc;

    @Before
    public void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).apply(springSecurity()).build();
        this.customerCrmIntegrationMockImpl.initialize();
    }

    @Test
    public void testGetProfileWithBadCredentials() {
        try {
            mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_1, "wrong-password"))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

    @Test
    public void testGetProfile() {
        try {
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(mvcResult.getResponse().getContentAsString().contains("\"id\":\"customer4\""));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

    @Test
    public void testUpdateProfile() {
        try {
            // Get original profile
            MvcResult initialMvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(initialMvcResult.getResponse().getContentAsString().contains("\"id\":\"customer4\""));

            // Update profile
            Customer modifiedCustomer4 = CustomerMockBuilder.getCustomerWithId(CUSTOMER_4);
            modifiedCustomer4.setLastName("modified-last-name");
            mvc.perform(MockMvcRequestBuilders.put(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .content(objectMapper.writeValueAsBytes(modifiedCustomer4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Get updated profile
            MvcResult finalMvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(finalMvcResult.getResponse().getContentAsString().contains("\"lastName\":\"modified-last-name" +
                    "\""));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

    @Test
    public void testUpdateProfileWithWrongCustomerId() {
        try {
            // Get original profile
            MvcResult initialMvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(initialMvcResult.getResponse().getContentAsString().contains("\"lastName\":\"lastName" +
                    "-customer4\""));

            // Update profile
            Customer modifiedCustomer4 = CustomerMockBuilder.getCustomerWithId(CUSTOMER_4);
            modifiedCustomer4.setLastName("modified-last-name");
            modifiedCustomer4.setId("a-different-customer");
            mvc.perform(MockMvcRequestBuilders.put(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .content(objectMapper.writeValueAsBytes(modifiedCustomer4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();

            // Get unmodified profile
            MvcResult finalMvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(finalMvcResult.getResponse().getContentAsString().contains("\"lastName\":\"lastName-customer4" +
                    "\""));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

    @Test
    public void testDeleteProfile() {
        try {
            // Get original profile
            MvcResult initialMvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(initialMvcResult.getResponse().getContentAsString().contains("\"id\":\"customer4\""));

            // Delete profile
            mvc.perform(MockMvcRequestBuilders.delete(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Verify profile does not exist
            mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_4, PASSWORD_4))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

    @Test
    public void testAddProfile() {
        try {
            // Verify that customer profile does not exist
            mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            // Create customer profile
            Customer customer = CustomerMockBuilder.getCustomerWithId(CUSTOMER_1);
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.post(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(customer))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            LoggerFactory.getLogger(IntegrationTests.class).info("Returned customer from addProfile: {}", mvcResult.getResponse().getContentAsString());

            // Verify that customer profile does exist
            mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(mvcResult.getResponse().getContentAsString().contains("\"id\":\"customer1\""));
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

    @Test
    public void testAddExistingProfile() {
        try {
            // Verify that customer profile does not exist
            mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound())
                    .andReturn();

            // Create customer profile
            Customer customer = CustomerMockBuilder.getCustomerWithId(CUSTOMER_1);
            mvc.perform(MockMvcRequestBuilders.post(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(customer))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();

            // Verify that customer profile does exist
            MvcResult mvcResult = mvc.perform(MockMvcRequestBuilders.get(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
            assertTrue(mvcResult.getResponse().getContentAsString().contains("\"id\":\"customer1\""));

            // Try to create existing profile again
            mvc.perform(MockMvcRequestBuilders.post(URI)
                    .with(httpBasic(CUSTOMER_1, PASSWORD_1))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(customer))
                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (Exception e) {
            assertTrue(e.getCause() instanceof AccessDeniedException);
        }
    }

}
