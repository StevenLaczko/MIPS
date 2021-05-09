package io.github.stevenlaczko;

import java.util.*;

class Opcode {
    String name;
    String binary;

    public String getName() {
        return name;
    }

    public String getBinary() {
        return binary;
    }

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

class FUNC_CODE {
    public static final String ADDU = "100001";
    public static final String SUBU = "100011";
    public static final String AND  = "100100";
    public static final String OR   = "100101";
    public static final String NOR  = "100111";
}


enum Instruction {
    LW, SW,
    ADDU, SUBU, AND, OR, NOR,
    BEQ, J,
    HALT
}

class InstructionObj {
    String opcode;
}

class RInstructionObj extends InstructionObj {

    String rs, rt, rd, shamt, func;

    public RInstructionObj(String [] _instrSections) {
        this(_instrSections[0], _instrSections[1], _instrSections[2], _instrSections[3], _instrSections[4], _instrSections[5]);
    }

    public RInstructionObj(String _opcode, String _rs, String _rt, String _rd, String _shamt, String _func) {
        opcode = _opcode;
        rs = _rs;
        rt = _rt;
        rd = _rd;
        shamt = _shamt;
        func = _func;
    }

}

class IInstructionObj extends InstructionObj {

    String rs, rt, imm;

    public IInstructionObj(String [] _instrSections) {
        this(_instrSections[0], _instrSections[1], _instrSections[2], _instrSections[3]);
    }

    public IInstructionObj(String _opcode, String _rs, String _rt, String _imm) {
        opcode = _opcode;
        rs = _rs;
        rt = _rt;
        imm = _imm;
    }

}

class JInstructionObj extends InstructionObj {

    String addr;

    public JInstructionObj(String [] _instrSections) {
        this(_instrSections[0], _instrSections[1]);
    }

    public JInstructionObj(String _opcode, String _addr) {
        opcode = _opcode;
        addr = _addr;
    }

}

public class Decoder {
    enum I_Type {
        RTYPE,
        ITYPE,
        JTYPE
    }

    Registers reg;

    public Decoder(Registers _reg) {
        reg = _reg;
    }

    // R: Opcode(6) Rs(5) Rt(5) Rd(5) Shamt(5) Func(6)
    // I: Opcode(6) Rs(5) Rt(5) Immediate(16)
    // J: Opcode(6) Word Address(26)
    InstructionObj Decode(String instrStr) {
        I_Type type = GetType(instrStr);
        return GetInstrObject(instrStr, type);
    }

    I_Type GetType(String instrStr) {
        I_Type type = null;
        String opcode = instrStr.substring(0, 6);
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

    InstructionObj GetInstrObject(String instr, I_Type type) {
        if (type == I_Type.RTYPE) {
            return new RInstructionObj(GetInstrSections(instr, type));
        }
        //TODO do ITYPE and JTYPE
        return null;
    }

    String[] GetInstrSections(String i, I_Type type) {
        if (type == I_Type.RTYPE) {
            String [] instrSections = new String[5];
            instrSections[0] = GetOpcode(i);
            instrSections[1] = GetRegAddr(i, "s");
            instrSections[2] = GetRegAddr(i, "t");
            instrSections[3] = GetRegAddr(i, "d");
            instrSections[4] = GetShamt(i);
            instrSections[5] = GetFunc(i);

            return instrSections;
        }
        //TODO do ITYPE and JTYPE

        return null;
    }

    String GetOpcode(String instr) {
        return instr.substring(0, 6);
    }

    String GetRegAddr(String instr, String regType) {
        return switch (regType) {
            case "s" -> GetRegS(instr);
            case "t" -> GetRegT(instr);
            case "d" -> GetRegD(instr);
            default -> null;
        };
    }

    String GetRegS(String instr) {
        return instr.substring(6, 11);
    }

    String GetRegT(String instr) {
        return instr.substring(11, 16);
    }

    String GetRegD(String instr) {
        return instr.substring(16, 21);
    }

    String GetShamt(String instr) {
        return instr.substring(21, 26);
    }

    String GetFunc(String instr) {
        return instr.substring(26, 32);
    }
}
