package com.thecodefather.untigrito.vibecoding3.professional.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.thecodefather.untigrito.vibecoding3.professional.domain.model.JobFilter

/**
 * Componente de tabs para filtrar trabajos
 */
@Composable
fun JobFilterTabs(
    selectedFilter: JobFilter,
    onFilterSelected: (JobFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val filters = listOf(
        JobFilter.RECENT to "Recientes",
        JobFilter.RECOMMENDED to "Recomendados",
        JobFilter.FAVORITES to "Favoritos"
    )
    
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(filters) { (filter, label) ->
            FilterTab(
                label = label,
                isSelected = selectedFilter == filter,
                onClick = { onFilterSelected(filter) }
            )
        }
    }
}

/**
 * Una sola pestaña de filtro
 */
@Composable
fun FilterTab(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                color = if (isSelected)
                    Color(0xFF1976D2)
                else
                    Color(0xFFE0E0E0)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected)
                Color.White
            else
                Color(0xFF666666)
        )
    }
}

/**
 * Componente avanzado de filtros
 */
@Composable
fun AdvancedFilters(
    selectedCategory: String?,
    onCategorySelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    val categories = listOf(
        "Plomería",
        "Electricidad",
        "Carpintería",
        "Pintura",
        "Limpieza",
        "Reparación"
    )
    
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Text(
            text = "Categorías",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                CategoryChip(
                    label = category,
                    isSelected = selectedCategory == category,
                    onClick = { 
                        onCategorySelected(
                            if (selectedCategory == category) null else category
                        )
                    }
                )
            }
        }
    }
}

/**
 * Chip individual para categorías
 */
@Composable
fun CategoryChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(
                color = if (isSelected)
                    Color(0xFF4CAF50)
                else
                    Color(0xFFF0F0F0)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected)
                Color.White
            else
                Color(0xFF555555)
        )
    }
}
