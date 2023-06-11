package com.example.translator.ui.selectLanguage

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.translator.R
import com.example.translator.databinding.FragmentSelectLanguagesBinding
import com.example.translator.ui.main.MainFragment
import com.google.mlkit.nl.languageid.LanguageIdentification
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslatorOptions


class SelectLanguagesFragment : Fragment() {

    companion object {
        fun newInstance() = SelectLanguagesFragment()
    }

    private lateinit var binding: FragmentSelectLanguagesBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_select_languages, container, false)
        return binding.root

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
        observers()
    }

    private fun observers() {
        // no-op
    }

    private fun init() {

        val supportedLanguages = TranslateLanguage.getAllLanguages()


        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, supportedLanguages
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.inputSpinner.adapter = adapter
        }

        binding.goOn.setOnClickListener {

            val nextFrag = MainFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.container, nextFrag).addToBackStack(null).commit()
        }
    }

}