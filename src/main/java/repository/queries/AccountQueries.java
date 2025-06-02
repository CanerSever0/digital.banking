package repository.queries;

public final class AccountQueries {
    private AccountQueries() {
        throw new UnsupportedOperationException("This is a utility class");
    }

    public static final String INSERT_ACCOUNT = """
            INSERT INTO accounts (account_number, customer_id, balance, account_type, created_at, updated_at, is_active)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String SELECT_ALL_ACCOUNTS = """
            SELECT * FROM accounts
            ORDER BY created_at DESC
            """;

    public static final String SELECT_ACCOUNT_BY_ID = """
            SELECT * FROM accounts
            WHERE id = ?
            """;

    public static final String SELECT_ACCOUNT_BY_NUMBER = """
            SELECT * FROM accounts
            WHERE account_number = ? AND is_active = true
            """;

    public static final String SELECT_ACCOUNTS_BY_CUSTOMER_ID = """
            SELECT * FROM accounts
            WHERE customer_id = ? AND is_active = true 
            ORDER BY created_at DESC
            """;

    public static final String SELECT_ACCOUNTS_BY_TYPE = """
            SELECT * FROM accounts
            WHERE account_type = ? AND is_active = true
            """;

    public static final String SELECT_ACTIVE_ACCOUNTS = """
            SELECT * FROM accounts
            WHERE is_active = true 
            ORDER BY created_at DESC
            """;

    public static final String UPDATE_ACCOUNT = """
            UPDATE accounts
            SET customer_id = ?, balance = ?, account_type = ?, updated_at = ?, is_active = ?
            WHERE id = ?
            """;

    public static final String UPDATE_ACCOUNT_BALANCE = """
            UPDATE accounts
            SET balance = ?, updated_at = ? 
            WHERE account_number = ? AND is_active = true
            """;

    public static final String SOFT_DELETE_ACCOUNT = """
            UPDATE accounts
            SET is_active = false, updated_at = ? 
            WHERE id = ?
            """;

    public static final String COUNT_ACTIVE_ACCOUNTS = """
            SELECT COUNT(*) FROM accounts
            WHERE is_active = true
            """;

    public static final String EXISTS_ACCOUNT_BY_ID = """
            SELECT COUNT(*) FROM accounts
            WHERE id = ? AND is_active = true
            """;
}