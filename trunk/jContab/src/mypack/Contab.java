/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Contab.java
 *
 * Created on May 1, 2011, 10:01:05 PM
 * RELEASE 2011 4 Noviembre
 * 
 * Correccion de vector to arraylist100
 * Mejoras de interfazContab
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
import javax.swing.JDialog;
import javax.swing.ListSelectionModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.jvnet.substance.SubstanceLookAndFeel;
import org.jvnet.substance.watermark.SubstanceImageWatermark;

/**
 *
 * @author jacg
 */
public final class Contab extends javax.swing.JFrame {
    //Variables
    BaseDatosClientes bdc = new BaseDatosClientes();
    BaseDatosVentas bdv = new BaseDatosVentas();
    BaseDatosDetallesVenta bddv = new BaseDatosDetallesVenta();
    BaseDatosArticulos bda = new BaseDatosArticulos();
   
    String filename = System.getProperty("user.home") + "/jContab/logo.jpg";
    int temp  = 0;
    private Connection connection = null;
    private Statement statement = null;
    private PreparedStatement pstatement = null;
    private ResultSet rs = null;
    char flagDriver = 0; //0 no driver 1 founded
    String fila;
    int flagSaveFichaArticulo = 0; // 0 no guardado 1 actualizar
    int flagSaveFichaCliente = 0; // 0 no guardado 1 actualizar
    int flagSaveFichaFacturaCliente = 0;
    int flagSaveFichaProveedor = 0;   //actualiza datos
    int flagSaveFacturaProveedor = 0;
    int flagSaveContrato = 0;
    int flagSaveEquipo = 0;
    int flagSaveContratoEquipo = 0;
    int flagSaveFallo = 0;
    int flagSaveMantenimiento = 0;
    int flagSaveTesoreria = 0;
    BufferedImage image = null;
    boolean is_Driver = false;
    private String baseDatos = "jdbc:mysql://localhost:3306/empresa?"+"user=root&password=treky5";
    String[] columnNamesX = {
                                "Cant.",
                                "Artículo",
                                "P. Unit.",
                                "P. Total",
                                "id"};
    DefaultTableModel model1 = new DefaultTableModel(columnNamesX, 0);
    Object[] row1 = new Object[5];
    
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


        //************************
        //VENTANA CLIENTES
        clientesInternalFrame.dispose();
        String buscar = buscarClienteTxt.getText();
        String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
        DefaultTableModel kde = bdc.leer("Cliente", "","LIKE");
        leerClientes2Tabla(kde);
        actualizaClientesTabla();
        seleccionClientesTabla();
        
        //************************
        //VENTANA FACTURAs CLIENTES
        String find1;
        if(fechaBuscarRadioButton.isSelected()){
            
            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
        facturaVentaInternalFrame.dispose();
        buscar = buscarFacturaClienteTxt.getText();
        kde = bdv.leer("Cliente", buscar, find1, "LIKE");
        leerVentas2Tabla(kde);
        actualizaVentasTabla();
        seleccionVentasTabla();
        
        //************************
        //ARTICULOS CLIENTES
        articuloInternalFrame.dispose();
        buscar = buscarArticuloTxt.getText();
        buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
        kde = bda.leer(buscarClave, "", "LIKE");
        leerArticulos2Tabla(kde);
        actualizaArticulosTabla();
        seleccionArticulosTabla();
        actualizaDetallesVentaTabla();
        
        
        
        //actualizaClientesTabla();
        //seleccionClientesTabla();
//        //************************
//        contratoInternalFrame.dispose();
//        leerContratos("Empresa", "");
//        actualizaTablaContratosAnchos();
//        seleccionContratosTabla();
//
//        apunteContableInternalFrame.dispose();
//        leerTesoreria("ClienteProveedor", "");
//        actualizaListaTesoreriaTablaAnchos();
//        seleccionTesoreriaTabla();
//
//        articuloInternalFrame.dispose();
//        leerArticulos("", "descripcion");
//        actualizaListaArticulosTablaAnchos();
//        seleccionArticulosTabla();
//        
//        String find1;
//        if(fechaBuscarRadioButton.isSelected()){
//            
//            find1 = buscarFechaTxt.getText();
//        }else{
//            find1 = "";
//        }
//
//        facturaVentaInternalFrame.dispose();
////        opcionesInternalFrame.dispose();
//        leerClientesFactura("Cliente", "", find1);
//        actualizaListaFacturasClientesTablaAnchos();
//        seleccionFacturasClientesTabla();
//        
//        //*********************
//        facturaArticulosClienteTable.removeAll();
//        facturaArticulosClienteTable.updateUI();
//        facturaArticulosClienteTable.setModel(model1);
//        actualizaTablaFacturaClienteAnchos();
//
//        //*********************
//        proveedorInternalFrame.dispose();
//        leerProveedorBD("Empresa", "");
//        actualizaTablaProveedoresAnchos();
//        seleccionProveedoresTabla();
//
//        //*********************
//        buscarFechaTxt1.setText(hoy);
//        String find2;
//        if(fechaBuscarRadioButton1.isSelected()){
//            
//            find2 = buscarFechaTxt1.getText();
//        }else{
//            find2 = "";
//        }
//
//        comprasInternalFrame.dispose();
//        leerFacturaProveedorBD("Proveedor", "", find2);
//        actualizaTablaFacturasProveedoresAnchos();
//        seleccionFacturasProveedoresTabla();
//
//        equipoInternalFrame.dispose();
//        leerEquipos("TipoEquipo", "");
//        actualizaTablaEquiposAnchos();
//        seleccionEquiposTabla();

    }

    /*
     * Lee articulos de la base de datos a una lista
     */
    public void leerArticulos2Tabla(DefaultTableModel articulo){
        JTable tabla = new JTable();
        articulosTabla.removeAll();
        articulosTabla.updateUI();
        String[] columnNames = {"Código",
                                "Articulo",
                                "Stock",
                                "P.V.P.",
                                "Imagen",
                                "Tipo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        tabla.setModel(articulo);

        int ncol = 0, nrow = 0;
        ncol = articulo.getColumnCount();
        nrow = articulo.getRowCount();

         for(int i=0;i<nrow;i++){
            Object[] row = new Object[6];
            row[0] = tabla.getModel().getValueAt(i,0).toString();
            row[1] = tabla.getModel().getValueAt(i,1).toString();
            row[2] = tabla.getModel().getValueAt(i,2).toString();
            row[3] = tabla.getModel().getValueAt(i,5).toString();
            row[4] = tabla.getModel().getValueAt(i,10);
            row[5] = tabla.getModel().getValueAt(i,6).toString();

            model.addRow(row);
         }
        this.articulosTabla.setModel(model);
    }

    /*
     * Lee ventas de la base de datos a un frame
     */
    public void leerArticulos2Ventana(DefaultTableModel articulo){
        JTable tabla = new JTable();
        tabla.setModel(articulo);

        filename = System.getProperty("user.home") + "/jContab/copia_temp.jpg";
        int ncol = 0, nrow = 0;
        ncol = articulo.getColumnCount();
        nrow = articulo.getRowCount();

        for(int i=0;i<nrow;i++){
            idArticuloTxt.setText(tabla.getModel().getValueAt(i,0).toString());
            detalleArticuloTxt.setText(tabla.getModel().getValueAt(i,1).toString());
            proveedorTxt.setText(tabla.getModel().getValueAt(i,7).toString());
            precioCosteTxt.setText(tabla.getModel().getValueAt(i,8).toString());
            beneficioTxt.setText(tabla.getModel().getValueAt(i,9).toString());
            precioFinalTxt.setText(tabla.getModel().getValueAt(i,5).toString());
            stockTxt.setText(tabla.getModel().getValueAt(i,2).toString());
            precioVentaTxt.setText(tabla.getModel().getValueAt(i,3).toString());
            FechaIngresoArticuloTxt.setText(tabla.getModel().getValueAt(i,11).toString());
            tipoArticuloTxt.setText(tabla.getModel().getValueAt(i,6).toString());
            
            Image image = null;
            try {
                InputStream is = new BufferedInputStream(new FileInputStream(filename));
                image = ImageIO.read(is);
            } catch (Exception e) {
            }
            ImagenLabel.setIcon(new ImageIcon(image)); 
        }
    }
    
    /*
     * Actualiza tabla
     */
    void actualizaVentasTabla(){
        ventasTabla.getColumnModel().getColumn(0).setPreferredWidth(57);
        ventasTabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        ventasTabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        ventasTabla.getColumnModel().getColumn(3).setPreferredWidth(230);
        ventasTabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(5).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(6).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(7).setPreferredWidth(80);
        ventasTabla.setRowHeight(25);
        ventasTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        ventasTabla.getColumnModel().getColumn(1).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxRenderer());
        ventasTabla.getColumnModel().getColumn(6).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(7).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(8).setCellRenderer(new ColorTableCellRenderer1());
    }

    /*
     * Lee clientes de la base de datos a una lista
     */
    public void leerVentas2Tabla(DefaultTableModel venta){
        JTable tabla = new JTable();
        ventasTabla.removeAll();
        ventasTabla.updateUI();
        String[] columnNames = {"Código",
                                "Factura",
                                "Fecha",
                                "Cliente",
                                "Total",
                                "Cobrado",
                                "IVA",
                                "Retenido",
                                "Recivido"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        tabla.setModel(venta);

        int ncol = 0, nrow = 0;
        ncol = venta.getColumnCount();
        nrow = venta.getRowCount();

         for(int i=0;i<nrow;i++){
            Object[] row = new Object[9];
            row[0] = tabla.getModel().getValueAt(i,0).toString();
            row[1] = tabla.getModel().getValueAt(i,10).toString();
            row[2] = tabla.getModel().getValueAt(i,9).toString();
            row[3] = tabla.getModel().getValueAt(i,1).toString();
            row[4] = tabla.getModel().getValueAt(i,6).toString();
            row[5] = tabla.getModel().getValueAt(i,3).toString();
            row[6] = tabla.getModel().getValueAt(i,5).toString();
            row[7] = tabla.getModel().getValueAt(i,14).toString();
            row[8] = tabla.getModel().getValueAt(i,7).toString();

            model.addRow(row);
         }
        this.ventasTabla.setModel(model);
    }

    /*
     * Lee clientes de la base de datos a una lista
     */
    public void leerDetallesVenta2Tabla(DefaultTableModel detallesVenta){
        JTable tabla = new JTable();
        detallesVentaTabla.removeAll();
        detallesVentaTabla.updateUI();
        String[] columnNames = {
                                "Cant.",
                                "Artículo",
                                "P. Unit.",
                                "P. Total",
                                "id"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        tabla.setModel(detallesVenta);

        int ncol = 0, nrow = 0;
        ncol = detallesVenta.getColumnCount();
        nrow = detallesVenta.getRowCount();

         for(int i=0;i<nrow;i++){
            Object[] row = new Object[5];
            row[0] = tabla.getModel().getValueAt(i,3).toString();
            row[1] = tabla.getModel().getValueAt(i,4).toString();
            row[2] = tabla.getModel().getValueAt(i,5).toString();
            row[3] = tabla.getModel().getValueAt(i,6).toString();
            row[4] = tabla.getModel().getValueAt(i,0).toString();
            model.addRow(row);
         }
        this.detallesVentaTabla.setModel(model);
    }

    /*
     * Lee ventas de la base de datos a un frame
     */
    public void leerVentas2Ventana(DefaultTableModel venta){
        JTable tabla = new JTable();
        tabla.setModel(venta);

        int ncol = 0, nrow = 0;
        ncol = venta.getColumnCount();
        nrow = venta.getRowCount();

        for(int i=0;i<nrow;i++){
            idFacturaTxt.setText(tabla.getModel().getValueAt(i,0).toString());
            clienteFacturaTxt.setText(tabla.getModel().getValueAt(i,1).toString());
            rucTxt.setText(tabla.getModel().getValueAt(i,2).toString());
            subtotalTxt.setText(tabla.getModel().getValueAt(i,4).toString());
            ivat12Txt.setText(tabla.getModel().getValueAt(i,5).toString());
            totalTxt.setText(tabla.getModel().getValueAt(i,6).toString());
            recividoTxt1.setText(tabla.getModel().getValueAt(i,7).toString());
            fechaTxt.setText(tabla.getModel().getValueAt(i,9).toString());
            numeroSerieTxt.setText(tabla.getModel().getValueAt(i,10).toString());
            direccionTxt.setText(tabla.getModel().getValueAt(i,11).toString());
            telefonoTxt.setText(tabla.getModel().getValueAt(i,12).toString());
            ciudadTxt.setText(tabla.getModel().getValueAt(i,13).toString());
            retencionTxt.setText(tabla.getModel().getValueAt(i,14).toString());
        }
    }
    
    /*
     * Lee clientes de la base de datos a un frame
     */
    public void leerClientes2Ventana(DefaultTableModel cliente){
        JTable tabla = new JTable();
        tabla.setModel(cliente);

        int ncol = 0, nrow = 0;
        ncol = cliente.getColumnCount();
        nrow = cliente.getRowCount();

        for(int i=0;i<nrow;i++){
            idClienteTxt.setText(tabla.getModel().getValueAt(i,0).toString());
            clienteTxt.setText(tabla.getModel().getValueAt(i,1).toString());
            rucClienteTxt.setText(tabla.getModel().getValueAt(i,2).toString());
            direccionClienteTxt.setText(tabla.getModel().getValueAt(i,3).toString());
            telefonoClienteTxt.setText(tabla.getModel().getValueAt(i,4).toString());
            fechaClienteTxt.setText(tabla.getModel().getValueAt(i,5).toString());
            ciudadClienteTxt.setText(tabla.getModel().getValueAt(i,6).toString());
            nombreComercialClienteTxt.setText(tabla.getModel().getValueAt(i,7).toString());
            emailClienteTxt.setText(tabla.getModel().getValueAt(i,8).toString());
        }
    }

    /*
     * Lee clientes de la base de datos a una lista
     */
    public void leerClientes2Tabla(DefaultTableModel cliente){
         JTable tabla = new JTable();
        clientesTabla.removeAll();
        clientesTabla.updateUI();
        String[] columnNames = {"Código",
                                "Nombre comercial",
                                "Razón social",
                                "Ciudad",
                                "R.U.C."};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        tabla.setModel(cliente);
        
        int ncol = 0, nrow = 0;
        ncol = cliente.getColumnCount();
        nrow = cliente.getRowCount();

         for(int i=0;i<nrow;i++){
            Object[] row = new Object[5];
            row[0] = tabla.getModel().getValueAt(i,0).toString();
            row[1] = tabla.getModel().getValueAt(i,7).toString();
            row[2] = tabla.getModel().getValueAt(i,1).toString();
            row[3] = tabla.getModel().getValueAt(i,6).toString();
            row[4] = tabla.getModel().getValueAt(i,2).toString();
            model.addRow(row);
         }
        this.clientesTabla.setModel(model);
    }

    final void actualizaClientesTabla(){
        clientesTabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        clientesTabla.getColumnModel().getColumn(1).setPreferredWidth(200);
        clientesTabla.getColumnModel().getColumn(2).setPreferredWidth(250);
        clientesTabla.getColumnModel().getColumn(3).setPreferredWidth(100);
        clientesTabla.getColumnModel().getColumn(4).setPreferredWidth(120);
        clientesTabla.setRowHeight(25);

        clientesTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        clientesTabla.getColumnModel().getColumn(1).setCellRenderer(new ColorTableCellRenderer());
        clientesTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        clientesTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        clientesTabla.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer());
    }

    /*
     * Listener para seleccion de facturas a partir de la tabla
     */
    void seleccionFacturasProveedoresTabla(){
        facturasProveedoresTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                        seleccionTablaFacturasProveedores();
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

        //Chequea si no esta cargada la ventana
        if(!comprasInternalFrame.isShowing()){
            comprasInternalFrame.setVisible(true);
        }else{
            this.comprasInternalFrame.moveToFront();
        }

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
                        seleccionTablaProveedores();
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

        //Chequea si no esta cargada la ventana
        if(!proveedorInternalFrame.isShowing()){
            proveedorInternalFrame.setVisible(true);
        }else{
            this.proveedorInternalFrame.moveToFront();
        }

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
        ventasTabla.removeAll();
        ventasTabla.updateUI();
        
        String[] columnNames = {"Código",
                                "Factura",
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
                rs = statement.executeQuery("SELECT * FROM FacturasClientes WHERE " + findClave + " LIKE '%" + (String)find+"%'" + "AND Fecha"+ " LIKE '%" + (String)findClave1 +"%' ORDER BY Nserie ASC");
                while(rs.next()){                               
                    Object[] row = new Object[10];
                    row[0] = rs.getObject("id");
                    row[1] = rs.getObject("Nserie");
                    row[2] = rs.getObject("Fecha");
                    row[3] = rs.getObject("Cliente");
                    row[4] = rs.getObject("Total");
                    row[5] = rs.getObject("Cobrado");
                    row[6] = rs.getObject("Iva");
                    row[7] = rs.getObject("Fuente");
                    row[8] = rs.getObject("Recivido");
                    row[9] = rs.getObject("Notas");
                    model.addRow(row);
                }
                
                ventasTabla.setModel(model);
                rs.close();
                statement.close();
                connection.close();
                
                //Computo de totales de iva, fuente, recivido
               
                
            } catch (Exception e) {
                //custom title, warning icon
                /*
                JOptionPane.showMessageDialog(new JFrame(),
                        "No se pueden leer facturas",
                        "Error - 4", JOptionPane.ERROR_MESSAGE);
                 * 
                 */
            }
        }
    }
    
    /*
     * Actualiza tabla
     */
    void actualizaListaFacturasClientesTablaAnchos(){
        ventasTabla.getColumnModel().getColumn(0).setPreferredWidth(57);
        ventasTabla.getColumnModel().getColumn(1).setPreferredWidth(100);
        ventasTabla.getColumnModel().getColumn(2).setPreferredWidth(100);
        ventasTabla.getColumnModel().getColumn(3).setPreferredWidth(230);
        ventasTabla.getColumnModel().getColumn(4).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(5).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(6).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(7).setPreferredWidth(80);
        ventasTabla.getColumnModel().getColumn(9).setPreferredWidth(220);
        ventasTabla.setRowHeight(25);
        ventasTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        ventasTabla.getColumnModel().getColumn(1).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(5).setCellRenderer(new CheckBoxRenderer());
        ventasTabla.getColumnModel().getColumn(6).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(7).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(8).setCellRenderer(new ColorTableCellRenderer1());
        ventasTabla.getColumnModel().getColumn(9).setCellRenderer(new ColorTableCellRenderer1());
    }
   
