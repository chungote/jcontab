package mypack;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class BaseDatosArticulos {

    String driver, url, login, password;
    Connection conexion = null;

    public BaseDatosArticulos() {

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

    public boolean escribir(Articulo articulo) {

        String codarticulo = articulo.getid();
        String descripcion = articulo.getDescripcion();
        String stock = articulo.getStock();
        String Pventa = articulo.getPVenta();
        String IVA = articulo.getIVA();
        String pvp = articulo.getPVP();
        String Tipo = articulo.getTipo();
        String Proveedor = articulo.getProveedor();
        String Pcoste = articulo.getPCoste();
        String beneficio = articulo.getBeneficio();
        String imagen = articulo.getImagen();
        String fecha = articulo.getFecha();
       
        try {
            Statement stmt = conexion.createStatement();

            stmt.executeUpdate("INSERT INTO Ventas " +
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

    public boolean actualizar(Cliente venta) {
        String id = venta.getid();
        String nombre = venta.getCliente();
        String ruc = venta.getRuc();
        String direccion = venta.getDireccion();
        String telefono = venta.getTelefono();
        String fecha_emision = venta.getFechaEmision();
        String ciudad = venta.getCiudad();
        String organizacion = venta.getOrganizacion();
        String email = venta.getEmail();

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
                                "8",
                                "9",
                                "10",
                                "11"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        String ComandoSQL = null;
        
        if (Like.equals("LIKE")) {
            ComandoSQL = "SELECT * FROM Articulos WHERE " + findClave + " LIKE '%" + find + "%' ORDER BY descripcion ASC";;
        }else{
            ComandoSQL = "SELECT * FROM Articulos WHERE " + findClave + " =" + find + " ORDER BY descripcion ASC";;
        }
        
        try {
            Statement stmt = conexion.createStatement();
            ResultSet resultado = stmt.executeQuery(ComandoSQL);
            while (resultado.next()) {
                Object[] row = new Object[12];
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
                                
                Blob bytesImagen = resultado.getBlob(11);
                byte[] img = bytesImagen.getBytes(1, (int)bytesImagen.length());
                InputStream is = bytesImagen.getBinaryStream();
                String filename = System.getProperty("user.home") + "/jContab/copia_temp.jpg";
                FileOutputStream fw = new FileOutputStream((String)filename);
                byte bytes[] = new byte[1024];
                int leidos = is.read(bytes);
                while (leidos > 0) {
                   fw.write(bytes);
                   leidos = is.read(bytes);
                }
                
                row[10] = img;
                row[11] = resultado.getString(12);
      
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

        //comando =  "DELETE FROM `venta` WHERE CONVERT(`venta`.`cedula` USING utf8) = '"+cedula+"' LIMIT 1";
        
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
