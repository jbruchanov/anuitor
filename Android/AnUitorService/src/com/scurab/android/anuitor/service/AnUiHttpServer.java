package com.scurab.android.anuitor.service;

import android.content.Context;
import com.scurab.android.anuitor.nanoplugin.*;
import com.scurab.android.anuitor.reflect.WindowManager;
import com.scurab.android.anuitor.reflect.WindowManagerGlobal;

import fi.iki.elonen.SimpleWebServer;

import java.io.File;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 11:24
 */
public class AnUiHttpServer extends SimpleWebServer {

    public AnUiHttpServer(Context context, int port, File wwwroot, boolean quiet, WindowManager windowManager) {
        super(null, port, wwwroot, quiet);
        initPlugins(context, windowManager);
    }

    protected void initPlugins(Context context, WindowManager windowManager) {
        registerPluginForMimeType(new AggregateMimePlugin(
                new ScreenViewPlugin(windowManager),
                new ViewshotPlugin(windowManager)));
        registerPluginForMimeType(new AggregateMimePlugin(
                new ViewHierarchyPlugin(windowManager),
                new FileStoragePlugin(context),
                new ResourcesPlugin(context.getResources()),
                new ScreenStructurePlugin(new WindowManagerGlobal())));
    }

    public static void registerPluginForMimeType(BasePlugin plugin) {
        SimpleWebServer.registerPluginForMimeType(plugin.files(), plugin.mimeType(), plugin, null);
    }
}
