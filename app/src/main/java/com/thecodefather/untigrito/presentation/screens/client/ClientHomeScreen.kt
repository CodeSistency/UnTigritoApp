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
import androidx.compose.material.icons.rounded.ArrowForwardIos
import androidx.compose.material.icons.rounded.Assignment
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.thecodefather.untigrito.presentation.screens.client.components.HomeHeader
import com.thecodefather.untigrito.R
import com.thecodefather.untigrito.presentation.components.ClientBottomNavBar
import com.thecodefather.untigrito.presentation.navigation.ClientRoutes // Importar ClientRoutes
import com.thecodefather.untigrito.presentation.navigation.Routes
import com.thecodefather.untigrito.presentation.screens.client.components.RequestServiceCard
import com.thecodefather.untigrito.presentation.viewmodel.ClientHomeViewModel
import androidx.navigation.compose.rememberNavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenClient(
    navController: NavController,
    onNavigateToAccountDetails: () -> Unit, // Cambiado de onNavigateToRechargeMethods
    viewModel: ClientHomeViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()
    val services by viewModel.services.collectAsState()
    val topProfessionals by viewModel.topProfessionals.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            AppTopBar(navController = navController, userName = user?.name)
            Spacer(modifier = Modifier.height(16.dp))


            // Tarjeta de historial con balance real
            HistoryCard(
                balance = user?.balance ?: 0.0,
                onNavigateToAccountDetails = onNavigateToAccountDetails
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Explora categorías
            CategorySection()

            Spacer(modifier = Modifier.height(24.dp))

            // Tigres mejor calificados
            TopRatedTigersSection(
                professionals = topProfessionals,
                onTigerClick = {
                    // TODO: Eventualmente, pasar el ID del profesional. Por ahora, navega al perfil de ejemplo.
                    navController.navigate(ClientRoutes.PROFESSIONAL_PROFILE)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Publica tu Solicitud
            PublishRequestCard(
                onNavigateToRequestService = {
                    navController.navigate(ClientRoutes.CREATE_REQUEST)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Servicios
            ServicesSection(services = services)

            Spacer(modifier = Modifier.height(16.dp)) // Espacio al final del contenido
        }

        // Loading indicator
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color(0xFFE67822)
            )
        }

        // Error message
        error?.let { errorMessage ->
            Snackbar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                action = {
                    TextButton(onClick = { viewModel.refresh() }) {
                        Text("Reintentar")
                    }
                }
            ) {
                Text(errorMessage)
            }
        }
    }
}

@Composable
fun AppTopBar(navController: NavController, userName: String? = null) {
    HomeHeader(
        userName = userName ?: "Usuario",
        onMessageClick = {
            navController.navigate(ClientRoutes.MESSAGES)
        }
    )
}


