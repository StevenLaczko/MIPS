package io.github.stevenlaczko;

public class Control {

    String inputs;
    boolean RegDst, Jump, Branch, MemRead, MemtoReg, ALUOp, MemWrite, ALUSrc, RegWrite;

    public Control() {

    }

    void SetInput(String opcode) {
        inputs = opcode;
    }

    void GenerateOutputs() {
        switch (inputs) {
            case "000000" ->
        }
    }
}
