package io.github.stevenlaczko;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import io.github.stevenlaczko.Registers.REGISTERS;

import static io.github.stevenlaczko.MIPS.FILE_PATH;

public class Fetcher {

    String programBytes;
    String [] instrArray;
    Registers reg;

    public Fetcher(Registers _reg) {
        reg = _reg;
        loadFile(FILE_PATH);
    }

    void loadFile(String filePath) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));
        File fileObj;
        try {
            fileObj = new File(filePath);
            Scanner myReader = new Scanner(fileObj);
            programBytes = "";
            while(myReader.hasNextLine()) {
                programBytes += myReader.nextLine() + "\n";
            }
            instrArray = programBytes.split("\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    String instrFetch() {
        if (reg.get(REGISTERS.pc) < instrArray.length) {
            StringBuilder instrStrBuild = new StringBuilder();
            for (int i = reg.get(REGISTERS.pc); i < reg.get(REGISTERS.pc) + 4; i++) {
                if (i >= instrArray.length) return null;
                instrStrBuild.append(instrArray[i]);
            }
            reg.set(REGISTERS.pc, reg.get(REGISTERS.pc) + 4);
            return instrStrBuild.toString();
        }

        return null;
    }
}
