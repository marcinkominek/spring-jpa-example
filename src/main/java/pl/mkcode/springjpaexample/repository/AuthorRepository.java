package pl.mkcode.springjpaexample.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.mkcode.springjpaexample.model.Author;

@Repository
public interface AuthorRepository extends CrudRepository<Author, Long> {
}
