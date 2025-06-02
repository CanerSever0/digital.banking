package service;

import model.dto.request.CreateCustomerRequest;
import model.dto.request.UpdateCustomerRequest;
import model.dto.response.CustomerResponse;

import java.util.List;

public interface CustomerService {
    CustomerResponse createCustomer(CreateCustomerRequest request);

    CustomerResponse getCustomerById(String customerId);

    CustomerResponse getCustomerByEmail(String email);

    CustomerResponse updateCustomer(String customerId, UpdateCustomerRequest request);

    void deactivateCustomer(String customerId);

    List<CustomerResponse> getAllActiveCustomers();

    boolean isEmailAvailable(String email);

    boolean isNationalIdAvailable(String nationalId);
}