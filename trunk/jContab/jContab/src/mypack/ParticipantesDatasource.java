package mypack;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * @author jacg
 */
public class ParticipantesDatasource implements JRDataSource {

    private int indiceParticipanteActual = -1;
    private List<Participante> listaParticipantes = new ArrayList<Participante>();

    
    public Object getFieldValue(JRField  jrField) throws JRException {
        Object valor = null;

        if("fechaEmision".equals(jrField.getName()))
        {
            valor = listaParticipantes.get(indiceParticipanteActual).getFechaEmision();
        } else if("cliente".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getCliente();
        } else if("ruc".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getRuc();
        } else if("direccion".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getDireccion();
        } else if("telefono".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getTelefono();
        } else if("cantidad".equals(jrField.getName())) {
            valor =listaParticipantes.get(indiceParticipanteActual).getCantidad();
        } else if("articulo".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getArticulo();
        } else if("pUnitario".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getpUnitario();
        } else if("pTotal".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getpTotal();
        } else if("subtotal".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getSubtotal();
        } else if("iva".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getIva();
        } else if("total".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getTotal();
        } else if("ciudad".equals(jrField.getName())) {
            valor = listaParticipantes.get(indiceParticipanteActual).getCiudad();
        }

        return valor;
    }

    public boolean next() throws JRException {
        return ++indiceParticipanteActual < listaParticipantes.size();
    }

    public void addParticipante(Participante participante)
    {
        this.listaParticipantes.add(participante);
    }



}
