package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Tomas Alvarez
 */
@Entity(name="CIUDADANO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombre;
    private String correo;
    private String telefono;
    private String localidad;
    private String direccion;

    private Boolean activo;
    
    private String genero;
    
    @ManyToOne
    @JoinColumn(name = "codigoderol", referencedColumnName = "codigoderol")
    private Rol rol;
    
    
    public boolean isActivo() {
        return activo;
    }
}