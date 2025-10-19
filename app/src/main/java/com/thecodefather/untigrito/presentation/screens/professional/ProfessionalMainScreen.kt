package com.thecodefather.untigrito.presentation.screens.professional

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.R
import com.thecodefather.untigrito.presentation.screens.professional.jobs.*
import com.thecodefather.untigrito.presentation.screens.professional.proposals.*
import com.thecodefather.untigrito.presentation.screens.professional.messages.*
import com.thecodefather.untigrito.presentation.screens.professional.services.*
import com.thecodefather.untigrito.presentation.screens.professional.profile.*
import com.thecodefather.untigrito.presentation.navigation.ProfessionalNavigation
import com.thecodefather.untigrito.presentation.viewmodel.ProfessionalViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfessionalMainScreen(
    onNavigateBack: () -> Unit,
    onNavigateToClient: () -> Unit = onNavigateBack,
    onNavigateToLogin: () -> Unit = onNavigateBack
) {
    val viewModel: ProfessionalViewModel = hiltViewModel()
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Estados del ViewModel
    val currentUser by viewModel.currentUser.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val logoutSuccess by viewModel.logoutSuccess.collectAsState()
    
    // Estados locales
    var isClient by remember { mutableStateOf(false) } // Estado para el switch "Soy un cliente"
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // Asegurar que el drawer esté cerrado al inicio
    LaunchedEffect(Unit) {
        drawerState.close()
    }

    val items = listOf(
        ProfessionalNavigationItem(
            route = ProfessionalRoutes.JOBS,
            label = "Trabajos",
            icon = painterResource(R.drawable.search_normal)
        ),
        ProfessionalNavigationItem(
            route = ProfessionalRoutes.PROPOSALS,
            label = "Propuestas",
            icon = painterResource(R.drawable.document)
        ),
        ProfessionalNavigationItem(
            route = ProfessionalRoutes.MESSAGES,
            label = "Mensajes",
            icon = painterResource(R.drawable.sms)
        ),
        ProfessionalNavigationItem(
            route = ProfessionalRoutes.SERVICES,
            label = "Mis Servicios",
            icon = painterResource(R.drawable.message_edit)
        )
    )
    
    // Navegar al cliente cuando el switch esté en true
    LaunchedEffect(isClient) {
        if (isClient) {
            onNavigateToClient() // Navegar al cliente
        }
    }
    
    // Manejar logout exitoso
    LaunchedEffect(logoutSuccess) {
        if (logoutSuccess) {
            // Resetear el estado de logout
            viewModel.resetLogoutSuccess()
            // Navegar al login
            onNavigateToLogin()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { 
            ProfessionalDrawerContent(
                user = currentUser,
                balance = balance,
                onProfileClick = { 
                    scope.launch { 
                        drawerState.close()
                        navController.navigate(ProfessionalRoutes.OWN_PROFILE)
                    }
                },
                onSwitchToClient = { 
                    scope.launch { 
                        drawerState.close()
                    }
                    isClient = true 
                },
                onLogout = { 
                    scope.launch { 
                        drawerState.close()
                    }
                    viewModel.logout() 
                }
            )
        }
    ) {
        Scaffold(
        containerColor = Color(0xFFF5F5F5), // Fondo gris claro como en client
        topBar = {
            if(isMainScreen(currentRoute)) {
                CenterAlignedTopAppBar(
                    title = { Text(items.firstOrNull { currentRoute == it.route }?.label ?: "Untigrito", maxLines = 1 ) },
                    navigationIcon = {
                        IconButton(onClick = { 
                            scope.launch { 
                                drawerState.open() 
                            } 
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color(0xFFF5F5F5), // Fondo gris claro
                        titleContentColor = Color(0xFF212121) // Texto oscuro
                    )
                )
            }
        },
        bottomBar = {
            if (isMainScreen(currentRoute)) {
                ProfessionalBottomNavigation(
                    currentRoute = currentRoute ?: ProfessionalRoutes.JOBS,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    list= items
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = ProfessionalRoutes.JOBS,
            modifier = Modifier
                .then(
                    if(isMainScreen(currentRoute)) {
                        Modifier.padding(paddingValues)
                    } else {
//                        Modifier.consumeWindowInsets(paddingValues)
                        Modifier
                    }
                )
        ) {
            // Subflujo de Trabajos
            composable(ProfessionalRoutes.JOBS) {
                JobsScreen(
                    onJobClick = { jobId ->
                        navController.navigate("${ProfessionalRoutes.JOB_DETAIL}/$jobId")
                    },
                    onNavigateToProposal = { jobId ->
                        navController.navigate("${ProfessionalRoutes.CREATE_PROPOSAL}/$jobId")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.JOB_DETAIL}/{jobId}") { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                JobDetailScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProposal = { jobId ->
                        navController.navigate("${ProfessionalRoutes.CREATE_PROPOSAL}/$jobId")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.CREATE_PROPOSAL}/{jobId}") { backStackEntry ->
                val jobId = backStackEntry.arguments?.getString("jobId") ?: ""
                CreateProposalScreen(
                    jobId = jobId,
                    onNavigateBack = { navController.popBackStack() },
                    onProposalCreated = { 
                        navController.navigate(ProfessionalRoutes.PROPOSALS) {
                            popUpTo(ProfessionalRoutes.JOBS) { inclusive = false }
                        }
                    }
                )
            }

            // Subflujo de Propuestas
            composable(ProfessionalRoutes.PROPOSALS) {
                ProposalsScreen(
                    onProposalClick = { proposalId ->
                        navController.navigate("${ProfessionalRoutes.PROPOSAL_DETAIL}/$proposalId")
                    }
                )
            }
            
            composable("${ProfessionalRoutes.PROPOSAL_DETAIL}/{proposalId}") { backStackEntry ->
                val proposalId = backStackEntry.arguments?.getString("proposalId") ?: ""
                ProposalDetailScreen(
                    proposalId = proposalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Subflujo de Mensajes
            composable(ProfessionalRoutes.MESSAGES) {
                MessagesScreen(
                    onConversationClick = { conversationId ->
                        navController.navigate("${ProfessionalRoutes.CHAT}/$conversationId")
                    },
                    onSupportClick = {
                        navController.navigate(ProfessionalNavigation.SUPPORT_CHAT)
                    }
                )
            }
            
            composable("${ProfessionalRoutes.CHAT}/{conversationId}") { backStackEntry ->
                val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
                ChatScreen(
                    conversationId = conversationId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }

            // Subflujo de Mis Servicios
            composable(ProfessionalRoutes.SERVICES) {
                ServicesScreen(
                    onServiceClick = { serviceId ->
                        navController.navigate("${ProfessionalRoutes.SERVICE_DETAIL}/$serviceId")
                    },
                    onCreateService = {
                        navController.navigate(ProfessionalRoutes.CREATE_SERVICE)
                    },
                    onEditService = { serviceId ->
                        navController.navigate("${ProfessionalRoutes.EDIT_SERVICE}/$serviceId")
                    }
                )
            }
            
            composable(ProfessionalRoutes.CREATE_SERVICE) {
                CreateEditServiceScreen(
                    serviceId = null,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable("${ProfessionalRoutes.EDIT_SERVICE}/{serviceId}") { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                CreateEditServiceScreen(
                    serviceId = serviceId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            
            composable("${ProfessionalRoutes.SERVICE_DETAIL}/{serviceId}") { backStackEntry ->
                val serviceId = backStackEntry.arguments?.getString("serviceId") ?: ""
                ServiceDetailScreen(
                    serviceId = serviceId,
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToProposals = { 
                        navController.navigate(ProfessionalRoutes.PROPOSALS)
                    },
                    onNavigateToEdit = { id ->
                        navController.navigate("${ProfessionalRoutes.EDIT_SERVICE}/$id")
                    }
                )
            }
            
            // Rutas de Perfil
            composable(ProfessionalRoutes.OWN_PROFILE) {
                ProfessionalOwnProfileScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onEditService = { serviceId ->
                        navController.navigate("${ProfessionalRoutes.EDIT_SERVICE}/$serviceId")
                    },
                    onCreateService = { navController.navigate(ProfessionalRoutes.CREATE_SERVICE) },
                    onViewAllServices = { navController.navigate(ProfessionalRoutes.SERVICES) }
                )
            }
            
            composable("${ProfessionalRoutes.PUBLIC_PROFILE}/{professionalId}") { backStackEntry ->
                val professionalId = backStackEntry.arguments?.getString("professionalId") ?: ""
                ProfessionalPublicProfileScreen(
                    professionalId = professionalId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
    }
}

@Composable
fun ProfessionalBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    list: List<ProfessionalNavigationItem>
) {
    NavigationBar(
        containerColor = Color.White // Fondo blanco como en client
    ) {
        list.forEach { item ->
            NavigationBarItem(
                icon = { Icon(painter = item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { onNavigate(item.route) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color.White,
                    selectedTextColor = Color.White,
                    unselectedIconColor = Color(0xFF616161), // Gris cuando inactivo
                    unselectedTextColor = Color(0xFF616161), // Gris cuando inactivo
                    indicatorColor = Color(0xFFE67822) // Naranja para indicador
                )
            )
        }
    }
}

data class ProfessionalNavigationItem(
    val route: String,
    val label: String,
    val icon: Painter
)

object ProfessionalRoutes {
    const val JOBS = "professional/jobs"
    const val JOB_DETAIL = "professional/jobs/detail"
    const val CREATE_PROPOSAL = "professional/proposals/create"
    const val PROPOSALS = "professional/proposals"
    const val PROPOSAL_DETAIL = "professional/proposals/detail"
    const val MESSAGES = "professional/messages"
    const val CHAT = "professional/chat"
    const val SERVICES = "professional/services"
    const val SERVICE_DETAIL = "professional/services/detail"
    const val CREATE_SERVICE = "professional/services/create"
    const val EDIT_SERVICE = "professional/services/edit"
    const val OWN_PROFILE = "professional/profile/own"
    const val PUBLIC_PROFILE = "professional/profile/public"
}

private fun isMainScreen(route: String?): Boolean {
    return route in listOf(
        ProfessionalRoutes.JOBS,
        ProfessionalRoutes.PROPOSALS,
        ProfessionalRoutes.MESSAGES,
        ProfessionalRoutes.SERVICES
    )
}

@Composable
fun ProfessionalDrawerContent(
    user: com.thecodefather.untigrito.domain.model.User?,
    balance: Double,
    onProfileClick: () -> Unit,
    onSwitchToClient: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Header con información del usuario
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Foto de perfil (placeholder)
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE67822)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = user?.name ?: "Usuario",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                
                Text(
                    text = user?.email ?: "email@ejemplo.com",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        // Saldo
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E8)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "Saldo",
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(24.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column {
                    Text(
                        text = "Saldo Disponible",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "Bs. ${String.format("%.2f", balance)}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
        
        // Opciones
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column {
                // Ver mi Perfil
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onProfileClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Perfil",
                        tint = Color(0xFFE67822),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Ver mi Perfil",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
                
                Divider(color = Color(0xFFE0E0E0), thickness = 1.dp)
                
                // Soy un Cliente
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onSwitchToClient() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SwitchAccount,
                        contentDescription = "Cambiar a Cliente",
                        tint = Color(0xFFE67822),
                        modifier = Modifier.size(24.dp)
                    )
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Text(
                        text = "Soy un Cliente",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Botón de Cerrar Sesión
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE67822)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ExitToApp,
                contentDescription = "Cerrar Sesión",
                tint = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                "Cerrar Sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
