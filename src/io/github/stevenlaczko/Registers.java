package io.github.stevenlaczko;

import java.util.Hashtable;


public class Registers {

    public enum REGISTERS {
        ZERO,
        AT,
        v0, v1,
        a0, a1, a2, a3,
        t0, t1, t2, t3, t4, t5, t6, t7,
        s0, s1, s2, s3, s4, s5, s6, s7,
        t8, t9,
        k0, k1,
        gp,
        sp,
        fp,
        ra,
        pc,
        INSTR
    }
    private final Hashtable<REGISTERS, Integer> registers = new Hashtable<REGISTERS, Integer>();

    public Registers() {
        for (int i = 0; i < REGISTERS.values().length; i++) {
            registers.put(REGISTERS.values()[i], 0);
        }
    }

    public int get(REGISTERS regName) {
        return this.registers.get(regName);
    }

    public void set(REGISTERS regName, int value) {
        this.registers.put(regName, value);
    }
}
