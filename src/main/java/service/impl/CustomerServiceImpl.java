package service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.dto.request.CreateCustomerRequest;
import model.dto.request.UpdateCustomerRequest;
import model.dto.response.CustomerResponse;
import model.entity.Customer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import repository.CustomerRepository;
import repository.impl.CustomerRepositoryImpl;
import service.CustomerService;
import utils.CustomerIdGenerator;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerRepositoryImpl customerRepositoryImpl;

    @Override
    public CustomerResponse createCustomer(CreateCustomerRequest request) {
        validateCreateCustomerRequest(request);

        if (customerRepositoryImpl.isEmailExists(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + request.getEmail());
        }

        if (customerRepositoryImpl.isNationalIdExists(request.getNationalId())) {
            throw new IllegalArgumentException("National ID already exists: " + request.getNationalId());
        }

        String customerId = generateCustomerId();

        Customer customer = Customer.builder()
                .customerId(customerId)
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .nationalId(request.getNationalId())
                .isActive(true)
                .build();

        Customer savedCustomer = customerRepository.save(customer);

        log.info("New customer created - Customer ID: {}, Email: {}", customerId, request.getEmail());

        return mapToCustomerResponse(savedCustomer);
    }

    @Override
    public CustomerResponse getCustomerById(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        return mapToCustomerResponse(customer);
    }

    @Override
    public CustomerResponse getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Customer not found with email: " + email));

        return mapToCustomerResponse(customer);
    }

    @Override
    public CustomerResponse updateCustomer(String customerId, UpdateCustomerRequest request) {
        validateUpdateCustomerRequest(request);

        Customer existingCustomer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        if (!existingCustomer.getEmail().equals(request.getEmail())) {
            if (customerRepositoryImpl.isEmailExists(request.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + request.getEmail());
            }
        }

        Customer updatedCustomer = Customer.builder()
                .id(existingCustomer.getId())
                .customerId(existingCustomer.getCustomerId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .nationalId(existingCustomer.getNationalId())
                .createdAt(existingCustomer.getCreatedAt())
                .isActive(existingCustomer.getIsActive())
                .build();

        Customer savedCustomer = customerRepository.save(updatedCustomer);

        log.info("Customer updated - Customer ID: {}", customerId);

        return mapToCustomerResponse(savedCustomer);
    }

    @Override
    public void deactivateCustomer(String customerId) {
        Customer customer = customerRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found: " + customerId));

        customerRepository.deleteById(customer.getId());
        log.info("Customer deactivated: {}", customerId);
    }

    @Override
    public List<CustomerResponse> getAllActiveCustomers() {
        return customerRepository.findActiveCustomers()
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean isEmailAvailable(String email) {
        return !customerRepositoryImpl.isEmailExists(email);
    }

    @Override
    public boolean isNationalIdAvailable(String nationalId) {
        return !customerRepositoryImpl.isNationalIdExists(nationalId);
    }

    private void validateCreateCustomerRequest(CreateCustomerRequest request) {
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }
        if (request.getNationalId() == null || request.getNationalId().trim().isEmpty()) {
            throw new IllegalArgumentException("National ID is required");
        }

        if (!request.getNationalId().matches("\\d{11}")) {
            throw new IllegalArgumentException("National ID must be 11 digits");
        }

        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private void validateUpdateCustomerRequest(UpdateCustomerRequest request) {
        if (request.getFirstName() == null || request.getFirstName().trim().isEmpty()) {
            throw new IllegalArgumentException("First name is required");
        }
        if (request.getLastName() == null || request.getLastName().trim().isEmpty()) {
            throw new IllegalArgumentException("Last name is required");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email is required");
        }

        if (!request.getEmail().contains("@")) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }

    private String generateCustomerId() {
        String getLastCustomerId = customerRepository.getLastCustomerId();

        CustomerIdGenerator generator = new CustomerIdGenerator(getLastCustomerId);
        return generator.getNextId();
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .customerId(customer.getCustomerId())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phoneNumber(customer.getPhoneNumber())
                .nationalId(customer.getNationalId())
                .createdAt(customer.getCreatedAt())
                .isActive(customer.getIsActive())
                .build();
    }
}