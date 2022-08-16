package com.example.ethereumwallet.fragments.settings

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.ethereumwallet.R
import com.example.ethereumwallet.databinding.FragmentSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SettingsFragment : Fragment() {
    private var _binding : FragmentSettingsBinding? = null
    private val binding get() = _binding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSettingsBinding.bind(view)

        binding!!.signOutIcon.setOnClickListener {
            signOut()
        }


    }

    private fun signOut() {
        val pref: SharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        val editor: SharedPreferences.Editor = pref.edit()
        editor.remove(getString(R.string.private_key))
        editor.apply()
        Toast.makeText(requireContext(), "Successful sign out", Toast.LENGTH_SHORT).show()
        findNavController().navigate(R.id.action_settingsFragment_to_loginFragment)

    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}