package com.example.composition.domain.useCases

import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.repository.GameRepository

class GameGetSettingsUseCase(
    private val repository:GameRepository
) {
    operator fun invoke(level:Level):GameSettings{
       return repository.getGameSettings(level = level)
    }
}

