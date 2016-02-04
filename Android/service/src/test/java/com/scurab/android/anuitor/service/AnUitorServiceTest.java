package com.scurab.android.anuitor.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.scurab.android.anuitor.C;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.Shadows;
import org.robolectric.annotation.Config;
import org.robolectric.shadows.ShadowNotification;
import org.robolectric.shadows.ShadowNotificationManager;
import org.robolectric.shadows.ShadowPendingIntent;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by jbruchanov on 23/06/2014.
 */
@Config(manifest = C.MANIFEST, sdk = 18)
@RunWith(RobolectricTestRunner.class)
public class AnUitorServiceTest {

    public static final int PORT = 8080;

    @Test
    public void testStopCommand() {
        AnUitorService service = mock(AnUitorService.class);
        doCallRealMethod().when(service).onStartCommand(any(Intent.class), anyInt(), anyInt());
        Intent intent = mock(Intent.class);
        doReturn(AnUitorService.STOP).when(intent).getAction();

        service.onStartCommand(intent, 0, 0);

        verify(service).stop();
    }

    @Test
    public void testStartCommandCustomParams() {
        AnUitorService service = mock(AnUitorService.class);
        doCallRealMethod().when(service).onStartCommand(any(Intent.class), anyInt(), anyInt());

        Intent intent = mock(Intent.class);
        doReturn(AnUitorService.START).when(intent).getAction();
        doReturn("/temp").when(intent).getStringExtra(eq(AnUitorService.ROOT_FOLDER));
        int port = 1234;
        doReturn(port).when(intent).getIntExtra(eq(AnUitorService.PORT), anyInt());

        service.onStartCommand(intent, 0, 0);

        verify(service).start(port, "/temp");
    }

    @Test
    public void testStartDefaultParams() {
        AnUitorService service = mock(AnUitorService.class);
        doCallRealMethod().when(service).onStartCommand(any(Intent.class), anyInt(), anyInt());

        Intent intent = spy(new Intent());
        doReturn(AnUitorService.START).when(intent).getAction();
        doCallRealMethod().when(intent).getIntExtra(anyString(), anyInt());

        service.onStartCommand(intent, 0, 0);

        verify(service).start(AnUitorService.DEFAULT_PORT, AnUitorService.DEFAULT_ROOT_FOLDER);
    }

    @Test
    public void testBinding() {
        AnUitorService service = mock(AnUitorService.class);
        doCallRealMethod().when(service).onBind(any(Intent.class));

        IBinder iBinder = service.onBind(mock(Intent.class));
        assertTrue(iBinder instanceof AnUitorServiceBinder);
        AnUitorServiceBinder binder = (AnUitorServiceBinder) iBinder;
        assertEquals(service, binder.getService());
    }

    @Test
    public void testStartWithDefaultValues() {
        AnUitorService service = mock(AnUitorService.class);
        doCallRealMethod().when(service).start();
        doReturn(false).when(service).start(anyInt(), anyString());

        service.start();

        verify(service).start(AnUitorService.DEFAULT_PORT, (AnUitorService.DEFAULT_ROOT_FOLDER));
    }

    @Test
    public void testStartWithValues() throws IOException {
        String root = "anuitor";
        String absPath = RuntimeEnvironment.application.getCacheDir().toString() + "/" + root;
        File rootFolder = new File(absPath);
        rootFolder.delete();

        AnUitorService service = mock(AnUitorService.class);
        AnUiHttpServer server = mock(AnUiHttpServer.class);

        doReturn(server).when(service).onCreateServer(anyInt(), anyString());
        doReturn(RuntimeEnvironment.application).when(service).getBaseContext();
        doCallRealMethod().when(service).start(anyInt(), anyString());

        assertTrue(service.start(PORT, root));//start

        verify(service).onCreateServer(PORT, absPath);
        verify(service).startForeground();
        verify(server).start();
        assertTrue(rootFolder.exists());
        assertTrue(rootFolder.isDirectory());

        //clean
        rootFolder.delete();
    }

    @Test
    public void testStartWithExceptionShowsNotificationWithNoStop() throws IOException {
        String root = "anuitor";
        String absPath = RuntimeEnvironment.application.getCacheDir().toString() + "/" + root;
        File rootFolder = new File(absPath);
        rootFolder.delete();

        AnUitorService service = mock(AnUitorService.class);
        AnUiHttpServer server = mock(AnUiHttpServer.class);

        doReturn(server).when(service).onCreateServer(anyInt(), anyString());
        String errMsg = "Intended";
        doThrow(new Error(errMsg)).when(server).start();
        doReturn(RuntimeEnvironment.application).when(service).getBaseContext();
        doCallRealMethod().when(service).start(anyInt(), anyString());
        doReturn(RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE)).when(service).getNotificationManager();

        assertFalse(service.start(PORT, root));//start

        ShadowNotificationManager snm = Shadows.shadowOf((NotificationManager) RuntimeEnvironment.application.getSystemService(Context.NOTIFICATION_SERVICE));
        List<Notification> allNotifications = snm.getAllNotifications();
        Notification notification = allNotifications.get(0);
        ShadowNotification shadowNotification = Shadows.shadowOf(notification);

        assertEquals(1, allNotifications.size());
        assertNull(notification.actions);
        assertTrue(shadowNotification.getContentText().toString().contains(errMsg));

        //clean
        rootFolder.delete();
    }

    @Test
    public void testCreateStopIntent() throws IOException {
        AnUitorService service = mock(AnUitorService.class);
        doCallRealMethod().when(service).createStopIntent();

        PendingIntent stopPendingIntent = service.createStopIntent();
        ShadowPendingIntent spi = Shadows.shadowOf(stopPendingIntent);
        assertTrue(AnUitorService.STOP.equals(spi.getSavedIntent().getAction()));
    }

    @Test
    public void testStopWebServer() throws IOException {
        String root = "anuitor";
        String absPath = RuntimeEnvironment.application.getCacheDir().toString() + "/" + root;
        File rootFolder = new File(absPath);

        AnUitorService service = mock(AnUitorService.class);
        AnUiHttpServer server = mock(AnUiHttpServer.class);

        doReturn(server).when(service).onCreateServer(anyInt(), anyString());
        doReturn(RuntimeEnvironment.application).when(service).getBaseContext();
        doCallRealMethod().when(service).start(anyInt(), anyString());
        doCallRealMethod().when(service).stop();

        service.start(PORT, root);

        service.stop();
        service.stop();
        verify(server).stop();//2nd stop, server is null
        verify(service, times(2)).stopForeground(true);

        //clean
        rootFolder.delete();
    }
}
