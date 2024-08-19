package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import com.example.composition.R
import com.example.composition.databinding.FragmentGameFinishedBinding
import com.example.composition.domain.entity.GameResult
import java.lang.RuntimeException
import kotlin.math.round

class GameFinishedFragment : Fragment() {

    private lateinit var gameResult: GameResult
    private var _binding: FragmentGameFinishedBinding? = null
    private val binding: FragmentGameFinishedBinding
        get() = _binding ?: throw RuntimeException("GameFinishedFragment == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameFinishedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addListenerOnBackPressed()
        listenerOnButtonRetry()
        showResult()
    }

    private fun showResult() {
        setEmoji()
        with(binding) {
            tvRequiredAnswers.text = getStringFromResource(
                R.string.required_score,
                gameResult.gameSettings.minCountOfRightAnswers.toString()
            )
            tvScoreAnswers.text = getStringFromResource(
                R.string.score_answers, gameResult
                    .countOfRightAnswers.toString()
            )
            tvRequiredPercentage.text = getStringFromResource(
                R.string.required_percentage, gameResult
                    .gameSettings
                    .minPercentOfRightAnswers.toString()
            )

            val roundedNumber = round(
                gameResult.countOfRightAnswers.toDouble()
                        / gameResult.countOfQuestions * 100
            ).toInt()
            tvScorePercentage.text = getStringFromResource(
                R.string.score_percentage,
                roundedNumber.toString()
            )
        }
    }

    private fun getStringFromResource(resString: Int, param: String): String {
        return requireActivity().resources.getString(resString, param)
    }

    private fun setEmoji() {
        val resIdSmile = if (gameResult.winner) {
            R.drawable.ic_smile
        } else {
            R.drawable.ic_sad
        }
        binding.emojiResult.setImageResource(resIdSmile)
    }

    private fun listenerOnButtonRetry() {
        binding.buttonRetry.setOnClickListener {
            retryGame()
        }
    }

    private fun addListenerOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    retryGame()
                }
            }
        )
    }

    private fun parseArgs() {
        requireArguments().getParcelable<GameResult>(KEY_GAME_RESULT)?.let {
            gameResult = it
        }
    }

    private fun retryGame() {
        requireActivity().supportFragmentManager.popBackStack(
            GameFragment.NAME_GAME_FRAGMENT,
            FragmentManager.POP_BACK_STACK_INCLUSIVE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_GAME_RESULT = "game_result"

        @JvmStatic
        fun newInstance(gameResult: GameResult) =
            GameFinishedFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_GAME_RESULT, gameResult)
                }
            }
    }
}

fun main() {
    val str = "23.4545"
    str.takeWhile { it != '.' }.also { println(it) }
}