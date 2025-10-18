package com.thecodefather.untigrito.vibecoding3.professional.navigation

/**
 * Rutas principales del módulo profesional
 */
object ProfessionalRoutes {
    // Home y Navegación Principal
    const val PROFESSIONAL_HOME = "professional_home"
    const val PROFESSIONAL_BOTTOM_NAV = "professional_bottom_nav"
    
    // Subflujo Trabajos
    const val JOBS_LIST = "jobs_list"
    const val JOB_DETAIL = "job_detail/{jobId}"
    const val JOB_DETAIL_ROUTE = "job_detail"
    const val CREATE_PROPOSAL = "create_proposal/{jobId}"
    const val CREATE_PROPOSAL_ROUTE = "create_proposal"
    
    // Subflujo Propuestas
    const val PROPOSALS_LIST = "proposals_list"
    const val PROPOSAL_DETAIL = "proposal_detail/{proposalId}"
    const val PROPOSAL_DETAIL_ROUTE = "proposal_detail"
    
    // Subflujo Mensajes
    const val MESSAGES_INBOX = "messages_inbox"
    const val CHAT_SCREEN = "chat_screen/{conversationId}"
    const val CHAT_SCREEN_ROUTE = "chat_screen"
    const val SUPPORT_CHAT = "support_chat"
    
    // Subflujo Mis Servicios
    const val MY_SERVICES_LIST = "my_services_list"
    const val SERVICE_DETAIL = "service_detail/{serviceId}"
    const val SERVICE_DETAIL_ROUTE = "service_detail"
    const val CREATE_SERVICE = "create_service"
    const val EDIT_SERVICE = "edit_service/{serviceId}"
    const val EDIT_SERVICE_ROUTE = "edit_service"
    
    // Perfil y Otros
    const val PROFESSIONAL_PROFILE = "professional_profile"
    const val PROFESSIONAL_EARNINGS = "professional_earnings"
    const val PROFESSIONAL_RATINGS = "professional_ratings"
    const val SETTINGS = "settings"
}

/**
 * Argumentos para navegación
 */
object ProfessionalNavArgs {
    const val JOB_ID = "jobId"
    const val PROPOSAL_ID = "proposalId"
    const val SERVICE_ID = "serviceId"
    const val CONVERSATION_ID = "conversationId"
}

/**
 * Estados de navegación del Bottom Navigation
 */
enum class ProfessionalNavTab {
    JOBS,           // Trabajos
    PROPOSALS,      // Propuestas
    MESSAGES,       // Mensajes
    SERVICES,       // Mis Servicios
    PROFILE         // Perfil
}
