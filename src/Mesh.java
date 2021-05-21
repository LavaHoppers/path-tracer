
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Class for keeping track of meshes
 */
class Mesh {

    ArrayList<Triangle> triangles;
    ArrayList<Vector3> verticies;
    BVH bvh;

    /**
     * Creates a new mesh from an OBJ file
     * @param path the path to the OBJ file
     */
    Mesh(String path) {

        triangles = new ArrayList<Triangle>();
        verticies = new ArrayList<Vector3>();

        /* read the contents of the file into verticies and triangles */
        File file = null;
        Scanner scanner = null;
        try {
            file = new File(path);
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + path);
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.length() == 0)
                continue;
            if (line.charAt(0) == 'v') {
                String[] split = line.split(" ");
                verticies.add(new Vector3(
                    Double.parseDouble(split[1]),
                    Double.parseDouble(split[3]),
                    Double.parseDouble(split[2])
                ));
            }
            if (line.charAt(0) == 'f') {
                String[] split = line.split(" ");
                triangles.add(new Triangle(
                    verticies.get(Integer.parseInt(split[1].split("//")[0]) - 1),
                    verticies.get(Integer.parseInt(split[2].split("//")[0]) - 1),
                    verticies.get(Integer.parseInt(split[3].split("//")[0]) - 1)
                ));
            }
        }
        scanner.close();

        /* create a bounding volume hierarchy */
        bvh = new BVH(triangles);

    }   
}
