package com.example.dino.presentation

data class UiState(
    val gameWorldTicks: Long,
    val score: Int,
    val dino: Dino,
    val obstacles: List<Obstacle>,
    val clouds: List<Cloud>,
    val isPlaying: Boolean
) {
    data class Dino(
        val avatarState: AvatarState,
        val left: Float,
        val top: Float
    )

    data class Obstacle(
        val top: Float,
        val left: Float,
        val type: DessertType
    )

    data class Cloud(
        val top: Float,
        val left: Float,
        val type: CloudType
    )
}
