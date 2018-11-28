package e.valka.taxver.Models;

import java.io.Serializable;

public class Posicionconductor implements Serializable {
    public int IdPosicionConductor;
    public String Lat;
    public String Lng;
    public int IdConductor;
    public int Status;
    public Conductor IdConductorNavigation;
}
