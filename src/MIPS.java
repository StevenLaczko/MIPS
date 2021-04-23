import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


class MIPS {

    public static void main(String[] args) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        final String FILE_PATH = "input/input.txt";
        try {
            File fileObj = new File(FILE_PATH);
            Scanner myReader = new Scanner(fileObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}




