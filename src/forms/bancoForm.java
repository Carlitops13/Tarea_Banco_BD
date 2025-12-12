package forms;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class bancoForm {
    private JLabel lblSaldo;
    private JPanel btnSalir;
    private JButton btnDeposito;
    private JButton SALIRButton;
    private JButton btnRetiro;
    private JButton btnTransferencia;
    private JTextArea txtHistorial;
    private double saldo = 1000.0;
    private ArrayList<String> listaTransacciones = new ArrayList<>();

    public bancoForm() {
        btnDeposito.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String deposito = JOptionPane.showInputDialog("Ingrese el monto a depositar:");
                if (deposito != null && !deposito.isEmpty()) {
                    double monto = Double.parseDouble(deposito);
                    if (monto > 0) {
                        saldo += monto;
                        lblSaldo.setText("Saldo: $" + saldo);
                        registrarTransaccion("Depósito: $" + monto + "Saldo $" + saldo);

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

    public static void main(String[] args) {
        JFrame frame = new JFrame("bancoForm");
        frame.setContentPane(new bancoForm().btnSalir);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }


}
