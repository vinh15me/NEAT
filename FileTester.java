import java.io.File;
import java.io.IOException;

public class FileTester {

    public static void main(String[] args) {
        // Enter your Source folder path here:
        String targetFolder = "";

        //{ filename, extension }
        String[][] files = {
                {"Test1", "java"},
                {"Test2", "html"},
                {"Test3", "txt"},
                {"Test4", "json"}
        };

        createFiles(targetFolder, filesToCreate);
    }

    public static void createFiles(String folderPath, String[][] files) {
        File folder = new File(folderPath);
        
        if (!folder.exists()) {
            if (folder.mkdirs()) {
                System.out.println("Created folder: " + folderPath);
            } else {
                System.err.println("Failed to create folder: " + folderPath);
                return;
            }
        }

        for (String[] fileData : files) {
            String name = fileData[0];
            String extension = fileData[1];

            File file = new File(folder, name + "." + extension);

            try {
                if (file.createNewFile()) {
                    System.out.println("Created file: " + file.getAbsolutePath());
                } else {
                    System.out.println("File already exists: " + file.getAbsolutePath());
                }
            } catch (IOException e) {
                System.err.println("Error creating file: " + file.getName());
                e.printStackTrace();
            }
        }
    }
}
