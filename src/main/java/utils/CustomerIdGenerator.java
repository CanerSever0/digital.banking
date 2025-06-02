package utils;

import lombok.Getter;

@Getter
public class CustomerIdGenerator {
    private static final String PREFIX = "CUST";
    private int lastNumber;

    public CustomerIdGenerator(String lastCustomerId) {
        if (lastCustomerId == null || !lastCustomerId.startsWith(PREFIX)) {
            throw new IllegalArgumentException("Invalid CustomerID " + lastCustomerId);
        }
        String numericPart = lastCustomerId.substring(PREFIX.length());
        try {
            this.lastNumber = Integer.parseInt(numericPart);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Customer ID cannot read: " + numericPart, e);
        }
    }

    public String getNextId() {
        lastNumber++;
        String formattedNumber = String.format("%04d", lastNumber);
        return PREFIX + formattedNumber;
    }

}