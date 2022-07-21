package com.example.example2

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.divyanshu.draw.widget.DrawView
import com.example.example2.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var digitClassifier: DigitClassifier
    private lateinit var resultText: TextView
    private lateinit var drawView: DrawView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Register listeners
        binding.clearButton.setOnClickListener { clearCanvas() }
        binding.inferButton.setOnClickListener { inferCanvas() }

        // Create the digit classifier
        digitClassifier = DigitClassifier(this)
        digitClassifier.initialize()

        // Capture elements form the UI
        resultText = binding.resultText
        drawView = binding.drawView

        // Set the parameters for the draw view
        drawView.setStrokeWidth(70.0f)
        drawView.setColor(Color.WHITE)
        drawView.setBackgroundColor(Color.BLACK)


    }

    /**
     * When the button infer is clicked
     */
    private fun inferCanvas() {
        val results = digitClassifier.classify(drawView.getBitmap())
        resultText.text = results
    }

    /**
     * When the button clear canvas is clicked
     */
    private fun clearCanvas() {
        drawView.clearCanvas()
        resultText.text = ""
    }


}
