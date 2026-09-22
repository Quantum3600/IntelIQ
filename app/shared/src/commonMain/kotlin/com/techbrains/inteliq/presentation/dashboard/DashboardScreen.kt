package com.techbrains.inteliq.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.techbrains.inteliq.presentation.theme.Ink
import com.techbrains.inteliq.presentation.theme.Paper
import com.techbrains.inteliq.presentation.theme.PaperRaised
import com.techbrains.inteliq.presentation.theme.Signal

@Composable
fun DashboardScreen(
    onStartPractice: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Paper)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Welcome Back!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = Ink
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Pick up where you left off",
                fontSize = 15.sp,
                color = Ink.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Continue Learning Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = PaperRaised),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = "Recommended Next",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Signal
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Linear Equations & Inequalities",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Ink
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Targets Developing skill at suitable difficulty",
                        fontSize = 13.sp,
                        color = Ink.copy(alpha = 0.7f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = onStartPractice,
                        colors = ButtonDefaults.buttonColors(containerColor = Signal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Start Recommended Practice", color = PaperRaised)
                    }
                }
            }
        }
    }
}
