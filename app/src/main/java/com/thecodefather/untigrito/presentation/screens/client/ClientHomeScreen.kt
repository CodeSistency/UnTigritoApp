package com.thecodefather.untigrito.presentation.screens.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thecodefather.untigrito.presentation.screens.client.components.HomeHeader
import com.thecodefather.untigrito.R
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
import com.thecodefather.untigrito.presentation.navigation.Routes
import com.thecodefather.untigrito.presentation.screens.client.components.RequestServiceCard
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenClient(
    navController: NavController,
    onNavigateToRechargeMethods: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF0F0F0)) // Fondo ligeramente gris
    ) {
        AppTopBar()
        Spacer(modifier = Modifier.height(16.dp))


        // Tarjeta de historial
        HistoryCard(onNavigateToRechargeMethods)

        Spacer(modifier = Modifier.height(24.dp))

        // Explora categorías
        CategorySection()

        Spacer(modifier = Modifier.height(24.dp))

        // Tigres mejor calificados
        TopRatedTigersSection()

        Spacer(modifier = Modifier.height(24.dp))

        // Publica tu Solicitud
        PublishRequestCard()

        Spacer(modifier = Modifier.height(24.dp))

        // Servicios
        ServicesSection()

        Spacer(modifier = Modifier.height(16.dp)) // Espacio al final del contenido
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    HomeHeader(userName = "Juan Pérez")

}


@Composable
fun HistoryCard(onNavigateToRechargeMethods: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Fondo blanco
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .clickable(onClick = onNavigateToRechargeMethods) // Hacer la tarjeta clickeable
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con el icono de billetera
                        contentDescription = "Wallet Icon",
                        tint = Color(0xFFE67822),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "15.000,00 Bs",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con el icono de exclamación
                    contentDescription = "Info Icon",
                    tint = Color(0xFFE67822),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con el icono de historial
                    contentDescription = "History Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con el icono de flecha
                    contentDescription = "Arrow Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CategorySection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Explora categorías",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Icon(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con el icono de flecha
                contentDescription = "Ver más categorías",
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index -> // 3 categorías de ejemplo
                CategoryItem(name = "Categoría ${index + 1}", iconRes = R.drawable.ic_launcher_foreground)
            }
        }
    }
}

@Composable
fun CategoryItem(name: String, iconRes: Int) {
    Card(
        modifier = Modifier.size(120.dp, 100.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = iconRes),
                contentDescription = name,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name, style = MaterialTheme.typography.bodySmall, color = Color.Black)
        }
    }
}


@Composable
fun TopRatedTigersSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Tigres mejor calificados",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 24.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(3) { index -> // 3 profesionales de ejemplo
                TopRatedTigerItem(name = "Profesional ${index + 1}", rating = 4.8f, reviews = 120, profession = "Electricista", location = "Valencia")
            }
        }
    }
}

@Composable
fun TopRatedTigerItem(name: String, rating: Float, reviews: Int, profession: String, location: String) {
    Card(
        modifier = Modifier.width(200.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con imagen de perfil
                contentDescription = "Perfil de $name",
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                Text(text = "$profession | $location", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(text = "$rating ($reviews)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun PublishRequestCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth(), // Color naranja
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        RequestServiceCard {  }
    }
}

@Composable
fun ServicesSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Servicios",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(
            modifier = Modifier.fillMaxWidth().height(300.dp), // Altura fija para el preview, ajustar si es necesario
            contentPadding = PaddingValues(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(4) { index -> // 4 servicios de ejemplo
                ServiceItem(
                    title = "Reparación de Fugas",
                    description = "Servicio de plomería urgente 24/7",
                    provider = "Andrés Rodríguez",
                    rating = 4.8f,
                    reviews = 120,
                    distance = "\n$" + (index + 1) * 10 + " Km"
                )
            }
        }
    }
}

@Composable
fun ServiceItem(title: String, description: String, provider: String, rating: Float, reviews: Int, distance: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground), // Reemplaza con imagen del servicio
                    contentDescription = "Servicio de $title",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = description, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Por: $provider", style = MaterialTheme.typography.bodySmall, color = Color(0xFFE67822))
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                        Text(text = "$rating ($reviews)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            }
            Text(text = distance, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreenClient(navController = navController, onNavigateToRechargeMethods = {})
}


