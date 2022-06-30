package com.shubhankaranku.expi.Common

import android.graphics.Bitmap
import androidx.annotation.GuardedBy
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.shubhankaranku.expi.Common.BitmapUtils.getBitmap
import com.shubhankaranku.expi.UiComponents.GraphicsOverlay
import java.nio.ByteBuffer

abstract class VisionProcessorBase<T> : VisionImageProcessor {

    // keep the data of latest images and its meta data
    @GuardedBy("this")
    private var latestImage: ByteBuffer? = null

    @GuardedBy("this")
    private var latestImageMetaData: FrameMetaData? = null


    // keep the data of latest images and its meta data with are is under process
    @GuardedBy("this")
    private var processingImage: ByteBuffer? = null

    @GuardedBy("this")
    private var processingMetaData: FrameMetaData? = null


    override fun process(
        data: ByteBuffer?,
        frameMetadata: FrameMetaData?,
        graphicOverlay: GraphicsOverlay
    ) {
        latestImage = data
        latestImageMetaData = frameMetadata
        if (processingImage == null && processingMetaData == null) {
            processLatestImage(graphicOverlay!!)
        }
    }

    override fun process(bitmap: Bitmap?, graphicsOverlay: GraphicsOverlay) {
        detectInVisionImage(null /* bitmap */, FirebaseVisionImage.fromBitmap(bitmap!!), null,
            graphicsOverlay!!)
    }

    @Synchronized
    private fun processLatestImage(graphicOverlay: GraphicsOverlay) {
        processingImage = latestImage
        processingMetaData = latestImageMetaData
        latestImage = null
        latestImageMetaData = null
        if (processingImage != null && processingMetaData != null) {
            processImage(processingImage!!, processingMetaData!!, graphicOverlay)
        }
    }

    private fun processImage(
        data: ByteBuffer, frameMetadata: FrameMetaData,
        graphicOverlay: GraphicsOverlay) {
        val metadata = FirebaseVisionImageMetadata.Builder()
            .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
            .setWidth(frameMetadata.width)
            .setHeight(frameMetadata.height)
            .setRotation(frameMetadata.rotation)
            .build()
        val bitmap = getBitmap(data, frameMetadata)
        detectInVisionImage(
            bitmap, FirebaseVisionImage.fromByteBuffer(data, metadata), frameMetadata,
            graphicOverlay)
    }

    private fun detectInVisionImage(
        originalCameraImage: Bitmap?,
        image: FirebaseVisionImage,
        metadata: FrameMetaData?,
        graphicOverlay: GraphicsOverlay) {
        detectInImage(image)
            .addOnSuccessListener { results ->
                this@VisionProcessorBase.onSuccess(originalCameraImage, results,
                    metadata!!,
                    graphicOverlay)
                processLatestImage(graphicOverlay)
            }
            .addOnFailureListener { e -> this@VisionProcessorBase.onFailure(e) }
    }

    override fun stop() {}
    protected abstract fun detectInImage(image: FirebaseVisionImage?): Task<T>

    protected abstract fun onSuccess(
        originalCameraImage: Bitmap?,
        results: T,
        frameMetadata: FrameMetaData,
        graphicOverlay: GraphicsOverlay)

    protected abstract fun onFailure(e: Exception)
}