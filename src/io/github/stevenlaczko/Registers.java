package io.github.stevenlaczko;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;

import static io.github.stevenlaczko.CPU.*;


public class Registers {

    Clock clock;
    Mux writeMux;
    String[] register = new String[32];
    String regWrite = "0";
    HashMap<RegInput, String> inputs = new HashMap<>() {{
        put(RegInput.Read_Register_1, GetBinaryZeroStr());
        put(RegInput.Read_Register_2, GetBinaryZeroStr());
        put(RegInput.Write_Data, GetBinaryZeroStr());
        put(RegInput.Write_Register, GetBinaryZeroStr());
    }};
    HashMap<Outputs, String> outputs = new HashMap<>() {{
        put(Outputs.Read_Data_1, GetBinaryZeroStr());
        put(Outputs.Read_Data_2, GetBinaryZeroStr());
    }};

    enum RegInput {
        Read_Register_1,
        Read_Register_2,
        Write_Data,
        Write_Register
    }

    enum Outputs {
        Read_Data_1,
        Read_Data_2
    }

    enum MuxInputs {
        rt,
        rd
    }

    public Registers(Clock clock) {
        clock.AddFunc(this::Callback);
        this.clock = clock;
        writeMux = new Mux(clock);
        LoadRegisterData();
    }

    void LoadRegisterData() {
        File memFile = new File("src/io/github/stevenlaczko/input/IMemory.txt");
        Scanner scanner = null;
        try { scanner = new Scanner(memFile); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
        for (int i = 0; i < register.length; i++) {
            register[i] = scanner.nextLine().strip();
        }
    }

    void WriteToFile() throws IOException {
        String filepath = "src/io/github/stevenlaczko/output/Registers.txt";
        File regOutputFile = new File(filepath);
        try { regOutputFile.createNewFile(); }
        catch (IOException e) { e.printStackTrace(); }

        FileWriter writer = null;
        try { writer = new FileWriter(filepath); }
        catch (IOException e) { e.printStackTrace(); }
        assert writer != null;

        String regStr = String.join("\n", register);
        writer.write(regStr);
        writer.close();
    }

    void Callback() {
        if (regWrite.equals("1") && !clock.isHigh) {
            WriteIfRegWrite();
        }
        UpdateOutputs();
    }

    String ReadRegister(String addr) {
        return register[Integer.parseInt(addr)];
    }

    void SetRegWrite(String flag) {
        if (!flag.equals("1") && !flag.equals("0")) {
            throw new IllegalArgumentException();
        }
        regWrite = flag;
    }

    void WriteRegister() {
        register[Integer.parseInt(inputs.get(RegInput.Write_Register), 2)] = inputs.get(RegInput.Write_Data);
    }

    void UpdateOutputs() {
        outputs.put(Outputs.Read_Data_1, ReadRegister(inputs.get(RegInput.Read_Register_1)));
        outputs.put(Outputs.Read_Data_2, ReadRegister(inputs.get(RegInput.Read_Register_2)));
    }

    void WriteIfRegWrite() {
        if (regWrite.equals("1")) {
            WriteRegister();
        }
    }

    void SetInput(RegInput input, String value) {
        value = Format32Bits(value);
        inputs.put(input, value);

        if (input == RegInput.Write_Data || input == RegInput.Write_Register) {
            WriteIfRegWrite();
        }
    }

    String GetOutput(Outputs output) {
        UpdateOutputs();
        return outputs.get(output);
    }

    void SetWriteMuxInput(MuxInputs input, String value) {
        if (input == MuxInputs.rt) { // 0
            writeMux.SetInput(0, value);
        }
        else if (input == MuxInputs.rd) { // 1
            writeMux.SetInput(1, value);
        }
    }

    String GetWriteMuxOutput() {
        return writeMux.GetOutput();
    }

}
