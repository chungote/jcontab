package mypack;

import javax.print.DocFlavor.STRING;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author jacg
 */
public class Participante {
    private String fechaEmision;;
    private String cliente;
    private String ruc;

    private String telefono;
    private String direccion;

    private String subtotal;
    private String iva;
    private String total;

    private String cantidad;
    private String articulo;
    private String pUnitario;
    private String pTotal;
    private String ciudad;
    
    public Participante(){
    }
    

    public Participante(String fechaEmision, String cliente, String ruc, String direccion, String telefono, String cantidad, String articulo, String pUnitario, String pTotal, String subtotal, String iva, String total, String ciudad) {
        this.fechaEmision = fechaEmision;
        this.cliente = cliente;
        this.ruc = ruc;
        this.telefono = telefono;
        this.direccion = direccion;

        this.articulo = articulo;
        this.cantidad = cantidad;
        this.pUnitario = pUnitario;
        this.pTotal = pTotal;
        this.subtotal = subtotal;
        this.iva = iva;
        this.total = total;
        this.ciudad = ciudad;
    }



    public void setFechaEmision(String fechaEmision){
        this.fechaEmision = fechaEmision;
    }

    public String getFechaEmision() {
        return fechaEmision;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getSubtotal() {
        return subtotal;
    }

    public void setsubtotal(String subtotal) {
        this.subtotal = subtotal;
    }

    public String getIva() {
        return iva;
    }

    public void setIva(String iva) {
        this.iva = iva;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCantidad() {
        return cantidad;
    }

    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    public String getArticulo() {
        return articulo;
    }

    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    public String getpUnitario() {
        return pUnitario;
    }

    public void setpUnitario(String pUnitario) {
        this.pUnitario = pUnitario;
    }

    public String getpTotal() {
        return pTotal;
    }

    public void setpTotal(String pTotal) {
        this.pTotal = pTotal;
    }

    public String getCiudad() {
        return ciudad;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

}