    /*
     * Actualiza tabla
     */
    void  actualizaDetallesVentaTabla() {
        detallesVentaTabla.getColumnModel().getColumn(0).setPreferredWidth(45);
        detallesVentaTabla.getColumnModel().getColumn(1).setPreferredWidth(300);
        detallesVentaTabla.getColumnModel().getColumn(2).setPreferredWidth(83);
        detallesVentaTabla.getColumnModel().getColumn(3).setPreferredWidth(83);
        detallesVentaTabla.getColumnModel().getColumn(4).setWidth(0);
        detallesVentaTabla.getColumnModel().getColumn(4).setMaxWidth(0);
        detallesVentaTabla.getColumnModel().getColumn(4).setMinWidth(0);
        detallesVentaTabla.getColumnModel().getColumn(4).setPreferredWidth(0);
        
        detallesVentaTabla.setRowHeight(75);
        detallesVentaTabla.getColumnModel().getColumn(0).setCellRenderer(new TextAreaRenderer());
        detallesVentaTabla.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        detallesVentaTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        detallesVentaTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
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
    void actualizaArticulosTabla(){
        articulosTabla.getColumnModel().getColumn(0).setPreferredWidth(50);
        articulosTabla.getColumnModel().getColumn(1).setPreferredWidth(420);
        articulosTabla.getColumnModel().getColumn(2).setPreferredWidth(76);
        articulosTabla.getColumnModel().getColumn(3).setPreferredWidth(95);
        articulosTabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        articulosTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        articulosTabla.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        articulosTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        articulosTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        articulosTabla.getColumnModel().getColumn(4).setCellRenderer(new ImageRenderer());
        articulosTabla.setRowHeight(90);
    }
    
    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionClientesTabla(){
        clientesTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                    if (clientesTabla.getSelectedRow() != -1){
                        seleccionTablaClientes();
                    }
                }
        });
    }

    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionContratosTabla(){
        listaContratosTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                    //if (clientesTable.getSelectedRow() != -1){
                    seleccionTablaContratos();
                    //}
                }
        });
    }

    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionEquiposTabla(){
        listaEquiposTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                    //if (clientesTable.getSelectedRow() != -1){
                    seleccionTablaEquipos();
                    //}
                }
        });
    }

    /*
     * base de datos seleccionar
     */
    void seleccionTablaContratos(){
        int filaa = listaContratosTabla.getSelectedRow();
        int columna = 0;
        String num = null;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(listaContratosTabla.getValueAt(filaa,columna));

        //Chequea si no esta cargada la ventana
        if(!contratoInternalFrame.isShowing()){
            contratoInternalFrame.setVisible(true);
        }else{
            this.contratoInternalFrame.moveToFront();
        }

        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Contratos WHERE id=" + (String)num);
                while(rs.next()){
                    idContrato.setText(rs.getString("id"));
                    numContratoTxt.setText(rs.getString("nContrato"));
                    empresaContratoTxt.setText(rs.getString("Empresa"));
                    tipoContratoTxt.setText(rs.getString("TipoContrato"));
                    firmaContratoTxt.setText(rs.getString("FirmaContrato"));
                    duracionContratoTxt.setText(rs.getString("Duracion"));
                    cancelacionContratoTxt.setText(rs.getString("Cancelacion"));
                    nequiposTxt.setText(rs.getString("nEquipos"));
                    ninsumos.setText(rs.getString("cInsumos"));
                    envioContratoTxt.setText(rs.getString("EnvioInsumos"));

                    ciudadContratoTxt.setText(rs.getString("Ciudad"));
                    direccionContratoTxt.setText(rs.getString("Direccion"));
                    telefonoContratoTxt.setText(rs.getString("Telefono"));
                    pequipos.setText(rs.getString("pEquipos"));
                    pinsumos.setText(rs.getString("pInsumos"));
                }


                flagSaveContrato = 1;   //actualiza datos
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
     * base de datos seleccionar
     */
    void seleccionTablaEquipos(){
        int filaa = listaEquiposTabla.getSelectedRow();
        int columna = 0;
        String num = null;
            //num = String.valueOf(TablaPacientes.getValueAt(fila,(int)0));
        num = String.valueOf(listaEquiposTabla.getValueAt(filaa,columna));

        //Chequea si no esta cargada la ventana
        if(!equipoInternalFrame.isShowing()){
            equipoInternalFrame.setVisible(true);
        }else{
            this.equipoInternalFrame.moveToFront();
        }

        limpiaEquipo();

        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Equipos WHERE id=" + (String)num);
                while(rs.next()){
                    idEquipoTxt.setText(rs.getString("id"));
                    marcaEquipoTxt.setText(rs.getString("Marca"));
                    modeloEquipoTxt.setText(rs.getString("Modelo"));
                    numSerieEquipoTxt.setText(rs.getString("nSerie"));
                    añoFabricacionEquipoTxt.setText(rs.getString("Fabricacion"));
                    origenEquipoTxt.setText(rs.getString("LugarOrigen"));

                    fechaFuncionamientoEquipoTxt.setText(rs.getString("FechaFuncionamiento"));
                    caracteristicaEquipoTxt.setText(rs.getString("Opcion"));
                    tipoEquipoTxt.setText(rs.getString("TipoEquipo"));
                }

                infoTabbedPane.setSelectedIndex(0);
                flagSaveEquipo = 1;   //actualiza datos
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
     * FUNCION PARA BUSCAR CONTROLES DEL PACIENTE
     */
    public void LeerContratoEquipos(String find1){
        contratoEquiposComboBox.removeAllItems();
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM ContratoEquipos WHERE nSerie= '" + (String)find1 + "'");
                while(rs.next()){
                    contratoEquiposComboBox.addItem(rs.getObject("FechaInstalacion"));
                }

                flagSaveContratoEquipo = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//FIN readControlesInDB

    /*
     * FUNCION PARA BUSCAR CONTROLES DEL PACIENTE
     */
    public void LeerFallasEquipos(String find1){
        fallasEquiposComboBox.removeAllItems();
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Fallas WHERE nSerie= '" + (String)find1 + "'");
                while(rs.next()){
                    fallasEquiposComboBox.addItem(rs.getObject("Fecha"));
                }

                flagSaveFallo = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//FIN readControlesInDB

     /*
     * FUNCION PARA BUSCAR CONTROLES DEL PACIENTE
     */
    public void LeerContratoEquiposTodo(String find1, String find2){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM ContratoEquipos WHERE FechaInstalacion = '" + (String)find1 + "' AND nSerie = '" + (String)find2 +"'");
                //rs = statement.executeQuery("SELECT * FROM ContratoEquipos WHERE FechaInstalacion= '30/jul/2010'");
                while(rs.next()){
                    idContratoEquipoTxt.setText(rs.getString("id"));
                    contratoEquipoTxt.setText(rs.getString("Contrato"));
                    activoEquipoComboBox.setSelectedIndex(Integer.valueOf((String)rs.getObject("Activo")));
                    fechaInstalacionEquipoTxt.setText(rs.getString("FechaInstalacion"));
                    fechaRetiroEquipoTxt.setText(rs.getString("FechaRetiro"));
                }

                try
                {
                    Thread.sleep(100);
                }catch (InterruptedException ie){
                    System.out.println(ie.getMessage());
                }

                flagSaveContratoEquipo = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//FIN readControlesInDB



     /*
     * FUNCION PARA BUSCAR CONTROLES DEL PACIENTE
     */
    public void LeerFallasEquiposTodo(String find1, String find2){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Fallas WHERE Fecha = '" + (String)find1 + "' AND nSerie = '" + (String)find2 +"'");
                //rs = statement.executeQuery("SELECT * FROM ContratoEquipos WHERE FechaInstalacion= '30/jul/2010'");
                while(rs.next()){
                    idContratoEquipoFalloTxt.setText(rs.getString("id"));
                    detalleTxt.setText(rs.getString("Detalle"));
                }

                try
                {
                    Thread.sleep(100);
                }catch (InterruptedException ie){
                    System.out.println(ie.getMessage());
                }

                flagSaveFallo = 1;   //actualiza datos
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }//FIN readControlesInDB


    /*
     * base de datos seleccionar
     */
    void seleccionTablaClientes(){
        int filaa = clientesTabla.getSelectedRow();
        int columna = 0;
        String num = null;
        num = String.valueOf(clientesTabla.getValueAt(filaa,columna));

        //Chequea si no esta cargada la ventana
        if(!clientesInternalFrame.isShowing()){
            clientesInternalFrame.setVisible(true);
        }else{
            this.clientesInternalFrame.moveToFront();
        }

        String buscar = buscarClienteTxt.getText();
        String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
        DefaultTableModel kde = bdc.leer("id", (String)num,"=");
        leerClientes2Ventana(kde);
        flagSaveFichaCliente = 1; //actualizar registro
    }
    
    
    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionVentasTabla(){
        ventasTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {                     
                    if (ventasTabla.getSelectedRow() != -1){
                        seleccionTablaVentas();
                    }
                }
        });
    }

    /*
     * base de datos seleccionar
     */
    void seleccionTablaVentas(){
        int filaa = ventasTabla.getSelectedRow();
        int columna = 0;
        String num = null;
        num = String.valueOf(ventasTabla.getValueAt(filaa,columna));

        //Chequea si no esta cargada la ventana
        if(!facturaVentaInternalFrame.isShowing()){
            facturaVentaInternalFrame.setVisible(true);
        }else{
            this.facturaVentaInternalFrame.moveToFront();
        }

        String buscar = buscarFacturaClienteTxt.getText();
        String buscarClave = (String)buscarFacturaClienteComboBox.getSelectedItem();
        String find1;
        if(fechaBuscarRadioButton.isSelected()){

            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
        DefaultTableModel kde = bdv.leer("id", (String)num, find1, "=");
        leerVentas2Ventana(kde);
        actualizaVentasTabla();
        flagSaveFichaFacturaCliente = 1; //actualizar registro
        
        kde = bddv.leer("nserie", numeroSerieTxt.getText(), "=");
        leerDetallesVenta2Tabla(kde);
        actualizaDetallesVentaTabla();

    }

    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionArticulosTabla(){
        articulosTabla.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                        seleccionTablaArticulos();
                }
        });
    }

    /*
     * Listener para seleccion de articulos a partir de la tabla
     */
    void seleccionTesoreriaTabla(){
        tesoreriaTable.getSelectionModel().addListSelectionListener(
            new ListSelectionListener() {
            @Override
                public void valueChanged(ListSelectionEvent  e) {
                        seleccionTablaTesoreria();
                }
        });
    }

    /*
     * base de datos seleccionar
     */
    void seleccionTablaArticulos(){
        int filaa = articulosTabla.getSelectedRow();
        int columna = 0;
        String num = null;
        num = String.valueOf(articulosTabla.getValueAt(filaa,columna));

        //Chequea si no esta cargada la ventana
        //Chequea si no esta cargada la ventana
        if(!articuloInternalFrame.isShowing()){
            articuloInternalFrame.setVisible(true);
        }else{
            this.articuloInternalFrame.moveToFront();
        }
        filename = System.getProperty("user.home") + "/jContab/copia_temp.jpg";
        String buscar = buscarArticuloTxt.getText();
        String buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
        DefaultTableModel kde = bda.leer("codarticulo", (String)num, "=");
        leerArticulos2Ventana(kde);
        flagSaveFichaArticulo = 1; //actualizar registro
    }
    
    /*
     * base de datos seleccionar
     */
    void seleccionTablaTesoreria(){
        int filaa = tesoreriaTable.getSelectedRow();
        int columna = 0;
        String num = null;
        num = String.valueOf(tesoreriaTable.getValueAt(filaa,columna));

        //Chequea si no esta cargada la ventana
        if(!apunteContableInternalFrame.isShowing()){
            apunteContableInternalFrame.setVisible(true);
        }else{
            this.apunteContableInternalFrame.moveToFront();
        }

        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Tesoreria WHERE id=" + (String)num);
                while(rs.next()){
                    idTesoreriaTxt.setText(rs.getString("id"));
                    fechaTesoreriaTxt.setText(rs.getString("Fecha"));
                    conceptoApunteContableComboBox.setSelectedItem((String)rs.getObject("Concepto"));
                    ingresoTxt.setText(rs.getString("Ingresos"));
                    pagoTxt.setText(rs.getString("Pagos"));
                    clienteProveedorTxt.setText(rs.getString("ClienteProveedor"));
                    memoTxt.setText(rs.getString("Memo"));

                }
                flagSaveTesoreria = 1;   //actualiza datos
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
     * Actualiza tabla
     */
    final void actualizaListaTesoreriaTablaAnchos(){
        tesoreriaTable.getColumnModel().getColumn(0).setPreferredWidth(60);
        tesoreriaTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        tesoreriaTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        tesoreriaTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        tesoreriaTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        tesoreriaTable.getColumnModel().getColumn(5).setPreferredWidth(120);
        tesoreriaTable.setRowHeight(25);

        tesoreriaTable.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        tesoreriaTable.getColumnModel().getColumn(1).setCellRenderer(new ColorTableCellRenderer());
        tesoreriaTable.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        tesoreriaTable.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        tesoreriaTable.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer());
    }

    /*
     * Actualiza tabla
     */
    void  actualizaTablaContratosAnchos() {
        listaContratosTabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        listaContratosTabla.getColumnModel().getColumn(1).setPreferredWidth(75);
        listaContratosTabla.getColumnModel().getColumn(2).setPreferredWidth(200);
        listaContratosTabla.getColumnModel().getColumn(3).setPreferredWidth(85);
        listaContratosTabla.getColumnModel().getColumn(4).setPreferredWidth(100);
        listaContratosTabla.getColumnModel().getColumn(5).setPreferredWidth(80);
        listaContratosTabla.getColumnModel().getColumn(6).setPreferredWidth(100);
        listaContratosTabla.getColumnModel().getColumn(7).setPreferredWidth(80);
        listaContratosTabla.getColumnModel().getColumn(9).setPreferredWidth(150);

        listaContratosTabla.setRowHeight(25);
        
    }

    /*
     * Actualiza tabla
     */
    void  actualizaTablaEquiposAnchos() {
        listaEquiposTabla.getColumnModel().getColumn(0).setPreferredWidth(40);
        listaEquiposTabla.getColumnModel().getColumn(1).setPreferredWidth(150);
        listaEquiposTabla.getColumnModel().getColumn(2).setPreferredWidth(200);
        listaEquiposTabla.getColumnModel().getColumn(3).setPreferredWidth(200);
        listaEquiposTabla.getColumnModel().getColumn(4).setPreferredWidth(200);
        listaEquiposTabla.getColumnModel().getColumn(5).setPreferredWidth(100);

        listaEquiposTabla.getColumnModel().getColumn(4).setCellRenderer(new ColorTableCellRenderer());
        listaEquiposTabla.getColumnModel().getColumn(5).setCellRenderer(new ColorTableCellRenderer());

        listaEquiposTabla.setRowHeight(25);
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
                    //setBackground(new Color(230,237,242));
                    setBackground(Color.WHITE);
                }

                if (isSelected) {
                    // this cell is the anchor and the table has the focus
                    setBackground(new Color(153,204,255));
                }
        
                if(column == 1){
                    setHorizontalAlignment(JLabel.LEFT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 0){
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                    //Number value = (Number)obj;
                    //setText(value.toString());
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
                    //setBackground(new Color(230,237,242));
                    setBackground(Color.WHITE);
                }

                if (isSelected) {
                    // this cell is the anchor and the table has the focus
                    setBackground(new Color(153,204,255));
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
                    setHorizontalAlignment(JLabel.CENTER);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 5){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 6){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.BOLD, 12));
                    //setForeground(Color.red);
                    setForeground(Color.blue);
                    setText((String)obj);
                }else if(column == 7){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.BOLD, 12));
                    setText((String)obj);
                }else if(column == 8){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 3){
                    setHorizontalAlignment(JLabel.LEFT);
                    setFont(new Font("Dialog", Font.PLAIN, 12));
                    setText((String)obj);
                }else if(column == 4){
                    setHorizontalAlignment(JLabel.RIGHT);
                    setFont(new Font("Dialog", Font.BOLD, 12));
                    setText((String)obj);
                }else{
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
                //setBackground(new Color(230,237,242));
                setBackground(Color.WHITE);
                //setBorder(BorderFactory.createLineBorder(new Color(235, 232, 232)));
            }

            if (isSelected) {
                setBackground(new Color(153,204,255));
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
                //setBackground(new Color(235, 232, 232));
                setBackground(Color.WHITE);
            }
            
            if (isSelected) {
                    // this cell is the anchor and the table has the focus
                    setBackground(new Color(153,204,255));
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
        clientesPanel = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        jLabel8 = new javax.swing.JLabel();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        buscarClienteComboBox = new javax.swing.JComboBox();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        buscarClienteTxt = new javax.swing.JTextField();
        jSeparator15 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt = new javax.swing.JButton();
        jSeparator82 = new javax.swing.JToolBar.Separator();
        nuevoClienteButton = new javax.swing.JButton();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        clientesInternalFrame = new javax.swing.JInternalFrame();
        jToolBar9 = new javax.swing.JToolBar();
        nuevoClienteButton2 = new javax.swing.JButton();
        jSeparator76 = new javax.swing.JToolBar.Separator();
        borrarFichaClienteButton = new javax.swing.JButton();
        jSeparator19 = new javax.swing.JToolBar.Separator();
        guardarClienteButton2 = new javax.swing.JButton();
        jSeparator80 = new javax.swing.JToolBar.Separator();
        crearFacturaClienteButton2 = new javax.swing.JButton();
        jSeparator81 = new javax.swing.JToolBar.Separator();
        cerrarButton = new javax.swing.JButton();
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
        clienteTxt = new javax.swing.JTextField();
        rucClienteTxt = new javax.swing.JTextField();
        nombreComercialClienteTxt = new javax.swing.JTextField();
        fechaClienteTxt = new javax.swing.JTextField();
        ciudadClienteTxt = new javax.swing.JTextField();
        direccionClienteTxt = new javax.swing.JTextField();
        telefonoClienteTxt = new javax.swing.JTextField();
        emailClienteTxt = new javax.swing.JTextField();
        idClienteTxt = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        clientesTabla = new javax.swing.JTable();
        ventasPanel = new javax.swing.JPanel();
        jToolBar3 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jSeparator20 = new javax.swing.JToolBar.Separator();
        buscarFacturaClienteComboBox = new javax.swing.JComboBox();
        jSeparator48 = new javax.swing.JToolBar.Separator();
        buscarFacturaClienteTxt = new javax.swing.JTextField();
        jSeparator99 = new javax.swing.JToolBar.Separator();
        cleanFacturaButton = new javax.swing.JButton();
        jSeparator23 = new javax.swing.JToolBar.Separator();
        fechaBuscarRadioButton = new javax.swing.JRadioButton();
        jSeparator22 = new javax.swing.JToolBar.Separator();
        buscarFechaTxt = new javax.swing.JTextField();
        jSeparator86 = new javax.swing.JToolBar.Separator();
        nuevaFacturaButton = new javax.swing.JButton();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        facturaVentaInternalFrame = new javax.swing.JInternalFrame();
        jToolBar4 = new javax.swing.JToolBar();
        borrarFacturaClienteButton = new javax.swing.JButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        guardarFacturaButton = new javax.swing.JButton();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        agregarArticuloButton = new javax.swing.JButton();
        jSeparator25 = new javax.swing.JToolBar.Separator();
        eliminarArticuloButton = new javax.swing.JButton();
        jSeparator27 = new javax.swing.JToolBar.Separator();
        jSeparator24 = new javax.swing.JToolBar.Separator();
        calucularFacturaButton = new javax.swing.JButton();
        jSeparator26 = new javax.swing.JToolBar.Separator();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        imprimirFacturaButton = new javax.swing.JButton();
        jSeparator83 = new javax.swing.JToolBar.Separator();
        cerrarButton1 = new javax.swing.JButton();
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
        detallesVentaTabla = new javax.swing.JTable();
        jLabel34 = new javax.swing.JLabel();
        numeroSerieTxt = new javax.swing.JTextField();
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
        cobrarFacturaCheckBox = new javax.swing.JCheckBox();
        jScrollPane5 = new javax.swing.JScrollPane();
        ventasTabla = new javax.swing.JTable();
        proveedoresPanel = new javax.swing.JPanel();
        jToolBar8 = new javax.swing.JToolBar();
        jSeparator49 = new javax.swing.JToolBar.Separator();
        jLabel37 = new javax.swing.JLabel();
        jSeparator50 = new javax.swing.JToolBar.Separator();
        buscarProveedorTxt = new javax.swing.JTextField();
        jSeparator51 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt1 = new javax.swing.JButton();
        jSeparator56 = new javax.swing.JToolBar.Separator();
        nuevoProveedorButton = new javax.swing.JButton();
        jSeparator91 = new javax.swing.JToolBar.Separator();
        jLayeredPane3 = new javax.swing.JLayeredPane();
        proveedorInternalFrame = new javax.swing.JInternalFrame();
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
        jToolBar12 = new javax.swing.JToolBar();
        jSeparator108 = new javax.swing.JToolBar.Separator();
        nuevoProveedorButton1 = new javax.swing.JButton();
        jSeparator109 = new javax.swing.JToolBar.Separator();
        borrarProveedorButton1 = new javax.swing.JButton();
        jSeparator110 = new javax.swing.JToolBar.Separator();
        guardarProveedorButton1 = new javax.swing.JButton();
        jSeparator111 = new javax.swing.JToolBar.Separator();
        crearFacturaClienteButton3 = new javax.swing.JButton();
        jSeparator112 = new javax.swing.JToolBar.Separator();
        cerrarButton2 = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jScrollPane4 = new javax.swing.JScrollPane();
        proveedoresTable = new javax.swing.JTable();
        comprasPanel = new javax.swing.JPanel();
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
        nuevaFacturaButton2 = new javax.swing.JButton();
        jLayeredPane5 = new javax.swing.JLayeredPane();
        comprasInternalFrame = new javax.swing.JInternalFrame();
        jToolBar11 = new javax.swing.JToolBar();
        jSeparator64 = new javax.swing.JToolBar.Separator();
        nuevaFacturaButton1 = new javax.swing.JButton();
        jSeparator65 = new javax.swing.JToolBar.Separator();
        borrarFacturaClienteButton1 = new javax.swing.JButton();
        jSeparator66 = new javax.swing.JToolBar.Separator();
        guardarFacturaButton1 = new javax.swing.JButton();
        jSeparator67 = new javax.swing.JToolBar.Separator();
        jSeparator68 = new javax.swing.JToolBar.Separator();
        calucularFacturaButton1 = new javax.swing.JButton();
        jSeparator79 = new javax.swing.JToolBar.Separator();
        cerrarButton3 = new javax.swing.JButton();
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
        jScrollPane11 = new javax.swing.JScrollPane();
        facturasProveedoresTable = new javax.swing.JTable();
        articulosPanel = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        jSeparator32 = new javax.swing.JToolBar.Separator();
        buscarArticuloComboBox = new javax.swing.JComboBox();
        jSeparator55 = new javax.swing.JToolBar.Separator();
        buscarArticuloTxt = new javax.swing.JTextField();
        jSeparator37 = new javax.swing.JToolBar.Separator();
        limpiarArticuloTxt = new javax.swing.JButton();
        jSeparator36 = new javax.swing.JToolBar.Separator();
        nuevoArticulobButton = new javax.swing.JButton();
        jLayeredPane4 = new javax.swing.JLayeredPane();
        articuloInternalFrame = new javax.swing.JInternalFrame();
        jToolBar6 = new javax.swing.JToolBar();
        nuevoArticuloButton = new javax.swing.JButton();
        jSeparator39 = new javax.swing.JToolBar.Separator();
        borrarFichaArticuloButton = new javax.swing.JButton();
        jSeparator40 = new javax.swing.JToolBar.Separator();
        guardarArticuloButton = new javax.swing.JButton();
        jSeparator41 = new javax.swing.JToolBar.Separator();
        jSeparator42 = new javax.swing.JToolBar.Separator();
        agregarArticuloAFacturaButton = new javax.swing.JButton();
        jSeparator92 = new javax.swing.JToolBar.Separator();
        jSeparator38 = new javax.swing.JToolBar.Separator();
        cerrarButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        idArticuloTxt = new javax.swing.JTextField();
        jLabel36 = new javax.swing.JLabel();
        FechaIngresoArticuloTxt = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        proveedorTxt = new javax.swing.JTextField();
        stockTxt = new javax.swing.JTextField();
        jLabel25 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        ImagenLabel = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        detalleArticuloTxt = new javax.swing.JTextArea();
        jPanel11 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        precioCosteTxt = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        beneficioTxt = new javax.swing.JTextField();
        jLabel29 = new javax.swing.JLabel();
        precioVentaTxt = new javax.swing.JTextField();
        jLabel30 = new javax.swing.JLabel();
        precioFinalTxt = new javax.swing.JTextField();
        calcularArticuloButton = new javax.swing.JButton();
        jLabel97 = new javax.swing.JLabel();
        tipoArticuloTxt = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        articulosTabla = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jToolBar14 = new javax.swing.JToolBar();
        jSeparator53 = new javax.swing.JToolBar.Separator();
        buscarEquipoComboBox = new javax.swing.JComboBox();
        jSeparator29 = new javax.swing.JToolBar.Separator();
        jSeparator30 = new javax.swing.JToolBar.Separator();
        jLabel84 = new javax.swing.JLabel();
        jSeparator75 = new javax.swing.JToolBar.Separator();
        buscarEquipoTxt = new javax.swing.JTextField();
        jSeparator77 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt3 = new javax.swing.JButton();
        jSeparator95 = new javax.swing.JToolBar.Separator();
        jSeparator100 = new javax.swing.JToolBar.Separator();
        nuevoClienteButton3 = new javax.swing.JButton();
        jSeparator78 = new javax.swing.JToolBar.Separator();
        jSeparator84 = new javax.swing.JToolBar.Separator();
        jSeparator85 = new javax.swing.JToolBar.Separator();
        jLayeredPane7 = new javax.swing.JLayeredPane();
        equipoInternalFrame = new javax.swing.JInternalFrame();
        jToolBar16 = new javax.swing.JToolBar();
        jSeparator89 = new javax.swing.JToolBar.Separator();
        nuevoEquipoButton = new javax.swing.JButton();
        jSeparator90 = new javax.swing.JToolBar.Separator();
        borrarEquipoButton = new javax.swing.JButton();
        jSeparator96 = new javax.swing.JToolBar.Separator();
        guardarEquipoButton = new javax.swing.JButton();
        jSeparator97 = new javax.swing.JToolBar.Separator();
        jSeparator102 = new javax.swing.JToolBar.Separator();
        jSeparator103 = new javax.swing.JToolBar.Separator();
        cerrarContratoButton2 = new javax.swing.JButton();
        jSeparator104 = new javax.swing.JToolBar.Separator();
        jLabel98 = new javax.swing.JLabel();
        jLabel102 = new javax.swing.JLabel();
        idEquipoTxt = new javax.swing.JTextField();
        tipoEquipoTxt = new javax.swing.JTextField();
        infoTabbedPane = new javax.swing.JTabbedPane();
        jPanel15 = new javax.swing.JPanel();
        jLabel104 = new javax.swing.JLabel();
        modeloEquipoTxt = new javax.swing.JTextField();
        jLabel105 = new javax.swing.JLabel();
        añoFabricacionEquipoTxt = new javax.swing.JTextField();
        jLabel106 = new javax.swing.JLabel();
        origenEquipoTxt = new javax.swing.JTextField();
        jLabel107 = new javax.swing.JLabel();
        fechaFuncionamientoEquipoTxt = new javax.swing.JTextField();
        jLabel108 = new javax.swing.JLabel();
        marcaEquipoTxt = new javax.swing.JTextField();
        numSerieEquipoTxt = new javax.swing.JTextField();
        jLabel109 = new javax.swing.JLabel();
        jLabel110 = new javax.swing.JLabel();
        caracteristicaEquipoTxt = new javax.swing.JTextField();
        jPanel16 = new javax.swing.JPanel();
        jButton13 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        contratoEquiposComboBox = new javax.swing.JComboBox();
        jButton15 = new javax.swing.JButton();
        jLabel85 = new javax.swing.JLabel();
        jLabel86 = new javax.swing.JLabel();
        jLabel88 = new javax.swing.JLabel();
        idContratoEquipoTxt = new javax.swing.JTextField();
        contratoEquipoTxt = new javax.swing.JTextField();
        fechaInstalacionEquipoTxt = new javax.swing.JTextField();
        jLabel89 = new javax.swing.JLabel();
        fechaRetiroEquipoTxt = new javax.swing.JTextField();
        jLabel90 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel87 = new javax.swing.JLabel();
        activoEquipoComboBox = new javax.swing.JComboBox();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane14 = new javax.swing.JScrollPane();
        contactoTextArea3 = new javax.swing.JTextArea();
        jButton16 = new javax.swing.JButton();
        jButton17 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel91 = new javax.swing.JLabel();
        contratoEquiposComboBox1 = new javax.swing.JComboBox();
        jButton18 = new javax.swing.JButton();
        idContratoEquipoTxt1 = new javax.swing.JTextField();
        jLabel92 = new javax.swing.JLabel();
        jLabel93 = new javax.swing.JLabel();
        jPanel10 = new javax.swing.JPanel();
        jButton19 = new javax.swing.JButton();
        jButton20 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        fallasEquiposComboBox = new javax.swing.JComboBox();
        jLabel94 = new javax.swing.JLabel();
        jLabel95 = new javax.swing.JLabel();
        idContratoEquipoFalloTxt = new javax.swing.JTextField();
        jLabel96 = new javax.swing.JLabel();
        jScrollPane15 = new javax.swing.JScrollPane();
        detalleTxt = new javax.swing.JTextArea();
        jButton21 = new javax.swing.JButton();
        jScrollPane13 = new javax.swing.JScrollPane();
        listaEquiposTabla = new javax.swing.JTable();
        contratosPanel = new javax.swing.JPanel();
        jToolBar7 = new javax.swing.JToolBar();
        jSeparator34 = new javax.swing.JToolBar.Separator();
        buscarContratoComboBox = new javax.swing.JComboBox();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jLabel75 = new javax.swing.JLabel();
        jSeparator70 = new javax.swing.JToolBar.Separator();
        buscarContratoTxt = new javax.swing.JTextField();
        jSeparator71 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt2 = new javax.swing.JButton();
        jSeparator94 = new javax.swing.JToolBar.Separator();
        jSeparator98 = new javax.swing.JToolBar.Separator();
        nuevoClienteButton1 = new javax.swing.JButton();
        jSeparator72 = new javax.swing.JToolBar.Separator();
        jSeparator73 = new javax.swing.JToolBar.Separator();
        jSeparator74 = new javax.swing.JToolBar.Separator();
        jLayeredPane6 = new javax.swing.JLayeredPane();
        contratoInternalFrame = new javax.swing.JInternalFrame();
        jPanel2 = new javax.swing.JPanel();
        jLabel76 = new javax.swing.JLabel();
        idContrato = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        empresaContratoTxt = new javax.swing.JTextField();
        jLabel67 = new javax.swing.JLabel();
        tipoContratoTxt = new javax.swing.JTextField();
        ciudadContratoTxt = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel53 = new javax.swing.JLabel();
        direccionContratoTxt = new javax.swing.JTextField();
        jLabel64 = new javax.swing.JLabel();
        telefonoContratoTxt = new javax.swing.JTextField();
        jLabel66 = new javax.swing.JLabel();
        firmaContratoTxt = new javax.swing.JTextField();
        jLabel70 = new javax.swing.JLabel();
        duracionContratoTxt = new javax.swing.JTextField();
        jLabel69 = new javax.swing.JLabel();
        jLabel68 = new javax.swing.JLabel();
        nequiposTxt = new javax.swing.JTextField();
        pequipos = new javax.swing.JTextField();
        jLabel74 = new javax.swing.JLabel();
        ninsumos = new javax.swing.JTextField();
        pinsumos = new javax.swing.JTextField();
        jLabel73 = new javax.swing.JLabel();
        envioContratoTxt = new javax.swing.JTextField();
        jLabel82 = new javax.swing.JLabel();
        numContratoTxt = new javax.swing.JTextField();
        jLabel83 = new javax.swing.JLabel();
        cancelacionContratoTxt = new javax.swing.JTextField();
        jToolBar13 = new javax.swing.JToolBar();
        jSeparator43 = new javax.swing.JToolBar.Separator();
        nuevoContratoButton = new javax.swing.JButton();
        jSeparator44 = new javax.swing.JToolBar.Separator();
        borrarContratoButton = new javax.swing.JButton();
        jSeparator46 = new javax.swing.JToolBar.Separator();
        guardarContratoButton = new javax.swing.JButton();
        jSeparator47 = new javax.swing.JToolBar.Separator();
        jSeparator52 = new javax.swing.JToolBar.Separator();
        jSeparator93 = new javax.swing.JToolBar.Separator();
        equipoAsociarButton = new javax.swing.JButton();
        jSeparator88 = new javax.swing.JToolBar.Separator();
        cerrarContratoButton = new javax.swing.JButton();
        jScrollPane8 = new javax.swing.JScrollPane();
        listaContratosTabla = new javax.swing.JTable();
        jSeparator31 = new javax.swing.JSeparator();
        tesoreriaPanel = new javax.swing.JPanel();
        jToolBar15 = new javax.swing.JToolBar();
        jLabel6 = new javax.swing.JLabel();
        jSeparator69 = new javax.swing.JToolBar.Separator();
        buscarTesoreriaComboBox = new javax.swing.JComboBox();
        jSeparator87 = new javax.swing.JToolBar.Separator();
        buscarTesoreriaTxt = new javax.swing.JTextField();
        jSeparator105 = new javax.swing.JToolBar.Separator();
        clearBusquedaTxt4 = new javax.swing.JButton();
        jSeparator107 = new javax.swing.JToolBar.Separator();
        nuevTesoreriaButton = new javax.swing.JButton();
        jLayeredPane8 = new javax.swing.JLayeredPane();
        apunteContableInternalFrame = new javax.swing.JInternalFrame();
        jToolBar17 = new javax.swing.JToolBar();
        nuevoApunteContableButton = new javax.swing.JButton();
        jSeparator115 = new javax.swing.JToolBar.Separator();
        borrarApunteContableButton = new javax.swing.JButton();
        jSeparator116 = new javax.swing.JToolBar.Separator();
        guardarApunteContableButton = new javax.swing.JButton();
        jSeparator117 = new javax.swing.JToolBar.Separator();
        cerrarApunteContableButton = new javax.swing.JButton();
        idTesoreriaTxt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel65 = new javax.swing.JLabel();
        fechaTesoreriaTxt = new javax.swing.JTextField();
        jLabel99 = new javax.swing.JLabel();
        jLabel100 = new javax.swing.JLabel();
        clienteProveedorTxt = new javax.swing.JTextField();
        ingresoTxt = new javax.swing.JTextField();
        jLabel101 = new javax.swing.JLabel();
        jLabel103 = new javax.swing.JLabel();
        pagoTxt = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        memoTxt = new javax.swing.JTextArea();
        jLabel111 = new javax.swing.JLabel();
        conceptoApunteContableComboBox = new javax.swing.JComboBox();
        jScrollPane10 = new javax.swing.JScrollPane();
        tesoreriaTable = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();
        jMenu3 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem5 = new javax.swing.JMenuItem();
        jMenuItem11 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator33 = new javax.swing.JPopupMenu.Separator();
        reporteFacturasCobradasMenuItem = new javax.swing.JMenuItem();
        facturasNoCobradasMenuItem = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        listaArticulosMenuItem = new javax.swing.JMenuItem();
        jSeparator54 = new javax.swing.JPopupMenu.Separator();
        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem7 = new javax.swing.JMenuItem();
        jMenuItem10 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Software PYME");

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 255));
        jTabbedPane1.setBorder(javax.swing.BorderFactory.createTitledBorder(""));
        jTabbedPane1.setTabLayoutPolicy(javax.swing.JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        clientesPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setBorderPainted(false);
        jToolBar2.setPreferredSize(new java.awt.Dimension(616, 32));

        jLabel8.setText("Buscar por:");
        jToolBar2.add(jLabel8);
        jToolBar2.add(jSeparator3);

        buscarClienteComboBox.setBackground(new java.awt.Color(255, 255, 255));
        buscarClienteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cliente", "RUC", "Organizacion", "Ciudad" }));
        jToolBar2.add(buscarClienteComboBox);
        jToolBar2.add(jSeparator6);

        buscarClienteTxt.setMinimumSize(new java.awt.Dimension(250, 19));
        buscarClienteTxt.setPreferredSize(new java.awt.Dimension(320, 27));
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

        clientesInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        clientesInternalFrame.setClosable(true);
        clientesInternalFrame.setTitle("Ficha del cliente");
        clientesInternalFrame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-attending.png"))); // NOI18N
        clientesInternalFrame.setVisible(true);

        jToolBar9.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar9.setFloatable(false);
        jToolBar9.setRollover(true);
        jToolBar9.setPreferredSize(new java.awt.Dimension(616, 32));

        nuevoClienteButton2.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoClienteButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoClienteButton2.setText("Nuevo");
        nuevoClienteButton2.setToolTipText("Nuevo cliente");
        nuevoClienteButton2.setFocusable(false);
        nuevoClienteButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoClienteButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButton2ActionPerformed(evt);
            }
        });
        jToolBar9.add(nuevoClienteButton2);
        jToolBar9.add(jSeparator76);

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
        jToolBar9.add(borrarFichaClienteButton);
        jToolBar9.add(jSeparator19);

        guardarClienteButton2.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarClienteButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarClienteButton2.setText("Guardar");
        guardarClienteButton2.setToolTipText("Guardar cliente");
        guardarClienteButton2.setFocusable(false);
        guardarClienteButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarClienteButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButton2ActionPerformed(evt);
            }
        });
        jToolBar9.add(guardarClienteButton2);
        jToolBar9.add(jSeparator80);

        crearFacturaClienteButton2.setBackground(new java.awt.Color(255, 255, 51));
        crearFacturaClienteButton2.setFont(new java.awt.Font("Dialog", 1, 10));
        crearFacturaClienteButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        crearFacturaClienteButton2.setText("Factura");
        crearFacturaClienteButton2.setToolTipText("Crear factura");
        crearFacturaClienteButton2.setFocusable(false);
        crearFacturaClienteButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        crearFacturaClienteButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearFacturaClienteButton2ActionPerformed(evt);
            }
        });
        jToolBar9.add(crearFacturaClienteButton2);
        jToolBar9.add(jSeparator81);

        cerrarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarButton.setText("Cerrar");
        cerrarButton.setFocusable(false);
        cerrarButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarButtonActionPerformed(evt);
            }
        });
        jToolBar9.add(cerrarButton);

        jLabel9.setText("Código Cliente:");

        jLabel10.setText("Cliente:");

        jLabel11.setText("R.U.C./C.I:");

        jLabel12.setText("Nombre Comercial:");

        jLabel13.setText("Fecha:");

        jLabel14.setText("Ciudad:");

        jLabel15.setText("Dirección:");

        jLabel16.setText("Teléfono:");

        jLabel17.setText("Email:");

        clienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        rucClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        nombreComercialClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        fechaClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ciudadClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        direccionClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        telefonoClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        emailClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        idClienteTxt.setEditable(false);
        idClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 16, Short.MAX_VALUE)
                        .addComponent(idClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rucClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombreComercialClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel13)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel14)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ciudadClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(telefonoClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                            .addComponent(direccionClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(idClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(rucClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(nombreComercialClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(fechaClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ciudadClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(direccionClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(telefonoClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(emailClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout clientesInternalFrameLayout = new javax.swing.GroupLayout(clientesInternalFrame.getContentPane());
        clientesInternalFrame.getContentPane().setLayout(clientesInternalFrameLayout);
        clientesInternalFrameLayout.setHorizontalGroup(
            clientesInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientesInternalFrameLayout.createSequentialGroup()
                .addGroup(clientesInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar9, javax.swing.GroupLayout.PREFERRED_SIZE, 402, Short.MAX_VALUE)
                    .addGroup(clientesInternalFrameLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        clientesInternalFrameLayout.setVerticalGroup(
            clientesInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientesInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar9, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        clientesInternalFrame.setBounds(160, 130, 420, 370);
        jLayeredPane1.add(clientesInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane1.setBackground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setBorder(null);
        jScrollPane1.setMaximumSize(new java.awt.Dimension(667, 32767));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(490, 400));

        clientesTabla.setModel(new javax.swing.table.DefaultTableModel(
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
        clientesTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        clientesTabla.setShowHorizontalLines(true);
        clientesTabla.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                clientesTablaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(clientesTabla);

        jScrollPane1.setBounds(10, 0, 740, 560);
        jLayeredPane1.add(jScrollPane1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout clientesPanelLayout = new javax.swing.GroupLayout(clientesPanel);
        clientesPanel.setLayout(clientesPanelLayout);
        clientesPanelLayout.setHorizontalGroup(
            clientesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, clientesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 764, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 764, Short.MAX_VALUE))
                .addContainerGap())
        );
        clientesPanelLayout.setVerticalGroup(
            clientesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientesPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Clientes", new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-attending_1.png")), clientesPanel); // NOI18N

        ventasPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);
        jToolBar3.setBorderPainted(false);

        jLabel3.setText("Buscar  por:");
        jToolBar3.add(jLabel3);
        jToolBar3.add(jSeparator20);

        buscarFacturaClienteComboBox.setBackground(new java.awt.Color(255, 255, 255));
        buscarFacturaClienteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cliente", "RUC", "Nserie", "Ciudad" }));
        buscarFacturaClienteComboBox.setPreferredSize(new java.awt.Dimension(100, 25));
        jToolBar3.add(buscarFacturaClienteComboBox);
        jToolBar3.add(jSeparator48);

        buscarFacturaClienteTxt.setMinimumSize(new java.awt.Dimension(4, 39));
        buscarFacturaClienteTxt.setPreferredSize(new java.awt.Dimension(270, 27));
        buscarFacturaClienteTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFacturaClienteTxtCaretUpdate(evt);
            }
        });
        jToolBar3.add(buscarFacturaClienteTxt);
        jToolBar3.add(jSeparator99);

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
        fechaBuscarRadioButton.setText("Fecha");
        fechaBuscarRadioButton.setFocusable(false);
        fechaBuscarRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fechaBuscarRadioButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(fechaBuscarRadioButton);
        jToolBar3.add(jSeparator22);

        buscarFechaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        buscarFechaTxt.setPreferredSize(new java.awt.Dimension(100, 27));
        buscarFechaTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFechaTxtCaretUpdate(evt);
            }
        });
        jToolBar3.add(buscarFechaTxt);
        jToolBar3.add(jSeparator86);

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
        jToolBar3.add(nuevaFacturaButton);

        facturaVentaInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        facturaVentaInternalFrame.setClosable(true);
        facturaVentaInternalFrame.setTitle("Factura de venta");
        facturaVentaInternalFrame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        facturaVentaInternalFrame.setVisible(true);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);

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
        jToolBar4.add(jSeparator14);

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

        cerrarButton1.setFont(new java.awt.Font("DejaVu Sans", 1, 10));
        cerrarButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarButton1.setText("Cerrar");
        cerrarButton1.setFocusable(false);
        cerrarButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarButton1ActionPerformed(evt);
            }
        });
        jToolBar4.add(cerrarButton1);

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

        detallesVentaTabla.setFont(new java.awt.Font("Dialog", 0, 10));
        detallesVentaTabla.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Cant.", "Articulo", "P. Unit.", "P. Total", "id"
            }
        ));
        detallesVentaTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane9.setViewportView(detallesVentaTabla);

        jLabel34.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel34.setText("Nº Serie:");

        numeroSerieTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        numeroSerieTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

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

        cobrarFacturaCheckBox.setText("Cobrar factura a Cliente");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addGap(47, 47, 47)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cobrarFacturaCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE)
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
                            .addComponent(fuentePorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE)
                            .addComponent(jLabel60)
                            .addComponent(ivaPorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 48, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cobradoTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cobradoTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel61))))
                .addGap(50, 50, 50)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel63)
                    .addComponent(jLabel62)
                    .addComponent(jLabel55)
                    .addComponent(jLabel49)
                    .addComponent(jLabel54))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(totalTxt)
                    .addComponent(ivat12Txt)
                    .addComponent(subtotalTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)
                    .addComponent(retencionTxt, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(recividoTxt1, javax.swing.GroupLayout.Alignment.TRAILING)))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel13Layout.createSequentialGroup()
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(retencionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel62))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(recividoTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel63)))
                    .addGroup(jPanel13Layout.createSequentialGroup()
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
                            .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(fuenteBaseTxt)
                                .addComponent(jLabel57))
                            .addComponent(cobradoTxt4))
                        .addGap(18, 18, 18)
                        .addComponent(cobrarFacturaCheckBox)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
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
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(telefonoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ciudadTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fechaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 174, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numeroSerieTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout facturaVentaInternalFrameLayout = new javax.swing.GroupLayout(facturaVentaInternalFrame.getContentPane());
        facturaVentaInternalFrame.getContentPane().setLayout(facturaVentaInternalFrameLayout);
        facturaVentaInternalFrameLayout.setHorizontalGroup(
            facturaVentaInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(facturaVentaInternalFrameLayout.createSequentialGroup()
                .addGroup(facturaVentaInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(facturaVentaInternalFrameLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 591, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        facturaVentaInternalFrameLayout.setVerticalGroup(
            facturaVentaInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(facturaVentaInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(13, Short.MAX_VALUE))
        );

        facturaVentaInternalFrame.setBounds(90, 10, 610, 530);
        jLayeredPane2.add(facturaVentaInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        ventasTabla.setModel(new javax.swing.table.DefaultTableModel(
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
        ventasTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        ventasTabla.setMaximumSize(new java.awt.Dimension(310, 64));
        ventasTabla.setMinimumSize(new java.awt.Dimension(310, 64));
        jScrollPane5.setViewportView(ventasTabla);

        jScrollPane5.setBounds(10, 0, 750, 560);
        jLayeredPane2.add(jScrollPane5, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout ventasPanelLayout = new javax.swing.GroupLayout(ventasPanel);
        ventasPanel.setLayout(ventasPanelLayout);
        ventasPanelLayout.setHorizontalGroup(
            ventasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, ventasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(ventasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addComponent(jLayeredPane2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE))
                .addContainerGap())
        );
        ventasPanelLayout.setVerticalGroup(
            ventasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(ventasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLayeredPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Ventas", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-process-users_1.png")), ventasPanel); // NOI18N

        proveedoresPanel.setBackground(new java.awt.Color(255, 255, 255));
        proveedoresPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar8.setFloatable(false);
        jToolBar8.setRollover(true);
        jToolBar8.setBorderPainted(false);
        jToolBar8.add(jSeparator49);

        jLabel37.setText("Buscar proveedor:");
        jToolBar8.add(jLabel37);
        jToolBar8.add(jSeparator50);

        buscarProveedorTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarProveedorTxt.setPreferredSize(new java.awt.Dimension(400, 27));
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
        jToolBar8.add(jSeparator91);

        proveedorInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        proveedorInternalFrame.setClosable(true);
        proveedorInternalFrame.setTitle("Ficha proveedor");
        proveedorInternalFrame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-chair.png"))); // NOI18N
        proveedorInternalFrame.setVisible(true);

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
                        .addGap(41, 41, 41)
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel39)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(empresaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(90, 90, 90)
                        .addComponent(jLabel45)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rucProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jLabel41)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ciudadProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(67, 67, 67)
                        .addComponent(jLabel44)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wwwTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(91, 91, 91)
                        .addComponent(jLabel43)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(55, 55, 55)
                        .addComponent(jLabel40)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(56, 56, 56)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel14Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel38)
                    .addComponent(idProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel39)
                    .addComponent(empresaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel45))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(rucProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel41))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ciudadProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel44))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(wwwTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel43))
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel40))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel42))
                .addGap(24, 24, 24))
        );

        jToolBar12.setFloatable(false);
        jToolBar12.setRollover(true);
        jToolBar12.setBorderPainted(false);
        jToolBar12.add(jSeparator108);

        nuevoProveedorButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoProveedorButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoProveedorButton1.setText("Nuevo");
        nuevoProveedorButton1.setToolTipText("Nuevo cliente");
        nuevoProveedorButton1.setFocusable(false);
        nuevoProveedorButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoProveedorButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoProveedorButton1ActionPerformed(evt);
            }
        });
        jToolBar12.add(nuevoProveedorButton1);
        jToolBar12.add(jSeparator109);

        borrarProveedorButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarProveedorButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarProveedorButton1.setText("Borrar");
        borrarProveedorButton1.setToolTipText("Borrar cliente");
        borrarProveedorButton1.setFocusable(false);
        borrarProveedorButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarProveedorButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarProveedorButton1ActionPerformed(evt);
            }
        });
        jToolBar12.add(borrarProveedorButton1);
        jToolBar12.add(jSeparator110);

        guardarProveedorButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarProveedorButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarProveedorButton1.setText("Guardar");
        guardarProveedorButton1.setToolTipText("Guardar cliente");
        guardarProveedorButton1.setFocusable(false);
        guardarProveedorButton1.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarProveedorButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarProveedorButton1ActionPerformed(evt);
            }
        });
        jToolBar12.add(guardarProveedorButton1);
        jToolBar12.add(jSeparator111);

        crearFacturaClienteButton3.setBackground(new java.awt.Color(255, 255, 51));
        crearFacturaClienteButton3.setFont(new java.awt.Font("Dialog", 1, 10));
        crearFacturaClienteButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        crearFacturaClienteButton3.setText("Factura");
        crearFacturaClienteButton3.setToolTipText("Crear factura");
        crearFacturaClienteButton3.setFocusable(false);
        crearFacturaClienteButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        crearFacturaClienteButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearFacturaClienteButton3ActionPerformed(evt);
            }
        });
        jToolBar12.add(crearFacturaClienteButton3);
        jToolBar12.add(jSeparator112);

        cerrarButton2.setFont(new java.awt.Font("DejaVu Sans", 1, 10));
        cerrarButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarButton2.setText("Cerrar");
        cerrarButton2.setFocusable(false);
        cerrarButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarButton2ActionPerformed(evt);
            }
        });
        jToolBar12.add(cerrarButton2);
        jToolBar12.add(jSeparator7);

        javax.swing.GroupLayout proveedorInternalFrameLayout = new javax.swing.GroupLayout(proveedorInternalFrame.getContentPane());
        proveedorInternalFrame.getContentPane().setLayout(proveedorInternalFrameLayout);
        proveedorInternalFrameLayout.setHorizontalGroup(
            proveedorInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar12, javax.swing.GroupLayout.PREFERRED_SIZE, 408, Short.MAX_VALUE)
            .addGroup(proveedorInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        proveedorInternalFrameLayout.setVerticalGroup(
            proveedorInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proveedorInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar12, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        proveedorInternalFrame.setBounds(210, 10, 420, 490);
        jLayeredPane3.add(proveedorInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

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

        jScrollPane4.setBounds(0, 0, 740, 540);
        jLayeredPane3.add(jScrollPane4, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout proveedoresPanelLayout = new javax.swing.GroupLayout(proveedoresPanel);
        proveedoresPanel.setLayout(proveedoresPanelLayout);
        proveedoresPanelLayout.setHorizontalGroup(
            proveedoresPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proveedoresPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(proveedoresPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar8, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLayeredPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE))
                .addContainerGap())
        );
        proveedoresPanelLayout.setVerticalGroup(
            proveedoresPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(proveedoresPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLayeredPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Proveedores", new javax.swing.ImageIcon(getClass().getResource("/mypack/meeting-chair_1.png")), proveedoresPanel); // NOI18N

        comprasPanel.setBackground(new java.awt.Color(255, 255, 255));
        comprasPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar10.setFloatable(false);
        jToolBar10.setRollover(true);
        jToolBar10.setBorderPainted(false);
        jToolBar10.add(jSeparator57);

        jLabel46.setText("Buscar factura:");
        jToolBar10.add(jLabel46);
        jToolBar10.add(jSeparator58);

        buscarFacturaProveedorTxt.setMinimumSize(new java.awt.Dimension(100, 39));
        buscarFacturaProveedorTxt.setPreferredSize(new java.awt.Dimension(280, 27));
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
        buscarFechaTxt1.setPreferredSize(new java.awt.Dimension(200, 27));
        buscarFechaTxt1.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFechaTxt1CaretUpdate(evt);
            }
        });
        jToolBar10.add(buscarFechaTxt1);
        jToolBar10.add(jSeparator62);

        nuevaFacturaButton2.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevaFacturaButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevaFacturaButton2.setText("Nueva");
        nuevaFacturaButton2.setToolTipText("Nueva factura");
        nuevaFacturaButton2.setFocusable(false);
        nuevaFacturaButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevaFacturaButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevaFacturaButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevaFacturaButton2ActionPerformed(evt);
            }
        });
        jToolBar10.add(nuevaFacturaButton2);

        comprasInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        comprasInternalFrame.setClosable(true);
        comprasInternalFrame.setTitle("Factura de compra");
        comprasInternalFrame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        comprasInternalFrame.setVisible(true);

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

        cerrarButton3.setFont(new java.awt.Font("Dialog", 1, 10));
        cerrarButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarButton3.setText("Cerrar");
        cerrarButton3.setFocusable(false);
        cerrarButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarButton3ActionPerformed(evt);
            }
        });
        jToolBar11.add(cerrarButton3);

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

        articuloComboBox.setBackground(new java.awt.Color(230, 237, 242));
        articuloComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
        articuloComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Bienes y servicios con IVA 12%", "Bienes y servicios con IVA 0%", "Activos fijos con IVA 12%", "Activos fijos con IVA 0%", " " }));

        jLabel51.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel51.setText("R.U.C.:");

        rucFacturaProveedorTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        javax.swing.GroupLayout jPanel25Layout = new javax.swing.GroupLayout(jPanel25);
        jPanel25.setLayout(jPanel25Layout);
        jPanel25Layout.setHorizontalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(jLabel77)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idFacturaProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                            .addComponent(jLabel51)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(jLabel50)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(articuloComboBox, 0, 218, Short.MAX_VALUE))
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addComponent(jLabel47)
                                .addGap(17, 17, 17)
                                .addComponent(direccionProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE))
                            .addComponent(rucFacturaProveedorTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 234, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(24, 24, 24))
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addComponent(jLabel71)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(proveedorFacturaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE)
                        .addGap(22, 22, 22)))
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel48, javax.swing.GroupLayout.DEFAULT_SIZE, 303, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                                    .addComponent(subtotalTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(telefonoProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(jPanel25Layout.createSequentialGroup()
                                    .addComponent(jLabel72, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(fechaCompraTxt))
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                                    .addComponent(jLabel78)
                                    .addGap(18, 18, 18)
                                    .addComponent(serieFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addContainerGap())))
        );
        jPanel25Layout.setVerticalGroup(
            jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel25Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel77)
                    .addComponent(idFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serieFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel78))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel51)
                        .addComponent(rucFacturaProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel72))
                    .addComponent(fechaCompraTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(telefonoProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel25Layout.createSequentialGroup()
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(proveedorFacturaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48)
                            .addComponent(jLabel71))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel25Layout.createSequentialGroup()
                                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel47)
                                    .addComponent(direccionProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel25Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel50)
                                    .addComponent(articuloComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel25Layout.createSequentialGroup()
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
                                    .addComponent(jLabel81))))))
                .addContainerGap())
        );

        pagarFacturaButton.setBackground(new java.awt.Color(102, 255, 0));
        pagarFacturaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        pagarFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/dialog-ok-apply.png"))); // NOI18N
        pagarFacturaButton.setText("Pagar factura");
        pagarFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        pagarFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pagarFacturaButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout comprasInternalFrameLayout = new javax.swing.GroupLayout(comprasInternalFrame.getContentPane());
        comprasInternalFrame.getContentPane().setLayout(comprasInternalFrameLayout);
        comprasInternalFrameLayout.setHorizontalGroup(
            comprasInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar11, javax.swing.GroupLayout.DEFAULT_SIZE, 618, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, comprasInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(comprasInternalFrameLayout.createSequentialGroup()
                .addGap(230, 230, 230)
                .addComponent(pagarFacturaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );
        comprasInternalFrameLayout.setVerticalGroup(
            comprasInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comprasInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar11, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel25, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pagarFacturaButton, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        comprasInternalFrame.setBounds(100, 10, 630, 310);
        jLayeredPane5.add(comprasInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

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

        jScrollPane11.setBounds(0, 0, 740, 480);
        jLayeredPane5.add(jScrollPane11, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout comprasPanelLayout = new javax.swing.GroupLayout(comprasPanel);
        comprasPanel.setLayout(comprasPanelLayout);
        comprasPanelLayout.setHorizontalGroup(
            comprasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comprasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(comprasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToolBar10, javax.swing.GroupLayout.PREFERRED_SIZE, 759, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLayeredPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
        );
        comprasPanelLayout.setVerticalGroup(
            comprasPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(comprasPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar10, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLayeredPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Compras", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-process-own_1.png")), comprasPanel); // NOI18N

        articulosPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.setBorderPainted(false);

        jLabel4.setText("Buscar por:");
        jToolBar5.add(jLabel4);
        jToolBar5.add(jSeparator32);

        buscarArticuloComboBox.setBackground(new java.awt.Color(255, 255, 255));
        buscarArticuloComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "descripcion", "Tipo", "proveedor", "fecha" }));
        buscarArticuloComboBox.setPreferredSize(new java.awt.Dimension(110, 25));
        jToolBar5.add(buscarArticuloComboBox);
        jToolBar5.add(jSeparator55);

        buscarArticuloTxt.setPreferredSize(new java.awt.Dimension(300, 27));
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

        nuevoArticulobButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoArticulobButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoArticulobButton.setText("Nuevo");
        nuevoArticulobButton.setToolTipText("Nuevo cliente");
        nuevoArticulobButton.setFocusable(false);
        nuevoArticulobButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoArticulobButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevoArticulobButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoArticulobButtonActionPerformed(evt);
            }
        });
        jToolBar5.add(nuevoArticulobButton);

        articuloInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        articuloInternalFrame.setClosable(true);
        articuloInternalFrame.setTitle("Ficha del artículo");
        articuloInternalFrame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-barcode.png"))); // NOI18N
        articuloInternalFrame.setVisible(true);

        jToolBar6.setFloatable(false);
        jToolBar6.setRollover(true);

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
        jToolBar6.add(jSeparator38);

        cerrarButton4.setFont(new java.awt.Font("Dialog", 1, 10));
        cerrarButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarButton4.setText("Cerrar");
        cerrarButton4.setFocusable(false);
        cerrarButton4.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarButton4ActionPerformed(evt);
            }
        });
        jToolBar6.add(cerrarButton4);

        jLabel19.setText("ID Articulo:");

        idArticuloTxt.setEditable(false);
        idArticuloTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel36.setText("Fecha Ingreso:");

        FechaIngresoArticuloTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel21.setText("Proveedor:");

        stockTxt.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                stockTxtFocusLost(evt);
            }
        });

        jLabel25.setText("Stock:");

        jLabel35.setText("Imagen:");

        ImagenLabel.setBackground(new java.awt.Color(230, 237, 242));
        ImagenLabel.setToolTipText("Cargar fotos de 96x96 pixeles o 2,5 x 2,5 cm");
        ImagenLabel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        ImagenLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                ImagenLabelMouseClicked(evt);
            }
        });

        jLabel20.setText("Detalle Articulo:");

        detalleArticuloTxt.setColumns(10);
        detalleArticuloTxt.setLineWrap(true);
        detalleArticuloTxt.setRows(5);
        detalleArticuloTxt.setWrapStyleWord(true);
        jScrollPane2.setViewportView(detalleArticuloTxt);

        jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Precios"));

        jLabel26.setText("P. Coste:");

        precioCosteTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel28.setText("Beneficio %:");

        beneficioTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        beneficioTxt.setText("20");

        jLabel29.setText("P.V.P.:");

        precioVentaTxt.setBackground(new java.awt.Color(204, 255, 204));
        precioVentaTxt.setEditable(false);
        precioVentaTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        precioVentaTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel30.setText("Precio IVA:");

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
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precioCosteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel28)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(beneficioTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel29)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precioVentaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel30)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(precioFinalTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)))
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

        jLabel97.setText("Tipo:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(12, 12, 12)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel35)
                            .addComponent(ImagenLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel19)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idArticuloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel36)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(FechaIngresoArticuloTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel21)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(proveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 424, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(calcularArticuloButton))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(stockTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel97)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoArticuloTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 205, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(idArticuloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel36)
                    .addComponent(FechaIngresoArticuloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(proveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stockTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25)
                    .addComponent(jLabel97)
                    .addComponent(tipoArticuloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(jLabel35))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(ImagenLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(calcularArticuloButton, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout articuloInternalFrameLayout = new javax.swing.GroupLayout(articuloInternalFrame.getContentPane());
        articuloInternalFrame.getContentPane().setLayout(articuloInternalFrameLayout);
        articuloInternalFrameLayout.setHorizontalGroup(
            articuloInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar6, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
            .addGroup(articuloInternalFrameLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        articuloInternalFrameLayout.setVerticalGroup(
            articuloInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(articuloInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        articuloInternalFrame.setBounds(140, 22, 540, 480);
        jLayeredPane4.add(articuloInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        articulosTabla.setModel(new javax.swing.table.DefaultTableModel(
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
        articulosTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane3.setViewportView(articulosTabla);

        jScrollPane3.setBounds(0, 0, 760, 540);
        jLayeredPane4.add(jScrollPane3, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout articulosPanelLayout = new javax.swing.GroupLayout(articulosPanel);
        articulosPanel.setLayout(articulosPanelLayout);
        articulosPanelLayout.setHorizontalGroup(
            articulosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, articulosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(articulosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToolBar5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addComponent(jLayeredPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE))
                .addContainerGap())
        );
        articulosPanelLayout.setVerticalGroup(
            articulosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(articulosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(17, 17, 17)
                .addComponent(jLayeredPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 561, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Articulos", new javax.swing.ImageIcon(getClass().getResource("/mypack/utilities-file-archiver.png")), articulosPanel); // NOI18N

        jPanel9.setBackground(new java.awt.Color(255, 255, 255));
        jPanel9.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar14.setFloatable(false);
        jToolBar14.setRollover(true);
        jToolBar14.setBorderPainted(false);
        jToolBar14.add(jSeparator53);

        buscarEquipoComboBox.setBackground(new java.awt.Color(255, 255, 255));
        buscarEquipoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TipoEquipo", "nSerie", "Marca", "Modelo", "Fabricacion", "LugarOrigen" }));
        jToolBar14.add(buscarEquipoComboBox);
        jToolBar14.add(jSeparator29);
        jToolBar14.add(jSeparator30);

        jLabel84.setText("Buscar:");
        jToolBar14.add(jLabel84);
        jToolBar14.add(jSeparator75);

        buscarEquipoTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarEquipoTxt.setMinimumSize(new java.awt.Dimension(60, 60));
        buscarEquipoTxt.setPreferredSize(new java.awt.Dimension(300, 27));
        buscarEquipoTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarEquipoTxtCaretUpdate(evt);
            }
        });
        jToolBar14.add(buscarEquipoTxt);
        jToolBar14.add(jSeparator77);

        clearBusquedaTxt3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        clearBusquedaTxt3.setToolTipText("Limpiar busqueda");
        clearBusquedaTxt3.setFocusable(false);
        clearBusquedaTxt3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearBusquedaTxt3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearBusquedaTxt3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBusquedaTxt3ActionPerformed(evt);
            }
        });
        jToolBar14.add(clearBusquedaTxt3);
        jToolBar14.add(jSeparator95);
        jToolBar14.add(jSeparator100);

        nuevoClienteButton3.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoClienteButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoClienteButton3.setText("Nuevo");
        nuevoClienteButton3.setToolTipText("Nuevo cliente");
        nuevoClienteButton3.setFocusable(false);
        nuevoClienteButton3.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoClienteButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButton3ActionPerformed(evt);
            }
        });
        jToolBar14.add(nuevoClienteButton3);
        jToolBar14.add(jSeparator78);
        jToolBar14.add(jSeparator84);
        jToolBar14.add(jSeparator85);

        equipoInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        equipoInternalFrame.setClosable(true);
        equipoInternalFrame.setTitle("Ficha de equipo");
        equipoInternalFrame.setVisible(true);
        equipoInternalFrame.addInternalFrameListener(new javax.swing.event.InternalFrameListener() {
            public void internalFrameActivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosed(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameClosing(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeactivated(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameDeiconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameIconified(javax.swing.event.InternalFrameEvent evt) {
            }
            public void internalFrameOpened(javax.swing.event.InternalFrameEvent evt) {
                equipoInternalFrameInternalFrameOpened(evt);
            }
        });

        jToolBar16.setFloatable(false);
        jToolBar16.setRollover(true);
        jToolBar16.add(jSeparator89);

        nuevoEquipoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoEquipoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoEquipoButton.setText("Nuevo");
        nuevoEquipoButton.setToolTipText("Nuevo artículo");
        nuevoEquipoButton.setFocusable(false);
        nuevoEquipoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoEquipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoEquipoButtonActionPerformed(evt);
            }
        });
        jToolBar16.add(nuevoEquipoButton);
        jToolBar16.add(jSeparator90);

        borrarEquipoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarEquipoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarEquipoButton.setText("Borrar");
        borrarEquipoButton.setToolTipText("Eliminar artículo");
        borrarEquipoButton.setFocusable(false);
        borrarEquipoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarEquipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarEquipoButtonActionPerformed(evt);
            }
        });
        jToolBar16.add(borrarEquipoButton);
        jToolBar16.add(jSeparator96);

        guardarEquipoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarEquipoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarEquipoButton.setText("Guardar");
        guardarEquipoButton.setToolTipText("Guardar articulo");
        guardarEquipoButton.setFocusable(false);
        guardarEquipoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarEquipoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarEquipoButtonActionPerformed(evt);
            }
        });
        jToolBar16.add(guardarEquipoButton);
        jToolBar16.add(jSeparator97);
        jToolBar16.add(jSeparator102);
        jToolBar16.add(jSeparator103);

        cerrarContratoButton2.setFont(new java.awt.Font("Dialog", 1, 10));
        cerrarContratoButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarContratoButton2.setText("Cerrar");
        cerrarContratoButton2.setFocusable(false);
        cerrarContratoButton2.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarContratoButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarContratoButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarContratoButton2ActionPerformed(evt);
            }
        });
        jToolBar16.add(cerrarContratoButton2);
        jToolBar16.add(jSeparator104);

        jLabel98.setText("Id:");

        jLabel102.setText("Tipo equipo:");

        idEquipoTxt.setEditable(false);

        infoTabbedPane.setTabPlacement(javax.swing.JTabbedPane.LEFT);
        infoTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                infoTabbedPaneStateChanged(evt);
            }
        });

        jPanel15.setBackground(new java.awt.Color(255, 255, 255));

        jLabel104.setText("Modelo:");

        jLabel105.setText("Año fabricación:");

        jLabel106.setText("Lugar origen:");

        jLabel107.setText("Fecha funcionamiento equipo:");

        jLabel108.setText("Marca:");

        jLabel109.setText("Número serie:");

        jLabel110.setText("Característica/opción:");

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel104)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                            .addGap(63, 63, 63)
                            .addComponent(modeloEquipoTxt))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel108)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(marcaEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel109)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel110)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(caracteristicaEquipoTxt))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel107)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(fechaFuncionamientoEquipoTxt))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel106)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(origenEquipoTxt))
                        .addGroup(jPanel15Layout.createSequentialGroup()
                            .addGap(90, 90, 90)
                            .addComponent(numSerieEquipoTxt))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel15Layout.createSequentialGroup()
                            .addComponent(jLabel105)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(añoFabricacionEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(71, 71, 71))
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel108)
                    .addComponent(marcaEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel104)
                    .addComponent(modeloEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel109)
                    .addComponent(numSerieEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel105)
                    .addComponent(añoFabricacionEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel106)
                    .addComponent(origenEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel107)
                    .addComponent(fechaFuncionamientoEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel110)
                    .addComponent(caracteristicaEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
        );

        infoTabbedPane.addTab("Info. equipo", jPanel15);

        jPanel16.setBackground(new java.awt.Color(255, 255, 255));

        jButton13.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton13.setText("Nuevo");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jButton14.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton14.setText("Guardar");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        contratoEquiposComboBox.setBackground(new java.awt.Color(255, 255, 51));
        contratoEquiposComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
        contratoEquiposComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton15.setBackground(new java.awt.Color(255, 255, 51));
        jButton15.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton15.setText("Carga");
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jLabel85.setText("id:");

        jLabel86.setText("Contrato:");

        jLabel88.setText("Fecha instalación:");

        idContratoEquipoTxt.setEditable(false);

        jLabel89.setText("Fecha retiro:");

        jLabel90.setText("Fecha:");

        jButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton1.setText("Borrar");

        jLabel87.setText("Activo equipo:");

        activoEquipoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "SI", "NO" }));

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel85)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idContratoEquipoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel86)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contratoEquipoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 306, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel88)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaInstalacionEquipoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 251, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel89)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fechaRetiroEquipoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jButton13)
                        .addGap(18, 18, 18)
                        .addComponent(jButton14)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel90)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contratoEquiposComboBox, 0, 256, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton15))
                    .addGroup(jPanel16Layout.createSequentialGroup()
                        .addComponent(jLabel87)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(activoEquipoComboBox, 0, 277, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton14)
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton15)
                    .addComponent(contratoEquiposComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel90))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel85)
                    .addComponent(idContratoEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel86)
                    .addComponent(contratoEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel87)
                    .addComponent(activoEquipoComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel88)
                    .addComponent(fechaInstalacionEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel89)
                    .addComponent(fechaRetiroEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(21, Short.MAX_VALUE))
        );

        infoTabbedPane.addTab("Contratos", jPanel16);

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));

        contactoTextArea3.setColumns(20);
        contactoTextArea3.setRows(5);
        jScrollPane14.setViewportView(contactoTextArea3);

        jButton16.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton16.setText("Nuevo");

        jButton17.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton17.setText("Guardar");
        jButton17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton17ActionPerformed(evt);
            }
        });

        jButton2.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton2.setText("Borrar");

        jLabel91.setText("Fecha:");

        contratoEquiposComboBox1.setBackground(new java.awt.Color(255, 255, 51));
        contratoEquiposComboBox1.setFont(new java.awt.Font("Dialog", 1, 10));
        contratoEquiposComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jButton18.setBackground(new java.awt.Color(255, 255, 51));
        jButton18.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton18.setText("Carga");

        idContratoEquipoTxt1.setEditable(false);

        jLabel92.setText("id:");

        jLabel93.setText("Detalle:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane14, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel92)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idContratoEquipoTxt1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                    .addComponent(jLabel93)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jButton16)
                        .addGap(18, 18, 18)
                        .addComponent(jButton17)
                        .addGap(18, 18, 18)
                        .addComponent(jButton2))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel91)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(contratoEquiposComboBox1, 0, 256, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton18)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton16)
                    .addComponent(jButton17)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton18)
                    .addComponent(contratoEquiposComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel91))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel92)
                    .addComponent(idContratoEquipoTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel93)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane14, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );

        infoTabbedPane.addTab("Mantenimientos", jPanel5);

        jPanel10.setBackground(new java.awt.Color(255, 255, 255));

        jButton19.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton19.setText("Nuevo");
        jButton19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton19ActionPerformed(evt);
            }
        });

        jButton20.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton20.setText("Guardar");
        jButton20.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton20ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton3.setText("Borrar");

        fallasEquiposComboBox.setBackground(new java.awt.Color(255, 255, 51));
        fallasEquiposComboBox.setFont(new java.awt.Font("Dialog", 1, 10));
        fallasEquiposComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel94.setText("Fecha:");

        jLabel95.setText("id:");

        idContratoEquipoFalloTxt.setEditable(false);

        jLabel96.setText("Detalle:");

        detalleTxt.setColumns(20);
        detalleTxt.setRows(5);
        jScrollPane15.setViewportView(detalleTxt);

        jButton21.setBackground(new java.awt.Color(255, 255, 51));
        jButton21.setFont(new java.awt.Font("Dialog", 1, 10));
        jButton21.setText("Carga");
        jButton21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton21ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane15, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel95)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idContratoEquipoFalloTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))
                    .addComponent(jLabel96)
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jButton19)
                        .addGap(18, 18, 18)
                        .addComponent(jButton20)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3))
                    .addGroup(jPanel10Layout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fallasEquiposComboBox, 0, 256, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton21)))
                .addContainerGap())
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton19)
                    .addComponent(jButton20)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton21)
                    .addComponent(fallasEquiposComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel94))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel95)
                    .addComponent(idContratoEquipoFalloTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel96)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane15, javax.swing.GroupLayout.DEFAULT_SIZE, 154, Short.MAX_VALUE)
                .addContainerGap())
        );

        infoTabbedPane.addTab("Fallos", jPanel10);

        javax.swing.GroupLayout equipoInternalFrameLayout = new javax.swing.GroupLayout(equipoInternalFrame.getContentPane());
        equipoInternalFrame.getContentPane().setLayout(equipoInternalFrameLayout);
        equipoInternalFrameLayout.setHorizontalGroup(
            equipoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(equipoInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel98)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(idEquipoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 495, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, equipoInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel102)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipoEquipoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(jToolBar16, javax.swing.GroupLayout.DEFAULT_SIZE, 528, Short.MAX_VALUE)
            .addGroup(equipoInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(infoTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );
        equipoInternalFrameLayout.setVerticalGroup(
            equipoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(equipoInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar16, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(equipoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel98)
                    .addComponent(idEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(equipoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel102)
                    .addComponent(tipoEquipoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(infoTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        equipoInternalFrame.setBounds(100, 10, 540, 450);
        jLayeredPane7.add(equipoInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        listaEquiposTabla.setModel(new javax.swing.table.DefaultTableModel(
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
        listaEquiposTabla.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane13.setViewportView(listaEquiposTabla);

        jScrollPane13.setBounds(0, 0, 770, 480);
        jLayeredPane7.add(jScrollPane13, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLayeredPane7, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addComponent(jToolBar14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar14, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 573, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Equipos", new javax.swing.ImageIcon(getClass().getResource("/mypack/hwinfo.png")), jPanel9); // NOI18N

        contratosPanel.setBackground(new java.awt.Color(255, 255, 255));
        contratosPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));

        jToolBar7.setFloatable(false);
        jToolBar7.setRollover(true);
        jToolBar7.setBorderPainted(false);
        jToolBar7.add(jSeparator34);

        buscarContratoComboBox.setBackground(new java.awt.Color(255, 255, 255));
        buscarContratoComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Empresa", "nContrato", "TipoContrato" }));
        jToolBar7.add(buscarContratoComboBox);
        jToolBar7.add(jSeparator9);
        jToolBar7.add(jSeparator10);

        jLabel75.setText("Buscar:");
        jToolBar7.add(jLabel75);
        jToolBar7.add(jSeparator70);

        buscarContratoTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarContratoTxt.setMinimumSize(new java.awt.Dimension(60, 60));
        buscarContratoTxt.setPreferredSize(new java.awt.Dimension(300, 27));
        buscarContratoTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarContratoTxtCaretUpdate(evt);
            }
        });
        jToolBar7.add(buscarContratoTxt);
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
        jToolBar7.add(jSeparator73);
        jToolBar7.add(jSeparator74);

        contratoInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        contratoInternalFrame.setClosable(true);
        contratoInternalFrame.setTitle("Informacion del contrato");
        contratoInternalFrame.setFrameIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-time-schedule-edit.png"))); // NOI18N
        contratoInternalFrame.setVisible(true);

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jLabel76.setText("ID:");

        idContrato.setEditable(false);

        jLabel1.setText("Institución:");

        jLabel67.setText("Tipo contrato:");

        jLabel2.setText("Ciudad:");

        jLabel53.setText("Dirección:");

        jLabel64.setText("Teléfono:");

        jLabel66.setText("Fecha firma del contrato:");

        firmaContratoTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel70.setText("Duración contrato:");

        duracionContratoTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel69.setText("Precio unitario:");

        jLabel68.setText("Numero equipos:");

        nequiposTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        pequipos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel74.setText("Cantidad insumos:");

        ninsumos.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        pinsumos.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel73.setText("Fecha envio insumos/mes:");

        jLabel82.setText("# contrato:");

        jLabel83.setText("Cancelación contrato:");

        cancelacionContratoTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel76)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idContrato, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel82)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(numContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 138, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(empresaContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel67)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tipoContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ciudadContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel53)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(direccionContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel64)
                        .addGap(15, 15, 15)
                        .addComponent(telefonoContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel66)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(firmaContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 253, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel70)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(duracionContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel73)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(envioContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel74)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ninsumos))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel68)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(nequiposTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(27, 27, 27)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pequipos, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(pinsumos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 161, Short.MAX_VALUE)
                            .addComponent(jLabel69, javax.swing.GroupLayout.Alignment.LEADING)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel83)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelacionContratoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel76)
                    .addComponent(idContrato, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel82)
                    .addComponent(numContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(empresaContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel67)
                    .addComponent(tipoContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(ciudadContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel53)
                    .addComponent(direccionContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel64)
                    .addComponent(telefonoContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel66)
                    .addComponent(firmaContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel70)
                    .addComponent(duracionContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel83)
                    .addComponent(cancelacionContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel69)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel68)
                    .addComponent(nequiposTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pequipos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel74)
                    .addComponent(ninsumos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pinsumos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel73)
                    .addComponent(envioContratoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jToolBar13.setFloatable(false);
        jToolBar13.setRollover(true);
        jToolBar13.add(jSeparator43);

        nuevoContratoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoContratoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoContratoButton.setText("Nuevo");
        nuevoContratoButton.setToolTipText("Nuevo artículo");
        nuevoContratoButton.setFocusable(false);
        nuevoContratoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoContratoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoContratoButtonActionPerformed(evt);
            }
        });
        jToolBar13.add(nuevoContratoButton);
        jToolBar13.add(jSeparator44);

        borrarContratoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarContratoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarContratoButton.setText("Borrar");
        borrarContratoButton.setToolTipText("Eliminar artículo");
        borrarContratoButton.setFocusable(false);
        borrarContratoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarContratoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarContratoButtonActionPerformed(evt);
            }
        });
        jToolBar13.add(borrarContratoButton);
        jToolBar13.add(jSeparator46);

        guardarContratoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarContratoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarContratoButton.setText("Guardar");
        guardarContratoButton.setToolTipText("Guardar articulo");
        guardarContratoButton.setFocusable(false);
        guardarContratoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarContratoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarContratoButtonActionPerformed(evt);
            }
        });
        jToolBar13.add(guardarContratoButton);
        jToolBar13.add(jSeparator47);
        jToolBar13.add(jSeparator52);
        jToolBar13.add(jSeparator93);

        equipoAsociarButton.setBackground(new java.awt.Color(255, 255, 51));
        equipoAsociarButton.setFont(new java.awt.Font("Dialog", 1, 10));
        equipoAsociarButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/hwinfo.png"))); // NOI18N
        equipoAsociarButton.setText("Equipo");
        equipoAsociarButton.setToolTipText("Asociar equipo al contrato");
        equipoAsociarButton.setFocusable(false);
        equipoAsociarButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        equipoAsociarButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        equipoAsociarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                equipoAsociarButtonActionPerformed(evt);
            }
        });
        jToolBar13.add(equipoAsociarButton);
        jToolBar13.add(jSeparator88);

        cerrarContratoButton.setFont(new java.awt.Font("Dialog", 1, 10));
        cerrarContratoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarContratoButton.setText("Cerrar");
        cerrarContratoButton.setFocusable(false);
        cerrarContratoButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarContratoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarContratoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarContratoButtonActionPerformed(evt);
            }
        });
        jToolBar13.add(cerrarContratoButton);

        javax.swing.GroupLayout contratoInternalFrameLayout = new javax.swing.GroupLayout(contratoInternalFrame.getContentPane());
        contratoInternalFrame.getContentPane().setLayout(contratoInternalFrameLayout);
        contratoInternalFrameLayout.setHorizontalGroup(
            contratoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contratoInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(contratoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(contratoInternalFrameLayout.createSequentialGroup()
                    .addComponent(jToolBar13, javax.swing.GroupLayout.PREFERRED_SIZE, 428, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        contratoInternalFrameLayout.setVerticalGroup(
            contratoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, contratoInternalFrameLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(contratoInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(contratoInternalFrameLayout.createSequentialGroup()
                    .addComponent(jToolBar13, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(460, Short.MAX_VALUE)))
        );

        contratoInternalFrame.setBounds(140, 10, 440, 480);
        jLayeredPane6.add(contratoInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

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

        jScrollPane8.setBounds(0, 0, 750, 480);
        jLayeredPane6.add(jScrollPane8, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jSeparator31.setBounds(30, 90, 0, 2);
        jLayeredPane6.add(jSeparator31, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout contratosPanelLayout = new javax.swing.GroupLayout(contratosPanel);
        contratosPanel.setLayout(contratosPanelLayout);
        contratosPanelLayout.setHorizontalGroup(
            contratosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contratosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(contratosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
                    .addComponent(jToolBar7, javax.swing.GroupLayout.PREFERRED_SIZE, 745, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        contratosPanelLayout.setVerticalGroup(
            contratosPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contratosPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar7, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLayeredPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Contratos", new javax.swing.ImageIcon(getClass().getResource("/mypack/story-editor.png")), contratosPanel); // NOI18N

        tesoreriaPanel.setBackground(new java.awt.Color(255, 255, 255));

        jToolBar15.setFloatable(false);
        jToolBar15.setRollover(true);
        jToolBar15.setBorderPainted(false);
        jToolBar15.setPreferredSize(new java.awt.Dimension(616, 32));

        jLabel6.setText("Buscar por:");
        jToolBar15.add(jLabel6);
        jToolBar15.add(jSeparator69);

        buscarTesoreriaComboBox.setBackground(new java.awt.Color(255, 255, 255));
        buscarTesoreriaComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ClienteProveedor", "Fecha", "Concepto" }));
        buscarTesoreriaComboBox.setPreferredSize(new java.awt.Dimension(200, 25));
        jToolBar15.add(buscarTesoreriaComboBox);
        jToolBar15.add(jSeparator87);

        buscarTesoreriaTxt.setMinimumSize(new java.awt.Dimension(250, 19));
        buscarTesoreriaTxt.setPreferredSize(new java.awt.Dimension(300, 27));
        buscarTesoreriaTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarTesoreriaTxtCaretUpdate(evt);
            }
        });
        jToolBar15.add(buscarTesoreriaTxt);
        jToolBar15.add(jSeparator105);

        clearBusquedaTxt4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        clearBusquedaTxt4.setToolTipText("Limpiar busqueda");
        clearBusquedaTxt4.setFocusable(false);
        clearBusquedaTxt4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        clearBusquedaTxt4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        clearBusquedaTxt4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearBusquedaTxt4ActionPerformed(evt);
            }
        });
        jToolBar15.add(clearBusquedaTxt4);
        jToolBar15.add(jSeparator107);

        nuevTesoreriaButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevTesoreriaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevTesoreriaButton.setText("Nuevo");
        nuevTesoreriaButton.setToolTipText("Nuevo cliente");
        nuevTesoreriaButton.setFocusable(false);
        nuevTesoreriaButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevTesoreriaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevTesoreriaButtonActionPerformed(evt);
            }
        });
        jToolBar15.add(nuevTesoreriaButton);

        apunteContableInternalFrame.setBackground(new java.awt.Color(255, 255, 255));
        apunteContableInternalFrame.setClosable(true);
        apunteContableInternalFrame.setTitle("Apunte contable");
        apunteContableInternalFrame.setVisible(true);

        jToolBar17.setFloatable(false);
        jToolBar17.setRollover(true);
        jToolBar17.setPreferredSize(new java.awt.Dimension(616, 32));

        nuevoApunteContableButton.setFont(new java.awt.Font("Dialog", 1, 10));
        nuevoApunteContableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoApunteContableButton.setText("Nuevo");
        nuevoApunteContableButton.setToolTipText("Nuevo cliente");
        nuevoApunteContableButton.setFocusable(false);
        nuevoApunteContableButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        nuevoApunteContableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoApunteContableButtonActionPerformed(evt);
            }
        });
        jToolBar17.add(nuevoApunteContableButton);
        jToolBar17.add(jSeparator115);

        borrarApunteContableButton.setFont(new java.awt.Font("Dialog", 1, 10));
        borrarApunteContableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarApunteContableButton.setText("Borrar");
        borrarApunteContableButton.setToolTipText("Borrar cliente");
        borrarApunteContableButton.setFocusable(false);
        borrarApunteContableButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        borrarApunteContableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarApunteContableButtonActionPerformed(evt);
            }
        });
        jToolBar17.add(borrarApunteContableButton);
        jToolBar17.add(jSeparator116);

        guardarApunteContableButton.setFont(new java.awt.Font("Dialog", 1, 10));
        guardarApunteContableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarApunteContableButton.setText("Guardar");
        guardarApunteContableButton.setToolTipText("Guardar cliente");
        guardarApunteContableButton.setFocusable(false);
        guardarApunteContableButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        guardarApunteContableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarApunteContableButtonActionPerformed(evt);
            }
        });
        jToolBar17.add(guardarApunteContableButton);
        jToolBar17.add(jSeparator117);

        cerrarApunteContableButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/application-exit.png"))); // NOI18N
        cerrarApunteContableButton.setText("Cerrar");
        cerrarApunteContableButton.setFocusable(false);
        cerrarApunteContableButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cerrarApunteContableButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cerrarApunteContableButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cerrarApunteContableButtonActionPerformed(evt);
            }
        });
        jToolBar17.add(cerrarApunteContableButton);

        idTesoreriaTxt.setEditable(false);

        jLabel7.setText("ID:");
        jLabel7.setEnabled(false);

        jLabel65.setText("Fecha:");

        jLabel99.setText("Cliente/Proveedor:");

        jLabel100.setText("Concepto:");

        ingresoTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ingresoTxt.setText("0.00");

        jLabel101.setText("Ingreso:");

        jLabel103.setText("Pago:");

        pagoTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        pagoTxt.setText("0.00");

        memoTxt.setColumns(20);
        memoTxt.setLineWrap(true);
        memoTxt.setRows(5);
        memoTxt.setWrapStyleWord(true);
        jScrollPane12.setViewportView(memoTxt);

        jLabel111.setText("Memo:");

        conceptoApunteContableComboBox.setBackground(new java.awt.Color(255, 255, 255));
        conceptoApunteContableComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Pago sueldo", "Pago envio courier/carga", "Pago carro gasolina", "Pago carro reparacion/mantenimiento", "Pago carro parqueadero", "Pago carro cuota", "Pago carro seguro", "Pago insumos/suministros medicos", "Pago repuestos", "Pago equipo medico", "Pago herrmientas trabajo", "Pago material oficina", "Pago publicidad", "Pago impuestos SRI", "Pago servicio bancario", "Pago seguro social", "Pago impuesto municipal", "Pago viaje+viaticos", "Pago transporte terrestre", "Pago transporte aereo", "Pago otros", "Pago servicio telefonico", "Pago servicio agua", "Pago servicio Internet", "Pago servicio telefonia", " ", "Ingreso venta insumos/suministros", "Ingreso venta repuestos", "Ingreso asesoriadiagnostico", "Ingreso venta equipo medico", "Ingreso venta herramienta", "Ingreso mantenimiento equipo", "Ingreso interes bancario", "Ingreso tributario", "Ingreso otros", " " }));

        javax.swing.GroupLayout apunteContableInternalFrameLayout = new javax.swing.GroupLayout(apunteContableInternalFrame.getContentPane());
        apunteContableInternalFrame.getContentPane().setLayout(apunteContableInternalFrameLayout);
        apunteContableInternalFrameLayout.setHorizontalGroup(
            apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                        .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                                .addComponent(jLabel100)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(conceptoApunteContableComboBox, 0, 270, Short.MAX_VALUE))
                            .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                                .addComponent(jLabel99)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clienteProveedorTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                            .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idTesoreriaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 318, Short.MAX_VALUE))
                            .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                                .addComponent(jLabel65)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fechaTesoreriaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 292, Short.MAX_VALUE))
                            .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                                .addComponent(jLabel111)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane12, javax.swing.GroupLayout.DEFAULT_SIZE, 293, Short.MAX_VALUE)))
                        .addGap(12, 12, 12))
                    .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                        .addComponent(jLabel101)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ingresoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(apunteContableInternalFrameLayout.createSequentialGroup()
                        .addComponent(jLabel103)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(pagoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(jToolBar17, javax.swing.GroupLayout.PREFERRED_SIZE, 358, Short.MAX_VALUE)
        );
        apunteContableInternalFrameLayout.setVerticalGroup(
            apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, apunteContableInternalFrameLayout.createSequentialGroup()
                .addComponent(jToolBar17, javax.swing.GroupLayout.PREFERRED_SIZE, 26, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(idTesoreriaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel65)
                    .addComponent(fechaTesoreriaTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel99)
                    .addComponent(clienteProveedorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(conceptoApunteContableComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel100))
                .addGap(18, 18, 18)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel111))
                .addGap(18, 18, 18)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel103)
                    .addComponent(pagoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(apunteContableInternalFrameLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel101)
                    .addComponent(ingresoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68))
        );

        apunteContableInternalFrame.setBounds(170, 110, 370, 370);
        jLayeredPane8.add(apunteContableInternalFrame, javax.swing.JLayeredPane.DEFAULT_LAYER);

        tesoreriaTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tesoreriaTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_OFF);
        jScrollPane10.setViewportView(tesoreriaTable);

        jScrollPane10.setBounds(0, 10, 760, 560);
        jLayeredPane8.add(jScrollPane10, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout tesoreriaPanelLayout = new javax.swing.GroupLayout(tesoreriaPanel);
        tesoreriaPanel.setLayout(tesoreriaPanelLayout);
        tesoreriaPanelLayout.setHorizontalGroup(
            tesoreriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tesoreriaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(tesoreriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLayeredPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 770, Short.MAX_VALUE)
                    .addGroup(tesoreriaPanelLayout.createSequentialGroup()
                        .addComponent(jToolBar15, javax.swing.GroupLayout.DEFAULT_SIZE, 756, Short.MAX_VALUE)
                        .addGap(14, 14, 14))))
        );
        tesoreriaPanelLayout.setVerticalGroup(
            tesoreriaPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tesoreriaPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToolBar15, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 575, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("Tesoreria", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-bank.png")), tesoreriaPanel); // NOI18N

        jMenu1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/server-database.png"))); // NOI18N
        jMenu1.setText("Base Datos");
        jMenu1.setFont(new java.awt.Font("Dialog", 0, 12));

        jMenuItem1.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        jMenuItem1.setText("Backup");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-export-table.png"))); // NOI18N
        jMenuItem2.setText("Restore");
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
        jMenuItem5.setText("Ventas a clientes");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuItem11.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem11.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/kchart.png"))); // NOI18N
        jMenuItem11.setText("Ingresos y Gastos");
        jMenuItem11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuItem9.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem9.setText("Compras a prooveedores");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);
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
        jMenu2.add(jSeparator54);

        jMenuItem6.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/hwinfo.png"))); // NOI18N
        jMenuItem6.setText("Equipos");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem6);

        jMenuItem8.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-time-schedule-edit.png"))); // NOI18N
        jMenuItem8.setText("Contratos + insumos");
        jMenuItem8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem7.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-time-schedule-edit.png"))); // NOI18N
        jMenuItem7.setText("Contratos + equipos");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem7);

        jMenuItem10.setFont(new java.awt.Font("Dialog", 0, 12));
        jMenuItem10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/run-build-configure.png"))); // NOI18N
        jMenuItem10.setText("Fallos en equipos");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 920, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 639, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /*
     * Método checkDriver()
     * chequea si esta disponible driver en el sistema
     * retorna true si encuentra driver
     * retorna false si no esta disponible el driver
     */
    private void checkDriver(){
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
        clientesTabla.removeAll();
        clientesTabla.updateUI();
        String[] columnNames = {"Código",
                                "Razon social",
                                "Cliente",
                                "Ciudad",
                                "R.U.C."};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        conectarBD(baseDatos);
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM Clientes WHERE " + findClave + " LIKE '%" + (String)find+"%' ORDER BY Cliente ASC");
            while(rs.next()){
                Object[] row = new Object[5];
                row[0] = rs.getObject("id");
                row[1] = rs.getObject("Cliente");
                row[2] = rs.getObject("Organizacion");
                row[3] = rs.getObject("Ciudad");
                row[4] = rs.getObject("RUC");
               
                               
                model.addRow(row);
            }
            clientesTabla.setModel(model);
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            //custom title, warning icon
            /*
                JOptionPane.showMessageDialog(new JFrame(),
                        "Error de lectura en lista de clientes",
                        "Error", JOptionPane.ERROR_MESSAGE);
             * 
             */
        }
        
    }


    /*
     * Lee clientes de la base de datos y los ennumera en lista
     */
    public void leerTesoreria(String findClave, String find){
        String cant;
        Double val1, val2, res;
        DecimalFormat nf = new DecimalFormat("####.##");
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);

        tesoreriaTable.removeAll();
        tesoreriaTable.updateUI();
        String[] columnNames = {"id",
                                "Fecha",
                                "Cliente/Proveedor",
                                "Gasto",
                                "Ingreso",
                                "Codigo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        conectarBD(baseDatos);
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM Tesoreria WHERE " + findClave + " LIKE '%" + (String)find+"%' ORDER BY Fecha ASC, ClienteProveedor ASC");
            while(rs.next()){
                Object[] row = new Object[6];
                row[0] = rs.getObject("id");
                row[1] = rs.getObject("Fecha");
                row[2] = rs.getObject("ClienteProveedor");
                row[3] = rs.getObject("Pagos");
                row[4] = rs.getObject("Ingresos");
                row[5] = rs.getObject("Concepto");

                model.addRow(row);
            }
            tesoreriaTable.setModel(model);
            rs.close();
            statement.close();
            connection.close();
        } catch (Exception e) {
            //custom title, warning icon
            /*
                JOptionPane.showMessageDialog(new JFrame(),
                        "Error de lectura en lista de clientes",
                        "Error", JOptionPane.ERROR_MESSAGE);
             *
             */
        }

    }

    /*
     * Metodo connectToDB()
     * conecta a base de datos
     */
    private void conectarBD(String DBName){
        try {
            connection = DriverManager.getConnection(DBName);
            //System.out.println("DataBase connected");
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    
    private void clearBusquedaTxtActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxtActionPerformed
        // TODO add your handling code here:
        buscarClienteTxt.setText("");
    }//GEN-LAST:event_clearBusquedaTxtActionPerformed

    
    
    private void nuevoClienteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        //Chequea si no esta cargada la ventana
        if(!clientesInternalFrame.isShowing()){
            clientesInternalFrame.setVisible(true);
        }else{
            this.clientesInternalFrame.moveToFront();
        }
        limpiaFichaCliente();
        fechaClienteTxt.setText(hoy);
    }//GEN-LAST:event_nuevoClienteButtonActionPerformed

    private void ivabaseTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ivabaseTxtCaretUpdate
        // TODO add your handling code here
    }//GEN-LAST:event_ivabaseTxtCaretUpdate

    private void buscarFacturaClienteTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarFacturaClienteTxtCaretUpdate
        // TODO add your handling code here
        String buscar = buscarFacturaClienteTxt.getText();      
        String buscarClave = (String)buscarFacturaClienteComboBox.getSelectedItem();
        String find1;
        if(fechaBuscarRadioButton.isSelected()){
            
            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
        clientesInternalFrame.dispose();
        buscar = buscarFacturaClienteTxt.getText();
        DefaultTableModel kde = bdv.leer(buscarClave, buscar, find1, "LIKE");
        leerVentas2Tabla(kde);
        actualizaVentasTabla();
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
        for (int i = 0, rows = detallesVentaTabla.getRowCount(); i < rows; i++)
        {
                cant = (String) detallesVentaTabla.getValueAt(i, 0).toString();
                cantidad = Integer.parseInt(cant);
                pUnit = (String) detallesVentaTabla.getValueAt(i, 2).toString();
                pUnitario = Double.valueOf(pUnit);
                res = cantidad * pUnitario;
                detallesVentaTabla.setValueAt(nf.format(res), i, 3);
        }
        Double subtotal;
        res = 0.00;
         for (int i = 0, rows = detallesVentaTabla.getRowCount(); i < rows; i++)
        {
                //Calculo de subtotal
                pUnit = (String) detallesVentaTabla.getValueAt(i, 3).toString();
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
        String buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
        String buscar = buscarArticuloTxt.getText();
        
        DefaultTableModel kde = bda.leer(buscarClave, buscar, "LIKE");
        leerArticulos2Tabla(kde);
        actualizaArticulosTabla();
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
        JFileChooser fc = new JFileChooser();
        int result = fc.showOpenDialog(this);
        if(result == JFileChooser.APPROVE_OPTION){
            //Open file
            File file = fc.getSelectedFile();
            filename = file.toString();
            try {
                image = ImageIO.read(file);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,e.getMessage(),
                "File error",JOptionPane.ERROR_MESSAGE);
            }
            ImagenLabel.setIcon(new ImageIcon(image));
        }
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
        filename = System.getProperty("user.home") + "/jContab/logo.jpg";
        //Chequea si no esta cargada la ventana
        if(!articuloInternalFrame.isShowing()){
            articuloInternalFrame.setVisible(true);
        }else{
            this.articuloInternalFrame.moveToFront();
        }
        
        limpiaFichaArticulos();
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

        //Chequea si no esta cargada la ventana
        if(!facturaVentaInternalFrame.isShowing()){
            facturaVentaInternalFrame.setVisible(true);
        }else{
            this.facturaVentaInternalFrame.moveToFront();
        }

        int cont = 0;
        if(detallesVentaTabla.getRowCount() != 0){
            do{
                model1.removeRow(detallesVentaTabla.getRowCount()-1);
                detallesVentaTabla.removeAll();
                detallesVentaTabla.updateUI();
                detallesVentaTabla.setModel(model1);
                actualizaTablaFacturaClienteAnchos();
                cont = model1.getRowCount();
            }while(cont != 0);
            detallesVentaTabla.removeAll();
            detallesVentaTabla.updateUI();  
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
                //numeroSerieTxt.setText(numeroSerieTxt.getText()+(String)res.toString());
                numeroSerieTxt.setText((String)res.toString());
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
 
        detallesVentaTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        detallesVentaTabla.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        detallesVentaTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        detallesVentaTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        detallesVentaTabla.setModel(model1);
        actualizaDetallesVentaTabla();
        jTabbedPane1.setSelectedIndex(1);
        articuloInternalFrame.dispose();
    }//GEN-LAST:event_agregarArticuloAFacturaButtonActionPerformed

    private void eliminarArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eliminarArticuloButtonActionPerformed
        // TODO add your handling code here:
        int cont = model1.getRowCount();
        if(cont != 0){
            System.out.println("Eliminar articulo");
            model1.removeRow(detallesVentaTabla.getRowCount()-1);
            detallesVentaTabla.removeAll();
            detallesVentaTabla.updateUI();
            detallesVentaTabla.setModel(model1);
            actualizaDetallesVentaTabla();
        }
    }//GEN-LAST:event_eliminarArticuloButtonActionPerformed

    private void guardarFacturaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarFacturaButtonActionPerformed
        // TODO add your handling code here:
        String cobrar;
        if(cobrarFacturaCheckBox.isSelected() == true){
            cobrar = "true";
        }else{
            cobrar = "false";
        }
        Venta venta = new Venta(
                idFacturaTxt.getText(),
                clienteFacturaTxt.getText(), 
                rucTxt.getText(), 
                cobrar, 
                subtotalTxt.getText(), 
                ivat12Txt.getText(), 
                totalTxt.getText(),
                recividoTxt1.getText(), 
                null, 
                fechaTxt.getText(), 
                numeroSerieTxt.getText(), 
                direccionTxt.getText(), 
                telefonoTxt.getText(), 
                ciudadTxt.getText(), 
                retencionTxt.getText());
        
        
        
        if(flagSaveFichaFacturaCliente == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaTxt.getText().isEmpty()) && !numeroSerieTxt.getText().isEmpty()){
                bdv.escribir(venta); //guarda ficha cliente en base datos
                
                //GUARDA TABLA DE ARTICULOS UNO POR UNO NUMERO SERIE CLAVE EN BUSQUEDA
                int rows = detallesVentaTabla.getRowCount();
                for (int i = 0; i < rows; i++){
                    DetallesVenta detallesVenta = new DetallesVenta(
                        null,
                        rucTxt.getText(), 
                        fechaTxt.getText(), 
                        detallesVentaTabla.getValueAt(i, 0).toString(),
                        detallesVentaTabla.getValueAt(i, 1).toString(), 
                        detallesVentaTabla.getValueAt(i, 2).toString(), 
                        detallesVentaTabla.getValueAt(i, 3).toString(),
                        numeroSerieTxt.getText());
                        
                    bddv.escribir(detallesVenta);
                }
                
                String buscar;
                String buscarClave = (String)buscarFacturaClienteComboBox.getSelectedItem();     
                String find1;
                if(fechaBuscarRadioButton.isSelected()){

                    find1 = buscarFechaTxt.getText();
                }else{
                    find1 = "";
                }
                facturaVentaInternalFrame.dispose();
                buscar = buscarFacturaClienteTxt.getText();
                DefaultTableModel kde = bdv.leer(buscarClave, buscar, find1, "LIKE");
                leerVentas2Tabla(kde);
                actualizaVentasTabla();
                
                
            }else{
                //custom title, warning icon
                JOptionPane.showMessageDialog(null, " Imposible agregar este registro!! ",
                        "Aviso!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaTxt.getText().isEmpty()) && !numeroSerieTxt.getText().isEmpty()){
                bdv.actualizar(venta); 
                
                //GUARDA TABLA DE ARTICULOS UNO POR UNO NUMERO SERIE CLAVE EN BUSQUEDA
                int rows = detallesVentaTabla.getRowCount();
                for (int i = 0; i < rows; i++){
                    DetallesVenta detallesVenta = new DetallesVenta(
                        detallesVentaTabla.getValueAt(i, 4).toString(),
                        rucTxt.getText(), 
                        fechaTxt.getText(), 
                        detallesVentaTabla.getValueAt(i, 0).toString(),
                        detallesVentaTabla.getValueAt(i, 1).toString(), 
                        detallesVentaTabla.getValueAt(i, 2).toString(), 
                        detallesVentaTabla.getValueAt(i, 3).toString(),
                        numeroSerieTxt.getText());
                        
                    bddv.actualizar(detallesVenta);
                }
                String buscar;
                String buscarClave = (String)buscarFacturaClienteComboBox.getSelectedItem();     
                String find1;
                if(fechaBuscarRadioButton.isSelected()){

                    find1 = buscarFechaTxt.getText();
                }else{
                    find1 = "";
                }
                facturaVentaInternalFrame.dispose();
                buscar = buscarFacturaClienteTxt.getText();
                DefaultTableModel kde = bdv.leer(buscarClave, buscar, find1, "LIKE");
                leerVentas2Tabla(kde);
                actualizaVentasTabla();
            }else{
                //custom title, warning icon
                JOptionPane.showMessageDialog(null, " Imposible actualizar este registro!! ",
                        "Aviso!", JOptionPane.ERROR_MESSAGE);
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
                bdv.borrar(idFacturaTxt.getText());
                bddv.borrar(numeroSerieTxt.getText());
                
                String buscar = buscarFacturaClienteTxt.getText();
                String buscarClave = (String)buscarFacturaClienteComboBox.getSelectedItem();
                String find1;
                if(fechaBuscarRadioButton.isSelected()){

                    find1 = buscarFechaTxt.getText();
                }else{
                    find1 = "";
                }
                facturaVentaInternalFrame.dispose();

                DefaultTableModel kde = bdv.leer(buscarClave, buscar, find1, "LIKE");
                leerVentas2Tabla(kde);
                actualizaVentasTabla();
            }
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(null, " No se puede eliminar este registro!! ",
                    "Aviso!", JOptionPane.ERROR_MESSAGE);
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
                bda.borrar(idArticuloTxt.getText());
                articuloInternalFrame.dispose();
                
                String buscar = buscarArticuloTxt.getText();
                String buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
                DefaultTableModel kde = bda.leer(buscarClave, buscar,"LIKE");
                leerArticulos2Tabla(kde);
                actualizaArticulosTabla();
            }
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(null, " No se puede eliminar este registro!! ",
                    "Aviso!", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarFichaArticuloButtonActionPerformed

    private void guardarArticuloButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarArticuloButtonActionPerformed
        // TODO add your handling code here:
        Articulo articulo = new Articulo(
                idArticuloTxt.getText(), 
                detalleArticuloTxt.getText(), 
                stockTxt.getText(),
                precioVentaTxt.getText(),
                null, 
                precioFinalTxt.getText(), 
                tipoArticuloTxt.getText(), 
                proveedorTxt.getText(), 
                precioCosteTxt.getText(), 
                beneficioTxt.getText(),
                null, 
                FechaIngresoArticuloTxt.getText());
            
            
        if(flagSaveFichaArticulo == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!FechaIngresoArticuloTxt.getText().isEmpty()) && !detalleArticuloTxt.getText().isEmpty()){
                bda.escribir(articulo, filename); //guarda ficha cliente en base datos
                
                String buscar = buscarArticuloTxt.getText();
                String buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
                DefaultTableModel kde = bda.leer(buscarClave, buscar, "LIKE");
                leerArticulos2Tabla(kde);
                actualizaArticulosTabla();
                seleccionArticulosTabla();
                actualizaDetallesVentaTabla();
            }else{
                //custom title, warning icon
                JOptionPane.showMessageDialog(null, " Imposible agregar este registro!! ",
                        "Aviso!", JOptionPane.ERROR_MESSAGE);
            }   
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!idArticuloTxt.getText().isEmpty()) && !detalleArticuloTxt.getText().isEmpty()){
                actualizarFichaArticulo();  
                String buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
                String buscar = buscarArticuloTxt.getText();
                //leerClienteToDB(buscarClave, buscar);
                leerArticulos(buscar, buscarClave);
                actualizaListaArticulosTablaAnchos();
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
            for (int i = 0, rows = detallesVentaTabla.getRowCount(); i < rows; i++)
            {
                    cantidad = (String) detallesVentaTabla.getValueAt(i, 0).toString();
                    articulo = (String) detallesVentaTabla.getValueAt(i, 1).toString();
                    pUnitario = (String) detallesVentaTabla.getValueAt(i, 2).toString();
                    pTotal = (String) detallesVentaTabla.getValueAt(i, 3).toString();
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
    public void actualizaTablaProveedoresAnchos(){
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
    private void leerProveedorBD(String findClave, String find){
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
                rs = statement.executeQuery("SELECT * FROM Proveedores WHERE " + "Empresa" + " LIKE '%" + (String)find+"%' ORDER BY Empresa ASC");
                while(rs.next()){                    
                    Object[] row = new Object[5];
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
        //Chequea si no esta cargada la ventana
        if(!proveedorInternalFrame.isShowing()){
            proveedorInternalFrame.setVisible(true);
        }else{
            this.proveedorInternalFrame.moveToFront();
        }
        limpiaFichaProveedor();       
        flagSaveFichaProveedor = 0;   //Guardar dato
    }//GEN-LAST:event_nuevoProveedorButtonActionPerformed

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


    
    
     /*
     * Actualizar datos de proveedores en la base de datos
     */
    private void actualizarProveedor(){
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
    private void guardarProveedorNuevo(){
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
                String buscarClave = (String)buscarArticuloComboBox.getSelectedItem();
                String buscar = buscarArticuloTxt.getText();
                //leerClienteToDB(buscarClave, buscar);
                leerArticulos(buscar, buscarClave);
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
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Tipo:\n hospitalario\n ECG\n bombas\n todos %",
                        "Ingrese tipo de articulo",
                        JOptionPane.WARNING_MESSAGE);

            parameters.put("tipo", input);


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
    }//GEN-LAST:event_listaArticulosMenuItemActionPerformed

    private void buscarContratoTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarContratoTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarContratoTxt.getText();
        String buscarClave = (String)buscarContratoComboBox.getSelectedItem();
        //leerClienteToDB(buscarClave, buscar);
        leerContratos(buscarClave, buscar);
        actualizaTablaContratosAnchos();
    }//GEN-LAST:event_buscarContratoTxtCaretUpdate

    private void clearBusquedaTxt2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxt2ActionPerformed
        // TODO add your handling code here:
        buscarContratoTxt.setText("");
    }//GEN-LAST:event_clearBusquedaTxt2ActionPerformed

    private void nuevoClienteButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButton1ActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        //Chequea si no esta cargada la ventana
        if(!contratoInternalFrame.isShowing()){
            contratoInternalFrame.setVisible(true);
        }else{
            this.contratoInternalFrame.moveToFront();
        }
        limpiaContrato();
        firmaContratoTxt.setText(hoy);
        flagSaveContrato = 0;
    }//GEN-LAST:event_nuevoClienteButton1ActionPerformed


    public void limpiaEquipo(){
        idEquipoTxt.setText("");
        contratoEquipoTxt.setText("");
        
        fechaInstalacionEquipoTxt.setText("");
        marcaEquipoTxt.setText("");
        modeloEquipoTxt.setText("");
        numSerieEquipoTxt.setText("");
        añoFabricacionEquipoTxt.setText("");
        origenEquipoTxt.setText("");

        fechaFuncionamientoEquipoTxt.setText("");
        caracteristicaEquipoTxt.setText("");
        tipoEquipoTxt.setText("");

        idContratoEquipoTxt.setText("");
        contratoEquipoTxt.setText("");

        fechaInstalacionEquipoTxt.setText("");
        fechaRetiroEquipoTxt.setText("");
    }

    public void limpiaContrato(){
        firmaContratoTxt.setText("");
        idContrato.setText("");
        numContratoTxt.setText("");
        empresaContratoTxt.setText("");
        tipoContratoTxt.setText("");
        ciudadContratoTxt.setText("");
        telefonoContratoTxt.setText("");
        direccionContratoTxt.setText("");
        firmaContratoTxt.setText("");
        duracionContratoTxt.setText("");
        cancelacionContratoTxt.setText("");

        nequiposTxt.setText("");
        ninsumos.setText("");
        pequipos.setText("");
        pinsumos.setText("");
        envioContratoTxt.setText("");
    }

    private void buscarClienteTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarClienteTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarClienteTxt.getText();
        String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
        DefaultTableModel kde = bdc.leer(buscarClave, buscar,"LIKE");
        leerClientes2Tabla(kde);
        actualizaClientesTabla();
    }//GEN-LAST:event_buscarClienteTxtCaretUpdate

    private void clientesTablaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_clientesTablaMouseReleased
     
    }//GEN-LAST:event_clientesTablaMouseReleased

    private void nuevoClienteButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButton2ActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        limpiaFichaCliente();
        //escrituraFichaCliente();
        fechaClienteTxt.setText(hoy);
}//GEN-LAST:event_nuevoClienteButton2ActionPerformed


    /*
     * LIMPIA FICHA
     */
    public void limpiaFichaCliente(){
        flagSaveFichaCliente = 0; //guardar ficha articulo
        idClienteTxt.setText("");
        clienteTxt.setText("");
        rucClienteTxt.setText("");
        nombreComercialClienteTxt.setText("");
        fechaClienteTxt.setText("");
        ciudadClienteTxt.setText("");
        direccionClienteTxt.setText("");
        telefonoClienteTxt.setText("");
        emailClienteTxt.setText("");
    }

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
                bdc.borrar(idClienteTxt.getText());
                clientesInternalFrame.dispose();
                
                String buscar = buscarClienteTxt.getText();
                String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
                DefaultTableModel kde = bdc.leer(buscarClave, buscar,"LIKE");
                leerClientes2Tabla(kde);
                actualizaClientesTabla();
            }
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(null, " No se puede eliminar este registro!! ",
                    "Aviso!", JOptionPane.ERROR_MESSAGE);
        }
}//GEN-LAST:event_borrarFichaClienteButtonActionPerformed


    private void guardarClienteButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarClienteButton2ActionPerformed
        // TODO add your handling code here:
        Cliente cliente = new Cliente(
                    idClienteTxt.getText(),
                    clienteTxt.getText(),
                    rucClienteTxt.getText(),
                    direccionClienteTxt.getText(),
                    telefonoClienteTxt.getText(),
                    fechaClienteTxt.getText(),
                    ciudadClienteTxt.getText(),
                    nombreComercialClienteTxt.getText(),
                    emailClienteTxt.getText());
        
        if(flagSaveFichaCliente == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaClienteTxt.getText().isEmpty()) && !clienteTxt.getText().isEmpty()){
                bdc.escribir(cliente); //guarda ficha cliente en base datos
                
                String buscar = buscarClienteTxt.getText();
                String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
                DefaultTableModel kde = bdc.leer(buscarClave, buscar,"LIKE");
                leerClientes2Tabla(kde);
                actualizaClientesTabla();
            }else{
                //custom title, warning icon
                JOptionPane.showMessageDialog(null, " Imposible agregar este registro!! ",
                        "Aviso!", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaClienteTxt.getText().isEmpty()) && !clienteTxt.getText().isEmpty()){
                bdc.actualizar(cliente); //guarda ficha cliente en base datos

                String buscar = buscarClienteTxt.getText();
                String buscarClave = (String)buscarClienteComboBox.getSelectedItem();
                DefaultTableModel kde = bdc.leer(buscarClave, buscar,"LIKE");
                leerClientes2Tabla(kde);
                actualizaClientesTabla();
            }else{
                //custom title, warning icon
                JOptionPane.showMessageDialog(null, " Imposible actualizar este registro!! ",
                        "Aviso!", JOptionPane.ERROR_MESSAGE);
            }
        }
}//GEN-LAST:event_guardarClienteButton2ActionPerformed

    private void cerrarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarButtonActionPerformed
        // TODO add your handling code here:
        clientesInternalFrame.dispose();
}//GEN-LAST:event_cerrarButtonActionPerformed

    private void crearFacturaClienteButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearFacturaClienteButton2ActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);

        //Chequea si no esta cargada la ventana
        if(!facturaVentaInternalFrame.isShowing()){
            facturaVentaInternalFrame.setVisible(true);
        }else{
            this.facturaVentaInternalFrame.moveToFront();
        }

        //escrituraFichaFactura();
        //cargaFichaCliente();
        jTabbedPane1.setSelectedIndex(1);
        //Tomo informacion para crear factura
        clienteFacturaTxt.setText(clienteTxt.getText());
        rucTxt.setText(rucClienteTxt.getText());
        fechaTxt.setText(hoy);
        direccionTxt.setText(direccionClienteTxt.getText());
        telefonoTxt.setText(telefonoClienteTxt.getText());
        ciudadTxt.setText(ciudadClienteTxt.getText());
        leerUltimaFactura();
        clientesInternalFrame.dispose();
    }//GEN-LAST:event_crearFacturaClienteButton2ActionPerformed

    private void borrarProveedorButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarProveedorButton1ActionPerformed
        // TODO add your handling code here:
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
                leerProveedorBD("Empresa", "");
                actualizaTablaProveedoresAnchos();
            }
        }
    }//GEN-LAST:event_borrarProveedorButton1ActionPerformed

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
    
    private void nuevoProveedorButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoProveedorButton1ActionPerformed
        // TODO add your handling code here:
        limpiaFichaProveedor();
        flagSaveFichaProveedor = 0;   //Guardar dato
    }//GEN-LAST:event_nuevoProveedorButton1ActionPerformed

    private void guardarProveedorButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarProveedorButton1ActionPerformed
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
    }//GEN-LAST:event_guardarProveedorButton1ActionPerformed

    private void crearFacturaClienteButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_crearFacturaClienteButton3ActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);

        //Chequea si no esta cargada la ventana
        if(!comprasInternalFrame.isShowing()){
            comprasInternalFrame.setVisible(true);
        }else{
            this.comprasInternalFrame.moveToFront();
        }
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
    }//GEN-LAST:event_crearFacturaClienteButton3ActionPerformed

    private void nuevaFacturaButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevaFacturaButton2ActionPerformed
        // TODO add your handling code here:
         SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);

        //Chequea si no esta cargada la ventana
        if(!comprasInternalFrame.isShowing()){
            comprasInternalFrame.setVisible(true);
        }else{
            this.comprasInternalFrame.moveToFront();
        }
        limpiaFichaFacturaProveedor();
        fechaCompraTxt.setText(hoy);
    }//GEN-LAST:event_nuevaFacturaButton2ActionPerformed

    private void cerrarButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarButton1ActionPerformed
        // TODO add your handling code here:
        facturaVentaInternalFrame.dispose();
    }//GEN-LAST:event_cerrarButton1ActionPerformed

    private void cerrarButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarButton2ActionPerformed
        // TODO add your handling code here:
        proveedorInternalFrame.dispose();
    }//GEN-LAST:event_cerrarButton2ActionPerformed

    private void cerrarButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarButton3ActionPerformed
        // TODO add your handling code here:
        comprasInternalFrame.dispose();
    }//GEN-LAST:event_cerrarButton3ActionPerformed

    private void cerrarButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarButton4ActionPerformed
        // TODO add your handling code here:
        articuloInternalFrame.dispose();
    }//GEN-LAST:event_cerrarButton4ActionPerformed

    private void cerrarContratoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarContratoButtonActionPerformed
        // TODO add your handling code here:
        contratoInternalFrame.dispose();
    }//GEN-LAST:event_cerrarContratoButtonActionPerformed

    private void guardarContratoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarContratoButtonActionPerformed
        // TODO add your handling code here:
         if(flagSaveContrato == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!numContratoTxt.getText().isEmpty()) && !empresaContratoTxt.getText().isEmpty()){
                guardarContratoBD(); //guarda ficha cliente en base datos
                leerContratos("Empresa","");
                actualizaTablaContratosAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no guaradado, llenar todos los datos",
                        "Error -5", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!numContratoTxt.getText().isEmpty()) && !empresaContratoTxt.getText().isEmpty()){
                actualizarContrato();
                leerContratos("Empresa","");
                actualizaTablaContratosAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no actualizado",
                        "Error -6", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarContratoButtonActionPerformed

    private void nuevoContratoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoContratoButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        //Chequea si no esta cargada la ventana
        if(!contratoInternalFrame.isShowing()){
            contratoInternalFrame.setVisible(true);
        }else{
            this.contratoInternalFrame.moveToFront();
        }
        limpiaContrato();
        firmaContratoTxt.setText(hoy);
        flagSaveContrato = 0;
    }//GEN-LAST:event_nuevoContratoButtonActionPerformed

    private void borrarContratoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarContratoButtonActionPerformed
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
            if (!idContrato.getText().isEmpty()){
                borrarContratoBD();
                limpiaContrato();
                leerContratos("Empresa","");
                actualizaTablaContratosAnchos();
                contratoInternalFrame.dispose();
            }
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame,
                    "Password no valido",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarContratoButtonActionPerformed

    private void buscarEquipoTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarEquipoTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarEquipoTxt.getText();
        String buscarClave = (String)buscarEquipoComboBox.getSelectedItem();
        //leerClienteToDB(buscarClave, buscar);
        leerEquipos(buscarClave, buscar);
        actualizaTablaEquiposAnchos();
    }//GEN-LAST:event_buscarEquipoTxtCaretUpdate

    private void clearBusquedaTxt3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxt3ActionPerformed
        // TODO add your handling code here:
        buscarEquipoTxt.setText("");
    }//GEN-LAST:event_clearBusquedaTxt3ActionPerformed

    private void nuevoClienteButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoClienteButton3ActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        //Chequea si no esta cargada la ventana
        if(!equipoInternalFrame.isShowing()){
            equipoInternalFrame.setVisible(true);
        }else{
            this.equipoInternalFrame.moveToFront();
        }
        limpiaEquipo();
        fechaInstalacionEquipoTxt.setText(hoy);
        fechaFuncionamientoEquipoTxt.setText(hoy);
        flagSaveEquipo = 0;
        infoTabbedPane.setSelectedIndex(0);
    }//GEN-LAST:event_nuevoClienteButton3ActionPerformed

    private void equipoAsociarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_equipoAsociarButtonActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);

        //Chequea si no esta cargada la ventana
        if(!equipoInternalFrame.isShowing()){
            equipoInternalFrame.setVisible(true);
        }else{
            this.equipoInternalFrame.moveToFront();
        }

        jTabbedPane1.setSelectedIndex(5);
        //Tomo informacion para crear factura
        contratoEquipoTxt.setText(numContratoTxt.getText());

        fechaInstalacionEquipoTxt.setText(firmaContratoTxt.getText());
        contratoInternalFrame.dispose();
    }//GEN-LAST:event_equipoAsociarButtonActionPerformed

    private void nuevoEquipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoEquipoButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        //Chequea si no esta cargada la ventana
        if(!equipoInternalFrame.isShowing()){
            equipoInternalFrame.setVisible(true);
        }else{
            this.equipoInternalFrame.moveToFront();
        }
        limpiaEquipo();
        fechaInstalacionEquipoTxt.setText(hoy);
        fechaFuncionamientoEquipoTxt.setText(hoy);
        flagSaveEquipo = 0;
        infoTabbedPane.setSelectedIndex(0);
    }//GEN-LAST:event_nuevoEquipoButtonActionPerformed

    private void borrarEquipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarEquipoButtonActionPerformed
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
            if (!idEquipoTxt.getText().isEmpty()){
                borrarEquipoBD();
                limpiaEquipo();
                String buscar = buscarEquipoTxt.getText();
                String buscarClave = (String)buscarEquipoComboBox.getSelectedItem();
                //leerClienteToDB(buscarClave, buscar);
                leerEquipos(buscarClave, buscar);
                actualizaTablaEquiposAnchos();
                equipoInternalFrame.dispose();
            }
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame,
                    "Password no valido",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarEquipoButtonActionPerformed

    private void guardarEquipoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarEquipoButtonActionPerformed
        // TODO add your handling code here:
        if(flagSaveEquipo == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!tipoEquipoTxt.getText().isEmpty()) && !numSerieEquipoTxt.getText().isEmpty()){
                guardarEquipoBD(); //guarda ficha cliente en base datos
                String buscar = buscarEquipoTxt.getText();
                String buscarClave = (String)buscarEquipoComboBox.getSelectedItem();
                //leerClienteToDB(buscarClave, buscar);
                leerEquipos(buscarClave, buscar);
                actualizaTablaEquiposAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo no guaradado, llenar todos los datos",
                        "Error -5", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!tipoEquipoTxt.getText().isEmpty()) && !numSerieEquipoTxt.getText().isEmpty()){
                actualizarEquipo();

                String buscar = buscarEquipoTxt.getText();
                String buscarClave = (String)buscarEquipoComboBox.getSelectedItem();
                //leerClienteToDB(buscarClave, buscar);
                leerEquipos(buscarClave, buscar);
                actualizaTablaEquiposAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo no actualizado",
                        "Error -6", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarEquipoButtonActionPerformed

    private void cerrarContratoButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarContratoButton2ActionPerformed
        // TODO add your handling code here:
        equipoInternalFrame.dispose();
    }//GEN-LAST:event_cerrarContratoButton2ActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        if(flagSaveContratoEquipo == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!contratoEquipoTxt.getText().isEmpty()) && !numSerieEquipoTxt.getText().isEmpty()){
                guardarContratoEquipoBD(); //guarda ficha cliente en base datos
                
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no guaradado, llenar todos los datos",
                        "Error -5", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!contratoEquipoTxt.getText().isEmpty()) && !numSerieEquipoTxt.getText().isEmpty()){
                actualizarContratoEquipo();

            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no actualizado",
                        "Error -6", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton14ActionPerformed

    private void equipoInternalFrameInternalFrameOpened(javax.swing.event.InternalFrameEvent evt) {//GEN-FIRST:event_equipoInternalFrameInternalFrameOpened
        // TODO add your handling code here:
        LeerContratoEquipos(numSerieEquipoTxt.getText());
    }//GEN-LAST:event_equipoInternalFrameInternalFrameOpened

    private void infoTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_infoTabbedPaneStateChanged
        // TODO add your handling code here:
        int num = infoTabbedPane.getSelectedIndex();
        switch (num){
            //Datos
            case 1:{
                LeerContratoEquipos(numSerieEquipoTxt.getText());
                break;
            }
            case 3:{
                LeerFallasEquipos(numSerieEquipoTxt.getText());
                break;
            }
        }
    }//GEN-LAST:event_infoTabbedPaneStateChanged

    private void jButton17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton17ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton17ActionPerformed

    private void jButton20ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton20ActionPerformed
        // TODO add your handling code here:
        if(flagSaveFallo == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!detalleTxt.getText().isEmpty()) && !numSerieEquipoTxt.getText().isEmpty()){
                guardarFallasEquipoBD(); //guarda ficha cliente en base datos

            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "falla no guaradada, llenar todos los datos",
                        "Error -5", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            //ACTUALIZAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!detalleTxt.getText().isEmpty()) && !numSerieEquipoTxt.getText().isEmpty()){
                actualizarFallasEquipo();

            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Falla no actualizada",
                        "Error -6", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_jButton20ActionPerformed

    private void nuevoArticulobButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoArticulobButtonActionPerformed
        // TODO add your handling code here:
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        filename = System.getProperty("user.home") + "/jContab/logo.jpg";
        //Chequea si no esta cargada la ventana
        if(!articuloInternalFrame.isShowing()){
            articuloInternalFrame.setVisible(true);
        }else{
            this.articuloInternalFrame.moveToFront();
        }
        
        limpiaFichaArticulos();
        FechaIngresoArticuloTxt.setText(hoy);
    }//GEN-LAST:event_nuevoArticulobButtonActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/VentasClientes.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Generar:\n% - todo\n%2011 - año\n%abr/2011 - mes",
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
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        // TODO add your handling code here:
        flagSaveContratoEquipo = 1;
        String temp = (String)contratoEquiposComboBox.getSelectedItem();
        LeerContratoEquiposTodo(temp, numSerieEquipoTxt.getText());
    }//GEN-LAST:event_jButton15ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        flagSaveContratoEquipo = 0;

        idContratoEquipoTxt.setText("");
        contratoEquipoTxt.setText("");

        fechaInstalacionEquipoTxt.setText("");
        fechaRetiroEquipoTxt.setText("");

    }//GEN-LAST:event_jButton13ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/Contratos.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Equipos activos:\n SI = 0\n NO = 1\n todos %",
                        "Ingrese tipo",
                        JOptionPane.WARNING_MESSAGE);

            parameters.put("activo", input);


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
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/Equipos.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Generar:\n% - todo\n%Bomba Infusion\n%ECG",
                        "Ingrese Tipo Equipo",
                        JOptionPane.WARNING_MESSAGE);

            parameters.put("tipo", input);

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
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/Contratos_envios.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Tipo contrato:\n comodato\n venta\n %",
                        "Ingrese tipo contrato",
                        JOptionPane.WARNING_MESSAGE);

            parameters.put("tipo", input);


            String input2 = JOptionPane.showInputDialog(new JFrame(),"Estado contrato:\n   - activo\n % - todos",
                        "Ingrese contrato",
                        JOptionPane.WARNING_MESSAGE);
            parameters.put("activo", input2);


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
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/ComprasProveedores.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Generar:\n% - todo\n%2011 - año\n%abr/2011 - mes",
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
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jButton19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton19ActionPerformed
        // TODO add your handling code here:
        flagSaveFallo = 0;

        idContratoEquipoFalloTxt.setText("");
        detalleTxt.setText("");
    }//GEN-LAST:event_jButton19ActionPerformed

    private void jButton21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton21ActionPerformed
        // TODO add your handling code here:
        flagSaveContrato = 1;
        String temp = (String)fallasEquiposComboBox.getSelectedItem();
        LeerFallasEquiposTodo(temp, numSerieEquipoTxt.getText());
    }//GEN-LAST:event_jButton21ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/ReporteFallas.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            String input = JOptionPane.showInputDialog(new JFrame(),"Equipos activos:\n SI = 0\n NO = 1\n todos %",
                        "Ingrese tipo",
                        JOptionPane.WARNING_MESSAGE);

            parameters.put("isActive", input);


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
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void buscarTesoreriaTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarTesoreriaTxtCaretUpdate
        // TODO add your handling code here:
    }//GEN-LAST:event_buscarTesoreriaTxtCaretUpdate

    private void clearBusquedaTxt4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearBusquedaTxt4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_clearBusquedaTxt4ActionPerformed

    private void nuevTesoreriaButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevTesoreriaButtonActionPerformed
        // TODO add your handling code here:
        flagSaveTesoreria = 0;
        if(!apunteContableInternalFrame.isShowing()){
            apunteContableInternalFrame.setVisible(true);
        }else{
            this.apunteContableInternalFrame.moveToFront();
        }
        
        limpiaFichaApunte();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        fechaTesoreriaTxt.setText(hoy);

    }//GEN-LAST:event_nuevTesoreriaButtonActionPerformed

    private void nuevoApunteContableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nuevoApunteContableButtonActionPerformed
        // TODO add your handling code here:
        flagSaveTesoreria = 0;
        limpiaFichaApunte();
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        fechaTesoreriaTxt.setText(hoy);
    }//GEN-LAST:event_nuevoApunteContableButtonActionPerformed

     /*
     * Limpia ficha de proveedor
     */
    void limpiaFichaApunte(){
        idTesoreriaTxt.setText("");
        fechaTesoreriaTxt.setText("");
        clienteProveedorTxt.setText("");
        ingresoTxt.setText("0.00");
        pagoTxt.setText("0.00");
        memoTxt.setText("");
    }

    private void borrarApunteContableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarApunteContableButtonActionPerformed
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
            if (!idTesoreriaTxt.getText().isEmpty()){
                borrarFichaApunte();
                limpiaFichaApunte();
                leerTesoreria("ClienteProveedor","");
                actualizaListaTesoreriaTablaAnchos();
                apunteContableInternalFrame.dispose();
            }
        }else{
            JFrame frame = new JFrame();
            JOptionPane.showMessageDialog(frame,
                    "Password no valido",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_borrarApunteContableButtonActionPerformed


     /*
     * Borrar proveedor de la base de datos
     */
    void borrarFichaApunte(){
        if(flagDriver == 1){
            //PROCEDE A BORRAR FICHA DEL ARTICULO
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM Tesoreria WHERE id=" + Integer.parseInt(idTesoreriaTxt.getText()));
                statement.close();
                connection.close();

                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Apunte contable borrado",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "No se borro apunte contable",
                        "Error - 8", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void guardarApunteContableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardarApunteContableButtonActionPerformed
        // TODO add your handling code here:
        if(flagSaveTesoreria == 0){
            //GUARDAR
            //chequea si hay escrito los datos, si hay? guarda
            if((!fechaTesoreriaTxt.getText().isEmpty()) && !clienteProveedorTxt.getText().isEmpty()){
                guardarApunteContable(); //guarda ficha cliente en base datos
                leerTesoreria("ClienteProveedor", ""); //Vuelva a cargar listado de clientes
                actualizaListaTesoreriaTablaAnchos();
                
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
            if((!fechaTesoreriaTxt.getText().isEmpty()) && !idTesoreriaTxt.getText().isEmpty()){
                actualizarApunteContable();
                leerTesoreria("ClienteProveedor", ""); //Vuelva a cargar listado de clientes
                actualizaListaTesoreriaTablaAnchos();
            }else{
                //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Apunte contable no actualizado",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_guardarApunteContableButtonActionPerformed


    /*
     * Actualizar datos de articulos en la base de datos
     */
    void actualizarApunteContable(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int actualizar = statement.executeUpdate("UPDATE Tesoreria SET " +               
                    "Fecha='"+fechaTesoreriaTxt.getText()+"' ,"+
                    "Concepto='"+conceptoApunteContableComboBox.getSelectedItem()+"' ,"+
                    "Ingresos='"+ingresoTxt.getText()+"' ,"+
                    "Pagos='"+pagoTxt.getText()+"' ,"+
                    "ClienteProveedor='"+clienteProveedorTxt.getText()+"' ,"+
                    "Memo='"+memoTxt.getText()+"'"+
                    " WHERE id =" + (String)idTesoreriaTxt.getText());
                statement.close();
                connection.close();
                 //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Apunte contable actualizado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveTesoreria = 1; //actualizar
 
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Apunte conteble no se actualizo",
                        "Error - 11", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * GUARDAR FICHA equipos NUEVA
     */
    void guardarApunteContable(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO Tesoreria " +
                        "(Fecha, Concepto, Ingresos, Pagos, ClienteProveedor, Memo) " +
                    "VALUES(" +
                    "'"+fechaTesoreriaTxt.getText()+"' ,"+
                    "'"+conceptoApunteContableComboBox.getSelectedItem()+"' ,"+
                    "'"+ingresoTxt.getText()+"' ,"+
                    "'"+pagoTxt.getText()+"' ,"+
                    "'"+clienteProveedorTxt.getText()+"' ,"+
                    "'"+memoTxt.getText()+"'"+
                ")");

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Apunte contable guardado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveTesoreria = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Apunte contable no guardado",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    private void cerrarApunteContableButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cerrarApunteContableButtonActionPerformed
        // TODO add your handling code here:
        apunteContableInternalFrame.dispose();
    }//GEN-LAST:event_cerrarApunteContableButtonActionPerformed

    private void jMenuItem11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        // TODO add your handling code here:
        String filename = System.getProperty("user.home") + "/jContab/Tesoreria.jasper";
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection(baseDatos);
            HashMap parameters = new HashMap();

            JasperReport reporte = (JasperReport) JRLoader.loadObject(filename);
            JasperPrint jasperPrint = JasperFillManager.fillReport(filename, null, con);
            JasperViewer jv = new JasperViewer(jasperPrint, false);
            jv.setVisible(true);


        } catch (Exception ex) {
            System.out.println("CAUSE: " + ex.getCause());
            System.out.println("MESSAGE" + ex.getMessage());
            System.out.println("LOCAL MESSAGE" + ex.getLocalizedMessage());
            ex.printStackTrace();
        }
    }//GEN-LAST:event_jMenuItem11ActionPerformed
  
    /*
     * BORRAR FICHA ARTICULO
     */
    public void borrarContratoBD(){
        if(flagDriver == 1){
            //PROCEDE A BORRAR FICHA DEL ARTICULO
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM Contratos WHERE id=" + Integer.parseInt(idContrato.getText()));
                statement.close();
                connection.close();

                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato borrado",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no se borro",
                        "Error - 8", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


     /*
     * BORRAR FICHA ARTICULO
     */
    public void borrarEquipoBD(){
        if(flagDriver == 1){
            //PROCEDE A BORRAR FICHA DEL ARTICULO
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int borrar = statement.executeUpdate("DELETE FROM Equipos WHERE id=" + Integer.parseInt(idEquipoTxt.getText()));
                statement.close();
                connection.close();

                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo borrado",
                        "Información", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo no se borro",
                        "Error - 8", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * GUARDAR FICHA equipos NUEVA
     */
    void guardarContratoEquipoBD(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO ContratoEquipos " +
                        "(Contrato, nSerie, TipoEquipo, FechaInstalacion, FechaRetiro, Activo) " +
                    "VALUES(" +
                    "'"+contratoEquipoTxt.getText()+"' ,"+
                    "'"+numSerieEquipoTxt.getText()+"' ,"+
                    "'"+tipoEquipoTxt.getText()+"' ,"+
                    "'"+fechaInstalacionEquipoTxt.getText()+"' ,"+
                    "'"+fechaRetiroEquipoTxt.getText()+"' ,"+
                    "'"+activoEquipoComboBox.getSelectedIndex()+"'"+
                ")");

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Contrato equipo guardado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContratoEquipo = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato equipo no guardado",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /*
     * GUARDAR FICHA equipos NUEVA
     */
    void guardarFallasEquipoBD(){
        //si esta presente driver guarda informacion
        SimpleDateFormat formato = new SimpleDateFormat("dd/MMM/yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO Fallas " +
                        "(nSerie, Marca, Modelo, Detalle, Fecha) " +
                    "VALUES(" +
                    "'"+numSerieEquipoTxt.getText()+"' ,"+
                    "'"+marcaEquipoTxt.getText()+"' ,"+
                    "'"+modeloEquipoTxt.getText()+"' ,"+
                    "'"+detalleTxt.getText()+"' ,"+
                    "'"+hoy+"'"+
                ")");

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Falla equipo guardada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContratoEquipo = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Falla equipo no guardada",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    /*
     * GUARDAR FICHA equipos NUEVA
     */
    void guardarEquipoBD(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO Equipos " +
                        "(TipoEquipo, Marca, Modelo, Fabricacion, nSerie, LugarOrigen, FechaFuncionamiento, Opcion) " +
                    "VALUES(" +
                    "'"+tipoEquipoTxt.getText()+"' ,"+
                    "'"+marcaEquipoTxt.getText()+"' ,"+
                    "'"+modeloEquipoTxt.getText()+"' ,"+
                    "'"+añoFabricacionEquipoTxt.getText()+"' ,"+
                    "'"+numSerieEquipoTxt.getText()+"' ,"+
                    "'"+origenEquipoTxt.getText()+"' ,"+
                    "'"+fechaFuncionamientoEquipoTxt.getText()+"' ,"+
                    "'"+caracteristicaEquipoTxt.getText()+"'"+
                ")");

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Equipo guardado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContrato = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo no guardado",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * GUARDAR FICHA CLIENTES NUEVA
     */
    void guardarContratoBD(){
        int rows = 0;
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int escribe = statement.executeUpdate("INSERT INTO Contratos " +
                        "(nContrato, Empresa, TipoContrato, Ciudad, Direccion, Telefono, FirmaContrato, Duracion, Cancelacion, nEquipos, pEquipos, cInsumos, pInsumos, EnvioInsumos) " +
                    "VALUES(" +
                    "'"+numContratoTxt.getText()+"' ,"+
                    "'"+empresaContratoTxt.getText()+"' ,"+
                    "'"+tipoContratoTxt.getText()+"' ,"+
                    "'"+ciudadContratoTxt.getText()+"' ,"+
                    "'"+direccionContratoTxt.getText()+"' ,"+
                    "'"+telefonoContratoTxt.getText()+"' ,"+
                    "'"+firmaContratoTxt.getText()+"' ,"+
                    "'"+duracionContratoTxt.getText()+"' ,"+
                    "'"+cancelacionContratoTxt.getText()+"' ,"+
                    "'"+nequiposTxt.getText()+"' ,"+
                    "'"+pequipos.getText()+"' ,"+
                    "'"+ninsumos.getText()+"' ,"+
                    "'"+pinsumos.getText()+"' ,"+
                    "'"+envioContratoTxt.getText()+"'"+
                ")");
       
                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Contrato guardado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContrato = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no guardado",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


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
                    + "Tipo = ?,"
                    + "beneficio = ?, pvp = ?, imagen = ? WHERE codarticulo = ?");

                    pstatement.setString(1, detalleArticuloTxt.getText());
                    pstatement.setString(2, FechaIngresoArticuloTxt.getText());
                    pstatement.setString(3, stockTxt.getText());
                    pstatement.setString(4, precioVentaTxt.getText());
                    pstatement.setString(5, proveedorTxt.getText());
                    pstatement.setString(6, precioCosteTxt.getText());
                    pstatement.setString(7, tipoArticuloTxt.getText());
                    pstatement.setString(8, beneficioTxt.getText());
                    pstatement.setString(9, precioFinalTxt.getText());

                    File imagen = new File(fila);
                    FileInputStream fis = new FileInputStream(imagen);

                
                    pstatement.setBinaryStream(10, fis, (int)imagen.length());
                    pstatement.setInt(11, Integer.parseInt(idArticuloTxt.getText()));
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
     * Actualizar datos de articulos en la base de datos
     */
    void actualizarEquipo(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int actualizar = statement.executeUpdate("UPDATE Equipos SET " +

                    "TipoEquipo='"+tipoEquipoTxt.getText()+"' ,"+
                    "Marca='"+marcaEquipoTxt.getText()+"' ,"+
                    "Modelo='"+modeloEquipoTxt.getText()+"' ,"+
                    "Fabricacion='"+añoFabricacionEquipoTxt.getText()+"' ,"+
                    "nSerie='"+numSerieEquipoTxt.getText()+"' ,"+
                    "LugarOrigen='"+origenEquipoTxt.getText()+"' ,"+
                    "FechaFuncionamiento='"+fechaFuncionamientoEquipoTxt.getText()+"' ,"+
                    "Opcion='"+caracteristicaEquipoTxt.getText()+"'"+

                    " WHERE id =" + (String)idEquipoTxt.getText());
                statement.close();
                connection.close();
                 //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo actualizado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveEquipo = 1; //actualizar

            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Equipo no se actualizo",
                        "Error - 11", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

 /*
     * GUARDAR FICHA equipos NUEVA
     */
    void actualizarContratoEquipo(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                 int actualizar = statement.executeUpdate("UPDATE ContratoEquipos SET " +

                    "TipoEquipo='"+tipoEquipoTxt.getText()+"' ,"+
                    "Contrato='"+contratoEquipoTxt.getText()+"' ,"+
                    "nSerie='"+numSerieEquipoTxt.getText()+"' ,"+
                    "TipoEquipo='"+tipoEquipoTxt.getText()+"' ,"+
                    "FechaInstalacion='"+fechaInstalacionEquipoTxt.getText()+"' ,"+
                    "FechaRetiro='"+fechaRetiroEquipoTxt.getText()+"' ,"+
                    "Activo='"+activoEquipoComboBox.getSelectedIndex()+"'"+

                    " WHERE id =" + (String)idContratoEquipoTxt.getText());
                

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Contrato equipo actualizado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContratoEquipo = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato equipo no actualizado",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }



 /*
     * GUARDAR FICHA equipos NUEVA
     */
    void actualizarFallasEquipo(){
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                 int actualizar = statement.executeUpdate("UPDATE Fallas SET " +

                    "nSerie='"+numSerieEquipoTxt.getText()+"' ,"+
                    "Marca='"+marcaEquipoTxt.getText()+"' ,"+
                    "Modelo='"+modeloEquipoTxt.getText()+"' ,"+
                    "Detalle='"+detalleTxt.getText()+"'"+


                    " WHERE id =" + (String)idContratoEquipoFalloTxt.getText());

                statement.close();
                connection.close();
                //custom title, warning icon
                JOptionPane.showMessageDialog(new JFrame(),
                        "Falla equipo actualizada",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContratoEquipo = 1; //guardado y listo para actualizar

            } catch (Exception e) {
                System.out.println("Error: "+ e);
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Falla equipo no actualizada",
                        "Error -7", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /*
     * Actualizar datos de articulos en la base de datos
     */
    void actualizarContrato(){
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                int actualizar = statement.executeUpdate("UPDATE Contratos SET " +
                    "nContrato='"+numContratoTxt.getText()+"' ,"+
                    "Empresa='"+empresaContratoTxt.getText()+"' ,"+
                    "TipoContrato='"+tipoContratoTxt.getText()+"' ,"+
                    "Ciudad='"+ciudadContratoTxt.getText()+"' ,"+
                    "Direccion='"+direccionContratoTxt.getText()+"' ,"+
                    "Telefono='"+telefonoContratoTxt.getText()+"' ,"+
                    "FirmaContrato='"+firmaContratoTxt.getText()+"' ,"+
                    "Duracion='"+duracionContratoTxt.getText()+"' ,"+
                    "Cancelacion='"+cancelacionContratoTxt.getText()+"' ,"+
                    "nEquipos='"+nequiposTxt.getText()+"' ,"+
                    "pEquipos='"+pequipos.getText()+"' ,"+
                    "cInsumos='"+ninsumos.getText()+"' ,"+
                    "pInsumos='"+pinsumos.getText()+"' ,"+
                    "EnvioInsumos='"+envioContratoTxt.getText()+"'"+
                    " WHERE id =" + (String)idContrato.getText());
                statement.close();
                connection.close();
                 //custom title, warning icon
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato actualizado",
                        "Información", JOptionPane.WARNING_MESSAGE);
                flagSaveContrato = 1; //actualizar
 
            } catch (Exception e) {
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame,
                        "Contrato no se actualizo",
                        "Error - 11", JOptionPane.ERROR_MESSAGE);
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
                pstatement.setString(7, tipoArticuloTxt.getText());
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
                    for (int i = 0, rows = detallesVentaTabla.getRowCount()-1; i < rows; i++){
                        statement = connection.createStatement();
                        int escribe2 = statement.executeUpdate("UPDATE Ventas SET " +

                        "fecha='"+fechaTxt.getText()+"' ,"+
                        "nserie='"+numeroSerieTxt.getText()+"' ,"+
                        "cantidad='"+detallesVentaTabla.getValueAt(i, 0).toString()+"' ,"+
                        "articulo='"+detallesVentaTabla.getValueAt(i, 1).toString()+"' ,"+
                        "punitario='"+detallesVentaTabla.getValueAt(i, 2).toString()+"' ,"+
                        "ptotal='"+detallesVentaTabla.getValueAt(i, 3).toString()+"'"+
                        " WHERE id =" + (String)detallesVentaTabla.getValueAt(i, 4).toString());
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
                    "'"+retencionTxt.getText()+"' ,"+
                    "'"+recividoTxt1.getText()+"' ,"+
                    "'"+fechaTxt.getText()+"' ,"+
                    "'"+numeroSerieTxt.getText()+"' ,"+
                    "'"+direccionTxt.getText()+"' ,"+
                    "'"+telefonoTxt.getText()+"' ,"+
                    "'"+ciudadTxt.getText()+"' "+
                ")");
                statement.close();
                
                //GUARDA TABLA DE ARTICULOS UNO POR UNO NUMERO SERIE CLAVE EN BUSQUEDA
                rows = detallesVentaTabla.getRowCount();
                for (int i = 0; i < rows; i++){
                    statement = connection.createStatement();
                    int escribe2 = statement.executeUpdate("INSERT INTO Ventas " +
                        "(RUC, fecha, nserie, cantidad, articulo, punitario, ptotal) " +
                    "VALUES(" +
                    "'"+rucTxt.getText()+"' ,"+
                    "'"+fechaTxt.getText()+"' ,"+
                    "'"+numeroSerieTxt.getText()+"' ,"+
                    "'"+detallesVentaTabla.getValueAt(i, 0).toString()+"' ,"+
                    "'"+detallesVentaTabla.getValueAt(i, 1).toString()+"' ,"+
                    "'"+detallesVentaTabla.getValueAt(i, 2).toString()+"' ,"+
                    "'"+detallesVentaTabla.getValueAt(i, 3).toString()+"'"+
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
        beneficioTxt.setText("25");
        precioVentaTxt.setText("");
        precioFinalTxt.setText("");
        detalleArticuloTxt.setText("");
        FechaIngresoArticuloTxt.setText("");
        tipoArticuloTxt.setText("");
        image = null;
        try {
            image = ImageIO.read(new File(filename));
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        ImagenLabel.setIcon(new ImageIcon(image));
        //System.out.println("Limpiando ficha de articulo.");
    }

    /*
     * LEE ARTICULO EN BASE DE DATOS, con criterio de busqueda
     */
    void leerContratos(String findClave, String find){
        listaContratosTabla.removeAll();
        listaContratosTabla.updateUI();
        String[] columnNames = {"ID",
                                "# contrato",
                                "Empresa",
                                "Tipo contrato",
                                "Firma",
                                "Duración",
                                "Cancelación",
                                "# Equipos",
                                "# Insumos",
                                "Envio"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Contratos WHERE " + findClave + " LIKE '%" + (String)find+"%'");
                
                while(rs.next()){
                    Object[] row = new Object[10];
                    row[0] = rs.getObject("id");
                    row[1] = rs.getObject("nContrato");
                    row[2] = rs.getObject("Empresa");
                    row[3] = rs.getObject("TipoContrato");
                    row[4] = rs.getObject("FirmaContrato");
                    row[5] = rs.getObject("Duracion");
                    row[6] = rs.getObject("Cancelacion");
                    row[7] = rs.getObject("nEquipos");
                    row[8] = rs.getObject("cInsumos");
                    row[9] = rs.getObject("EnvioInsumos");
                    
                    model.addRow(row);
                }
                listaContratosTabla.setModel(model);
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                //custom title, warning icon
                /*JOptionPane.showMessageDialog(new JFrame(),
                        "No se puede leer contratos",
                        "Error - 5", JOptionPane.ERROR_MESSAGE);
                 * 
                 */
            }
        }
    }



    /*
     * LEE ARTICULO EN BASE DE DATOS, con criterio de busqueda
     */
    void leerEquipos(String findClave, String find){
        listaEquiposTabla.removeAll();
        listaEquiposTabla.updateUI();
        String[] columnNames = {"id",
                                "Tipo equipo",
                                "Marca",
                                "Modelo",
                                "# serie",
                                "Año fabricacion"};

        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Equipos WHERE " + findClave + " LIKE '%" + (String)find+"%'");

                while(rs.next()){
                    Object[] row = new Object[6];
                    row[0] = rs.getObject("id");
                    row[1] = rs.getObject("TipoEquipo");
                    row[2] = rs.getObject("Marca");
                    row[3] = rs.getObject("Modelo");
                    row[4] = rs.getObject("nSerie");
                    row[5] = rs.getObject("Fabricacion");

       
                    model.addRow(row);
                }
                listaEquiposTabla.setModel(model);
                rs.close();
                statement.close();
                connection.close();
            } catch (Exception e) {
                //custom title, warning icon
                /*JOptionPane.showMessageDialog(new JFrame(),
                        "No se puede leer contratos",
                        "Error - 5", JOptionPane.ERROR_MESSAGE);
                 *
                 */
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {               
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
                } catch (Exception e) {
                    System.out.println("L&F error");
                }
                new Contab().setVisible(true);
            }
        });
    }
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField FechaIngresoArticuloTxt;
    public javax.swing.JLabel ImagenLabel;
    private javax.swing.JComboBox activoEquipoComboBox;
    private javax.swing.JButton agregarArticuloAFacturaButton;
    private javax.swing.JButton agregarArticuloButton;
    private javax.swing.JInternalFrame apunteContableInternalFrame;
    private javax.swing.JComboBox articuloComboBox;
    private javax.swing.JInternalFrame articuloInternalFrame;
    private javax.swing.JPanel articulosPanel;
    private javax.swing.JTable articulosTabla;
    private javax.swing.JTextField añoFabricacionEquipoTxt;
    public javax.swing.JTextField beneficioTxt;
    private javax.swing.JButton borrarApunteContableButton;
    private javax.swing.JButton borrarContratoButton;
    private javax.swing.JButton borrarEquipoButton;
    private javax.swing.JButton borrarFacturaClienteButton;
    private javax.swing.JButton borrarFacturaClienteButton1;
    private javax.swing.JButton borrarFichaArticuloButton;
    private javax.swing.JButton borrarFichaClienteButton;
    private javax.swing.JButton borrarProveedorButton1;
    private javax.swing.JComboBox buscarArticuloComboBox;
    private javax.swing.JTextField buscarArticuloTxt;
    private javax.swing.JComboBox buscarClienteComboBox;
    public javax.swing.JTextField buscarClienteTxt;
    private javax.swing.JComboBox buscarContratoComboBox;
    private javax.swing.JTextField buscarContratoTxt;
    private javax.swing.JComboBox buscarEquipoComboBox;
    private javax.swing.JTextField buscarEquipoTxt;
    private javax.swing.JComboBox buscarFacturaClienteComboBox;
    private javax.swing.JTextField buscarFacturaClienteTxt;
    private javax.swing.JTextField buscarFacturaProveedorTxt;
    private javax.swing.JTextField buscarFechaTxt;
    private javax.swing.JTextField buscarFechaTxt1;
    private javax.swing.JTextField buscarProveedorTxt;
    private javax.swing.JComboBox buscarTesoreriaComboBox;
    public javax.swing.JTextField buscarTesoreriaTxt;
    private javax.swing.JButton calcularArticuloButton;
    private javax.swing.JButton calucularFacturaButton;
    private javax.swing.JButton calucularFacturaButton1;
    private javax.swing.JTextField cancelacionContratoTxt;
    private javax.swing.JTextField caracteristicaEquipoTxt;
    private javax.swing.JButton cerrarApunteContableButton;
    private javax.swing.JButton cerrarButton;
    private javax.swing.JButton cerrarButton1;
    private javax.swing.JButton cerrarButton2;
    private javax.swing.JButton cerrarButton3;
    private javax.swing.JButton cerrarButton4;
    private javax.swing.JButton cerrarContratoButton;
    private javax.swing.JButton cerrarContratoButton2;
    public javax.swing.JTextField ciudadClienteTxt;
    private javax.swing.JTextField ciudadContratoTxt;
    private javax.swing.JTextField ciudadProveedorTxt;
    private javax.swing.JTextField ciudadTxt;
    private javax.swing.JButton cleanFacturaButton;
    private javax.swing.JButton cleanFacturaButton1;
    private javax.swing.JButton clearBusquedaTxt;
    private javax.swing.JButton clearBusquedaTxt1;
    private javax.swing.JButton clearBusquedaTxt2;
    private javax.swing.JButton clearBusquedaTxt3;
    private javax.swing.JButton clearBusquedaTxt4;
    private javax.swing.JTextField clienteFacturaTxt;
    private javax.swing.JTextField clienteProveedorTxt;
    public javax.swing.JTextField clienteTxt;
    private javax.swing.JInternalFrame clientesInternalFrame;
    private javax.swing.JPanel clientesPanel;
    public javax.swing.JTable clientesTabla;
    private javax.swing.JTextField cobradoTxt2;
    private javax.swing.JTextField cobradoTxt4;
    private javax.swing.JCheckBox cobrarFacturaCheckBox;
    private javax.swing.JInternalFrame comprasInternalFrame;
    private javax.swing.JPanel comprasPanel;
    private javax.swing.JComboBox conceptoApunteContableComboBox;
    private javax.swing.JTextArea contactoTextArea;
    private javax.swing.JTextArea contactoTextArea3;
    private javax.swing.JTextField contratoEquipoTxt;
    private javax.swing.JComboBox contratoEquiposComboBox;
    private javax.swing.JComboBox contratoEquiposComboBox1;
    private javax.swing.JInternalFrame contratoInternalFrame;
    private javax.swing.JPanel contratosPanel;
    private javax.swing.JButton crearFacturaClienteButton2;
    private javax.swing.JButton crearFacturaClienteButton3;
    public javax.swing.JTextArea detalleArticuloTxt;
    private javax.swing.JTextArea detalleTxt;
    private javax.swing.JTable detallesVentaTabla;
    public javax.swing.JTextField direccionClienteTxt;
    private javax.swing.JTextField direccionContratoTxt;
    private javax.swing.JTextField direccionProveedorTxt;
    private javax.swing.JTextField direccionTxt;
    private javax.swing.JTextField duracionContratoTxt;
    private javax.swing.JButton eliminarArticuloButton;
    public javax.swing.JTextField emailClienteTxt;
    private javax.swing.JTextField emailProveedorTxt;
    private javax.swing.JTextField empresaContratoTxt;
    private javax.swing.JTextField empresaTxt;
    private javax.swing.JTextField envioContratoTxt;
    private javax.swing.JButton equipoAsociarButton;
    private javax.swing.JInternalFrame equipoInternalFrame;
    private javax.swing.JInternalFrame facturaVentaInternalFrame;
    private javax.swing.JMenuItem facturasNoCobradasMenuItem;
    private javax.swing.JTable facturasProveedoresTable;
    private javax.swing.JComboBox fallasEquiposComboBox;
    private javax.swing.JRadioButton fechaBuscarRadioButton;
    private javax.swing.JRadioButton fechaBuscarRadioButton1;
    public javax.swing.JTextField fechaClienteTxt;
    private javax.swing.JTextField fechaCompraTxt;
    private javax.swing.JTextField fechaFuncionamientoEquipoTxt;
    private javax.swing.JTextField fechaInstalacionEquipoTxt;
    private javax.swing.JTextField fechaRetiroEquipoTxt;
    private javax.swing.JTextField fechaTesoreriaTxt;
    private javax.swing.JTextField fechaTxt;
    private javax.swing.JTextField firmaContratoTxt;
    private javax.swing.JTextField fuenteBaseTxt;
    private javax.swing.JTextField fuentePorTxt;
    private javax.swing.JButton guardarApunteContableButton;
    private javax.swing.JButton guardarArticuloButton;
    private javax.swing.JButton guardarClienteButton2;
    private javax.swing.JButton guardarContratoButton;
    private javax.swing.JButton guardarEquipoButton;
    private javax.swing.JButton guardarFacturaButton;
    private javax.swing.JButton guardarFacturaButton1;
    private javax.swing.JButton guardarProveedorButton1;
    public javax.swing.JTextField idArticuloTxt;
    public javax.swing.JTextField idClienteTxt;
    private javax.swing.JTextField idContrato;
    private javax.swing.JTextField idContratoEquipoFalloTxt;
    private javax.swing.JTextField idContratoEquipoTxt;
    private javax.swing.JTextField idContratoEquipoTxt1;
    private javax.swing.JTextField idEquipoTxt;
    private javax.swing.JTextField idFacturaProveedorTxt;
    private javax.swing.JTextField idFacturaTxt;
    private javax.swing.JTextField idProveedorTxt;
    private javax.swing.JTextField idTesoreriaTxt;
    private javax.swing.JButton imprimirFacturaButton;
    private javax.swing.JTabbedPane infoTabbedPane;
    private javax.swing.JTextField ingresoTxt;
    private javax.swing.JTextField ivaPorTxt;
    private javax.swing.JTextField ivabaseTxt;
    private javax.swing.JTextField ivat12Txt;
    private javax.swing.JTextField ivat12Txt1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton16;
    private javax.swing.JButton jButton17;
    private javax.swing.JButton jButton18;
    private javax.swing.JButton jButton19;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton20;
    private javax.swing.JButton jButton21;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel100;
    private javax.swing.JLabel jLabel101;
    private javax.swing.JLabel jLabel102;
    private javax.swing.JLabel jLabel103;
    private javax.swing.JLabel jLabel104;
    private javax.swing.JLabel jLabel105;
    private javax.swing.JLabel jLabel106;
    private javax.swing.JLabel jLabel107;
    private javax.swing.JLabel jLabel108;
    private javax.swing.JLabel jLabel109;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel110;
    private javax.swing.JLabel jLabel111;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
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
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
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
    private javax.swing.JLabel jLabel76;
    private javax.swing.JLabel jLabel77;
    private javax.swing.JLabel jLabel78;
    private javax.swing.JLabel jLabel79;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel80;
    private javax.swing.JLabel jLabel81;
    private javax.swing.JLabel jLabel82;
    private javax.swing.JLabel jLabel83;
    private javax.swing.JLabel jLabel84;
    private javax.swing.JLabel jLabel85;
    private javax.swing.JLabel jLabel86;
    private javax.swing.JLabel jLabel87;
    private javax.swing.JLabel jLabel88;
    private javax.swing.JLabel jLabel89;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel90;
    private javax.swing.JLabel jLabel91;
    private javax.swing.JLabel jLabel92;
    private javax.swing.JLabel jLabel93;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JLabel jLabel95;
    private javax.swing.JLabel jLabel96;
    private javax.swing.JLabel jLabel97;
    private javax.swing.JLabel jLabel98;
    private javax.swing.JLabel jLabel99;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JLayeredPane jLayeredPane3;
    private javax.swing.JLayeredPane jLayeredPane4;
    private javax.swing.JLayeredPane jLayeredPane5;
    private javax.swing.JLayeredPane jLayeredPane6;
    private javax.swing.JLayeredPane jLayeredPane7;
    private javax.swing.JLayeredPane jLayeredPane8;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenu jMenu3;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem11;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
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
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane14;
    private javax.swing.JScrollPane jScrollPane15;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator100;
    private javax.swing.JToolBar.Separator jSeparator102;
    private javax.swing.JToolBar.Separator jSeparator103;
    private javax.swing.JToolBar.Separator jSeparator104;
    private javax.swing.JToolBar.Separator jSeparator105;
    private javax.swing.JToolBar.Separator jSeparator107;
    private javax.swing.JToolBar.Separator jSeparator108;
    private javax.swing.JToolBar.Separator jSeparator109;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JToolBar.Separator jSeparator110;
    private javax.swing.JToolBar.Separator jSeparator111;
    private javax.swing.JToolBar.Separator jSeparator112;
    private javax.swing.JToolBar.Separator jSeparator115;
    private javax.swing.JToolBar.Separator jSeparator116;
    private javax.swing.JToolBar.Separator jSeparator117;
    private javax.swing.JToolBar.Separator jSeparator12;
    private javax.swing.JToolBar.Separator jSeparator13;
    private javax.swing.JToolBar.Separator jSeparator14;
    private javax.swing.JToolBar.Separator jSeparator15;
    private javax.swing.JToolBar.Separator jSeparator19;
    private javax.swing.JToolBar.Separator jSeparator20;
    private javax.swing.JToolBar.Separator jSeparator22;
    private javax.swing.JToolBar.Separator jSeparator23;
    private javax.swing.JToolBar.Separator jSeparator24;
    private javax.swing.JToolBar.Separator jSeparator25;
    private javax.swing.JToolBar.Separator jSeparator26;
    private javax.swing.JToolBar.Separator jSeparator27;
    private javax.swing.JToolBar.Separator jSeparator29;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JToolBar.Separator jSeparator30;
    private javax.swing.JSeparator jSeparator31;
    private javax.swing.JToolBar.Separator jSeparator32;
    private javax.swing.JPopupMenu.Separator jSeparator33;
    private javax.swing.JToolBar.Separator jSeparator34;
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
    private javax.swing.JToolBar.Separator jSeparator46;
    private javax.swing.JToolBar.Separator jSeparator47;
    private javax.swing.JToolBar.Separator jSeparator48;
    private javax.swing.JToolBar.Separator jSeparator49;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator50;
    private javax.swing.JToolBar.Separator jSeparator51;
    private javax.swing.JToolBar.Separator jSeparator52;
    private javax.swing.JToolBar.Separator jSeparator53;
    private javax.swing.JPopupMenu.Separator jSeparator54;
    private javax.swing.JToolBar.Separator jSeparator55;
    private javax.swing.JToolBar.Separator jSeparator56;
    private javax.swing.JToolBar.Separator jSeparator57;
    private javax.swing.JToolBar.Separator jSeparator58;
    private javax.swing.JToolBar.Separator jSeparator59;
    private javax.swing.JToolBar.Separator jSeparator6;
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
    private javax.swing.JToolBar.Separator jSeparator76;
    private javax.swing.JToolBar.Separator jSeparator77;
    private javax.swing.JToolBar.Separator jSeparator78;
    private javax.swing.JToolBar.Separator jSeparator79;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator80;
    private javax.swing.JToolBar.Separator jSeparator81;
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
    private javax.swing.JToolBar.Separator jSeparator99;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar10;
    private javax.swing.JToolBar jToolBar11;
    private javax.swing.JToolBar jToolBar12;
    private javax.swing.JToolBar jToolBar13;
    private javax.swing.JToolBar jToolBar14;
    private javax.swing.JToolBar jToolBar15;
    private javax.swing.JToolBar jToolBar16;
    private javax.swing.JToolBar jToolBar17;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JToolBar jToolBar6;
    private javax.swing.JToolBar jToolBar7;
    private javax.swing.JToolBar jToolBar8;
    private javax.swing.JToolBar jToolBar9;
    private javax.swing.JButton limpiarArticuloTxt;
    private javax.swing.JMenuItem listaArticulosMenuItem;
    private javax.swing.JTable listaContratosTabla;
    private javax.swing.JTable listaEquiposTabla;
    private javax.swing.JTextField marcaEquipoTxt;
    private javax.swing.JTextArea memoTxt;
    private javax.swing.JTextField modeloEquipoTxt;
    private javax.swing.JTextField nequiposTxt;
    private javax.swing.JTextField ninsumos;
    public javax.swing.JTextField nombreComercialClienteTxt;
    private javax.swing.JButton nuevTesoreriaButton;
    private javax.swing.JButton nuevaFacturaButton;
    private javax.swing.JButton nuevaFacturaButton1;
    private javax.swing.JButton nuevaFacturaButton2;
    private javax.swing.JButton nuevoApunteContableButton;
    private javax.swing.JButton nuevoArticuloButton;
    private javax.swing.JButton nuevoArticulobButton;
    private javax.swing.JButton nuevoClienteButton;
    private javax.swing.JButton nuevoClienteButton1;
    private javax.swing.JButton nuevoClienteButton2;
    private javax.swing.JButton nuevoClienteButton3;
    private javax.swing.JButton nuevoContratoButton;
    private javax.swing.JButton nuevoEquipoButton;
    private javax.swing.JButton nuevoProveedorButton;
    private javax.swing.JButton nuevoProveedorButton1;
    private javax.swing.JTextField numContratoTxt;
    private javax.swing.JTextField numSerieEquipoTxt;
    private javax.swing.JTextField numeroSerieTxt;
    private javax.swing.JTextField origenEquipoTxt;
    private javax.swing.JButton pagarFacturaButton;
    private javax.swing.JTextField pagoTxt;
    private javax.swing.JTextField pequipos;
    private javax.swing.JTextField pinsumos;
    public javax.swing.JTextField precioCosteTxt;
    public javax.swing.JTextField precioFinalTxt;
    public javax.swing.JTextField precioVentaTxt;
    private javax.swing.JTextField proveedorFacturaTxt;
    private javax.swing.JInternalFrame proveedorInternalFrame;
    public javax.swing.JTextField proveedorTxt;
    private javax.swing.JPanel proveedoresPanel;
    private javax.swing.JTable proveedoresTable;
    private javax.swing.JTextField recividoTxt1;
    private javax.swing.JMenuItem reporteFacturasCobradasMenuItem;
    private javax.swing.JTextField retencionTxt;
    public javax.swing.JTextField rucClienteTxt;
    private javax.swing.JTextField rucFacturaProveedorTxt;
    private javax.swing.JTextField rucProveedorTxt;
    private javax.swing.JTextField rucTxt;
    private javax.swing.JTextField serieFacturaProveedorTxt;
    public javax.swing.JTextField stockTxt;
    private javax.swing.JTextField subtotalTxt;
    private javax.swing.JTextField subtotalTxt1;
    public javax.swing.JTextField telefonoClienteTxt;
    private javax.swing.JTextField telefonoContratoTxt;
    private javax.swing.JTextField telefonoProveedorTxt;
    private javax.swing.JTextArea telefonoTextArea;
    private javax.swing.JTextField telefonoTxt;
    private javax.swing.JPanel tesoreriaPanel;
    private javax.swing.JTable tesoreriaTable;
    private javax.swing.JTextField tipoArticuloTxt;
    private javax.swing.JTextField tipoContratoTxt;
    private javax.swing.JTextField tipoEquipoTxt;
    private javax.swing.JTextField totalTxt;
    private javax.swing.JTextField totalTxt1;
    private javax.swing.JPanel ventasPanel;
    private javax.swing.JTable ventasTabla;
    private javax.swing.JTextField wwwTxt;
    // End of variables declaration//GEN-END:variables
}
