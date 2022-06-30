package com.shubhankaranku.expi.Interfaces

import com.shubhankaranku.expi.Models.RectModel


interface FaceDetectStatus {
    fun onFaceLocated(rectModel: RectModel?)
    fun onFaceNotLocated()
}