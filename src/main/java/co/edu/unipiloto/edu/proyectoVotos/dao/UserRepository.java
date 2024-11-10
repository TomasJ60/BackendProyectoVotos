package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.User;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.*;

/**
 *
 * @author Tomas Alvarez
 */
public interface UserRepository extends CrudRepository<User, Integer> {
    List<User> findAll();

    Boolean existsByIdAndActivo(Integer id, Boolean activo);

}
