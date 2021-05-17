# MIPS
An instruction level MIPS simulator written in Java.  
This program simulates a MIPS processor with a limited instruction set and without pipelining.

# How To Run
Must have java 16 installed.
1. Open command line, cd to /MIPS/out/production
2. Then enter: java io.github.stevenlaczko.Main
3. Once the CLI appears, type "run" or "run X", where X is the input filename.

# Set Up Inputs
In the bottom of the directory you will find input and output folders.  
Place an instructions file (one byte per line) in the input folder called Instructions.txt  
Place a memory init file in the same folder called IMemory.txt
* Instructions file must have one byte per line
* Memory init file must have thirty-two 32-bit lines of memory

Alternatively, run any instructions file in the input folder by typing "run X" in the CLI where X is the filename.

Output memory and register files will appear in the output folder.

Thanks!
