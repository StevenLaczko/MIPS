package io.github.stevenlaczko;

import java.util.Arrays;
import java.util.Scanner;

enum INPUT {
    run,
    fetch,
    decode,
    execute,
    mem,
    writeback,
    exit
}

public class CPU {
    public static final String CLI_PREFIX = "io.github.stevenlaczko.MIPS > ";
    public static final String CLI_MSG_EOI = "Reached end of instructions.";

    Registers reg = new Registers();
    InstructionMemory instrMem = new InstructionMemory();
    MemoryAccessor memAccess = new MemoryAccessor(reg);
    Writebacker writeback = new Writebacker(reg);
    ALU alu = new ALU();
    Control control = new Control();

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
            if (input.equals(INPUT.run.toString())) {
                RunProgram();
            }

            input = scanner.nextLine();
            if (input.equals(INPUT.exit.toString())) {
                cliFlag = false;
            }

            System.out.print(CLI_PREFIX);
        }

    }

    void RunProgram() {
        String PC = GetBinaryZeroStr();
        String instr;
        I_Type instrType;

        while ((instr = instrMem.instrFetch(PC)) != null) {
            String incrementedPC = alu.ADD(PC, "0100");
            instrType = GetType(instr);
            control.SetInput(BWSubstring(instr, 31,26));
            reg.SetInput(Registers.Inputs.Read_Register_1, instr.bwSubstring(21,25));
            reg.SetInput(Registers.Inputs.Read_Register_2, instr.bwSubstring(16,20));
            reg.SetWriteMuxInput(Registers.MuxInputs.rt, instr.bwSubstring(16,20));
            reg.SetWriteMuxInput(Registers.MuxInputs.rd, instr.bwSubstring(1,5));
            reg.SetInput(Registers.Inputs.Write_Data, GetBinaryZeroStr());
            reg.SetInput(Registers.Inputs.Write_Register, reg.GetWriteMuxOutput());

            String imm = SignExtend(instr.bwSubstring(15,0));

            String aluControlInput = ALU.Concat(control.GetOutput(ALUOp), BWSubstring(instr, 0, 5));
            String aluControlOutput = alu.GetALUControlFromALUOp(BWSubstring(instr, 31, 26));
            alu.SetControlInput(aluControlOutput);
            alu.SetMuxInput(1, imm);
            alu.SetMuxInput(0, reg.GetOutput(Registers.Outputs.Read_Data_2));
            alu.SetInput(0, reg.GetOutput(Registers.Outputs.Read_Data_1));
            alu.SetInput(1, alu.GetMuxOutput());
            String resultALU = alu.GetOutput();

            dataMem.SetInput("Address", resultALU);
            dataMem.SetInput("Write Data", reg.GetOutput("Read Data 2"));

            SetMemToRegMuxInput(1, dataMem.GetOutput("Read Data"));
            SetMemToRegMuxInput(0, resultALU);

            reg.SetInput("Write Data", GetMemToRegMuxOutput());

            HandlePCMuxes();
            SetBranchMuxInput(0, incrementedPC);
            SetBranchMuxInput(1, ALU.ADD(incrementedPC, ALU.ShiftLeft2(imm)));
            SetBranchMuxControlInput(ALU.AND(Control.GetBranch(), ALU.GetZeroFlag));

            SetJumpMuxInput(0, GetBranchMuxOutput());
            SetJumpMuxInput(1, ALU.Concat(BWSubstring(incrementedPC, 31,28), ALU.ShiftLeft2(BWSubstring(instr, 25,0))));
            SetJumpMuxControlInput(control.GetOutput("Jump"));

            PC = GetJumpMuxOutput();
        }

        System.out.println(CLI_MSG_EOI);
    }

    static String BWSubstring(String s, int i, int j) {
        i = (s.length() - 1) - i;
        j = (s.length() - 1) - j;
        return s.substring(i, j);
    }

    String GetBinaryZeroStr() {
        return "00000000000000000000000000000000";
    }

    static I_Type GetType(String instrStr) {
        I_Type type = null;
        String opcode = BWSubstring(instrStr, 31, 26);
        if (opcode.equals(Opcodes.rType.name)) {
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

    String ADD(String a, String b) {
        int x = Integer.parseInt(a, 2);
        int y = Integer.parseInt(b, 2);
        return Format32Bits(Integer.toBinaryString(x + y));
    }

    static String Format32Bits(String s) {
        int length = 32;
        if (s.length() >= length) {
            return s;
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - s.length()) {
            sb.append('0');
        }
        sb.append(s);

        return sb.toString();
    }

}
// TODO else if (i instanceof IInstructionObj)
