package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

public class GeneradorDinamico {
	
	private static final Logger Log = Logger.getLogger(Main.class);
	private String propertiesLine;
	private Map<String, String> propertiesMap = new LinkedHashMap<>();
	IconoMensajes Ico = IconoMensajes.getInstance();
	
	public Map<String, String> createPropertiesMap(String propertiesLine){		
		Map<String, String> map = new LinkedHashMap<>();
		try {
	        // Separar la cadena en pares clave-valor
	        String[] pairs = propertiesLine.split(";");
	        for (String pair : pairs) {
	            String[] keyValue = pair.split(":");
	            if (keyValue.length == 2) {
	                String key = keyValue[0].trim();
	                String value = keyValue[1].trim();
	                map.put(key, value);
	            }
	        }      
			return map;
		} catch (Exception e) {
			Log.error(e);
			String msjError = "Error: Archivo de entrada con formato incorrecto.";
			Log.error(msjError);
			Ico.mandarMsj(msjError);
			return map;
		}

	}
	
    public Map<String, Integer> parseHeaderToMap(String filePath) throws IOException {
        Map<String, Integer> headerMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String firstLine = br.readLine();
            if (firstLine != null) {
                String[] headers = firstLine.split("\t");
                for (int i = 0; i < headers.length; i++) {
                    headerMap.put(headers[i].trim(), i);
                }
            }
        }catch (Exception e) {
        	String mensajeError = "No se encuentra información sobre el archivo generado " + filePath + ".";
			Log.error(mensajeError);
			Ico.mandarMsj(mensajeError);
		}
        return headerMap;
    }
       
    public Map<Integer, String> parseLastLineToMap(String filePath) throws IOException {
        Map<Integer, String> lastLineMap = new HashMap<>();
        String lastLine = null;
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                lastLine = line;
            }
        }

        if (lastLine != null) {
            String[] values = lastLine.split("\t");
            for (int i = 0; i < values.length; i++) {
                lastLineMap.put(i, values[i].trim());
            }
        }
        return lastLineMap;
    }
    
    public Map<String, String> obtenerDatosFinales(Map<String, String> createPropertiesMap, Map<String, Integer> header, Map<Integer, String> lastLine){
    	
        Map<String, String> map4 = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : createPropertiesMap.entrySet()) {
            String key = entry.getKey(); // Clave de Map1
            String map2Key = entry.getValue(); // Valor de Map1 (clave para Map2)

            // Obtener índice de Map2
            Integer index = header.get(map2Key);
            if (index != null) {
                // Obtener valor de Map3 usando el índice
                String value = lastLine.get(index);
                if (value != null) {
                    map4.put(key, value);
                }
            }
        }

        // Imprimir el resultado
        System.out.println("Map4 (key: Map1, value: Map3):");
        for (Map.Entry<String, String> entry : map4.entrySet()) {
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }
    	
        return map4;
    }
    
    public void generarArchivo(String id, Map<String, String> datosFinales, String fileName, String directorio) {
		
    	try {
			int dia, mes, ano, hora2, min, seg;
			LocalDateTime hoy = LocalDateTime.now();
			
			dia = hoy.getDayOfMonth();
			mes = hoy.getMonthValue(); 
			ano = hoy.getYear();
			hora2 = hoy.getHour();
			min = hoy.getMinute();
			seg = hoy.getSecond();
			String ruta = "C:\\Resultados\\"+ fileName + "_dia_" + dia + "-" + mes + "-" + ano + "_hora_" + hora2 + "-" + min
					+ "-" + seg + ".QAN";
			
			//Procesamiento del ID
			//Sacamos directorio si contiene
			char MoY  = id.charAt(4);
			char MGoYO = id.charAt(5);
			
			if (directorio.compareTo("true") == 0) {
				//vemos si corresponde sacar 14 o 20 caracteres.
				int tildeIndex = id.indexOf('~');
				int charAQuitar;
				if ((MoY == 'M' || MoY == 'Y') && (MGoYO == 'G' || MGoYO == 'O')) {
					charAQuitar = 14;
				}else {
					charAQuitar = 20;
				}
				
				String resultado = null;				
		        if (tildeIndex > 0 && tildeIndex >= charAQuitar) {
		            // Extraer los 20 caracteres anteriores al ~
		            resultado = id.substring(tildeIndex - charAQuitar, tildeIndex);
		            System.out.println("Resultado: " + resultado);
		        } else {
		            System.out.println("No hay suficientes caracteres antes del símbolo ~");
		            Log.error("No se encontró '~' en la entrada.");
		        }							
				id = resultado;
			}
			
			// System.out.println(linea.charAt(indiceInicial - 10));

			if ((MoY == 'M' || MoY == 'Y') && (MGoYO == 'G' || MGoYO == 'O')) {
				id = GenerarArchivoConID(id);
			} else {
				id = GenerarArchivoConFechaHora(id);
			}			
						
			//Concatenamos id con datos...
			String contenidoNuevo = id + ";";
			for (Entry<String, String> entry : datosFinales.entrySet()) {			
				contenidoNuevo = contenidoNuevo + entry.getKey() + "=" + entry.getValue() + ";";				
			}
			File file = new File(ruta);
			// Si el archivo no existe es creado
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(contenidoNuevo);
			bw.close();
		} catch (Exception e) {
			String msjError = "Formato incorrecto de identificación de muestras.";
			Ico.mandarMsj(msjError);
			Log.error(msjError);
		}
    	
    }
    
    private String GenerarArchivoConFechaHora(String id) {
		String newId = null;		
		String hora = "";
		String fecha = "";

		for (int i = 12; i <= 19; i++) {
			fecha = fecha + id.charAt(i);
		}

		for (int i = 6; i <= 10; i++) {
			char caracter;
			if (id.charAt(i) == '_') {
				caracter = ':';
			} else {
				caracter = id.charAt(i);
			}
			hora = hora + caracter;
		}
		char tipo = id.charAt(4);
		
		newId = fecha + ";" + hora + ";" + tipo;
		
		return newId;
	}

	private String GenerarArchivoConID(String id) {
		String newId = "";
		for (int i = 4; i <= 13; i++) {
			newId = newId + id.charAt(i);
		}
		return newId;
	}

	public String obtenerID(String idHeader, Map<String, Integer> header, Map<Integer, String> lastLine) {
    	String id = null;
    	System.out.println(idHeader);   	
    	Integer index = header.get(idHeader);   	
    	id = lastLine.get(index);    	
    	return id;    	
    }

	public String getPropertiesLine() {
		return propertiesLine;
	}

	public void setPropertiesLine(String propertiesLine) {
		this.propertiesLine = propertiesLine;
	}

	public Map<String, String> getDescriptionMap() {
		return propertiesMap;
	}

	public void setDescriptionMap(Map<String, String> descriptionMap) {
		this.propertiesMap = descriptionMap;
	}
	

}
