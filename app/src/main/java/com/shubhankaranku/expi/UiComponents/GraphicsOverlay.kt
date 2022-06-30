package com.shubhankaranku.expi.UiComponents

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.google.android.gms.vision.CameraSource

class GraphicsOverlay (context: Context?, attrs: AttributeSet?) : View(context,attrs){

    private val lock = Any()
    private var previewWidth = 0
    private var widthScaleFactor = 1.0f
    private var previewHeight = 0
    private var heightScaleFactor = 1.0f
    private var facing = CameraSource.CAMERA_FACING_BACK
    private var graphics: MutableList<Graphic> = ArrayList()

    //remove all graphics from overlay
    fun clear (){
        synchronized(lock) { graphics.clear() }
        postInvalidate()
    }

    //adds a graphics to the overlay
    fun add(graphic: Graphic){
        synchronized(lock) { graphics.add(graphic)}
    }

    //remove particular graphics from overlay
    fun remove (graphic: Graphic){
        synchronized(lock) { graphics.remove(graphic) }
        postInvalidate()
    }



    abstract class Graphic(private val overlay: GraphicsOverlay){
        abstract fun draw(canvas: Canvas?)

        protected fun scaleX(horizontal: Float): Float{
            return horizontal * overlay.widthScaleFactor
        }

        fun scaleY(vertical: Float): Float{
            return vertical * overlay.heightScaleFactor
        }

        val applicationContext: Context
            get() = overlay.context.applicationContext

        fun translateX(x: Float): Float{
            return if (overlay.facing == CameraSource.CAMERA_FACING_FRONT){
                overlay.width - scaleX(x)
            }else{
                scaleX(x)
            }
        }

        fun translateY(y: Float): Float{
            return scaleY(y)
        }

        fun postInvalidate() {
            overlay.postInvalidate()
        }

    }

    fun setCameraInfo(previewWidth: Int, previewHeight: Int, facing: Int){
        synchronized(lock){
            this.previewWidth = previewWidth
            this.previewHeight = previewHeight
            this.facing = facing
        }
        postInvalidate()
    }

    //Draws the overlay with its associated graphic objects
    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        synchronized(lock){
            if (previewWidth != 0 && previewHeight != 0){
                if (canvas != null) {
                    widthScaleFactor = canvas.width.toFloat() / previewWidth.toFloat()
                    heightScaleFactor = canvas.height.toFloat() / previewHeight.toFloat()
                }

            }
            for (graphic :Graphic in graphics){
                graphic.draw(canvas)
            }
        }
    }
}