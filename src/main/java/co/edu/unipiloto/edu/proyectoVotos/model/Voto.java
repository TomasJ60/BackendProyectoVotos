package co.edu.unipiloto.edu.proyectoVotos.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 *
 * @author tomas
 */
@Entity
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ciudadano_id", nullable = false)
    private User ciudadano;

    @ManyToOne
    @JoinColumn(name = "proyecto_id", nullable = false)
    private Proyecto proyecto;

    @Enumerated(EnumType.STRING)
    private VotoEnum voto;

    // Constructor sin argumentos
    public Voto() {}

    // Constructor
    public Voto(User ciudadano, Proyecto proyecto, VotoEnum voto) {
        this.ciudadano = ciudadano;
        this.proyecto = proyecto;
        this.voto = voto;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public User getCiudadano() {
        return ciudadano;
    }

    public void setCiudadano(User ciudadano) {
        this.ciudadano = ciudadano;
    }

    public Proyecto getProyecto() {
        return proyecto;
    }

    public void setProyecto(Proyecto proyecto) {
        this.proyecto = proyecto;
    }

    public VotoEnum getVoto() {
        return voto;
    }

    public void setVoto(VotoEnum voto) {
        this.voto = voto;
    }
    
    
}

