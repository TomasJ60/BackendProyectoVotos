package co.edu.unipiloto.edu.proyectoVotos.tareas;

import co.edu.unipiloto.edu.proyectoVotos.dao.*;
import co.edu.unipiloto.edu.proyectoVotos.model.Procesodevotacion;
import java.util.List;
import org.springframework.beans.factory.annotation.*;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.*;

/**
 *
 * @author tomas
 */
@Component
public class TareaProgramada {

    @Autowired
    private ProcesoRepository procesoRepository;

    @Scheduled(cron = "0 0 0 * * *") // Se ejecuta todos los días a la medianoche
    public void verificarEstadoVotacion() {
        List<Procesodevotacion> procesos = procesoRepository.findAll();
        for (Procesodevotacion proceso : procesos) {
            proceso.checkEstado();
            procesoRepository.save(proceso);
        }
    }
}

