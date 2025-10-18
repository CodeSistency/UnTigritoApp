package com.thecodefather.untigrito.presentation.screens.professionals.editprofile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.thecodefather.untigrito.R
import com.thecodefather.untigrito.presentation.screens.professionals.profile.dummyProfessional

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfessionalProfileScreen(
    onBackClick: () -> Unit = {},
    onSaveChanges: (Any) -> Unit = {} // Parameter would be a view model state object
) {
    var name by rememberSaveable { mutableStateOf(dummyProfessional.name) }
    var phone by rememberSaveable { mutableStateOf("+57 310 123 4567") } // Dummy phone
    var email by rememberSaveable { mutableStateOf("luisjose@gmail.com") } // Dummy email
    var about by rememberSaveable { mutableStateOf(dummyProfessional.about) }
    var services by remember { mutableStateOf(dummyProfessional.services.toMutableList()) }
    var portfolio by remember { mutableStateOf(dummyProfessional.portfolio.toMutableList()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar perfil") },
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
            ProfileImageEditor()
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Teléfono") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo Electrónico") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = about,
                onValueChange = { about = it },
                label = { Text("Sobre mí") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            ServicesEditor(
                services = services,
                onAddService = { services.add(it) },
                onRemoveService = { services.remove(it) }
            )
            Spacer(modifier = Modifier.height(24.dp))
            PortfolioEditor(
                portfolio = portfolio,
                onAddImage = { /* TODO */ },
                onRemoveImage = { portfolio.remove(it) }
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = { /* onSaveChanges(/* updated data */) */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Guardar cambios")
            }
        }
    }
}

@Composable
fun ProfileImageEditor() {
    Box(contentAlignment = Alignment.Center) {
        Image(
            painter = painterResource(id = R.drawable.profile),
            contentDescription = "Avatar",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(4.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primary)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.AddAPhoto,
                contentDescription = "Cambiar foto",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun ServicesEditor(
    services: List<String>,
    onAddService: (String) -> Unit,
    onRemoveService: (String) -> Unit
) {
    var newService by rememberSaveable { mutableStateOf("") }
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Servicios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        services.forEach { service ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(service, modifier = Modifier.weight(1f))
                IconButton(onClick = { onRemoveService(service) }) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar servicio")
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newService,
                onValueChange = { newService = it },
                label = { Text("Nuevo servicio") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newService.isNotBlank()) {
                    onAddService(newService)
                    newService = ""
                }
            }) {
                Text("Añadir")
            }
        }
    }
}


@Composable
fun PortfolioEditor(
    portfolio: List<Int>,
    onAddImage: () -> Unit,
    onRemoveImage: (Int) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Portafolio", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item {
                AddPortfolioItem(onClick = onAddImage)
            }
            items(portfolio) { imageRes ->
                Box {
                    Image(
                        painter = painterResource(id = imageRes),
                        contentDescription = "Portfolio image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                    IconButton(
                        onClick = { onRemoveImage(imageRes) },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            .size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Eliminar imagen",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AddPortfolioItem(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(RoundedCornerShape(8.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = "Añadir foto")
            Spacer(modifier = Modifier.height(4.dp))
            Text("Añadir", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EditProfessionalProfileScreenPreview() {
    MaterialTheme {
        EditProfessionalProfileScreen()
    }
}
