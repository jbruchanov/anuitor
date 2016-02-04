package com.scurab.android.anuitor.tools;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;

/**
 * Created by jbruchanov on 26/06/2014.
 */
public class Executor {

    /**
     * Run op in main thread with 2s timeout
     * @param op
     */
    public static void runInMainThreadBlocking(final Runnable op) {
        runInMainThreadBlocking(new Handler(Looper.getMainLooper()), op, 2000);
    }

    /**
     * Run op in main thread with 2s timeout only if it's crashing...
     * First try is started in current thread
     * @param op
     */
    public static void runInMainThreadBlockingOnlyIfCrashing(final Runnable op) {
        try {
            op.run();
        } catch (Throwable t) {
            runInMainThreadBlocking(new Handler(Looper.getMainLooper()), op, 2000);
        }
    }
    /**
     * Run code in main thread and block current running thread
     * @param handler
     * @param op
     * @param timeout
     */
    public static void runInMainThreadBlocking(Handler handler, final Runnable op, int timeout) {
        final Object lock = new Object();
        final Bitmap[] output = new Bitmap[1];
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
