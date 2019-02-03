package au.com.intelligentpathways.customerapi.api;

import au.com.intelligentpathways.customerapi.model.Customer;
import au.com.intelligentpathways.customerapi.service.CustomerProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2019-02-02T10:24:20.940+10:00")

@Controller
public class ProfileApiController implements ProfileApi {

    private static final Logger LOG = LoggerFactory.getLogger(ProfileApiController.class);

    private final ObjectMapper objectMapper;
    private final HttpServletRequest request;
    private CustomerProfileService customerProfileService;

    @Autowired
    public ProfileApiController(ObjectMapper objectMapper, HttpServletRequest request, CustomerProfileService customerProfileService) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.customerProfileService = customerProfileService;
    }

    public ResponseEntity<Void> addProfile(@ApiParam(value = "Customer object that needs to be added" ,required=true )  @Valid @RequestBody Customer body) {
        return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
    }

    public ResponseEntity<Void> deleteProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        try {
            customerProfileService.deleteProfile(username);
        } catch (NotFoundException e) {
            return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    public ResponseEntity<Customer> getProfile() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customerProfile = null;
        try {
            customerProfile = customerProfileService.getProfile(user.getUsername());
        } catch (NotFoundException e) {
            return new ResponseEntity<Customer>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<Customer>(customerProfile, HttpStatus.OK);
    }

    public ResponseEntity<Void> updateProfile(@ApiParam(value = "Customer object that needs to be updated" ,required=true )  @Valid @RequestBody Customer customer) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = user.getUsername();
        if (!validProfile(customer, username)) {
            return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
        } else {
            try {
                customerProfileService.updateProfile(customer);
            } catch (NotFoundException e) {
                return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<Void>(HttpStatus.OK);
    }

    private boolean validProfile(Customer body, String username) {
        return body != null && !StringUtils.isEmpty(body.getId()) && body.getId().equals(username);
    }

}
