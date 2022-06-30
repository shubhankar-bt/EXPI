package com.shubhankaranku.expi.Interfaces

import android.graphics.Bitmap
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.shubhankaranku.expi.Common.FrameMetaData
import com.shubhankaranku.expi.UiComponents.GraphicsOverlay

interface FrameReturn {
    fun onFrame(
        image: Bitmap?,
        face: FirebaseVisionFace?,
        frameMetadata: FrameMetaData?,
        graphicOverlay: GraphicsOverlay?
    )
}