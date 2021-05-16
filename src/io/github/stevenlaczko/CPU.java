package io.github.stevenlaczko;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import static io.github.stevenlaczko.ALU.*;
import static io.github.stevenlaczko.Control.*;
import static io.github.stevenlaczko.DataMemory.*;
import static io.github.stevenlaczko.Registers.*;

enum INPUT {
    run,
    fetch,
    decode,
    execute,
    mem,
    writeback,
    exit
}

enum I_Type {
    RTYPE,
    ITYPE,
    JTYPE
}

class Opcode {
    String name;
    String binary;

    public Opcode(String _name, String _binary) {
        name = _name;
        binary = _binary;
    }
}

class Opcodes {
    public static final Opcode rType = new Opcode("rType", "000000");
    public static final Opcode[] iTypes = new Opcode[] {
            new Opcode("ADDIU", "001001"),
            new Opcode("BEQ", "000100"),
            new Opcode("LW", "100011"),
            new Opcode("SW", "101011")
    };
    public static final Opcode[] jTypes = new Opcode[] {
            new Opcode("HALT", "111111"),
            new Opcode("J", "000010")
    };
}

@FunctionalInterface
interface CallbackInterface {
    public void Callback();
}


public class CPU {
    public static final String CLI_PREFIX = "io.github.stevenlaczko.MIPS > ";
    public static final String CLI_MSG_EOI = "Reached end of instructions.";
    public static final String HALT = SignExtend("1");

    static Clock clock;

    Registers reg;
    InstructionMemory instrMem;
    DataMemory dataMem;
    ALU alu;
    Control control;
    Mux branchMux;
    Mux jumpMux;

    public int start() {

        Scanner scanner = new Scanner(System.in);
        CLI(scanner);

        return 0;
    }

    void CLI(Scanner scanner) {
        // CLI Interface
        System.out.print(CLI_PREFIX);
        boolean cliFlag = true;
        String input = scanner.nextLine();
        while (cliFlag) {
            if (input.startsWith(INPUT.run.toString())) {
                String[] inputTokens = input.split(" ");

                try { RunProgram(inputTokens[1]); }
                catch (IOException e) { e.printStackTrace(); }
            }

            input = scanner.nextLine();
            if (input.equals(INPUT.exit.toString())) {
                cliFlag = false;
            }

            System.out.print(CLI_PREFIX);
        }

    }

    void RunProgram(String instrFile) throws IOException {
        String PC = GetBinaryZeroStr();
        String instr;
        boolean flag = false;

        clock = new Clock();
        reg = new Registers(clock);
        instrMem = new InstructionMemory(instrFile);
        dataMem = new DataMemory(clock);
        alu = new ALU(clock);
        control = new Control(clock);
        branchMux = new Mux(clock);
        jumpMux = new Mux(clock);


        while ((instr = instrMem.instrFetch(PC)) != null && !instr.equals(HALT)) {
            clock.SetHigh();
            String incrementedPC = alu.ADD(PC, "0100");

            // Control
            control.SetInput(BWSubstring(instr, 31,26));

            if (flag) {
                System.out.println("Before reg set:\n" + InterpretInstruction(instr));
            } else {
                flag = true;
            }

            // Registers
            reg.SetRegWrite(control.GetLine(ControlLine.RegWrite));
            reg.SetInput(RegInput.Read_Register_1, BWSubstring(instr, 25,21));
            reg.SetInput(RegInput.Read_Register_2, BWSubstring(instr, 20,16));

            reg.writeMux.SetControlInput(control.GetLine(ControlLine.RegDst));
            reg.writeMux.SetInput(0, BWSubstring(instr, 20,16));
            reg.writeMux.SetInput(1, BWSubstring(instr, 15,11));

            reg.SetInput(RegInput.Write_Data, GetBinaryZeroStr());
            reg.SetInput(RegInput.Write_Register, reg.GetWriteMuxOutput());

            System.out.println("After reg set:\n" + InterpretInstruction(instr));

            // Calc immediate
            String imm = SignExtend(BWSubstring(instr, 15,0));

            // ALU
            String aluControlInput = control.GetLine(ControlLine.ALUOp) + BWSubstring(instr, 5, 0);
            String aluControlOutput = alu.GetALUControlFromALUOp(aluControlInput);
            alu.SetControlInput(aluControlOutput);

            alu.aluMux.SetInput(1, imm);
            alu.aluMux.SetInput(0, reg.GetOutput(Outputs.Read_Data_2));
            alu.aluMux.SetControlInput(control.GetLine(ControlLine.ALUSrc));

            alu.SetInput(0, reg.GetOutput(Outputs.Read_Data_1));
            alu.SetInput(1, alu.aluMux.GetOutput());

            String resultALU = alu.GetOutput();

            // Data Memory
            dataMem.memWriteFlag = control.GetLine(ControlLine.MemWrite);
            dataMem.memReadFlag = control.GetLine(ControlLine.MemRead);
            dataMem.SetInput(DMInput.Address, resultALU);
            dataMem.SetInput(DMInput.WriteData, reg.GetOutput(Outputs.Read_Data_2));

            dataMem.memToRegMux.SetControlInput(control.GetLine(ControlLine.MemtoReg));
            dataMem.memToRegMux.SetInput(1, dataMem.GetOutput());
            dataMem.memToRegMux.SetInput(0, resultALU);

            reg.SetInput(RegInput.Write_Data, dataMem.memToRegMux.GetOutput());

            branchMux.SetControlInput((control.GetLine(ControlLine.Branch).equals("1") && alu.GetZeroFlag().equals("1")) ? "1" : "0");
            branchMux.SetInput(0, incrementedPC);
            branchMux.SetInput(1, alu.ADD(incrementedPC, alu.ShiftLeft(imm, 2)));

            jumpMux.SetControlInput(control.GetLine(ControlLine.Jump));
            jumpMux.SetInput(0, branchMux.GetOutput());
            jumpMux.SetInput(1, BWSubstring(incrementedPC, 31,28) + alu.ShiftLeft(BWSubstring(instr, 25,0), 2));

            PC = jumpMux.GetOutput();
            clock.SetLow(); // Writes to registers
        }

        WriteOutputToFile();

        System.out.println(CLI_MSG_EOI);
    }

