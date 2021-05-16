package io.github.stevenlaczko;

public class Mux {

    private final String[] inputs = new String[2];
    private int controlInput = 0;

    public Mux(Clock clock) {
        clock.AddFunc(this::Callback);
    }

    void Callback() {

    }

    void SetInput(int input, String value) {
        inputs[input] = value;
    }

    void SetControlInput(String input) {
        controlInput = (int)Long.parseLong(input, 2);

        if (controlInput != 0 && controlInput != 1) {
            throw new IllegalArgumentException();
        }
    }

    String GetOutput() {
        return inputs[controlInput];
    }
}
