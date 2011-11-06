
package mypack;


public class Cliente {
    private String id;
    private String Cliente;
    private String RUC;
    private String Direccion;
    private String Telefono;
    private String Fecha_Emision;
    private String Ciudad;
    private String Organizacion;
    private String Email;
    

    public Cliente(String id, String Cliente, String RUC, String Direccion, String Telefono, String Fecha_Emision, String Ciudad, String Organizacion, String Email) {
        this.id = id;
        this.Cliente = Cliente;
        this.RUC = RUC;
        this.Direccion = Direccion;
        this.Telefono = Telefono;
        this.Fecha_Emision = Fecha_Emision;
        this.Ciudad = Ciudad;
        this.Organizacion = Organizacion;
        this.Email = Email;
        
    }

     public String getid() {
        return id;
    }

    public void setid(String id) {
        this.id = id;
    }

    public String getCliente() {
        return Cliente;
    }

    public void setCliente(String Cliente) {
        this.Cliente = Cliente;
    }

    public String getRuc() {
        return RUC;
    }

    public void setRuc(String ruc) {
        this.RUC = ruc;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String Direccion) {
        this.Direccion = Direccion;
    }

    public String getTelefono() {
        return Telefono;
    }

    public void setTelefono(String Telefono) {
        this.Telefono = Telefono;
    }

    public String getFechaEmision() {
        return Fecha_Emision;
    }

    public void setFechaEmision(String Fecha_Emision) {
        this.Fecha_Emision = Fecha_Emision;
    }

    public String getCiudad() {
        return Ciudad;
    }

    public void setCiudad(String Ciudad) {
        this.Ciudad = Ciudad;
    }

    public String getOrganizacion() {
        return Organizacion;
    }

    public void setOrganizacion(String Organizacion) {
        this.Organizacion = Organizacion;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String Email) {
        this.Email = Email;
    }
}
