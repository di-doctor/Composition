package com.example.composition.presentation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.composition.R
import com.example.composition.databinding.FragmentChooseLevelBinding
import com.example.composition.domain.entity.Level
import java.lang.RuntimeException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ChooseLevelFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ChooseLevelFragment : Fragment() {
    private var _binding : FragmentChooseLevelBinding? = null
    private val binding: FragmentChooseLevelBinding
        get() = _binding ?:throw RuntimeException("ChooseLevelFragment == null")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChooseLevelBinding.inflate(inflater,container,false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            buttonLevelTest.setOnClickListener {
                val fragment = GameFragment.newInstance(Level.TEST)
                launchChooseFragmentWithParam(fragment)
            }
            buttonLevelEasy.setOnClickListener {
                val fragment = GameFragment.newInstance(Level.EASY)
                launchChooseFragmentWithParam(fragment)
            }
           buttonLevelMedium.setOnClickListener {
                val fragment = GameFragment.newInstance(Level.MEDIUM)
                launchChooseFragmentWithParam(fragment)
            }
            buttonLevelHard.setOnClickListener {
                val fragment = GameFragment.newInstance(Level.HARD)
                launchChooseFragmentWithParam(fragment)
            }
        }
    }
    private fun launchChooseFragmentWithParam(fragment:GameFragment){
        requireActivity().supportFragmentManager
            .beginTransaction()
            .replace(R.id.main_container,fragment)
            .addToBackStack(GameFragment.NAME_GAME_FRAGMENT)
            .commit()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    companion object {
        const val NAME = "name"
        @JvmStatic
        fun newInstance() = ChooseLevelFragment()
    }
}