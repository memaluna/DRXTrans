package main;

import java.io.IOException;

import org.apache.log4j.Logger;


public class Main {
	
	private static final Logger Log = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		
		//Levantamos interfaz icono en segundo plano.
		IconoMensajes Ico = new IconoMensajes();
		Ico.generarIcono();
		
		Lector fileChangeWatcher = new Lector();
		try {
//			fileChangeWatcher.doWath("C:\\XRD\\results");
			fileChangeWatcher.doWath("C:\\QCX");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
