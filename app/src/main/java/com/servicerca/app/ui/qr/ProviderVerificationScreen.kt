package com.servicerca.app.ui.qr

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.servicerca.app.R
import com.servicerca.app.core.components.card.ConfirmServiceSummaryCard
import com.servicerca.app.core.components.navigation.AppTopAppBarBack
import com.servicerca.app.ui.theme.ServiCercaTheme
import java.util.concurrent.Executors


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderVerificationScreen(
    onBackClick: () -> Unit = {},
    viewModel: ProviderVerificationViewModel = hiltViewModel()
) {
    val colorScheme = MaterialTheme.colorScheme
    val typography = MaterialTheme.typography
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            AppTopAppBarBack(
                title = stringResource(R.string.confirm_service_title),
                onBackClick = onBackClick
            )
        },
        containerColor = colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Provider Service Summary Card
            ConfirmServiceSummaryCard()

            Spacer(modifier = Modifier.height(40.dp))

            // Cámara para escanear QR
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {
                QRCodeScannerScreen(
                    scannedValue = uiState.scannedValue,
                    onQrDetected = viewModel::onQrDetected
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.scan_qr_title),
                style = typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = stringResource(R.string.scan_qr_description),
                style = typography.bodyMedium,
                color = colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp),
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Confirmation Code Input
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = stringResource(R.string.confirmation_code_label),
                    style = typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Reusing AppTextField or similar structure if exists, falling back to OutlinedTextField for exact visual match
                OutlinedTextField(
                    value = uiState.scannedValue,
                    onValueChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    readOnly = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = colorScheme.surface,
                        unfocusedContainerColor = colorScheme.surface,
                        focusedBorderColor = colorScheme.primary,
                        unfocusedBorderColor = colorScheme.onSurface.copy(alpha = 0.1f)
                    ),
                    textStyle = typography.bodyLarge.copy(color = colorScheme.onSurface)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (uiState.isProcessing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(32.dp),
                    color = colorScheme.primary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.validating_qr_text),
                    style = typography.bodySmall,
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (!uiState.isProcessing && !uiState.isSuccess && uiState.message.isBlank()) {
                Text(
                    text = stringResource(R.string.scanning_qr_text),
                    style = typography.bodySmall,
                    color = colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }

        // Modal de resultado: éxito o error al escanear el QR
        if (uiState.showResultModal) {
            QrResultDialog(
                isSuccess = uiState.isSuccess,
                message = uiState.message,
                onDismiss = {
                    viewModel.dismissModal()
                    if (uiState.isSuccess) onBackClick()
                },
                onConfirm = {
                    viewModel.dismissModal()
                    if (uiState.isSuccess) onBackClick()
                }
            )
        }
    }
}

// Diálogo de resultado del escaneo QR (éxito o error)
@Composable
fun QrResultDialog(
    isSuccess: Boolean,
    message: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val iconVector = if (isSuccess) Icons.Default.CheckCircleOutline else Icons.Default.ErrorOutline
    val iconTint = if (isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
    val title = if (isSuccess) stringResource(R.string.service_confirmed_title) else stringResource(R.string.unable_to_confirm_title)
    val confirmText = if (isSuccess) stringResource(R.string.accept_action) else stringResource(R.string.retry_action)

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(48.dp)
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        titleContentColor = MaterialTheme.colorScheme.onBackground,
        textContentColor = MaterialTheme.colorScheme.onSurface,
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmText,
                    color = if (isSuccess) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

//@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProviderVerificationScreenPreview() {
    ServiCercaTheme {
        ProviderVerificationScreen()
    }
}


// Composable principal para el lector QR
@Composable
fun QRCodeScannerScreen(
    scannedValue: String,
    onQrDetected: (String) -> Unit
) {
    val context = LocalContext.current
    var hasCameraPermission by remember { mutableStateOf(false) }
    var permissionMessage by remember { mutableStateOf("") }

    // Launcher para solicitar permisos de cámara
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasCameraPermission = isGranted
            if (!isGranted) {
                permissionMessage = context.getString(R.string.camera_permission_denied)
            }
        }
    )

    // Solicitar permiso al iniciar el Composable
    LaunchedEffect(Unit) {
        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                hasCameraPermission = true
            }
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (hasCameraPermission) {
            CameraPreview(onQrCodeDetected = onQrDetected)
            Spacer(Modifier.height(16.dp))
            Text(text = stringResource(R.string.qr_result_format, scannedValue), modifier = Modifier.padding(16.dp))
        } else {
            // UI cuando no hay permiso
            Text(
                text = permissionMessage.ifBlank { stringResource(R.string.camera_permission_required_message) },
                modifier = Modifier.padding(16.dp)
            )
            Button(onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) }) {
                Text(stringResource(R.string.grant_permission_action))
            }
        }
    }
}

@Composable
fun CameraPreview(onQrCodeDetected: (String) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(),
        factory = { ctx ->
            val previewView = PreviewView(ctx)
            val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val preview = Preview.Builder().build().also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

                val imageAnalyzer = ImageAnalysis.Builder()
                    .setTargetResolution(android.util.Size(1280, 720))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, BarcodeAnalyzer(onQrCodeDetected))
                    }

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                try {
                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalyzer
                    )
                } catch (exc: Exception) {
                    // Manejar errores de inicialización de cámara
                }
            }, ContextCompat.getMainExecutor(ctx))
            previewView
        }
    )
    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
        }
    }
}

// Clase para analizar los frames de la cámara
class BarcodeAnalyzer(private val onQrCodeDetected: (String) -> Unit) : ImageAnalysis.Analyzer {

    private val scanner = BarcodeScanning.getClient(
        BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
    )

    @androidx.annotation.OptIn(androidx.camera.core.ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            scanner.process(image)
                .addOnSuccessListener { barcodes ->

                    println("Detectando QR... encontrados: ${barcodes.size}")

                    if (barcodes.isNotEmpty()) {
                        val value = barcodes.first().rawValue

                        println("QR DETECTADO: $value")

                        value?.let {
                            onQrCodeDetected(it)
                        }
                    }
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}
