package repository;

public interface BaseWriteRepository<T, ID> {
    T save(T entity);
}
