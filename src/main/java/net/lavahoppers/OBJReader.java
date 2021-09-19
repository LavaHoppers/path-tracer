package net.lavahoppers;

/*
 * OBJReader.java
 * 
 * 29 May 2021
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A static class for reading OBJ files
 * <p>
 * This obj reader is not that feature rich and could break for
 * any number of reasons. Handle with care! :)
 * 
 * @version 1.0.1
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class OBJReader {
    
    /**
     * Reads an OBJ file into a mesh
     * 
     * @param path the path to the OBJ
     * @return the mesh
     */
    public static Mesh read(String path) {

        ArrayList<Vector3> verticies = new ArrayList<Vector3>();
        ArrayList<Triangle> triangles = new ArrayList<Triangle>();

        Scanner scanner = null;

        try {
            scanner = new Scanner(new File(path));
        } catch (Exception e) {
            System.err.printf("Could not open file \"%s\".", path);
            System.exit(1);
        }

        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            if (line.length() == 0)
                continue;
            
            String[] parsed = line.split(" ");
            
            switch (parsed[0]) {

                case "v": // verticies
                    try {
                        double x = Double.parseDouble(parsed[1]);
                        double y = Double.parseDouble(parsed[2]);
                        double z = Double.parseDouble(parsed[3]);
                        verticies.add(new Vector3(x, y, z));
                    } catch (Exception e) {
                        System.err.println("Failed to load vertex information for: " + line);
                        verticies.add(new Vector3());
                    }
                    break;

                case "f": // faces
                    /* vertex_index/texture_index/normal_index */
                    // potentially incorrect
                    if (parsed.length == 4)
                        triangles.add(
                                new Triangle(verticies.get(Integer.parseInt(parsed[1].split("/")[0]) - 1),
                                        verticies.get(Integer.parseInt(parsed[2].split("/")[0]) - 1),
                                        verticies.get(Integer.parseInt(parsed[3].split("/")[0]) - 1)));
                    else if (parsed.length == 5) {
                        triangles.add(
                                new Triangle(verticies.get(Integer.parseInt(parsed[1].split("/")[0]) - 1),
                                        verticies.get(Integer.parseInt(parsed[2].split("/")[0]) - 1),
                                        verticies.get(Integer.parseInt(parsed[3].split("/")[0]) - 1)));
                        triangles.add(
                                new Triangle(verticies.get(Integer.parseInt(parsed[1].split("/")[0]) - 1),
                                        verticies.get(Integer.parseInt(parsed[3].split("/")[0]) - 1),
                                        verticies.get(Integer.parseInt(parsed[4].split("/")[0]) - 1)));
                    }
                    break;

                default:
                    break;
            }

        }
        scanner.close();
        
        return new Mesh(verticies, triangles);
    }


}
