package controllers;

import DAO.ClientesDAO;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import models.*;
import views.*;

public class ClientesControlador implements MouseListener, ActionListener {

    frmClientes vistaClientes;
    frmVentas vistaVentas;
    Cliente cl;
    ClientesDAO clienteDAO;
    DefaultTableModel modeloClienteActivo = new DefaultTableModel();
    DefaultTableModel modeloClienteInactivo = new DefaultTableModel();

    public ClientesControlador(Cliente cl, ClientesDAO clienteDAO, frmClientes vistaClientes) {
        this.vistaClientes = vistaClientes;
        this.cl = cl;
        this.clienteDAO = clienteDAO;
        this.vistaClientes.btnregistrarcli.addActionListener(this);
        this.vistaClientes.btnmodificarcli.addActionListener(this);
        this.vistaClientes.btnnuevocli.addActionListener(this);
        this.vistaClientes.btnBuscarCliente.addActionListener(this);
        this.vistaClientes.JMenuEliminarCli.addActionListener(this);
        this.vistaClientes.TableMostrarClientes.addMouseListener(this);
        this.vistaClientes.JLabelCrearReg.addMouseListener(this);
        this.vistaClientes.JLabelUsuariosReg.addMouseListener(this);
        this.vistaClientes.JLabelSalirReg.addMouseListener(this);
        this.vistaClientes.cbTipoDocumento.addActionListener(this);
        this.vistaClientes.TableInactiveClientes.addMouseListener(this);
        this.vistaClientes.btnActivarCliente.addActionListener(this);
        this.vistaClientes.radioButtonTrueCliente.addActionListener(this);
        this.vistaClientes.radioButtonFalseCliente.addActionListener(this);
        listarClientes();
        listarClientesInactivo();
        this.vistaClientes.radioButtonFalseCliente.setSelected(true);
        this.vistaClientes.radioButtonTrueCliente.setSelected(false);
        this.vistaClientes.TableInactiveClientes.setDefaultEditor(Object.class, null);
        this.vistaClientes.TableMostrarClientes.setDefaultEditor(Object.class, null);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        //Llenar datos en el textField
        if (e.getSource() == vistaClientes.TableMostrarClientes) {
            int fila = vistaClientes.TableMostrarClientes.rowAtPoint(e.getPoint());
            vistaClientes.txtidcli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 0).toString());
            vistaClientes.cbTipoDocumento.setSelectedItem(vistaClientes.TableMostrarClientes.getValueAt(fila, 1));
            vistaClientes.txtdni.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 2).toString());
            vistaClientes.txtnombrecli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 3).toString());
            vistaClientes.txttelefonocli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 4).toString());
            vistaClientes.txadireccioncli.setText(vistaClientes.TableMostrarClientes.getValueAt(fila, 5).toString());
            vistaClientes.btnregistrarcli.setEnabled(false);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        if (e.getSource() == vistaClientes.TableInactiveClientes) {
            int fila = vistaClientes.TableInactiveClientes.rowAtPoint(e.getPoint());
            vistaClientes.txtActivarClienteId.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 0).toString());
            vistaClientes.txtActiveTipoDocumento.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 1).toString());
            vistaClientes.txtActiveNumeroDocumentoCliente.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 2).toString());
            vistaClientes.txtActiveNombreCliente.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 3).toString());
            vistaClientes.txtActiveTelefonoCliente.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 4).toString());
            vistaClientes.txtActiveDireccion.setText(vistaClientes.TableInactiveClientes.getValueAt(fila, 5).toString());
            vistaClientes.radioButtonFalseCliente.setSelected(true);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Formulario de clientes
        if (e.getSource() == vistaClientes.JLabelCrearReg) {
            vistaClientes.jTabbedPane1.setSelectedIndex(0);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Formulario de usuarios
        if (e.getSource() == vistaClientes.JLabelUsuariosReg) {
            Usuario usuarioActual = Session.getUsuarioActual();
            if (usuarioActual.getRol().equals("Usuario")) {
                vistaClientes.jTabbedPane1.setSelectedIndex(0);
            } else {
                vistaClientes.jTabbedPane1.setSelectedIndex(1);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        if (e.getSource() == vistaClientes.JLabelSalirReg) {
            new frmMenuPrincipal().setVisible(true);
            vistaClientes.dispose();
        }

    }

    @Override
    public void mousePressed(MouseEvent me) {
        ///////////////////////////////
    }

    @Override
    public void mouseReleased(MouseEvent me) {
        ///////////////////////////////
    }

    @Override
    public void mouseEntered(MouseEvent me) {
        //////////////////////////////
    }

    @Override
    public void mouseExited(MouseEvent me) {
        //////////////////////////////
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        //Registrar Cliente
        if (e.getSource() == vistaClientes.btnregistrarcli) {
            String nombreCliente = vistaClientes.txtnombrecli.getText();
            String numeroDocumentoCliente = vistaClientes.txtdni.getText();
            String telefonoCliente = vistaClientes.txttelefonocli.getText();
            String direccionCliente = vistaClientes.txadireccioncli.getText();
            String tipoDocumento = vistaClientes.cbTipoDocumento.getSelectedItem().toString();

            if (nombreCliente.isEmpty() || numeroDocumentoCliente.isEmpty() || telefonoCliente.isEmpty() || direccionCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Por favor complete todos los campos obligatorios.", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Validar campos con numeros, se ejecuta si devuelve false
            if (!validarCamposEnteros(numeroDocumentoCliente, telefonoCliente)) {
                return;
            }

            //Verificar el tipo de documento ingresado #DNI o #RUC
            if (tipoDocumento.equals("DNI")) {
                if (numeroDocumentoCliente.length() != 8) {
                    JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos");
                    return;
                }
            } else if (tipoDocumento.equals("RUC")) {
                if (!(numeroDocumentoCliente.startsWith("10") || numeroDocumentoCliente.startsWith("20")) || numeroDocumentoCliente.length() != 10) {
                    JOptionPane.showMessageDialog(null, "El RUC debe comenzar con '10' o '20' y tener 10 dígitos", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            int dni = Integer.parseInt(numeroDocumentoCliente);
            int telefono = Integer.parseInt(telefonoCliente);

            cl.setTipoDocumento(tipoDocumento);
            cl.setDni(dni);
            cl.setNombre(nombreCliente);
            cl.setTelefono(telefono);
            cl.setDireccion(direccionCliente);
            //Registro del cliente
            if (clienteDAO.registrarCliente(cl)) {
                limpiarTable(modeloClienteActivo);
                listarClientes();
                limpiar();
                JOptionPane.showMessageDialog(null, "Cliente Registrado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Actualizar informacion del cliente
        if (e.getSource() == vistaClientes.btnmodificarcli) {
            String idCliente = vistaClientes.txtidcli.getText();
            String nombreCliente = vistaClientes.txtnombrecli.getText();
            String dniCliente = vistaClientes.txtdni.getText();
            String telefonoCliente = vistaClientes.txttelefonocli.getText();
            String direccionCliente = vistaClientes.txadireccioncli.getText();
            String tipoDocumento = vistaClientes.cbTipoDocumento.getSelectedItem().toString();

            //Verificar que se selecciono una fila de la tabla
            if (idCliente.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila");
                return;
            }

            if (dniCliente.isEmpty() || tipoDocumento.isEmpty()) {
                JOptionPane.showMessageDialog(null, "El tipo y n° de documento son obligatorios", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Validar si se ingresan valores String o caracteres en campos numericos
            if (!validarCamposEnteros(dniCliente, telefonoCliente)) {
                return;
            }

            //Validacion para asignar el tipo de documento
            if (tipoDocumento.equals("DNI")) {
                if (dniCliente.length() != 8) {
                    JOptionPane.showMessageDialog(null, "El DNI debe tener 8 dígitos", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            } else if (tipoDocumento.equals("RUC")) {
                if (!(dniCliente.startsWith("10") || dniCliente.startsWith("20")) || dniCliente.length() != 10) {
                    JOptionPane.showMessageDialog(null, "El RUC debe comenzar con '10' o '20' y tener 10 dígitos", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            int idCli = Integer.parseInt(idCliente);
            int dni = Integer.parseInt(dniCliente);
            int telefono;
            if (telefonoCliente.isEmpty()) {
                telefono = 0; // Asignar un valor predeterminado de 0 cuando la cadena está vacía
            } else {
                telefono = Integer.parseInt(telefonoCliente); // Convertir la cadena a entero
            }

            //int telefono = Integer.parseInt(telefonoCliente);

            cl.setTipoDocumento(tipoDocumento);
            cl.setDni(dni);
            cl.setNombre(nombreCliente);
            cl.setTelefono(telefono);
            cl.setDireccion(direccionCliente);
            cl.setIdCliente(idCli);

            if (clienteDAO.actualizarCliente(cl)) {
                limpiarTable(modeloClienteActivo);
                listarClientes();
                limpiar();
                JOptionPane.showMessageDialog(null, "Los datos del cliente se actualizaron correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Buscar cliente
        if (e.getSource() == vistaClientes.btnBuscarCliente) {
            String dniClienteString = vistaClientes.txtbuscarcli.getText();

            //Verificar que el campo de N°Documento no se encuentre vacio
            if (dniClienteString.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Ingrese el DNI del cliente", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            //Validar los campos numericos, se ejecuta si devuelve false
            if (!validarCamposEnteros(dniClienteString)) {
                return;
            }

            int dniCliente = Integer.parseInt(dniClienteString);

            try {
                cl = clienteDAO.buscarCliente(dniCliente);
                if (cl.getDni() != 0) {
                    vistaClientes.txtnombrecli.setText(cl.getNombre());
                    vistaClientes.cbTipoDocumento.setSelectedItem(cl.getTipoDocumento());
                    vistaClientes.txtdni.setText(String.valueOf(cl.getDni()));
                    vistaClientes.txttelefonocli.setText(String.valueOf(cl.getTelefono()));
                    vistaClientes.txadireccioncli.setText(cl.getDireccion());
                    vistaClientes.txtidcli.setText(String.valueOf(cl.getIdCliente()));
                    vistaClientes.btnregistrarcli.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "No existe el DNI ingresado", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ex.getMessage());
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Nuevo Cliente, limpiar todos los datos del formulario
        if (e.getSource() == vistaClientes.btnnuevocli) {
            limpiar();
            vistaClientes.btnregistrarcli.setEnabled(true);
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Eliminar Cliente
        if (e.getSource() == vistaClientes.JMenuEliminarCli) {
            String idClienteStr = vistaClientes.txtidcli.getText();

            if (idClienteStr.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Selecciona una fila con el cliente");
                return;
            }

            int idCliente = Integer.parseInt(idClienteStr);
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas eliminar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (clienteDAO.eliminarCliente(idCliente)) {
                    limpiar();
                    limpiarTable(modeloClienteActivo);
                    limpiarTable(modeloClienteInactivo);
                    listarClientes();
                    listarClientesInactivo();
                    JOptionPane.showMessageDialog(null, "Cliente eliminado", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al eliminar el cliente");
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Activar el cliente, cambio de estado a "ACTIVO"
        if (e.getSource() == vistaClientes.btnActivarCliente) {
            if (!vistaClientes.radioButtonTrueCliente.isSelected()) {
                JOptionPane.showMessageDialog(null, "Seleccione la opcion de 'ACTIVO' para continuar", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (vistaClientes.radioButtonFalseCliente.isSelected()) {
                JOptionPane.showMessageDialog(null, "La opcion de INACTIVO no debe estar seleccionada", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return;
            }

            int idCliente = Integer.parseInt(vistaClientes.txtActivarClienteId.getText());
            int respuesta = JOptionPane.showConfirmDialog(null, "¿Estás seguro de que deseas activar el cliente?", "Confirmación", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                if (clienteDAO.activarCliente(idCliente)) {
                    limpiarTable(modeloClienteInactivo);
                    limpiarTable(modeloClienteActivo);
                    listarClientes();
                    listarClientesInactivo();
                    limpiarClienteInactivo();
                    JOptionPane.showMessageDialog(null, "El cliente se activo correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }

        //------------------------------------------------------------------------------------------------------------------------------------------
        //Control de evento para el radio button
        if (e.getSource() == vistaClientes.radioButtonTrueCliente) {
            vistaClientes.radioButtonFalseCliente.setSelected(false);
        }
        if (e.getSource() == vistaClientes.radioButtonFalseCliente) {
            vistaClientes.radioButtonTrueCliente.setSelected(false);
        }

    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void listarClientes() {
        List<Cliente> lista = clienteDAO.listarClientes();
        modeloClienteActivo = (DefaultTableModel) vistaClientes.TableMostrarClientes.getModel();
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getTipoDocumento();
            obj[2] = lista.get(i).getDni();
            obj[3] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[4] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[5] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modeloClienteActivo.addRow(obj);
        }
        vistaClientes.TableMostrarClientes.setModel(modeloClienteActivo);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void listarClientesInactivo() {
        List<Cliente> lista = clienteDAO.listarClientesInactivo();
        modeloClienteInactivo = (DefaultTableModel) vistaClientes.TableInactiveClientes.getModel();
        Object[] obj = new Object[6];
        for (int i = 0; i < lista.size(); i++) {
            obj[0] = lista.get(i).getIdCliente();
            obj[1] = lista.get(i).getTipoDocumento();
            obj[2] = lista.get(i).getDni();
            obj[3] = lista.get(i).getNombre() != null ? lista.get(i).getNombre() : "";
            obj[4] = lista.get(i).getTelefono() != 0 ? lista.get(i).getTelefono() : "";
            obj[5] = lista.get(i).getDireccion() != null ? lista.get(i).getDireccion() : "";
            modeloClienteInactivo.addRow(obj);
        }
        vistaClientes.TableInactiveClientes.setModel(modeloClienteInactivo);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiar() {
        vistaClientes.txtidcli.setText(null);
        vistaClientes.txtdni.setText(null);
        vistaClientes.txtnombrecli.setText(null);
        vistaClientes.txttelefonocli.setText(null);
        vistaClientes.txadireccioncli.setText(null);
        vistaClientes.txtbuscarcli.setText(null);
    }

    public void limpiarClienteInactivo() {
        vistaClientes.txtActivarClienteId.setText(null);
        vistaClientes.txtActiveNombreCliente.setText(null);
        vistaClientes.txtActiveNumeroDocumentoCliente.setText(null);
        vistaClientes.txtActiveTipoDocumento.setText(null);
        vistaClientes.txtActiveTelefonoCliente.setText(null);
        vistaClientes.txtActiveDireccion.setText(null);
    }

    //------------------------------------------------------------------------------------------------------------------------------------------
    public void limpiarTable(DefaultTableModel modelo) {
        for (int i = 0; i < modelo.getRowCount(); i++) {
            modelo.removeRow(i);
            i = i - 1;
        }
    }

    //-------------------------------------------------------------------------------------------------------------------------------------------
    public boolean validarCamposEnteros(String... valores) {
        for (String valor : valores) {

            if (valor.isEmpty()) {
                continue; // Saltar a la siguiente iteración si el valor está vacío
            }

            try {
                Long.parseLong(valor.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "El valor ingresado  '" + valor + "'  no puede contener letras o caracteres", "Campos incompletos", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        return true;
    }

}
