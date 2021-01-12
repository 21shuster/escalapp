package es.eoi.proyectofinal.presentacion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.modelo.User
import kotlinx.android.synthetic.main.fragment_register.*

class RegisterFragment : Fragment() {

    val TAG = "miapp"
    lateinit var db: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        db = Firebase.firestore
        setUp()
    }

    fun createUser(firstName: String, uid: String, email: String) {
        val newUser = User()
        newUser.nombre = firstName
        newUser.email = email

        db.collection("users").document(uid)
            .set(newUser)
            .addOnSuccessListener { document ->
                Log.v(TAG, "createUser:USUARIO CREADO")
                //showHome()

            }.addOnFailureListener { e ->
                Log.v(TAG, "createUser:ERROR CREACION USUARIO", e)
            }

    }

    fun setUp() {
        btnRegister.setOnClickListener {
            if (txtEdName.text!!.isNotEmpty() && txtEdEmail.text!!.isNotEmpty() && txtEdPassword.text!!.isNotEmpty()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(
                    txtEdEmail.text.toString(),
                    txtEdPassword.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        createUser(txtEdName.text.toString(),it.result?.user!!.uid, txtEdEmail.text.toString())
                        //showHome()
                        Log.d(TAG, "entrado2")
                    } else {
                        Toast.makeText(activity, "Acceso denegado", Toast.LENGTH_SHORT).show()
                        Log.d(TAG, "entrado3")
                    }
                }
            }
        }
    }
}