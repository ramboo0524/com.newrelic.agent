//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.newrelic.agent.android.unity;

import android.text.TextUtils;

public class UnityException extends RuntimeException {
    private String sourceExceptionType = null;

    public UnityException() {
    }

    public UnityException(String sourceExceptionType, String detailMessage) {
        super(detailMessage);
        this.sourceExceptionType = sourceExceptionType;
    }

    public UnityException(String detailMessage) {
        super(detailMessage);
    }

    public UnityException(String detailMessage, StackTraceElement[] stackTraceElements) {
        super(detailMessage);
        this.setStackTrace(stackTraceElements);
    }

    public void appendStackFrame(StackTraceElement stackFrame) {
        StackTraceElement[] currentStack = this.getStackTrace();
        StackTraceElement[] newStack = new StackTraceElement[currentStack.length + 1];

        for(int i = 0; i < currentStack.length; ++i) {
            newStack[i] = currentStack[i];
        }

        newStack[currentStack.length] = stackFrame;
        this.setStackTrace(newStack);
    }

    public void appendStackFrame(String className, String methodName, String fileName, int lineNumber) {
        StackTraceElement stackFrame = new StackTraceElement(className, methodName, fileName, lineNumber);
        StackTraceElement[] currentStack = this.getStackTrace();
        StackTraceElement[] newStack = new StackTraceElement[currentStack.length + 1];

        for(int i = 0; i < currentStack.length; ++i) {
            newStack[i] = currentStack[i];
        }

        newStack[currentStack.length] = stackFrame;
        this.setStackTrace(newStack);
    }

    public void setSourceExceptionType(String sourceExceptionType) {
        this.sourceExceptionType = sourceExceptionType;
    }

    public String toString() {
        return TextUtils.isEmpty(this.sourceExceptionType)?this.getClass().getName():this.sourceExceptionType;
    }
}
