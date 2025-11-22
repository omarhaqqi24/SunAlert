package com.example.sunalert.ui

import android.Manifest
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sunalert.LocationState
import com.example.sunalert.LocationViewModel
import com.example.sunalert.R
import com.example.sunalert.UVViewModel
import com.google.android.gms.location.FusedLocationProviderClient

@Composable
fun CekUVScreen(
    fusedLocationClient: FusedLocationProviderClient,
    viewModel: LocationViewModel = viewModel(),
    viewModelUV: UVViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        val context = LocalContext.current

        val locationPermissionLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.startLocationFetch(fusedLocationClient)
            } else {
                viewModel.updateLocationStateToError("Izin lokasi diperlukan untuk mealnjutkan")
            }
        }

        LaunchedEffect(Unit) {
            val permission = Manifest.permission.ACCESS_FINE_LOCATION

            when {
                ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED -> {
                    viewModel.startLocationFetch(fusedLocationClient)
                } else -> {
                locationPermissionLauncher.launch(permission)
            }
            }
        }

        val state = viewModel.locationState.value

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

//            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = when (state) {
                    is LocationState.Loading -> "Memuat Data..."
                    is LocationState.Success -> {
                        val lat = state.location.latitude
                        val lng = state.location.longitude
                        viewModelUV.fetchUV(lat, lng)

                        "lat: ${state.location.latitude}, long: ${state.location.longitude}\n${viewModel.addressResult.value}"
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
                text = when (val uv = viewModelUV.UVIndex.value) {
                    null -> "--"
                    else -> uv.toInt().toString()
                },
                fontFamily = poppins,
                fontWeight = FontWeight.Bold,
                fontSize = 200.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                textAlign = TextAlign.Center,
                lineHeight = 28.sp,
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(
                        color = Color(0xFFFDEE4B),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )) {
                        append("Sangat Tinggi\n")
                    }
                    withStyle(style = SpanStyle(
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 12.sp
                    )) {
                        append("UV Index Saat Ini")
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
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

//@Preview
//@Composable
//fun PreviewCekUV() {
//    CekUVScreen()
//}