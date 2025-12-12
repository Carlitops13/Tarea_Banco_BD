package forms;
import forms.bancoForm;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class loginForm extends JFrame {
    private JPanel loginPanel;
    private JLabel icon;
    private JLabel logo;
    private JTextField textField1;
    private JTextField textField2;
    private JButton button1;
    private JPasswordField passwordField1;

    public loginForm(){
        setTitle("loginForm");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        setContentPane(loginPanel);
        setLocationRelativeTo(null);
        textField1.setBorder(BorderFactory.createEmptyBorder());
        passwordField1.setBorder(BorderFactory.createEmptyBorder());
        button1.setBorder(BorderFactory.createEmptyBorder());
        ImageIcon icono = new ImageIcon(getClass().getResource("/img/icon.png"));
        ImageIcon logotipo = new ImageIcon(getClass().getResource("/img/logo.png"));
        logo.setIcon(logotipo);
        icon.setIcon(icono);


        button1.addActionListener(new ActionListener() {


            @Override
            public void actionPerformed(ActionEvent e) {
                String usuario = "cliente123";
                String clave = "clave123";
                String claveIngresada = new String(passwordField1.getPassword());
                if(textField1.getText().equals(usuario) && claveIngresada.equals(clave)){
                    JOptionPane.showMessageDialog(null, usuario + " con exito");
                    dispose();
                    new bancoForm();
                }else{
                    JOptionPane.showMessageDialog(null, "Clave o Usuario incorrecto");
                }
            }
        });
    }

}
