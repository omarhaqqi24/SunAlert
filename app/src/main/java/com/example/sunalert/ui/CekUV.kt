package com.example.sunalert.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunalert.HistoryEntity
import com.example.sunalert.HistoryViewModel
import com.example.sunalert.HistoryViewModelFactory
import com.example.sunalert.LocationState
import com.example.sunalert.LocationViewModel
import com.example.sunalert.R
import com.example.sunalert.UVViewModel
import com.google.android.gms.location.FusedLocationProviderClient

data class UvRiskInfo(
    val label: String,
    val color: Color,
    val advice: String
)

fun getUvRiskInfo(uv: Double?): UvRiskInfo {
    if (uv == null) {
        return UvRiskInfo(
            label = "Mencoba mengecek UV Index",
            color = Color.White,
            advice = "Sedang memuat data UV untuk memberikan rekomendasi yang sesuai."
        )
    }

    return when {
        uv <= 2.0 -> UvRiskInfo(
            label = "Risiko bahaya rendah",
            color = Color(0xFF4CAF50),
            advice = "Paparan sinar UV masih rendah. Tetap gunakan kacamata hitam saat cerah dan sunscreen jika memiliki kulit sensitif."
        )

        uv <= 5.0 -> UvRiskInfo(
            label = "Risiko bahaya sedang",
            color = Color(0xFFFFEB3B),
            advice = "Paparan UV cukup terasa. Usahakan berada di tempat teduh saat siang dan gunakan sunscreen SPF 30+, pakaian tertutup, serta kacamata hitam."
        )

        uv <= 7.0 -> UvRiskInfo(
            label = "Risiko bahaya tinggi",
            color = Color(0xFFFF9800),
            advice = "Sinar UV cukup berbahaya. Kurangi aktivitas luar ruangan di jam terik dan gunakan pelindung lengkap seperti sunscreen, topi, dan kacamata UV."
        )

        uv <= 10.0 -> UvRiskInfo(
            label = "Risiko bahaya sangat tinggi",
            color = Color(0xFFF44336),
            advice = "Paparan UV sangat kuat. Batasi waktu di bawah matahari dan selalu gunakan sunscreen, pakaian pelindung, serta kacamata UV."
        )

        else -> UvRiskInfo(
            label = "Risiko bahaya sangat ekstrem",
            color = Color(0xFF9C27B0),
            advice = "Sinar UV berada pada tingkat ekstrem. Hindari paparan langsung sebisa mungkin, dan jika terpaksa keluar gunakan perlindungan penuh."
        )
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun CekUVScreen(
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: LocationViewModel = viewModel(),
    viewModelUV: UVViewModel = viewModel()
) {
    val context = LocalContext.current
    val app = context.applicationContext as Application

    // HistoryViewModel untuk menyimpan riwayat
    val historyViewModel: HistoryViewModel = viewModel(
        factory = HistoryViewModelFactory(app)
    )

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val locationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.startLocationFetch(fusedLocationClient)
            } else {
                viewModel.updateLocationStateToError("Izin lokasi diperlukan untuk melanjutkan")
            }
        }

        // Minta izin lokasi saat pertama kali masuk screen
        LaunchedEffect(Unit) {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION
            when {
                ContextCompat.checkSelfPermission(context, permission) ==
                        PackageManager.PERMISSION_GRANTED -> {
                    viewModel.startLocationFetch(fusedLocationClient)
                }

                else -> {
                    locationPermissionLauncher.launch(permission)
                }
            }
        }

        val state = viewModel.locationState.value
        val uvValue = viewModelUV.UVIndex.value
        val address = viewModel.addressResult.value

        // ========= AUTO-SAVE HISTORY =========
        var lastSavedUv by remember { mutableStateOf<Double?>(null) }

        LaunchedEffect(uvValue, state) {
            if (uvValue != null &&
                uvValue != lastSavedUv &&
                state is LocationState.Success
            ) {
                val lat = state.location.latitude
                val lng = state.location.longitude
                val uvInfo = getUvRiskInfo(uvValue)

                val history = HistoryEntity(
                    timestamp = System.currentTimeMillis(),
                    latitude = lat,
                    longitude = lng,
                    alamat = address,
                    uvIndex = uvValue,
                    kategoriRisiko = uvInfo.label,
                    rekomendasi = uvInfo.advice,
                    fotoUri = "",   // nanti diisi URI foto SkyCheck
                    note = null
                )

                historyViewModel.insertHistory(history)
                lastSavedUv = uvValue
            }
        }
        // =====================================

        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .85f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp)
                .padding(top = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_teks_samping),
                contentDescription = null,
                modifier = Modifier.size(120.dp)
            )

            Text(
                text = when (state) {
                    is LocationState.Loading -> "Memuat Data..."
                    is LocationState.Success -> {
                        val lat = state.location.latitude
                        val lng = state.location.longitude
                        // trigger fetch UV ketika lokasi berhasil
                        viewModelUV.fetchUV(lat, lng)
                        "lat: ${lat}, long: ${lng}\n$address"
                    }

                    is LocationState.Error -> "Gagal: ${state.message}"
                    else -> "Menunggu Izin"
                },
                fontFamily = poppins,
                fontSize = 14.sp,
                color = Color(0xFFF29A2E),
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when (val uv = uvValue) {
                    null -> "--"
                    else -> String.format("%.1f", uv)
                },
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 180.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(5.dp))

            val uvInfo = getUvRiskInfo(uvValue)

            Text(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 24.sp,
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            color = uvInfo.color,
                            fontSize = 16.sp
                        )
                    ) {
                        append(uvInfo.label)
                    }
                    withStyle(
                        style = SpanStyle(
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    ) {
                        append("\nUV Index Saat Ini")
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Color.White.copy(alpha = .33f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = uvInfo.advice,
                    fontFamily = poppins,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    // user bisa klik untuk re-check lokasi/uv
                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFFFDEE4B),
                                Color(0xFFE75A08)
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Cek UV Indeks",
                    fontFamily = poppins,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White
                )
            }
        }
    }
}
