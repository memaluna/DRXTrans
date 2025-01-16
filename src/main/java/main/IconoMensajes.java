package main;

import java.awt.AWTException;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;

public class IconoMensajes {

    private static IconoMensajes instancia; // Instancia única de la clase
    private TrayIcon trayIcon = null;

    // Constructor privado para evitar la creación de instancias adicionales
    private IconoMensajes() {
    }

    // Método para obtener la instancia única
    public static IconoMensajes getInstance() {
        if (instancia == null) {
            instancia = new IconoMensajes();
        }
        return instancia;
    }

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
        if (trayIcon != null) {
            trayIcon.displayMessage("NHRobot", mensaje, TrayIcon.MessageType.NONE);
        } else {
            System.err.println("El icono de la bandeja no está inicializado.");
        }
    }

}
