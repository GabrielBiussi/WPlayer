/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package wplayer.wperformance;
import java.awt.AWTException;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 * @author Gabriel
 */
public class SystemTrayDemo {
    
    public static void main(String[] args) {
        if(!SystemTray.isSupported()){
            System.out.println("System Tray is not supported");
            return;
        }
        
        SystemTray systemTray = SystemTray.getSystemTray();
        
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Image image = toolkit.getImage("C:/Users/Gabriel/Desktop/icon.png");
        
        PopupMenu menu = new PopupMenu();
        MenuItem action = new MenuItem("Action");
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //show frames,start services
                JOptionPane.showMessageDialog(null, "Action Menu Item");
            }
        });
         
        menu.add(action);
        
         MenuItem close = new MenuItem("Close");
        action.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        menu.add(close);
        
        
        TrayIcon trayIcon = new TrayIcon(image, "SystemTray Demo", menu);
        trayIcon.setImageAutoSize(true);
        try {
            systemTray.add(trayIcon);
        } catch (AWTException ex) {
            Logger.getLogger(SystemTrayDemo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
