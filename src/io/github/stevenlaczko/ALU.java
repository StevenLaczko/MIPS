package io.github.stevenlaczko;

import static io.github.stevenlaczko.CPU.*;
import static io.github.stevenlaczko.CPU.BWSubstring;
import static io.github.stevenlaczko.CPU.Format32Bits;

public class ALU {
    final String ALU_ADD = "0010";
    final String ALU_SUB = "0110";
    final String ALU_AND = "0000";
    final String ALU_OR = "0001";
    final String ALU_NOR = "1110";
    final String ALU_SLT = "0111";

    final String FUNC_ADDU = "100001";
    final String FUNC_SUBU = "100011";
    final String FUNC_AND  = "100100";
    final String FUNC_OR   = "100101";
    final String FUNC_NOR  = "100111";

    Clock clock;
    Mux aluMux;
    String[] inputs = {GetBinaryZeroStr(), GetBinaryZeroStr()};
    String controlInput;
    String zeroFlag = "0";
    String output;

    public ALU(Clock clock) {
        clock.AddFunc(this::Callback);
        this.clock = clock;
        aluMux = new Mux(clock);
    }

    void Callback() {

    }

    String GetZeroFlag() {
        return zeroFlag;
    }

    String GetOutput() {
        zeroFlag = "0";
        int a = Integer.parseInt(inputs[0], 2);
        int b = Integer.parseInt(inputs[1], 2);
        String output = switch (controlInput) {
            case ALU_ADD -> ADD(a, b);
            case ALU_SUB -> SUB(a, b);
            case ALU_AND -> AND(a, b);
            case ALU_OR ->  OR(a, b);
            case ALU_NOR -> NOR(a, b);
            default -> null;
        };
        assert output != null;
        if (Integer.parseInt(output, 2) == 0) {
            zeroFlag = "1";
        }

        return output;
    }

    void SetInput(int input, String value) {
        inputs[input] = value;
    }

    void SetControlInput(String aluControlInput) {
        if (aluControlInput.length() == 4) {
            controlInput = aluControlInput;
        } else {
            throw new IllegalArgumentException("aluControlInput was " + aluControlInput.length() + ". Should be 4.");
        }
    }

    String GetALUControlFromALUOp(String aluControlInput) {
        // input = func fr/ instr + ALUOp (2-bits fr/ control)
        // length of 8

        String aluOp = BWSubstring(aluControlInput, 7, 6);
        String func = BWSubstring(aluControlInput, 5, 0);
        if (aluControlInput.equals(SignExtend("1"))) {

        }
        return switch (aluOp) {
            case "00" -> ALU_ADD; // sw, lw, or addiu
            case "01" -> ALU_SUB; // beq
            case "10" ->  // R-type instr
                    switch (func) {
                        case FUNC_ADDU -> ALU_ADD;
                        case FUNC_SUBU -> ALU_SUB;
                        case FUNC_AND  -> ALU_AND;
                        case FUNC_OR   -> ALU_OR;
                        case FUNC_NOR  -> ALU_NOR;
                        default -> null;
                    };
            default -> null;
        };
    }

    String ShiftLeft(String imm, int num) {
        StringBuilder sb = new StringBuilder();
        sb.append(imm);
        sb.append("0".repeat(Math.max(0, num)));

        return sb.toString();
    }

    void LW() {

    }

    void SW() {

    }

    String ADD(int a, int b) {
        output = Format32Bits(Integer.toBinaryString(a + b));
        return output;
    }

    String ADD(String s1, String s2) {
        int a = Integer.parseInt(s1, 2);
        int b = Integer.parseInt(s2, 2);
        return Format32Bits(Integer.toBinaryString(a + b));
    }

    String SUB(int a, int b) {
        output = Format32Bits(Integer.toBinaryString(a - b));
        return output;
    }

    String AND(int a, int b) {
        String s1 = Format32Bits(Integer.toBinaryString(a));
        String s2 = Format32Bits(Integer.toBinaryString(b));
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            if (s1.charAt(i) == s2.charAt(i)) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    String AND(String s1, String s2) {
        s1 = Format32Bits(s1);
        s2 = Format32Bits(s2);
        StringBuilder sb = new StringBuilder();

        if (s1.length() != 1 || s2.length() != 1) {
            for (int i = 0; i < 32; i++) {
                if (s1.charAt(i) == '1' && s2.charAt(i) == '1') {
                    sb.append('1');
                } else {
                    sb.append('0');
                }
            }
        }

        return sb.toString();
    }

    String OR(int a, int b) {
        String s1 = Format32Bits(Integer.toBinaryString(a));
        String s2 = Format32Bits(Integer.toBinaryString(b));
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            if (s1.charAt(i) == '1' || s2.charAt(i) == '1') {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }

        return sb.toString();
    }

    String NOR(int a, int b) {
        String s1 = Format32Bits(Integer.toBinaryString(a));
        String s2 = Format32Bits(Integer.toBinaryString(b));
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < 32; i++) {
            if (s1.charAt(i) == '1' || s2.charAt(i) == '1') {
                sb.append('0');
            } else {
                sb.append('1');
            }
        }

        return sb.toString();
    }

    void BEQ() {

    }

    void J() {

    }

    void HALT() {

    }
}
