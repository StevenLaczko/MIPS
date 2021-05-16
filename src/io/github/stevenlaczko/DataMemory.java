package io.github.stevenlaczko;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;

public class DataMemory {

    Clock clock;
    String[] inputs = new String[2];
    Vector<String> dataMemory;
    Mux memToRegMux;

    String memReadFlag = "0";
    String memWriteFlag = "0";

    File dataFile;
    Scanner scanner;

    enum DMInput {
        Address(0),
        WriteData(1);

        final int value;
        DMInput(int _value) {
            this.value = _value;
        }
    }

    public DataMemory(Clock clock) {
        clock.AddFunc(this::Callback);
        this.clock = clock;
        memToRegMux = new Mux(clock);

        dataFile = new File("src/io/github/stevenlaczko/input/IMemory.txt");
        try {
            scanner = new Scanner(dataFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        dataMemory = new Vector<>();
        while (scanner.hasNextLine()) {
            dataMemory.add(scanner.nextLine().strip());
        }
    }

    void WriteToFile() throws IOException {
        String filepath = "src/io/github/stevenlaczko/output/Memory.txt";
        File memOutputFile = new File(filepath);
        try { memOutputFile.createNewFile(); }
        catch (IOException e) { e.printStackTrace(); }

        FileWriter writer = null;
        try { writer = new FileWriter(filepath); }
        catch (IOException e) { e.printStackTrace(); }
        assert writer != null;

        String memStr = String.join("\n", dataMemory);
        writer.write(memStr);
        writer.close();
    }

    void Callback() {

    }

    void SetInput(DMInput input, String value) {
        inputs[input.value] = value;

        if (memWriteFlag.equals("1")
                && inputs[DMInput.WriteData.value] != null
                && inputs[DMInput.Address.value] != null) {
            WriteData();
        }
    }

    int GetAddrDecimal() {
        return Integer.parseInt(inputs[DMInput.Address.value]);
    }

    void WriteData() {
        int lineNum = GetAddrDecimal();
        dataMemory.set(lineNum, inputs[DMInput.WriteData.value]);
    }

    String ReadData() {
        if (memReadFlag.equals("1")) {
            return dataMemory.get(GetAddrDecimal());
        } else {
            return null;
        }
    }

    String GetOutput() {
        return ReadData();
    }
}
