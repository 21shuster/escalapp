package es.eoi.proyectofinal.presentacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.PhotoActivity
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.User
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_profile.*
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL

class ProfileFragment : Fragment() {

    val TAG = "miapp"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    private var user: FirebaseUser? = null
    private val RESULT_OK = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.title  = "Perfil"

        auth = Firebase.auth
        db = Firebase.firestore
        user = auth.currentUser

        profileImage.setOnClickListener{
            startActivityForResult(Intent(context, PhotoActivity::class.java), RESULT_OK)
        }

        db.collection(DataHolder.dbUsers).document(auth.currentUser!!.uid).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val userData = document.toObject(User::class.java)
                    if (userData != null) {
                        Picasso.get().load(userData.photo).into(profileImage)
                        tvName.setText(userData.nombre)
                        tvEmail.setText(userData.email)
                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RESULT_OK) {
            updateUI()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateUI() {
        if (user != null) {
            try {
                val newurl = URL(user!!.photoUrl.toString())
                Log.e("miapp", "Uri: $newurl User: ${auth.currentUser?.uid!!}")
                Picasso.get().load(user?.photoUrl).into(profileImage)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}