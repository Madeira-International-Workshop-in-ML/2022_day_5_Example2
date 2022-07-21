package com.example.example2

import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel

class DigitClassifier(private val context: Context) {

    private val modelPath: String = "mnist.tflite"
    private lateinit var interpreter: Interpreter


    /**
     * Loads the SavedModel
     */
    private fun loadModelFile(assetManager: AssetManager): MappedByteBuffer {
        val fileDescriptor = assetManager.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    /**
     * Initializes the SavedModel
     */
    fun initialize() {
        val options = Interpreter.Options()
        options.setNumThreads(5)
        interpreter = Interpreter(loadModelFile(context.assets), options)
    }


    /**
     * Returns the result after running the recognition with the help of interpreter
     * on the passed bitmap
     */
    fun classify(bitmap: Bitmap): String {
        val resizedImage = Bitmap.createScaledBitmap(bitmap, INPUT_SIZE, INPUT_SIZE, true)
        val byteBuffer = convertBitmapToByteBuffer(resizedImage)

        // Define an array to store the model output.
        val output = Array(1) { FloatArray(OUTPUT_CLASSES_COUNT) }

        // Run inference with the input data.
        interpreter.run(byteBuffer, output)

        return getSortedResult(output)

    }

    /**
     * Converts the bitmap to byte buffer
     */
    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(4 * INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE)
        byteBuffer.order(ByteOrder.nativeOrder())

        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        for (pixelValue in pixels) {
            val r = (pixelValue shr 16 and 0xFF)
            val g = (pixelValue shr 8 and 0xFF)
            val b = (pixelValue and 0xFF)

            // Convert RGB to grayscale and normalize pixel value to [0..1].
            val normalizedPixelValue = (r + g + b) / 3.0f / 255.0f
            byteBuffer.putFloat(normalizedPixelValue)
        }

        return byteBuffer

    }

    /**
     * Sorts the results and outputs the argmax
     */
    private fun getSortedResult(output: Array<FloatArray>): String {
        val result = output[0]
        val maxIndex = result.indices.maxByOrNull { result[it] } ?: -1
        return "Prediction Result: %d\nConfidence: %.2f%%".format(maxIndex, result[maxIndex] * 100)
    }

    companion object {
        const val INPUT_SIZE = 28
        const val PIXEL_SIZE = 1
        const val OUTPUT_CLASSES_COUNT = 10
    }

}