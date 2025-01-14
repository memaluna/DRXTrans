package main;

import java.io.*;
import java.util.Properties;

public class ExternalConfigManager extends Properties {
    private String filePath;

    public ExternalConfigManager(String filePath) throws IOException {
        super();
        this.filePath = filePath;
        try (FileInputStream input = new FileInputStream(filePath)) {
            this.load(input);
        }
    }

    // Guardar cambios en el archivo
    public void save(String comments) throws IOException {
        try (FileOutputStream output = new FileOutputStream(filePath)) {
            this.store(output, comments);
        }
    }

    // Obtener la ruta del archivo
    public String getFilePath() {
        return filePath;
    }

    // Actualizar la ruta del archivo (opcional)
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}

