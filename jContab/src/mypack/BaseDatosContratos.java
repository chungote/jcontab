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
public class BaseDatosContratos {
    String driver, url, login, password;
    Connection conexion = null;
    
    public BaseDatosContratos() {

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
                                "7",
                                "8",
                                "9",
                                "10",
                                "11",
                                "12",
                                "13",
                                "14"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String ComandoSQL = null;
        

        if (Like.equals("LIKE")) {
            ComandoSQL = "SELECT * FROM Contratos WHERE " + findClave + " LIKE '%" +find+"%' ORDER BY nContrato ASC";
        }else{
            ComandoSQL = "SELECT * FROM Contratos WHERE " + findClave + " = " +find;
        }
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(ComandoSQL);
            while (resultado.next()) {
                Object[] row = new Object[15];
                row[0] = resultado.getString(1);
                row[1] = resultado.getString(2);
                row[2] = resultado.getString(3);
                row[3] = resultado.getString(4);
                row[4] = resultado.getString(5);
                row[5] = resultado.getString(6);
                row[6] = resultado.getString(7);
                row[7] = resultado.getString(8);
                row[8] = resultado.getString(9);
                row[9] = resultado.getString(10);
                row[10] = resultado.getString(11);
                row[11] = resultado.getString(12);
                row[12] = resultado.getString(13);
                row[13] = resultado.getString(14);
                row[14] = resultado.getString(15);
                model.addRow(row);
            }
            //JOptionPane.showMessageDialog(null, " Registro leido con exito! ", "Aviso!", JOptionPane.INFORMATION_MESSAGE);
            System.out.println("Registro leido con exito!");
        } catch (Exception e) {
        }
        return model;
    }
     
    public boolean borrar(String id) {
        String comando = "";

        comando = "DELETE FROM FacturaProveedores WHERE id =" + Integer.parseInt(id);

        //comando =  "DELETE FROM `cliente` WHERE CONVERT(`cliente`.`cedula` USING utf8) = '"+cedula+"' LIMIT 1";
        
        try {
            Statement stmt = conexion.createStatement();
            stmt.executeUpdate(comando);
            JOptionPane.showMessageDialog(null, " Registro eliminado con exito! ", "Aviso!", JOptionPane.INFORMATION_MESSAGE);

            // Cerramos la interfaz Statement
            stmt.close();
            return true;

        } catch (java.sql.SQLException er) {
            JOptionPane.showMessageDialog(null, " No se puede eliminar este registro!! ", "Aviso!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    public boolean escribir(Compra compra) {
        String id = compra.getId();
        String Nserie = compra.getNserie();
        String Proveedor = compra.getProveedor();
        String Fecha = compra.getFecha();
        String RUC = compra.getRUC();
        String Pagado = compra.getPagado();
        String Direccion = compra.getDireccion();
        String Subtotal = compra.getSubtotal();
        String IVA = compra.getIVA();
        String Total = compra.getTotal();
        String Telefono = compra.getTelefono();
        String Articulo = compra.getArticulo();
       
        try {
            Statement stmt = conexion.createStatement();
           
            stmt.executeUpdate("INSERT INTO FacturaProveedores " +
                        "(Nserie, Proveedor, Fecha, RUC, Pagado, Direccion, Subtotal, IVA, Total, Telefono, Articulo) " +
                    "VALUES(" +
                    "'"+Nserie+"' ,"+
                    "'"+Proveedor+"' ,"+
                    "'"+Fecha+"' ,"+
                    "'"+RUC+"' ,"+
                    "'"+"false"+"' ,"+
                    "'"+Direccion+"' ,"+
                    "'"+Subtotal+"' ,"+
                    "'"+IVA+"' ,"+
                    "'"+Total+"' ,"+
                    "'"+Telefono+"' ,"+
                    "'"+Articulo+"'"+
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
    
    public boolean actualizar(Compra compra) {
        String id = compra.getId();
        String Nserie = compra.getNserie();
        String Proveedor = compra.getProveedor();
        String Fecha = compra.getFecha();
        String RUC = compra.getRUC();
        String Pagado = compra.getPagado();
        String Direccion = compra.getDireccion();
        String Subtotal = compra.getSubtotal();
        String IVA = compra.getIVA();
        String Total = compra.getTotal();
        String Telefono = compra.getTelefono();
        String Articulo = compra.getArticulo();

        try {          
            //Statement stmt = conexion.createStatement();
            PreparedStatement pstmt = conexion.prepareStatement("UPDATE FacturaProveedores SET " +
                        "Nserie= ?, Proveedor= ?, Fecha= ?, RUC= ?, Pagado= ?, Direccion= ?,"
                    + " Subtotal= ?, IVA= ?, Total= ?, Telefono= ?, Articulo= ? WHERE id= ?");
            pstmt.setString(1, Nserie);
            pstmt.setString(2, Proveedor);
            pstmt.setString(3, Fecha);
            pstmt.setString(4, RUC);
            pstmt.setString(5, Pagado);
            pstmt.setString(6, Direccion);
            pstmt.setString(7, Subtotal);
            pstmt.setString(8, IVA);
            pstmt.setString(9, Total);
            pstmt.setString(10, Telefono);
            pstmt.setString(11, Articulo);
            pstmt.setString(12, id);
                      
            int actualizar = pstmt.executeUpdate();

            // Cerramos la interfaz Statement
            pstmt.close();
            
            JOptionPane.showMessageDialog(null, " Registro actualizado con exito! ",
                        "Aviso!", JOptionPane.INFORMATION_MESSAGE);
            return true;

        } catch (java.sql.SQLException er) {
            JOptionPane.showMessageDialog(null, " Imposible actualizar este registro!! ", "Aviso!", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
