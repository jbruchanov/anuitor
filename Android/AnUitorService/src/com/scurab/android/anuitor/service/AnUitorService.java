package com.scurab.android.anuitor.service;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.reflect.WindowManagerGlobal;
import com.scurab.android.anuitor.tools.NetTools;
import com.scurab.android.anuitor.tools.ZipTools;

import java.io.*;

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
    public static final String ROOT_FOLDER = "ROOT_FOLDER";
    public static final String DEFAULT_ROOT_FOLDER = "anuitor";

    private AnUiHttpServer mServer;

    public static final int NOTIF_ID = 0x375012AF;
    public static final int ICON_RES_ID = android.R.drawable.ic_dialog_alert;

    @Override
    public IBinder onBind(Intent intent) {
        return new AnUitorServiceBinder(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            IdsHelper.loadValues(Class.forName(getPackageName() + ".R"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (STOP.equals(intent.getAction())) {
            stop();
            return 0;
        } else {
            if (START.equals(intent.getAction())) {
                String folder = intent.getStringExtra(ROOT_FOLDER);
                start(intent.getIntExtra(PORT, DEFAULT_PORT), folder != null ? folder : DEFAULT_ROOT_FOLDER);
            }
            return super.onStartCommand(intent, flags, startId);
        }
    }

    /**
     * Start service with default values {@link #DEFAULT_PORT}, {@link #DEFAULT_ROOT_FOLDER}
     * @see {@link #start(int, String)}
     */
    public void start()  {
        start(DEFAULT_PORT, DEFAULT_ROOT_FOLDER);
    }

    /**
     * Start service
     * If there was any exception notification is shown with id {@link #NOTIF_ID}
     *
     * @param port       port for web browser
     * @param rootFolder rootFolder for webBrowser
     * @return true if web server has been sucessfuly started, false otherwise
     * @throws IOException
     */
    public boolean start(int port, String rootFolder) {
        String s = getBaseContext().getCacheDir().toString() + "/" + rootFolder;
        File f = new File(s);
        /*
            If web foloder doesn't exist it means that extraction didn't happen or some problem,
            but at least we will enable plugins to work, because http server won't execute them if root folder doesn't exist.
         */
        if (!f.exists()) {
            f.mkdirs();
        }

        mServer = new AnUiHttpServer(getApplicationContext(), port, new File(s), false, new WindowManagerGlobal());
        try {
            mServer.start();
            startForeground();
            return true;
        } catch (Exception e) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.notify(NOTIF_ID, createSimpleNotification(e.getMessage()));
        }
        return false;
    }

    /**
     * Stop service
     */
    public void stop() {
        if (mServer != null && mServer.isAlive()) {
            mServer.stop();
            mServer = null;
            stopForeground(true);
        }
    }

    /**
     * Start service in foreground
     */
    protected void startForeground() {
        startForeground(NOTIF_ID, createSimpleNotification());
    }

    /**
     * @return true if server is running
     */
    public boolean isRunning() {
        return mServer != null && mServer.isAlive();
    }

    /**
     * Create notification
     *
     * @return
     * @see {@link #createSimpleNotification(String)}
     */
    private Notification createSimpleNotification() {
        return createSimpleNotification(null);
    }

    /**
     * Create notification for service, adds stop button
     *
     * @param addMsg option msg added after IPs and port
     * @return
     */
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

    /**
     * Create intent called when you clicked on notification
     * Opens browser for localhost
     *
     * @return
     */
    private PendingIntent createContentIntent() {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(String.format("http://127.0.0.1:%s/", mServer.getListeningPort())));
        return PendingIntent.getActivity(this, (int) System.currentTimeMillis(), i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Create intent to stop service
     *
     * @return
     */
    private PendingIntent createStopIntent() {
        Intent i = new Intent(this, AnUitorService.class);
        i.setAction(STOP);
        return PendingIntent.getService(this, (int) System.currentTimeMillis(), i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    /**
     * Called when activity bounds service
     * @param activity
     */
    public void onBound(Activity activity) {

    }

    /**
     * Start service
     *
     * @param context
     * @param rawWebZipFileRes
     * @see {@link #startService(android.content.Context, int, boolean, Runnable)}
     */
    public static void startService(Context context, int rawWebZipFileRes) {
        startService(context, rawWebZipFileRes, false, null);
    }

    /**
     * Async Extract web and start service.
     * Throws RuntimeException in async thread if there is an exception from unzip process.
     *
     * @param context
     * @param rawWebZipFileRes   resource id for zip file of web
     * @param overwriteWebFolder true to delete old web folder and unzip again
     * @param onFinishCallback   called when {@link Context#startService(android.content.Intent)} has been called, can be null, is called in non main thread!
     * @throws IllegalStateException if application object doesn't implement {@link com.scurab.android.anuitor.reflect.WindowManager}
     */
    public static void startService(final Context context, final int rawWebZipFileRes, final boolean overwriteWebFolder, final Runnable onFinishCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String folder = String.format("%s/%s", context.getCacheDir().toString(), DEFAULT_ROOT_FOLDER);
                File f = new File(folder);
                if (overwriteWebFolder || !f.exists()) {
                    f.delete();
                    f.mkdir();
                    try {
                        String zipFile = folder + "/web.zip";
                        if(rawWebZipFileRes != 0) {
                            ZipTools.copyFileIntoInternalStorageIfNecessary(context, rawWebZipFileRes, zipFile);
                            ZipTools.extractFolder(zipFile, folder);
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }

                Intent i = new Intent(context, AnUitorService.class);
                i.setAction(AnUitorService.START);
                i.putExtra(AnUitorService.ROOT_FOLDER, DEFAULT_ROOT_FOLDER);
                context.startService(i);

                if (onFinishCallback != null) {
                    onFinishCallback.run();
                }
            }
        }).start();
    }
}
