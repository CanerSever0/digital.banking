package model.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {

    @NotBlank(message = "Customer ID is required")
    @Size(min = 1, max = 50, message = "Customer ID must be between 1 and 50 characters")
    private String customerId;

    @NotBlank(message = "Account type is required")
    @Pattern(regexp = "CHECKING|SAVINGS|BUSINESS", message = "Account type must be CHECKING, SAVINGS, or BUSINESS")
    private String accountType;

    @DecimalMin(value = "0.0", inclusive = true, message = "Initial balance cannot be negative")
    @Digits(integer = 13, fraction = 2, message = "Invalid balance format")
    private BigDecimal initialBalance;

    private String description;
}