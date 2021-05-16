package io.github.stevenlaczko;

import java.util.Vector;

public class Clock {
    boolean isHigh = false;
    Vector<CallbackInterface> funcs = new Vector<>();

    void SetHigh() {
        isHigh = true;
        CallFuncs();
    }

    void SetLow() {
        isHigh = false;
        CallFuncs();
    }

    void AddFunc(CallbackInterface callbackFunc) {
        funcs.add(callbackFunc);
    }

    void CallFuncs() {
        for (CallbackInterface func : funcs) {
            func.Callback();
        }
    }
}
