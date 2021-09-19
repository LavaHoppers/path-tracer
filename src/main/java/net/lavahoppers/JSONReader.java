package net.lavahoppers;

/*
 * JSONReader.java
 * 
 * 19 Sep 2021
 */

import java.io.FileReader;

import org.json.simple.*;
import org.json.simple.parser.*;

/**
 * Class for reading a JSON file.
 * 
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class JSONReader {

    /**
     * Get the root JSONObject from a JSON file.
     * 
     * @param file String the settings file
     */
    public static JSONObject getRoot(String file) {
        JSONParser parser = new JSONParser();
		try {
			return (JSONObject)parser.parse(new FileReader(file));
        } catch (Exception e) {
            System.err.println("Couldn't read file " + file + "."  );
            System.exit(1);
        }
        return null;
    }

    /**
     * Read a boolean value from a json object
     * 
     * @param json the json object to read the value from 
     * @param key the name of the json key
     * 
     * @return the value of the key as a boolean
     */
    public static boolean getBoolean(JSONObject json, String key) {
        try {
            return (boolean)json.get(key);
        } catch (Exception e) {
            System.err.println("Couldn't read the JSON key " + key + 
                " as a boolean.");
            System.exit(1);
        }
        return false;
    }

    /**
     * Read an integer value from a json object
     * 
     * @param json the json object to read the value from 
     * @param key the name of the json key
     * 
     * @return the value of the key as a int
     */
    public static int getInt(JSONObject json, String key) {
        try {
            return (int)(long)json.get(key);
        } catch (Exception e) {
            System.err.println("Couldn't read the JSON key " + key + 
                " as an int.");
            System.exit(1);
        }
        return 0;
    }

    /**
     * Read a double value from a json object
     * 
     * @param json the json object to read the value from 
     * @param key the name of the json key
     * 
     * @return the value of the key as a double
     */
    public static double getDouble(JSONObject json, String key) {
        try {
            return (double)json.get(key);
        } catch (Exception e) {
            System.err.println("Couldn't read the JSON key " + key + 
                " as a double.");
            System.exit(1);
        }
        return 0.0;
    }

    /**
     * Read a String value from a json object
     * 
     * @param json the json object to read the value from 
     * @param key the name of the json key
     * 
     * @return the value of the key as a String
     */
    public static String getString(JSONObject json, String key) {
        try {
            return (String)json.get(key);
        } catch (Exception e) {
            System.err.println("Couldn't read the JSON key " + key + 
                " as a String.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Read an int array value from a json object
     * 
     * @param json the json object to read the value from 
     * @param key the name of the json key
     * 
     * @return the value of the key as an int array
     */
    public static int[] getIntArray(JSONObject json, String key) {
        try {
            JSONArray a = (JSONArray)json.get(key);
            int[] b = new int[a.size()];
            for (int i = 0; i < b.length; i++)
                b[i] = (int)(long)a.get(i);
            return b;
        } catch (Exception e) {
            System.err.println("Couldn't read the JSON key " + key + 
                " as an int array.");
            System.exit(1);
        }
        return null;
    }

    /**
     * Read a double array value from a json object
     * 
     * @param json the json object to read the value from 
     * @param key the name of the json key
     * 
     * @return the value of the key as a double array
     */
    public static double[] getDoubleArray(JSONObject json, String key) {
        try {
            JSONArray a = (JSONArray)json.get(key);
            double[] b = new double[a.size()];
            for (int i = 0; i < b.length; i++)
                b[i] = (double)a.get(i);
            return b;
        } catch (Exception e) {
            System.err.println("Couldn't read the JSON key " + key + 
                " as a double array.");
            System.exit(1);
        }
        return null;
    }

}