@Composable
fun HistoryCard(
    balance: Double,
    onNavigateToAccountDetails: () -> Unit
) {
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
                .clickable(onClick = onNavigateToAccountDetails)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = R.drawable.wallet_2), // Reemplaza con el icono de billetera
                        contentDescription = "Wallet Icon",
                        tint = Color(0xFFE67822),
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = String.format("%,.2f", balance) + " Bs",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
                Icon(
                    painter = painterResource(id = R.drawable.info_circle), // Reemplaza con el icono de exclamación
                    contentDescription = "Info Icon",
                    tint = Color(0xFFE67822),
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Historial",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_circle_down), // Reemplaza con el icono de historial
                    contentDescription = "History Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.arrow_circle_up), // Reemplaza con el icono de flecha
                    contentDescription = "Arrow Icon",
                    tint = Color.Gray,
                    modifier = Modifier.size(30.dp)
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
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                   Icons.Rounded.Assignment,
                    contentDescription = "Categorías",
                    tint = Color(0xFFE67822),
                )
                Spacer(Modifier.width(3.dp))
                Text(
                    text = "Explora categorías",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            Icon(
                imageVector = Icons.Rounded.ArrowForwardIos, // Reemplaza con el icono de flecha
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
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
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
fun TopRatedTigersSection(
    professionals: List<com.thecodefather.untigrito.domain.model.Professional>,
    onTigerClick: () -> Unit
) {
    val testProfessionals = listOf(
        com.thecodefather.untigrito.domain.model.Professional(
            id = "1",
            userId = "Carlos Méndez",
            bio = "Electricista certificado con 10 años de experiencia",
            rating = 4.9,
            totalReviews = 127,
            yearsOfExperience = 10,
            specialties = listOf("Electricidad", "Instalaciones"),
            responseTime = 2,
            completionRate = 98.5,
            hourlyRate = 25.0,
            isVerified = true
        ),
        com.thecodefather.untigrito.domain.model.Professional(
            id = "2",
            userId = "María González",
            bio = "Plomera profesional especializada en sistemas modernos",
            rating = 4.8,
            totalReviews = 89,
            yearsOfExperience = 8,
            specialties = listOf("Plomería", "Reparaciones"),
            responseTime = 3,
            completionRate = 96.0,
            hourlyRate = 22.0,
            isVerified = true
        ),
        com.thecodefather.untigrito.domain.model.Professional(
            id = "3",
            userId = "José Ramírez",
            bio = "Carpintero experto en muebles a medida",
            rating = 5.0,
            totalReviews = 156,
            yearsOfExperience = 15,
            specialties = listOf("Carpintería", "Muebles"),
            responseTime = 4,
            completionRate = 99.2,
            hourlyRate = 30.0,
            isVerified = true
        ),
        com.thecodefather.untigrito.domain.model.Professional(
            id = "4",
            userId = "Ana Rodríguez",
            bio = "Pintora profesional con ojo para el detalle",
            rating = 4.7,
            totalReviews = 73,
            yearsOfExperience = 6,
            specialties = listOf("Pintura", "Decoración"),
            responseTime = 5,
            completionRate = 94.5,
            hourlyRate = 20.0,
            isVerified = true
        ),
        com.thecodefather.untigrito.domain.model.Professional(
            id = "5",
            userId = "Pedro Martínez",
            bio = "Técnico en refrigeración y aires acondicionados",
            rating = 4.9,
            totalReviews = 112,
            yearsOfExperience = 12,
            specialties = listOf("Refrigeración", "Aires acondicionados"),
            responseTime = 2,
            completionRate = 97.8,
            hourlyRate = 28.0,
            isVerified = true
        )
    )
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(R.drawable.medal_star),
                contentDescription = "Categorías",
                tint = Color(0xFFE67822),
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = "Tigres mejor calificados",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (testProfessionals.isEmpty()) {
            Text(
                text = "No hay profesionales disponibles",
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.Gray
            )
        } else {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(testProfessionals.size) { index ->
                    val prof = testProfessionals[index]
                    TopRatedTigerItem(
                        name = prof.userId,
                        rating = prof.rating?.toFloat() ?: 0f,
                        reviews = prof.totalReviews ?: 0,
                        profession = prof.specialties.firstOrNull() ?: "Profesional",
                        location = "Valencia",
                        onClick = onTigerClick
                    )
                }
            }
        }
    }
}

@Composable
fun TopRatedTigerItem(
    name: String,
    rating: Float,
    reviews: Int,
    profession: String,
    location: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .clickable(onClick = onClick),
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
                Text(text = "$profession | $location", style = MaterialTheme.typography.bodySmall, color = Color.Gray
                , overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                    Text(text = "$rating ($reviews)", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                }
            }
        }
    }
}

@Composable
fun PublishRequestCard(onNavigateToRequestService: () -> Unit) {

    RequestServiceCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        onPublishServiceClick = onNavigateToRequestService)
}

