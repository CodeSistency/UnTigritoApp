package com.thecodefather.untigrito.presentation.screens.client.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecodefather.untigrito.R

@Composable
fun RequestServiceCard(
    modifier: Modifier = Modifier,
    onPublishServiceClick: () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE67822),
            contentColor = MaterialTheme.colorScheme.onTertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(R.drawable.radar),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(48.dp)
                )
                Column {
                    Text(
                        text = "Publica tu Solicitud",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Describe tu problema y resive ofertas al instante",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(top = 4.dp, end = 10.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Row() {
                Spacer(
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                        .size(48.dp)
                )
                Button(
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor =  Color.White,
                        )
                    ,
                    onClick = onPublishServiceClick,
                    modifier = Modifier
                        .wrapContentWidth(align = Alignment.CenterHorizontally)

                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Publicar Servicio",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

            }
        }
    }
}

@Preview
@Composable
private fun test() {
    RequestServiceCard(onPublishServiceClick = {})
}
