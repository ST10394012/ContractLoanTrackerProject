/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package util;

import com.google.gson.Gson; //Gson library for json serialisation
import com.google.gson.GsonBuilder; // Builder for gson configuration
import com.google.gson.reflect.TypeToken; //For generic type handling
import java.io.*; //For file operations
import java.lang.reflect.Type; //For reflection types
import java.util.List; // For collection handling

public class JsonUtil {
    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting() // Formats JSON with indentation for readability
            .serializeNulls() // Includes null values in JSON
            .create(); // Creates configured Gson instance
    
    
     //Writes a list of objects to JSON file
    public static void writeToJson(String filePath, List<?> objects) {
        try (FileWriter writer = new FileWriter(filePath)) { // Auto-closes writer
            gson.toJson(objects, writer); // Serializes list to JSON and writes to file
        } catch (IOException e) { // Catches file writing errors
            System.err.println("Error writing to " + filePath + ": " + e.getMessage());
        }
    }
    
    //Reads a list of objects from JSON file
    public static <T> List<T> readFromJson(String filePath, Class<T> clazz) {
        File file = new File(filePath); // Creates file reference
        if (!file.exists()) { // Checks if file exists
            return new java.util.ArrayList<>(); // Returns empty list for new files
        }
        
        try (FileReader reader = new FileReader(filePath)) { // Auto-closes reader
            Type type = TypeToken.getParameterized(List.class, clazz).getType(); // Gets list type
            return gson.fromJson(reader, type); // Deserializes JSON to list
        } catch (IOException e) { // Catches file reading errors
            System.err.println("Error reading from " + filePath + ": " + e.getMessage());
            return new java.util.ArrayList<>(); // Returns empty list on error
        }
    }
}