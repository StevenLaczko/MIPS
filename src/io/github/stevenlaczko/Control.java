package io.github.stevenlaczko;

import java.util.HashMap;

public class Control {

    Clock clock;
    String opcodeStr;
    HashMap<ControlLine, String> outputs = new HashMap<>();

    enum ControlLine {
        RegDst, ALUSrc, MemtoReg, RegWrite, MemRead, MemWrite, Branch, Jump, ALUOp
    }

    enum OPToOutput {
        RType("000000", "1001000010"),
        ADDIU("001001", "0101000000"),
        LW("100011", "0111100000"),
        SW("101011", "0100010000"),
        BEQ("000100", "0000001001"),
        J("000010", "0000000100"),
        HALT("111111", "1111111111");

        public final String opcode;
        public final String controlOutput;
        OPToOutput(String _opcode, String _controlOutput) {
            this.opcode = _opcode;
            this.controlOutput = _controlOutput;
        }
    }

    public Control(Clock clock) {
        clock.AddFunc(this::Callback);
        this.clock = clock;
    }

    void Callback() {
        if (!clock.isHigh) {
            GenerateControlLinesFromOpcode();
        }
    }

    void SetInput(String opcode) {
        opcodeStr = opcode;
        GenerateControlLinesFromOpcode();
    }

    String GetLine(ControlLine line) {
        return outputs.get(line);
    }

    void GenerateControlLinesFromOpcode() {
        String outputStr = "";
        for (int i = 0; i < OPToOutput.values().length; i++) {
            OPToOutput output = OPToOutput.values()[i];
            if (output.opcode.equals(opcodeStr)) {
                outputStr = output.controlOutput;
                break;
            }
        }

        int len = outputStr.length();
        String[] flagsAndAluOp = {
                outputStr.substring(0, len - 2),
                outputStr.substring(len - 2)
        };

        AssignOutputHashMap(flagsAndAluOp);
    }

    void AssignOutputHashMap(String[] flagsAndAluOp) {
        for (int i = 0; i < ControlLine.values().length - 1; i++) {
            ControlLine line = ControlLine.values()[i];
            outputs.put(line, String.valueOf(flagsAndAluOp[0].charAt(i)));
        }
        outputs.put(ControlLine.ALUOp, flagsAndAluOp[1]);
    }
}
