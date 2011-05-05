/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Contab.java
 *
 * Created on May 1, 2011, 10:01:05 PM
 * 
 * Correccion de vector to arraylist
 * Mejoras de interfaz
 * Codigo mas limpio
 * Mas estabilidad y nuevas caracteristicas
 * 
 */
package mypack;


import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
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
import java.util.ArrayList;
import javax.swing.JCheckBox;
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
    BufferedImage image = null;
    boolean is_Driver = false;
    String baseDatos = "jdbc:mysql://localhost:3306/empresa?"+"user=root&password=treky5";
    
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
        
        SimpleDateFormat formato = new SimpleDateFormat("yyyy", new Locale("ES", "EC"));
        Date fechaActual = new Date();
        String hoy = formato.format(fechaActual);
        buscarFechaTxt.setText(hoy);
        
        leerClientes("Cliente", "");
        actualizaListaClientesTablaAnchos();
        seleccionClientesTabla();
        
        
        
        String find1;
        if(fechaBuscarRadioButton.isSelected()){
            
            find1 = buscarFechaTxt.getText();
        }else{
            find1 = "";
        }
        leerClientesFactura("Cliente", "", find1);
        actualizaListaFacturasClientesTablaAnchos();
        
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
                                "Fuente",
                                "Recivido",
                                "Nota"};
        ArrayList data1 = new ArrayList();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
                
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM Facturas WHERE " + findClave + " LIKE '%" + (String)find+"%'" + "AND Fecha"+ " LIKE '%" + (String)findClave1 +"%'");
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
                Double res = 0.00;
                Double res1 = 0.00;
                Double res2 = 0.00;
                Double res3 = 0.00;
                Double temp = 0.00;
                for (int i = 0, rows =listaFacturaClientesTabla.getRowCount(); i < rows; i++)
                { 
                    iva = (String) listaFacturaClientesTabla.getValueAt(i, 5).toString();
                    temp =  Double.valueOf(iva);
                    res = res + temp;
                    
                    total = (String) listaFacturaClientesTabla.getValueAt(i, 3).toString();
                    temp =  Double.valueOf(total);
                    res1 = res1 + temp;
                    
                    fuente = (String) listaFacturaClientesTabla.getValueAt(i, 3).toString();
                    temp =  Double.valueOf(fuente);
                    res2 = res2 + temp;
                    
                    recivido = (String) listaFacturaClientesTabla.getValueAt(i, 7).toString();
                    temp =  Double.valueOf(recivido);
                    res3 = res3 + temp;
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
    void actualizaListaArticulosTablaAnchos(){
        listaArticulosTabla.getColumnModel().getColumn(0).setPreferredWidth(60);
        listaArticulosTabla.getColumnModel().getColumn(1).setPreferredWidth(420);
        listaArticulosTabla.getColumnModel().getColumn(2).setPreferredWidth(46);
        listaArticulosTabla.getColumnModel().getColumn(3).setPreferredWidth(95);
        listaArticulosTabla.getColumnModel().getColumn(0).setCellRenderer(new ColorTableCellRenderer());
        listaArticulosTabla.getColumnModel().getColumn(1).setCellRenderer(new TextAreaRenderer());
        listaArticulosTabla.getColumnModel().getColumn(2).setCellRenderer(new ColorTableCellRenderer());
        listaArticulosTabla.getColumnModel().getColumn(3).setCellRenderer(new ColorTableCellRenderer());
        listaArticulosTabla.setRowHeight(70);
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
                    //setBackground(Color.lightGray);
                    setBackground(new Color(153, 204, 255));
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
                    setHorizontalAlignment(JLabel.RIGHT);
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
                    setBackground(new Color(153, 204, 255));
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
                setBackground(new Color(153, 204, 255));
            }
            setFont(new Font("Dialog", Font.PLAIN, 12));
            setText((String)obj);

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
                setBackground(new Color(153, 204, 255));
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

        jLabel1 = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jSeparator4 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        jSeparator5 = new javax.swing.JToolBar.Separator();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToolBar2 = new javax.swing.JToolBar();
        nuevoClienteButton = new javax.swing.JButton();
        borrarFichaClienteButton = new javax.swing.JButton();
        guardarClienteButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        crearFacturaClienteButton = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        buscarClienteComboBox = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        buscarClienteTxt = new javax.swing.JTextField();
        clearBusquedaTxt = new javax.swing.JButton();
        jSeparator15 = new javax.swing.JToolBar.Separator();
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
        subtotalTxt = new javax.swing.JTextField();
        jLabel49 = new javax.swing.JLabel();
        jLabel54 = new javax.swing.JLabel();
        ivat12Txt = new javax.swing.JTextField();
        totalTxt = new javax.swing.JTextField();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        ivabaseTxt = new javax.swing.JTextField();
        fuenteBaseTxt = new javax.swing.JTextField();
        jLabel34 = new javax.swing.JLabel();
        numeroSerieTxt = new javax.swing.JTextField();
        jLabel58 = new javax.swing.JLabel();
        ivaPorTxt = new javax.swing.JTextField();
        cobradoTxt2 = new javax.swing.JTextField();
        fuentePorTxt = new javax.swing.JTextField();
        cobradoTxt4 = new javax.swing.JTextField();
        jLabel59 = new javax.swing.JLabel();
        jLabel60 = new javax.swing.JLabel();
        jLabel61 = new javax.swing.JLabel();
        jLabel62 = new javax.swing.JLabel();
        retencionTxt = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        retencionTxt1 = new javax.swing.JTextField();
        jToolBar3 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        buscarFacturaClienteTxt = new javax.swing.JTextField();
        cleanFacturaButton = new javax.swing.JButton();
        jSeparator18 = new javax.swing.JToolBar.Separator();
        jSeparator21 = new javax.swing.JToolBar.Separator();
        jSeparator20 = new javax.swing.JToolBar.Separator();
        jSeparator23 = new javax.swing.JToolBar.Separator();
        jSeparator22 = new javax.swing.JToolBar.Separator();
        fechaBuscarRadioButton = new javax.swing.JRadioButton();
        buscarFechaTxt = new javax.swing.JTextField();
        jSeparator19 = new javax.swing.JToolBar.Separator();
        jScrollPane5 = new javax.swing.JScrollPane();
        listaFacturaClientesTabla = new javax.swing.JTable();
        jToolBar4 = new javax.swing.JToolBar();
        jSeparator17 = new javax.swing.JToolBar.Separator();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jSeparator8 = new javax.swing.JToolBar.Separator();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jSeparator12 = new javax.swing.JToolBar.Separator();
        jSeparator13 = new javax.swing.JToolBar.Separator();
        jSeparator25 = new javax.swing.JToolBar.Separator();
        jSeparator27 = new javax.swing.JToolBar.Separator();
        jSeparator24 = new javax.swing.JToolBar.Separator();
        jSeparator28 = new javax.swing.JToolBar.Separator();
        jSeparator29 = new javax.swing.JToolBar.Separator();
        jSeparator31 = new javax.swing.JToolBar.Separator();
        jSeparator30 = new javax.swing.JToolBar.Separator();
        calucularFacturaButton = new javax.swing.JButton();
        jSeparator26 = new javax.swing.JToolBar.Separator();
        jSeparator14 = new javax.swing.JToolBar.Separator();
        jSeparator16 = new javax.swing.JToolBar.Separator();
        jButton8 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        ivaTxt = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        fuenteTxt = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        recividoTxt = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        ivaTxt1 = new javax.swing.JTextField();
        cobrarFacturaButton1 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jToolBar5 = new javax.swing.JToolBar();
        jSeparator32 = new javax.swing.JToolBar.Separator();
        jLabel5 = new javax.swing.JLabel();
        buscarArticuloTxt = new javax.swing.JTextField();
        jButton9 = new javax.swing.JButton();
        jSeparator37 = new javax.swing.JToolBar.Separator();
        jSeparator36 = new javax.swing.JToolBar.Separator();
        jSeparator35 = new javax.swing.JToolBar.Separator();
        jSeparator34 = new javax.swing.JToolBar.Separator();
        jSeparator33 = new javax.swing.JToolBar.Separator();
        jScrollPane3 = new javax.swing.JScrollPane();
        listaArticulosTabla = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/arcusmedicalogocolores.png"))); // NOI18N

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.add(jSeparator4);

        jLabel2.setFont(new java.awt.Font("Purisa", 3, 18)); // NOI18N
        jLabel2.setText("jContab - V3.0");
        jLabel2.setPreferredSize(new java.awt.Dimension(109, 22));
        jToolBar1.add(jLabel2);
        jToolBar1.add(jSeparator5);
        jToolBar1.add(jSeparator6);

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/media-floppy.png"))); // NOI18N
        jButton1.setToolTipText("Guardar base de datos");
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton1);

        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-export-table.png"))); // NOI18N
        jButton2.setToolTipText("Restaurar base de datos");
        jButton2.setFocusable(false);
        jButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton2);

        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/documentinfo.png"))); // NOI18N
        jButton3.setToolTipText("Informacion");
        jButton3.setFocusable(false);
        jButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jButton3);

        jToolBar2.setFloatable(false);
        jToolBar2.setRollover(true);
        jToolBar2.setBorderPainted(false);

        nuevoClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-add.png"))); // NOI18N
        nuevoClienteButton.setToolTipText("Nuevo cliente");
        nuevoClienteButton.setFocusable(false);
        nuevoClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        nuevoClienteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        nuevoClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nuevoClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(nuevoClienteButton);

        borrarFichaClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        borrarFichaClienteButton.setToolTipText("Borrar cliente");
        borrarFichaClienteButton.setFocusable(false);
        borrarFichaClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        borrarFichaClienteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        borrarFichaClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarFichaClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(borrarFichaClienteButton);

        guardarClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        guardarClienteButton.setToolTipText("Guardar cliente");
        guardarClienteButton.setFocusable(false);
        guardarClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        guardarClienteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        guardarClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardarClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(guardarClienteButton);
        jToolBar2.add(jSeparator1);

        crearFacturaClienteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png"))); // NOI18N
        crearFacturaClienteButton.setToolTipText("Crear factura");
        crearFacturaClienteButton.setFocusable(false);
        crearFacturaClienteButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        crearFacturaClienteButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        crearFacturaClienteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                crearFacturaClienteButtonActionPerformed(evt);
            }
        });
        jToolBar2.add(crearFacturaClienteButton);
        jToolBar2.add(jSeparator2);
        jToolBar2.add(jSeparator3);

        buscarClienteComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cliente", "RUC", "Organizacion" }));
        jToolBar2.add(buscarClienteComboBox);

        jLabel4.setText("Buscar:");
        jToolBar2.add(jLabel4);

        buscarClienteTxt.setFont(new java.awt.Font("Dialog", 1, 12));
        buscarClienteTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarClienteTxtCaretUpdate(evt);
            }
        });
        jToolBar2.add(buscarClienteTxt);

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
        jToolBar2.add(jSeparator15);

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

        clienteTxt.setEditable(false);

        RUCTxt.setEditable(false);

        nombreComercialTxt.setEditable(false);

        fechaClienteTxt.setEditable(false);
        fechaClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ciudadClienteTxt.setEditable(false);
        ciudadClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        direccionClienteTxt.setEditable(false);

        telefonoClienteTxt.setEditable(false);
        telefonoClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        emailClienteTxt.setEditable(false);
        emailClienteTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(idClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(clienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(RUCTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nombreComercialTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel15)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(direccionClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel16)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(telefonoClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel17)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel14))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addComponent(ciudadClienteTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE))
                            .addComponent(fechaClienteTxt, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 396, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(idClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(clienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(RUCTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(nombreComercialTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(fechaClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(ciudadClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(direccionClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(telefonoClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(emailClienteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
            .addComponent(jToolBar2, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

        clienteFacturaTxt.setEditable(false);
        clienteFacturaTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        rucTxt.setEditable(false);
        rucTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        direccionTxt.setEditable(false);
        direccionTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        fechaTxt.setEditable(false);
        fechaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fechaTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        ciudadTxt.setEditable(false);
        ciudadTxt.setFont(new java.awt.Font("Dialog", 0, 10));

        telefonoTxt.setEditable(false);
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

        subtotalTxt.setEditable(false);
        subtotalTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        subtotalTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel49.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel49.setText("Subtotal $");

        jLabel54.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel54.setText("I.V.A. T. 12% $");

        ivat12Txt.setEditable(false);
        ivat12Txt.setFont(new java.awt.Font("Dialog", 0, 10));
        ivat12Txt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        totalTxt.setBackground(new java.awt.Color(255, 255, 51));
        totalTxt.setEditable(false);
        totalTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        totalTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel55.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel55.setText("Total US $");

        jLabel56.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel56.setText("I.V.A.:");

        jLabel57.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel57.setText("Fuente:");

        ivabaseTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        ivabaseTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        ivabaseTxt.setText("0.00");
        ivabaseTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ivabaseTxtCaretUpdate(evt);
            }
        });

        fuenteBaseTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fuenteBaseTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        fuenteBaseTxt.setText("0.00");

        jLabel34.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel34.setText("Nº Serie:");

        numeroSerieTxt.setEditable(false);
        numeroSerieTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        numeroSerieTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        jLabel58.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel58.setText("RENTENCIONES:");

        ivaPorTxt.setFont(new java.awt.Font("Dialog", 0, 10)); // NOI18N
        ivaPorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        ivaPorTxt.setText("1");
        ivaPorTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                ivaPorTxtCaretUpdate(evt);
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

        fuentePorTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        fuentePorTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fuentePorTxt.setText("30");
        fuentePorTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                fuentePorTxtCaretUpdate(evt);
            }
        });

        cobradoTxt4.setFont(new java.awt.Font("Dialog", 0, 10));
        cobradoTxt4.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        cobradoTxt4.setText("0.00");
        cobradoTxt4.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                cobradoTxt4CaretUpdate(evt);
            }
        });

        jLabel59.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel59.setText("base");

        jLabel60.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel60.setText("%");

        jLabel61.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel61.setText("retenido");

        jLabel62.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel62.setText("Retencion $");

        retencionTxt.setEditable(false);
        retencionTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        retencionTxt.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel63.setFont(new java.awt.Font("Dialog", 1, 10));
        jLabel63.setText("Recivido $");

        retencionTxt1.setEditable(false);
        retencionTxt1.setFont(new java.awt.Font("Dialog", 0, 10));
        retencionTxt1.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 460, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel22)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(idFacturaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel23)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(clienteFacturaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel24)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(rucTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 178, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(direccionTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel34)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(numeroSerieTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel32)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(telefonoTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fechaTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 100, Short.MAX_VALUE))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(jLabel31)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(ciudadTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel58)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel57)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(fuenteBaseTxt))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel56)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel59)
                                            .addComponent(ivabaseTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(fuentePorTxt, javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel60)
                                    .addComponent(ivaPorTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(cobradoTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel61))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 59, Short.MAX_VALUE)
                                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel54, javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(jLabel55, javax.swing.GroupLayout.Alignment.TRAILING)))
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(cobradoTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 76, Short.MAX_VALUE)
                                        .addComponent(jLabel62))
                                    .addComponent(jLabel63, javax.swing.GroupLayout.Alignment.TRAILING)))
                            .addComponent(jLabel49))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(retencionTxt1, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .addComponent(totalTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(ivat12Txt, javax.swing.GroupLayout.Alignment.TRAILING)
                                .addComponent(subtotalTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(retencionTxt, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
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
                            .addComponent(telefonoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
                            .addComponent(direccionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(subtotalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel58))
                    .addComponent(jLabel49))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ivat12Txt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel54))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel55)
                            .addComponent(totalTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel62)
                            .addComponent(retencionTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel59)
                            .addComponent(jLabel60)
                            .addComponent(jLabel61))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel56)
                            .addComponent(ivabaseTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ivaPorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cobradoTxt2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel57)
                            .addComponent(fuenteBaseTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(fuentePorTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cobradoTxt4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel63)
                    .addComponent(retencionTxt1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        jLabel3.setText("Buscar factura:");
        jToolBar3.add(jLabel3);

        buscarFacturaClienteTxt.setMinimumSize(new java.awt.Dimension(4, 39));
        buscarFacturaClienteTxt.setPreferredSize(new java.awt.Dimension(124, 39));
        buscarFacturaClienteTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFacturaClienteTxtCaretUpdate(evt);
            }
        });
        jToolBar3.add(buscarFacturaClienteTxt);

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
        jToolBar3.add(jSeparator18);
        jToolBar3.add(jSeparator21);
        jToolBar3.add(jSeparator20);
        jToolBar3.add(jSeparator23);
        jToolBar3.add(jSeparator22);

        fechaBuscarRadioButton.setFont(new java.awt.Font("Dialog", 1, 10)); // NOI18N
        fechaBuscarRadioButton.setSelected(true);
        fechaBuscarRadioButton.setText("Fecha");
        fechaBuscarRadioButton.setFocusable(false);
        fechaBuscarRadioButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        fechaBuscarRadioButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(fechaBuscarRadioButton);

        buscarFechaTxt.setFont(new java.awt.Font("Dialog", 0, 10));
        buscarFechaTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarFechaTxtCaretUpdate(evt);
            }
        });
        jToolBar3.add(buscarFechaTxt);
        jToolBar3.add(jSeparator19);

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
        jScrollPane5.setViewportView(listaFacturaClientesTabla);

        jToolBar4.setFloatable(false);
        jToolBar4.setRollover(true);
        jToolBar4.add(jSeparator17);

        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/list-remove.png"))); // NOI18N
        jButton4.setToolTipText("Borrar factura");
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jButton4);

        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/document-save.png"))); // NOI18N
        jButton5.setToolTipText("Guardar factura");
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jButton5);
        jToolBar4.add(jSeparator10);
        jToolBar4.add(jSeparator11);
        jToolBar4.add(jSeparator9);
        jToolBar4.add(jSeparator8);
        jToolBar4.add(jSeparator7);

        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/insert-table.png"))); // NOI18N
        jButton6.setToolTipText("Agregar un artículo a la factura");
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jButton6);

        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-table-delete-row.png"))); // NOI18N
        jButton7.setToolTipText("Borrar un artículo de la factura");
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jButton7);
        jToolBar4.add(jSeparator12);
        jToolBar4.add(jSeparator13);
        jToolBar4.add(jSeparator25);
        jToolBar4.add(jSeparator27);
        jToolBar4.add(jSeparator24);
        jToolBar4.add(jSeparator28);
        jToolBar4.add(jSeparator29);
        jToolBar4.add(jSeparator31);
        jToolBar4.add(jSeparator30);

        calucularFacturaButton.setBackground(new java.awt.Color(255, 255, 51));
        calucularFacturaButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/accessories-calculator.png"))); // NOI18N
        calucularFacturaButton.setToolTipText("Computar factura");
        calucularFacturaButton.setFocusable(false);
        calucularFacturaButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        calucularFacturaButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        calucularFacturaButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                calucularFacturaButtonActionPerformed(evt);
            }
        });
        jToolBar4.add(calucularFacturaButton);
        jToolBar4.add(jSeparator26);
        jToolBar4.add(jSeparator14);
        jToolBar4.add(jSeparator16);

        jButton8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/printer.png"))); // NOI18N
        jButton8.setToolTipText("Imprimir factura");
        jButton8.setFocusable(false);
        jButton8.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton8.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar4.add(jButton8);

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

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ivaTxt))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fuenteTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(recividoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel18)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ivaTxt1)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(recividoTxt, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(52, 52, 52))
        );

        cobrarFacturaButton1.setFont(new java.awt.Font("Dialog", 1, 10));
        cobrarFacturaButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/dialog-ok-apply.png"))); // NOI18N
        cobrarFacturaButton1.setText("Cobrar factura");
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
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(66, 66, 66)
                                .addComponent(cobrarFacturaButton1))))
                    .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 760, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 736, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar4, javax.swing.GroupLayout.DEFAULT_SIZE, 760, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar4, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(82, 82, 82)
                        .addComponent(cobrarFacturaButton1))
                    .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31))
        );

        jTabbedPane1.addTab("Facturas venta", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png")), jPanel2); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 762, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Proveedores", jPanel6);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 762, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 604, Short.MAX_VALUE)
        );

        jTabbedPane1.addTab("Facturas compras", new javax.swing.ImageIcon(getClass().getResource("/mypack/view-file-columns.png")), jPanel4); // NOI18N

        jToolBar5.setFloatable(false);
        jToolBar5.setRollover(true);
        jToolBar5.add(jSeparator32);

        jLabel5.setText("Buscar artículo:");
        jToolBar5.add(jLabel5);

        buscarArticuloTxt.addCaretListener(new javax.swing.event.CaretListener() {
            public void caretUpdate(javax.swing.event.CaretEvent evt) {
                buscarArticuloTxtCaretUpdate(evt);
            }
        });
        jToolBar5.add(buscarArticuloTxt);

        jButton9.setIcon(new javax.swing.ImageIcon(getClass().getResource("/mypack/edit-clear-locationbar-rtl.png"))); // NOI18N
        jButton9.setToolTipText("Limpiar busqueda");
        jButton9.setFocusable(false);
        jButton9.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton9.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar5.add(jButton9);
        jToolBar5.add(jSeparator37);
        jToolBar5.add(jSeparator36);
        jToolBar5.add(jSeparator35);
        jToolBar5.add(jSeparator34);
        jToolBar5.add(jSeparator33);

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

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar5, javax.swing.GroupLayout.DEFAULT_SIZE, 762, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 738, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addComponent(jToolBar5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(370, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Articulos", jPanel8);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 767, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 631, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                                "R.U.C.",
                                "Telefono",
                                "Ciudad"};
        ArrayList data1 = new ArrayList();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        conectarBD(baseDatos);
        try {
            statement = connection.createStatement();
            rs = statement.executeQuery("SELECT * FROM Clientes WHERE " + findClave + " LIKE '%" + (String)find+"%'");
            while(rs.next()){
                Object[] row = new Object[5];
                row[0] = rs.getObject("id");
                row[1] = rs.getObject("Cliente");
                row[2] = rs.getObject("RUC");
                row[3] = rs.getObject("Telefono");
                row[4] = rs.getObject("Ciudad");               
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
        //leerUltimaFactura();
}//GEN-LAST:event_crearFacturaClienteButtonActionPerformed

    private void ivabaseTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_ivabaseTxtCaretUpdate
        // TODO add your handling code here
    }//GEN-LAST:event_ivabaseTxtCaretUpdate

    private void cobrarFacturaButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cobrarFacturaButton1ActionPerformed
        // TODO add your handling code here:
        
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
        
        
    }//GEN-LAST:event_calucularFacturaButtonActionPerformed

    private void buscarArticuloTxtCaretUpdate(javax.swing.event.CaretEvent evt) {//GEN-FIRST:event_buscarArticuloTxtCaretUpdate
        // TODO add your handling code here:
        String buscar = buscarArticuloTxt.getText();
        
        leerArticulos(buscar);
        actualizaListaArticulosTablaAnchos();
}//GEN-LAST:event_buscarArticuloTxtCaretUpdate

    /*
     * LEE ARTICULO EN BASE DE DATOS, con criterio de busqueda
     */
    void leerArticulos(String find){
        listaArticulosTabla.removeAll();
        listaArticulosTabla.updateUI();
        String[] columnNames = {"Código",
                                "Articulo",
                                "Stock",
                                "P.V.P"};
        ArrayList data1 = new ArrayList();
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);
        
        //si esta presente driver guarda informacion
        if(flagDriver == 1){
            conectarBD(baseDatos);
            try {
                statement = connection.createStatement();
                rs = statement.executeQuery("SELECT * FROM articulos WHERE " + "descripcion" + " LIKE '%" + (String)find+"%'");
                while(rs.next()){
                    Object[] row = new Object[4];
                    row[0] = rs.getObject("codarticulo");
                    row[1] = rs.getObject("descripcion");
                    row[2] = rs.getObject("stock");
                    row[3] = rs.getObject("pvp");              
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
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                new Contab().setVisible(true);
            }
        });
    }
    
    
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField RUCTxt;
    private javax.swing.JButton borrarFichaClienteButton;
    private javax.swing.JTextField buscarArticuloTxt;
    private javax.swing.JComboBox buscarClienteComboBox;
    private javax.swing.JTextField buscarClienteTxt;
    private javax.swing.JTextField buscarFacturaClienteTxt;
    private javax.swing.JTextField buscarFechaTxt;
    private javax.swing.JButton calucularFacturaButton;
    private javax.swing.JTextField ciudadClienteTxt;
    private javax.swing.JTextField ciudadTxt;
    private javax.swing.JButton cleanFacturaButton;
    private javax.swing.JButton clearBusquedaTxt;
    private javax.swing.JTextField clienteFacturaTxt;
    private javax.swing.JTextField clienteTxt;
    private javax.swing.JTable clientesTable;
    private javax.swing.JTextField cobradoTxt2;
    private javax.swing.JTextField cobradoTxt4;
    private javax.swing.JButton cobrarFacturaButton1;
    private javax.swing.JButton crearFacturaClienteButton;
    private javax.swing.JTextField direccionClienteTxt;
    private javax.swing.JTextField direccionTxt;
    private javax.swing.JTextField emailClienteTxt;
    private javax.swing.JTable facturaArticulosClienteTable;
    private javax.swing.JRadioButton fechaBuscarRadioButton;
    private javax.swing.JTextField fechaClienteTxt;
    private javax.swing.JTextField fechaTxt;
    private javax.swing.JTextField fuenteBaseTxt;
    private javax.swing.JTextField fuentePorTxt;
    private javax.swing.JTextField fuenteTxt;
    private javax.swing.JButton guardarClienteButton;
    private javax.swing.JTextField idClienteTxt;
    private javax.swing.JTextField idFacturaTxt;
    private javax.swing.JTextField ivaPorTxt;
    private javax.swing.JTextField ivaTxt;
    private javax.swing.JTextField ivaTxt1;
    private javax.swing.JTextField ivabaseTxt;
    private javax.swing.JTextField ivat12Txt;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
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
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
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
    private javax.swing.JToolBar.Separator jSeparator33;
    private javax.swing.JToolBar.Separator jSeparator34;
    private javax.swing.JToolBar.Separator jSeparator35;
    private javax.swing.JToolBar.Separator jSeparator36;
    private javax.swing.JToolBar.Separator jSeparator37;
    private javax.swing.JToolBar.Separator jSeparator4;
    private javax.swing.JToolBar.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JToolBar.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar2;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JToolBar jToolBar4;
    private javax.swing.JToolBar jToolBar5;
    private javax.swing.JTable listaArticulosTabla;
    private javax.swing.JTable listaFacturaClientesTabla;
    private javax.swing.JTextField nombreComercialTxt;
    private javax.swing.JButton nuevoClienteButton;
    private javax.swing.JTextField numeroSerieTxt;
    private javax.swing.JTextField recividoTxt;
    private javax.swing.JTextField retencionTxt;
    private javax.swing.JTextField retencionTxt1;
    private javax.swing.JTextField rucTxt;
    private javax.swing.JTextField subtotalTxt;
    private javax.swing.JTextField telefonoClienteTxt;
    private javax.swing.JTextField telefonoTxt;
    private javax.swing.JTextField totalTxt;
    // End of variables declaration//GEN-END:variables
}