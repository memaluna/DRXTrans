package main;

import java.io.IOException;

import org.apache.log4j.Logger;


public class Main {
	
	private static final Logger Log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		
		//Levantamos interfaz icono en segundo plano.
		IconoMensajes Ico = new IconoMensajes();
		Ico.generarIcono();
				
		ExternalConfigManager configManager = null;
		try {
			String configPath = "conf.properties";
	        configManager = new ExternalConfigManager(configPath);
		} catch (IOException e1) {
			Log.warn("No se puede cargar archivo de configuración.");
			Ico.mandarMsj("No se puede cargar archivo de configuración.");
		}
				
		Lector fileChangeWatcher = new Lector();
		try {
			String directorioEntrada = configManager.getProperty("DirectorioEntrada");
			Ico.mandarMsj("Observando Directorio: " + directorioEntrada);
			fileChangeWatcher.doWath(directorioEntrada);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
