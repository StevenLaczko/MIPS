import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Fetcher {

    String program;
    String [] programLines;
    byte pc = 0;

    void loadFile(String filename) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        try {
            File fileObj = new File(filename);
            Scanner myReader = new Scanner(fileObj);
            program = myReader.toString();
            //TODO: split program lines into 8 character bytes
            programLines = program.split("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    //TODO: Won't work until above TODO is fixed (split program lines)
    String getInstruction() {
        if (pc < programLines.length) {
            return programLines[pc];
        }
    }
}
