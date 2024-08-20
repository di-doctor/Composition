package com.example.composition.presentation

import android.app.Application
import android.content.Context
import android.os.CountDownTimer
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.composition.R
import com.example.composition.data.GameRepositoryImpl
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.GameSettings
import com.example.composition.domain.entity.Level
import com.example.composition.domain.entity.Question
import com.example.composition.domain.useCases.GameGetSettingsUseCase
import com.example.composition.domain.useCases.GenerateQuestionUseCase


class GameViewModel(
    private val application: Application,
    private val level: Level
) : ViewModel() {
    private val repository = GameRepositoryImpl
    private val gameGetSettingUseCase = GameGetSettingsUseCase(repository)
    private val generateQuestionUseCase = GenerateQuestionUseCase(repository)
    private lateinit var gameSetting: GameSettings

    private var timer: CountDownTimer? = null

    private val _enoughCountOfRightAnswers: MutableLiveData<Boolean> = MutableLiveData()
    val enoughCountOfRightAnswers: LiveData<Boolean>
        get() = _enoughCountOfRightAnswers

    private val _enoughPercentOfRightAnswers: MutableLiveData<Boolean> = MutableLiveData()
    val enoughPercentOfRightAnswers: LiveData<Boolean>
        get() = _enoughPercentOfRightAnswers

    private val _percentOfRightAnswers: MutableLiveData<Int> = MutableLiveData()
    val percentOfRightAnswers: LiveData<Int>
        get() = _percentOfRightAnswers

    private val _progressAnswers: MutableLiveData<String> = MutableLiveData()
    val progressAnswers: LiveData<String>
        get() = _progressAnswers

    private val _formattedTime: MutableLiveData<String> = MutableLiveData()
    val formattedTime: LiveData<String>
        get() = _formattedTime

    private val _question: MutableLiveData<Question> = MutableLiveData()
    val question: LiveData<Question>
        get() = _question

    private val _minPercent: MutableLiveData<Int> = MutableLiveData()
    val minPercent: LiveData<Int>
        get() = _minPercent

    private val _gameResult: MutableLiveData<GameResult> = MutableLiveData()
    val gameResult: LiveData<GameResult>
        get() = _gameResult

    private var countOfRightAnswers: Int = 0
    private var countOfQuestion = 0

    init {
        startGame()
    }

    private fun startGame() {
        getSetting()
        startTimer()
        generateQuestion()
        updateProgress()
    }

    fun chooseAnswer(number: Int) {
        val rightAnswer = _question.value?.rightAnswer
        if (number == rightAnswer) {
            countOfRightAnswers++
        }
        countOfQuestion++
        updateProgress()
        generateQuestion()
    }

    private fun getSetting() {

        this.gameSetting = gameGetSettingUseCase(level)
        _minPercent.value = gameSetting.minPercentOfRightAnswers
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            gameSetting.gameTimeInSeconds * MILLIS_IN_SECONDS,
            MILLIS_IN_SECONDS
        ) {
            override fun onTick(millisUntilFinished: Long) {
                _formattedTime.value = formatTime(millisUntilFinished)
            }

            override fun onFinish() {
                finishGame()
            }
        }
        timer?.start()
    }

    private fun formatTime(millisUntilFinished: Long): String {
        val second = millisUntilFinished / MILLIS_IN_SECONDS
        val minute = second / SECONDS_IN_MINUTE
        val secondLeft = second - minute * SECONDS_IN_MINUTE
        return String.format(format = "%02d:%02d", minute, secondLeft)
    }

    private fun generateQuestion() {
        _question.value = generateQuestionUseCase(gameSetting.maxSumValue)
    }

    private fun updateProgress() {
        val percent = (countOfRightAnswers / countOfQuestion.toDouble() * 100).toInt()
        _percentOfRightAnswers.value = percent
        _progressAnswers.value = String.format(
            application.resources.getString(R.string.progress_answers),
            countOfRightAnswers,
            gameSetting.minCountOfRightAnswers
        )
        _enoughCountOfRightAnswers.value = countOfRightAnswers >= gameSetting.minCountOfRightAnswers
        _enoughPercentOfRightAnswers.value = percent >= gameSetting.minPercentOfRightAnswers
    }

    private fun finishGame() {
        _gameResult.value = GameResult(
            enoughCountOfRightAnswers.value == true
                    && enoughPercentOfRightAnswers.value == true,
            countOfRightAnswers,
            countOfQuestion,
            gameSetting
        )
    }

    override fun onCleared() {
        super.onCleared()
        timer?.cancel()
    }

    companion object {
        const val MILLIS_IN_SECONDS = 1000L
        const val SECONDS_IN_MINUTE = 60L
    }
}
