package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.Proyecto;
import co.edu.unipiloto.edu.proyectoVotos.model.User;
import co.edu.unipiloto.edu.proyectoVotos.model.Voto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author tomas
 */
public interface VotoRepository extends JpaRepository<Voto, Integer> {
    List<Voto> findByProyecto(Proyecto proyecto);
    List<Voto> findByCiudadano(User ciudadano);
}

