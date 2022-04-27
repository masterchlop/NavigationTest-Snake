package com.example.navigationtest

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.navigationtest.databinding.FragmentOneBinding
import kotlinx.android.synthetic.main.fragment_one.view.*


class fragment_one : Fragment() {


    private lateinit var binding: FragmentOneBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOneBinding.inflate(inflater,container,false)
        val view = binding.root

        view.ranks.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_fragment_one_to_deviceRankFragment)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        view.button1.setOnClickListener {
            val player_name = binding.plainTextInput.text.toString()
            if (player_name.isNotEmpty()){
                val action = fragment_oneDirections.actionFragmentOneToFragmentSnake(player_name)
                Navigation.findNavController(view).navigate(action)
            }
            else{
                Toast.makeText(requireContext(),"set name",Toast.LENGTH_SHORT).show()
            }

        }
    }

}