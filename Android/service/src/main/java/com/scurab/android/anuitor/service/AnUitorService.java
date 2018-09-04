package com.scurab.android.anuitor.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.scurab.android.anuitor.R;
import com.scurab.android.anuitor.hierarchy.IdsHelper;
import com.scurab.android.anuitor.reflect.WindowManagerProvider;
import com.scurab.android.anuitor.tools.FileSystemTools;
import com.scurab.android.anuitor.tools.NetTools;
import com.scurab.android.anuitor.tools.ZipTools;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 10:27
 */
public class AnUitorService extends Service {
    private static final String TAG = "AnUitorService";
    private static final String TITLE = "AnUitor";
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
            final String rClassName = getPackageName() + ".R";
            final Class<?> rclass = Class.forName(rClassName);
            IdsHelper.loadValues(rclass);
        } catch (Throwable e) {
            Log.e(TAG, "Unable to load Resources, probably R class is not in your packageName => call IdsHelper.loadValues(com.application.R.class);");
            Log.e(TAG, e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) { //called with null intent when the app is killed...
            if (STOP.equals(intent.getAction())) {
                stop();
            } else {
                if (START.equals(intent.getAction())) {
                    if (mServer != null && mServer.isAlive()) {
                        return START_NOT_STICKY;//we are already running, do nothing
                    }
                    String folder = intent.getStringExtra(ROOT_FOLDER);
                    start(intent.getIntExtra(PORT, DEFAULT_PORT), folder != null ? folder : DEFAULT_ROOT_FOLDER);
                }
            }
        }
        return START_NOT_STICKY;
    }

    /**
     * Start service with default values {@link #DEFAULT_PORT}, {@link #DEFAULT_ROOT_FOLDER}
     * {@link #start(int, String)}
     */
    public boolean start()  {
        return start(DEFAULT_PORT, DEFAULT_ROOT_FOLDER);
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
        String absPath = getBaseContext().getCacheDir().toString() + "/" + rootFolder;
        File f = new File(absPath);
        /*
            If web folder doesn't exist it means that extraction didn't happen or some problem,
            but at least we will enable plugins to work, because http server won't execute them if root folder doesn't exist.
         */
        if (!f.exists()) {
            f.mkdirs();
        }

        mServer = onCreateServer(port, absPath);
        try {
            mServer.start();
            startForeground();
            return true;
        } catch (Throwable e) {
            NotificationManager nm = getNotificationManager();
            nm.notify(NOTIF_ID, createSimpleNotification(e.getMessage(), false));
        }
        return false;
    }

    NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    protected AnUiHttpServer onCreateServer(int port, String root) {
        return new AnUiHttpServer(getApplicationContext(), port, new File(root), true, WindowManagerProvider.getManager());
    }

    /**
     * Stop service
     */
    public void stop() {
        if (mServer != null) {
            mServer.stop();
            mServer = null;
        }
        stopForeground(true);
    }

    /**
     * Start service in foreground
     */
    protected void startForeground() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
        }
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
     * {@link #createSimpleNotification(String)}
     *
     * @return
     */
    private Notification createSimpleNotification() {
        return createSimpleNotification(null);
    }

    /**
     * {@see #createSimpleNotification}, stop action is true by default
     * @param addMsg
     * @return
     */
    private Notification createSimpleNotification(String addMsg) {
        return createSimpleNotification(addMsg, true);
    }

    /**
     * Create notification for service, adds stop button
     *
     * @param addMsg option msg added after IPs and port
     * @return
     */
    private Notification createSimpleNotification(String addMsg, boolean addStopAction) {
        int defaults = Notification.DEFAULT_LIGHTS;
        String msg = String.format("IPs:%s\nPort:%s", NetTools.getLocalIpAddress(), mServer.getListeningPort());
        if (addMsg != null) {
            msg = msg + "\n" + addMsg;
        }
        String title = TITLE;
        String appTitle = getAppTitle();
        if (appTitle != null) {
            title = String.format("%s (%s)", title, appTitle);
        }

        try {
            NotificationCompat.Builder notib = new NotificationCompat.Builder(this, TAG)
                    .setContentTitle(title)
                    .setAutoCancel(true)
                    .setDefaults(defaults)
                    .setContentText(msg)
                    .setSmallIcon(ICON_RES_ID)
                    .setContentIntent(createContentIntent())
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(msg));

            if (addStopAction) {
                notib.addAction(0, STOP, createStopIntent());
            }
            return notib.build();
        } catch (Throwable e) {
            //we don't have support library around
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                Notification.Builder builder = new Notification.Builder(this)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setDefaults(defaults)
                        .setContentText(msg)
                        .setSmallIcon(ICON_RES_ID)
                        .setContentIntent(createContentIntent());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    builder.setStyle(new Notification.BigTextStyle().bigText(msg));
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    builder.setChannelId(TAG);
                }
                return builder.getNotification();
            } else {
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
            }
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Returns app title, can return null if any troubles
     * @return
     */
    String getAppTitle() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo appInfo = packageManager.getPackageInfo(getPackageName(), 0).applicationInfo;
            CharSequence appLabel = packageManager.getApplicationLabel(appInfo);
            return appLabel.toString();
        } catch (PackageManager.NameNotFoundException e) {
            //just ignore it and use default value with no app name...
            e.printStackTrace();
            return null;
        }
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
    PendingIntent createStopIntent() {
        Intent i = new Intent(this, AnUitorService.class);
        i.setAction(STOP);
        return PendingIntent.getService(this, (int) System.currentTimeMillis(), i, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void createNotificationChannel() {
        NotificationChannel chan = new NotificationChannel(TAG, TITLE, NotificationManager.IMPORTANCE_NONE);
        NotificationManager service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert service != null;
        service.createNotificationChannel(chan);
    }

    /**
     * Download web app if necessary and Start service
     * @param context
     */
    public static void startService(Context context) {
        startService(context, DEFAULT_PORT, 0, false, null);
    }

    /**
     * Download web app if necessary and Start service on particular port
     *
     * @param context
     * @param port
     */
    public static void startServiceUsingPort(Context context, int port) {
        startService(context, port, 0, false, null);
    }

    /**
     * Start service
     *
     * @param context
     * @param rawWebZipFileRes {@link #startService(android.content.Context, int, int, boolean, Runnable)}
     */
    public static void startService(Context context, int rawWebZipFileRes) {
        startService(context, DEFAULT_PORT, rawWebZipFileRes, false, null);
    }

    /**
     * Async Download & Extract web and start service.
     * @param context
     * @param overwriteWebFolder
     * @param onFinishCallback
     */
    public static void startService(Context context, boolean overwriteWebFolder, Runnable onFinishCallback) {
        startService(context, DEFAULT_PORT, 0, overwriteWebFolder, onFinishCallback);
    }

    /**
     * Async Extract web and start service.
     * Throws RuntimeException in async thread if there is an exception from unzip process.
     *
     * @param context
     * @param rawWebZipFileRes   resource id for zip file of web, if -1 'http://anuitor.scurab.com/download/anuitor.zip' is used as link to download app, 0 is used default included zip, otherwise your own asset file
     * @param overwriteWebFolder true to delete old web folder and unzip again
     * @param onFinishCallback   called when {@link Context#startService(android.content.Intent)} has been called, can be null, is called in non main thread!
     * @throws IllegalStateException if application object doesn't implement {@link com.scurab.android.anuitor.reflect.WindowManager}
     */
    public static void startService(final Context context, final int port, final int rawWebZipFileRes, final boolean overwriteWebFolder, final Runnable onFinishCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String folder = String.format("%s/%s", context.getCacheDir().toString(), DEFAULT_ROOT_FOLDER);
                File f = new File(folder);
                if (overwriteWebFolder || !f.exists()) {
                    FileSystemTools.deleteFolder(f);
                    f.mkdir();
                    try {
                        String zipFile = folder + "/web.zip";
                        if (rawWebZipFileRes == -1) {
                            new File(zipFile).delete();
                            URL website = new URL("http://anuitor.scurab.com/download/anuitor.zip");
                            FileSystemTools.copyFile(website.openStream(), zipFile);
                        } else {
                            int resId = rawWebZipFileRes;
                            if (resId == 0) {
                                resId = R.raw.anuitor;
                            }
                            ZipTools.copyFileIntoInternalStorageIfNecessary(context, resId, zipFile);
                        }
                        ZipTools.extractFolder(zipFile, folder);
                    } catch (Throwable e) {
                        Notification notification = new NotificationCompat.Builder(context)
                        .setSmallIcon(ICON_RES_ID)
                        .setContentTitle(TAG + " Error")
                        .setContentText(e.getMessage())
                        .build();
                        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        nm.notify(1, notification);

                        Log.e(TAG, e.getMessage());
                        e.printStackTrace();
                        f.delete();
                    }
                }

                Intent i = new Intent(context, AnUitorService.class);
                i.setAction(AnUitorService.START);
                i.putExtra(AnUitorService.ROOT_FOLDER, DEFAULT_ROOT_FOLDER);
                i.putExtra(AnUitorService.PORT, port);
                context.startService(i);

                if (onFinishCallback != null) {
                    onFinishCallback.run();
                }
            }
        }).start();
    }
}
