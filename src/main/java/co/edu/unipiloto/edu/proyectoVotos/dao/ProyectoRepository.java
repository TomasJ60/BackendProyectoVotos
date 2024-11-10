package co.edu.unipiloto.edu.proyectoVotos.dao;

import co.edu.unipiloto.edu.proyectoVotos.model.Proyecto;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.*;

/**
 *
 * @author tomas
 */
public interface ProyectoRepository extends CrudRepository<Proyecto, Integer> {
    List<Proyecto> findByLocalidad(String localidad);
    
    boolean existsByIdentificador(Integer identificador);
    
    Optional<Proyecto> findByIdAndLocalidad(Integer id, String localidad);
    
    List<Proyecto> findAll();
    Optional<Proyecto> findByNombreProyecto(String nombreProyecto);
    
    default boolean identificadorEnUso(Integer identificador) {
        return existsByIdentificador(identificador);
    }
    
    default boolean tienePermiso(String rolDescripcion, String permiso) {
        switch (rolDescripcion.toLowerCase()) {
            case "ciudadano":
                return permiso.equals("votar") || permiso.equals("verProyectos") || permiso.equals("verificarProceso");
            case "planeador":
                return permiso.equals("crearProyecto") || permiso.equals("crearTipoProyecto") || permiso.equals("crearProcesoVotacion");
            case "decisor":
                return permiso.equals("consultarVotos") || permiso.equals("verResultados") || permiso.equals("filtrarPorLocalidad");
            default:
                return false;
        }
    }
}

