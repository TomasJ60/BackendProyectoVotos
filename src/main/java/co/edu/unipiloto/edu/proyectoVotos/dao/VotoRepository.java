package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.*;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author tomas
 */
public interface VotoRepository extends JpaRepository<Voto, Integer> {
    List<Voto> findByProyecto(Proyecto proyecto);
    List<Voto> findByCiudadano(User ciudadano);
    Optional<Voto> findByCiudadanoAndProyecto(User ciudadano, Proyecto proyectoId);

}

