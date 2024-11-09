package co.edu.unipiloto.edu.proyectoVotos.model;

/**
 *
 * @author tomas
 */
public class VotoRequest {
    
    private VotoEnum voto; // Tipo de voto

    // Constructor
    public VotoRequest() {
    }

    public VotoRequest(VotoEnum voto) {
        this.voto = voto;
    }

    // Getter y Setter
    public VotoEnum getVoto() {
        return voto;
    }

    public void setVoto(VotoEnum voto) {
        this.voto = voto;
    }
}