    void WriteOutputToFile() throws IOException {
        dataMem.WriteToFile();
        reg.WriteToFile();
    }

    static String SignExtend(String s16bits) {
        int length = 32;
        if (s16bits.length() >= length) {
            return s16bits;
        }
        boolean isNeg = (s16bits.charAt(0) == '1');
        StringBuilder sb = new StringBuilder();

        for (int i = s16bits.length(); i < 32; i++) {
            if (isNeg) {
                sb.append('1');
            } else {
                sb.append('0');
            }
        }
        sb.append(s16bits);

        return sb.toString();
    }

    static String BWSubstring(String s, int i, int j) {
        i = (s.length() - 1) - i;
        j = (s.length() - 1) - j + 1;
        return s.substring(i, j);
    }

    static String GetBinaryZeroStr() {
        return "00000000000000000000000000000000";
    }

    static String Format32Bits(String s) {
        int length = 32;
        if (s.length() >= length) {
            return s;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("0".repeat(32 - s.length()));
        sb.append(s);

        return sb.toString();
    }

    static I_Type GetType(String instrStr) {
        I_Type type = null;
        String opcode = BWSubstring(instrStr, 31, 26);
        if (opcode.equals(Opcodes.rType.binary)) {
            type = I_Type.RTYPE;
        }
        else if (Arrays.stream(Opcodes.iTypes).anyMatch(x -> opcode.equals(x.binary))) {
            type = I_Type.ITYPE;
        }
        else if (Arrays.stream(Opcodes.jTypes).anyMatch(x -> opcode.equals(x.binary))) {
            type = I_Type.JTYPE;
        }

        return type;
    }

    String InterpretInstruction(String instr) {
        String opcode = instr.substring(0, 6);
        String rs = instr.substring(6, 11);
        String rt = instr.substring(11, 16);
        String rd = instr.substring(16, 21);
        String shamt = instr.substring(21, 26);
        String funct = instr.substring(26, 32);
        I_Type type = GetType(instr);


        String readableInstr = instr + "\nType: " + type + "\n Opcode: ";

        for (int i = 0; i < OPToOutput.values().length; i++) {
            OPToOutput output = OPToOutput.values()[i];
            if (output.opcode.equals(opcode)) {
                readableInstr += output.name();
                break;
            }
        }

        if (type != I_Type.JTYPE) {
            readableInstr += "\n RS: " + Long.parseLong(rs, 2);
            readableInstr += "\nRS value = " + reg.GetOutput(Outputs.Read_Data_1);
            readableInstr += "\n RT: " + Long.parseLong(rt, 2);
            readableInstr += "\nRT value = " + reg.GetOutput(Outputs.Read_Data_2);

            if (type == I_Type.RTYPE) {
                readableInstr += "\n RD: " + (int)Long.parseLong(rd, 2) + "\n";
                String funcStr = switch (funct) {
                    case FUNC_ADDU -> "Addu";
                    case FUNC_SUBU -> "Subu";
                    case FUNC_AND  -> "AND";
                    case FUNC_OR   -> "OR";
                    case FUNC_NOR  -> "NOR";
                    default -> null;
                };
                readableInstr += "Func: " + funcStr + "\n";
            } else {
                readableInstr += "\nImmediate: " + (int)Long.parseLong(instr.substring(16, 32), 2);
            }
        } else {
            readableInstr += "Jump Address: " + (int)Long.parseLong(instr.substring(6, 32), 2);
        }

        readableInstr += "\n\n";


        return readableInstr;
    }

}
// TODO else if (i instanceof IInstructionObj)
