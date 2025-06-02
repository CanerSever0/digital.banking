package repository.queries;

public final class CustomerQueries {

    public static final String INSERT_CUSTOMER = """
            INSERT INTO customers (customer_id, first_name, last_name, email, 
                                 phone_number, national_id, created_at, updated_at, is_active) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String UPDATE_CUSTOMER = """
            UPDATE customers 
            SET first_name = ?, last_name = ?, email = ?, phone_number = ?, 
                updated_at = ?, is_active = ? 
            WHERE id = ?
            """;

    public static final String SELECT_CUSTOMER_BY_ID = """
            SELECT * FROM customers WHERE id = ?
            """;

    public static final String SELECT_CUSTOMER_BY_CUSTOMER_ID = """
            SELECT * FROM customers WHERE customer_id = ?
            """;

    public static final String SELECT_CUSTOMER_BY_EMAIL = """
            SELECT * FROM customers WHERE email = ?
            """;

    public static final String SELECT_CUSTOMER_BY_NATIONAL_ID = """
            SELECT * FROM customers WHERE national_id = ?
            """;

    public static final String SELECT_ACTIVE_CUSTOMERS = """
            SELECT * FROM customers 
            WHERE is_active = true 
            ORDER BY created_at DESC
            """;

    public static final String SELECT_ALL_CUSTOMERS = """
            SELECT * FROM customers 
            ORDER BY created_at DESC
            """;

    public static final String SOFT_DELETE_CUSTOMER = """
            UPDATE customers 
            SET is_active = false, updated_at = ? 
            WHERE id = ?
            """;

    public static final String EXISTS_CUSTOMER_BY_ID = """
            SELECT COUNT(*) FROM customers WHERE id = ?
            """;

    public static final String COUNT_ACTIVE_CUSTOMERS = """
            SELECT COUNT(*) FROM customers WHERE is_active = true
            """;

    public static final String EXISTS_CUSTOMER_BY_EMAIL = """
            SELECT COUNT(*) FROM customers WHERE email = ? AND is_active = true
            """;

    public static final String EXISTS_CUSTOMER_BY_NATIONAL_ID = """
            SELECT COUNT(*) FROM customers WHERE national_id = ? AND is_active = true
            """;

    public static final String SELECT_GET_LAST_CUSTOMER = """
            SELECT * FROM customers ORDER BY id DESC
            LIMIT 1;
            """;
}