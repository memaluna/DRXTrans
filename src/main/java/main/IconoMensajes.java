package main;

import java.awt.AWTException;
import java.awt.GridLayout;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;

import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class IconoMensajes {

	TrayIcon trayIcon = null;

	public void generarIcono() {


		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			ImageIcon im = new ImageIcon(IconoMensajes.class.getClassLoader().getResource("ojo.png"));

			ActionListener exitListener = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.out.println("Exiting...");
					System.exit(0);
				}
			};

			PopupMenu popup = new PopupMenu();
			MenuItem defaultItem = new MenuItem("Salir");
			defaultItem.addActionListener(exitListener);
			popup.add(defaultItem);
			//Agregamos boton Config
			MenuItem configItem = new MenuItem("Configuración");
//			configItem.addActionListener(exitListener);
			popup.add(configItem);
			
            configItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Llama a la interfaz de configuración
                	InterfazConfig configuracionUI = new InterfazConfig();
                    configuracionUI.mostrarVentanaConfiguracion();
                }
            });
			
			trayIcon = new TrayIcon(im.getImage(), "NHRobot", popup);
			trayIcon.setImageAutoSize(true);
									
			try {
				tray.add(trayIcon);

			} catch (AWTException ex) {
				ex.printStackTrace();
			}

		} else {
			System.err.println("System tray is currently not supported.");
		}
		trayIcon.displayMessage("NHRobot", "Inicio de aplicación.", TrayIcon.MessageType.NONE);
	}
	
	public void mandarMsj(String mensaje) {
		trayIcon.displayMessage("NHRobot", mensaje, TrayIcon.MessageType.NONE);
	}

}
