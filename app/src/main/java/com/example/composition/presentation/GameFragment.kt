package com.example.composition.presentation

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.composition.R
import com.example.composition.databinding.FragmentGameBinding
import com.example.composition.domain.entity.GameResult
import com.example.composition.domain.entity.Level

class GameFragment : Fragment() {

    private val viewModel: GameViewModel by lazy {
        ViewModelProvider(
            this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(requireActivity().application)
        )[GameViewModel::class.java]
    }
    private val tvOptions by lazy {
        mutableListOf<TextView>().apply {
            add(binding.tvOption1)
            add(binding.tvOption2)
            add(binding.tvOption3)
            add(binding.tvOption4)
            add(binding.tvOption5)
            add(binding.tvOption6)
            shuffle()
        }
    }


    private lateinit var level: Level
    private var _binding: FragmentGameBinding? = null
    private val binding: FragmentGameBinding
        get() = _binding ?: throw RuntimeException("GameFragment == null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseArgs()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        observeOnViewModel()
        viewModel.startGame(level)
    }

    private fun observeOnViewModel() {
        with(viewModel) {
            question.observe(viewLifecycleOwner) { question ->
                binding.tvSum.text = question.sum.toString()
                binding.tvLeftNumber.text = question.visibleNumber.toString()
                tvOptions.forEachIndexed { index, textView ->
                    val number = question.option[index]
                    textView.text = number.toString()
                    textView.setOnClickListener {
                        viewModel.chooseAnswer(number)
                    }
                }
            }
            formattedTime.observe(viewLifecycleOwner) {
                binding.tvTimer.text = it
            }
            progressAnswers.observe(viewLifecycleOwner) {
                binding.tvAnswersProgress.text = it
            }

            gameResult.observe(viewLifecycleOwner) {
                launchGameFinishedFragment(it)
            }
            percentOfRightAnswers.observe(viewLifecycleOwner) {
                binding.progressBar.setProgress(it, true)
                binding.tvProgressBarText.text = it.toString()
            }
            enoughCountOfRightAnswers.observe(viewLifecycleOwner) {
                if (it) {
                    binding.tvAnswersProgress.setTextColor(
                        ContextCompat.getColor(
                            requireActivity(),
                            R.color.string_right
                        )
                    )
                }
            }
            enoughPercentOfRightAnswers.observe(viewLifecycleOwner) {
                val colorRight = ContextCompat.getColor(requireActivity(), R.color.string_right)
                val colorWrong = ContextCompat.getColor(requireActivity(), R.color.string_wrong)
                binding.progressBar.progressTintList = if (it) {
                    ColorStateList.valueOf(colorRight)
                } else {
                    ColorStateList.valueOf(colorWrong)
                }
            }
        }
    }

    private fun launchGameFinishedFragment(gameResult: GameResult) {
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container, GameFinishedFragment.newInstance(gameResult))
            .addToBackStack(null)
            .commit()
    }

    private fun parseArgs() {
        requireArguments().getParcelable<Level>(KEY_LEVEL)?.let {
            level = it
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val KEY_LEVEL = "level"
        const val NAME_GAME_FRAGMENT = "name_game_fragment"

        @JvmStatic
        fun newInstance(level: Level) =
            GameFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(KEY_LEVEL, level)
                }
            }
    }
}