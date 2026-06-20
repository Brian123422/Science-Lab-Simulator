package com.briantech.sciencelabsimulator.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.briantech.sciencelabsimulator.data.model.Container
import com.briantech.sciencelabsimulator.data.model.ContainerType
import com.briantech.sciencelabsimulator.data.model.getContainerProperties

@Composable
fun ContainerVisualization(
    container: Container,
    isSelected: Boolean = false,
    isPouringFrom: Boolean = false,
    isPouringTo: Boolean = false,
    onClick: () -> Unit,
    onLongPress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val props = getContainerProperties(container.type)
    val borderColor = when {
        isPouringFrom -> Color(0xFFFFD700) // Gold
        isPouringTo -> Color(0xFF00FF00) // Green
        isSelected -> Color(0xFF6750A4) // Primary
        else -> Color.Gray
    }
    val borderWidth = if (isSelected || isPouringFrom || isPouringTo) 3.dp else 1.dp

    Box(
        modifier = modifier
            .width(props.width.dp)
            .height(props.height.dp)
            .clickable(onClick = onClick)
            .shadow(elevation = if (isSelected) 8.dp else 2.dp)
            .border(borderWidth, borderColor, getContainerShape(container.type))
            .background(Color.White, getContainerShape(container.type)),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Render liquids
        if (container.currentVolume > 0) {
            LiquidLayer(container, props)
        }

        // Container outline details
        ContainerOutline(container.type)

        // Label
        Text(
            text = props.displayName,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 4.dp)
        )

        // Volume indicator
        Text(
            text = "${container.currentVolume.toInt()}/${container.capacity.toInt()} ml",
            fontSize = 6.sp,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
    }
}

@Composable
fun LiquidLayer(
    container: Container,
    props: com.briantech.sciencelabsimulator.data.model.ContainerProperties
) {
    val percentFilled = container.currentVolume / container.capacity
    val liquidHeight = props.height * percentFilled

    Box(
        modifier = Modifier
            .width(props.width.dp)
            .height(liquidHeight.dp)
            .background(
                if (container.isReacting) {
                    Color(0xFFFFA500).copy(alpha = 0.7f) // Orange for reaction
                } else {
                    container.liquids.firstOrNull()?.color?.copy(alpha = 0.7f) ?: Color.Transparent
                }
            )
    ) {
        // Bubbles during reaction
        if (container.isReacting) {
            repeat(5) {
                Bubble(
                    modifier = Modifier
                        .size((2..6).random().dp)
                        .offset(x = (0..props.width.toInt()).random().dp, y = (0..liquidHeight.toInt()).random().dp)
                )
            }
        }
    }
}

@Composable
fun Bubble(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.5f))
    )
}

@Composable
fun ContainerOutline(
    type: ContainerType
) {
    when (type) {
        ContainerType.TEST_TUBE_SMALL,
        ContainerType.TEST_TUBE_MEDIUM,
        ContainerType.TEST_TUBE_LARGE -> TestTubeOutline()
        ContainerType.BEAKER_SMALL,
        ContainerType.BEAKER_MEDIUM,
        ContainerType.BEAKER_LARGE -> BeakerOutline()
        ContainerType.FLASK_SMALL,
        ContainerType.FLASK_MEDIUM,
        ContainerType.FLASK_LARGE -> FlaskOutline()
        ContainerType.GRADUATED_CYLINDER_SMALL,
        ContainerType.GRADUATED_CYLINDER_MEDIUM,
        ContainerType.GRADUATED_CYLINDER_LARGE -> GraduatedCylinderOutline()
        ContainerType.PETRI_DISH_SMALL,
        ContainerType.PETRI_DISH_MEDIUM,
        ContainerType.PETRI_DISH_LARGE -> PetriDishOutline()
    }
}

@Composable
fun TestTubeOutline() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .height(12.dp)
                .background(Color.Black)
        )
    }
}

@Composable
fun BeakerOutline() {
    // Simple beaker shape
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    )
}

@Composable
fun FlaskOutline() {
    // Flask shape with narrow top
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .height(8.dp)
                .background(Color.Black)
        )
    }
}

@Composable
fun GraduatedCylinderOutline() {
    // Graduation marks
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 2.dp)
    ) {
        repeat(5) {
            Box(
                modifier = Modifier
                    .width(12.dp)
                    .height(0.5.dp)
                    .background(Color.Black)
                    .padding(vertical = 6.dp)
            )
        }
    }
}

@Composable
fun PetriDishOutline() {
    // Petri dish is flat
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(2.dp)
    )
}

fun getContainerShape(type: ContainerType) = when (type) {
    ContainerType.TEST_TUBE_SMALL,
    ContainerType.TEST_TUBE_MEDIUM,
    ContainerType.TEST_TUBE_LARGE -> RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
    ContainerType.BEAKER_SMALL,
    ContainerType.BEAKER_MEDIUM,
    ContainerType.BEAKER_LARGE -> RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp, bottomStart = 4.dp, bottomEnd = 4.dp)
    ContainerType.FLASK_SMALL,
    ContainerType.FLASK_MEDIUM,
    ContainerType.FLASK_LARGE -> RoundedCornerShape(8.dp)
    ContainerType.GRADUATED_CYLINDER_SMALL,
    ContainerType.GRADUATED_CYLINDER_MEDIUM,
    ContainerType.GRADUATED_CYLINDER_LARGE -> RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp, bottomStart = 8.dp, bottomEnd = 8.dp)
    ContainerType.PETRI_DISH_SMALL,
    ContainerType.PETRI_DISH_MEDIUM,
    ContainerType.PETRI_DISH_LARGE -> RoundedCornerShape(50)
}
