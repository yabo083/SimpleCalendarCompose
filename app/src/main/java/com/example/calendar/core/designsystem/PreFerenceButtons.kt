package com.example.calendar.core.designsystem


import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun PreferenceButtons(
    onLike: () -> Unit,
    onDislike: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isLiked by remember { mutableStateOf(false) }
    var isDisliked by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val likeScale = remember { Animatable(1f) }
    val dislikeOffset = remember { Animatable(0f) }

    val likeColor by animateColorAsState(
        targetValue = if (isLiked) Color.Gray else Color.White,
        label = "likeColor"
    )

    val dislikeColor by animateColorAsState(
        targetValue = if (isDisliked) Color.Gray else Color.White,
        label = "dislikeColor"
    )

    Column(
        modifier = modifier
            .width(46.dp)
            .clip(RoundedCornerShape(topStart = 30.dp, bottomStart = 30.dp))
            .background(Color.Black.copy(alpha = 0.34f))
            .padding(vertical = 6.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            onClick = {
                isLiked = true
                onLike()
                scope.launch {
                    likeScale.snapTo(1f)
                    likeScale.animateTo(
                        targetValue = 1f,
                        animationSpec = keyframes {
                            durationMillis = 300
                            1.3f at 150
                            1.0f at 300
                        }
                    )
                    delay(250)
                    isLiked = false
                }
            },
            modifier = Modifier
                .size(34.dp)
                .graphicsLayer(scaleX = likeScale.value, scaleY = likeScale.value)
        ) {
            Icon(
                imageVector = Icons.Outlined.FavoriteBorder,
                contentDescription = "喜欢当前壁纸",
                tint = likeColor
            )
        }
        IconButton(
            onClick = {
                isDisliked = true
                onDislike()
                scope.launch {
                    dislikeOffset.snapTo(0f)
                    dislikeOffset.animateTo(
                        targetValue = 0f,
                        animationSpec = keyframes {
                            durationMillis = 200
                            -4f at 50
                            4f at 100
                            -4f at 150
                            0f at 200
                        }
                    )
                    delay(250)
                    isDisliked = false
                }
            },
            modifier = Modifier
                .size(34.dp)
                .graphicsLayer(translationX = dislikeOffset.value)
        ) {
            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = "不喜欢当前壁纸",
                tint = dislikeColor
            )
        }
    }
}
