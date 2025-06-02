package repository;

import model.entity.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepository extends BaseReadRepository<Customer, Long>, BaseWriteRepository<Customer, Long>, DeletableRepository<Long> {
    Optional<Customer> findByCustomerId(String customerId);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByNationalId(String nationalId);

    List<Customer> findActiveCustomers();

    String getLastCustomerId();
}