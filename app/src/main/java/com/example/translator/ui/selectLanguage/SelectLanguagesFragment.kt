package com.example.translator.ui.selectLanguage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.translator.R
import com.example.translator.databinding.FragmentSelectLanguagesBinding
import com.example.translator.ui.main.MainFragment
import com.google.android.material.navigation.NavigationBarView.OnItemSelectedListener
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class SelectLanguagesFragment : Fragment() {

    companion object {
        fun newInstance() = SelectLanguagesFragment()
    }

    private lateinit var binding: FragmentSelectLanguagesBinding
    private var inputLanguage : String? = null
    private var outputLanguage : String? = null


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
        binding.progressBar.visibility = View.GONE
        binding.loadingText.visibility = View.GONE
        binding.main.visibility = View.VISIBLE

        binding.inputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                inputLanguage = p0?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
               // no-op
            }

        }

        binding.outputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                outputLanguage = p0?.selectedItem.toString()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // no-op
            }

        }

        val supportedLanguages = TranslateLanguage.getAllLanguages()


        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, supportedLanguages
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.inputSpinner.adapter = adapter
        }

        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, supportedLanguages
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.outputSpinner.adapter = adapter
        }

        binding.goOn.setOnClickListener {

            binding.progressBar.visibility = View.VISIBLE
            binding.loadingText.visibility = View.VISIBLE
            binding.main.visibility = View.GONE

            val options = TranslatorOptions.Builder().setSourceLanguage(inputLanguage!!)
                .setTargetLanguage(outputLanguage!!).build()
            val translator = Translation.getClient(options)

            val conditions = DownloadConditions.Builder().build()

            translator.downloadModelIfNeeded(conditions).addOnSuccessListener {
                println("Successfull")

                val nextFrag = MainFragment()
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.container, nextFrag).addToBackStack(null).commit()

            }.addOnFailureListener { exception ->
                println("Hata")
                Log.d("MainFragment", exception.toString())
                requireActivity().finish()
            }
        }
    }



}