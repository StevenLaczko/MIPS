package io.github.stevenlaczko;

import java.util.HashMap;

public class Mux {

    String[] inputs = new String[2];
    int controlInput = 0;

    void SetInput(int input, String value) {
        inputs[input] = value;
    }

    void SetControlInput(int input) {
        controlInput = input;
    }

    String GetOutput() {
        //TODO make sure this is right
        return inputs[controlInput];
    }

}
