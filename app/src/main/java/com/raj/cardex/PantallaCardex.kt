package com.raj.cardex.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.raj.cardex.data.CocheAvistado
import com.raj.cardex.viewmodel.EstadoUiCardex
import com.raj.cardex.viewmodel.CarViewModel
import androidx.compose.ui.platform.LocalContext
import com.raj.cardex.viewmodel.AyudanteFactoriaViewModel

@Composable
fun PantallaCardex(
    alHacerClicCoche: (CocheAvistado) -> Unit
) {
    val contexto = LocalContext.current
    val viewModel: CarViewModel = viewModel(
        factory = AyudanteFactoriaViewModel.provideCarViewModelFactory(contexto)
    )
    val estadoUi by viewModel.estadoUi.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "📇 Mi Cardex",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )
        when (val state = estadoUi) {
            is EstadoUiCardex.Cargando -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is EstadoUiCardex.Error -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = state.mensaje, color = Color.Red)
                    Button(onClick = { viewModel.obtenerCoches() }) { Text("Reintentar") }
                }
            }
            is EstadoUiCardex.Exito -> {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(8.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(state.coches) { coche ->
                        ItemCoche(coche = coche, onClick = { alHacerClicCoche(coche) })
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemCoche(coche: CocheAvistado, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            if (coche.rutaImagen != null) {
                AsyncImage(
                    model = coche.rutaImagen,
                    contentDescription = coche.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp)),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🚗", fontSize = 40.sp)
                }
            }
            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = coche.marca.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = coche.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Por: ${coche.usuario}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}
