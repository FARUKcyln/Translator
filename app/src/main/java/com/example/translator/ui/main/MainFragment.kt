package com.example.translator.ui.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.translator.R
import com.example.translator.databinding.FragmentMainBinding
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import java.util.*


class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private lateinit var speechRecognizer: SpeechRecognizer
    private lateinit var speechRecognizerIntent: Intent
    private lateinit var textToSpeech: TextToSpeech
    private var isListening = false
    private val adapter = GroupAdapter<GroupieViewHolder>()

    companion object {
        fun newInstance() = MainFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_main, container, false)
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

        if (!allPermissionGranted()) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO), 123
            )
        }

        binding.history.layoutManager = LinearLayoutManager(requireContext())
        binding.history.adapter = adapter

        // Create an English-German translator:
        val options = TranslatorOptions.Builder().setSourceLanguage(TranslateLanguage.TURKISH)
            .setTargetLanguage(TranslateLanguage.ENGLISH).build()
        val turkishEnglishTranslator = Translation.getClient(options)

        val conditions = DownloadConditions.Builder().build()

        turkishEnglishTranslator.downloadModelIfNeeded(conditions).addOnSuccessListener {
            // no-op
            println("Successfull")
        }.addOnFailureListener { exception ->
            println("Hata")
            Log.d("MainFragment", exception.toString())
            requireActivity().finish()
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
        speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(p0: Bundle?) {
            }

            override fun onBeginningOfSpeech() {
            }

            override fun onRmsChanged(p0: Float) {
            }

            override fun onBufferReceived(p0: ByteArray?) {
            }

            override fun onEndOfSpeech() {
            }

            override fun onError(p0: Int) {
            }

            override fun onResults(p0: Bundle?) {

                val matches: ArrayList<String> =
                    p0?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) as ArrayList<String>

                if (matches[0].isNotEmpty()) {
                    adapter.add(SpeechToItem(matches[0]))
                }
                turkishEnglishTranslator.translate(matches[0])
                    .addOnSuccessListener { translatedText ->
                        binding.text.setText(translatedText)
                        adapter.add(SpeechFromItem(translatedText))
                    }.addOnFailureListener { exception ->
                        println()
                        Log.d("Translation", exception.toString())
                    }
            }

            override fun onPartialResults(p0: Bundle?) {
            }

            override fun onEvent(p0: Int, p1: Bundle?) {
            }
        })

        textToSpeech = TextToSpeech(
            requireActivity().applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech.language = Locale.US
            }
        }

        binding.text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val timer = Timer()
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val text = binding.text.text.toString()
                        textToSpeech.speak(text, TextToSpeech.QUEUE_ADD, null, null)
                    }

                }, 100)
            }

            override fun afterTextChanged(p0: Editable?) {
                if (isListening) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.microphone.performClick()
                    }, 2000)

                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.microphone.performClick()
                    }, 2000)
                }
            }
        })

        binding.microphone.setOnClickListener {
            if (!isListening) {
                isListening = true
                binding.microphone.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_mic_on_50)
                binding.text.hint = ""
                binding.text.hint = "Listening....."
                speechRecognizer.startListening(speechRecognizerIntent)
            } else {
                isListening = false
                binding.microphone.background =
                    ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_mic_50)
                binding.text.setText("")
                binding.text.hint = "You will see input here"
                speechRecognizer.stopListening()
            }
        }

    }

    private fun allPermissionGranted(): Boolean {
        return arrayOf(Manifest.permission.RECORD_AUDIO).all {
            ContextCompat.checkSelfPermission(
                requireContext(), it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == 123) {
            if (allPermissionGranted()) {
                //no-op
            } else {
                android.R.attr.finishOnTaskLaunch
            }
        }
    }

}

class SpeechFromItem(private val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.text).text = text
    }

    override fun getLayout(): Int {
        return R.layout.speech_from_message
    }
}

class SpeechToItem(private val text: String) : Item<GroupieViewHolder>() {
    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        viewHolder.itemView.findViewById<TextView>(R.id.text).text = text
    }

    override fun getLayout(): Int {
        return R.layout.speech_to_message
    }
}