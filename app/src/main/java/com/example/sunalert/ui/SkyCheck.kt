package com.example.sunalert.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.example.sunalert.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun SkyCheckScreen(navBack: (() -> Unit)? = null) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var camera: Camera? by remember { mutableStateOf(null) }
    var isFlashEnabled by remember { mutableStateOf(false) }
    var lastCapturedImageUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        hasPermission = granted
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            lastCapturedImageUri = it
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F0F10))) {
        if (hasPermission) {
            CameraPreviewView(
                modifier = Modifier.fillMaxSize(),
                lifecycleOwner = lifecycleOwner,
                context = context,
                onImageCaptureReady = { capture, cam ->
                    imageCapture = capture
                    camera = cam
                }
            )
        } else {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.Text("Permission kamera diperlukan", color = Color.White)
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Button(onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    androidx.compose.material3.Text("Izinkan Kamera")
                }
            }
        }

        IconButton(
            onClick = { navBack?.invoke() },
            modifier = Modifier
                .padding(16.dp)
                .size(48.dp)
                .background(Color.Black.copy(alpha = 0.5f), shape = CircleShape)
                .align(Alignment.TopStart)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_close_clear_cancel),
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val logoRes = runCatching { R.drawable.logo_teks_samping }.getOrElse { 0 }
            if (logoRes != 0) {
                Image(
                    painter = painterResource(id = logoRes),
                    contentDescription = "Logo",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.Center)
        ) {
            Box(modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(0.35f)
                .align(Alignment.TopCenter)
                .background(Color.White.copy(alpha = 0.6f)))
            Box(modifier = Modifier
                .height(3.dp)
                .fillMaxWidth(0.35f)
                .align(Alignment.BottomCenter)
                .background(Color.White.copy(alpha = 0.6f)))
            Box(modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(0.35f)
                .align(Alignment.CenterStart)
                .background(Color.White.copy(alpha = 0.6f)))
            Box(modifier = Modifier
                .width(3.dp)
                .fillMaxHeight(0.35f)
                .align(Alignment.CenterEnd)
                .background(Color.White.copy(alpha = 0.6f)))
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 36.dp)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.DarkGray, shape = MaterialTheme.shapes.medium)
                    .clickable {
                        galleryLauncher.launch("image/*")
                    },
                contentAlignment = Alignment.Center
            ) {
                if (lastCapturedImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(lastCapturedImageUri),
                        contentDescription = "Last captured",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    androidx.compose.material3.Text("📷", color = Color.White)
                }
            }

            Box(
                modifier = Modifier
                    .size(84.dp)
                    .background(Color.White, shape = CircleShape)
                    .clickable {
                        imageCapture?.let { capture ->
                            val photoFile = File(
                                context.getExternalFilesDir(null),
                                SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US)
                                    .format(System.currentTimeMillis()) + ".jpg"
                            )

                            val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                            capture.takePicture(
                                outputOptions,
                                ContextCompat.getMainExecutor(context),
                                object : ImageCapture.OnImageSavedCallback {
                                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                                        lastCapturedImageUri = Uri.fromFile(photoFile)
                                        Log.d("SkyCheck", "Photo saved: ${photoFile.absolutePath}")
                                    }

                                    override fun onError(exc: ImageCaptureException) {
                                        Log.e("SkyCheck", "Photo capture failed: ${exc.message}", exc)
                                    }
                                }
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.shutter),
                    contentDescription = "Capture",
                    modifier = Modifier.size(64.dp)
                )
            }

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(
                        if (isFlashEnabled) Color(0xFFFDEE4B) else Color.DarkGray,
                        shape = MaterialTheme.shapes.medium
                    )
                    .clickable {
                        isFlashEnabled = !isFlashEnabled
                        camera?.cameraControl?.enableTorch(isFlashEnabled)
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.flash),
                    contentDescription = "Flash",
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
fun CameraPreviewView(
    modifier: Modifier = Modifier,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    context: Context,
    onImageCaptureReady: (ImageCapture, Camera) -> Unit
) {
    AndroidView(factory = { ctx ->
        val previewView = PreviewView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            val imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                val camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                onImageCaptureReady(imageCapture, camera)
            } catch (e: Exception) {
                Log.e("SkyCheck", "Camera binding failed", e)
            }
        }, ContextCompat.getMainExecutor(ctx))

        previewView
    }, modifier = modifier)
}