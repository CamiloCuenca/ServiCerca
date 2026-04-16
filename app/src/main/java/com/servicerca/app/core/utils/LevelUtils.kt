package com.servicerca.app.core.utils

object LevelUtils {
    private val levels = listOf(500, 1300, 2500, 4300, 7000)
    private val levelNames = listOf("Principiante", "Colaborador", "Confiable", "Profesional local", "Experto local")

    fun getLevelName(totalXp: Int): String {
        var currentLevel = 1
        
        for (i in levels.indices) {
            if (totalXp >= levels[i]) {
                currentLevel = i + 2
            } else {
                break
            }
        }

        // Aseguramos que el nivel esté entre 1 y 5
        val clampedLevel = currentLevel.coerceIn(1, 5)
        return levelNames[clampedLevel - 1]
    }
}
