package com.scurab.android.anuitor.tools;

import android.os.Handler;
import android.os.Looper;

import com.scurab.android.anuitor.model.OutRef;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class Executor {

    private static Handler sHandler;
    private static final int DEFAULT_TIMEOUT = 2000;

    public interface Action<T> {
        T run();
    }

    /**
     * Run op in main thread with 2s timeout
     *
     * @param op
     */
    public static <T> T runInMainThreadBlocking(Action<T> op) {
        return runInMainThreadBlocking(getHandler(), op, DEFAULT_TIMEOUT);
    }

    /**
     * @param timeout
     * @param op
     */
    public static <T> T runInMainThreadBlocking(int timeout, Action<T> op) {
        return runInMainThreadBlocking(getHandler(), op, timeout);
    }

    /**
     * Run op in main thread with 2s timeout only if it's crashing...
     * First try is started in current thread
     *
     * @param op
     */
    public static <T> T runInMainThreadBlockingOnlyIfCrashing(Action<T> op) {
        try {
            return op.run();
        } catch (Throwable t) {
            return runInMainThreadBlocking(getHandler(), op, DEFAULT_TIMEOUT);
        }
    }

    private static Handler getHandler() {
        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }
        return sHandler;
    }

    /**
     * Run code in main thread and block current running thread
     *
     * @param handler
     * @param op
     * @param timeout
     */
    public static <T> T runInMainThreadBlocking(Handler handler, final Action<T> op, int timeout) {
        final Object lock = new Object();
        final OutRef<T> result = new OutRef<>();
        if (Looper.getMainLooper() == Looper.myLooper()) {
            return op.run();
        }
        synchronized (lock) {
            handler.post(() -> {
                result.setValue(op.run());
                synchronized (lock) {
                    lock.notifyAll();
                }
            });

            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result.getValue();
    }
}
