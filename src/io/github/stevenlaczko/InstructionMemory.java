package io.github.stevenlaczko;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static io.github.stevenlaczko.MIPS.FILE_PATH;

public class InstructionMemory {
    File instrFile;
    Scanner scanner;

    public InstructionMemory(Clock clock) {
        loadFile(FILE_PATH);
        clock.AddFunc(this::Callback);
    }

    void Callback() {

    }

    void loadFile(String filePath) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        try {
            instrFile = new File(filePath);
            scanner = new Scanner(instrFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    String instrFetch(String PC) {
        int pcInt = Integer.parseInt(PC, 2);
        String instr = "";
        int i = 0;
        boolean foundPC = false;
        while (!foundPC && scanner.hasNextLine()) {
            if (pcInt == i) {
                for (int j = 0; j < 4; j++) {
                    instr += scanner.nextLine();
                }
                foundPC = true;
            }
            i++;
        }

        if (!foundPC) {
            instr = null;
        }

        return instr;
    }
}
