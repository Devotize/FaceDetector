
import android.view.LayoutInflater
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.sychev.camera.impl.R

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember {ProcessCameraProvider.getInstance(context)}
    AndroidView(
//        modifier = modifier,
        factory = {
            val view = LayoutInflater.from(context).inflate(R.layout.camera_host, null, false)
            val cameraView = view.findViewById<PreviewView>(R.id.previewView)
            cameraView
        }) {
        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(
                lifecycleOwner,
                it as PreviewView /*the inflated layout*/,
                cameraProvider)
        }, ContextCompat.getMainExecutor(context))
        it.apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
}

fun bindPreview(
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    cameraProvider: ProcessCameraProvider
) {
    val preview: Preview = Preview.Builder().build()

    val cameraSelector: CameraSelector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
        .build()

    preview.setSurfaceProvider(previewView.surfaceProvider)

    var camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
}