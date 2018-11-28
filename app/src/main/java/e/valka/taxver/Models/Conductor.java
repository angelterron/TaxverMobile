package e.valka.taxver.Models;

import java.io.Serializable;

public class Conductor implements Serializable {
    public int IdConductor;
    public int IdVehiculo ;
    public int IdPersona;
    public String Foto;
    public int Status;
    public Persona IdPersonaNavigation;
    public Vehiculo IdVehiculoNavigation;
}
