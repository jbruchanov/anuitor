package com.scurab.android.anuitor.nanoplugin;

import java.io.File;
import java.util.HashSet;
import java.util.Map;

import fi.iki.elonen.NanoHTTPD;

/**
 * Created by jbruchanov on 20/05/2014.
 * Simple wrapper for multiple plugins with same mime type.
 */
public class AggregateMimePlugin extends BasePlugin {

    private BasePlugin[] mPlugins;
    private String[] mFiles;

    public AggregateMimePlugin(BasePlugin... plugins) {
        mPlugins = plugins;
        if (mPlugins == null || mPlugins.length == 0) {
            throw new IllegalArgumentException("Plugins are null or empty!");
        }
        checkMimeTypes();
        mFiles = mergeFiles(mPlugins);
    }

    private String[] mergeFiles(BasePlugin[] plugins) {
        HashSet<String> files = new HashSet<String>();
        for (BasePlugin b : plugins) {
            for (String file : b.files()) {
                if(files.contains(file)){
                    throw new IllegalStateException(String.format("File:'%s' is already defined from previous plugin", file));
                }
                files.add(file);
            }
        }
        return files.toArray(new String[files.size()]);
    }

    private void checkMimeTypes() {
        String mime = mPlugins[0].mimeType();
        for (BasePlugin b : mPlugins) {
            if (!mime.equals(b.mimeType())) {
                throw new IllegalStateException(String.format("Plugins don't have same mime types %s vs %s", mime, b.mimeType()));
            }
        }
    }

    @Override
    public String[] files() {
        return mFiles;
    }

    @Override
    public String mimeType() {
        return mPlugins[0].mimeType();
    }

    @Override
    public boolean canServeUri(String uri, File rootDir) {
        return getServeCandidate(uri, rootDir) != null;
    }

    @Override
    public NanoHTTPD.Response serveFile(String uri, Map<String, String> headers, NanoHTTPD.IHTTPSession session, File file, String mimeType) {
        //null should never happened, because it's not called if canServerUri returns false
        return getServeCandidate(uri, file /*TODO: check if it's fine */).serveFile(uri, headers, session, file, mimeType);
    }

    BasePlugin getServeCandidate(String uri, File rootDir) {
        for (BasePlugin plugin : mPlugins) {
            if (plugin.canServeUri(uri, rootDir)) {
                return plugin;
            }
        }
        return null;
    }
}
