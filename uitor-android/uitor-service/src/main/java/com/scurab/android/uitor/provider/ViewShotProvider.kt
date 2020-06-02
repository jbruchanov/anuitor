package com.scurab.android.uitor.provider

import android.graphics.Bitmap
import android.view.ViewGroup
import com.scurab.android.uitor.extract2.DetailExtractor
import com.scurab.android.uitor.reflect.WindowManager
import com.scurab.android.uitor.tools.*

/**
 * Class helping to get a screenshot of particular view
 */
class ViewShotProvider(private val windowManager: WindowManager) {

    fun getViewShot(screenIndex: Int, viewIndex: Int): ByteArray? {
        return windowManager.getRootView(screenIndex)?.let { rootView ->
            DetailExtractor.findViewByPosition(rootView, viewIndex)
                    ?.let { view ->
                        synchronized(LOCK) {
                            Executor.runInMainThreadBlockingOnlyIfCrashing {
                                view.takeIf { it is ViewGroup && !DetailExtractor.isExcludedViewGroup(it.javaClass.name) }
                                        ?.let { view.renderBackground() }
                                        ?: view.takeIf { it.hasSize() }?.render()
                            }
                        }?.let {
                            var bitmap = it
                            //TODO: add scaling directly to render
                            val (scaleX, scaleY) = view.absoluteScale()
                            if (scaleX != 1f || scaleY != 1f && it.width > 0 && it.height > 0) {
                                val scaledW = ((it.width * scaleX) + 0.5f).toInt()
                                val scaledH = ((it.height * scaleY) + 0.5f).toInt()
                                if (scaledW > 0 && scaledH > 0) {//just prevention about some nonsense
                                    val scaled = Bitmap.createScaledBitmap(bitmap, scaledW, scaledH, false)
                                    it.recycle()
                                    bitmap = scaled
                                }
                            }
                            bitmap.save()
                        } ?: EMPTY
                    }
        }
    }

    companion object {
        //lock rendering to 1 view a time
        //hammering this crashing the app
        private val LOCK = Object()
        private val EMPTY = ByteArray(0)
    }
}