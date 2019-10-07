package com.example.forgetpreventer.Utils;

public class Part {

    private int start;
    private int end;
    private int operation;

    public Part(int start, int end) {
        this.start = start;
        this.end = end;
    }

    public Part (int operation, int start, int end){
        this.operation = operation;
        this.start = start;
        this.end = end;
    }

    public int getStart() {
        return this.start;
    }

    public int getEnd() {
        return this.end;
    }

    public int getOperation() {
        return this.operation;
    }

    public boolean isValid() {
        return this.start < this.end;
    }

}
