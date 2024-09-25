package com.edu.uptc.api_rest;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

@SpringBootApplication
public class ApiRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRestApplication.class, args);
	}


	@RestController
	@RequestMapping("/api")
	class RandomNameController {

		private static final String FILE_PATH = "utils/data.csv";
		private static final Random RANDOM = new Random();
		private List<String[]> names = new ArrayList<>();

		/**
		 * Cargar los nombres después de la inicialización de Spring Boot
		 */
		@PostConstruct
		public void init() {
			names = loadNamesFromFile(FILE_PATH);
			if (names.isEmpty()) {
				System.out.println("No se encuentran nombres en el archivo.");
			}
		}

		/**
		 * Cargar nombres desde el archivo CSV
		 *
		 * @param filePath
		 * @return lista de nombres
		 */
		private List<String[]> loadNamesFromFile(String filePath) {
			List<String[]> names = new ArrayList<>();
			try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
				reader.readLine(); // Saltar la cabecera
				addNames(names, reader);
			} catch (IOException e) {
				System.err.println("Error leyendo el archivo: " + e.getMessage());
			}
			return names;
		}

		private void addNames(List<String[]> names, BufferedReader reader) throws IOException {
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(",");
				if (parts.length == 2) {
					String firstName = parts[0];
					String lastName = parts[1];
					names.add(new String[]{firstName, lastName});
				}
			}
		}

		/**
		 * Endpoint para obtener un nombre aleatorio en formato JSON
		 *
		 * @return Map<String, String>, donde el nombre y apellido se almacenan como pares clave-valor.
		 * Permite que Spring Boot lo serialice automáticamente a formato JSON.
		 */
		@GetMapping("/name")
		public Map<String, String> getRandomName() {
			Map<String, String> response = new HashMap<>();
			if (!names.isEmpty()) {
				int index = RANDOM.nextInt(names.size());
				String[] selectedName = names.get(index);
				response.put("nombre", selectedName[0]);
				response.put("apellido", selectedName[1]);
			} else {
				response.put("error", "No hay nombres disponibles.");
			}
			return response;
		}

		@GetMapping
		public List<Map<String, String>> getNamesInRange(@RequestParam int from, @RequestParam int to) {
			List<Map<String, String>> response = new ArrayList<>();
			if (from < 1 || to > names.size() || from > to) {
				throw new IllegalArgumentException("Rango inválido.");
			}
			for (int i = from - 1; i < to; i++) {
				String[] selectedName = names.get(i);
				Map<String, String> nameMap = new HashMap<>();
				nameMap.put("nombre", selectedName[0]);
				nameMap.put("apellido", selectedName[1]);
				response.add(nameMap);
			}
			return response;
		}

	}
}