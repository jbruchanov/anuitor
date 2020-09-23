package com.scurab.android.uitor.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager.NameNotFoundException
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.Builder
import com.scurab.android.uitor.R
import com.scurab.android.uitor.hierarchy.IdsHelper
import com.scurab.android.uitor.tools.FileSystemTools
import com.scurab.android.uitor.tools.NetTools
import com.scurab.android.uitor.tools.ZipTools
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val STOP = "STOP"
private const val START = "START"
private const val PORT = "PORT"
private const val TAG = "UitorService"
private const val TITLE = "Uitor"
private const val NOTIF_ID = 0x375012AF
private const val ROOT_FOLDER = "ROOT_FOLDER"
private const val DEFAULT_ROOT_FOLDER = "uitor"
private const val DEFAULT_PORT = 8080

class UitorService : Service() {

    private var server = KtorServer(this)

    private val appTitle: String? by lazy {
        try {
            val packageManager = packageManager
            val appInfo = packageManager.getPackageInfo(packageName, 0).applicationInfo
            val appLabel = packageManager.getApplicationLabel(appInfo)
            appLabel.toString()
        } catch (e: NameNotFoundException) { // just ignore it and use default value with no app name...
            e.printStackTrace()
            null
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        try {
            val rClassName = "$packageName.R"
            val rclass = Class.forName(rClassName)
            IdsHelper.loadValues(rclass)
        } catch (e: Throwable) {
            Log.e(TAG, "Unable to load Resources, probably R class is not in your packageName => call IdsHelper.loadValues(com.application.R.class);")
            Log.e(TAG, e.message ?: "null message")
            e.printStackTrace()
        }
        super.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        server.stop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        try {
            intent?.action?.let { action ->
                when (action) {
                    START -> {
                        if (!server.isRunning) {
                            val port = intent.extras?.getInt(PORT, DEFAULT_PORT) ?: DEFAULT_PORT
                            val rootFolder = intent.extras?.getString(ROOT_FOLDER)
                                ?: throw NullPointerException("Undefined '$ROOT_FOLDER' in extras")
                            val rootFolderFullPath = "${baseContext.cacheDir}/$rootFolder"
                            server.start(rootFolderFullPath, port)
                            startForeground(NOTIF_ID, createSimpleNotification(null, true))
                        }
                    }
                    STOP -> {
                        stopForeground(true)
                        server.stop()
                    }
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace(System.err)
            startForeground(NOTIF_ID, createSimpleNotification(e.message, false))
        }
        return START_NOT_STICKY
    }

    /**
     * Create intent called when you clicked on notification
     * Opens browser for localhost
     *
     * @return
     */
    private fun createContentIntent(): PendingIntent? {
        val i = Intent(Intent.ACTION_VIEW)
        val port = server.port ?: 80
        i.data = Uri.parse("http://127.0.0.1:$port/")
        return PendingIntent.getActivity(this, System.currentTimeMillis().toInt(), i, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /**
     * Create intent to stop service
     *
     * @return
     */
    private fun createStopIntent(): PendingIntent? {
        val i = Intent(this, UitorService::class.java)
        i.action = STOP
        return PendingIntent.getService(this, System.currentTimeMillis().toInt(), i, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createSimpleNotification(addMsg: String?, addStopAction: Boolean): Notification? {
        val defaults = Notification.DEFAULT_LIGHTS
        var msg = "IPs:${NetTools.getLocalIpAddress()}\nPort:${server.port}"
        if (addMsg != null) {
            msg = msg + "\n" + addMsg
        }
        var title = TITLE
        appTitle?.let {
            title = String.format("%s (%s)", title, it)
        }
        val stopIntent = if (addStopAction) createStopIntent() else null
        val n = createNotification(this, title, msg, defaults, createContentIntent(), stopIntent)
        if (n == null) {
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        }
        return n
    }

    companion object {
        private fun createNotification(
            context: Context,
            title: String?,
            msg: String?,
            defaults: Int,
            contentIntent: PendingIntent?,
            stopIntent: PendingIntent?
        ): Notification? {
            val notib = Builder(context, TAG)
                .setContentTitle(title ?: TAG)
                .setAutoCancel(true)
                .setDefaults(defaults)
                .setContentText(msg ?: "Null msg")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentIntent(contentIntent)
                .setStyle(BigTextStyle().bigText(msg))
            if (stopIntent != null) {
                notib.addAction(0, STOP, stopIntent)
            }
            return notib.build()
        }

        /**
         * Create notification channel if necessary
         * @param context
         */
        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val chan = NotificationChannel(TAG, TITLE, NotificationManager.IMPORTANCE_NONE)
                chan.importance = NotificationManager.IMPORTANCE_DEFAULT
                val service = (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                service.createNotificationChannel(chan)
            }
        }

        @JvmStatic
        @JvmOverloads
        fun startService(context: Context, port: Int, overwriteWebFolder: Boolean = false) {
            createNotificationChannel(context)
            GlobalScope.launch {
                val exception = withContext(Dispatchers.IO) {
                    val folder = "${context.cacheDir}/$DEFAULT_ROOT_FOLDER"
                    val f = File(folder)
                    var exception: Throwable? = null
                    if (overwriteWebFolder || !f.exists()) {
                        FileSystemTools.deleteFolder(f)
                        f.mkdirs()
                        try {
                            val zipFile = "$folder/web.zip"
                            ZipTools.copyFileIntoInternalStorageIfNecessary(context, R.raw.uitor_webapp, zipFile)
                            ZipTools.extractFolder(zipFile, folder)
                        } catch (e: Throwable) {
                            exception = e
                            Log.e(TAG, e.message ?: "Null")
                            e.printStackTrace()
                            f.delete()
                        }
                    }
                    exception
                }

                if (exception == null) {
                    val intent = Intent(context, UitorService::class.java).apply {
                        action = START
                        putExtra(ROOT_FOLDER, DEFAULT_ROOT_FOLDER)
                        putExtra(PORT, port)
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(intent)
                    } else {
                        context.startService(intent)
                    }
                } else {
                    val notification = createNotification(
                        context,
                        "$TAG Error",
                        exception.message!!,
                        Notification.DEFAULT_ALL,
                        null, null
                    )
                    val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    nm.notify(NOTIF_ID, notification)
                }
            }
        }
    }
}
