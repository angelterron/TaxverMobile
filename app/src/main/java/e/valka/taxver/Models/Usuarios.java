package e.valka.taxver.Models;

import java.io.Serializable;

public class Usuarios implements Serializable {
    public int IdUsuarios;
    public String Nombre;
    public String Password;
    public int IdTipoUsuario;
    public int Status;
    public String Descripcion;
    public int IdPersona;
    public Persona IdPersonaNavigation;
    public String PhoneId;
}
