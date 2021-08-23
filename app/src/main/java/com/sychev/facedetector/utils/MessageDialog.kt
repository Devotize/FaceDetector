package com.sychev.facedetector.utils

import androidx.compose.runtime.mutableStateListOf

class MessageDialog private constructor(builder: Builder) {

    val title: String
    val message: String
    val onDismiss: () -> Unit
    val onPositiveAction: () -> Unit

    companion object{
        val dialogMessages = mutableStateListOf<MessageDialog>()
    }

    init {
        title = builder.title
        message = builder.message
        onDismiss = builder.onDismiss
        onPositiveAction = builder.onPositiveAction
    }

    class Builder(){

        lateinit var message: String
        private set
        lateinit var title: String
        private set
        lateinit var onDismiss: () -> Unit
        private set
        lateinit var onPositiveAction: () -> Unit
        private set

        fun message(message: String): Builder {
            this.message = message
            return this
        }

        fun title(title: String): Builder {
            this.title = title
            return this
        }

        fun onDismiss(onDismiss: () -> Unit): Builder {
            this.onDismiss = onDismiss
            return this
        }

        fun onPositiveAction(onPositiveAction: () -> Unit): Builder {
            this.onPositiveAction = onPositiveAction
            return this
        }

        fun build(): MessageDialog {
            return MessageDialog(this)
        }

    }

}