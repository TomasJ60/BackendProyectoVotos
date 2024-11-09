package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.*;
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
    private LocalDateTime  fechadeinicio;
    private LocalDateTime  fechadecierre;
    private String estado;
    
    
    //getter y setters
    public Integer getIdentificador() {
        return identificador;
    }

    public void setIdentificador(Integer identificador) {
        this.identificador = identificador;
    }

    // Getter y Setter para estado
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Getter y Setter para fechadeinicio
    public LocalDateTime getFechadeinicio() {
        return fechadeinicio;
    }

    public void setFechadeinicio(LocalDateTime fechadeinicio) {
        this.fechadeinicio = fechadeinicio;
    }

    // Getter y Setter para fechadecierre
    public LocalDateTime getFechadecierre() {
        return fechadecierre;
    }

    public void setFechadecierre(LocalDateTime fechadecierre) {
        this.fechadecierre = fechadecierre;
    }
    
    
    public void checkEstado() {
        LocalDateTime fechaActual = LocalDateTime.now();
        
        if (estado.equals("activo") && fechaActual.isAfter(fechadecierre)) {
            this.estado = "cerrado";
        } else if (estado.equals("definido") && fechaActual.isAfter(fechadeinicio)) {
            this.estado = "activo";
        }
    }
    
}
