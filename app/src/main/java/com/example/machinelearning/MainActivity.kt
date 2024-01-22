package com.example.machinelearning

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var buttonCamera = findViewById<Button>(R.id.btnCamera)

        buttonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(intent.resolveActivity(packageManager)!=null){
                startActivityForResult(intent, 123)
            }
            else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==123 && resultCode== RESULT_OK){
            val extras = data?.extras
            val bitmap = extras?.get("data") as? Bitmap
            if (bitmap != null) {
                detectFace(bitmap)
            }
        }
    }

    private fun detectFace(bitmap: Bitmap){
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()
        val detector = FaceDetection.getClient(options)

        val image = InputImage.fromBitmap(bitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully, our face is successfully detected
                var resultText = " "
                var i =1
                for(face in faces) {
                    resultText="Face Number $i \n" +
                    "Smile : ${face.smilingProbability?.times(100)}%\n" +
                    "Left Eye Open : ${face.leftEyeOpenProbability?.times(100)}%\n" +
                    "Right Eye Open : ${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                }

                if(faces.isEmpty()){
                    Toast.makeText(this, "No face detected", Toast.LENGTH_LONG).show()
                }
                else{
                    val cardText=findViewById<TextView>(R.id.cardText)
                    cardText.text=resultText

                }
            }
            .addOnFailureListener { e ->
                // Task failed with an exception, face detection is failed
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }
    }
}