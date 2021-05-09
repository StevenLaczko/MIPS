package io.github.stevenlaczko;

import javax.imageio.stream.IIOByteBuffer;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class Execute {

    Registers reg = new Registers();

    public Execute(Registers _reg) {
        reg = _reg;
    }

    void Execute(InstructionObj iObj) {
        Instruction instr = GetInstr(iObj);
        java.lang.reflect.Method method = null;
        try {

            method = this.getClass().getMethod(instr.name(), InstructionObj.class);

        } catch (SecurityException e) { System.out.println("Security Exception: " + e); }
        catch (NoSuchMethodException e) { System.out.println("No such instruction: " + e); }

        try {
            assert method != null;
            method.invoke(this, iObj);
        } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException ignored) { }
    }

    void LW(InstructionObj iObj) {

    }

    void SW(InstructionObj iObj) {

    }

    void ADDU(InstructionObj iObj) {

    }
    void SUBU(InstructionObj iObj) {

    }
    void AND(InstructionObj iObj) {

    }
    void OR(InstructionObj iObj) {

    }

    void NOR(InstructionObj iObj) {

    }

    void BEQ(InstructionObj iObj) {

    }

    void J(InstructionObj iObj) {

    }

    void HALT(InstructionObj iObj) {

    }

    Instruction GetInstr(InstructionObj i) {
        if (i instanceof RInstructionObj) {
            return switch (((RInstructionObj) i).func) {
                case FUNC_CODE.ADDU -> Instruction.ADDU;
                case FUNC_CODE.SUBU -> Instruction.SUBU;
                case FUNC_CODE.AND -> Instruction.AND;
                case FUNC_CODE.OR -> Instruction.OR;
                case FUNC_CODE.NOR -> Instruction.NOR;
                default -> null;
            };
        }
        else if (i instanceof IInstructionObj) {
            String instrName = Arrays.stream(Opcodes.iTypes).filter(x -> x.binary.equals(i.opcode)).findFirst().get().name;
            return Instruction.valueOf(instrName);
        }
        else if (i instanceof JInstructionObj) {
            String instrName = Arrays.stream(Opcodes.jTypes).filter(x -> x.binary.equals(i.opcode)).findFirst().get().name;
            return Instruction.valueOf(instrName);
        }
        return null;
    }
}
