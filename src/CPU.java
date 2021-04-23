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
    public static final String CLI_PREFIX = "MIPS > ";

    Fetcher fetcher = new Fetcher();
    Decoder decoder = new Decoder();
    Execute executor = new Execute();
    MemoryAccessor memAccess = new MemoryAccessor();
    Writebacker writeback = new Writebacker();

    int start() {
        boolean cliFlag = true;
        Scanner scanner = new Scanner(System.in);

        System.out.println(CLI_PREFIX);
        String input = scanner.nextLine();
        while (cliFlag) {
            if (INPUT.run.toString().equals(input)) {
                fetcher.getInstruction();
            }

            input = scanner.nextLine();
            if (input.equals(INPUT.exit.toString())) {
                cliFlag = false;
            }
        }

        return 0;
    }
}
