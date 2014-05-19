package com.scurab.android.anuitor.service;

import android.content.Context;
import com.scurab.android.anuitor.nanoplugin.*;
import fi.iki.elonen.SimpleWebServer;

import java.io.File;

/**
 * User: jbruchanov
 * Date: 12/05/2014
 * Time: 11:24
 */
public class AnUiHttpServer extends SimpleWebServer {

    public AnUiHttpServer(Context context, int port, File wwwroot, boolean quiet, KnowsActivity... activityKeeper) {
        super(null, port, wwwroot, quiet);
        initPlugins(context, activityKeeper);
    }

    private void initPlugins(Context context, KnowsActivity... activityKeeper) {
        AnUiHttpServer.registerPluginForMimeType(new ScreenViewPlugin(activityKeeper));
        AnUiHttpServer.registerPluginForMimeType(new ViewHierarchyPlugin(activityKeeper));
        AnUiHttpServer.registerPluginForMimeType(new FileStoragePlugin(context));
    }

    public static void registerPluginForMimeType(BasePlugin plugin) {
        SimpleWebServer.registerPluginForMimeType(plugin.files(), plugin.mimeType(), plugin, null);
    }
}
