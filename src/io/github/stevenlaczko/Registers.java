package io.github.stevenlaczko;

import java.util.HashMap;
import java.util.Hashtable;


public class Registers {

    Mux writeMux = new Mux();
    String[] register = new String[32];
    HashMap<Inputs, String> inputs = new HashMap<>();
    HashMap<Outputs, String> outputs = new HashMap<>();

    enum Inputs {
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

    void SetInput(Inputs input, String value) {
        inputs.put(input, value);
    }

    String GetOutput(Outputs output) {
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
