import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * A static class for reading OBJ files
 * <p>
 * This obj reader is not that feature rich and could break for
 * any number of reasons. Handle with care
 * @author <a href=https://github.com/lavahoppers>Joshua Hopwood</a>
 */
public class OBJReader {
    
    /**
     * Reads an OBJ file into an array of meshes
     * @param path the path to the OBJ
     * @return the array of meshes
     */
    public static ArrayList<Mesh> read(String path) {

        ArrayList<Mesh> meshes = new ArrayList<Mesh>();

        Mesh currentMesh = new Mesh();
        meshes.add(currentMesh);

        Scanner scanner = null;

        try {
            scanner = new Scanner(new File(path));
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + path);
            e.printStackTrace();
        }

    
        while (scanner.hasNextLine()) {

            String line = scanner.nextLine();
            if (line.length() == 0)
                continue;
            
            String[] parsed = line.split(" ");
            
            switch (parsed[0]) {

                case "#": // comments
                    break;

                case "vt": // texture coridinates
                    break;

                case "vp": // parameter-space vertices
                    break;

                case "v": // verticies
                    currentMesh.verticies.add(new Vector3(Double.parseDouble(parsed[1]), Double.parseDouble(parsed[3]),
                            Double.parseDouble(parsed[2])));
                    break;

                case "vn": // vertex normals
                    break;

                case "mtllib":
                    String matPath = parsed[1];
                    break;

                case "usemtl": // use a material
                    break;

                case "f": // faces
                    /* vertex_index/texture_index/normal_index */
                    // potentially incorrect
                    if (parsed.length == 4)
                        currentMesh.triangles.add(
                                new Triangle(currentMesh.verticies.get(Integer.parseInt(parsed[1].split("/")[0]) - 1),
                                        currentMesh.verticies.get(Integer.parseInt(parsed[2].split("/")[0]) - 1),
                                        currentMesh.verticies.get(Integer.parseInt(parsed[3].split("/")[0]) - 1)));
                    else if (parsed.length == 5) {
                        currentMesh.triangles.add(
                                new Triangle(currentMesh.verticies.get(Integer.parseInt(parsed[1].split("/")[0]) - 1),
                                        currentMesh.verticies.get(Integer.parseInt(parsed[2].split("/")[0]) - 1),
                                        currentMesh.verticies.get(Integer.parseInt(parsed[3].split("/")[0]) - 1)));
                        currentMesh.triangles.add(
                                new Triangle(currentMesh.verticies.get(Integer.parseInt(parsed[1].split("/")[0]) - 1),
                                        currentMesh.verticies.get(Integer.parseInt(parsed[3].split("/")[0]) - 1),
                                        currentMesh.verticies.get(Integer.parseInt(parsed[4].split("/")[0]) - 1)));
                    }
                    break;

                case "o": // object name
                    break;
                    
                case "g": // object group
                    break;

                default:
                    break;
            }

        }
        scanner.close();
        
        return meshes;
    }


}
