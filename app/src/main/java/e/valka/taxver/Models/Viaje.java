package e.valka.taxver.Models;

public class Viaje {
    public int IdViaje;
    public int IdConductor;
    public float Kilometros;
    public float Tarifa;
    public int Status;
    public String Descripcion;
    public String Fecha;
    public int IdPersona;

    public Conductor IdConductorNavigation;
    public Persona IdPersonaNavigation;
}
