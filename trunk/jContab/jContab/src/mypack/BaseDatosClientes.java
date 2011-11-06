package mypack;

import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class BaseDatosClientes {

    String driver, url, login, password;
    Connection conexion = null;

    public BaseDatosClientes() {

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

    public boolean escribir(Cliente cliente) {
        String nombre = cliente.getCliente();
        String ruc = cliente.getRuc();
        String direccion = cliente.getDireccion();
        String telefono = cliente.getTelefono();
        String fecha_emision = cliente.getFechaEmision();
        String ciudad = cliente.getCiudad();
        String organizacion = cliente.getOrganizacion();
        String email = cliente.getEmail();

        try {
            Statement stmt = conexion.createStatement();

            stmt.executeUpdate("INSERT INTO Clientes " +
                        "(Cliente, RUC, Direccion, Telefono, Fecha_Emision, Ciudad, Organizacion, Email) " +
                    "VALUES(" +
                    "'"+nombre+"', "+
                    "'"+ruc+"', "+
                    "'"+direccion+"', "+
                    "'"+telefono+"', "+
                    "'"+fecha_emision+"', "+
                    "'"+ciudad+"', "+
                    "'"+organizacion+"', "+
                    "'"+email+"'"+
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

    public boolean actualizar(Cliente cliente) {
        String id = cliente.getid();
        String nombre = cliente.getCliente();
        String ruc = cliente.getRuc();
        String direccion = cliente.getDireccion();
        String telefono = cliente.getTelefono();
        String fecha_emision = cliente.getFechaEmision();
        String ciudad = cliente.getCiudad();
        String organizacion = cliente.getOrganizacion();
        String email = cliente.getEmail();

        try {
            //Statement stmt = conexion.createStatement();
            PreparedStatement pstmt = conexion.prepareStatement("UPDATE Clientes SET " +
                        "Cliente= ?, RUC= ?, Direccion= ?, Telefono= ?, Fecha_Emision= ?,"
                        + " Ciudad= ?, Organizacion= ?, Email= ? WHERE id= ?");
            pstmt.setString(1, nombre);
            pstmt.setString(2, ruc);
            pstmt.setString(3, direccion);
            pstmt.setString(4, telefono);
            pstmt.setString(5, fecha_emision);
            pstmt.setString(6, ciudad);
            pstmt.setString(7, organizacion);
            pstmt.setString(8, email);
            pstmt.setString(9, id);

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

    public DefaultTableModel leer(String findClave, String find, String Like){
        String[] columnNames = {"0",
                                "1",
                                "2",
                                "3",
                                "4",
                                "5",
                                "6",
                                "7",
                                "8"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String ComandoSQL = null;
        //ComandoSQL = "SELECT * FROM Clientes WHERE " + findClave + " LIKE '%" + (String)find +"%' ORDER BY Cliente ASC";

        if (Like.equals("LIKE")) {
            ComandoSQL = "SELECT * FROM Clientes WHERE " + findClave + " LIKE '%" + (String)find +"%' ORDER BY Cliente ASC";
        }else{
            ComandoSQL = "SELECT * FROM Clientes WHERE " + findClave + " = " + (String)find +" ORDER BY Cliente ASC";
        }
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(ComandoSQL);
            while (resultado.next()) {
                Object[] row = new Object[9];
                row[0] = resultado.getString(1);
                row[1] = resultado.getString(2);
                row[2] = resultado.getString(3);
                row[3] = resultado.getString(4);
                row[4] = resultado.getString(5);
                row[5] = resultado.getString(6);
                row[6] = resultado.getString(7);
                row[7] = resultado.getString(8);
                row[8] = resultado.getString(9);

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

        comando = "DELETE FROM Clientes WHERE id =" + Integer.parseInt(id);

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
}
