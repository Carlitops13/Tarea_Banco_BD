package forms;

import model.DBConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateAccountForm extends JFrame {
    private JPanel mainPanel;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtInitialBalance;
    private JButton btnCreate;

    public CreateAccountForm() {
        setTitle("Crear Cuenta");

        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(4,4,4,4);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            gbc.gridx = 0; gbc.gridy = 0;
            mainPanel.add(new JLabel("Usuario:"), gbc);
            txtUsername = new JTextField(20);
            gbc.gridx = 1; gbc.gridy = 0;
            mainPanel.add(txtUsername, gbc);

            gbc.gridx = 0; gbc.gridy = 1;
            mainPanel.add(new JLabel("Contraseña:"), gbc);
            txtPassword = new JPasswordField(20);
            gbc.gridx = 1; gbc.gridy = 1;
            mainPanel.add(txtPassword, gbc);

            gbc.gridx = 0; gbc.gridy = 2;
            mainPanel.add(new JLabel("Saldo inicial:"), gbc);
            txtInitialBalance = new JTextField("0.0", 10);
            gbc.gridx = 1; gbc.gridy = 2;
            mainPanel.add(txtInitialBalance, gbc);

            btnCreate = new JButton("Crear cuenta");
            gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
            mainPanel.add(btnCreate, gbc);
        } else {
            if (txtUsername == null) txtUsername = new JTextField();
            if (txtPassword == null) txtPassword = new JPasswordField();
            if (txtInitialBalance == null) txtInitialBalance = new JTextField("0.0");
            if (btnCreate == null) btnCreate = new JButton("Crear cuenta");
        }

        if (mainPanel == null) {
            System.err.println("NOTICE: mainPanel era null, creando fallback simple");
            mainPanel = new JPanel();
            mainPanel.add(new JLabel("Formulario de creación (fallback)"));
        }

        setContentPane(mainPanel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        btnCreate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText().trim();
                String password = new String(txtPassword.getPassword());
                double initialBalance = 0.0;
                try {
                    initialBalance = Double.parseDouble(txtInitialBalance.getText().trim());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Saldo inicial inválido");
                    return;
                }

                if (username.isEmpty() || password.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Ingrese usuario y contraseña");
                    return;
                }

                try (Connection conn = DBConnection.getConnection()) {
                    String insUser = "INSERT INTO users(username, password, active) VALUES (?, ?, true) ON CONFLICT (username) DO NOTHING RETURNING id";
                    Long userId = null;
                    try (PreparedStatement ps = conn.prepareStatement(insUser)) {
                        ps.setString(1, username);
                        ps.setString(2, password);
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                userId = rs.getLong(1);
                            }
                        }
                    }

                    if (userId == null) {
                        String sel = "SELECT id FROM users WHERE username = ?";
                        try (PreparedStatement ps2 = conn.prepareStatement(sel)) {
                            ps2.setString(1, username);
                            try (ResultSet rs2 = ps2.executeQuery()) {
                                if (rs2.next()) userId = rs2.getLong(1);
                            }
                        }
                    }

                    if (userId == null) {
                        JOptionPane.showMessageDialog(null, "No fue posible crear o recuperar el usuario");
                        return;
                    }

                    String insAccount = "INSERT INTO accounts(user_id, balance) SELECT ?, ? WHERE NOT EXISTS (SELECT 1 FROM accounts WHERE user_id = ?);";
                    try (PreparedStatement ps3 = conn.prepareStatement(insAccount)) {
                        ps3.setLong(1, userId);
                        ps3.setDouble(2, initialBalance);
                        ps3.setLong(3, userId);
                        ps3.executeUpdate();
                    }

                    JOptionPane.showMessageDialog(null, "Cuenta creada/actualizada correctamente. Usuario id=" + userId);
                    dispose();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Error al crear la cuenta: " + ex.getMessage());
                }
            }
        });
    }
}
