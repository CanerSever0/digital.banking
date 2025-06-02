package repository;

public interface DeletableRepository<ID> {
    void deleteById(ID id);
}
