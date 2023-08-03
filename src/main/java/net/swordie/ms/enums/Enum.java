package net.swordie.ms.enums;

public enum Enum {

    test(-2),

    ;
    final int val;

    Enum(int val) {
        this.val = val;
    }
    Enum(Enum e, int offset) {
        this.val = e.val + offset;
    }
}
