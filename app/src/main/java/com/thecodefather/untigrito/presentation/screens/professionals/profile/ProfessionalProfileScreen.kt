package com.thecodefather.untigrito.presentation.screens.professionals.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.R

// --- Data Classes for the Professional Profile ---

data class Professional(
    val name: String,
    val specialty: String,
    val isVerified: Boolean,
    val about: String,
    val services: List<String>,
    val reviews: List<Review>,
    val portfolio: List<Int> // Using drawable resource IDs
)

data class Review(
    val author: String,
    val rating: Int,
    val comment: String
)

// --- Dummy Data ---

val dummyProfessional = Professional(
    name = "Andrés Rodríguez",
    specialty = "Plomero Verificado",
    isVerified = true,
    about = "Soy un plomero certificado con más de 10 años de experiencia en el área. Me especializo en reparaciones de tuberías, instalación de sanitarios y sistemas de agua caliente. Mi objetivo es ofrecer un servicio de calidad, rápido y a un precio justo.",
    services = listOf(
        "Reparación de filtraciones y tuberías.",
        "Instalación de griferías y sanitarios.",
        "Desatascos de tuberías con herramientas especializadas."
    ),
    reviews = listOf(
        Review("Ana G.", 5, "¡Excelente trabajo! Andrés llegó a tiempo y resolvió mi problema de plomería muy rápido. Muy recomendado."),
        Review("Ana G.", 5, "¡Excelente trabajo! Andrés llegó a tiempo y resolvió mi problema de plomería muy rápido. Muy recomendado.")
    ),
    portfolio = listOf(
        R.drawable.briefcase, // Reemplaza con tus propios recursos drawable
        R.drawable.profile,
        R.drawable.archive
    )
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalProfileScreen(
    onBackClick: () -> Unit = {},
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { /* No title */ },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ProfileHeader(professional = dummyProfessional)
            Spacer(modifier = Modifier.height(24.dp))
            AboutSection(about = dummyProfessional.about)
            Spacer(modifier = Modifier.height(16.dp))
            ServicesSection(services = dummyProfessional.services)
            Spacer(modifier = Modifier.height(16.dp))
            ReviewsSection(reviews = dummyProfessional.reviews)
            Spacer(modifier = Modifier.height(16.dp))
            PortfolioSection(portfolio = dummyProfessional.portfolio)
        }
    }
}

@Composable
fun ProfileHeader(professional: Professional) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = R.drawable.profile), // Reemplaza con tu avatar
            contentDescription = "Avatar de ${professional.name}",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = professional.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = professional.specialty, style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
            if (professional.isVerified) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Verificado",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* TODO: Contact logic */ },
            modifier = Modifier.fillMaxWidth(0.7f)
        ) {
            Text("Contactar")
        }
    }
}

@Composable
fun AboutSection(about: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Sobre Andrés", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(about, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ServicesSection(services: List<String>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Servicios Destacados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            services.forEach { service ->
                Text("• $service", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(bottom = 4.dp))
            }
        }
    }
}

@Composable
fun ReviewsSection(reviews: List<Review>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Reseñas y Valoraciones", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("${reviews.size} reseñas", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(16.dp))
            reviews.forEach { review ->
                ReviewItem(review)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            repeat(review.rating) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFFFC107), // Amber color for stars
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(review.author, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(review.comment, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun PortfolioSection(portfolio: List<Int>) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Portafolio", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(portfolio) { imageRes ->
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Portfolio image",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProfessionalProfileScreenPreview() {
    MaterialTheme {
        ProfessionalProfileScreen()
    }
}
