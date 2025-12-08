package com.example.sunalert.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
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
import androidx.navigation.NavHostController
import com.example.sunalert.HistoryEntity
import com.example.sunalert.HistoryViewModel
import com.example.sunalert.LocationState
import com.example.sunalert.LocationViewModel
import com.example.sunalert.R
import com.example.sunalert.UVViewModel
import androidx.compose.foundation.layout.Arrangement
import com.example.sunalert.SharedHistoryViewModel
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
    viewModelUV: UVViewModel = viewModel(),
    navController: NavHostController? = null,
    historyViewModel: HistoryViewModel,
    sharedHistoryVM: SharedHistoryViewModel
) {
    val context = LocalContext.current

    // States controlling flow
    var showResult by remember { mutableStateOf(false) }
    var isChecking by remember { mutableStateOf(false) }

    // permission launcher
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            Log.d("CekUV", "Permission granted, starting location fetch")
            viewModel.startLocationFetch(fusedLocationClient)
        } else {
            Log.d("CekUV", "Permission denied")
            viewModel.updateLocationStateToError("Izin lokasi diperlukan untuk melanjutkan")
        }
    }

    // ask permission on first composition if not granted
    LaunchedEffect(Unit) {
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (ContextCompat.checkSelfPermission(context, permission)
            != PackageManager.PERMISSION_GRANTED
        ) {
            locationPermissionLauncher.launch(permission)
        }
    }

    val state = viewModel.locationState.value
    val uvValue = viewModelUV.UVIndex.value
    val address = viewModel.addressResult.value

    Box(modifier = Modifier.fillMaxSize()) {
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

            // Status text: show progress / location / error depending on states
            Text(
                text = when {
                    isChecking && state is LocationState.Loading -> "Memuat Data Lokasi..."
                    isChecking && state is LocationState.Success -> {
                        val lat = state.location.latitude
                        val lng = state.location.longitude
                        "lat: ${lat}, long: ${lng}\n${address}"
                    }
                    state is LocationState.Success -> {
                        val lat = state.location.latitude
                        val lng = state.location.longitude
                        "lat: ${lat}, long: ${lng}\n${address}"
                    }
                    state is LocationState.Error -> "Gagal: ${state.message}"
                    else -> "Tekan tombol untuk cek UV"
                },
                fontFamily = poppins,
                fontSize = 14.sp,
                color = Color(0xFFF29A2E),
                lineHeight = 18.sp,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Big UV number
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

            val uvInfo = if (showResult) getUvRiskInfo(uvValue) else null

            if (uvInfo != null) {
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
            } else {
                Text("Belum ada data.", color = Color.White)
            }

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
                    text = uvInfo?.advice ?: "Belum ada data.",
                    fontFamily = poppins,
                    fontSize = 16.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Justify,
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // BUTTON: start the checking flow (async handled by LaunchedEffect listeners)
            Button(
                onClick = {
                    val permission = Manifest.permission.ACCESS_FINE_LOCATION
                    if (ContextCompat.checkSelfPermission(context, permission)
                        != PackageManager.PERMISSION_GRANTED
                    ) {
                        locationPermissionLauncher.launch(permission)
                        return@Button
                    }

                    Log.d("CekUV", "Tombol Cek UV diklik — mulai proses")
                    isChecking = true
                    showResult = false
                    viewModel.startLocationFetch(fusedLocationClient)
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

            Spacer(modifier = Modifier.height(12.dp))

            if (navController != null) {
                SkyCheckButton(navController = navController)
            }
        }
    }

    // EFFECT: when location becomes Success while we are checking -> fetch UV
    LaunchedEffect(state, isChecking) {
        if (isChecking) {
            when (state) {
                is LocationState.Success -> {
                    val lat = state.location.latitude
                    val lng = state.location.longitude
                    Log.d("CekUV", "Lokasi sukses — memanggil fetchUV($lat, $lng)")
                    viewModelUV.fetchUV(lat, lng)
                }
                is LocationState.Error -> {
                    Log.d("CekUV", "Lokasi error: ${state.message}")
                    // you may want to set isChecking = false here if you don't want retries
                }
                is LocationState.Loading -> {
                    // still waiting
                }
                else -> {}
            }
        }
    }

    // EFFECT: when UV value is available while we are checking -> save history & show result
    LaunchedEffect(uvValue, isChecking) {
        if (isChecking && uvValue != null) {
            val s = viewModel.locationState.value
            if (s is LocationState.Success) {
                val lat = s.location.latitude
                val lng = s.location.longitude
                val uv = uvValue
                val uvInfoLocal = getUvRiskInfo(uv)

                val history = HistoryEntity(
                    timestamp = System.currentTimeMillis(),
                    latitude = lat,
                    longitude = lng,
                    alamat = viewModel.addressResult.value,
                    uvIndex = uv,
                    kategoriRisiko = uvInfoLocal.label,
                    rekomendasi = uvInfoLocal.advice,
                    fotoUri = "",
                    note = null
                )

                Log.d("CekUV", "Menyimpan history uv=$uv")
                historyViewModel.insertHistory(history) { newId ->
                    sharedHistoryVM.lastHistoryId = newId
                    Log.d("CekUV", "History disimpan id=$newId")
                }

                showResult = true
                isChecking = false
            } else {
                Log.d("CekUV", "UV tersedia, tapi lokasi bukan Success — tidak menyimpan")
            }
        }
    }
}

@Composable
fun SkyCheckButton(navController: NavHostController) {
    Button(
        onClick = { navController.navigate("skycheck") },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .shadow(12.dp, RoundedCornerShape(16.dp))
            .background(
                Color(0xFFF3C94B),
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            val imgRes = runCatching { R.drawable.img }.getOrElse { 0 }
            if (imgRes != 0) {
                Image(
                    painter = painterResource(id = imgRes),
                    contentDescription = null,
                    modifier = Modifier.size(52.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    "SkyCheck",
                    fontFamily = poppins,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    "Potret langit untuk dokumentasi",
                    fontFamily = poppins,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4D2412)
                )
            }
        }
    }
}