package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Tomas Alvarez
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findAll();
}

