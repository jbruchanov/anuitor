package com.scurab.android.anuitor.tools;

import android.os.Handler;
import android.os.Looper;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class Executor {

    private static Handler sHandler;

    /**
     * Run op in main thread with 2s timeout
     *
     * @param op
     */
    public static void runInMainThreadBlocking(final Runnable op) {
        runInMainThreadBlocking(getHandler(), op, 2000);
    }

    /**
     * Run op in main thread with 2s timeout only if it's crashing...
     * First try is started in current thread
     *
     * @param op
     */
    public static void runInMainThreadBlockingOnlyIfCrashing(final Runnable op) {
        try {
            op.run();
        } catch (Throwable t) {
            runInMainThreadBlocking(getHandler(), op, 2000);
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
     * @param handler
     * @param op
     * @param timeout
     */
    public static void runInMainThreadBlocking(Handler handler, final Runnable op, int timeout) {
        final Object lock = new Object();
        synchronized (lock) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    op.run();
                    synchronized (lock) {
                        lock.notifyAll();
                    }
                }
            });

            try {
                lock.wait(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
