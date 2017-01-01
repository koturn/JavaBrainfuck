package org.koturn.brainfuck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;


/**
 * Brainfuck interpretor
 */
public class Brainfuck {
    /**
     * Execution mode
     */
    public enum ExecuteMode {
        /**
         * Execute brainfuck without compile
         */
        Normal,
        /**
         * Execute compiled brainfuck
         */
        Compiled
    }

    /**
     * Size of buffer used for read InpuStream
     */
    private static final int BUFFER_SIZE = 65536;
    /**
     * Heap size for brainfuck execution
     */
    private static final int DEFAULT_HEAP_SIZE = 65536;
    /**
     * Character-BfInstruction conversion map
     */
    private static final HashMap<Byte, BfInstruction.InstructionType> InstructionMap = new HashMap<>();

    static {
        InstructionMap.put((byte)'+', BfInstruction.InstructionType.Add);
        InstructionMap.put((byte)'-', BfInstruction.InstructionType.Sub);
        InstructionMap.put((byte)'>', BfInstruction.InstructionType.Next);
        InstructionMap.put((byte)'<', BfInstruction.InstructionType.Prev);
        InstructionMap.put((byte)'.', BfInstruction.InstructionType.Putchar);
        InstructionMap.put((byte)',', BfInstruction.InstructionType.Getchar);
        InstructionMap.put((byte)'[', BfInstruction.InstructionType.LoopStart);
        InstructionMap.put((byte)']', BfInstruction.InstructionType.LoopEnd);
    }

    /**
     * Brainfuck source code
     */
    private byte[] bfSource;
    /**
     * Brainfuck IR-code
     */
    private ArrayList<BfInstruction> ircode;
    /**
     * Execution mode
     */
    private ExecuteMode mode;

    /**
     * Ctor
     */
    public Brainfuck() {
    }

    /**
     * Load brainfuck source code from a file
     * @param filepath  Path to brainfuck source code
     * @throws IOException  Throw when something error is occured while reading a file
     */
    public void load(String filepath) throws IOException {
        bfSource = Files.readAllBytes(Paths.get(filepath));
        mode = ExecuteMode.Normal;
    }

