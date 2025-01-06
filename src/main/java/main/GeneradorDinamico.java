package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeneradorDinamico {
	
	private String propertiesLine;
	private Map<String, String> propertiesMap = new LinkedHashMap<>();
	
	public Map<String, String> createPropertiesMap(String propertiesLine){
		
		System.out.println(propertiesLine);		
		Map<String, String> map = new LinkedHashMap<>();
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
