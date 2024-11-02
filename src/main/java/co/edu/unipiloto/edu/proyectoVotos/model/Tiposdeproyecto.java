package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.*;
import lombok.*;

/**
 *
 * @author tomas
 */
@Entity(name="TIPODEPROYECTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Tiposdeproyecto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer identificador;
    private String nombredeltipo;
    private String descripcion;
}
