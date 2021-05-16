package io.github.stevenlaczko;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


class MIPS {
    static final String FILE_PATH = "src/io/github/stevenlaczko/input/Instructions1.txt";

    public static void main(String[] args) {

        CPU cpu = new CPU();
        cpu.start();
    }
}




