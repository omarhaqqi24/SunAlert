package com.example.sunalert.ui

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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.navigation.NavHostController
import com.example.sunalert.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.example.sunalert.Destinations

val poppins = FontFamily(
    Font(R.font.poppins_light, FontWeight.Light),
    Font(R.font.poppins_semibold, FontWeight.SemiBold),
    Font(R.font.poppins_bold, FontWeight.Bold)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingPage(
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = .75f))
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
                contentDescription = "",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                fontFamily = poppins,
                fontSize = 28.sp,
                color = Color.White,
                lineHeight = 40.sp,
                text = buildAnnotatedString {
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append("Selamat datang...\nMau cek ")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.SemiBold)) {
                        append("UV Index")
                    }
                    withStyle(style = SpanStyle(fontWeight = FontWeight.Light)) {
                        append("?")
                    }
                }
            )

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = {
                    navController.navigate("cekUV")
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Black.copy(alpha = .5f),
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(16.dp))
                    .zIndex(1f),
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(vertical = 14.dp)
            ) {
                Text(
                    text = "Cek UV Index",
                    fontFamily = poppins,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    navController.navigate(Destinations.HISTORY)
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
                    text = "Cek Riwayat",
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
//fun LPPreview() {
//    val navController = rememberNavController()
//    val fusedLocationClient = LocationServices.getFusedLocationProviderClient()
//    LandingPage(navController)
//}