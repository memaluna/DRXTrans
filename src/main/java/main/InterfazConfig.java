package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;

public class InterfazConfig {

//    private Properties properties = new Properties();
//	ClassLoader loader = Thread.currentThread().getContextClassLoader();       
	private static final Logger Log = Logger.getLogger(InterfazConfig.class);
	String configPath = "conf.properties";
	ExternalConfigManager configManager = null;

    public void mostrarVentanaConfiguracion() {
        // Cargar propiedades desde el archivo
        cargarPropiedades();

        // Crear la ventana
        JFrame frame = new JFrame("Configuración");
        frame.setSize(800, 400);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Panel para las propiedades fijas
        JPanel panelFijo = new JPanel(new GridLayout(2, 2, 10, 10));
        panelFijo.setBorder(BorderFactory.createTitledBorder("Propiedades Fijas"));

        JLabel labelDirectorioEntrada = new JLabel("Directorio de Entrada:");
        JTextField textDirectorioEntrada = new JTextField(configManager.getProperty("DirectorioEntrada", ""));
        JLabel labelDirectorioSalida = new JLabel("Directorio de Salida:");
        JTextField textDirectorioSalida = new JTextField(configManager.getProperty("DirectorioSalida", ""));

        panelFijo.add(labelDirectorioEntrada);
        panelFijo.add(textDirectorioEntrada);
        panelFijo.add(labelDirectorioSalida);
        panelFijo.add(textDirectorioSalida);

        // Panel para las propiedades dinámicas
        JPanel panelDinamico = new JPanel(new BorderLayout());
        panelDinamico.setBorder(BorderFactory.createTitledBorder("Propiedades Dinámicas"));

        DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"Elemento", "FileName", "ID", "Campos"}, 0);
        JTable table = new JTable(tableModel);

        // Cargar datos dinámicos en la tabla
        cargarDatosDinamicos(tableModel);

        JScrollPane scrollPane = new JScrollPane(table);
        panelDinamico.add(scrollPane, BorderLayout.CENTER);

        JPanel panelBotones = new JPanel();
        JButton btnAgregar = new JButton("Agregar");
        JButton btnEliminar = new JButton("Eliminar");
        panelBotones.add(btnAgregar);
        panelBotones.add(btnEliminar);

        panelDinamico.add(panelBotones, BorderLayout.SOUTH);

        // Botón guardar
        JButton btnGuardar = new JButton("Guardar");

        // Acción para agregar un nuevo grupo dinámico
        btnAgregar.addActionListener(e -> {
            tableModel.addRow(new Object[]{"", "", "", ""});
        });

        // Acción para eliminar un grupo dinámico seleccionado
        btnEliminar.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
            }
        });

        // Acción para guardar las propiedades en el archivo
        btnGuardar.addActionListener(e -> {
            guardarPropiedades(textDirectorioEntrada.getText(), textDirectorioSalida.getText(), tableModel);
            JOptionPane.showMessageDialog(frame, "Configuración guardada con éxito. Para que los cambios sean exitosos, por favor reinicie el programa.");
        });

        // Agregar paneles a la ventana
        frame.add(panelFijo, BorderLayout.NORTH);
        frame.add(panelDinamico, BorderLayout.CENTER);
        frame.add(btnGuardar, BorderLayout.SOUTH);

        // Mostrar la ventana
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void cargarPropiedades() {
//        try (InputStream stream = loader.getResourceAsStream("conf.properties")) {
//            properties.load(stream);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    	
		String configPath = "conf.properties";
		try {
			configManager = new ExternalConfigManager(configPath);
		} catch (IOException e) {
			Log.error(e);
			Log.info("No se pudo cargar properties en interfaz.");
			e.printStackTrace();
		}
    }

    private void cargarDatosDinamicos(DefaultTableModel tableModel) {
        Map<String, Map<String, String>> elementos = new HashMap<>();

        configManager.forEach((key, value) -> {
            String keyStr = key.toString();
            if (keyStr.contains("-Campos") || keyStr.contains("-ID") || keyStr.contains("-FileName")) {
                String elemento = keyStr.substring(0, keyStr.lastIndexOf('-'));
                String propiedad = keyStr.substring(keyStr.lastIndexOf('-') + 1);

                elementos.putIfAbsent(elemento, new HashMap<>());
                elementos.get(elemento).put(propiedad, value.toString());
            }
        });

        elementos.forEach((elemento, valores) -> {
            String fileName = valores.getOrDefault("FileName", "");
            String id = valores.getOrDefault("ID", "");
            String campos = valores.getOrDefault("Campos", "");
            tableModel.addRow(new Object[]{elemento, fileName, id, campos});
        });
    }

    private void guardarPropiedades(String directorioEntrada, String directorioSalida, DefaultTableModel tableModel) {
        // Guardar propiedades fijas
    	configManager.setProperty("DirectorioEntrada", directorioEntrada);
    	configManager.setProperty("DirectorioSalida", directorioSalida);

        // Eliminar propiedades dinámicas existentes
    	configManager.keySet().removeIf(key -> key.toString().matches(".*-(Campos|ID|FileName)$"));

        // Guardar propiedades dinámicas
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String elemento = tableModel.getValueAt(i, 0).toString();
            String fileName = tableModel.getValueAt(i, 1).toString();
            String id = tableModel.getValueAt(i, 2).toString();
            String campos = tableModel.getValueAt(i, 3).toString();

            if (!elemento.isEmpty()) {
            	configManager.setProperty(elemento + "-FileName", fileName);
            	configManager.setProperty(elemento + "-ID", id);
            	configManager.setProperty(elemento + "-Campos", campos);
            }
        }

        // Guardar en el archivo
        try (FileOutputStream output = new FileOutputStream("src/main/resources/conf.properties")) {
        	configManager.store(output, "Configuración actualizada");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        InterfazConfig interfazConfig = new InterfazConfig();
        interfazConfig.mostrarVentanaConfiguracion();
    }
}
