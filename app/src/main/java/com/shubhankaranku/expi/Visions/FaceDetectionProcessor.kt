package com.shubhankaranku.expi.Visions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Camera
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.shubhankaranku.expi.Common.CameraImageGraphic
import com.shubhankaranku.expi.Common.FrameMetaData
import com.shubhankaranku.expi.Common.VisionProcessorBase
import com.shubhankaranku.expi.Interfaces.FaceDetectStatus
import com.shubhankaranku.expi.Interfaces.FrameReturn
import com.shubhankaranku.expi.Models.RectModel
import com.shubhankaranku.expi.R
import com.shubhankaranku.expi.UiComponents.GraphicsOverlay

import java.io.IOException

class FaceDetectionProcessor(resources: Resources?) : VisionProcessorBase<List<FirebaseVisionFace?>?>(),
    FaceDetectStatus {
    var faceDetectStatus: FaceDetectStatus? = null
    private val detector: FirebaseVisionFaceDetector
    private val overlayBitmap: Bitmap
    var frameHandler: FrameReturn? = null

    override fun stop() {
        try {
            detector.close()
        } catch (e: IOException) {
            Log.e(TAG, "Exception thrown while trying to close Face Detector: $e")
        }
    }

    override fun detectInImage(image: FirebaseVisionImage?): Task<List<FirebaseVisionFace?>?> {
        return detector.detectInImage(image!!)
    }

    override fun onFailure(e: Exception) {
        Log.e(TAG, "Face detection failed $e")
    }

    override fun onFaceLocated(rectModel: RectModel?) {
        if (faceDetectStatus != null) faceDetectStatus!!.onFaceLocated(rectModel)
    }

    override fun onFaceNotLocated() {
        if (faceDetectStatus != null) faceDetectStatus!!.onFaceNotLocated()
    }

    companion object {
        private const val TAG = "FaceDetectionProcessor"
    }

    init {
        val options = FirebaseVisionFaceDetectorOptions.Builder()
            .setPerformanceMode(FirebaseVisionFaceDetectorOptions.FAST)
            .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
            .build()
        detector = FirebaseVision.getInstance().getVisionFaceDetector(options)
        overlayBitmap = BitmapFactory.decodeResource(resources, R.drawable.clown_nose)
    }

    override fun onSuccess(originalCameraImage: Bitmap?, faces: List<FirebaseVisionFace?>?, frameMetadata: FrameMetaData, graphicOverlay: GraphicsOverlay) {
        graphicOverlay.clear()
        if (originalCameraImage != null) {
            val imageGraphic = CameraImageGraphic(graphicOverlay, originalCameraImage)
            graphicOverlay.add(imageGraphic)
        }
        for (i in faces!!.indices) {
            val face = faces[i]
            if (frameHandler != null) {
                frameHandler!!.onFrame(originalCameraImage, face, frameMetadata, graphicOverlay)
            }
            val cameraFacing = frameMetadata?.cameraFacing ?:  android.hardware.Camera.CameraInfo.CAMERA_FACING_BACK
            val faceGraphic = FaceGraphic(graphicOverlay, face!!, cameraFacing, overlayBitmap)
            faceGraphic.faceDetectStatus = this
            graphicOverlay.add(faceGraphic)
        }
        graphicOverlay.postInvalidate()
    }
}