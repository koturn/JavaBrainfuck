package org.koturn.brainfuck;

class BfInstruction {
    enum InstructionType {
        Add,
        Sub,
        Prev,
        Next,
        Putchar,
        Getchar,
        LoopStart,
        LoopEnd,
        AssignZero,
        Unknown
    }

    private InstructionType type;
    /**
     * Operand 1
     */
    private int value1;
    /**
     * Operand 2
     */
    private int value2;

    public BfInstruction() {
        this(InstructionType.Unknown, 0, 0);
    }

    public BfInstruction(InstructionType type) {
        this(type, 0, 0);
    }

    public BfInstruction(InstructionType type, int value1) {
        this(type, value1, 0);
    }

    public BfInstruction(InstructionType type, int value1, int value2) {
        this.type = type;
        this.value1 = value1;
        this.value2 = value2;
    }

    public InstructionType getType() {
        return type;
    }

    public void setType(InstructionType type) {
        this.type = type;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }
}
