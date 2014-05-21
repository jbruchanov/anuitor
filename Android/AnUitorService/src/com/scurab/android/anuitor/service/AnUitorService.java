package com.scurab.android.anuitor.service;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.nanoplugin.KnowsActivity;

import java.io.*;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 10:27
 */
public class AnUitorService extends Service {

    public static final String STOP = "STOP";
    public static final String START = "START";
    public static final String PORT = "PORT";
    public static final int DEFAULT_PORT = 8080;
    private AnUiHttpServer mServer;

    public static final int NOTIF_ID = 0x375012AF;
    public static final int ICON_RES_ID = android.R.drawable.ic_dialog_alert;

    private ActivityCallbackHandler mCallbackHandler = new ActivityCallbackHandler();

    @Override
    public IBinder onBind(Intent intent) {
        return new AnUitorServiceBinder(this);
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onCreate() {
        super.onCreate();
        //TODO: fix it for older versions
        getApplication().registerActivityLifecycleCallbacks(mCallbackHandler);
        try {
            IdsHelper.loadValues(Class.forName(getPackageName()+ ".R"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onDestroy() {
        getApplication().unregisterActivityLifecycleCallbacks(mCallbackHandler);
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (STOP.equals(intent.getAction())) {
            stop();
            return 0;
        } else {
            if (START.equals(intent.getAction())) {
                try {
                    start(intent.getIntExtra(PORT, DEFAULT_PORT));
                } catch (IOException e) {
                    throw new IllegalStateException(e);
                }
            }
            return super.onStartCommand(intent, flags, startId);
        }
    }

    public void start() throws IOException {
        start(DEFAULT_PORT);
    }

    public void start(int port) throws IOException {
        String s = getBaseContext().getCacheDir().toString();

        mServer = new AnUiHttpServer(this, port, new File(s), false, mCallbackHandler, (KnowsActivity)getApplication());
        try {
            mServer.start();
            startForeground();
        } catch (Exception e) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(1, createSimpleNotification(e.getMessage()));
        }
    }



    public void stop() {
        if (mServer != null && mServer.isAlive()) {
            mServer.stop();
            mServer = null;
            stopForeground(true);
        }
    }

    protected void startForeground() {
        startForeground(NOTIF_ID, createSimpleNotification());
    }

    public boolean isRunning() {
        return mServer != null && mServer.isAlive();
    }

    private Notification createSimpleNotification() {
        return createSimpleNotification(null);
    }
    private Notification createSimpleNotification(String addMsg) {
        int defaults = Notification.DEFAULT_LIGHTS;
        String msg = String.format("IPs:%s\nPort:%s", NetTools.getLocalIpAddress(), mServer.getListeningPort());
        if (addMsg != null) {
            msg = msg + "\n" + addMsg;
        }
        Notification noti = new NotificationCompat.Builder(this)
                .setContentTitle("AnUitor")
                .setAutoCancel(true)
                .setDefaults(defaults)
                .setContentText(msg)
                .setSmallIcon(ICON_RES_ID)
                .addAction(0, STOP, createStopIntent())
                .setContentIntent(createContentIntent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                .build();

        return noti;
    }

    private PendingIntent createContentIntent() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(String.format("http://127.0.0.1:%s/storage.json?path=lib", mServer.getListeningPort())));
        return PendingIntent.getActivity(this, (int) System.currentTimeMillis(), i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private PendingIntent createStopIntent() {
        Intent i = new Intent(this, AnUitorService.class);
        i.setAction(STOP);
        return PendingIntent.getService(this, (int)System.currentTimeMillis(), i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    public void onBound(Activity activity) {
        mCallbackHandler.setCurrentActivity(activity);
    }
}
