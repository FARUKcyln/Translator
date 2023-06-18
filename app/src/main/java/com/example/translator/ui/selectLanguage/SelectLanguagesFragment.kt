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
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions


class SelectLanguagesFragment : Fragment() {

    companion object {
        fun newInstance() = SelectLanguagesFragment()

        var inputLanguage: String? = null
        var outputLanguage: String? = null

        val languageMap = mapOf(
            "af" to "Afrikaans",
            "ar" to "Arabic",
            "be" to "Belarusian",
            "bg" to "Bulgarian",
            "bn" to "Bengali",
            "ca" to "Catalan",
            "cs" to "Czech",
            "cy" to "Welsh",
            "da" to "Danish",
            "de" to "German",
            "el" to "Greek",
            "en" to "English",
            "eo" to "Esperanto",
            "es" to "Spanish",
            "et" to "Estonian",
            "fa" to "Persian",
            "fi" to "Finnish",
            "fr" to "French",
            "ga" to "Irish",
            "gl" to "Galician",
            "gu" to "Gujarati",
            "he" to "Hebrew",
            "hi" to "Hindi",
            "hr" to "Croatian",
            "ht" to "Haitian",
            "hu" to "Hungarian",
            "id" to "Indonesian",
            "is" to "Icelandic",
            "it" to "Italian",
            "ja" to "Japanese",
            "ka" to "Georgian",
            "kn" to "Kannada",
            "ko" to "Korean",
            "lt" to "Lithuanian",
            "lv" to "Latvian",
            "mk" to "Macedonian",
            "mr" to "Marathi",
            "ms" to "Malay",
            "mt" to "Maltese",
            "nl" to "Dutch",
            "no" to "Norwegian",
            "pl" to "Polish",
            "pt" to "Portuguese",
            "ro" to "Romanian",
            "ru" to "Russian",
            "sk" to "Slovak",
            "sl" to "Slovenian",
            "sq" to "Albanian",
            "sv" to "Swedish",
            "sw" to "Swahili",
            "ta" to "Tamil",
            "te" to "Telugu",
            "th" to "Thai",
            "tl" to "Tagalog",
            "tr" to "Turkish",
            "uk" to "Ukrainian",
            "ur" to "Urdu",
            "vi" to "Vietnamese",
            "zh" to "Chinese"
        )
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
        binding.progressBar.visibility = View.GONE
        binding.loadingText.visibility = View.GONE
        binding.main.visibility = View.VISIBLE

        binding.inputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                for ((key, value) in languageMap) {
                    if (value == p0?.selectedItem.toString()) {
                        inputLanguage = key
                        break
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // no-op
            }

        }

        binding.outputSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                for ((key, value) in languageMap) {
                    if (value == p0?.selectedItem.toString()) {
                        outputLanguage = key
                        break
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                // no-op
            }

        }

        val supportedLanguages = TranslateLanguage.getAllLanguages()


        val mapResult = mutableMapOf<String, String>()
        for (lang in supportedLanguages) {
            mapResult[lang] = languageMap[lang].toString()
        }

        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, mapResult.values.toList()
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            binding.inputSpinner.adapter = adapter
        }

        ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_item, mapResult.values.toList()
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

                val nextFrag = MainFragment(translator)
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