package com.sychev.camera.api

import com.sychev.common.BaseEntryPoint

abstract class CameraEntryPoint: BaseEntryPoint() {

    final override val entryRoute: String
        get() = "camera"
}