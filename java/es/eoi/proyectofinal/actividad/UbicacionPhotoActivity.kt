package es.eoi.proyectofinal.actividad

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.webkit.MimeTypeMap
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.datos.DataHolder
import java.io.IOException
import java.util.*

class UbicacionPhotoActivity : AppCompatActivity() {

    private var imageReference: StorageReference? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    private val CHOOSING_IMAGE_REQUEST = 1234
    private var fileUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ubicacion_photo)
        supportActionBar?.title = "Foto de ubicacion"

        imageReference = Firebase.storage.reference.child("images")
        auth = Firebase.auth
        db = Firebase.firestore

        val btnSelection = findViewById<MaterialButton>(R.id.btnSelectionUbi)
        btnSelection.setOnClickListener {
            showChoosingFile()
        }

        val btnUpload = findViewById<MaterialButton>(R.id.btnUploadUbi)
        btnUpload.setOnClickListener {
            uploadFile()
        }
    }

    // LLama a la pantalla para seleccionar una imagen
    fun showChoosingFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_PICK
        startActivityForResult(
            Intent.createChooser(intent, "Selecciona una imágen"),
            CHOOSING_IMAGE_REQUEST
        )
    }

    // Se ejecuta despues de seleccionar la foto esta atento a cuando seleccionas un valor tu galeria de fotos
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
            try {
                val image_preview = findViewById<ImageView>(R.id.image_preview_ubi)
                image_preview.setImageURI(data.data)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    fun uploadFile() {
        if (fileUri != null) {
            val fileName = Date().time.toString() + auth.currentUser!!.uid
            if (!validateInputFileName(fileName)) {
                return
            }

            val fileRef = imageReference!!.child(fileName + "." + getFileExtension(fileUri!!))
            fileRef.putFile(fileUri!!)
                .addOnSuccessListener { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener { uri ->
                        Log.e("miapp", "Uri: $uri")
                        Log.e("miapp", "Uri: ${DataHolder.currentUbication?.id!!}")
                        Log.e("miapp", "Uri: ${DataHolder.currentUbication?.toString()}")
                        db.collection(DataHolder.dbUbication).document(DataHolder.currentUbication?.id!!)
                            .update("foto", uri.toString()).addOnCompleteListener {
                                Toast.makeText(this, "Fichero subido ", Toast.LENGTH_LONG).show()
                                goHome()
                            }
                    }
                }
                .addOnFailureListener { exception ->
                    // Mostramos mensaje en caso de fallo
                    Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                }


        } else {
            Toast.makeText(this, "No hay fichero!", Toast.LENGTH_LONG).show()
        }
    }

    // Obtiene la extension del fichero
    private fun getFileExtension(uri: Uri): String {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri))!!
    }

    // Valida que el dichero tenga un nombre
    private fun validateInputFileName(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "La foto necesita un nombre!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }


    fun goHome() {
        finish()
    }
}