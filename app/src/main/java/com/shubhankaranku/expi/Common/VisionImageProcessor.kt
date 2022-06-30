package com.shubhankaranku.expi.Common

import android.graphics.Bitmap
import com.google.firebase.ml.common.FirebaseMLException
import com.shubhankaranku.expi.UiComponents.GraphicsOverlay
import java.nio.ByteBuffer
import kotlin.jvm.Throws

interface VisionImageProcessor {

    //process the images with underlying machine learning model

    @Throws(FirebaseMLException::class)
    fun process(data: ByteBuffer?, frameMetaData: FrameMetaData?, graphicsOverlay: GraphicsOverlay)

    //process the bitmap images
    fun process(bitmap: Bitmap?, graphicsOverlay: GraphicsOverlay)

    //stop the underlying machine learning model and release resources
    fun stop()

}