package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;
import java.util.Properties;

import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;

public class Lector {


	public void doWath(String directory) throws IOException {
				
		Properties propiedades = new Properties();
		ClassLoader loader = Thread.currentThread().getContextClassLoader();           
		InputStream stream = loader.getResourceAsStream("conf.properties");
		propiedades.load(stream);
		
		System.out.println("WatchService in " + directory);

		// Obtenemos el directorio
		Path directoryToWatch = Paths.get(directory);
		if (directoryToWatch == null) {
			throw new UnsupportedOperationException("Directory not found");
		}

		// Solicitamos el servicio WatchService
		WatchService watchService = directoryToWatch.getFileSystem().newWatchService();

		// Registramos los eventos que queremos monitorear
		directoryToWatch.register(watchService, new WatchEvent.Kind[] { ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY });

		System.out.println("Started WatchService in " + directory);

		try {

			// Esperamos que algo suceda con el directorio
			WatchKey key = watchService.take();

			// Algo ocurrio en el directorio para los eventos registrados
			while (key != null) {
				for (WatchEvent event : key.pollEvents()) {
					String eventKind = event.kind().toString();
					String file = event.context().toString();
					System.out.println("Event : " + eventKind + " in File " + file);
					Thread.sleep(1000);
					Generador Gen = new Generador();
					String lineaLeida = Gen.leerArchivo(file, directory);
					
					String fases = propiedades.getProperty(file + "-Campos");
					GeneradorDinamico genDin = new GeneradorDinamico();
					Map<String, String> createPropertiesMap = genDin.createPropertiesMap(fases);
					
			        for (Map.Entry<String, String> entry : createPropertiesMap.entrySet()) {
			            System.out.println(entry.getKey() + " -> " + entry.getValue());
			        }
					
			        String fileDirectory = directory + "\\"+ file;
			        
			        Map<String, Integer> header = genDin.parseHeaderToMap(fileDirectory);
			        
			        System.out.println("-------- header -----");
			        for (Map.Entry<String, Integer> entry : header.entrySet()) {
			            System.out.println(entry.getKey() + " -> " + entry.getValue());
			        }
			        
			        Map<Integer, String> lastLine = genDin.parseLastLineToMap(fileDirectory);
					
			        System.out.println("-------- lastLine -----");
			        for (Map.Entry<Integer, String> entry : lastLine.entrySet()) {
			            System.out.println(entry.getKey() + " -> " + entry.getValue());
			        }
			        
			        
			        Map<String, String> datosFinales = genDin.obtenerDatosFinales(createPropertiesMap, header, lastLine);
			        
			        // Extraemos id para generar dato
			        String id = genDin.obtenerID(propiedades.getProperty(file + "-ID"), header, lastLine);
			        
			        // Generamos archivo
			        genDin.generarArchivo(id, datosFinales, propiedades.getProperty(file + "-FileName"));
			        
//					if (file.equals("Cement.OUT")) {
//						Cemento Cem = new Cemento();
//						Cem.GenerarCemento(lineaLeida);
//					}
//					if (file.equals("Clinker_HTEC_MG.out")) {
//						CK CK = new CK();
//						CK.GenerarCK(lineaLeida);
//					}
				}

				// Volvemos a escuchar. Lo mantenemos en un loop para escuchar indefinidamente.
				key.reset();
				key = watchService.take();
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
