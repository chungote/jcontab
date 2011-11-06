
package mypack;

import java.sql.Blob;


public class Articulo {
    private String codarticulo;
    private String descripcion;
    private String stock;
    private String Pventa;
    private String IVA;
    private String pvp;
    private String Tipo;
    private String Proveedor;
    private String Pcoste;
    private String beneficio;
    private Blob imagen;
    private String fecha;


    public Articulo(String codarticulo, String descripcion, String stock, String PVenta, String IVA, String pvp, String Tipo, String Proveedor,
            String Pcoste, String beneficio, Blob imagen, String fecha) {
        this.codarticulo = codarticulo;
        this.descripcion = descripcion;
        this.stock = stock;
        this.Pventa = PVenta;
        this.IVA = IVA;
        this.pvp = pvp;
        this.Tipo = Tipo;
        this.Proveedor = Proveedor;
        this.Pcoste = Pcoste;

        this.beneficio= beneficio;
        this.imagen = imagen;
        this.fecha = fecha;
    }

     public String getid() {
        return codarticulo;
    }

    public void setid(String cod) {
        this.codarticulo = cod;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String Cliente) {
        this.descripcion = Cliente;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String ruc) {
        this.stock = ruc;
    }

     public String getPVenta() {
        return Pventa;
    }

    public void setPVenta(String Cobrado) {
        this.Pventa = Cobrado;
    }

     public String getIVA() {
        return IVA;
    }

    public void setIVA(String Subtotal) {
        this.IVA = Subtotal;
    }

     public String getPVP() {
        return pvp;
    }

    public void setPVP(String Iva) {
        this.pvp = Iva;
    }

     public String getTipo() {
        return Tipo;
    }

    public void setTipo(String Total) {
        this.Tipo = Total;
    }

    public String getProveedor() {
        return Proveedor;
    }

    public void setProveedor(String Recivido) {
        this.Proveedor = Recivido;
    }

    public String getPCoste() {
        return Pcoste;
    }

    public void setPCoste(String Notas) {
        this.Pcoste = Notas;
    }

    public String getBeneficio() {
        return beneficio;
    }

    public void setBeneficio(String Fecha) {
        this.beneficio = Fecha;
    }

    public Blob getImagen() {
        return imagen;
    }

    public void setImagen(Blob Nserie) {
        this.imagen = Nserie;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String Direccion) {
        this.fecha = Direccion;
    }
}
