package com.scurab.android.anuitor.tools;

import android.content.Context;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static com.scurab.android.anuitor.tools.FileSystemTools.BUFFER;
/**
 * Created by jbruchanov on 21/05/2014.
 *
 * Few help methods for operation with zip files
 */
public class ZipTools {

    /**
     * Extract zip file into folder
     *
     * @param zipFile
     * @param extractFolder
     */
    public static void extractFolder(String zipFile, String extractFolder) throws IOException {
        File file = new File(zipFile);

        ZipFile zip = new ZipFile(file);
        String newPath = extractFolder;

        new File(newPath).mkdir();
        Enumeration zipFileEntries = zip.entries();

        // Process each entry
        while (zipFileEntries.hasMoreElements()) {
            // grab a zip file entry
            ZipEntry entry = (ZipEntry) zipFileEntries.nextElement();
            String currentEntry = entry.getName();

            File destFile = new File(newPath, currentEntry);
            File destinationParent = destFile.getParentFile();

            // create the parent directory structure if needed
            destinationParent.mkdirs();

            if (!entry.isDirectory()) {
                BufferedInputStream is = new BufferedInputStream(zip.getInputStream(entry));
                int currentByte;
                // establish buffer for writing file
                byte[] data = new byte[BUFFER];

                // write the current file to disk
                FileOutputStream fos = new FileOutputStream(destFile);
                BufferedOutputStream dest = new BufferedOutputStream(fos,BUFFER);

                // read and write until last byte is encountered
                while ((currentByte = is.read(data, 0, BUFFER)) != -1) {
                    dest.write(data, 0, currentByte);
                }
                dest.flush();
                dest.close();
                is.close();
            }
        }
    }

    /**
     * Copy raw resource file into target file only if target file doesn't exists
     * @param context
     * @param rawRes
     * @param targetFile
     * @return true if file was copied, false if exists
     * @throws IOException
     */
    public static boolean copyFileIntoInternalStorageIfNecessary(Context context, int rawRes, String targetFile) throws IOException {
        File target = new File(targetFile);
        if (target.exists()) {
            return false;
        }

        InputStream in = context.getResources().openRawResource(rawRes);
        FileSystemTools.copyFile(in, targetFile);
        return true;
    }
}
