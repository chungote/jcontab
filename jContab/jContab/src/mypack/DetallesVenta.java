/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mypack;

/**
 *
 * @author jacg
 */
public class DetallesVenta {
    private String id;
    private String RUC;
    private String fecha;
    private String cantidad;
    private String articulo;
    private String punitario;
    private String ptotal;
    private String nserie;

    public DetallesVenta(String id, String RUC, String fecha, String cantidad, String articulo, String punitario, String ptotal, String nserie){
        this.id = id;
        this.RUC = RUC;
        this.fecha = fecha;
        this.cantidad = cantidad;
        this.articulo = articulo;
        this.punitario = punitario;
        this.ptotal = ptotal;
        this.nserie = nserie;
    }
    
    /**
     * @return the articulo
     */
    public String getArticulo() {
        return articulo;
    }

    /**
     * @return the cantidad
     */
    public String getCantidad() {
        return cantidad;
    }

    /**
     * @return the fecha
     */
    public String getFecha() {
        return fecha;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the nserie
     */
    public String getNserie() {
        return nserie;
    }

    /**
     * @return the ptotal
     */
    public String getPtotal() {
        return ptotal;
    }

    /**
     * @return the punitario
     */
    public String getPunitario() {
        return punitario;
    }

    /**
     * @return the RUC
     */
    public String getRUC() {
        return RUC;
    }

    /**
     * @param articulo the articulo to set
     */
    public void setArticulo(String articulo) {
        this.articulo = articulo;
    }

    /**
     * @param cantidad the cantidad to set
     */
    public void setCantidad(String cantidad) {
        this.cantidad = cantidad;
    }

    /**
     * @param fecha the fecha to set
     */
    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @param nserie the nserie to set
     */
    public void setNserie(String nserie) {
        this.nserie = nserie;
    }

    /**
     * @param ptotal the ptotal to set
     */
    public void setPtotal(String ptotal) {
        this.ptotal = ptotal;
    }

    /**
     * @param punitario the punitario to set
     */
    public void setPunitario(String punitario) {
        this.punitario = punitario;
    }

    /**
     * @param RUC the RUC to set
     */
    public void setRUC(String RUC) {
        this.RUC = RUC;
    }
       
}
