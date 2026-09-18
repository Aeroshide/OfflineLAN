package com.aeroshide.offlinelan;

// --gotta do this quick ignore how im doing this rip 16 bytes kappa--
// yea i am not replacing this, GO MY OOP SLOP
// not a record because we target <Java 17, and it's net zero on memory footprint anyway (trust me i searched)
public class OnlineModeBoolean {
    boolean onlineMode;

    OnlineModeBoolean(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }

    public void setOnlineMode(boolean onlineMode) {
        this.onlineMode = onlineMode;
    }

    public boolean getOnlineMode() {
        return this.onlineMode;
    }
}
