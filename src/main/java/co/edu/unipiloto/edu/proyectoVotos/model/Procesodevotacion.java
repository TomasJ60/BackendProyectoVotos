package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.*;
import lombok.*;

/**
 *
 * @author tomas
 */

@Entity(name="PROCESODEVOTACION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Procesodevotacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    private Integer identificador;
    private String fechadeinicio;
    private String fechadecierre;
    private String estado;
    
}
