package com.kevinolarte.resibenissa;

public class LogContext {
    private static final ThreadLocal<Long> currentLogId = new ThreadLocal<>();

    public static void setCurrentLogId(Long id) {
        currentLogId.set(id);
    }

    public static Long getCurrentLogId() {
        return currentLogId.get();
    }

    public static void clear() {
        currentLogId.remove();
    }
}
