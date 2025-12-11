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
                if(deposito != null && !deposito.isEmpty()){
                    double monto = Double.parseDouble(deposito);
                    if(monto>0){
                        saldo+=monto;
                        lblSaldo.setText("Saldo: $" + saldo);
                        registrarTransaccion("Depósito: $" + monto +"Saldo $" + saldo);

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