@Composable
fun ServicesSection(services: List<com.thecodefather.untigrito.domain.model.ProfessionalService>) {

    // Usar datos de prueba si no hay servicios del ViewModel
//    val displayServices = if (services.isEmpty()) testServices else services
    val displayServices = services

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Icon(
                painter = painterResource(R.drawable.briefcase),
                contentDescription = "Servicios",
                tint = Color(0xFFE67822),
            )
            Spacer(Modifier.width(3.dp))
            Text(
                text = "Servicios",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        
        if (displayServices.isEmpty()) {
            Text(
                text = "No hay servicios disponibles",
                modifier = Modifier.padding(horizontal = 24.dp),
                color = Color.Gray
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                displayServices.take(4).forEach { service ->
                    ServiceItem(
                        title = service.title,
                        description = service.description,
                        provider = service.professionalId,
                        rating = (4.5f + (Math.random() * 0.5f).toFloat()), // Rating aleatorio entre 4.5 y 5.0
                        reviews = (50 + (Math.random() * 100).toInt()),
                        price = "$${String.format("%.2f", service.price)}"
                    )
                }
            }
        }
    }
}

@Composable
fun ServiceItem(title: String, description: String, provider: String, rating: Float, reviews: Int, price: String) {
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
            Text(text = price, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewHomeScreen() {
    val navController = rememberNavController()
    HomeScreenClient(
        navController = navController, 
        onNavigateToAccountDetails = {}
    )
}

@Preview(showBackground = true, name = "Servicios Preview")
@Composable
fun PreviewServicesSection() {
    val testServices = listOf(
        com.thecodefather.untigrito.domain.model.ProfessionalService(
            id = "1",
            professionalId = "Carlos Méndez",
            title = "Instalación Eléctrica Completa",
            description = "Instalación de circuitos eléctricos residenciales con garantía",
            price = 150.0,
            categoryId = "electricidad",
            isActive = true
        ),
        com.thecodefather.untigrito.domain.model.ProfessionalService(
            id = "2",
            professionalId = "María González",
            title = "Reparación de Tuberías",
            description = "Detección y reparación de fugas, cambio de tuberías",
            price = 80.0,
            categoryId = "plomeria",
            isActive = true
        ),
        com.thecodefather.untigrito.domain.model.ProfessionalService(
            id = "3",
            professionalId = "José Ramírez",
            title = "Muebles a Medida",
            description = "Diseño y fabricación de muebles personalizados en madera",
            price = 300.0,
            categoryId = "carpinteria",
            isActive = true
        ),
        com.thecodefather.untigrito.domain.model.ProfessionalService(
            id = "4",
            professionalId = "Ana Rodríguez",
            title = "Pintura de Interiores",
            description = "Pintura profesional de espacios interiores con acabado premium",
            price = 120.0,
            categoryId = "pintura",
            isActive = true
        )
    )

    ServicesSection(services = testServices)
}

@Preview(showBackground = true, name = "Top Tigers Preview")
@Composable
fun PreviewTopRatedTigersSection() {
    val testProfessionals = listOf(
        com.thecodefather.untigrito.domain.model.Professional(
            id = "1",
            userId = "Carlos Méndez",
            bio = "Electricista certificado con 10 años de experiencia",
            rating = 4.9,
            totalReviews = 127,
            yearsOfExperience = 10,
            specialties = listOf("Electricidad", "Instalaciones"),
            responseTime = 2,
            completionRate = 98.5,
            hourlyRate = 25.0,
            isVerified = true
        ),
        com.thecodefather.untigrito.domain.model.Professional(
            id = "2",
            userId = "María González",
            bio = "Plomera profesional especializada en sistemas modernos",
            rating = 4.8,
            totalReviews = 89,
            yearsOfExperience = 8,
            specialties = listOf("Plomería", "Reparaciones"),
            responseTime = 3,
            completionRate = 96.0,
            hourlyRate = 22.0,
            isVerified = true
        ),
        com.thecodefather.untigrito.domain.model.Professional(
            id = "3",
            userId = "José Ramírez",
            bio = "Carpintero experto en muebles a medida",
            rating = 5.0,
            totalReviews = 156,
            yearsOfExperience = 15,
            specialties = listOf("Carpintería", "Muebles"),
            responseTime = 4,
            completionRate = 99.2,
            hourlyRate = 30.0,
            isVerified = true
        )
    )

    TopRatedTigersSection(
        professionals = testProfessionals,
        onTigerClick = {}
    )
}
