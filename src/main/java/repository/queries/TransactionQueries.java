package repository.queries;

public final class TransactionQueries {

    public static final String INSERT_TRANSACTION = """
            INSERT INTO transactions (transaction_id, from_account_number, to_account_number, 
                                    amount, transaction_type, description, transaction_date, status) 
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

    public static final String SELECT_TRANSACTION_BY_ID = """
            SELECT * FROM transactions WHERE id = ?
            """;

    public static final String SELECT_TRANSACTION_BY_TRANSACTION_ID = """
            SELECT * FROM transactions WHERE transaction_id = ?
            """;

    public static final String SELECT_TRANSACTIONS_BY_ACCOUNT = """
            SELECT * FROM transactions 
            WHERE from_account_number = ? OR to_account_number = ? 
            ORDER BY transaction_date DESC
            """;

    public static final String SELECT_TRANSACTIONS_BY_DATE_RANGE = """
            SELECT * FROM transactions 
            WHERE transaction_date BETWEEN ? AND ? 
            ORDER BY transaction_date DESC
            """;

    public static final String SELECT_TRANSACTIONS_BY_STATUS = """
            SELECT * FROM transactions 
            WHERE status = ? 
            ORDER BY transaction_date DESC
            """;

    public static final String UPDATE_TRANSACTION_STATUS = """
            UPDATE transactions 
            SET status = ? 
            WHERE transaction_id = ?
            """;

    public static final String SELECT_ALL_TRANSACTIONS = """
            SELECT * FROM transactions 
            ORDER BY transaction_date DESC
            """;

    public static final String EXISTS_TRANSACTION_BY_ID = """
            SELECT COUNT(*) FROM transactions WHERE id = ?
            """;

    public static final String COUNT_ALL_TRANSACTIONS = """
            SELECT COUNT(*) FROM transactions
            """;
}