package com.idat.presentation.ayuda

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AyudaScreen(
    navController: NavHostController,
    viewModel: AyudaViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isDarkTheme by viewModel.isDarkTheme.collectAsState()
    val expandedFaqId by viewModel.expandedFaqId.collectAsState()
    
    val backgroundColor = if (isDarkTheme) Color(0xFF1C1C1C) else Color(0xFFF5F5F5)
    val cardColor = if (isDarkTheme) Color(0xFF2C2C2C) else Color.White
    val textColor = if (isDarkTheme) Color(0xFFFAFAFA) else Color(0xFF222222)
    val textSecondaryColor = if (isDarkTheme) Color(0xFFCCCCCC) else Color(0xFF666666)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Centro de Ayuda",
                        color = textColor,
                        fontWeight = FontWeight.Bold
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { 
                        navController.navigate("catalogo?openDrawer=true") {
                            popUpTo("catalogo") { inclusive = true }
                        }
                    }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = textColor
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = cardColor
                )
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Banner de bienvenida
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE50010)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Help,
                        contentDescription = "Ayuda",
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "¿En qué podemos ayudarte?",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Encuentra respuestas rápidas a tus preguntas",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }

            // Sección de Contacto Rápido
            Text(
                text = "Contacto Rápido",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(top = 8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ContactCard(
                    icon = Icons.Default.Email,
                    title = "Email",
                    subtitle = "yeffercastillovega24@gmail.com",
                    backgroundColor = cardColor,
                    textColor = textColor,
                    textSecondaryColor = textSecondaryColor,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("mailto:yeffercastillovega24@gmail.com")
                            putExtra(Intent.EXTRA_SUBJECT, "Consulta desde la app")
                        }
                        context.startActivity(intent)
                    }
                )
                ContactCard(
                    icon = Icons.Default.Phone,
                    title = "Teléfono",
                    subtitle = "947837554",
                    backgroundColor = cardColor,
                    textColor = textColor,
                    textSecondaryColor = textSecondaryColor,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        val intent = Intent(Intent.ACTION_DIAL).apply {
                            data = Uri.parse("tel:947837554")
                        }
                        context.startActivity(intent)
                    }
                )
            }

            // Preguntas Frecuentes
            Text(
                text = "Preguntas Frecuentes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(top = 8.dp)
            )

            FaqItem(
                id = 1,
                question = "¿Cómo puedo realizar un pedido?",
                answer = "Para realizar un pedido, navega por nuestro catálogo de productos, selecciona los artículos que desees, agrégalos al carrito y sigue el proceso de compra. Necesitarás una cuenta registrada para completar la compra.",
                isExpanded = expandedFaqId == 1,
                onToggle = { viewModel.toggleFaq(1) },
                backgroundColor = cardColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )

            FaqItem(
                id = 2,
                question = "¿Cuáles son los métodos de pago disponibles?",
                answer = "Aceptamos tarjetas de crédito y débito (Visa, Mastercard, American Express), transferencias bancarias y pago contra entrega en zonas seleccionadas.",
                isExpanded = expandedFaqId == 2,
                onToggle = { viewModel.toggleFaq(2) },
                backgroundColor = cardColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )

            FaqItem(
                id = 3,
                question = "¿Cuánto tiempo tarda el envío?",
                answer = "Los envíos estándar tardan entre 3-5 días hábiles. Los envíos express tardan 1-2 días hábiles. Recibirás un número de seguimiento una vez que tu pedido sea enviado.",
                isExpanded = expandedFaqId == 3,
                onToggle = { viewModel.toggleFaq(3) },
                backgroundColor = cardColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )

            FaqItem(
                id = 4,
                question = "¿Puedo devolver un producto?",
                answer = "Sí, aceptamos devoluciones dentro de los 30 días posteriores a la compra. El producto debe estar en su estado original con etiquetas. Los costos de envío de devolución corren por cuenta del cliente.",
                isExpanded = expandedFaqId == 4,
                onToggle = { viewModel.toggleFaq(4) },
                backgroundColor = cardColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )

            FaqItem(
                id = 5,
                question = "¿Cómo puedo rastrear mi pedido?",
                answer = "Una vez que tu pedido sea enviado, recibirás un correo electrónico con el número de seguimiento. También puedes ver el estado de tu pedido en la sección 'Mis Pedidos' de tu cuenta.",
                isExpanded = expandedFaqId == 5,
                onToggle = { viewModel.toggleFaq(5) },
                backgroundColor = cardColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )

            FaqItem(
                id = 6,
                question = "¿Cómo cambio mi contraseña?",
                answer = "Ve a Configuración en el menú del perfil, luego selecciona 'Cambiar Contraseña'. Deberás ingresar tu contraseña actual y la nueva contraseña que deseas establecer.",
                isExpanded = expandedFaqId == 6,
                onToggle = { viewModel.toggleFaq(6) },
                backgroundColor = cardColor,
                textColor = textColor,
                textSecondaryColor = textSecondaryColor
            )

            // Sección de Ayuda Adicional
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "¿No encontraste lo que buscabas?",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    
                    Text(
                        text = "Nuestro equipo de soporte está disponible de lunes a viernes de 9:00 AM a 6:00 PM para ayudarte con cualquier consulta.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = textSecondaryColor
                    )

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW).apply {
                                data = Uri.parse("https://wa.me/51947837554?text=Hola,%20necesito%20ayuda%20con%20la%20aplicación")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF25D366)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Chat,
                            contentDescription = "WhatsApp",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Contactar por WhatsApp")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_SENDTO).apply {
                                data = Uri.parse("smsto:947837554")
                                putExtra("sms_body", "Hola, necesito ayuda con la aplicación")
                            }
                            context.startActivity(intent)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = textColor
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Sms,
                            contentDescription = "SMS",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Enviar SMS")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun ContactCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    backgroundColor: Color,
    textColor: Color,
    textSecondaryColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                icon,
                contentDescription = title,
                modifier = Modifier.size(32.dp),
                tint = Color(0xFFE50010)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = textColor
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = textSecondaryColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun FaqItem(
    id: Int,
    question: String,
    answer: String,
    isExpanded: Boolean,
    onToggle: () -> Unit,
    backgroundColor: Color,
    textColor: Color,
    textSecondaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onToggle() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = question,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Contraer" else "Expandir",
                    tint = Color(0xFFE50010)
                )
            }
            
            if (isExpanded) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = answer,
                    style = MaterialTheme.typography.bodyMedium,
                    color = textSecondaryColor,
                    lineHeight = 20.sp
                )
            }
        }
    }
}
