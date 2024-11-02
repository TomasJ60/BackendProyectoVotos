package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.*;
import lombok.*;

/**
 *
 * @author tomas
 */

@Entity(name="ROL")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Rol {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer codigoderol;
    private String descripcion;
    private String responsabilidades;  
}
