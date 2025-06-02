package controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.dto.request.CreateCustomerRequest;
import model.dto.request.UpdateCustomerRequest;
import model.dto.response.ApiResponse;
import model.dto.response.CustomerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import service.CustomerService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Customer Management", description = "Customer operations API")
public class CustomerController {

    private final CustomerService customerService;

    @PostMapping
    @Operation(summary = "Create new customer", description = "Creates a new customer account")
    public ResponseEntity<ApiResponse<CustomerResponse>> createCustomer(
            @Valid @RequestBody CreateCustomerRequest request) {

        log.info("Creating new customer - Email: {}", request.getEmail());

        try {
            CustomerResponse customer = customerService.createCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(customer, "Customer created successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Customer creation failed - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (Exception e) {
            log.error("Customer creation failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Customer creation failed", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/{customerId}")
    @Operation(summary = "Get customer by ID", description = "Retrieves customer information by customer ID")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {

        log.info("Getting customer - Customer ID: {}", customerId);

        try {
            CustomerResponse customer = customerService.getCustomerById(customerId);
            return ResponseEntity.ok(ApiResponse.success(customer));
        } catch (RuntimeException e) {
            log.warn("Customer not found - Customer ID: {}", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Customer not found", "CUSTOMER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting customer - Customer ID: {}", customerId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customer", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get customer by email", description = "Retrieves customer information by email address")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerByEmail(
            @Parameter(description = "Customer email address") @PathVariable String email) {

        log.info("Getting customer by email: {}", email);

        try {
            CustomerResponse customer = customerService.getCustomerByEmail(email);
            return ResponseEntity.ok(ApiResponse.success(customer));
        } catch (RuntimeException e) {
            log.warn("Customer not found - Email: {}", email);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Customer not found", "CUSTOMER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Error getting customer by email: {}", email);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customer", "SYSTEM_ERROR"));
        }
    }

    @PutMapping("/{customerId}")
    @Operation(summary = "Update customer", description = "Updates customer information")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId,
            @Valid @RequestBody UpdateCustomerRequest request) {

        log.info("Updating customer - Customer ID: {}", customerId);

        try {
            CustomerResponse customer = customerService.updateCustomer(customerId, request);
            return ResponseEntity.ok(ApiResponse.success(customer, "Customer updated successfully"));
        } catch (IllegalArgumentException e) {
            log.warn("Customer update failed - Validation error: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "VALIDATION_ERROR"));
        } catch (RuntimeException e) {
            log.warn("Customer update failed - Customer not found: {}", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Customer not found", "CUSTOMER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Customer update failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Customer update failed", "SYSTEM_ERROR"));
        }
    }

    @DeleteMapping("/{customerId}")
    @Operation(summary = "Deactivate customer", description = "Deactivates a customer account (soft delete)")
    public ResponseEntity<ApiResponse<Void>> deactivateCustomer(
            @Parameter(description = "Customer ID") @PathVariable String customerId) {

        log.info("Deactivating customer - Customer ID: {}", customerId);

        try {
            customerService.deactivateCustomer(customerId);
            return ResponseEntity.ok(ApiResponse.success(null, "Customer deactivated successfully"));
        } catch (RuntimeException e) {
            log.warn("Customer deactivation failed - Customer not found: {}", customerId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("Customer not found", "CUSTOMER_NOT_FOUND"));
        } catch (Exception e) {
            log.error("Customer deactivation failed - System error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Customer deactivation failed", "SYSTEM_ERROR"));
        }
    }

    @GetMapping
    @Operation(summary = "Get all active customers", description = "Retrieves all active customers")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getAllActiveCustomers() {

        log.info("Getting all active customers");

        try {
            List<CustomerResponse> customers = customerService.getAllActiveCustomers();
            return ResponseEntity.ok(ApiResponse.success(customers,
                    customers.size() + " active customers found"));
        } catch (Exception e) {
            log.error("Error getting active customers: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to retrieve customers", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/check-email/{email}")
    @Operation(summary = "Check email availability", description = "Checks if email address is available")
    public ResponseEntity<ApiResponse<Boolean>> checkEmailAvailability(
            @Parameter(description = "Email address to check") @PathVariable String email) {

        log.info("Checking email availability: {}", email);

        try {
            boolean isAvailable = customerService.isEmailAvailable(email);
            String message = isAvailable ? "Email is available" : "Email is already taken";
            return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
        } catch (Exception e) {
            log.error("Error checking email availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check email availability", "SYSTEM_ERROR"));
        }
    }

    @GetMapping("/check-national-id/{nationalId}")
    @Operation(summary = "Check national ID availability", description = "Checks if national ID is available")
    public ResponseEntity<ApiResponse<Boolean>> checkNationalIdAvailability(
            @Parameter(description = "National ID to check") @PathVariable String nationalId) {

        log.info("Checking national ID availability: {}", nationalId);

        try {
            boolean isAvailable = customerService.isNationalIdAvailable(nationalId);
            String message = isAvailable ? "National ID is available" : "National ID is already registered";
            return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
        } catch (Exception e) {
            log.error("Error checking national ID availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("Failed to check national ID availability", "SYSTEM_ERROR"));
        }
    }
}