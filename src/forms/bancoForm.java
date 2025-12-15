package forms;

import model.DBConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class bancoForm extends JFrame{
    private JLabel lblSaldo;
    private JPanel mainPanel;
    private JButton btnDeposito;
    private JButton SALIRButton;
    private JButton btnRetiro;
    private JButton btnTransferencia;
    private JTextArea txtHistorial;
    private double saldo = 0.0;
    private ArrayList<String> listaTransacciones = new ArrayList<>();
    private long userId;

    // Constructor por defecto (mantener compatibilidad con UI builder / Main)
    public bancoForm() {
        this(0L);
    }

    public bancoForm(long userId) {
        this.userId = userId;
        setTitle("Banco");
        setSize(520, 250);
        setContentPane(mainPanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);


        if (this.userId == 0L) {
            try (Connection conn = DBConnection.getConnection()) {
                String selUser = "SELECT id FROM users ORDER BY id LIMIT 1";
                try (PreparedStatement psu = conn.prepareStatement(selUser)) {
                    try (ResultSet rsu = psu.executeQuery()) {
                        if (rsu.next()) {
                            this.userId = rsu.getLong("id");
                        } else {
                            JOptionPane.showMessageDialog(null, "No existen usuarios en la base de datos. Cree un usuario primero.");
                            return;
                        }
                    }
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error al obtener usuario por defecto: " + ex.getMessage());
                return;
            }
        }


        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, balance FROM accounts WHERE user_id = ? ORDER BY id LIMIT 1";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setLong(1, this.userId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        saldo = rs.getDouble("balance");
                    } else {
                        // Si no existe cuenta, crear una con saldo 0
                        String ins = "INSERT INTO accounts(user_id, balance) VALUES (?, 0)"; // RETURNING id no necesario aquí
                        try (PreparedStatement ps2 = conn.prepareStatement(ins)) {
                            ps2.setLong(1, this.userId);
                            ps2.executeUpdate();
                            saldo = 0.0;
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "No fue posible cargar el saldo: " + ex.getMessage());
        }

        lblSaldo.setText("Saldo: $" + saldo);

        btnDeposito.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deposito = JOptionPane.showInputDialog("Ingrese el monto a depositar:");
                if (deposito != null && !deposito.isEmpty()) {
                    double monto;
                    try {
                        monto = Double.parseDouble(deposito);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Monto inválido");
                        return;
                    }
                    if (monto > 0) {
                        saldo += monto;
                        lblSaldo.setText("Saldo: $" + saldo);
                        registrarTransaccion("Depósito: $" + monto + " Saldo $" + saldo);

                        // Actualizar en BD
                        actualizarSaldoEnBD();

                        JOptionPane.showMessageDialog(null, "Depósito exitoso.");
                    }

                }

            }
        });

        SALIRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnTransferencia.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String destinatario = JOptionPane.showInputDialog("Ingrese el nombre del destinatario:");
                if (destinatario == null || destinatario.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Debe ingresar un nombre");
                    return;
                }

                String valor = JOptionPane.showInputDialog("Ingrese el monto a transferir:");
                if (valor == null || valor.isEmpty()) {
                    return;
                }

                double monto = Double.parseDouble(valor);

                if (monto <= 0) {
                    JOptionPane.showMessageDialog(null, "El monto debe ser mayor a 0.");
                    return;
                }

                if (monto > saldo) {
                    JOptionPane.showMessageDialog(null, "Fondos insuficientes.");
                    return;
                }

                saldo -= monto;
                lblSaldo.setText("Saldo: $" + saldo);

                registrarTransaccion("Transferencia a " + destinatario + ": $" + monto + " | Saldo: $" + saldo);

                // Actualizar en BD
                actualizarSaldoEnBD();

                JOptionPane.showMessageDialog(null,
                        "Transferencia exitosa a " + destinatario + " por $" + monto);
            }
        });
        btnRetiro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String retiro = JOptionPane.showInputDialog("Ingrese el monto a retirar:");
                if(retiro == null || retiro.isEmpty()){
                    return;
                }

                double monto = Double.parseDouble(retiro);

                if(monto <= 0){
                    JOptionPane.showMessageDialog(null, "El monto no puede ser negativo.");
                    return;
                }

                if(monto > saldo){
                    JOptionPane.showMessageDialog(null, "Saldo insuficiente.");
                    return;
                }

                saldo -= monto;
                lblSaldo.setText("Saldo: $" + saldo);

                registrarTransaccion("Retiro: $" + monto + " | Saldo: $" + saldo);

                // Actualizar en BD
                actualizarSaldoEnBD();

                JOptionPane.showMessageDialog(null, "Retiro exitoso.");
            }
        });


    }

    private void registrarTransaccion(String mensaje) {
        listaTransacciones.add(mensaje);
        txtHistorial.setText("");
        for (String transaccion : listaTransacciones){
            txtHistorial.append(transaccion + "\n");

        }
    }

    private void actualizarSaldoEnBD() {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE accounts SET balance = ? WHERE user_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, saldo);
                ps.setLong(2, userId);
                ps.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "No fue posible actualizar el saldo en la base de datos: " + ex.getMessage());
        }
    }




}
