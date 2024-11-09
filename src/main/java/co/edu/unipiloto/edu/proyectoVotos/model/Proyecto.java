package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.Entity;
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
 * @author tomas
 */
@Entity(name = "PROYECTOS")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nombreProyecto;
    private Integer identificador;
    private String alcance;
    private String periododetiempoprevisto;

    @ManyToOne
    @JoinColumn(name = "tipo_proyecto_id", referencedColumnName = "identificador") // Cambia a un nombre más específico para la clave foránea
    private Tiposdeproyecto tipodeproyecto;

    @ManyToOne
    @JoinColumn(name = "proceso_votacion_id", referencedColumnName = "identificador") 
    private Procesodevotacion procesodevotacion;
    
    private String localidad;
    private String presupuesto;
    private Integer nciudadanos;
    private String impacto;
    private String aspectosasociados;

    private int votosSi;
    private int votosNo;
    private int votosBlanco;
}
