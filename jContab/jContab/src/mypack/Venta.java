
package mypack;


public class Venta {
    private String id;
    private String Cliente;
    private String RUC;
    private String Cobrado;
    private String Subtotal;
    private String Iva;
    private String Total;
    private String Recivido;
    private String Notas;
    private String Fecha;
    private String Nserie;
    private String Direccion;
    private String Telefono;
    private String Ciudad;
    private String Fuente;
    

    public Venta(String id, String Cliente, String RUC, String Cobrado, String Subtotal, String Iva, String Total, String Recivido, String Notas,
            String Fecha, String Nserie, String Direccion, String Telefono, String Ciudad, String Fuente) {
        this.id = id;
        this.Cliente = Cliente;
        this.RUC = RUC;
        this.Cobrado = Cobrado;
        this.Subtotal = Subtotal;
        this.Iva = Iva;
        this.Total = Total;
        this.Recivido = Recivido;
        this.Notas = Notas;

        this.Fecha = Fecha;
        this.Nserie = Nserie;
        this.Direccion = Direccion;
        this.Telefono = Telefono;
        this.Ciudad = Ciudad;
        this.Fuente = Fuente;
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

     public String getCobrado() {
        return Cobrado;
    }

    public void setCobrado(String Cobrado) {
        this.Cobrado = Cobrado;
    }

     public String getSubtotal() {
        return Subtotal;
    }

    public void setSubtotal(String Subtotal) {
        this.Subtotal = Subtotal;
    }

     public String getIva() {
        return Iva;
    }

    public void setIva(String Iva) {
        this.Iva = Iva;
    }

     public String getTotal() {
        return Total;
    }

    public void setTotal(String Total) {
        this.Total = Total;
    }

    public String getDireccion() {
        return Direccion;
    }

    public String getRecivido() {
        return Recivido;
    }

    public void setRecivido(String Recivido) {
        this.Recivido = Recivido;
    }

    public String getNotas() {
        return Notas;
    }

    public void setNotas(String Notas) {
        this.Notas = Notas;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String Fecha) {
        this.Fecha = Fecha;
    }

    public String getNserie() {
        return Nserie;
    }

    public void setNserie(String Nserie) {
        this.Nserie = Nserie;
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

    public String getCiudad() {
        return Ciudad;
    }

    public void setCiudad(String Ciudad) {
        this.Ciudad = Ciudad;
    }

    public String getFuente() {
        return Fuente;
    }

    public void setFuente(String Fuente) {
        this.Fuente = Fuente;
    }
}
