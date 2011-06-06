/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Contab.java
 *
 * Created on May 1, 2011, 10:01:05 PM
 * 
 * Correccion de vector to arraylist100
 * Mejoras de interfaz
 * Codigo mas limpio
 * Mas estabilidad y nuevas caracteristicas
 * 
 */
package mypack;


import com.nilo.plaf.nimrod.NimRODLookAndFeel;
import com.nilo.plaf.nimrod.NimRODTheme;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.tree.TreePath;
import java.lang.String.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyVetoException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;
import javax.swing.JTable;
import javax.swing.ImageIcon;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.Renderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Arrays;
import java.util.Calendar;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author jacg
 */
public final class Contab extends javax.swing.JFrame {
    //Variables
    int temp  = 0;
    Connection connection = null;
    Statement statement = null;
    PreparedStatement pstatement = null;
    ResultSet rs = null;
    char flagDriver = 0; //0 no driver 1 founded
    String fila;
    int flagSaveFichaArticulo = 0; // 0 no guardado 1 actualizar
    int flagSaveFichaCliente = 0; // 0 no guardado 1 actualizar
    int flagSaveFichaFacturaCliente = 0;
    int flagSaveFichaProveedor = 0;   //actualiza datos
    int flagSaveFacturaProveedor = 0;
    BufferedImage image = null;
    boolean is_Driver = false;
    String baseDatos = "jdbc:mysql://localhost:3306/empresa?"+"user=root&password=treky5";
    String[] columnNamesX = {
                                "Cant.",
                                "Artículo",
                                "P. Unit.",
                                "P. Total"};
    DefaultTableModel model1 = new DefaultTableModel(columnNamesX, 0);
    Object[] row1 = new Object[4];
    
