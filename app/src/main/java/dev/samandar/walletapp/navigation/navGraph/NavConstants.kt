package dev.samandar.walletapp.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween

const val TRANSITION_DURATION = 360
const val MODAL_TRANSITION_DURATION = 400

val StandardEasing = CubicBezierEasing(0.2f, 0.0f, 0.0f, 1.0f)

val ZoomInForward: EnterTransition = scaleIn(
    initialScale = 0.9f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

val ZoomOutForward: ExitTransition = scaleOut(
    targetScale = 0.9f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeOut(animationSpec = tween(150))

val ZoomInBackward: EnterTransition = scaleIn(
    initialScale = 1.05f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeIn(animationSpec = tween(TRANSITION_DURATION))

val ZoomOutBackward: ExitTransition = scaleOut(
    targetScale = 1.05f,
    animationSpec = tween(TRANSITION_DURATION, easing = StandardEasing)
) + fadeOut(animationSpec = tween(150))

val ModalEnterTransition = slideInVertically(
    initialOffsetY = { it },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = StandardEasing)
)

val ModalExitTransition = slideOutVertically(
    targetOffsetY = { it },
    animationSpec = tween(MODAL_TRANSITION_DURATION, easing = StandardEasing)
)