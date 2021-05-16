package io.github.stevenlaczko;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

import static io.github.stevenlaczko.Main.INPUT_FILE_PATH;

public class InstructionMemory {
    File instrFile;
    Scanner scanner;
    Vector<String> instrLines = new Vector<>();

    public InstructionMemory(String instrFile) {
        loadFile(INPUT_FILE_PATH + instrFile);
    }

    void loadFile(String filePath) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        try {
            instrFile = new File(filePath);
            scanner = new Scanner(instrFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine()) {
            instrLines.add(scanner.nextLine());
        }
    }

    String instrFetch(String PC) {
        int i = (int)Long.parseLong(PC, 2);
        int j = i + 4;
        if (j >= instrLines.size()) {
            return null;
        }

        return String.join("", instrLines.subList(i, j));
    }
}