    /** Creates new form Contab */
    public Contab() {
        initComponents();
        checkDriver();
        if(flagDriver == 0){
            //custom title, warning icon
            JOptionPane.showMessageDialog(new JFrame(),
                        "Driver JDBC no encontrado.\nBase de datos no puede ser leida",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        SimpleDateFormat formato = new SimpleDateFormat("MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        buscarFechaTxt.setText(hoy);
        
        leerClientes("Cliente", "");
        actualizaListaClientesTablaAnchos();
        seleccionClientesTabla();
        
        leerArticulos("");
        actualizaListaArticulosTablaAnchos();
        seleccionArticulosTabla();
        
        String find1;
        if(fechaBuscarRadioButton.isSelected()){
            
            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
   
        leerClientesFactura("Cliente", "", find1);
        actualizaListaFacturasClientesTablaAnchos();
        seleccionFacturasClientesTabla();
        
        //*********************
        facturaArticulosClienteTable.removeAll();
        facturaArticulosClienteTable.updateUI();
        facturaArticulosClienteTable.setModel(model1);
        actualizaTablaFacturaClienteAnchos();
        
        leerProveedorBD("Empresa", "");
        actualizaTablaProveedoresAnchos();
        seleccionProveedoresTabla();
        
        buscarFechaTxt1.setText(hoy);
        String find2;
        if(fechaBuscarRadioButton1.isSelected()){
            
            find2 = buscarFechaTxt1.getText();
        }else{
            find2 = "";
        }
        leerFacturaProveedorBD("Proveedor", "", find2);
        actualizaTablaFacturasProveedoresAnchos();
        seleccionFacturasProveedoresTabla();
    }
 
    /*
     * Listener para seleccion de facturas a partir de la tabla
     */
    void seleccionFacturasProveedoresTabla(){
        facturasProveedoresTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    if (facturasProveedoresTable.getSelectedRow() != -1){
                        seleccionTablaFacturasProveedores();
                    }
                }
        });
    }

    
     /*
     * base de datos seleccionar
     */
    void seleccionTablaFacturasProveedores(){
        int fila = facturasProveedoresTable.getSelectedRow();
        int columna = 0;
        String num;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(facturasProveedoresTable.getValueAt(fila,columna));
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM FacturaProveedores WHERE id=" + (String)num);
                while(rs.next()){
                    idFacturaProveedorTxt.setText(rs.getString("id"));
                    serieFacturaProveedorTxt.setText(rs.getString("Nserie"));
                    proveedorFacturaTxt.setText(rs.getString("Proveedor"));
                    fechaCompraTxt.setText(rs.getString("Fecha"));
                    rucFacturaProveedorTxt.setText(rs.getString("RUC"));
                    subtotalTxt1.setText(rs.getString("Subtotal"));
                    ivat12Txt1.setText(rs.getString("IVA"));
                    totalTxt1.setText(rs.getString("Total"));
                    telefonoProveedorTxt.setText(rs.getString("Telefono"));
                    direccionProveedorTxt.setText(rs.getString("Direccion"));
                    articuloComboBox.setSelectedIndex(Integer.valueOf((String)rs.getObject("Articulo")));
                }
                flagSaveFacturaProveedor = 1;   //actualiza datos
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /*
     * Listener para seleccion de proveedores a partir de la tabla
     */
    void seleccionProveedoresTabla(){
        proveedoresTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent e) {
                    if (proveedoresTable.getSelectedRow() != -1){
                        seleccionTablaProveedores();
                    }
                }
        });
    }

    
    /*
     * base de datos seleccionar
     */
    void seleccionTablaProveedores(){
        int fila = proveedoresTable.getSelectedRow();
        int columna = 0;
        String num = null;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(proveedoresTable.getValueAt(fila,columna));
         if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Proveedores WHERE id=" + (String)num);
                while(rs.next()){
                    idProveedorTxt.setText(rs.getString("id"));
                    empresaTxt.setText(rs.getString("Empresa"));
                    contactoTextArea.setText(rs.getString("Contacto"));
                    telefonoTextArea.setText(rs.getString("Telefono"));
                    ciudadProveedorTxt.setText(rs.getString("Ciudad"));
                    emailProveedorTxt.setText(rs.getString("Email"));
                    wwwTxt.setText(rs.getString("Direccion"));
                    rucProveedorTxt.setText(rs.getString("RUC"));
                }
                flagSaveFichaProveedor = 1;   //actualiza datos
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /*
     * LEER FACTURAS DE CLIENTES
     */
    public void leerClientesFactura(String findClave, String find, String findClave1){
        listaFacturaClientesTabla.removeAll();
        listaFacturaClientesTabla.updateUI();
        
        String[] columnNames = {"Código",
                                "Fecha",
                                "Cliente",
                                "Total",
                                "Cobrado",
                                "IVA",
                                "Retenido",
                                "Recivido",
                                "Nota"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
                
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM FacturasClientes WHERE " + findClave + " LIKE '%" + (String)find+"%'" + "AND Fecha"+ " LIKE '%" + (String)findClave1 +"%'");
                while(rs.next()){                               
                    Object[] row = new Object[9];
                    row[0] = rs.getObject("id");
                    row[1] = rs.getObject("Fecha");
                    row[2] = rs.getObject("Cliente");
                    row[3] = rs.getObject("Total");
                    row[4] = rs.getObject("Cobrado");
                    row[5] = rs.getObject("Iva");
                    row[6] = rs.getObject("Fuente");  
                    row[7] = rs.getObject("Recivido");  
                    row[8] = rs.getObject("Notas");  
                    model.addRow(row);
                }
                
                listaFacturaClientesTabla.setModel(model);
                rs.close();
                statement.close();
                connection.close();
                
                //Computo de totales de iva, fuente, recivido
                //______________________________
                DecimalFormat nf = new DecimalFormat("####.##");
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(2);
                String recivido = "";
                String iva = "";
                String fuente= "";
                String total = "";
                String cobrado = "";
                Double res = 0.00;
                Double res1 = 0.00;
                Double res2 = 0.00;
                Double res3 = 0.00;
                Double temp = 0.00;
                for (int i = 0, rows =listaFacturaClientesTabla.getRowCount(); i < rows; i++)
                { 
                    if(filtrarCobradoRadioButton.isSelected()){
                        cobrado = (String) listaFacturaClientesTabla.getValueAt(i, 4).toString();
                        if(cobrado.equals("true")){
                            //Calcula y muestra solo facturas cobradas
                            iva = (String) listaFacturaClientesTabla.getValueAt(i, 5).toString();
                            temp =  Double.valueOf(iva);
                            res = res + temp;

                            total = (String) listaFacturaClientesTabla.getValueAt(i, 3).toString();
                            temp =  Double.valueOf(total);
                            res1 = res1 + temp;

                            fuente = (String) listaFacturaClientesTabla.getValueAt(i, 6).toString();
                            temp =  Double.valueOf(fuente);
                            res2 = res2 + temp;

                            recivido = (String) listaFacturaClientesTabla.getValueAt(i, 7).toString();
                            temp =  Double.valueOf(recivido);
                            res3 = res3 + temp;
                        }
                    }else{
                        iva = (String) listaFacturaClientesTabla.getValueAt(i, 5).toString();
                        temp =  Double.valueOf(iva);
                        res = res + temp;

                        total = (String) listaFacturaClientesTabla.getValueAt(i, 3).toString();
                        temp =  Double.valueOf(total);
                        res1 = res1 + temp;

                        fuente = (String) listaFacturaClientesTabla.getValueAt(i, 6).toString();
                        temp =  Double.valueOf(fuente);
                        res2 = res2 + temp;

                        recivido = (String) listaFacturaClientesTabla.getValueAt(i, 7).toString();
                        temp =  Double.valueOf(recivido);
                        res3 = res3 + temp;
                    }
                }
                ivaTxt.setText(nf.format(res));
                ivaTxt1.setText(nf.format(res1));
                fuenteTxt.setText(nf.format(res2));
                recividoTxt.setText(nf.format(res3));
                
            } catch (Exception e) {
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "No se pueden leer facturas",
                        "Error - 4", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /*
     * Actualiza tabla
     */
    void actualizaListaFacturasClientesTablaAnchos(){
        listaFacturaClientesTabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        listaFacturaClientesTabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        listaFacturaClientesTabla.getColumnModel().getColumn(2).setPreferredWidth(230);
        listaFacturaClientesTabla.getColumnModel().getColumn(3).setPreferredWidth(80);
        listaFacturaClientesTabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        listaFacturaClientesTabla.getColumnModel().getColumn(5).setPreferredWidth(80);
        listaFacturaClientesTabla.getColumnModel().getColumn(8).setPreferredWidth(220); 
        listaFacturaClientesTabla.setRowHeight(25);
        listaFacturaClientesTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        listaFacturaClientesTabla.getColumnModel().getColumn(1).setCellRenderer(new ColorTableCellRenderer1());
        listaFacturaClientesTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer1());
        listaFacturaClientesTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer1());
        listaFacturaClientesTabla.getColumnModel().getColumn(4).setCellRenderer(new CheckBoxRenderer());
        listaFacturaClientesTabla.getColumnModel().getColumn(5).setCellRenderer(new ColorTableCellRenderer1());
        listaFacturaClientesTabla.getColumnModel().getColumn(6).setCellRenderer(new ColorTableCellRenderer1());
        listaFacturaClientesTabla.getColumnModel().getColumn(7).setCellRenderer(new ColorTableCellRenderer1());
        listaFacturaClientesTabla.getColumnModel().getColumn(8).setCellRenderer(new ColorTableCellRenderer1());
    }
   
    /*
     * Actualiza tabla
     */
    void  actualizaTablaFacturaClienteAnchos() {
        facturaArticulosClienteTable.getColumnModel().getColumn(0).setPreferredWidth(45);
        facturaArticulosClienteTable.getColumnModel().getColumn(1).setPreferredWidth(300);
        facturaArticulosClienteTable.getColumnModel().getColumn(2).setPreferredWidth(80);
        facturaArticulosClienteTable.getColumnModel().getColumn(3).setPreferredWidth(80);

        facturaArticulosClienteTable.setRowHeight(70);
        facturaArticulosClienteTable.getColumnModel().getColumn(0).setCellRenderer(new TextAreaRenderer());
        facturaArticulosClienteTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        facturaArticulosClienteTable.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        facturaArticulosClienteTable.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
    }

    
   void  actualizaTablaFacturasProveedoresAnchos() {
       facturasProveedoresTable.getColumnModel().getColumn(0).setPreferredWidth(60);
       facturasProveedoresTable.getColumnModel().getColumn(1).setPreferredWidth(270);
       facturasProveedoresTable.getColumnModel().getColumn(2).setPreferredWidth(100);
       facturasProveedoresTable.getColumnModel().getColumn(3).setPreferredWidth(80);
       facturasProveedoresTable.getColumnModel().getColumn(4).setPreferredWidth(80);
       facturasProveedoresTable.getColumnModel().getColumn(5).setPreferredWidth(80);
       facturasProveedoresTable.getColumnModel().getColumn(6).setPreferredWidth(80);

       facturasProveedoresTable.setRowHeight(25);

       facturasProveedoresTable.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
       facturasProveedoresTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
       facturasProveedoresTable.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
       facturasProveedoresTable.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
       facturasProveedoresTable.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer1());
       facturasProveedoresTable.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxRenderer());
       facturasProveedoresTable.getColumnModel().getColumn(6).setCellRenderer(new ColorTableCellRenderer1());
      

    }

    /*
     * Actualiza tabla
     */
    void actualizaListaArticulosTablaAnchos(){
        listaArticulosTabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        listaArticulosTabla.getColumnModel().getColumn(1).setPreferredWidth(420);
        listaArticulosTabla.getColumnModel().getColumn(2).setPreferredWidth(46);
        listaArticulosTabla.getColumnModel().getColumn(3).setPreferredWidth(95);
        listaArticulosTabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        listaArticulosTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        listaArticulosTabla.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        listaArticulosTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        listaArticulosTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        listaArticulosTabla.getColumnModel().getColumn(4).setCellRenderer(new ImageRenderer());
        listaArticulosTabla.setRowHeight(90);
    }
    
    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionClientesTabla(){
        clientesTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                    if (clientesTable.getSelectedRow() != -1){
                        seleccionTablaClientes();
                    }
                }
        });
    }
    
    /*
     * base de datos seleccionar
     */
    void seleccionTablaClientes(){
        int filaa = clientesTable.getSelectedRow();
        int columna = 0;
        String num = null;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(clientesTable.getValueAt(filaa,columna));

        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Clientes WHERE id=" + (String)num);
                while(rs.next()){
                    idClienteTxt.setText(rs.getString("id"));
                    clienteTxt.setText(rs.getString("Cliente"));
                    RUCTxt.setText(rs.getString("RUC"));
                    nombreComercialTxt.setText(rs.getString("Organizacion"));
                    fechaClienteTxt.setText(rs.getString("Fecha_Emision"));
                    ciudadClienteTxt.setText(rs.getString("Ciudad"));
                    direccionClienteTxt.setText(rs.getString("Direccion"));
                    telefonoClienteTxt.setText(rs.getString("Telefono"));
                    emailClienteTxt.setText(rs.getString("Email"));

                }
                escrituraFichaCliente();
                flagSaveFichaCliente = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //articulosTabbedPane.setSelectedIndex(1);
    }
    
    
    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionFacturasClientesTabla(){
        listaFacturaClientesTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                    if (listaFacturaClientesTabla.getSelectedRow() != -1){
                        seleccionTablaFacturasClientes();
                    }
                }
        });
    }

    /*
     * base de datos seleccionar
     */
    void seleccionTablaFacturasClientes(){
        int filaa = listaFacturaClientesTabla.getSelectedRow();
        int columna = 0;
        String num = null;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(listaFacturaClientesTabla.getValueAt(filaa,columna));

        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM FacturasClientes WHERE id=" + (String)num);
                while(rs.next()){
                    clienteFacturaTxt.setText(rs.getString("Cliente"));
                    fechaTxt.setText(rs.getString("Fecha"));
                    rucTxt.setText(rs.getString("RUC"));
                    subtotalTxt.setText(rs.getString("Subtotal"));
                    ivat12Txt.setText(rs.getString("Iva"));
                    totalTxt.setText(rs.getString("Total"));
                    numeroSerieTxt.setText(rs.getString("Nserie"));
                    ciudadTxt.setText(rs.getString("Ciudad"));
                    direccionTxt.setText(rs.getString("Direccion"));
                    telefonoTxt.setText(rs.getString("Telefono"));
                    retencionTxt.setText(rs.getString("Fuente"));
                    recividoTxt1.setText(rs.getString("Recivido"));
                    idFacturaTxt.setText(rs.getString("id"));
                }
                escrituraFichaCliente();
                flagSaveFichaFacturaCliente = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }

            facturaArticulosClienteTable.removeAll();
            facturaArticulosClienteTable.updateUI();
            String[] columnNames = {
                                "Cant.",
                                "Artículo",
                                "P. Unit.",
                                "P. Total"};
    
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
            conectarBD(baseDatos);
            try {
                //BUSCA Y LEE ARTICULOS VENDIDOS AL CLIENTE
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Ventas WHERE nserie" + " LIKE '%" +(String)numeroSerieTxt.getText()+"%'");
                while(rs.next()){
                    Object[] row = new Object[4];
                    row[0] = rs.getObject("cantidad");
                    row[1] = rs.getObject("articulo");
                    row[2] = rs.getObject("punitario");
                    row[3] = rs.getObject("ptotal");               
                    model.addRow(row);
                }
                facturaArticulosClienteTable.setModel(model);
                rs.close();
                statement.close();
                connection.close();
                actualizaTablaFacturaClienteAnchos();
            } catch (Exception e) {
                System.out.println("ERROR:"+e);
            }
        }
        //articulosTabbedPane.setSelectedIndex(1);
    }

    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionArticulosTabla(){
        listaArticulosTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                    if (listaArticulosTabla.getSelectedRow() != -1){
                        seleccionTablaArticulos();
                    }
                }
        });
    }
    
    /*
     * base de datos seleccionar
     */
    void seleccionTablaArticulos(){
        int filaa = listaArticulosTabla.getSelectedRow();
        int columna = 0;
        String num = null;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(listaArticulosTabla.getValueAt(filaa,columna));

        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Articulos WHERE codarticulo=" + (String)num);
                while(rs.next()){
                    //historiaclinicaTxt.setText(rs.getString("Historia_Clinica"));
                    idArticuloTxt.setText(rs.getString("codarticulo"));
                    detalleArticuloTxt.setText(rs.getString("descripcion"));
                    FechaIngresoArticuloTxt.setText(rs.getString("fecha"));
                    proveedorTxt.setText(rs.getString("proveedor"));
                    precioCosteTxt.setText(rs.getString("pcoste"));
                    beneficioTxt.setText(rs.getString("beneficio"));
                    precioFinalTxt.setText(rs.getString("pvp"));
                    stockTxt.setText(rs.getString("stock"));
                    precioVentaTxt.setText(rs.getString("PVenta"));
                    FechaIngresoArticuloTxt.setText(rs.getString("fecha"));
                    //********************************
                    Blob bytesImagen = rs.getBlob("Imagen");
                    byte[] img = bytesImagen.getBytes(1, (int)bytesImagen.length());
                    InputStream is = bytesImagen.getBinaryStream();
                    String filename = System.getProperty("user.home") + "/jContab/copia.jpg";
                    FileOutputStream fw = new FileOutputStream((String)filename);
                    byte bytes[] = new byte[1024];
                    int leidos = is.read(bytes);
                    while (leidos > 0) {
                       fw.write(bytes);
                       leidos = is.read(bytes);
                    }
                    ImagenLabel.setIcon(new ImageIcon(img));
                    //********************************
                }
                escrituraFichaArticulos();
                flagSaveFichaArticulo = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        //articulosTabbedPane.setSelectedIndex(1);
    }

    /*
     * HABILITA ESCRITURA O ACTUALIZACION de FICHA ARTICULOS
     */
    public void escrituraFichaArticulos(){
        idArticuloTxt.setEditable(false);
        proveedorTxt.setEditable(true);
        stockTxt.setEditable(true);
        precioCosteTxt.setEditable(true);
        beneficioTxt.setEditable(true);
        precioVentaTxt.setEditable(true);
        precioFinalTxt.setEditable(true);
        detalleArticuloTxt.setEditable(true);
        detalleArticuloTxt.setOpaque(true);
        FechaIngresoArticuloTxt.setEditable(true);
    }
    
    /*
     * HABILITA ESCRITURA O ACTUALIZACION de FICHA ARTICULOS
     */
    public void escrituraFichaCliente(){
        idClienteTxt.setEditable(false);
        RUCTxt.setEditable(true);
        clienteTxt.setEditable(true);
        nombreComercialTxt.setEditable(true);
        fechaClienteTxt.setEditable(true);
        ciudadClienteTxt.setEditable(true);
        direccionClienteTxt.setEditable(true);
        telefonoClienteTxt.setEditable(true);
        emailClienteTxt.setEditable(true);
    }
    
    /*
     * Actualiza tabla
     */
    final void actualizaListaClientesTablaAnchos(){
        clientesTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        clientesTable.getColumnModel().getColumn(1).setPreferredWidth(320);
        clientesTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        clientesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        clientesTable.getColumnModel().getColumn(4).setPreferredWidth(100);

        clientesTable.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        clientesTable.getColumnModel().getColumn(1).setCellRenderer(new ColorTableCellRenderer());
        clientesTable.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        clientesTable.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        clientesTable.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer());
    }
    
    
    /*
     * RENDERIZADO DE TABLAS
     */
    class ColorTableCellRenderer extends JLabel implements TableCellRenderer{
            public ColorTableCellRenderer() {
                setOpaque(true);
            }
@Override
            public Component getTableCellRendererComponent(JTable table,
                Object obj, boolean isSelected, boolean hasFocus, int row,
                int column) {
                if (row % 2 == 0) {
                    setBackground(Color.WHITE);
                }
                else {
                    setBackground(new Color(235, 232, 232));
                }

                if (hasFocus) {
                    // this cell is the anchor and the table has the focus
                    setBackground(Color.GREEN);
                }
                
                if(column == 1){
                    setHorizontalAlignment(JLabel.LEFT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 0){
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    Number value = (Number)obj;
                    setText(value.toString());
                }else if(column == 3){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                } else if(column == 2){
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else{
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }
                return this;
            }
    }
    
    /*
     * RENDERIZADO DE TABLAS
     */
    class ColorTableCellRenderer1 extends JLabel implements TableCellRenderer{
            public ColorTableCellRenderer1() {
                setOpaque(true);
            }

        @Override
            public Component getTableCellRendererComponent(JTable table,
                Object obj, boolean isSelected, boolean hasFocus, int row,
                int column) {
                if (row % 2 == 0) {
                    setBackground(Color.WHITE);
                }
                else {
                    //setBackground(Color.lightGray);
                    setBackground(new Color(235, 232, 232));
                }

                if (hasFocus) {
                    // this cell is the anchor and the table has the focus
                    setBackground(Color.GREEN);
                }
                if(column == 1){
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 0){
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    Number value = (Number)obj;
                    setText(value.toString());
                }else if(column == 2){
                    setHorizontalAlignment(JLabel.LEFT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 5){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 6){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    //setForeground(Color.red);
                    setText((String)obj);
                }else if(column == 7){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.BOLD, 12));
                    setForeground(Color.blue);
                    setText((String)obj);
                }else if(column == 8){
                    setHorizontalAlignment(JLabel.LEFT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 3){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.BOLD, 12));
                    setText((String)obj);
                } else{
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }


                return this;
            }
    }
    
    /*
     * RENDERIZADO JTEXTAREA EN TABLA
     */
    public class TextAreaRenderer extends JTextArea implements TableCellRenderer{
        public TextAreaRenderer() {
            setLineWrap(true);
            setWrapStyleWord(true);
            setBorder(BorderFactory.createLineBorder(new Color(235, 232, 232)));
            //setBorder(BorderFactory.createEmptyBorder());
        }
        @Override
        public Component getTableCellRendererComponent(JTable table,
            Object obj, boolean isSelected, boolean hasFocus, int row,
            int column) {

            if (row % 2 == 0) {
                setBackground(Color.WHITE);
                setBorder(BorderFactory.createLineBorder(Color.WHITE));
            }
            else {
                //setBackground(Color.lightGray);
                setBackground(new Color(235, 232, 232));
                setBorder(BorderFactory.createLineBorder(new Color(235, 232, 232)));
            }
            
            if (hasFocus) {
                    // this cell is the anchor and the table has the focus
                    this.setBackground(Color.GREEN);
            }
            setFont(new Font("Dialog", Font.PLAIN, 12));
            setText((String)obj);

            return this;
        }
    }
    
    
    /*
     * RENDERIZADO IMAGEN EN JTABLE
     */
    class ImageRenderer extends JLabel implements TableCellRenderer {
        public ImageRenderer() {
                setHorizontalAlignment(JLabel.CENTER);
        }
        //ImageIcon icon = new ImageIcon(getClass().getResource("printer.png"));   
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
        boolean hasFocus, int row, int column) {
            //setIcon(icon);
            setIcon(new ImageIcon((byte[])value));
            return this;
        }
    }
    
    /*
     * RENDERIZADO DE TABLAS
     */   
    class CheckBoxRenderer extends JCheckBox implements TableCellRenderer {
          CheckBoxRenderer() {
            setHorizontalAlignment(JLabel.CENTER);
          }

        @Override
          public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            
            if (row % 2 == 0) {
                setBackground(Color.WHITE);
            }
            else {
                //setBackground(Color.lightGray);
                setBackground(new Color(235, 232, 232));
            }
            
            if (hasFocus) {
                    // this cell is the anchor and the table has the focus
                    setBackground(Color.GREEN);
            }
            if(value.toString().equals("true")){
                //setBackground(new Color(153, 204, 255));
                setSelected(true);
            }else{
                //setBackground(Color.WHITE);
                setSelected(false);
            }
                 
            return this;
          }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        buscarClienteComboBox = new javax.swing.JComboBox();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jLabel4 = new javax.swing.JLabel();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        buscarClienteTxt = new javax.swing.JTextField();
        jSeparator15 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt = new javax.swing.JButton();
        jSeparator82 = new javax.swing.JToolBar.Separator();
        jSeparator84 = new javax.swing.JToolBar.Separator();
        jSeparator85 = new javax.swing.JToolBar.Separator();
        jSeparator86 = new javax.swing.JToolBar.Separator();
        jSeparator87 = new javax.swing.JToolBar.Separator();
        nuevoClienteButton = new javax.swing.JButton();
        jSeparator45 = new javax.swing.JToolBar.Separator();
        borrarFichaClienteButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        guardarClienteButton = new javax.swing.JButton();
        jSeparator47 = new javax.swing.JToolBar.Separator();
        crearFacturaClienteButton = new javax.swing.JButton();
        jSeparator46 = new javax.swing.JToolBar.Separator();
        jScrollPane1 = new javax.swing.JScrollPane();
        clientesTable = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        idClienteTxt = new javax.swing.JTextField();
        clienteTxt = new javax.swing.JTextField();
        RUCTxt = new javax.swing.JTextField();
        nombreComercialTxt = new javax.swing.JTextField();
        fechaClienteTxt = new javax.swing.JTextField();
        ciudadClienteTxt = new javax.swing.JTextField();
        direccionClienteTxt = new javax.swing.JTextField();
        telefonoClienteTxt = new javax.swing.JTextField();
        emailClienteTxt = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        idFacturaTxt = new javax.swing.JTextField();
        clienteFacturaTxt = new javax.swing.JTextField();
        rucTxt = new javax.swing.JTextField();
        direccionTxt = new javax.swing.JTextField();
        fechaTxt = new javax.swing.JTextField();
        ciudadTxt = new javax.swing.JTextField();
        telefonoTxt = new javax.swing.JTextField();
        jScrollPane9 = new javax.swing.JScrollPane();
        facturaArticulosClienteTable = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        numeroSerieTxt = new javax.swing.JTextField();
        jToolBar3 = new javax.swing.JToolBar();
        jSeparator18 = new javax.swing.JToolBar.Separator();
        jLabel3 = new javax.swing.JLabel();
        jSeparator20 = new javax.swing.JToolBar.Separator();
        buscarFacturaClienteTxt = new javax.swing.JTextField();
        jSeparator21 = new javax.swing.JToolBar.Separator();
        cleanFacturaButton = new javax.swing.JButton();
        jSeparator23 = new javax.swing.JToolBar.Separator();
        fechaBuscarRadioButton = new javax.swing.JRadioButton();
        jSeparator22 = new javax.swing.JToolBar.Separator();
        buscarFechaTxt = new javax.swing.JTextField();
        jSeparator48 = new javax.swing.JToolBar.Separator();
        jScrollPane5 = new javax.swing.JScrollPane();
        listaFacturaClientesTabla = new javax.swing.JTable();
        jToolBar4 = new javax.swing.JToolBar();
        jSeparator17 = new javax.swing.JToolBar.Separator();
        nuevaFacturaButton = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        borrarFacturaClienteButton = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        guardarFacturaButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        agregarArticuloButton = new javax.swing.JButton();
        jSeparator25 = new javax.swing.JToolBar.Separator();
        eliminarArticuloButton = new javax.swing.JButton();
        jSeparator27 = new javax.swing.JToolBar.Separator();
        jSeparator24 = new javax.swing.JToolBar.Separator();
        jSeparator28 = new javax.swing.JToolBar.Separator();
        jSeparator29 = new javax.swing.JToolBar.Separator();
        jSeparator31 = new javax.swing.JToolBar.Separator();
        calucularFacturaButton = new javax.swing.JButton();
        jSeparator26 = new javax.swing.JToolBar.Separator();
        jSeparator30 = new javax.swing.JToolBar.Separator();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        jSeparator19 = new javax.swing.JToolBar.Separator();
        imprimirFacturaButton = new javax.swing.JButton();
        jSeparator83 = new javax.swing.JToolBar.Separator();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ivaTxt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        fuenteTxt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        recividoTxt = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        ivaTxt1 = new javax.swing.JTextField();
        filtrarCobradoRadioButton = new javax.swing.JRadioButton();
        jPanel13 = new javax.swing.JPanel();
        jLabel49 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        ivat12Txt = new javax.swing.JTextField();
        subtotalTxt = new javax.swing.JTextField();
        totalTxt = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        retencionTxt = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        recividoTxt1 = new javax.swing.JTextField();
        fuenteBaseTxt = new javax.swing.JTextField();
        ivaPorTxt = new javax.swing.JTextField();
        jLabel56 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        fuentePorTxt = new javax.swing.JTextField();
        cobradoTxt2 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        ivabaseTxt = new javax.swing.JTextField();
        jLabel57 = new javax.swing.JLabel();
        cobradoTxt4 = new javax.swing.JTextField();
        cobrarFacturaButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jToolBar8 = new javax.swing.JToolBar();
        jSeparator49 = new javax.swing.JToolBar.Separator();
        jLabel37 = new javax.swing.JLabel();
        jSeparator50 = new javax.swing.JToolBar.Separator();
        buscarProveedorTxt = new javax.swing.JTextField();
        jSeparator51 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt1 = new javax.swing.JButton();
        jSeparator56 = new javax.swing.JToolBar.Separator();
        jSeparator52 = new javax.swing.JToolBar.Separator();
        jSeparator88 = new javax.swing.JToolBar.Separator();
        jSeparator89 = new javax.swing.JToolBar.Separator();
        jSeparator90 = new javax.swing.JToolBar.Separator();
        nuevoProveedorButton = new javax.swing.JButton();
        jSeparator53 = new javax.swing.JToolBar.Separator();
        borrarProveedorButton = new javax.swing.JButton();
        jSeparator91 = new javax.swing.JToolBar.Separator();
        guardarProveedorButton = new javax.swing.JButton();
        jSeparator54 = new javax.swing.JToolBar.Separator();
        crearFacturaClienteButton1 = new javax.swing.JButton();
        jSeparator55 = new javax.swing.JToolBar.Separator();
        jScrollPane4 = new javax.swing.JScrollPane();
        proveedoresTable = new javax.swing.JTable();
        jPanel14 = new javax.swing.JPanel();
        idProveedorTxt = new javax.swing.JTextField();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        empresaTxt = new javax.swing.JTextField();
        jLabel40 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        contactoTextArea = new javax.swing.JTextArea();
        jLabel41 = new javax.swing.JLabel();
        ciudadProveedorTxt = new javax.swing.JTextField();
        jLabel42 = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        telefonoTextArea = new javax.swing.JTextArea();
        jLabel43 = new javax.swing.JLabel();
        emailProveedorTxt = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        wwwTxt = new javax.swing.JTextField();
        jLabel45 = new javax.swing.JLabel();
        rucProveedorTxt = new javax.swing.JTextField();
        jPanel4 = new javax.swing.JPanel();
        jToolBar10 = new javax.swing.JToolBar();
        jSeparator57 = new javax.swing.JToolBar.Separator();
        jLabel46 = new javax.swing.JLabel();
        jSeparator58 = new javax.swing.JToolBar.Separator();
        buscarFacturaProveedorTxt = new javax.swing.JTextField();
        jSeparator61 = new javax.swing.JToolBar.Separator();
        cleanFacturaButton1 = new javax.swing.JButton();
        jSeparator59 = new javax.swing.JToolBar.Separator();
        fechaBuscarRadioButton1 = new javax.swing.JRadioButton();
        jSeparator63 = new javax.swing.JToolBar.Separator();
        buscarFechaTxt1 = new javax.swing.JTextField();
        jSeparator62 = new javax.swing.JToolBar.Separator();
        jScrollPane11 = new javax.swing.JScrollPane();
        facturasProveedoresTable = new javax.swing.JTable();
        jToolBar11 = new javax.swing.JToolBar();
        jSeparator64 = new javax.swing.JToolBar.Separator();
        nuevaFacturaButton1 = new javax.swing.JButton();
        jSeparator65 = new javax.swing.JToolBar.Separator();
        borrarFacturaClienteButton1 = new javax.swing.JButton();
        jSeparator66 = new javax.swing.JToolBar.Separator();
        guardarFacturaButton1 = new javax.swing.JButton();
        jSeparator67 = new javax.swing.JToolBar.Separator();
        jSeparator68 = new javax.swing.JToolBar.Separator();
        jSeparator69 = new javax.swing.JToolBar.Separator();
        jSeparator77 = new javax.swing.JToolBar.Separator();
        jSeparator78 = new javax.swing.JToolBar.Separator();
        calucularFacturaButton1 = new javax.swing.JButton();
        jSeparator79 = new javax.swing.JToolBar.Separator();
        jPanel25 = new javax.swing.JPanel();
        jLabel71 = new javax.swing.JLabel();
        jLabel72 = new javax.swing.JLabel();
        proveedorFacturaTxt = new javax.swing.JTextField();
        fechaCompraTxt = new javax.swing.JTextField();
        jLabel77 = new javax.swing.JLabel();
        idFacturaProveedorTxt = new javax.swing.JTextField();
        jLabel78 = new javax.swing.JLabel();
        serieFacturaProveedorTxt = new javax.swing.JTextField();
        subtotalTxt1 = new javax.swing.JTextField();
        jLabel79 = new javax.swing.JLabel();
        jLabel80 = new javax.swing.JLabel();
        ivat12Txt1 = new javax.swing.JTextField();
        totalTxt1 = new javax.swing.JTextField();
        jLabel81 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        direccionProveedorTxt = new javax.swing.JTextField();
        jLabel48 = new javax.swing.JLabel();
        telefonoProveedorTxt = new javax.swing.JTextField();
        jLabel50 = new javax.swing.JLabel();
        articuloComboBox = new javax.swing.JComboBox();
        jLabel51 = new javax.swing.JLabel();
        rucFacturaProveedorTxt = new javax.swing.JTextField();
        pagarFacturaButton = new javax.swing.JButton();
        ivaTxt3 = new javax.swing.JTextField();
        jLabel65 = new javax.swing.JLabel();
        jLabel52 = new javax.swing.JLabel();
        ivaTxt2 = new javax.swing.JTextField();
        jPanel8 = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        jSeparator32 = new javax.swing.JToolBar.Separator();
        jLabel5 = new javax.swing.JLabel();
        jSeparator35 = new javax.swing.JToolBar.Separator();
        buscarArticuloTxt = new javax.swing.JTextField();
        jSeparator37 = new javax.swing.JToolBar.Separator();
        limpiarArticuloTxt = new javax.swing.JButton();
        jSeparator36 = new javax.swing.JToolBar.Separator();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaArticulosTabla = new javax.swing.JTable();
        jPanel10 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detalleArticuloTxt = new javax.swing.JTextArea();
        jLabel21 = new javax.swing.JLabel();
        proveedorTxt = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        stockTxt = new javax.swing.JTextField();
        ImagenLabel = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        precioCosteTxt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        beneficioTxt = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        precioVentaTxt = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        precioFinalTxt = new javax.swing.JTextField();
        idArticuloTxt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        FechaIngresoArticuloTxt = new javax.swing.JTextField();
        jLabel35 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        calcularArticuloButton = new javax.swing.JButton();
        jToolBar6 = new javax.swing.JToolBar();
        jSeparator38 = new javax.swing.JToolBar.Separator();
        nuevoArticuloButton = new javax.swing.JButton();
        jSeparator39 = new javax.swing.JToolBar.Separator();
        borrarFichaArticuloButton = new javax.swing.JButton();
        jSeparator40 = new javax.swing.JToolBar.Separator();
        guardarArticuloButton = new javax.swing.JButton();
        jSeparator41 = new javax.swing.JToolBar.Separator();
        jSeparator42 = new javax.swing.JToolBar.Separator();
        jSeparator43 = new javax.swing.JToolBar.Separator();
        jSeparator44 = new javax.swing.JToolBar.Separator();
        jSeparator93 = new javax.swing.JToolBar.Separator();
        agregarArticuloAFacturaButton = new javax.swing.JButton();
        jSeparator92 = new javax.swing.JToolBar.Separator();
        jPanel9 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jPanel16 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        jLabel64 = new javax.swing.JLabel();
        jLabel66 = new javax.swing.JLabel();
        jLabel67 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        jLabel70 = new javax.swing.JLabel();
        jLabel73 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jTextField4 = new javax.swing.JTextField();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jTextField7 = new javax.swing.JTextField();
        jTextField8 = new javax.swing.JTextField();
        jTextField9 = new javax.swing.JTextField();
        jTextField10 = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        jTextField11 = new javax.swing.JTextField();
        jTextField12 = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        listaContratosTabla = new javax.swing.JTable();
        jToolBar7 = new javax.swing.JToolBar();
        jSeparator34 = new javax.swing.JToolBar.Separator();
        jSeparator60 = new javax.swing.JToolBar.Separator();
        jLabel75 = new javax.swing.JLabel();
        jSeparator70 = new javax.swing.JToolBar.Separator();
        buscarClienteTxt1 = new javax.swing.JTextField();
        jSeparator71 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt2 = new javax.swing.JButton();
        jSeparator94 = new javax.swing.JToolBar.Separator();
        jSeparator95 = new javax.swing.JToolBar.Separator();
        jSeparator96 = new javax.swing.JToolBar.Separator();
        jSeparator97 = new javax.swing.JToolBar.Separator();
        jSeparator98 = new javax.swing.JToolBar.Separator();
        nuevoClienteButton1 = new javax.swing.JButton();
        jSeparator72 = new javax.swing.JToolBar.Separator();
        borrarFichaClienteButton1 = new javax.swing.JButton();
        jSeparator73 = new javax.swing.JToolBar.Separator();
        guardarClienteButton1 = new javax.swing.JButton();
        jSeparator74 = new javax.swing.JToolBar.Separator();
        jSeparator75 = new javax.swing.JToolBar.Separator();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu4 = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jSeparator33 = new javax.swing.JPopupMenu.Separator();
        reporteFacturasCobradasMenuItem = new javax.swing.JMenuItem();
        facturasNoCobradasMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        listaArticulosMenuItem = new javax.swing.JMenuItem();
        jMenuItem6 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setBorderPainted(false);
        jToolBar2.add(jSeparator3);

        buscarClienteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cliente", "RUC", "Organizacion" }));
        jToolBar2.add(buscarClienteComboBox);
        jToolBar2.add(jSeparator6);

        jLabel4.setText("Buscar:");
        jToolBar2.add(jLabel4);
        jToolBar2.add(jSeparator2);

        buscarClienteTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarClienteTxt.setMinimumSize(new java.awt.Dimension(60, 60));
        buscarClienteTxt.setPreferredSize(new java.awt.Dimension(400, 27));
        buscarClienteTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarClienteTxtCaretUpdate(evt);
            }
        });
        jToolBar2.add(buscarClienteTxt);
        jToolBar2.add(jSeparator15);

        clearBusquedaTxt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        clearBusquedaTxt.setToolTipText("Limpiar busqueda");
        clearBusquedaTxt.setFocusable(false);
        clearBusquedaTxt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearBusquedaTxt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearBusquedaTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBusquedaTxtActionPerformed(evt);
            }
        });
        jToolBar2.add(clearBusquedaTxt);
        jToolBar2.add(jSeparator82);
        jToolBar2.add(jSeparator84);
        jToolBar2.add(jSeparator85);
        jToolBar2.add(jSeparator86);
        jToolBar2.add(jSeparator87);

        nuevoClienteButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoClienteButton.setText("Nuevo");
        nuevoClienteButton.setToolTipText("Nuevo cliente");
        nuevoClienteButton.setFocusable(false);
        nuevoClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(nuevoClienteButton);
        jToolBar2.add(jSeparator45);

        borrarFichaClienteButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarFichaClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarFichaClienteButton.setText("Borrar");
        borrarFichaClienteButton.setToolTipText("Borrar cliente");
        borrarFichaClienteButton.setFocusable(false);
        borrarFichaClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarFichaClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFichaClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(borrarFichaClienteButton);
        jToolBar2.add(jSeparator1);

        guardarClienteButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarClienteButton.setText("Guardar");
        guardarClienteButton.setToolTipText("Guardar cliente");
        guardarClienteButton.setFocusable(false);
        guardarClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(guardarClienteButton);
        jToolBar2.add(jSeparator47);

        crearFacturaClienteButton.setBackground(new java.awt.Color(255, 255, 51));
        crearFacturaClienteButton.setFont(new java.awt.Font("Dialog", 1, 10));
        crearFacturaClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        crearFacturaClienteButton.setText("Factura");
        crearFacturaClienteButton.setToolTipText("Crear factura");
        crearFacturaClienteButton.setFocusable(false);
        crearFacturaClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        crearFacturaClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearFacturaClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(crearFacturaClienteButton);
        jToolBar2.add(jSeparator46);

        jScrollPane1.setBorder(null);
        jScrollPane1.setPreferredSize(new java.awt.Dimension(490, 400));

        clientesTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Cliente", "Teléfono", "R.U.C.", "Ciudad", "Dirección"
            }
        ));
        clientesTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane1.setViewportView(clientesTable);

        jLabel9.setText("Código Cliente:");

        jLabel10.setText("Cliente:");

        jLabel11.setText("R.U.C./C.I:");

        jLabel12.setText("Nombre Comercial:");

        jLabel13.setText("Fecha:");

        jLabel14.setText("Ciudad:");

        jLabel15.setText("Dirección:");

        jLabel16.setText("Teléfono:");

        jLabel17.setText("Email:");

        idClienteTxt.setEditable(false);
        idClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        fechaClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ciudadClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        telefonoClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        emailClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(telefonoClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 343, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(direccionClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fechaClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)
                            .addComponent(ciudadClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 356, Short.MAX_VALUE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nombreComercialTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 277, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(idClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel10))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(clienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE)
                            .addComponent(RUCTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 340, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(idClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(RUCTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(nombreComercialTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(fechaClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ciudadClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(direccionClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(telefonoClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(16, 16, 16)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(emailClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 1054, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 566, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(51, 51, 51))
        );

        jTabbedPane1.addTab("Clientes", new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-attending.png")), jPanel1); // NOI18N

        jLabel22.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel22.setText("Id. factura:");

        jLabel23.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel23.setText("Cliente:");

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel24.setText("R.U.C. / C.I.:");

        jLabel27.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel27.setText("Fecha venta:");

        jLabel31.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel31.setText("Ciudad:");

        jLabel32.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel32.setText("Telefono:");

        jLabel33.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel33.setText("Direccion:");

        idFacturaTxt.setEditable(false);
        idFacturaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        idFacturaTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        clienteFacturaTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        rucTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        direccionTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        fechaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fechaTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ciudadTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        telefonoTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        facturaArticulosClienteTable.setFont(new java.awt.Font("Dialog", 0, 10));
        facturaArticulosClienteTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Cant.", "Articulo", "P. Unit.", "P. Total"
            }
        ));
        facturaArticulosClienteTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane9.setViewportView(facturaArticulosClienteTable);

        jLabel34.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel34.setText("Nº Serie:");

        numeroSerieTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        numeroSerieTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 520, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(direccionTxt))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rucTxt))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clienteFacturaTxt))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idFacturaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(telefonoTxt))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ciudadTxt))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fechaTxt))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numeroSerieTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(idFacturaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel23)
                            .addComponent(clienteFacturaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel24)
                            .addComponent(rucTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel33)
                            .addComponent(direccionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel34)
                            .addComponent(numeroSerieTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel27)
                            .addComponent(fechaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel31)
                            .addComponent(ciudadTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel32)
                            .addComponent(telefonoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                .addContainerGap())
        );

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.add(jSeparator18);

        jLabel3.setText("Buscar factura:");
        jToolBar3.add(jLabel3);
        jToolBar3.add(jSeparator20);

        buscarFacturaClienteTxt.setMinimumSize(new java.awt.Dimension(4, 39));
        buscarFacturaClienteTxt.setPreferredSize(new java.awt.Dimension(300, 25));
        buscarFacturaClienteTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFacturaClienteTxtCaretUpdate(evt);
            }
        });
        jToolBar3.add(buscarFacturaClienteTxt);
        jToolBar3.add(jSeparator21);

        cleanFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        cleanFacturaButton.setToolTipText("Limpiar busqueda");
        cleanFacturaButton.setFocusable(false);
        cleanFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cleanFacturaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cleanFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar3.add(cleanFacturaButton);
        jToolBar3.add(jSeparator23);

        fechaBuscarRadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
        fechaBuscarRadioButton.setSelected(true);
        fechaBuscarRadioButton.setText("Fecha");
        fechaBuscarRadioButton.setFocusable(false);
        fechaBuscarRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fechaBuscarRadioButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(fechaBuscarRadioButton);
        jToolBar3.add(jSeparator22);

        buscarFechaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        buscarFechaTxt.setPreferredSize(new java.awt.Dimension(200, 25));
        buscarFechaTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFechaTxtCaretUpdate(evt);
            }
        });
        jToolBar3.add(buscarFechaTxt);
        jToolBar3.add(jSeparator48);

        listaFacturaClientesTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Articulo", "Stock", "P.V.P"
            }
        ));
        listaFacturaClientesTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        listaFacturaClientesTabla.setMaximumSize(new java.awt.Dimension(310, 64));
        listaFacturaClientesTabla.setMinimumSize(new java.awt.Dimension(310, 64));
        jScrollPane5.setViewportView(listaFacturaClientesTabla);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.add(jSeparator17);

        nuevaFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevaFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevaFacturaButton.setText("Nueva");
        nuevaFacturaButton.setToolTipText("Nueva factura");
        nuevaFacturaButton.setFocusable(false);
        nuevaFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevaFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(nuevaFacturaButton);
        jToolBar4.add(jSeparator10);

        borrarFacturaClienteButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarFacturaClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarFacturaClienteButton.setText("Borrar");
        borrarFacturaClienteButton.setToolTipText("Borrar factura");
        borrarFacturaClienteButton.setFocusable(false);
        borrarFacturaClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarFacturaClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFacturaClienteButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(borrarFacturaClienteButton);
        jToolBar4.add(jSeparator11);

        guardarFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarFacturaButton.setText("Guardar");
        guardarFacturaButton.setToolTipText("Guardar factura");
        guardarFacturaButton.setFocusable(false);
        guardarFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(guardarFacturaButton);
        jToolBar4.add(jSeparator8);
        jToolBar4.add(jSeparator9);
        jToolBar4.add(jSeparator7);
        jToolBar4.add(jSeparator12);
        jToolBar4.add(jSeparator13);

        agregarArticuloButton.setFont(new java.awt.Font("Dialog", 1, 10));
        agregarArticuloButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/insert-table.png"))); // NOI18N
        agregarArticuloButton.setText("Add");
        agregarArticuloButton.setToolTipText("Agregar un artículo a la factura");
        agregarArticuloButton.setFocusable(false);
        agregarArticuloButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        agregarArticuloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarArticuloButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(agregarArticuloButton);
        jToolBar4.add(jSeparator25);

        eliminarArticuloButton.setFont(new java.awt.Font("Dialog", 1, 10));
        eliminarArticuloButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-table-delete-row.png"))); // NOI18N
        eliminarArticuloButton.setText("Del");
        eliminarArticuloButton.setToolTipText("Borrar un artículo de la factura");
        eliminarArticuloButton.setFocusable(false);
        eliminarArticuloButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        eliminarArticuloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eliminarArticuloButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(eliminarArticuloButton);
        jToolBar4.add(jSeparator27);
        jToolBar4.add(jSeparator24);
        jToolBar4.add(jSeparator28);
        jToolBar4.add(jSeparator29);
        jToolBar4.add(jSeparator31);

        calucularFacturaButton.setBackground(new java.awt.Color(255, 255, 51));
        calucularFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        calucularFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/accessories-calculator.png"))); // NOI18N
        calucularFacturaButton.setText("Calcular factura");
        calucularFacturaButton.setToolTipText("Computar factura");
        calucularFacturaButton.setFocusable(false);
        calucularFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        calucularFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calucularFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(calucularFacturaButton);
        jToolBar4.add(jSeparator26);
        jToolBar4.add(jSeparator30);
        jToolBar4.add(jSeparator14);
        jToolBar4.add(jSeparator16);
        jToolBar4.add(jSeparator19);

        imprimirFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        imprimirFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/printer.png"))); // NOI18N
        imprimirFacturaButton.setText("Imprimir");
        imprimirFacturaButton.setToolTipText("Imprimir factura");
        imprimirFacturaButton.setFocusable(false);
        imprimirFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        imprimirFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                imprimirFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(imprimirFacturaButton);
        jToolBar4.add(jSeparator83);

        jLabel6.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel6.setForeground(new java.awt.Color(153, 153, 153));
        jLabel6.setText("I.V.A.");

        ivaTxt.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        ivaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        ivaTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel7.setForeground(new java.awt.Color(153, 153, 153));
        jLabel7.setText("R.Fuente");

        fuenteTxt.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        fuenteTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fuenteTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel8.setForeground(new java.awt.Color(153, 153, 153));
        jLabel8.setText("Recivido");

        recividoTxt.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        recividoTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        recividoTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel18.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel18.setForeground(new java.awt.Color(153, 153, 153));
        jLabel18.setText("Ventas");

        ivaTxt1.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        ivaTxt1.setFont(new java.awt.Font("Dialog", 0, 10));
        ivaTxt1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        filtrarCobradoRadioButton.setFont(new java.awt.Font("Dialog", 1, 10));
        filtrarCobradoRadioButton.setText("Filtrar cobrados");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(filtrarCobradoRadioButton)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(recividoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE))
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fuenteTxt))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ivaTxt))
                        .addGroup(jPanel5Layout.createSequentialGroup()
                            .addComponent(jLabel18)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(ivaTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(filtrarCobradoRadioButton)
                .addGap(13, 13, 13)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(ivaTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(ivaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(fuenteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(recividoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel49.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel49.setText("Subtotal $");

        jLabel54.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel54.setText("I.V.A. T. 12% $");

        ivat12Txt.setFont(new java.awt.Font("Dialog", 0, 10));
        ivat12Txt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        subtotalTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        subtotalTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        totalTxt.setBackground(new java.awt.Color(255, 255, 51));
        totalTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        totalTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel55.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel55.setText("Total US $");

        jLabel62.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel62.setText("Retencion $");

        retencionTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        retencionTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel63.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel63.setText("Recivido $");

        recividoTxt1.setFont(new java.awt.Font("Dialog", 0, 10));
        recividoTxt1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        fuenteBaseTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fuenteBaseTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fuenteBaseTxt.setText("0.00");

        ivaPorTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        ivaPorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ivaPorTxt.setText("30");
        ivaPorTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ivaPorTxtCaretUpdate(evt);
            }
        });

        jLabel56.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel56.setText("I.V.A.:");

        jLabel58.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel58.setText("IMPUESTOS Y RENTENCIONES:");

        jLabel61.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel61.setText("retenido");

        fuentePorTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fuentePorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fuentePorTxt.setText("1");
        fuentePorTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fuentePorTxtCaretUpdate(evt);
            }
        });

        cobradoTxt2.setFont(new java.awt.Font("Dialog", 0, 10));
        cobradoTxt2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cobradoTxt2.setText("0.00");
        cobradoTxt2.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                cobradoTxt2CaretUpdate(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel59.setText("base");

        jLabel60.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel60.setText("%");

        ivabaseTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        ivabaseTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ivabaseTxt.setText("0.00");
        ivabaseTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ivabaseTxtCaretUpdate(evt);
            }
        });

        jLabel57.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel57.setText("Renta:");

        cobradoTxt4.setFont(new java.awt.Font("Dialog", 0, 10));
        cobradoTxt4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cobradoTxt4.setText("0.00");
        cobradoTxt4.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                cobradoTxt4CaretUpdate(evt);
            }
        });

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel55)
                            .addComponent(jLabel62)
                            .addComponent(jLabel63)
                            .addComponent(jLabel49)
                            .addComponent(jLabel54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(recividoTxt1)
                            .addComponent(retencionTxt)
                            .addComponent(totalTxt)
                            .addComponent(ivat12Txt)
                            .addComponent(subtotalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel13Layout.createSequentialGroup()
                        .addGap(49, 49, 49)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel58)
                            .addGroup(jPanel13Layout.createSequentialGroup()
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addComponent(jLabel57)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fuenteBaseTxt))
                                    .addGroup(jPanel13Layout.createSequentialGroup()
                                        .addComponent(jLabel56)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel59)
                                            .addComponent(ivabaseTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(fuentePorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                                    .addComponent(jLabel60)
                                    .addComponent(ivaPorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(cobradoTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cobradoTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel61))))))
                .addGap(30, 30, 30))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jLabel58)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel59)
                    .addComponent(jLabel60)
                    .addComponent(jLabel61))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(ivabaseTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(ivaPorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cobradoTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(fuentePorTxt)
                    .addComponent(fuenteBaseTxt)
                    .addComponent(cobradoTxt4)
                    .addComponent(jLabel57))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 27, Short.MAX_VALUE)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(subtotalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ivat12Txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel55)
                    .addComponent(totalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(retencionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel62))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(recividoTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel63))
                .addGap(41, 41, 41))
        );

        cobrarFacturaButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        cobrarFacturaButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/dialog-ok-apply.png"))); // NOI18N
        cobrarFacturaButton1.setText("Cobrar factura");
        cobrarFacturaButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cobrarFacturaButton1.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        cobrarFacturaButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cobrarFacturaButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cobrarFacturaButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(46, 46, 46)
                        .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 711, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cobrarFacturaButton1)))
                .addContainerGap())
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
            .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cobrarFacturaButton1)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jTabbedPane1.addTab("Facturas venta", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png")), jPanel2); // NOI18N

        jToolBar8.setFloatable(false);
        jToolBar8.setRollover(true);
        jToolBar8.setBorderPainted(false);
        jToolBar8.add(jSeparator49);

        jLabel37.setText("Buscar proveedor:");
        jToolBar8.add(jLabel37);
        jToolBar8.add(jSeparator50);

        buscarProveedorTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarProveedorTxt.setPreferredSize(new java.awt.Dimension(400, 25));
        buscarProveedorTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarProveedorTxtCaretUpdate(evt);
            }
        });
        jToolBar8.add(buscarProveedorTxt);
        jToolBar8.add(jSeparator51);

        clearBusquedaTxt1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        clearBusquedaTxt1.setToolTipText("Limpiar busqueda");
        clearBusquedaTxt1.setFocusable(false);
        clearBusquedaTxt1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearBusquedaTxt1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearBusquedaTxt1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBusquedaTxt1ActionPerformed(evt);
            }
        });
        jToolBar8.add(clearBusquedaTxt1);
        jToolBar8.add(jSeparator56);
        jToolBar8.add(jSeparator52);
        jToolBar8.add(jSeparator88);
        jToolBar8.add(jSeparator89);
        jToolBar8.add(jSeparator90);

        nuevoProveedorButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoProveedorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoProveedorButton.setText("Nuevo");
        nuevoProveedorButton.setToolTipText("Nuevo cliente");
        nuevoProveedorButton.setFocusable(false);
        nuevoProveedorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoProveedorButtonActionPerformed(evt);
            }
        });
        jToolBar8.add(nuevoProveedorButton);
        jToolBar8.add(jSeparator53);

        borrarProveedorButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarProveedorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarProveedorButton.setText("Borrar");
        borrarProveedorButton.setToolTipText("Borrar cliente");
        borrarProveedorButton.setFocusable(false);
        borrarProveedorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarProveedorButtonActionPerformed(evt);
            }
        });
        jToolBar8.add(borrarProveedorButton);
        jToolBar8.add(jSeparator91);

        guardarProveedorButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarProveedorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarProveedorButton.setText("Guardar");
        guardarProveedorButton.setToolTipText("Guardar cliente");
        guardarProveedorButton.setFocusable(false);
        guardarProveedorButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarProveedorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarProveedorButtonActionPerformed(evt);
            }
        });
        jToolBar8.add(guardarProveedorButton);
        jToolBar8.add(jSeparator54);

        crearFacturaClienteButton1.setBackground(new java.awt.Color(255, 255, 51));
        crearFacturaClienteButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        crearFacturaClienteButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        crearFacturaClienteButton1.setText("Factura");
        crearFacturaClienteButton1.setToolTipText("Crear factura");
        crearFacturaClienteButton1.setFocusable(false);
        crearFacturaClienteButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        crearFacturaClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearFacturaClienteButton1ActionPerformed(evt);
            }
        });
        jToolBar8.add(crearFacturaClienteButton1);
        jToolBar8.add(jSeparator55);

        proveedoresTable.setFont(new java.awt.Font("Dialog", 0, 14));
        proveedoresTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Cliente", "Teléfono", "R.U.C.", "Ciudad", "Dirección"
            }
        ));
        proveedoresTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane4.setViewportView(proveedoresTable);

        idProveedorTxt.setEditable(false);
        idProveedorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel38.setText("id:");
        jLabel38.setEnabled(false);

        jLabel39.setText("Empresa:");

        jLabel40.setText("Contacto/s:");

        contactoTextArea.setColumns(20);
        contactoTextArea.setRows(5);
        jScrollPane6.setViewportView(contactoTextArea);

        jLabel41.setText("Ciudad:");

        jLabel42.setText("Teléfono/s:");

        telefonoTextArea.setColumns(20);
        telefonoTextArea.setRows(5);
        jScrollPane7.setViewportView(telefonoTextArea);

        jLabel43.setText("Email:");

        jLabel44.setText("Direccion:");

        jLabel45.setText("R.U.C.:");

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel39)
                            .addComponent(jLabel38))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(empresaTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)
                            .addComponent(idProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 333, Short.MAX_VALUE)))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel41)
                            .addComponent(jLabel44)
                            .addComponent(jLabel45)
                            .addComponent(jLabel43)
                            .addComponent(jLabel40)
                            .addComponent(jLabel42))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(emailProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(rucProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(ciudadProveedorTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
                            .addComponent(wwwTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(idProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(empresaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(rucProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel45))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ciudadProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel41))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(wwwTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(emailProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel43))
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jLabel40)
                        .addGap(88, 88, 88)
                        .addComponent(jLabel42))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(26, 26, 26))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar8, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jToolBar8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 488, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(55, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Proveedores", new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-participant.png")), jPanel6); // NOI18N

        jToolBar10.setFloatable(false);
        jToolBar10.setRollover(true);
        jToolBar10.add(jSeparator57);

        jLabel46.setText("Buscar factura:");
        jToolBar10.add(jLabel46);
        jToolBar10.add(jSeparator58);

        buscarFacturaProveedorTxt.setMinimumSize(new java.awt.Dimension(400, 39));
        buscarFacturaProveedorTxt.setPreferredSize(new java.awt.Dimension(400, 39));
        buscarFacturaProveedorTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFacturaProveedorTxtCaretUpdate(evt);
            }
        });
        jToolBar10.add(buscarFacturaProveedorTxt);
        jToolBar10.add(jSeparator61);

        cleanFacturaButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        cleanFacturaButton1.setToolTipText("Limpiar busqueda");
        cleanFacturaButton1.setFocusable(false);
        cleanFacturaButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        cleanFacturaButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cleanFacturaButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanFacturaButton1ActionPerformed(evt);
            }
        });
        jToolBar10.add(cleanFacturaButton1);
        jToolBar10.add(jSeparator59);

        fechaBuscarRadioButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        fechaBuscarRadioButton1.setSelected(true);
        fechaBuscarRadioButton1.setText("Fecha");
        fechaBuscarRadioButton1.setFocusable(false);
        fechaBuscarRadioButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fechaBuscarRadioButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar10.add(fechaBuscarRadioButton1);
        jToolBar10.add(jSeparator63);

        buscarFechaTxt1.setFont(new java.awt.Font("Dialog", 0, 10));
        buscarFechaTxt1.setMinimumSize(new java.awt.Dimension(200, 25));
        buscarFechaTxt1.setPreferredSize(new java.awt.Dimension(200, 25));
        buscarFechaTxt1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFechaTxt1CaretUpdate(evt);
            }
        });
        jToolBar10.add(buscarFechaTxt1);
        jToolBar10.add(jSeparator62);

        facturasProveedoresTable.setFont(new java.awt.Font("Dialog", 0, 14));
        facturasProveedoresTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Cliente", "Teléfono", "R.U.C.", "Ciudad", "Dirección"
            }
        ));
        facturasProveedoresTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane11.setViewportView(facturasProveedoresTable);

        jToolBar11.setFloatable(false);
        jToolBar11.setRollover(true);
        jToolBar11.add(jSeparator64);

        nuevaFacturaButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevaFacturaButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevaFacturaButton1.setText("Nueva");
        nuevaFacturaButton1.setToolTipText("Nueva factura");
        nuevaFacturaButton1.setFocusable(false);
        nuevaFacturaButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevaFacturaButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaFacturaButton1ActionPerformed(evt);
            }
        });
        jToolBar11.add(nuevaFacturaButton1);
        jToolBar11.add(jSeparator65);

        borrarFacturaClienteButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarFacturaClienteButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarFacturaClienteButton1.setText("Borrar");
        borrarFacturaClienteButton1.setToolTipText("Borrar factura");
        borrarFacturaClienteButton1.setFocusable(false);
        borrarFacturaClienteButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarFacturaClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFacturaClienteButton1ActionPerformed(evt);
            }
        });
        jToolBar11.add(borrarFacturaClienteButton1);
        jToolBar11.add(jSeparator66);

        guardarFacturaButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarFacturaButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarFacturaButton1.setText("Guardar");
        guardarFacturaButton1.setToolTipText("Guardar factura");
        guardarFacturaButton1.setFocusable(false);
        guardarFacturaButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarFacturaButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarFacturaButton1ActionPerformed(evt);
            }
        });
        jToolBar11.add(guardarFacturaButton1);
        jToolBar11.add(jSeparator67);
        jToolBar11.add(jSeparator68);
        jToolBar11.add(jSeparator69);
        jToolBar11.add(jSeparator77);
        jToolBar11.add(jSeparator78);

        calucularFacturaButton1.setBackground(new java.awt.Color(255, 255, 51));
        calucularFacturaButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        calucularFacturaButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/accessories-calculator.png"))); // NOI18N
        calucularFacturaButton1.setText("Calcular");
        calucularFacturaButton1.setToolTipText("Computar factura");
        calucularFacturaButton1.setFocusable(false);
        calucularFacturaButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        calucularFacturaButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calucularFacturaButton1ActionPerformed(evt);
            }
        });
        jToolBar11.add(calucularFacturaButton1);
        jToolBar11.add(jSeparator79);

        jLabel71.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel71.setText("Proveedor:");

        jLabel72.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel72.setText("Fecha compra:");

        proveedorFacturaTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        fechaCompraTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fechaCompraTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel77.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel77.setText("id:");
        jLabel77.setEnabled(false);

        idFacturaProveedorTxt.setEditable(false);
        idFacturaProveedorTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        idFacturaProveedorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel78.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel78.setText("Factura SERIE Nº :");

        serieFacturaProveedorTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        serieFacturaProveedorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        subtotalTxt1.setFont(new java.awt.Font("Dialog", 0, 10));
        subtotalTxt1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel79.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel79.setText("Subtotal $");

        jLabel80.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel80.setText("I.V.A. 12% $");

        ivat12Txt1.setFont(new java.awt.Font("Dialog", 0, 10));
        ivat12Txt1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        totalTxt1.setBackground(new java.awt.Color(255, 255, 51));
        totalTxt1.setFont(new java.awt.Font("Dialog", 0, 10));
        totalTxt1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel81.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel81.setText("Total US $");

        jLabel47.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel47.setText("Dirección:");

        direccionProveedorTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        jLabel48.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel48.setText("Telefono:");

        telefonoProveedorTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        jLabel50.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel50.setText("Artículo:");

        articuloComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
        articuloComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bienes y servicios con IVA 12%", "Bienes y servicios con IVA 0%", "Activos fijos con IVA 12%", "Activos fijos con IVA 0%", " " }));

        jLabel51.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel51.setText("R.U.C.:");

        rucFacturaProveedorTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel77)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 98, Short.MAX_VALUE)
                        .addComponent(jLabel78)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(serieFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel51)
                            .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel25Layout.createSequentialGroup()
                                    .addComponent(jLabel50)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(articuloComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(rucFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 235, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel25Layout.createSequentialGroup()
                                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel47)
                                            .addComponent(jLabel71))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(proveedorFacturaTxt)
                                            .addComponent(direccionProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE))))))
                        .addGap(54, 54, 54)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel81)
                                    .addComponent(jLabel80)
                                    .addComponent(jLabel79))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(totalTxt1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(ivat12Txt1, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(subtotalTxt1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel48)
                                    .addComponent(jLabel72))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(telefonoProveedorTxt, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(fechaCompraTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(16, 16, 16))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(idFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serieFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 23, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel51)
                            .addComponent(rucFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel71)
                            .addComponent(proveedorFacturaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel47)
                            .addComponent(direccionProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel50)
                            .addComponent(articuloComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fechaCompraTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel72))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(telefonoProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(subtotalTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel79))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ivat12Txt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel80))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(totalTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel81))))
                .addGap(6, 6, 6))
        );

        pagarFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        pagarFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/dialog-ok-apply.png"))); // NOI18N
        pagarFacturaButton.setText("Pagar factura");
        pagarFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        pagarFacturaButton.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        pagarFacturaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pagarFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagarFacturaButtonActionPerformed(evt);
            }
        });

        ivaTxt3.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        ivaTxt3.setFont(new java.awt.Font("Dialog", 0, 10));
        ivaTxt3.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel65.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel65.setForeground(new java.awt.Color(153, 153, 153));
        jLabel65.setText("Compras");

        jLabel52.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel52.setForeground(new java.awt.Color(153, 153, 153));
        jLabel52.setText("I.V.A.");

        ivaTxt2.setBackground(javax.swing.UIManager.getDefaults().getColor("Button.background"));
        ivaTxt2.setFont(new java.awt.Font("Dialog", 0, 10));
        ivaTxt2.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 757, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 73, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(pagarFacturaButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel52)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ivaTxt2))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                        .addComponent(jLabel65)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ivaTxt3, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(61, 61, 61))
            .addComponent(jToolBar10, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
            .addComponent(jToolBar11, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(414, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jToolBar10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(64, 64, 64)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel65)
                            .addComponent(ivaTxt3, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel52)
                            .addComponent(ivaTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(28, 28, 28)
                        .addComponent(pagarFacturaButton))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane11, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Facturas compras", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png")), jPanel4); // NOI18N

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.add(jSeparator32);

        jLabel5.setText("Buscar artículo:");
        jToolBar5.add(jLabel5);
        jToolBar5.add(jSeparator35);

        buscarArticuloTxt.setPreferredSize(new java.awt.Dimension(400, 25));
        buscarArticuloTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarArticuloTxtCaretUpdate(evt);
            }
        });
        jToolBar5.add(buscarArticuloTxt);
        jToolBar5.add(jSeparator37);

        limpiarArticuloTxt.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        limpiarArticuloTxt.setToolTipText("Limpiar busqueda");
        limpiarArticuloTxt.setFocusable(false);
        limpiarArticuloTxt.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        limpiarArticuloTxt.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        limpiarArticuloTxt.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                limpiarArticuloTxtActionPerformed(evt);
            }
        });
        jToolBar5.add(limpiarArticuloTxt);
        jToolBar5.add(jSeparator36);

        listaArticulosTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Articulo", "Stock", "P.V.P"
            }
        ));
        listaArticulosTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(listaArticulosTabla);

        jLabel19.setText("ID Articulo:");

        detalleArticuloTxt.setColumns(10);
        detalleArticuloTxt.setEditable(false);
        detalleArticuloTxt.setLineWrap(true);
        detalleArticuloTxt.setRows(5);
        detalleArticuloTxt.setWrapStyleWord(true);
        jScrollPane2.setViewportView(detalleArticuloTxt);

        jLabel21.setText("Proveedor:");

        proveedorTxt.setEditable(false);

        jLabel25.setText("Stock:");

        stockTxt.setEditable(false);
        stockTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                stockTxtFocusLost(evt);
            }
        });

        ImagenLabel.setToolTipText("Cargar fotos de 96x96 pixeles o 2,5 x 2,5 cm");
        ImagenLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ImagenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ImagenLabelMouseClicked(evt);
            }
        });

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Precios"));

        jLabel26.setText("P. Coste:");

        precioCosteTxt.setEditable(false);
        precioCosteTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel28.setText("Beneficio %:");

        beneficioTxt.setEditable(false);
        beneficioTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        beneficioTxt.setText("20");

        jLabel29.setText("P. Venta:");

        precioVentaTxt.setBackground(new java.awt.Color(204, 255, 204));
        precioVentaTxt.setEditable(false);
        precioVentaTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        precioVentaTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel30.setText("P.V.P:");

        precioFinalTxt.setBackground(new java.awt.Color(153, 255, 0));
        precioFinalTxt.setEditable(false);
        precioFinalTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(jLabel26)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(precioCosteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE))
                        .addGroup(jPanel11Layout.createSequentialGroup()
                            .addComponent(jLabel28)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(beneficioTxt)))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precioVentaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precioFinalTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(precioCosteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(beneficioTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel28))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(precioVentaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel30)
                    .addComponent(precioFinalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        idArticuloTxt.setEditable(false);
        idArticuloTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel36.setText("Fecha Ingreso:");

        FechaIngresoArticuloTxt.setEditable(false);
        FechaIngresoArticuloTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel35.setText("Imagen:");

        jLabel20.setText("Detalle Articulo:");

        calcularArticuloButton.setFont(new java.awt.Font("Dialog", 1, 10));
        calcularArticuloButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/accessories-calculator.png"))); // NOI18N
        calcularArticuloButton.setText("Calcular");
        calcularArticuloButton.setFocusPainted(false);
        calcularArticuloButton.setHideActionText(true);
        calcularArticuloButton.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        calcularArticuloButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        calcularArticuloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calcularArticuloButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idArticuloTxt))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel25)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(stockTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 233, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel36)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(FechaIngresoArticuloTxt))
                            .addGroup(jPanel10Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(proveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 288, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel20)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(ImagenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel10Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel35)
                        .addGap(36, 36, 36)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(calcularArticuloButton)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel19)
                            .addComponent(idArticuloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel21)
                            .addComponent(proveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(stockTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel25)
                            .addComponent(jLabel36)
                            .addComponent(FechaIngresoArticuloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20)
                            .addComponent(ImagenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel35))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(calcularArticuloButton)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar6.setFloatable(false);
        jToolBar6.setRollover(true);
        jToolBar6.add(jSeparator38);

        nuevoArticuloButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoArticuloButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoArticuloButton.setText("Nuevo");
        nuevoArticuloButton.setToolTipText("Nuevo artículo");
        nuevoArticuloButton.setFocusable(false);
        nuevoArticuloButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoArticuloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoArticuloButtonActionPerformed(evt);
            }
        });
        jToolBar6.add(nuevoArticuloButton);
        jToolBar6.add(jSeparator39);

        borrarFichaArticuloButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarFichaArticuloButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarFichaArticuloButton.setText("Borrar");
        borrarFichaArticuloButton.setToolTipText("Eliminar artículo");
        borrarFichaArticuloButton.setFocusable(false);
        borrarFichaArticuloButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarFichaArticuloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFichaArticuloButtonActionPerformed(evt);
            }
        });
        jToolBar6.add(borrarFichaArticuloButton);
        jToolBar6.add(jSeparator40);

        guardarArticuloButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarArticuloButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarArticuloButton.setText("Guardar");
        guardarArticuloButton.setToolTipText("Guardar articulo");
        guardarArticuloButton.setFocusable(false);
        guardarArticuloButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarArticuloButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarArticuloButtonActionPerformed(evt);
            }
        });
        jToolBar6.add(guardarArticuloButton);
        jToolBar6.add(jSeparator41);
        jToolBar6.add(jSeparator42);
        jToolBar6.add(jSeparator43);
        jToolBar6.add(jSeparator44);
        jToolBar6.add(jSeparator93);

        agregarArticuloAFacturaButton.setBackground(new java.awt.Color(255, 255, 51));
        agregarArticuloAFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        agregarArticuloAFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/news-subscribe.png"))); // NOI18N
        agregarArticuloAFacturaButton.setText("factura");
        agregarArticuloAFacturaButton.setToolTipText("Agregar articulo a factura");
        agregarArticuloAFacturaButton.setFocusable(false);
        agregarArticuloAFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        agregarArticuloAFacturaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        agregarArticuloAFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                agregarArticuloAFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar6.add(agregarArticuloAFacturaButton);
        jToolBar6.add(jSeparator92);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar6, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(166, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 734, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(149, 149, 149))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Articulos", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-barcode.png")), jPanel8); // NOI18N

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1049, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 574, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Equipos", new javax.swing.ImageIcon(getClass().getResource("/mypack/hwinfo.png")), jPanel9); // NOI18N

        jLabel1.setText("Empresa:");

        jLabel2.setText("Ciudad:");

        jLabel53.setText("Dirección:");

        jLabel64.setText("Teléfono:");

        jLabel66.setText("Fecha firma del contrato:");

        jLabel67.setText("Tipo contrato:");

        jLabel68.setText("Numero equipos:");

        jLabel70.setText("Duración contrato:");

        jLabel73.setText("Fecha envio insumos/mes:");

        jTextField8.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jTextField9.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel74.setText("Cantidad insumos:");

        jTextField11.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel69.setText("Precio:");

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField6))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField5))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel53)
                            .addComponent(jLabel64))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jTextField3)
                            .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
                            .addComponent(jTextField4)))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2)))
                .addGap(142, 142, 142)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel67)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel74)
                                .addComponent(jLabel68))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField11)
                                .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGap(18, 18, 18)
                            .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField12, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                                .addComponent(jLabel69)
                                .addComponent(jTextField9)))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel16Layout.createSequentialGroup()
                            .addComponent(jLabel73)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(jTextField10, javax.swing.GroupLayout.DEFAULT_SIZE, 204, Short.MAX_VALUE))))
                .addContainerGap(47, Short.MAX_VALUE))
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel67)
                        .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel53)
                            .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel64)
                            .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel66)
                            .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel70)
                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel69)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel68)
                            .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel74)
                            .addComponent(jTextField11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel73)
                            .addComponent(jTextField10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        listaContratosTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Codigo", "Articulo", "Stock", "P.V.P"
            }
        ));
        listaContratosTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane8.setViewportView(listaContratosTabla);

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);
        jToolBar7.setBorderPainted(false);
        jToolBar7.add(jSeparator34);
        jToolBar7.add(jSeparator60);

        jLabel75.setText("Buscar:");
        jToolBar7.add(jLabel75);
        jToolBar7.add(jSeparator70);

        buscarClienteTxt1.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarClienteTxt1.setMinimumSize(new java.awt.Dimension(60, 60));
        buscarClienteTxt1.setPreferredSize(new java.awt.Dimension(400, 27));
        buscarClienteTxt1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarClienteTxt1CaretUpdate(evt);
            }
        });
        jToolBar7.add(buscarClienteTxt1);
        jToolBar7.add(jSeparator71);

        clearBusquedaTxt2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        clearBusquedaTxt2.setToolTipText("Limpiar busqueda");
        clearBusquedaTxt2.setFocusable(false);
        clearBusquedaTxt2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearBusquedaTxt2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearBusquedaTxt2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBusquedaTxt2ActionPerformed(evt);
            }
        });
        jToolBar7.add(clearBusquedaTxt2);
        jToolBar7.add(jSeparator94);
        jToolBar7.add(jSeparator95);
        jToolBar7.add(jSeparator96);
        jToolBar7.add(jSeparator97);
        jToolBar7.add(jSeparator98);

        nuevoClienteButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoClienteButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoClienteButton1.setText("Nuevo");
        nuevoClienteButton1.setToolTipText("Nuevo cliente");
        nuevoClienteButton1.setFocusable(false);
        nuevoClienteButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButton1ActionPerformed(evt);
            }
        });
        jToolBar7.add(nuevoClienteButton1);
        jToolBar7.add(jSeparator72);

        borrarFichaClienteButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarFichaClienteButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarFichaClienteButton1.setText("Borrar");
        borrarFichaClienteButton1.setToolTipText("Borrar cliente");
        borrarFichaClienteButton1.setFocusable(false);
        borrarFichaClienteButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarFichaClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFichaClienteButton1ActionPerformed(evt);
            }
        });
        jToolBar7.add(borrarFichaClienteButton1);
        jToolBar7.add(jSeparator73);

        guardarClienteButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarClienteButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarClienteButton1.setText("Guardar");
        guardarClienteButton1.setToolTipText("Guardar cliente");
        guardarClienteButton1.setFocusable(false);
        guardarClienteButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarClienteButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButton1ActionPerformed(evt);
            }
        });
        jToolBar7.add(guardarClienteButton1);
        jToolBar7.add(jSeparator74);
        jToolBar7.add(jSeparator75);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 761, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel16, javax.swing.GroupLayout.DEFAULT_SIZE, 1025, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jToolBar7, javax.swing.GroupLayout.DEFAULT_SIZE, 1049, Short.MAX_VALUE))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 79, Short.MAX_VALUE)
                .addComponent(jPanel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel15Layout.createSequentialGroup()
                    .addGap(274, 274, 274)
                    .addComponent(jToolBar7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(275, Short.MAX_VALUE)))
        );

        jTabbedPane1.addTab("Contratos", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-investment.png")), jPanel15); // NOI18N

        jMenu4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/logo_pequeño.png"))); // NOI18N
        jMenu4.setText(" jContab 3.1");
        jMenu4.setContentAreaFilled(false);
        jMenu4.setFocusable(false);
        jMenu4.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenu4.setRequestFocusEnabled(false);
        jMenu4.setVerifyInputWhenFocusTarget(false);

        jMenuItem7.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem7.setText("jContab 3.1 - Software ERP para pequeñas empresas.");
        jMenuItem7.setContentAreaFilled(false);
        jMenu4.add(jMenuItem7);

        jMenuBar1.add(jMenu4);

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/server-database.png"))); // NOI18N
        jMenu1.setText("Base Datos");
        jMenu1.setFont(new java.awt.Font("Dialog", 0, 12));

        jMenuItem1.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        jMenuItem1.setText("Copiar");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-export-table.png"))); // NOI18N
        jMenuItem2.setText("Restaurar");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        jMenu3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/documentinfo.png"))); // NOI18N
        jMenu3.setText("Info");
        jMenu3.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuBar1.add(jMenu3);

        jMenu2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        jMenu2.setText("Reportes");
        jMenu2.setFont(new java.awt.Font("Dialog", 0, 12));

        jMenuItem5.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-investment.png"))); // NOI18N
        jMenuItem5.setText("Ingresos y Gastos");
        jMenu2.add(jMenuItem5);
        jMenu2.add(jSeparator33);

        reporteFacturasCobradasMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        reporteFacturasCobradasMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/news-subscribe.png"))); // NOI18N
        reporteFacturasCobradasMenuItem.setText("Facturas venta cobradas");
        reporteFacturasCobradasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                reporteFacturasCobradasMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(reporteFacturasCobradasMenuItem);

        facturasNoCobradasMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        facturasNoCobradasMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/news-unsubscribe.png"))); // NOI18N
        facturasNoCobradasMenuItem.setText("Facturas venta x cobrar");
        facturasNoCobradasMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                facturasNoCobradasMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(facturasNoCobradasMenuItem);
        jMenu2.add(jSeparator4);

        jMenuItem3.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/news-subscribe.png"))); // NOI18N
        jMenuItem3.setText("Facturas compra pagadas");
        jMenu2.add(jMenuItem3);

        jMenuItem4.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/news-unsubscribe.png"))); // NOI18N
        jMenuItem4.setText("Facturas compra x pagar");
        jMenu2.add(jMenuItem4);
        jMenu2.add(jSeparator5);

        listaArticulosMenuItem.setFont(new java.awt.Font("Dialog", 0, 12));
        listaArticulosMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-barcode.png"))); // NOI18N
        listaArticulosMenuItem.setText("Lista artículos");
        listaArticulosMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                listaArticulosMenuItemActionPerformed(evt);
            }
        });
        jMenu2.add(listaArticulosMenuItem);

        jMenuItem6.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-attending.png"))); // NOI18N
        jMenuItem6.setText("Lista Clientes");
        jMenu2.add(jMenuItem6);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 1054, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
     * Método checkDriver()
     * chequea si esta disponible driver en el sistema
     * retorna true si encuentra driver
     * retorna false si no esta disponible el driver
     */
    public void checkDriver(){
        try {
            Class.forName("com.mysql.jdbc.Driver");
            System.out.println("JDBC driver found");
            flagDriver = 1;
        } catch(java.lang.ClassNotFoundException e){
            System.err.print("JDBC driver no found ") ;
            System.err.println(e.getMessage()) ;
            flagDriver = 0;
        }
    }
    
    /*
     * Lee clientes de la base de datos y los ennumera en lista
     */
    public void leerClientes(String findClave, String find){
        clientesTable.removeAll();
        clientesTable.updateUI();
        String[] columnNames = {"Código",
                                "Cliente",
                                "Ciudad",
                                "R.U.C.",
                                "Telefono"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        conectarBD(baseDatos);
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM Clientes WHERE " + findClave + " LIKE '%" + (String)find+"%'");
            while(rs.next()){
                Object[] row = new Object[5];
                row[0] = rs.getObject("id");
                row[1] = rs.getObject("Cliente");
                row[2] = rs.getObject("Ciudad");
                row[3] = rs.getObject("RUC");
                row[4] = rs.getObject("Telefono");
                               
                model.addRow(row);
            }
            clientesTable.setModel(model);
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Error de lectura en lista de clientes",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
        
    }
    
    /*
     * Metodo connectToDB()
     * conecta a base de datos
     */
    void conectarBD(String DBName){
        try {
            connection = DriverManager.getConnection(DBName);
            //System.out.println("DataBase connected");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    private void buscarClienteTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarClienteTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarClienteTxt.getText();
        String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
        //leerClienteToDB(buscarClave, buscar);
        leerClientes(buscarClave, buscar);
        actualizaListaClientesTablaAnchos();
    }//GEN-LAST:event_buscarClienteTxtCaretUpdate

    private void clearBusquedaTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxtActionPerformed
        // TODO add your handling code here:
        buscarClienteTxt.setText("");
    }//GEN-LAST:event_clearBusquedaTxtActionPerformed

    private void guardarClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButtonActionPerformed
        // TODO add your handling code here:
        if(flagSaveFichaCliente == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaClienteTxt.getText().isEmpty()) && !clienteTxt.getText().isEmpty()){
                guardarFichaClienteBD(); //guarda ficha cliente en base datos
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Cliente no guardado.\n\n Llenar todos los datos",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaClienteTxt.getText().isEmpty()) && !clienteTxt.getText().isEmpty()){
                actualizarFichaClienteBD();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Cliente no actualizado",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarClienteButtonActionPerformed

    /*
     * Actualizar datos de cliente en la base de datos
     */
    void actualizarFichaClienteBD(){
        //custom title, warning icon
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                    statement = connection.createStatement();
                    int actualizar = statement.executeUpdate("UPDATE Clientes SET " +
                    "Cliente='" + clienteTxt.getText()+"', "+
                    "RUC='" + RUCTxt.getText()+"', "+
                    "Direccion='" + direccionClienteTxt.getText()+"', "+
                    "Telefono='" + telefonoClienteTxt.getText()+"', "+
                    "Fecha_Emision='" + fechaClienteTxt.getText()+"', "+
                    "Ciudad='" + ciudadClienteTxt.getText()+"', "+
                    "Organizacion='" + nombreComercialTxt.getText()+"', "+
                    "Email='" + emailClienteTxt.getText()+"'"+
                    " WHERE id =" + (String)idClienteTxt.getText());

                statement.close();
                connection.close();

                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha de cliente actualizada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaCliente = 1; //actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha de cliente no se actualizo",
                        "Error - 3", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    /*
     * GUARDAR FICHA CLIENTES NUEVA
     */
    void guardarFichaClienteBD(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO Clientes " +
                        "(Cliente, RUC, Direccion, Telefono, Fecha_Emision, Ciudad, Organizacion, Email) " +
                    "VALUES(" +
                    "'"+clienteTxt.getText()+"', "+
                    "'"+RUCTxt.getText()+"', "+
                    "'"+direccionClienteTxt.getText()+"', "+
                    "'"+telefonoClienteTxt.getText()+"', "+
                    "'"+fechaClienteTxt.getText()+"', "+
                    "'"+ciudadClienteTxt.getText()+"', "+
                    "'"+nombreComercialTxt.getText()+"', "+
                    "'"+emailClienteTxt.getText()+"'"+
                ")");
                statement.close();
                connection.close();

                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Ficha de cliente guardada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaCliente = 1; //guardado y listo para actualizar
                leerClientes("Cliente", ""); //Vuelva a cargar listado de clientes
                actualizaListaClientesTablaAnchos();
    
            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha de cliente no se guardo",
                        "Error - 2", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    private void nuevoClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        limpiaFichaCliente();
        escrituraFichaCliente();
        fechaClienteTxt.setText(hoy);
    }//GEN-LAST:event_nuevoClienteButtonActionPerformed

    private void borrarFichaClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarFichaClienteButtonActionPerformed
        // TODO add your handling code here:
        //Necesita password de super usuario
        JPasswordField jpf = new JPasswordField();
        JOptionPane.showConfirmDialog(null, jpf, "Password administrador:   ", JOptionPane.OK_CANCEL_OPTION);
        char[] input = jpf.getPassword();
        char[] correctPassword = { '1','2'};
        boolean isCorrect = true;
        isCorrect = Arrays.equals(input, correctPassword);
        if(isCorrect){
            //Password Valido procede a borrar
            if (!idClienteTxt.getText().isEmpty()){
                borrarFichaClienteBD();
                limpiaFichaCliente();
                leerClientes("Cliente", ""); //Vuelve a cargar listado de clientes
                actualizaListaClientesTablaAnchos();
            }
        }else{
            JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Password no valido",
                        "Error - 1", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarFichaClienteButtonActionPerformed

    private void crearFacturaClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearFacturaClienteButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        
        //escrituraFichaFactura();
        //cargaFichaCliente();
        jTabbedPane1.setSelectedIndex(1);
        //Tomo informacion para crear factura
        clienteFacturaTxt.setText(clienteTxt.getText());
        rucTxt.setText(RUCTxt.getText());
        fechaTxt.setText(hoy);
        direccionTxt.setText(direccionClienteTxt.getText());
        telefonoTxt.setText(telefonoClienteTxt.getText());
        ciudadTxt.setText(ciudadClienteTxt.getText());
        leerUltimaFactura();
}//GEN-LAST:event_crearFacturaClienteButtonActionPerformed

    private void ivabaseTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ivabaseTxtCaretUpdate
        // TODO add your handling code here
    }//GEN-LAST:event_ivabaseTxtCaretUpdate

    private void cobrarFacturaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cobrarFacturaButton1ActionPerformed
        // TODO add your handling code here:
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                    statement = connection.createStatement();
                    int actualizar = statement.executeUpdate("UPDATE FacturasClientes SET " +
                    "Cliente='"+clienteFacturaTxt.getText()+"' ,"+
                    "RUC='"+rucTxt.getText()+"' ,"+
                    "Cobrado='"+"true"+"' ,"+
                    "Subtotal='"+subtotalTxt.getText()+"' ,"+
                    "Iva='"+ivat12Txt.getText()+"' ,"+
                    "Total='"+totalTxt.getText()+"' ,"+
                    "Fuente='"+retencionTxt.getText()+"' ,"+
                    "Recivido='"+recividoTxt1.getText()+"' ,"+
                    "Fecha='"+fechaTxt.getText()+"' ,"+
                    "Nserie='"+numeroSerieTxt.getText()+"' ,"+
                    "Direccion='"+direccionTxt.getText()+"' ,"+
                    "Ciudad='"+ciudadTxt.getText()+"' ,"+
                    "Telefono='"+telefonoTxt.getText()+"'"+
                    " WHERE id =" + (String)idFacturaTxt.getText());

                    statement.close();
                    connection.close();

                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura cobrada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaCliente = 1; //actualizar

            } catch (Exception e) {
                System.out.println("ERROR: "+e);
            }
        }
        
    }//GEN-LAST:event_cobrarFacturaButton1ActionPerformed

    private void buscarFacturaClienteTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarFacturaClienteTxtCaretUpdate
        // TODO add your handling code here:
        String buscarClave;
        String buscar = buscarFacturaClienteTxt.getText();
        buscarClave = "Cliente";        
        
        String find1;
        if(fechaBuscarRadioButton.isSelected()){
            
            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
        
        leerClientesFactura(buscarClave, buscar, find1);
        actualizaListaFacturasClientesTablaAnchos();
    }//GEN-LAST:event_buscarFacturaClienteTxtCaretUpdate

    private void cleanFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanFacturaButtonActionPerformed
        // TODO add your handling code here:
        buscarFacturaClienteTxt.setText("");
    }//GEN-LAST:event_cleanFacturaButtonActionPerformed

    private void buscarFechaTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarFechaTxtCaretUpdate
        // TODO add your handling code here:
        String buscarClave;
        String buscar = buscarFacturaClienteTxt.getText();
        buscarClave = "Cliente";        
        
        String find1;
        if(fechaBuscarRadioButton.isSelected()){
            
            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
        
        leerClientesFactura(buscarClave, buscar, find1);
        actualizaListaFacturasClientesTablaAnchos();
    }//GEN-LAST:event_buscarFechaTxtCaretUpdate

    private void ivaPorTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ivaPorTxtCaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_ivaPorTxtCaretUpdate

    private void cobradoTxt2CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_cobradoTxt2CaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_cobradoTxt2CaretUpdate

    private void fuentePorTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_fuentePorTxtCaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_fuentePorTxtCaretUpdate

    private void cobradoTxt4CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_cobradoTxt4CaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_cobradoTxt4CaretUpdate

    private void calucularFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calucularFacturaButtonActionPerformed
        // TODO add your handling code here:
         //Obtengo datos de la tabla para computar datos
        String cant;
        Integer cantidad;
        String pUnit;
        Double pUnitario;
        Double res;
        Double res2,res3;
        //NumberFormat nf = NumberFormat.getInstance();
        DecimalFormat nf = new DecimalFormat("####.##");
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

         //Obtener número de columnas y computar datos
        for (int i = 0, rows = facturaArticulosClienteTable.getRowCount(); i < rows; i++)
        {
                cant = (String) facturaArticulosClienteTable.getValueAt(i, 0).toString();
                cantidad = Integer.parseInt(cant);
                pUnit = (String) facturaArticulosClienteTable.getValueAt(i, 2).toString();
                pUnitario = Double.valueOf(pUnit);
                res = cantidad * pUnitario;
                facturaArticulosClienteTable.setValueAt(nf.format(res), i, 3);
        }
        Double subtotal;
        res = 0.00;
         for (int i = 0, rows = facturaArticulosClienteTable.getRowCount(); i < rows; i++)
        {
                //Calculo de subtotal
                pUnit = (String) facturaArticulosClienteTable.getValueAt(i, 3).toString();
                subtotal = Double.valueOf(pUnit);
                res = res + subtotal;
        }
        subtotalTxt.setText(nf.format(res));
        Double iva12;
        res2 = (res*0.12);
        ivat12Txt.setText(nf.format(res2));
        res3 = res2 + res;
        totalTxt.setText(nf.format(res3));
        //pendienteTxt.setText(nf.format(res3));
        ivabaseTxt.setText(ivat12Txt.getText());
        fuenteBaseTxt.setText(subtotalTxt.getText());
        
        res = Double.valueOf(ivabaseTxt.getText());
        res2 = Double.valueOf(ivaPorTxt.getText());
        res3 = res * (res2/100);
        cobradoTxt2.setText(nf.format(res3));
        
        res = Double.valueOf(fuenteBaseTxt.getText());
        res2 = Double.valueOf(fuentePorTxt.getText());
        res3 = res * (res2/100);
        cobradoTxt4.setText(nf.format(res3));
        
        res = Double.valueOf(cobradoTxt2.getText());
        res2 = Double.valueOf(cobradoTxt4.getText());
        res3 = res + res2;
        retencionTxt.setText(nf.format(res3));
        
        res = Double.valueOf(retencionTxt.getText());
        res2 = Double.valueOf(totalTxt.getText());
        res3 = res2 - res;
        recividoTxt1.setText(nf.format(res3));
    }//GEN-LAST:event_calucularFacturaButtonActionPerformed

    private void buscarArticuloTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarArticuloTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarArticuloTxt.getText();
        
        leerArticulos(buscar);
        actualizaListaArticulosTablaAnchos();
}//GEN-LAST:event_buscarArticuloTxtCaretUpdate

    /*
     * ES NUEMERICA CADENA
     */
    private boolean isNumeric(String cadena){
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe){
            return false;
        }
    }
      
    private void stockTxtFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_stockTxtFocusLost
        // TODO add your handling code here:
        if(isNumeric(stockTxt.getText())== true){
            System.out.println("Numero correcto");
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame,
                    "Stock debe contener solo numeros",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_stockTxtFocusLost

    private void ImagenLabelMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_ImagenLabelMouseClicked
        // TODO add your handling code here:
        cargaImagen();
}//GEN-LAST:event_ImagenLabelMouseClicked

    
    /*
     * CARGAR IMAGEN
     */
    public void cargaImagen(){
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){

            //Open file
            File file = fc.getSelectedFile();
            System.out.println(file);
            fila = file.toString();

            try {
                image = ImageIO.read(file);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,e.getMessage(),
                "File error",JOptionPane.ERROR_MESSAGE);
            }
            ImagenLabel.setIcon(new ImageIcon(image));
        }
    }
    
    private void calcularArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calcularArticuloButtonActionPerformed
        // TODO add your handling code here:
        //Verifica si hay datos para computar
        if(!precioCosteTxt.getText().isEmpty()){
            Double precio_coste = Double.valueOf(precioCosteTxt.getText());
            Double beneficio = Double.valueOf(beneficioTxt.getText());
            Double precio_venta = (precio_coste*(beneficio/100))+precio_coste;
            String temp;
            DecimalFormat nf = new DecimalFormat("####.##");
            //NumberFormat nf = NumberFormat.getInstance();
            nf.setMinimumFractionDigits(2);
            nf.setMaximumFractionDigits(2);
            temp = nf.format(precio_venta);
            precioVentaTxt.setText(temp);
            
            Double iva = (0.12 * precio_venta);
            Double precio_iva = iva + precio_venta;;
            temp = nf.format(precio_iva);
            precioFinalTxt.setText(temp);
        }else{
            //NO COMPUTA NADA
            System.out.println("No hay datos para computar.");
        }
}//GEN-LAST:event_calcularArticuloButtonActionPerformed

    private void limpiarArticuloTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_limpiarArticuloTxtActionPerformed
        // TODO add your handling code here:
        buscarArticuloTxt.setText("");
    }//GEN-LAST:event_limpiarArticuloTxtActionPerformed

    private void nuevoArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoArticuloButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        limpiaFichaArticulos();
        escrituraFichaArticulos();
        FechaIngresoArticuloTxt.setText(hoy);
    }//GEN-LAST:event_nuevoArticuloButtonActionPerformed

    private void nuevaFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaFacturaButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        limpiaFichaFactura();
        escrituraFichaFactura();
        fechaTxt.setText(hoy);
        leerUltimaFactura();
         
        int cont = 0;
        if(facturaArticulosClienteTable.getRowCount() != 0){
            do{
                model1.removeRow(facturaArticulosClienteTable.getRowCount()-1);
                facturaArticulosClienteTable.removeAll();
                facturaArticulosClienteTable.updateUI();
                facturaArticulosClienteTable.setModel(model1);
                actualizaTablaFacturaClienteAnchos();
                cont = model1.getRowCount();
            }while(cont != 0);
            facturaArticulosClienteTable.removeAll();
            facturaArticulosClienteTable.updateUI();  
        }
}//GEN-LAST:event_nuevaFacturaButtonActionPerformed

    
    /*
     * Leer ultimo numero de factura emitida
     */
    public void leerUltimaFactura(){
        numeroSerieTxt.setText("0000");
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM FacturasClientes");
                //while(conectarBD.rs.last()){
                rs.last();
                String cant;
                Integer res;
                //NumberFormat nf = NumberFormat.getInstance();
                DecimalFormat nf = new DecimalFormat("####.##");
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(2);


                cant = rs.getString("Nserie");
                res = Integer.valueOf(cant)+1;
                numeroSerieTxt.setText(numeroSerieTxt.getText()+(String)res.toString());

                //}
                rs.close();
                statement.close();
                connection.close();

            } catch (Exception e) {
                System.out.println("Error de lectura");
                System.out.println(e);
            }
        }
    }

    private void agregarArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarArticuloButtonActionPerformed
        // TODO add your handling code here:
        jTabbedPane1.setSelectedIndex(4);
                //articuloTabbedPane.setSelectedIndex(1);
    }//GEN-LAST:event_agregarArticuloButtonActionPerformed

    private void agregarArticuloAFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_agregarArticuloAFacturaButtonActionPerformed
        // TODO add your handling code here:
        //verificar si ventana de factura esta abierta
        Object[] row = new Object[4];
        row[0] = "1";
        row[1] = detalleArticuloTxt.getText();
        row[2] = precioVentaTxt.getText();
        row[3] = "0.00";
        model1.addRow(row);
 
        facturaArticulosClienteTable.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        facturaArticulosClienteTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        facturaArticulosClienteTable.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        facturaArticulosClienteTable.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        facturaArticulosClienteTable.setModel(model1);
        actualizaTablaFacturaClienteAnchos();
        jTabbedPane1.setSelectedIndex(1);
    }//GEN-LAST:event_agregarArticuloAFacturaButtonActionPerformed

    private void eliminarArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarArticuloButtonActionPerformed
        // TODO add your handling code here:
        int cont = model1.getRowCount();
        if(cont != 0){
            System.out.println("Eliminar articulo");
            model1.removeRow(facturaArticulosClienteTable.getRowCount()-1);
            facturaArticulosClienteTable.removeAll();
            facturaArticulosClienteTable.updateUI();
            facturaArticulosClienteTable.setModel(model1);
            actualizaTablaFacturaClienteAnchos();
        }
    }//GEN-LAST:event_eliminarArticuloButtonActionPerformed

    private void guardarFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarFacturaButtonActionPerformed
        // TODO add your handling code here:
        if(flagSaveFichaFacturaCliente == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaTxt.getText().isEmpty()) && !numeroSerieTxt.getText().isEmpty()){
                guardarFichaFacturaClienteBD(); //guarda ficha cliente en base datos
                leerClientesFactura("Cliente", "", "");
                actualizaListaFacturasClientesTablaAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura no guardada, llenar todos los datos",
                        "Error -5", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaTxt.getText().isEmpty()) && !numeroSerieTxt.getText().isEmpty()){
                actualizarFichaFacturaCliente();
                leerClientesFactura("Cliente", "", "");
                actualizaListaFacturasClientesTablaAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura no actualizada",
                        "Error -6", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarFacturaButtonActionPerformed

    private void borrarFacturaClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarFacturaClienteButtonActionPerformed
        // TODO add your handling code here:
        //Necesita password de super usuario
        JPasswordField jpf = new JPasswordField();
        JOptionPane.showConfirmDialog(null, jpf, "Password administrador: ", JOptionPane.OK_CANCEL_OPTION);
        char[] input = jpf.getPassword();
        char[] correctPassword = { '1','2'};
        boolean isCorrect = true;
        isCorrect = Arrays.equals(input, correctPassword);
        if(isCorrect){
            //Password Valido procede a borrar
            if (!idFacturaTxt.getText().isEmpty()){
                borrarFichaFactura();
                limpiaFichaFactura();                         
                facturaArticulosClienteTable.removeAll();
                facturaArticulosClienteTable.updateUI();   
                int cont = 0;
                if(facturaArticulosClienteTable.getRowCount() != 0){
                    do{
                        model1.removeRow(facturaArticulosClienteTable.getRowCount()-1);
                        facturaArticulosClienteTable.removeAll();
                        facturaArticulosClienteTable.updateUI();
                        facturaArticulosClienteTable.setModel(model1);
                        actualizaTablaFacturaClienteAnchos();
                        cont = model1.getRowCount();
                    }while(cont != 0);
                }
                leerClientesFactura("Cliente", "", "");
                actualizaListaFacturasClientesTablaAnchos();
            }
        }else{
            JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Error 1: al borrar, password no valido",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarFacturaClienteButtonActionPerformed

    private void borrarFichaArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarFichaArticuloButtonActionPerformed
        // TODO add your handling code here:
        //Necesita password de super usuario
        JPasswordField jpf = new JPasswordField();
        JOptionPane.showConfirmDialog(null, jpf, "Password administrador: ", JOptionPane.OK_CANCEL_OPTION);
        char[] input = jpf.getPassword();
        char[] correctPassword = { '1','2'};
        boolean isCorrect = true;
        isCorrect = Arrays.equals(input, correctPassword);
        if(isCorrect){
            //Password Valido procede a borrar
            if (!idArticuloTxt.getText().isEmpty()){
                borrarFichaArticulo();
                limpiaFichaArticulos();
                leerArticulos("");
                actualizaListaArticulosTablaAnchos();
            }
        }else{
            JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Error 1: al borrar, password no valido",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarFichaArticuloButtonActionPerformed

    private void guardarArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarArticuloButtonActionPerformed
        // TODO add your handling code here:
        if(flagSaveFichaArticulo == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!FechaIngresoArticuloTxt.getText().isEmpty()) && !detalleArticuloTxt.getText().isEmpty()){
                guardarFichaArticuloBD(); //guarda ficha cliente en base datos
                leerArticulos("");
                actualizaListaArticulosTablaAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Artículo no guardado, llenar todos los datos",
                        "Error -10", JOptionPane.ERROR_MESSAGE);
            }   
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!idArticuloTxt.getText().isEmpty()) && !detalleArticuloTxt.getText().isEmpty()){
                actualizarFichaArticulo();           
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Artículo no actualizado \nCargar imagen",
                        "Error - 11", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarArticuloButtonActionPerformed

    private void imprimirFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_imprimirFacturaButtonActionPerformed
        // TODO add your handling code here:
        if(!totalTxt.getText().isEmpty()){
            //System.out.println("Imprimiendo reporte...");
            String filename = System.getProperty("user.home") + "/jContab/ImpresionFactura.jasper";

             //Obtener número de columnas y computar datos
            String cantidad = " ";
            String articulo = " ";
            String pUnitario = " ";
            String pTotal= " ";

            ParticipantesDatasource datasource = new ParticipantesDatasource();
            for (int i = 0, rows = facturaArticulosClienteTable.getRowCount(); i < rows; i++)
            {
                    cantidad = (String) facturaArticulosClienteTable.getValueAt(i, 0).toString();
                    articulo = (String) facturaArticulosClienteTable.getValueAt(i, 1).toString();
                    pUnitario = (String) facturaArticulosClienteTable.getValueAt(i, 2).toString();
                    pTotal = (String) facturaArticulosClienteTable.getValueAt(i, 3).toString();
                    Participante p = new Participante(fechaTxt.getText(), clienteFacturaTxt.getText(), rucTxt.getText(), direccionTxt.getText(), telefonoTxt.getText(), cantidad, articulo, pUnitario, pTotal, subtotalTxt.getText(), ivat12Txt.getText(), totalTxt.getText(), ciudadTxt.getText());
                    datasource.addParticipante(p);
            }

            //System.out.println("Imprimiendo..");
            //custom title, warning icon
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame,
                        "Imprimiendo factura...",
                        "Información", JOptionPane.WARNING_MESSAGE);
                
            try {
                JasperReport reporte = (JasperReport) JRLoader.loadObject(filename);
                JasperPrint jasperPrint = JasperFillManager.fillReport(reporte, null,datasource);
                JasperViewer jv = new JasperViewer(jasperPrint, false);
                jv.setVisible(true);

            } catch (Exception e) {
                //System.out.println(e);
            }
        }
    }//GEN-LAST:event_imprimirFacturaButtonActionPerformed

    private void buscarProveedorTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarProveedorTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarProveedorTxt.getText();
        leerProveedorBD("Empresa", buscar);
        actualizaTablaProveedoresAnchos();
    }//GEN-LAST:event_buscarProveedorTxtCaretUpdate

    /*
     * Actualiza tabla
     */
    void actualizaTablaProveedoresAnchos(){
        proveedoresTable.getColumnModel().getColumn(0).setPreferredWidth(80);
        proveedoresTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        proveedoresTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        proveedoresTable.getColumnModel().getColumn(3).setPreferredWidth(138);
        proveedoresTable.getColumnModel().getColumn(4).setPreferredWidth(180);
                

        proveedoresTable.setRowHeight(40);

        proveedoresTable.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        proveedoresTable.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        proveedoresTable.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        proveedoresTable.getColumnModel().getColumn(3).setCellRenderer(new TextAreaRenderer());   
        proveedoresTable.getColumnModel().getColumn(4).setCellRenderer(new TextAreaRenderer());   
    }

    
    /*
     * Lee base de datos leerToDB() de acuerdo a cierta busqueda
     */
    void leerProveedorBD(String findClave, String find){
        proveedoresTable.removeAll();
        proveedoresTable.updateUI();
        String[] columnNames = {"Código",
                                "Empresa",
                                "Ciudad",
                                "Contactos",
                                "Telefono/s"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        if(flagDriver ==1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Proveedores WHERE " + "Empresa" + " LIKE '%" + (String)find+"%'");
                while(rs.next()){                    
                    Object[] row = new Object[9];
                    row[0] = rs.getObject("id");
                    row[1] = rs.getObject("Empresa");
                    row[2] = rs.getObject("Ciudad");
                    row[3] = rs.getObject("Contacto");
                    row[4] = rs.getObject("Telefono");
                    model.addRow(row);
                }
                proveedoresTable.setModel(model);

            } catch (Exception e) {
                System.out.println("Error de lectura");
                System.out.println(e);
            }
        }
    }

    private void clearBusquedaTxt1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxt1ActionPerformed
        // TODO add your handling code here:
        buscarProveedorTxt.setText("");
    }//GEN-LAST:event_clearBusquedaTxt1ActionPerformed

    private void nuevoProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoProveedorButtonActionPerformed
        // TODO add your handling code here:
        limpiaFichaProveedor();       
        flagSaveFichaProveedor = 0;   //Guardar dato
    }//GEN-LAST:event_nuevoProveedorButtonActionPerformed

    private void borrarProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarProveedorButtonActionPerformed
        // TODO add your handling code here:
        //Necesita password de super usuario
        JPasswordField jpf = new JPasswordField();
        JOptionPane.showConfirmDialog(null, jpf, "Clave de administrador", JOptionPane.OK_CANCEL_OPTION);
        // jpf.getPassword();
        char[] input = jpf.getPassword();
        char[] correctPassword = { '1','2'};
        boolean isCorrect = true;
        isCorrect = Arrays.equals(input, correctPassword);
        if(isCorrect){
            //Password Valido procede a borrar
            ////chequea si es posible borrar
            if (!idProveedorTxt.getText().isEmpty()){
                borrarFichaProveedor();
                limpiaFichaProveedor();
            }
        }
    }//GEN-LAST:event_borrarProveedorButtonActionPerformed

    
    /*
     * Borrar proveedor de la base de datos
     */
    void borrarFichaProveedor(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM Proveedores WHERE id=" + (String)idProveedorTxt.getText());
                System.out.println("Erased");
                statement.close();
                connection.close();
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Proveedor borrado",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "No se borro proveedor",
                        "Error - 12", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * Limpia ficha de proveedor
     */
    void limpiaFichaProveedor(){
        idProveedorTxt.setText("");
        empresaTxt.setText("");
        contactoTextArea.setText("");
        telefonoTextArea.setText("");
        emailProveedorTxt.setText("");
        ciudadProveedorTxt.setText("");
        wwwTxt.setText("");
        rucProveedorTxt.setText("");
    }
    
    private void guardarProveedorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarProveedorButtonActionPerformed
        // TODO add your handling code here:
        if (flagSaveFichaProveedor == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!empresaTxt.getText().isEmpty()) && !telefonoTextArea.getText().isEmpty()){
                guardarProveedorNuevo();
            }
        }else{
            //ACTUALIZA
            //chequea si hay escrito los datos
           if((!empresaTxt.getText().isEmpty()) && !telefonoTextArea.getText().isEmpty()){
                actualizarProveedor();
            }
        }
    }//GEN-LAST:event_guardarProveedorButtonActionPerformed

    
     /*
     * Actualizar datos de proveedores en la base de datos
     */
    void actualizarProveedor(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int actualizar = statement.executeUpdate("UPDATE Proveedores SET " +
                    "Empresa='" + empresaTxt.getText()+"', "+
                    "Contacto='" + contactoTextArea.getText()+"', "+
                    "Email='" + emailProveedorTxt.getText()+"', "+
                    "Telefono='" + telefonoTextArea.getText()+"', "+
                    "Ciudad='" + ciudadProveedorTxt.getText()+"', "+
                    "Direccion='" + wwwTxt.getText()+"', "+
                    "RUC='" + rucProveedorTxt.getText()+"'"+
                    " WHERE id =" + (String)idProveedorTxt.getText());

                statement.close();
                connection.close();
                leerProveedorBD("Empresa", "");
                actualizaTablaProveedoresAnchos();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Proveedor actualizado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                leerProveedorBD("Empresa", "");
                actualizaTablaProveedoresAnchos();

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Proveedor no se actualizo",
                        "Error - 14", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
     /*
     * Guarda en base de datos nueva ficha de proveedor
     */
    void guardarProveedorNuevo(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO Proveedores " +
                        "(Empresa, Contacto, Email, Telefono, Ciudad, Direccion, RUC) " +
                    "VALUES(" +
                    "'"+empresaTxt.getText()+"' ,"+
                    "'"+contactoTextArea.getText()+"' ,"+
                    "'"+emailProveedorTxt.getText()+"' ,"+
                    "'"+telefonoTextArea.getText()+"' ,"+
                    "'"+ciudadProveedorTxt.getText()+"' ,"+
                    "'"+wwwTxt.getText()+"' ,"+
                    "'"+rucProveedorTxt.getText()+"'"+
                ")");
                flagSaveFichaProveedor = 1;
                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Proveedor guardado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                leerProveedorBD("Empresa", "");
                actualizaTablaProveedoresAnchos();
            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha de proveedor no se guardo",
                        "Error - 13", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void crearFacturaClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearFacturaClienteButton1ActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        
        //escrituraFichaFactura();
        //cargaFichaCliente();
        jTabbedPane1.setSelectedIndex(3);
        //Tomo informacion para crear factura
        proveedorFacturaTxt.setText(empresaTxt.getText());
        rucFacturaProveedorTxt.setText(rucProveedorTxt.getText());
        fechaCompraTxt.setText(hoy);
        direccionProveedorTxt.setText(wwwTxt.getText());
        //telefonoProveedorTxt.setText(telefonoTextArea.getText());
        subtotalTxt1.setText("0.00");
    }//GEN-LAST:event_crearFacturaClienteButton1ActionPerformed

    private void buscarFacturaProveedorTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarFacturaProveedorTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarFacturaProveedorTxt.getText();
        String find2;
        if(fechaBuscarRadioButton1.isSelected()){
            
            find2 = buscarFechaTxt1.getText();
        }else{
            find2 = "";
        }
        
        leerFacturaProveedorBD("Proveedor", buscar, find2);
        actualizaTablaFacturasProveedoresAnchos();
    }//GEN-LAST:event_buscarFacturaProveedorTxtCaretUpdate

    
     /*
     * Lee base de datos leerToDB() de acuerdo a cierta busqueda
     */
    void leerFacturaProveedorBD(String findClave, String find, String findClave1){
        //leerClientesFactura("Cliente", "", find1);
        facturasProveedoresTable.removeAll();
        facturasProveedoresTable.updateUI();
        String[] columnNames = {"Código",
                                "Proveedor",
                                "Fecha",
                                "Factura #",
                                "Total",
                                "Pagado",
                                "IVA"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                //rs = statement.executeQuery("SELECT * FROM FacturasClientes WHERE " + findClave + " LIKE '%" + (String)find+"%'" + "AND Fecha"+ " LIKE '%" + (String)findClave1 +"%'");
                rs = statement.executeQuery("SELECT * FROM FacturaProveedores WHERE " + findClave + " LIKE '%" + (String)find+"%'"+ "AND Fecha"+ " LIKE '%" + (String)findClave1 +"%'");
                while(rs.next()){
                    Object[] row = new Object[7];
                    row[0] = rs.getObject("id");
                    row[1] = rs.getObject("Proveedor");
                    row[2] = rs.getObject("Fecha");
                    row[3] = rs.getObject("Nserie");
                    row[4] = rs.getObject("Total");
                    row[5] = rs.getObject("Pagado");
                    row[6] = rs.getObject("IVA");  
                    model.addRow(row);
                }
                facturasProveedoresTable.setModel(model);
                rs.close();
                statement.close();
                connection.close();
                
                //Computo de totales de iva, fuente, recivido
                //______________________________
                DecimalFormat nf = new DecimalFormat("####.##");
                nf.setMinimumFractionDigits(2);
                nf.setMaximumFractionDigits(2);
                
                String iva = "";
                String total = "";
                Double res = 0.00;
                Double res1 = 0.00;
                Double temp = 0.00;
                for (int i = 0, rows =facturasProveedoresTable.getRowCount(); i < rows; i++)
                { 
                    iva = (String) facturasProveedoresTable.getValueAt(i, 4).toString();
                    temp =  Double.valueOf(iva);
                    res = res + temp;
                    
                    total = (String) facturasProveedoresTable.getValueAt(i, 6).toString();
                    temp =  Double.valueOf(total);
                    res1 = res1 + temp;
                }
                ivaTxt2.setText(nf.format(res1));
                ivaTxt3.setText(nf.format(res)); //compras
                
            } catch (Exception e) {
                System.out.println("Error de lectura");
                System.out.println(e);
            }
        }
    }

    private void cleanFacturaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanFacturaButton1ActionPerformed
        // TODO add your handling code here:
        buscarFacturaProveedorTxt.setText("");
    }//GEN-LAST:event_cleanFacturaButton1ActionPerformed

    private void buscarFechaTxt1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarFechaTxt1CaretUpdate
        // TODO add your handling code here:
        String buscar = buscarFacturaProveedorTxt.getText();
        String find2;
        if(fechaBuscarRadioButton1.isSelected()){
            
            find2 = buscarFechaTxt1.getText();
        }else{
            find2 = "";
        }
        
        leerFacturaProveedorBD("Proveedor", buscar, find2);
        actualizaTablaFacturasProveedoresAnchos();
    }//GEN-LAST:event_buscarFechaTxt1CaretUpdate

    private void nuevaFacturaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaFacturaButton1ActionPerformed
        // TODO add your handling code here:
        limpiaFichaFacturaProveedor();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        fechaCompraTxt.setText(hoy);
        flagSaveFacturaProveedor = 0;   //Guardar dato
    }//GEN-LAST:event_nuevaFacturaButton1ActionPerformed

    /*
     * Limpia ficha de factura
     */
    void limpiaFichaFacturaProveedor(){
        idFacturaProveedorTxt.setText("");
        serieFacturaProveedorTxt.setText("");
        proveedorFacturaTxt.setText("");
        rucFacturaProveedorTxt.setText("");
        subtotalTxt1.setText("0.00");
        ivat12Txt1.setText("");
        totalTxt1.setText("");
        telefonoProveedorTxt.setText("");
        fechaCompraTxt.setText("");
        direccionProveedorTxt.setText("");
    }
    
    
    private void borrarFacturaClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarFacturaClienteButton1ActionPerformed
        // TODO add your handling code here:
        //Necesita password de super usuario
        JPasswordField jpf = new JPasswordField();
        JOptionPane.showConfirmDialog(null, jpf, "Clave de administrador", JOptionPane.OK_CANCEL_OPTION);
        // jpf.getPassword();
        char[] input = jpf.getPassword();
        char[] correctPassword = { '1','2'};
        boolean isCorrect = true;
        isCorrect = Arrays.equals(input, correctPassword);
        if(isCorrect){
            //Password Valido procede a borrar
            ////chequea si es posible borrar
            if (!idFacturaProveedorTxt.getText().isEmpty()){
               borrarFichaFacturaProveedor();
               limpiaFichaFacturaProveedor();
               leerFacturaProveedorBD("Proveedor", "", buscarFechaTxt1.getText());
               actualizaTablaFacturasProveedoresAnchos();
            }
        }
    }//GEN-LAST:event_borrarFacturaClienteButton1ActionPerformed

    
     /*
     * Borrar articulo de la base de datos
     */
    void borrarFichaFacturaProveedor(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM FacturaProveedores WHERE id=" + (String)idFacturaProveedorTxt.getText());
                statement.close();
                connection.close();
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura borrada",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                System.out.println(e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "No se borro factura",
                        "Error - 11", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void guardarFacturaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarFacturaButton1ActionPerformed
        // TODO add your handling code here:
        //chequea si actualiza o guarda datos
        if (flagSaveFacturaProveedor == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!totalTxt1.getText().isEmpty()) && !rucFacturaProveedorTxt.getText().isEmpty()){
                guardarFacturaProveedor();
                flagSaveFacturaProveedor = 1;
            }
        }else{
            //ACTUALIZA
            //chequea si hay escrito los datos
            if((!totalTxt1.getText().isEmpty()) && !rucFacturaProveedorTxt.getText().isEmpty()){
                actualizarFacturaProveedor();
                flagSaveFacturaProveedor = 1; //actualizar
            }
        }
    }//GEN-LAST:event_guardarFacturaButton1ActionPerformed

    /*
     * Actualizar datos de facturas en la base de datos
     */
    void actualizarFacturaProveedor(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {                    
                statement = connection.createStatement();
                int actualizar = statement.executeUpdate("UPDATE FacturaProveedores SET " +
                    "Nserie='" +serieFacturaProveedorTxt.getText()+"', "+
                    "Proveedor='" + proveedorFacturaTxt.getText()+"', "+
                    "Fecha='" + fechaCompraTxt.getText()+"', "+
                    "RUC='" + rucFacturaProveedorTxt.getText()+"', "+
                    //"Pagado='" +"false"+"', "+
                    "Direccion='" + direccionProveedorTxt.getText()+"', "+
                    "Subtotal='" + subtotalTxt1.getText()+"', "+
                    "IVA='" + ivat12Txt1.getText()+"', "+
                    "Total='" + totalTxt1.getText()+"', "+
                    "Telefono='" + telefonoProveedorTxt.getText()+"', "+
                    "Articulo='" + articuloComboBox.getSelectedIndex() +"'"+
                    " WHERE id =" + (String)idFacturaProveedorTxt.getText());
                statement.close();
                connection.close();
                 //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura actualizada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFacturaProveedor = 1; //actualizar
                leerFacturaProveedorBD("Proveedor", "", buscarFechaTxt1.getText());
                actualizaTablaFacturasProveedoresAnchos();
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura no se actualizo",
                        "Error - 11", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * Guarda en base de datos nueva factura
     */
    void guardarFacturaProveedor(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO FacturaProveedores " +
                        "(Nserie, Proveedor, Fecha, RUC, Pagado, Direccion, Subtotal, IVA, Total, Telefono, Articulo) " +
                    "VALUES(" +
                    "'"+serieFacturaProveedorTxt.getText()+"' ,"+
                    "'"+proveedorFacturaTxt.getText()+"' ,"+
                    "'"+fechaCompraTxt.getText()+"' ,"+
                    "'"+rucFacturaProveedorTxt.getText()+"' ,"+
                    "'"+"false"+"' ,"+
                    "'"+direccionProveedorTxt.getText()+"' ,"+
                    "'"+subtotalTxt1.getText()+"' ,"+
                    "'"+ivat12Txt1.getText()+"' ,"+
                    "'"+totalTxt1.getText()+"' ,"+
                    "'"+telefonoProveedorTxt.getText()+"' ,"+
                    "'"+articuloComboBox.getSelectedIndex()+"'"+
                ")");
                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Factura guardada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                leerFacturaProveedorBD("Proveedor", "", buscarFechaTxt1.getText());
                actualizaTablaFacturasProveedoresAnchos();
            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura no se guardo",
                        "Error - 10", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    private void calucularFacturaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_calucularFacturaButton1ActionPerformed
        // TODO add your handling code here:
        //Obtengo datos de la tabla para computar datos
        String cant;
        Integer cantidad;
        String pUnit;
        Double pUnitario;
        Double iva=0.00;
        Double res2,res3;
        //NumberFormat nf = NumberFormat.getInstance();
        DecimalFormat nf = new DecimalFormat("####.##");
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
        
        if(articuloComboBox.getSelectedIndex()==0){
            //System.out.println("12%");
            iva= 0.12;
        }else if(articuloComboBox.getSelectedIndex()==1){
            iva= 0.00;
        }else if(articuloComboBox.getSelectedIndex()==2){
            iva= 0.12;
        }else if(articuloComboBox.getSelectedIndex()==3){
            iva= 0.00;
        }
        cant = (String) subtotalTxt1.getText().toString();
        res2 = Double.valueOf(cant);
        res3 = res2*iva;
        ivat12Txt1.setText(nf.format(res3));
        iva = res2 + res3;
        totalTxt1.setText(nf.format(iva));
    }//GEN-LAST:event_calucularFacturaButton1ActionPerformed

    private void pagarFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pagarFacturaButtonActionPerformed
        // TODO add your handling code here:
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {                    
                statement = connection.createStatement();
                int actualizar = statement.executeUpdate("UPDATE FacturaProveedores SET " +
                    "Nserie='" +serieFacturaProveedorTxt.getText()+"', "+
                    "Proveedor='" + proveedorFacturaTxt.getText()+"', "+
                    "Fecha='" + fechaCompraTxt.getText()+"', "+
                    "RUC='" + rucFacturaProveedorTxt.getText()+"', "+
                    "Pagado='" +"true"+"', "+
                    "Direccion='" + direccionProveedorTxt.getText()+"', "+
                    "Subtotal='" + subtotalTxt1.getText()+"', "+
                    "IVA='" + ivat12Txt1.getText()+"', "+
                    "Total='" + totalTxt1.getText()+"', "+
                    "Telefono='" + telefonoProveedorTxt.getText()+"', "+
                    "Articulo='" + articuloComboBox.getSelectedIndex() +"'"+
                    " WHERE id =" + (String)idFacturaProveedorTxt.getText());
                statement.close();
                connection.close();
                 //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura pagada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFacturaProveedor = 1; //actualizar
                leerFacturaProveedorBD("Proveedor", "", "");
                actualizaTablaFacturasProveedoresAnchos();
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura no se pago",
                        "Error - 12", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_pagarFacturaButtonActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
        //LINUX
        String filename = System.getProperty("user.home") + "/jContab/";
        //WINDOWS
        //String filename = File.separator + "D:\\HC9\\";

        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DATE);
        int month = c.get(Calendar.MONTH) + 1;
        int year = c.get(Calendar.YEAR);
        String fecha = String.valueOf(year)+"_"+String.valueOf(month)+"_"+String.valueOf(day);

        JFileChooser chooser = new JFileChooser();
        File f = new File( filename + fecha + "_jContab_empresa_bckup.sql");
        FileFilter jpegFilter = new FileNameExtensionFilter(null, new String[] { "sql"});

        chooser.addChoosableFileFilter(jpegFilter);
        chooser.setSelectedFile(f);
        chooser.showSaveDialog(null);
        File curFile = chooser.getSelectedFile();

        //LINUX
        //String command= "mysqldump --opt --user=root --password=treky5 --host=localhost historias_clinicas -r copia-seguridad.sql";
        String command= "mysqldump --opt --user=root --password=treky5 --host=localhost empresa -r "+ String.valueOf(curFile);

        //WINDOWS
        //String command= "D:/Archivos de programa/MySQL/MySQL Server 5.1/bin/mysqldump --opt --user=root --password=treky5 --host=localhost historias_clinicas -r C:/copia-seguridad.sql";
        //String command= "C:/Program Files/MySQL/MySQL Server 5.1/bin/mysqldump --opt --user=root --password=treky5 --host=localhost historias_clinicas -r "+ String.valueOf(curFile);

        String line;
        try{
            java.lang.Process proc = Runtime.getRuntime().exec(command);
            BufferedReader input = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
            int retVal = proc.waitFor();
            System.out.println(retVal);
            proc.destroy(); //beta
            if (retVal == 0){
                JOptionPane.showMessageDialog(null, "Copia de seguridad realizada ...");
            }

        } catch(Exception e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al guardar base de datos! ...");
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        // TODO add your handling code here:
        //Necesita password de super usuario
        JPasswordField jpf = new JPasswordField();
        JOptionPane.showConfirmDialog(null, jpf, "Código Super Usuario", JOptionPane.OK_CANCEL_OPTION);
        // jpf.getPassword();
        char[] input = jpf.getPassword();
        char[] correctPassword = { '1','2'};
        boolean isCorrect = true;
        isCorrect = Arrays.equals(input, correctPassword);
        if(isCorrect){

            //LINUX
            String filename = System.getProperty("user.home") + "/jContab/";

            //WINDOWS
            //String filename = File.separator + "D:\\HC9\\";

            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DATE);
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);
            String fecha = String.valueOf(year)+"_"+String.valueOf(month)+"_"+String.valueOf(day);

            JFileChooser chooser = new JFileChooser();
            File f = new File( filename + fecha + "_jContab_empresa_bckup.sql");
            FileFilter jpegFilter = new FileNameExtensionFilter(null, new String[] { "sql"});
            chooser.addChoosableFileFilter(jpegFilter);
            chooser.setSelectedFile(f);
            chooser.showOpenDialog(null);
            File curFile = chooser.getSelectedFile();

            int processComplete;
            //String command= "mysqldump --opt --user=root --password=treky5 --host=localhost historias_clinicas -r copia-seguridad.sql";
            try {
                //LINUX
                //String[] executeCmd = new String[]{"mysql", "historias_clinicas", "--user=" + "root", "--password=" + "treky5", "-e", " source /home/jacg/NetBeansProjects/ListaBaseDatos/copia-seguridad.sql" };
                //String[] executeCmd = new String[]{"mysql", "ClinicManagerPediatria", "--user=" + "root", "--password=" + "treky5", "-e", " source " + curFile };
                String[] executeCmd = new String[]{"mysql", "empresa", "--user=" + "root", "--password=" + "treky5", "-e", " source " + curFile };

                //WINDOWS
                //String[] executeCmd = new String[]{"C:/Program Files/MySQL/MySQL Server 5.1/bin/mysql", "historias_clinicas", "--user=" + "root", "--password=" + "treky5", "-e", " source " + curFile };


                java.lang.Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
                processComplete = runtimeProcess.waitFor();
                int pc = processComplete;
                System.out.print(processComplete);
                runtimeProcess.destroy();
                
                leerClientes("Cliente","");
                actualizaListaClientesTablaAnchos();
                leerArticulos("");
                actualizaListaArticulosTablaAnchos();
                leerClientesFactura("Cliente","","");
                actualizaListaFacturasClientesTablaAnchos();

                JOptionPane.showMessageDialog(null, "Restauracion exitosa ...");
   

            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Error no se actualizo la DB: " + e.getMessage(), "Verificar",JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Base de datos no restaurada",
                        "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void reporteFacturasCobradasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_reporteFacturasCobradasMenuItemActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/FacturasVenta.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();
            parameters.put("cobrado", "true");
            parameters.put("indica", " cobradas");
            
            String input = JOptionPane.showInputDialog(new JFrame(),"Generar reporte:\n% - todo\n%2011 - periodo\nabr/2011 - mes",
                        "Ingrese fecha", 
                        JOptionPane.WARNING_MESSAGE);
    
            parameters.put("fecha", input);
            JasperReport reporte = (JasperReport) JRLoader.loadObject(filename);
            JasperPrint jasperPrint = JasperFillManager.fillReport(filename, parameters, con);
            JasperViewer jv = new JasperViewer(jasperPrint, false);
            jv.setVisible(true);
        } catch (Exception ex) {
            System.out.println("CAUSE: " + ex.getCause());
            System.out.println("MESSAGE" + ex.getMessage());
            System.out.println("LOCAL MESSAGE" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
        
    }//GEN-LAST:event_reporteFacturasCobradasMenuItemActionPerformed

    private void facturasNoCobradasMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_facturasNoCobradasMenuItemActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/FacturasVenta.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();
            parameters.put("cobrado", "false");
            parameters.put("indica", " por cobrar");
            
            String input = JOptionPane.showInputDialog(new JFrame(),"Generar reporte:\n% - todo\n%2011 - periodo\nabr/2011 - mes",
                        "Ingrese fecha", 
                        JOptionPane.WARNING_MESSAGE);
    
            parameters.put("fecha", input);
            JasperReport reporte = (JasperReport) JRLoader.loadObject(filename);
            JasperPrint jasperPrint = JasperFillManager.fillReport(filename, parameters, con);
            JasperViewer jv = new JasperViewer(jasperPrint, false);
            jv.setVisible(true);
        } catch (Exception ex) {
            System.out.println("CAUSE: " + ex.getCause());
            System.out.println("MESSAGE" + ex.getMessage());
            System.out.println("LOCAL MESSAGE" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_facturasNoCobradasMenuItemActionPerformed

    private void listaArticulosMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_listaArticulosMenuItemActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/ListaArticulos.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            JasperReport reporte = (JasperReport) JRLoader.loadObject(filename);
            JasperPrint jasperPrint = JasperFillManager.fillReport(filename, new HashMap(), con);
            JasperViewer jv = new JasperViewer(jasperPrint, false);
            jv.setVisible(true);
        } catch (Exception ex) {
            System.out.println("CAUSE: " + ex.getCause());
            System.out.println("MESSAGE" + ex.getMessage());
            System.out.println("LOCAL MESSAGE" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_listaArticulosMenuItemActionPerformed

    private void buscarClienteTxt1CaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarClienteTxt1CaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarClienteTxt1CaretUpdate

    private void clearBusquedaTxt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxt2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearBusquedaTxt2ActionPerformed

    private void nuevoClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nuevoClienteButton1ActionPerformed

    private void borrarFichaClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarFichaClienteButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_borrarFichaClienteButton1ActionPerformed

    private void guardarClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_guardarClienteButton1ActionPerformed
  
    
    /*
     * Actualizar datos de articulos en la base de datos
     */
    void actualizarFichaArticulo(){
        //custom title, warning icon
        JOptionPane.showMessageDialog(new JFrame(),
                  "Debe cargar nueva foto, o buscar copia.jpg",
                  "Información", JOptionPane.WARNING_MESSAGE);
        cargaImagen();
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                    pstatement = connection.prepareStatement(
                    "UPDATE Articulos SET descripcion = ?, fecha = ?,"
                    + "stock = ?, PVenta=?, proveedor = ?, pcoste = ?,"
                    + "beneficio = ?, pvp = ?, imagen = ? WHERE codarticulo = ?");

                    pstatement.setString(1, detalleArticuloTxt.getText());
                    pstatement.setString(2, FechaIngresoArticuloTxt.getText());
                    pstatement.setString(3, stockTxt.getText());
                    pstatement.setString(4, precioVentaTxt.getText());
                    pstatement.setString(5, proveedorTxt.getText());
                    pstatement.setString(6, precioCosteTxt.getText());
                    pstatement.setString(7, beneficioTxt.getText());
                    pstatement.setString(8, precioFinalTxt.getText());

                    File imagen = new File(fila);
                    FileInputStream fis = new FileInputStream(imagen);

                
                    pstatement.setBinaryStream(9, fis, (int)imagen.length());
                    pstatement.setInt(10, Integer.parseInt(idArticuloTxt.getText()));
                    int actualizar = pstatement.executeUpdate();
                    
                    System.out.println("Resultado: " +actualizar);
                    fis.close();
                    pstatement.close();
                    connection.close();

                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha artículo actualizado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaArticulo = 1; //actualizar

            } catch (Exception e) {
                System.out.println("ERROR: "+e);
            }
        }
    }

    
    /*
     * GUARDAR FICHA CLIENTES NUEVA
     */
    void guardarFichaArticuloBD(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                pstatement = connection.prepareStatement(
                        "INSERT INTO Articulos VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

                pstatement.setString(1, null);
                pstatement.setString(2, detalleArticuloTxt.getText());
                pstatement.setString(3, stockTxt.getText());
                pstatement.setString(4, precioVentaTxt.getText());
                pstatement.setString(5, ivat12Txt.getText());
                pstatement.setString(6, precioFinalTxt.getText());
                pstatement.setString(7, "");
                pstatement.setString(8, proveedorTxt.getText());
                pstatement.setString(9, precioCosteTxt.getText());
                pstatement.setString(10, beneficioTxt.getText());

                File imagen = new File(fila);
                FileInputStream fis = new FileInputStream(imagen);
                pstatement.setBinaryStream(11, fis, (int)imagen.length());
                pstatement.setString(12, FechaIngresoArticuloTxt.getText());
                
                pstatement.execute();
                pstatement.close();
                connection.close();
                fis.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Artículo guardado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaArticulo = 1; //guardado y listo para actualizar
                int temp2 = temp + 1; //actualiza id

                //ESCRIVE ARCHIVO CONFIGURACION
                /*
                try {
                    FileWriter fw = new FileWriter("codart.config");
                    BufferedWriter bw = new BufferedWriter(fw);
                    PrintWriter salida = new PrintWriter(bw);
                    salida.print(temp2);
                    salida.close();
                } catch (Exception e) {
                    System.out.println("Error escritura codart.config: "+e);
                    //custom title, warning icon
                        JOptionPane.showMessageDialog(new JFrame(),
                                "Error escritura codart.config, pongase en contacto con arcusmedical.soporte@gmail.com",
                                "Error", JOptionPane.ERROR_MESSAGE);
                }
                 *
                 */
            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Articulo no se guardo",
                        "Error - 9", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    
    /*
     * BORRAR FICHA ARTICULO
     */
    public void borrarFichaArticulo(){
        if(flagDriver == 1){
            //PROCEDE A BORRAR FICHA DEL ARTICULO
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM Articulos WHERE codarticulo=" + Integer.parseInt(idArticuloTxt.getText()));
                statement.close();
                connection.close();

                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Articulo borrado",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "No se borro articulo",
                        "Error - 8", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    /*
     * Borrar factura de la base de datos
     */
    void borrarFichaFactura(){
        if(flagDriver == 1){
            //PROCEDE A BORRAR FICHA DEL ARTICULO
            conectarBD(baseDatos);
            try {

                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM FacturasClientes WHERE id=" + (String)idFacturaTxt.getText());
                statement.close();
                connection.close();
                
                
                statement = connection.createStatement();
                int borrar2 = statement.executeUpdate("DELETE FROM Ventas WHERE nserie=" + (String)numeroSerieTxt.getText());
                statement.close();
                connection.close();
                
                

                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura borrada",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "No se borro factura",
                        "Error - 13", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }

    /*
     * Actualizar datos de articulos en la base de datos
     */
    void actualizarFichaFacturaCliente(){
        //custom title, warning icon
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                    statement = connection.createStatement();
                    int actualizar = statement.executeUpdate("UPDATE FacturasClientes SET " +
                    "Cliente='"+clienteFacturaTxt.getText()+"' ,"+
                    "RUC='"+rucTxt.getText()+"' ,"+
                //    "Cobrado='"+"false"+"' ,"+
                    "Subtotal='"+subtotalTxt.getText()+"' ,"+
                    "Iva='"+ivat12Txt.getText()+"' ,"+
                    "Total='"+totalTxt.getText()+"' ,"+
                    "Fuente='"+retencionTxt.getText()+"' ,"+
                    "Recivido='"+recividoTxt1.getText()+"' ,"+
                    "Fecha='"+fechaTxt.getText()+"' ,"+
                    "Nserie='"+numeroSerieTxt.getText()+"' ,"+
                    "Direccion='"+direccionTxt.getText()+"' ,"+
                    "Ciudad='"+ciudadTxt.getText()+"' ,"+
                    "Telefono='"+telefonoTxt.getText()+"'"+
                    " WHERE id =" + (String)idFacturaTxt.getText());

                    //GUARDA TABLA DE ARTICULOS UNO POR UNO NUMERO SERIE CLAVE EN BUSQUEDA
                    for (int i = 0, rows = facturaArticulosClienteTable.getRowCount()-1; i < rows; i++){
                        statement = connection.createStatement();
                        int escribe2 = statement.executeUpdate("UPDATE Ventas SET " +

                        "fecha='"+fechaTxt.getText()+"' ,"+
                        "nserie='"+numeroSerieTxt.getText()+"' ,"+
                        "cantidad='"+facturaArticulosClienteTable.getValueAt(i, 0).toString()+"' ,"+
                        "articulo='"+facturaArticulosClienteTable.getValueAt(i, 1).toString()+"' ,"+
                        "punitario='"+facturaArticulosClienteTable.getValueAt(i, 2).toString()+"' ,"+
                        "ptotal='"+facturaArticulosClienteTable.getValueAt(i, 3).toString()+"'"+
                        " WHERE id =" + (String)facturaArticulosClienteTable.getValueAt(i, 4).toString());
                    }

                    statement.close();
                    connection.close();

                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha factura actualizada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaCliente = 1; //actualizar

            } catch (Exception e) {
                System.out.println("ERROR: "+e);
            }
        }
    }

    
    /*
     * GUARDAR FICHA CLIENTES NUEVA
     */
    void guardarFichaFacturaClienteBD(){
        int rows = 0;
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO FacturasClientes " +
                        "(Cliente, RUC, Cobrado, Subtotal, Iva, Total, Fuente, Recivido, Fecha, Nserie, Direccion, Telefono, Ciudad) " +
                    "VALUES(" +
                    "'"+clienteFacturaTxt.getText()+"' ,"+
                    "'"+rucTxt.getText()+"' ,"+
                    "'"+"false"+"' ,"+  
                    "'"+subtotalTxt.getText()+"' ,"+
                    "'"+ivat12Txt.getText()+"' ,"+
                    "'"+totalTxt.getText()+"' ,"+
                    "'"+fuenteTxt.getText()+"' ,"+
                    "'"+recividoTxt1.getText()+"' ,"+
                    "'"+fechaTxt.getText()+"' ,"+
                    "'"+numeroSerieTxt.getText()+"' ,"+
                    "'"+direccionTxt.getText()+"' ,"+
                    "'"+telefonoTxt.getText()+"' ,"+
                    "'"+ciudadTxt.getText()+"'"+
                ")");
                statement.close();
                
                //GUARDA TABLA DE ARTICULOS UNO POR UNO NUMERO SERIE CLAVE EN BUSQUEDA
                rows = facturaArticulosClienteTable.getRowCount();
                for (int i = 0; i < rows; i++){
                    statement = connection.createStatement();
                    int escribe2 = statement.executeUpdate("INSERT INTO Ventas " +
                        "(RUC, fecha, nserie, cantidad, articulo, punitario, ptotal) " +
                    "VALUES(" +
                    "'"+rucTxt.getText()+"' ,"+
                    "'"+fechaTxt.getText()+"' ,"+
                    "'"+numeroSerieTxt.getText()+"' ,"+
                    "'"+facturaArticulosClienteTable.getValueAt(i, 0).toString()+"' ,"+
                    "'"+facturaArticulosClienteTable.getValueAt(i, 1).toString()+"' ,"+
                    "'"+facturaArticulosClienteTable.getValueAt(i, 2).toString()+"' ,"+
                    "'"+facturaArticulosClienteTable.getValueAt(i, 3).toString()+"'"+
                    ")");
                }

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Factura guardada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveFichaCliente = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Factura no se guardo",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    
    /*
     * HABILITA ESCRITURA O ACTUALIZACION de FICHA
     */
    public void escrituraFichaFactura(){
        idFacturaTxt.setEditable(false);
        clienteFacturaTxt.setEditable(true);
        rucTxt.setEditable(true);
        direccionTxt.setEditable(true);
        telefonoTxt.setEditable(true);
        ciudadTxt.setEditable(true);
        fechaTxt.setEditable(true);
        numeroSerieTxt.setEditable(true);
   
    }
    
    /*
     * LIMPIA FICHA ARTICULO
     */
    public void limpiaFichaFactura(){
        idFacturaTxt.setText("");
        clienteFacturaTxt.setText("");
        rucTxt.setText("");
        direccionTxt.setText("");
        telefonoTxt.setText("");
        ciudadTxt.setText("");
        fechaTxt.setText("");
        numeroSerieTxt.setText("");
        cobradoTxt2.setText("");
        cobradoTxt4.setText("");
        ivaPorTxt.setText("30");
        ivabaseTxt.setText("");
        fuenteBaseTxt.setText("");
        fuentePorTxt.setText("1");
        subtotalTxt.setText("");
        ivat12Txt.setText("");
        totalTxt.setText("");
        retencionTxt.setText("");
        recividoTxt1.setText("");
    }

    
    /*
     * LIMPIA FICHA ARTICULO
     */
    public void limpiaFichaArticulos(){
        flagSaveFichaArticulo = 0; //guardar ficha articulo
        idArticuloTxt.setText("");
        proveedorTxt.setText("");
        stockTxt.setText("");
        precioCosteTxt.setText("");
        beneficioTxt.setText("0.20");
        precioVentaTxt.setText("");
        precioFinalTxt.setText("");
        detalleArticuloTxt.setText("");
        FechaIngresoArticuloTxt.setText("");
        image = null;
        try {
            image = ImageIO.read(new File("fondo.png"));
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        ImagenLabel.setIcon(new ImageIcon(image));
        //System.out.println("Limpiando ficha de articulo.");
    }

    
    /*
     * LEE ARTICULO EN BASE DE DATOS, con criterio de busqueda
     */
    void leerArticulos(String find){
        listaArticulosTabla.removeAll();
        listaArticulosTabla.updateUI();
        String[] columnNames = {"Código",
                                "Articulo",
                                "Stock",
                                "P.V.P",
                                "Imagen"};
        ArrayList data1 = new ArrayList();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Articulos WHERE " + "descripcion" + " LIKE '%" + (String)find+"%'");
                while(rs.next()){
                    Object[] row = new Object[5];
                    row[0] = rs.getObject("codarticulo");
                    row[1] = rs.getObject("descripcion");
                    row[2] = rs.getObject("stock");
                    row[3] = rs.getObject("pvp");    
                    
                    Blob bytesImagen = rs.getBlob("imagen");
                    byte[] img = bytesImagen.getBytes(1, (int)bytesImagen.length());
             
                    row[4] = img;
                    model.addRow(row);
                }
                listaArticulosTabla.setModel(model);
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "No se puede leer articulos",
                        "Error - 5", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /*
     * BORRAR FICHA ARTICULO
     */
    public void borrarFichaClienteBD(){
        if(flagDriver == 1){
            //PROCEDE A BORRAR FICHA DEL ARTICULO
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM Clientes WHERE id=" + Integer.parseInt(idClienteTxt.getText()));
                statement.close();
                connection.close();

                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha de cliente borrada",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Ficha de cliente no pudo ser borrada",
                        "Error - 4", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    
    /*
     * LIMPIA FICHA
     */
    public void limpiaFichaCliente(){
        flagSaveFichaCliente = 0; //guardar ficha articulo
        idClienteTxt.setText("");
        clienteTxt.setText("");
        RUCTxt.setText("");
        nombreComercialTxt.setText("");
        fechaClienteTxt.setText("");
        ciudadClienteTxt.setText("");
        direccionClienteTxt.setText("");
        telefonoClienteTxt.setText("");
        emailClienteTxt.setText("");
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /*
        try {
              
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
             
            /*
            String filename = System.getProperty("user.home") + "/jContab/Snow.theme";
            NimRODTheme nt = new NimRODTheme(filename);
            NimRODLookAndFeel nf = new NimRODLookAndFeel();
            nf.setCurrentTheme(nt);
            UIManager.setLookAndFeel(nf);      

        } catch (Exception e) {
            System.out.println("Theme no encontrado: "+e);
        }
        */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Contab().setVisible(true);
            }
        });
    }
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField FechaIngresoArticuloTxt;
    public javax.swing.JLabel ImagenLabel;
    private javax.swing.JTextField RUCTxt;
    private javax.swing.JButton agregarArticuloAFacturaButton;
    private javax.swing.JButton agregarArticuloButton;
    private javax.swing.JComboBox articuloComboBox;
    public javax.swing.JTextField beneficioTxt;
    private javax.swing.JButton borrarFacturaClienteButton;
    private javax.swing.JButton borrarFacturaClienteButton1;
    private javax.swing.JButton borrarFichaArticuloButton;
    private javax.swing.JButton borrarFichaClienteButton;
    private javax.swing.JButton borrarFichaClienteButton1;
    private javax.swing.JButton borrarProveedorButton;
    private javax.swing.JTextField buscarArticuloTxt;
    private javax.swing.JComboBox buscarClienteComboBox;
    private javax.swing.JTextField buscarClienteTxt;
    private javax.swing.JTextField buscarClienteTxt1;
    private javax.swing.JTextField buscarFacturaClienteTxt;
    private javax.swing.JTextField buscarFacturaProveedorTxt;
    private javax.swing.JTextField buscarFechaTxt;
    private javax.swing.JTextField buscarFechaTxt1;
    private javax.swing.JTextField buscarProveedorTxt;
    private javax.swing.JButton calcularArticuloButton;
    private javax.swing.JButton calucularFacturaButton;
    private javax.swing.JButton calucularFacturaButton1;
    private javax.swing.JTextField ciudadClienteTxt;
    private javax.swing.JTextField ciudadProveedorTxt;
    private javax.swing.JTextField ciudadTxt;
    private javax.swing.JButton cleanFacturaButton;
    private javax.swing.JButton cleanFacturaButton1;
    private javax.swing.JButton clearBusquedaTxt;
    private javax.swing.JButton clearBusquedaTxt1;
    private javax.swing.JButton clearBusquedaTxt2;
    private javax.swing.JTextField clienteFacturaTxt;
    private javax.swing.JTextField clienteTxt;
    private javax.swing.JTable clientesTable;
    private javax.swing.JTextField cobradoTxt2;
    private javax.swing.JTextField cobradoTxt4;
    private javax.swing.JButton cobrarFacturaButton1;
    private javax.swing.JTextArea contactoTextArea;
    private javax.swing.JButton crearFacturaClienteButton;
    private javax.swing.JButton crearFacturaClienteButton1;
    public javax.swing.JTextArea detalleArticuloTxt;
    private javax.swing.JTextField direccionClienteTxt;
    private javax.swing.JTextField direccionProveedorTxt;
    private javax.swing.JTextField direccionTxt;
    private javax.swing.JButton eliminarArticuloButton;
    private javax.swing.JTextField emailClienteTxt;
    private javax.swing.JTextField emailProveedorTxt;
    private javax.swing.JTextField empresaTxt;
    private javax.swing.JTable facturaArticulosClienteTable;
    private javax.swing.JMenuItem facturasNoCobradasMenuItem;
    private javax.swing.JTable facturasProveedoresTable;
    private javax.swing.JRadioButton fechaBuscarRadioButton;
    private javax.swing.JRadioButton fechaBuscarRadioButton1;
    private javax.swing.JTextField fechaClienteTxt;
    private javax.swing.JTextField fechaCompraTxt;
    private javax.swing.JTextField fechaTxt;
    private javax.swing.JRadioButton filtrarCobradoRadioButton;
    private javax.swing.JTextField fuenteBaseTxt;
    private javax.swing.JTextField fuentePorTxt;
    private javax.swing.JTextField fuenteTxt;
    private javax.swing.JButton guardarArticuloButton;
    private javax.swing.JButton guardarClienteButton;
    private javax.swing.JButton guardarClienteButton1;
    private javax.swing.JButton guardarFacturaButton;
    private javax.swing.JButton guardarFacturaButton1;
    private javax.swing.JButton guardarProveedorButton;
    public javax.swing.JTextField idArticuloTxt;
    private javax.swing.JTextField idClienteTxt;
    private javax.swing.JTextField idFacturaProveedorTxt;
    private javax.swing.JTextField idFacturaTxt;
    private javax.swing.JTextField idProveedorTxt;
    private javax.swing.JButton imprimirFacturaButton;
    private javax.swing.JTextField ivaPorTxt;
    private javax.swing.JTextField ivaTxt;
    private javax.swing.JTextField ivaTxt1;
    private javax.swing.JTextField ivaTxt2;
    private javax.swing.JTextField ivaTxt3;
    private javax.swing.JTextField ivabaseTxt;
    private javax.swing.JTextField ivat12Txt;
    private javax.swing.JTextField ivat12Txt1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel60;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel64;
    private javax.swing.JLabel jLabel65;
    private javax.swing.JLabel jLabel66;
    private javax.swing.JLabel jLabel67;
    private javax.swing.JLabel jLabel68;
    private javax.swing.JLabel jLabel69;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel70;
    private javax.swing.JLabel jLabel71;
    private javax.swing.JLabel jLabel72;
    private javax.swing.JLabel jLabel73;
    private javax.swing.JLabel jLabel74;
    private javax.swing.JLabel jLabel75;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenu jMenu4;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel25;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JToolBar.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator16;
    private javax.swing.JToolBar.Separator jSeparator17;
    private javax.swing.JToolBar.Separator jSeparator18;
    private javax.swing.JToolBar.Separator jSeparator19;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator20;
    private javax.swing.JToolBar.Separator jSeparator21;
    private javax.swing.JToolBar.Separator jSeparator22;
    private javax.swing.JToolBar.Separator jSeparator23;
    private javax.swing.JToolBar.Separator jSeparator24;
    private javax.swing.JToolBar.Separator jSeparator25;
    private javax.swing.JToolBar.Separator jSeparator26;
    private javax.swing.JToolBar.Separator jSeparator27;
    private javax.swing.JToolBar.Separator jSeparator28;
    private javax.swing.JToolBar.Separator jSeparator29;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator30;
    private javax.swing.JToolBar.Separator jSeparator31;
    private javax.swing.JToolBar.Separator jSeparator32;
    private javax.swing.JPopupMenu.Separator jSeparator33;
    private javax.swing.JToolBar.Separator jSeparator34;
    private javax.swing.JToolBar.Separator jSeparator35;
    private javax.swing.JToolBar.Separator jSeparator36;
    private javax.swing.JToolBar.Separator jSeparator37;
    private javax.swing.JToolBar.Separator jSeparator38;
    private javax.swing.JToolBar.Separator jSeparator39;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator40;
    private javax.swing.JToolBar.Separator jSeparator41;
    private javax.swing.JToolBar.Separator jSeparator42;
    private javax.swing.JToolBar.Separator jSeparator43;
    private javax.swing.JToolBar.Separator jSeparator44;
    private javax.swing.JToolBar.Separator jSeparator45;
    private javax.swing.JToolBar.Separator jSeparator46;
    private javax.swing.JToolBar.Separator jSeparator47;
    private javax.swing.JToolBar.Separator jSeparator48;
    private javax.swing.JToolBar.Separator jSeparator49;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator50;
    private javax.swing.JToolBar.Separator jSeparator51;
    private javax.swing.JToolBar.Separator jSeparator52;
    private javax.swing.JToolBar.Separator jSeparator53;
    private javax.swing.JToolBar.Separator jSeparator54;
    private javax.swing.JToolBar.Separator jSeparator55;
    private javax.swing.JToolBar.Separator jSeparator56;
    private javax.swing.JToolBar.Separator jSeparator57;
    private javax.swing.JToolBar.Separator jSeparator58;
    private javax.swing.JToolBar.Separator jSeparator59;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator60;
    private javax.swing.JToolBar.Separator jSeparator61;
    private javax.swing.JToolBar.Separator jSeparator62;
    private javax.swing.JToolBar.Separator jSeparator63;
    private javax.swing.JToolBar.Separator jSeparator64;
    private javax.swing.JToolBar.Separator jSeparator65;
    private javax.swing.JToolBar.Separator jSeparator66;
    private javax.swing.JToolBar.Separator jSeparator67;
    private javax.swing.JToolBar.Separator jSeparator68;
    private javax.swing.JToolBar.Separator jSeparator69;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator70;
    private javax.swing.JToolBar.Separator jSeparator71;
    private javax.swing.JToolBar.Separator jSeparator72;
    private javax.swing.JToolBar.Separator jSeparator73;
    private javax.swing.JToolBar.Separator jSeparator74;
    private javax.swing.JToolBar.Separator jSeparator75;
    private javax.swing.JToolBar.Separator jSeparator77;
    private javax.swing.JToolBar.Separator jSeparator78;
    private javax.swing.JToolBar.Separator jSeparator79;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator82;
    private javax.swing.JToolBar.Separator jSeparator83;
    private javax.swing.JToolBar.Separator jSeparator84;
    private javax.swing.JToolBar.Separator jSeparator85;
    private javax.swing.JToolBar.Separator jSeparator86;
    private javax.swing.JToolBar.Separator jSeparator87;
    private javax.swing.JToolBar.Separator jSeparator88;
    private javax.swing.JToolBar.Separator jSeparator89;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JToolBar.Separator jSeparator90;
    private javax.swing.JToolBar.Separator jSeparator91;
    private javax.swing.JToolBar.Separator jSeparator92;
    private javax.swing.JToolBar.Separator jSeparator93;
    private javax.swing.JToolBar.Separator jSeparator94;
    private javax.swing.JToolBar.Separator jSeparator95;
    private javax.swing.JToolBar.Separator jSeparator96;
    private javax.swing.JToolBar.Separator jSeparator97;
    private javax.swing.JToolBar.Separator jSeparator98;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField10;
    private javax.swing.JTextField jTextField11;
    private javax.swing.JTextField jTextField12;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    private javax.swing.JTextField jTextField9;
    private javax.swing.JToolBar jToolBar10;
    private javax.swing.JToolBar jToolBar11;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JButton limpiarArticuloTxt;
    private javax.swing.JMenuItem listaArticulosMenuItem;
    private javax.swing.JTable listaArticulosTabla;
    private javax.swing.JTable listaContratosTabla;
    private javax.swing.JTable listaFacturaClientesTabla;
    private javax.swing.JTextField nombreComercialTxt;
    private javax.swing.JButton nuevaFacturaButton;
    private javax.swing.JButton nuevaFacturaButton1;
    private javax.swing.JButton nuevoArticuloButton;
    private javax.swing.JButton nuevoClienteButton;
    private javax.swing.JButton nuevoClienteButton1;
    private javax.swing.JButton nuevoProveedorButton;
    private javax.swing.JTextField numeroSerieTxt;
    private javax.swing.JButton pagarFacturaButton;
    public javax.swing.JTextField precioCosteTxt;
    public javax.swing.JTextField precioFinalTxt;
    public javax.swing.JTextField precioVentaTxt;
    private javax.swing.JTextField proveedorFacturaTxt;
    public javax.swing.JTextField proveedorTxt;
    private javax.swing.JTable proveedoresTable;
    private javax.swing.JTextField recividoTxt;
    private javax.swing.JTextField recividoTxt1;
    private javax.swing.JMenuItem reporteFacturasCobradasMenuItem;
    private javax.swing.JTextField retencionTxt;
    private javax.swing.JTextField rucFacturaProveedorTxt;
    private javax.swing.JTextField rucProveedorTxt;
    private javax.swing.JTextField rucTxt;
    private javax.swing.JTextField serieFacturaProveedorTxt;
    public javax.swing.JTextField stockTxt;
    private javax.swing.JTextField subtotalTxt;
    private javax.swing.JTextField subtotalTxt1;
    private javax.swing.JTextField telefonoClienteTxt;
    private javax.swing.JTextField telefonoProveedorTxt;
    private javax.swing.JTextArea telefonoTextArea;
    private javax.swing.JTextField telefonoTxt;
    private javax.swing.JTextField totalTxt;
    private javax.swing.JTextField totalTxt1;
    private javax.swing.JTextField wwwTxt;
    // End of variables declaration//GEN-END:variables
}
