package main;

import java.io.IOException;

import org.apache.log4j.Logger;


public class Main {
	
	private static final Logger Log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		
		//Levantamos interfaz icono en segundo plano.
		IconoMensajes Ico = IconoMensajes.getInstance();
		Ico.generarIcono();
				
		ExternalConfigManager configManager = null;
		try {
			String configPath = "conf.properties";
	        configManager = new ExternalConfigManager(configPath);
		} catch (IOException e1) {
			Log.warn("No se puede cargar archivo de configuración.");
			Ico.mandarMsj("No se puede cargar archivo de configuración.");
		}
				
		String directorioEntrada = configManager.getProperty("DirectorioEntrada");
		String directorioEntrada2 = configManager.getProperty("DirectorioEntrada2");
		Ico.mandarMsj("Observando Directorio: " + directorioEntrada);
		Ico.mandarMsj("Observando Directorio 2: " + directorioEntrada2);
		
		// Crear y ejecutar hilos para cada directorio
		Thread watcher1 = new Thread(() -> {
		    try {
		        new Lector().doWatch(directorioEntrada);
		    } catch (IOException e) {
		    	String msjError = "Error al ejecutar hilo 1: " + e;
		        Log.error(msjError);
		        Ico.mandarMsj(msjError);
		    }
		});

		Thread watcher2 = new Thread(() -> {
		    try {
		        new Lector().doWatch(directorioEntrada2);
		    } catch (IOException e) {
		    	String msjError = "Error al ejecutar hilo 2: " + e;
		        Log.error(msjError);
		        Ico.mandarMsj(msjError);
		    }
		});
		watcher1.start();
		watcher2.start();		
	}

}
