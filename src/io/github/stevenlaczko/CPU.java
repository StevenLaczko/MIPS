package io.github.stevenlaczko;

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
    Fetcher fetcher = new Fetcher(reg);
    Decoder decoder = new Decoder(reg);
    Execute executor = new Execute(reg);
//    MemoryAccessor memAccess = new MemoryAccessor(reg);
//    Writebacker writeback = new Writebacker(reg);

    public int start() {

        boolean cliFlag = true;
        Scanner scanner = new Scanner(System.in);

        // CLI Interface
        System.out.print(CLI_PREFIX);
        String input = scanner.nextLine();
        while (cliFlag) {
            if (input.equals(INPUT.run.toString())) {
                String instr = fetcher.instrFetch();
                while (instr != null) {
                    HandleInstr(instr);
                    instr = fetcher.instrFetch();
                }

                System.out.println(CLI_MSG_EOI);
            }

            input = scanner.nextLine();
            if (input.equals(INPUT.exit.toString())) {
                cliFlag = false;
            }

            System.out.print(CLI_PREFIX);
        }

        return 0;
    }

    void HandleInstr(String instrStr) {
        InstructionObj instrObj = decoder.Decode(instrStr);
        executor.Execute(instrObj);
    }

}
// TODO else if (i instanceof IInstructionObj)
