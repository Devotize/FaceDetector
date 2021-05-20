package com.sychev.facedetector.service

import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.media.projection.MediaProjectionManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.sychev.facedetector.presentation.PhotoFaceDetector
import com.sychev.facedetector.utils.TAG



class FaceDetectorService: Service() {

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "face_detector_service_channel_id"
        const val SERVICE_ID = 1
        const val EXIT_REQUEST_CODE = 2
        const val EXIT_NAME = "exit_intent"
        const val EXIT_VALUE = "command_exit"
    }

    private var faceDetector: PhotoFaceDetector? = null

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun stopService() {
        stopForeground(true)
        stopSelf()
        faceDetector?.close()
        faceDetector = null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val command = intent?.getStringExtra(EXIT_NAME)
        if (command == EXIT_VALUE){
            Log.d(TAG, "onStartCommand: trying to stop service")
            faceDetector?.let{
                stopService().also { return START_NOT_STICKY }
            }
            return START_NOT_STICKY
        }
        Log.d(TAG, "onStartCommand: starting face detector")

        val data = intent?.getParcelableExtra<Intent>("data")
        if (data != null){
            Log.d(TAG, "onStartCommand: starting foreground")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
                startForeground(
                    SERVICE_ID,
                    createNotification(),
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PROJECTION
                )
            } else {
                startForeground(
                    SERVICE_ID,
                    createNotification()
                )
            }
            val projectionManager = applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
            val mediaProjection = projectionManager.getMediaProjection(RESULT_OK, data)
//            VideoFaceDetector(applicationContext, mediaProjection)
            faceDetector = PhotoFaceDetector(applicationContext, mediaProjection, ::stopService)
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        faceDetector?.close()
        faceDetector = null
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    "Face Detector Service",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    enableLights(false)
                    enableVibration(false)
                    setSound(null, null)
                    setShowBadge(false)
                    lockscreenVisibility =Notification.VISIBILITY_PUBLIC
                }
            )
        }

        val exitIntent = Intent(applicationContext, FaceDetectorService::class.java)
        exitIntent.putExtra("exit_intent", "command_exit")
        val exitPendingIntent = PendingIntent.getService(
            applicationContext,
            EXIT_REQUEST_CODE,
            exitIntent,
            0
        )

        return NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Face Detector")
            .setContentText("Some content text")
            .setAutoCancel(false)
            .setOngoing(true)
            .setWhen(System.currentTimeMillis())
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .addAction(NotificationCompat.Action(
                0,
                "Exit",
                exitPendingIntent
            ))
            .build()

    }


}












