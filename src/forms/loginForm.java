package forms;
import model.DBConnection;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class loginForm extends JFrame {
    private JPanel loginPanel;
    private JLabel icon;
    private JLabel logo;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JPasswordField passwordField1;
    private JButton CREARCUENTAButton;


    private int failedAttempts = 0;

    public loginForm(){
        setTitle("loginForm");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        if (loginPanel == null) {
            loginPanel = new JPanel();
            loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));
            textField1 = new JTextField(20);
            passwordField1 = new JPasswordField(20);
            button1 = new JButton("Ingresar");
            CREARCUENTAButton = new JButton("Crear cuenta");
            loginPanel.add(new JLabel("Usuario:"));
            loginPanel.add(textField1);
            loginPanel.add(new JLabel("Contraseña:"));
            loginPanel.add(passwordField1);
            loginPanel.add(button1);
            loginPanel.add(CREARCUENTAButton);
        } else {
            if (textField1 == null) textField1 = new JTextField();
            if (passwordField1 == null) passwordField1 = new JPasswordField();
            if (button1 == null) button1 = new JButton("Ingresar");
            if (CREARCUENTAButton == null) CREARCUENTAButton = new JButton("Crear cuenta");
        }

        setContentPane(loginPanel);
        setLocationRelativeTo(null);
        try {
            if (logo == null) logo = new JLabel();
            if (icon == null) icon = new JLabel();
            java.net.URL iconUrl = getClass().getResource("/img/icon.png");
            java.net.URL logoUrl = getClass().getResource("/img/logo.png");
            if (iconUrl != null) icon.setIcon(new ImageIcon(iconUrl));
            if (logoUrl != null) logo.setIcon(new ImageIcon(logoUrl));
        } catch (Exception ignored) {}

        setVisible(true);

        textField1.setBorder(BorderFactory.createEmptyBorder());
        passwordField1.setBorder(BorderFactory.createEmptyBorder());
        button1.setBorder(BorderFactory.createEmptyBorder());


        button1.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                String username = textField1.getText().trim();
                String enteredPassword = new String(passwordField1.getPassword());

                if (username.isEmpty() || enteredPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese usuario y contraseña");
                    return;
                }

                try (Connection conn = DBConnection.getConnection()) {
                    String sql = "SELECT id, password, active FROM users WHERE username = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, username);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (!rs.next()) {
                                failedAttempts++;
                                handleFailedAttempt();
                                return;
                            }

                            long userId = rs.getLong("id");
                            String dbPassword = rs.getString("password");
                            boolean active = rs.getBoolean("active");

                            if (!active) {
                                JOptionPane.showMessageDialog(null, "Usuario inhabilitado. Contacte con el administrador.");
                                return;
                            }


                            if (enteredPassword.equals(dbPassword)) {
                                JOptionPane.showMessageDialog(null, username + " con exito");
                                dispose();
                                new bancoForm();
                            } else {
                                failedAttempts++;
                                handleFailedAttempt();
                            }
                        }
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error de conexión: " + ex.getMessage());
                }
            }
        });
        CREARCUENTAButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new CreateAccountForm();

            }
        });
    }

    private void handleFailedAttempt() {
        if (failedAttempts >= 3) {
            JOptionPane.showMessageDialog(null, "Máximos intentos alcanzados. Acceso bloqueado.");
            button1.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(null, "Usuario o contraseña incorrectos. Intentos: " + failedAttempts + "/3");
        }
    }

}