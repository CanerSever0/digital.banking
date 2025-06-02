package repository.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import model.entity.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import repository.CustomerRepository;
import repository.queries.CustomerQueries;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomerRepositoryImpl implements CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) ->
            Customer.builder()
                    .id(rs.getLong("id"))
                    .customerId(rs.getString("customer_id"))
                    .firstName(rs.getString("first_name"))
                    .lastName(rs.getString("last_name"))
                    .email(rs.getString("email"))
                    .phoneNumber(rs.getString("phone_number"))
                    .nationalId(rs.getString("national_id"))
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                    .isActive(rs.getBoolean("is_active"))
                    .build();

    @Override
    public Customer save(Customer customer) {
        if (customer.getId() == null) {
            LocalDateTime now = LocalDateTime.now();
            customer.setCreatedAt(now);
            customer.setUpdatedAt(now);

            jdbcTemplate.update(CustomerQueries.INSERT_CUSTOMER,
                    customer.getCustomerId(),
                    customer.getFirstName(),
                    customer.getLastName(),
                    customer.getEmail(),
                    customer.getPhoneNumber(),
                    customer.getNationalId(),
                    customer.getCreatedAt(),
                    customer.getUpdatedAt(),
                    customer.getIsActive()
            );

            log.info("New customer created: {}", customer.getCustomerId());
        } else {
            update(customer);
        }
        return customer;
    }

    @Override
    public Optional<Customer> findById(Long id) {
        try {
            Customer customer = jdbcTemplate.queryForObject(
                    CustomerQueries.SELECT_CUSTOMER_BY_ID,
                    customerRowMapper,
                    id
            );
            return Optional.of(customer);
        } catch (Exception e) {
            log.warn("Customer not found - ID: {}", id);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByCustomerId(String customerId) {
        try {
            Customer customer = jdbcTemplate.queryForObject(
                    CustomerQueries.SELECT_CUSTOMER_BY_CUSTOMER_ID,
                    customerRowMapper,
                    customerId
            );
            return Optional.of(customer);
        } catch (Exception e) {
            log.warn("Customer not found - Customer ID: {}", customerId);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        try {
            Customer customer = jdbcTemplate.queryForObject(
                    CustomerQueries.SELECT_CUSTOMER_BY_EMAIL,
                    customerRowMapper,
                    email
            );
            return Optional.of(customer);
        } catch (Exception e) {
            log.warn("Customer not found - Email: {}", email);
            return Optional.empty();
        }
    }

    @Override
    public Optional<Customer> findByNationalId(String nationalId) {
        try {
            Customer customer = jdbcTemplate.queryForObject(
                    CustomerQueries.SELECT_CUSTOMER_BY_NATIONAL_ID,
                    customerRowMapper,
                    nationalId
            );
            return Optional.of(customer);
        } catch (Exception e) {
            log.warn("Customer not found - National ID: {}", nationalId);
            return Optional.empty();
        }
    }

    @Override
    public List<Customer> findActiveCustomers() {
        return jdbcTemplate.query(CustomerQueries.SELECT_ACTIVE_CUSTOMERS, customerRowMapper);
    }

    @Override
    public String getLastCustomerId() {
        Customer customer = jdbcTemplate.queryForObject(
                CustomerQueries.SELECT_GET_LAST_CUSTOMER,
                customerRowMapper
        );
        return customer != null ? customer.getCustomerId() : null;
    }

    @Override
    public List<Customer> findAll() {
        return jdbcTemplate.query(CustomerQueries.SELECT_ALL_CUSTOMERS, customerRowMapper);
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.update(CustomerQueries.SOFT_DELETE_CUSTOMER, LocalDateTime.now(), id);
        log.info("Customer deactivated - ID: {}", id);
    }

    @Override
    public boolean existsById(Long id) {
        Integer count = jdbcTemplate.queryForObject(CustomerQueries.EXISTS_CUSTOMER_BY_ID, Integer.class, id);
        return count != null && count > 0;
    }

    @Override
    public long count() {
        Integer count = jdbcTemplate.queryForObject(CustomerQueries.COUNT_ACTIVE_CUSTOMERS, Integer.class);
        return count != null ? count : 0;
    }

    public boolean isEmailExists(String email) {
        Integer count = jdbcTemplate.queryForObject(CustomerQueries.EXISTS_CUSTOMER_BY_EMAIL, Integer.class, email);
        return count != null && count > 0;
    }

    public boolean isNationalIdExists(String nationalId) {
        Integer count = jdbcTemplate.queryForObject(CustomerQueries.EXISTS_CUSTOMER_BY_NATIONAL_ID, Integer.class, nationalId);
        return count != null && count > 0;
    }

    private void update(Customer customer) {
        customer.setUpdatedAt(LocalDateTime.now());

        jdbcTemplate.update(CustomerQueries.UPDATE_CUSTOMER,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail(),
                customer.getPhoneNumber(),
                customer.getUpdatedAt(),
                customer.getIsActive(),
                customer.getId()
        );

        log.info("Customer updated - ID: {}", customer.getId());
    }
}