    /**
     * Load brainfuck soruce code from specified InputStream
     * @param is  InputStream of brainfuck source code
     * @throws IOException  Throw when something error is occured while reading {@code InputStream}
     */
    public void load(InputStream is) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte [] buffer = new byte[BUFFER_SIZE];
        int nBytes;
        while ((nBytes = is.read(buffer)) >= 0) {
            os.write(buffer, 0, nBytes);
        }
        bfSource = os.toByteArray();
        mode = ExecuteMode.Normal;
    }

    /**
     * Load brainfuck source code from a String
     * @param bfSource  Brainfuck source code
     */
    public void loadString(String bfSource) {
        this.bfSource = bfSource.getBytes();
        mode = ExecuteMode.Normal;
    }

    /**
     * Compile brainfuck source code to IR-code
     */
    public void compile() {
        Stack<Integer> loopStack = new Stack<>();
        ircode = new ArrayList<>(bfSource.length >> 1);
        for (int i = 0; i < bfSource.length; i++) {
            switch (bfSource[i]) {
                case '+':
                case '-':
                case '>':
                case '<':
                    {
                        int cnt = countConsecutiveCharacters(bfSource[i], bfSource, i + 1);
                        ircode.add(new BfInstruction(InstructionMap.get(bfSource[i]), cnt + 1));
                        i += cnt;
                    }
                    break;
                case '.':
                case ',':
                    ircode.add(new BfInstruction(InstructionMap.get(bfSource[i])));
                    break;
                case '[':
                    if (i + 2 < bfSource.length && bfSource[i + 1] == '-' && bfSource[i + 2] == ']') {
                        ircode.add(new BfInstruction(BfInstruction.InstructionType.AssignZero));
                        i += 2;
                    } else {
                        loopStack.push(ircode.size());
                        ircode.add(new BfInstruction(BfInstruction.InstructionType.LoopStart));
                    }
                    break;
                case ']':
                    {
                        int loopStartIdx = loopStack.pop();
                        ircode.get(loopStartIdx).setValue1(ircode.size());
                        ircode.add(new BfInstruction(BfInstruction.InstructionType.LoopEnd, loopStartIdx));
                    }
                    break;
            }
        }
        mode = ExecuteMode.Compiled;
    }

    /**
     * Execute brainfuck.
     * @param heapSize  Runtime heap size
     * @param mode      Execution mode
     * @throws IOException  Throw when something error is occured while reading from stdin
     */
    public void execute(int heapSize, ExecuteMode mode) throws IOException {
        switch (mode) {
            case Normal:
                executeNormal(heapSize);
                break;
            case Compiled:
                if (this.mode == ExecuteMode.Normal) {
                    compile();
                }
                executeIR(heapSize);
                break;
        }
    }

    /**
     * Execute brainfuck with {@code heapSize}.
     * @param heapSize  Runtime heap size
     * @throws IOException  Throw when something error is occured while reading from stdin
     * @see #execute(int, ExecuteMode)
     */
    public void execute(int heapSize) throws IOException {
        execute(heapSize, mode);
    }

    /**
     * Execute brainfuck with {@link #DEFAULT_HEAP_SIZE}.
     * @param mode  Execution mode
     * @throws IOException  Throw when something error is occured while reading from stdin
     * @see #execute(int, ExecuteMode)
     */
    public void execute(ExecuteMode mode) throws IOException {
        execute(DEFAULT_HEAP_SIZE, mode);
    }

    /**
     * Execute brainfuck with {@link #DEFAULT_HEAP_SIZE}.
     * @throws IOException  Throw when something error is occured while reading from stdin
     * @see #execute(int, ExecuteMode)
     */
    public void execute() throws IOException {
        execute(DEFAULT_HEAP_SIZE, mode);
    }

    /**
     * Execute brainfuck source code directly
     * @param heapSize  Runtime heap size
     * @throws IOException  Throw when something error is occured while reading from stdin
     */
    private void executeNormal(int heapSize) throws IOException {
        byte[] heap = new byte[heapSize];
        int hp = 0;
        for (int pc = 0; pc < bfSource.length; pc++) {
            switch (bfSource[pc]) {
                case '+':
                    heap[hp]++;
                    break;
                case '-':
                    heap[hp]--;
                    break;
                case '>':
                    hp++;
                    break;
                case '<':
                    hp--;
                    break;
                case '.':
                    System.out.write(heap[hp]);
                    break;
                case ',':
                    System.out.flush();
                    heap[hp] = (byte) System.in.read();
                    break;
                case '[':
                    if (heap[hp] != 0) {
                        break;
                    }
                    pc++;
                    for (int depth = 1; depth > 0; pc++) {
                        switch (bfSource[pc]) {
                            case '[':
                                depth++;
                                break;
                            case ']':
                                depth--;
                                break;
                        }
                    }
                    pc--;
                    break;
                case ']':
                    if (heap[hp] == 0) {
                        break;
                    }
                    pc--;
                    for (int depth = 1; depth > 0; pc--) {
                        switch (bfSource[pc]) {
                            case '[':
                                depth--;
                                break;
                            case ']':
                                depth++;
                                break;
                        }
                    }
                    pc++;
                    break;
            }
        }
    }

    /**
     * Execute brainfuck with specified heap size
     * @param heapSize  Runtime Heap size
     * @throws IOException  Throw when something error is occured while reading from stdin
     */
    private void executeIR(int heapSize) throws IOException {
        byte[] heap = new byte[heapSize];
        int hp = 0;
        BfInstruction inst;
        for (int pc = 0; pc < ircode.size(); pc++) {
            switch ((inst = ircode.get(pc)).getType()) {
                case Add:
                    heap[hp] += inst.getValue1();
                    break;
                case Sub:
                    heap[hp] -= inst.getValue1();
                    break;
                case Next:
                    hp += inst.getValue1();
                    break;
                case Prev:
                    hp -= inst.getValue1();
                    break;
                case Putchar:
                    System.out.write(heap[hp]);
                    break;
                case Getchar:
                    System.out.flush();
                    heap[hp] = (byte) System.in.read();
                    break;
                case LoopStart:
                    if (heap[hp] == 0) {
                        pc = inst.getValue1();
                    }
                    break;
                case LoopEnd:
                    if (heap[hp] != 0) {
                        pc = inst.getValue1();
                    }
                    break;
                case AssignZero:
                    heap[hp] = 0;
                    break;
                default:
            }
        }
        System.out.println();
    }

    /**
     * Count how many specified characters are consecutive from specified position
     * @param c         Target character
     * @param bfSource  Brainfuck source code
     * @param from      Count start position
     * @return  Number of consecutive characters
     */
    private int countConsecutiveCharacters(byte c, byte[] bfSource, int from) {
        int to;
        for (to = from; to < bfSource.length && bfSource[to] == c; to++);
        return to - from;
    }
}