package com.shubhankaranku.expi.UiComponents

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.ViewGroup
import java.io.IOException
import kotlin.jvm.Throws
import android.content.res.Configuration
import com.shubhankaranku.expi.Common.CameraSource

class CameraPreview (context: Context, attrs: AttributeSet?) : ViewGroup (context, attrs){
    private val surfaceView: SurfaceView
    private var startRequested= false
    private var surfaceAvailable = false
    private var cameraSource: CameraSource? = null
    private var overlay: GraphicsOverlay? = null

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource){
        if (cameraSource == null){
            stop()
        }
        this.cameraSource = cameraSource
        if (this.cameraSource != null){
            startRequested = true
            startIfReady()
        }
    }


    fun stop() {
        if (cameraSource != null){
            cameraSource!!.stop()
        }
    }

    fun release() {
        if (cameraSource != null){
            cameraSource!!.release()
            cameraSource = null
        }
    }

    @Throws(IOException::class)
    fun start(cameraSource: CameraSource?, overlay: GraphicsOverlay?){
        this.overlay = overlay
        if (cameraSource != null) {
            start(cameraSource)
        }
    }

    @SuppressLint("MissingPermission")
    @Throws(IOException::class)
    private fun startIfReady(){
        if (startRequested && surfaceAvailable){
            cameraSource!!.start()
            if (overlay != null){
                val size = cameraSource!!.previewSize
                val min = Math.min(size!!.width, size.height)
                val max = Math.max(size.width, size.height)

                if (isPortraitMode){
                    overlay!!.setCameraInfo(min, max, cameraSource!!.cameraFacing)
                }else{
                    overlay!!.setCameraInfo(max, min, cameraSource!!.cameraFacing)
                }
                overlay!!.clear()
            }
            startRequested = false
        }
    }




    override fun onLayout(p0: Boolean, p1: Int, p2: Int, p3: Int, p4: Int) {
        var width = 320
        var height = 240
        if (cameraSource != null){
            val size  = cameraSource!!.previewSize
            if (size != null){
                width = size.width
                height = size.height
            }
        }

        if (isPortraitMode){
            val tmp :Int = width
            width = height
            height = tmp

        }

        val layoutWidth :Int = right - left
        val layoutHeight :Int = bottom - top

        var childWidth :Int = layoutWidth
        var childHeight :Int = (layoutWidth.toFloat() / width.toFloat() * height).toInt()

        if (childHeight > layoutHeight){
            childHeight = layoutHeight
            childWidth = (layoutHeight.toFloat() / height.toFloat() * width) as Int
        }

        for (i :Int in 0 until childCount){
            getChildAt(i).layout(0,0, childWidth, childHeight)
            Log.d(TAG, "Assigned View: $i")

        }
        try {
            startIfReady()
        }catch (e: IOException){
            Log.e(TAG, " Could not start camera source", e)
        }

    }

    private val isPortraitMode: Boolean
    private get() {
        val orientation = context.resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            return false
        }
        if (orientation == Configuration.ORIENTATION_PORTRAIT){
            return true
        }
        Log.d("Tag", "isPortraitMode returning false by default")
        return false
    }

    private inner class SurfaceCallback: SurfaceHolder.Callback{
        override fun surfaceCreated(surface: SurfaceHolder) {
            surfaceAvailable = true
            try {
                startIfReady()
            }catch (e: IOException){
                Log.e("Tag", "Could not start camera source.", e)
            }
        }

        override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {

        }

        override fun surfaceDestroyed(surface: SurfaceHolder) {
            surfaceAvailable = false
        }

    }

    companion object{
        private const val TAG = "EXPI"
    }

    init {
        surfaceView = SurfaceView(context)
        surfaceView.holder.addCallback(SurfaceCallback())
        addView(surfaceView)
    }


}