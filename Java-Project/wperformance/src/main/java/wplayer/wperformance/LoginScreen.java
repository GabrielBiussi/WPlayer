package wplayer.wperformance;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import wplayer.database.DBConnection;

/**
 9synonsesn2fdld
 * @author petter
 */
public class LoginScreen extends javax.swing.JFrame {

    MachineMonitoringScreen machineScreen;
    
    public LoginScreen() {
      
        initComponents();
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        this.setVisible(true);
        
    }
  
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        txtEmail = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        lblEmail = new javax.swing.JLabel();
        lblPassword = new javax.swing.JLabel();
        btnLogin = new javax.swing.JButton();
        lblLogo = new javax.swing.JLabel();
        lblCopyright = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(1, 1, 1));

        lblEmail.setForeground(new java.awt.Color(254, 254, 254));
        lblEmail.setText("Email: ");

        lblPassword.setForeground(new java.awt.Color(254, 254, 254));
        lblPassword.setText("Senha: ");

        btnLogin.setText("Logar");
        btnLogin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLoginActionPerformed(evt);
            }
        });

        lblLogo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo-login.png"))); // NOI18N

        lblCopyright.setForeground(new java.awt.Color(153, 153, 153));
        lblCopyright.setText("ALL RIGHTS RESERVED. ™ ® & COPYRIGHT © 2019");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 426, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(27, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(lblCopyright)
                        .addGap(41, 41, 41))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblEmail)
                            .addComponent(lblPassword))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtEmail)
                            .addComponent(txtPassword)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(113, 113, 113)
                                .addComponent(btnLogin, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(56, 56, 56))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(lblLogo, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 49, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail))
                .addGap(42, 42, 42)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addGap(26, 26, 26)
                .addComponent(btnLogin)
                .addGap(37, 37, 37)
                .addComponent(lblCopyright)
                .addGap(21, 21, 21))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnLoginActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLoginActionPerformed
        System.out.println("Cliquei");
        
        if(!txtEmail.getText().isEmpty() || !txtPassword.getText().isEmpty()){
            Connection connection = null;
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            String TABLE_CUSTOMER = "CUSTOMER";
            String TABLE_MACHINE = "MACHINE";
            String queryCustomer = String.format("SELECT "
                                                    + "CUSTOMER_ID AS ID, "
                                                    + "CUSTOMER_EMAIL AS EMAIL, "
                                                    + "CUSTOMER_PASSWORD AS PASSWORD, "
                                                    + "CUSTOMER_NAME AS NAME "
                                                 + "FROM %s "
                                                + "WHERE LOWER(CUSTOMER_EMAIL) = ?", TABLE_CUSTOMER);
            String queryMachine = String.format("SELECT "
                                                   + "MACHINE_KEY "
                                                + "FROM %s "
                                               + "WHERE CUSTOMER_ID = ?", TABLE_MACHINE);
            
            System.out.println("Variaveis Criadas");
            
            try{
                connection = DBConnection.getConnection();
                statement = connection.prepareStatement(queryCustomer);
                
                statement.setString(1, txtEmail.getText().toLowerCase());
                
                resultSet = statement.executeQuery();
                
                System.out.println("Customer: "+ resultSet);
                String pass = "";
                String id = "";
                while(resultSet.next()){
                  pass = resultSet.getString("PASSWORD");
                    id = resultSet.getString("ID");
                }
                
                if(pass.equals(txtPassword.getText())){
                    System.out.println("Senhas iguais");
                    
                    String machineKey = JOptionPane.showInputDialog("Digite a Machine Key: ");
                    
                    if(!machineKey.isEmpty() && machineKey != null){
                        
                       ArrayList<String> machines = new ArrayList<String>(); 
                       statement = connection.prepareStatement(queryMachine);
                       statement.setString(1, id);
                       resultSet = statement.executeQuery();
                       
                       while(resultSet.next())
                           machines.add(resultSet.getString("MACHINE_KEY"));
                       
                        System.out.println(machines);
                        
                       if(!machines.isEmpty()){
                           if(machines.contains(machineKey)){
                                    machineScreen = new MachineMonitoringScreen();
                                    machineScreen.setMachineKey(machineKey);
                                    machineScreen.setLoginScreen(this);
                                    SystemTrayTest.createAndShowGUI(machineScreen);
                                    this.setVisible(false);
                                }
                           }
                       }
                }else{
                    JOptionPane.showMessageDialog(null, "Senha Incorreta!");
                }
                
            }catch(SQLException ex){
                System.out.println("Erro:" +ex);
            }finally{
                DBConnection.closeConnection(connection, statement, resultSet);
            }
            
        }else{
            JOptionPane.showMessageDialog(null, "Campos Inválidos!");
        }
    }//GEN-LAST:event_btnLoginActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LoginScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LoginScreen().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnLogin;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel lblCopyright;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblLogo;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtPassword;
    // End of variables declaration//GEN-END:variables
}
