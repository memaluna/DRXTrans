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
	
	public Map<String, String> createPropertiesMap(String propertiesLine){
		System.out.println(propertiesLine);		
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
			Log.warn(e);
			Log.warn("Error: Archivo de entrada con formato incorrecto.");
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
    
    public void generarArchivo(String id, Map<String, String> datosFinales) {
		
    	try {
			int dia, mes, ano, hora2, min, seg;
			LocalDateTime hoy = LocalDateTime.now();
			
			dia = hoy.getDayOfMonth();
			mes = hoy.getMonthValue();
			ano = hoy.getYear();
			hora2 = hoy.getHour();
			min = hoy.getMinute();
			seg = hoy.getSecond();
			String ruta = "C:\\Resultados\\CK_dia_" + dia + "-" + mes + "-" + ano + "_hora_" + hora2 + "-" + min
					+ "-" + seg + ".QAN";
			// "\\C:\\Users\\josluna\\Desktop\\directorio\\CK_dia_" + dia + "-" + mes + "-"
			// + ano + "_hora_"
			// + hora2 + "-" + min + "-" + seg + ".QAN";

//			String contenido = fecha + ";" + hora + ";" + tipo + ";MG.C3S_DRX=" + C3S_DRX + ";MG.C2S_DRX=" + C2S_DRX
//				+ ";MG.C4AF_DRX=" + C4AF_DRX + ";MG.c_C3A_DRX=" + c_C3A_DRX + ";MG.o_C3A_DRX=" + o_C3A_DRX
//				+ ";MG.CaO_DRX=" + CaO_DRX + ";MG.CaOH2 _DRX=" + CaOH2_DRX + ";MG.MgO_DRX=" + MgO_DRX
//				+ ";MG.K2SO4_DRX=" + K2SO4_DRX + ";MG.Alphthitalite_DRX=" + Alphthitalite_DRX
//				+ ";MG.Langbeinite_DRX=" + Langbeinite_DRX + ";";
			
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
			e.printStackTrace();
		}
    	
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
