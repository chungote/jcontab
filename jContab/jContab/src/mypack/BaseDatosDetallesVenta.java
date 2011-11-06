/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mypack;


import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author jacg
 */
public class BaseDatosDetallesVenta {
    String driver, url, login, password;
    Connection conexion = null;

    public BaseDatosDetallesVenta() {

        driver = "com.mysql.jdbc.Driver";
        url = "jdbc:mysql://localhost:3306/empresa";
        login = "root";
        password = "treky5";
        try {
            Class.forName(driver).newInstance();
            conexion = DriverManager.getConnection(url, login, password);
            System.out.println("Conexion con Base de datos Ok....");
        } catch (Exception exc) {
            System.out.println("Error al tratar de abrir la base de datos");
        }
    }
    
    public DefaultTableModel leer(String findClave, String find, String Like){
        String[] columnNames = {"0",
                                "1",
                                "2",
                                "3",
                                "4",
                                "5",
                                "6",
                                "7"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String ComandoSQL = null;
        

        if (Like.equals("LIKE")) {
            ComandoSQL = "SELECT * FROM Ventas WHERE " + findClave + " LIKE '%" +find+"%'";
        }else{
            ComandoSQL =  "SELECT * FROM Ventas WHERE " + findClave + " = " +find;
        }
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(ComandoSQL);
            while (resultado.next()) {
                Object[] row = new Object[8];
                row[0] = resultado.getString(1);
                row[1] = resultado.getString(2);
                row[2] = resultado.getString(3);
                row[3] = resultado.getString(4);
                row[4] = resultado.getString(5);
                row[5] = resultado.getString(6);
                row[6] = resultado.getString(7);
                row[7] = resultado.getString(8);
                
                model.addRow(row);
            }
            //JOptionPane.showMessageDialog(null, " Registro leido con exito! ", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Registro leido con exito!");
        } catch (Exception e) {
        }
        return model;
    }
        
    public boolean escribir(DetallesVenta detallesVenta) {
        String id = detallesVenta.getId();
        String RUC = detallesVenta.getRUC();
        String fecha = detallesVenta.getFecha();
        String cantidad = detallesVenta.getCantidad();
        String articulo = detallesVenta.getArticulo();
        String punitario = detallesVenta.getPunitario();
        String ptotal = detallesVenta.getPtotal();
        String nserie = detallesVenta.getNserie();
       
        try {
            Statement stmt = conexion.createStatement();

            stmt.executeUpdate("INSERT INTO FacturasClientes " +
                        "(Cliente, RUC, Cobrado, Subtotal, Iva, Total, Recivido, Notas, Fecha, Nserie,"
                        + " Direccion, Telefono, Ciudad, Fuente) " +
                    "VALUES(" +
                    "'"+cliente+"', "+
                    "'"+ruc+"', "+
                    "'"+cobrado+"', "+
                    "'"+subtotal+"', "+
                    "'"+iva+"', "+
                    "'"+total+"', "+
                    "'"+recivido+"', "+
                    "'"+notas+"', "+
                    "'"+fecha+"', "+
                    "'"+nserie+"', "+
                    "'"+direccion+"', "+
                    "'"+telefono+"', "+
                    "'"+ciudad+"', "+
                    "'"+fuente+"'"+
                ")");
            // Cerramos la interfaz Statement
            stmt.close();

            JOptionPane.showMessageDialog(null, " Registro agregado con exito! ",
                        "Aviso!", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (java.sql.SQLException er) {
            JOptionPane.showMessageDialog(null, " Imposible agregar este registro!! ", "Aviso!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }


    
    
    
}
