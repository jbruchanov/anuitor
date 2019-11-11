package com.scurab.android.anuitor.service;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.scurab.android.anuitor.nanoplugin.ActiveScreensPlugin;
import com.scurab.android.anuitor.nanoplugin.AggregateMimePlugin;
import com.scurab.android.anuitor.nanoplugin.BasePlugin;
import com.scurab.android.anuitor.nanoplugin.ConfigClientPlugin;
import com.scurab.android.anuitor.nanoplugin.FileStoragePlugin;
import com.scurab.android.anuitor.nanoplugin.GroovyPlugin;
import com.scurab.android.anuitor.nanoplugin.LogCatPlugin;
import com.scurab.android.anuitor.nanoplugin.ResourcesPlugin;
import com.scurab.android.anuitor.nanoplugin.ScreenStructurePlugin;
import com.scurab.android.anuitor.nanoplugin.ScreenViewPlugin;
import com.scurab.android.anuitor.nanoplugin.ScreenComponentsPlugin;
import com.scurab.android.anuitor.nanoplugin.ViewHierarchyPlugin;
import com.scurab.android.anuitor.nanoplugin.ViewPropertyPlugin;
import com.scurab.android.anuitor.nanoplugin.ViewshotPlugin;
import com.scurab.android.anuitor.reflect.WindowManager;

import java.io.File;

import fi.iki.elonen.SimpleWebServer;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 11:24
 */
public class AnUitorHttpServer extends SimpleWebServer {

    public AnUitorHttpServer(Context context, int port, File wwwroot, boolean quiet, WindowManager windowManager) {
        super(null, port, wwwroot, quiet);
        initPlugins(context, windowManager);
    }

    protected void initPlugins(Context context, WindowManager windowManager) {
        boolean hasGroovySupport = false;
        try {
            //TODO: check more cacheDir sometimes fails on permission_denied exception
            File tmp = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                    ? context.getCodeCacheDir()
                    : context.getCacheDir();
            registerPluginForMimeType(new GroovyPlugin(tmp));
            hasGroovySupport = true;
        } catch (Throwable e) {
            Log.v("AnUitorHttpServer", "Unable to register GroovyPlugin");
        }

        registerPluginForMimeType(new AggregateMimePlugin(
                new ScreenViewPlugin(windowManager),
                new ViewshotPlugin(windowManager)));
        registerPluginForMimeType(new AggregateMimePlugin(
                new ConfigClientPlugin(AnUitorClientConfig.init(context, hasGroovySupport)),
                new ActiveScreensPlugin(windowManager),
                new ViewHierarchyPlugin(windowManager),
                new FileStoragePlugin(context),
                new ResourcesPlugin(context.getApplicationContext().getResources(), windowManager),
                new ScreenStructurePlugin(windowManager),
                new ViewPropertyPlugin(windowManager),
                new ScreenComponentsPlugin(windowManager)));
        registerPluginForMimeType(new LogCatPlugin());
    }

    public static void registerPluginForMimeType(BasePlugin plugin) {
        SimpleWebServer.registerPluginForMimeType(plugin.files(), plugin.mimeType(), plugin, null);
    }
}