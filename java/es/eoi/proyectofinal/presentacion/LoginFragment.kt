package es.eoi.proyectofinal.presentacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.MainActivity
import es.eoi.proyectofinal.modelo.User
import kotlinx.android.synthetic.main.activity_start.*
import kotlinx.android.synthetic.main.fragment_login.*
import render.animations.Render
import render.animations.Zoom

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var db: FirebaseFirestore
    private lateinit var callbackManager: CallbackManager
    private var user: FirebaseUser? = null
    private val render = context?.let { Render(it) }
    val RC_SIGN_IN = 1
    val TAG = "miapp"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        onInit()
        appear()
        setUp()
    }

    private fun setUp() {
        btnLogin.setOnClickListener {
            if (inputEmail.text!!.isNotEmpty() && inputPassword.text!!.isNotEmpty()) {
                FirebaseAuth.getInstance()
                    .signInWithEmailAndPassword(
                        inputEmail.text.toString(),
                        inputPassword.text.toString()
                    ).addOnCompleteListener {
                        if (it.isSuccessful) {
                            showHome(it.result?.user?.email ?: "")
                        } else {
                            Log.d("MIAPP", it.exception.toString())
                            showAlert()
                        }
                    }

            }
        }
    }

    fun showHome(email: String) {
        val homeIntent = Intent(context, MainActivity::class.java).apply {
            putExtra("email", email)
        }
        startActivity(homeIntent)
    }

    fun showAlert() {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("aceptar", null)
        val dialog: AlertDialog = builder.create()
    }

    fun appear() {
        render?.setAnimation(Zoom().In(logo))
        render?.setDuration(1500)
        render?.start()
    }

    private fun onInit() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
        // Initialize Firebase Auth
        auth = Firebase.auth
        // Initialize Firebase Firestore
        db = Firebase.firestore
        // Initialize Facebook Login button
        callbackManager = CallbackManager.Factory.create()

        btnGoogle.setOnClickListener {
            signInGoogle()
        }

        btnFacebook.setOnClickListener {
            signInFacebook()
        }

        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                }

                override fun onCancel() {
                    showAlert()
                }

                override fun onError(exception: FacebookException) {
                    showAlert()
                }
            })
    }

    fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signInFacebook() {
        btnFacebook.setReadPermissions("email")
        // If using in a fragment
        btnFacebook.fragment = this
        btnFacebook.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
            }

            override fun onCancel() {
                Log.d(TAG, "facebook:onCancel")
                showAlert()
            }

            override fun onError(error: FacebookException) {
                Log.d(TAG, "facebook:onError", error)
                showAlert()
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(
                    TAG, "firebaseAuthWithGoogle:" + account.id +
                            "\n" + account.displayName +
                            "\n" + account.email +
                            "\n" + account.familyName +
                            "\n" + account.photoUrl +
                            "\n" + account.givenName
                )

                firebaseAuthWithGoogle(account.idToken!!, account.givenName, account.familyName)
            } catch (error: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", error)
                showAlert()
            }
        }

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data)

    }

    private fun firebaseAuthWithGoogle(idToken: String, firstName: String?, lastName: String?) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    createUser(firstName.toString(), lastName.toString(), user?.uid.toString())

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                }
            }

    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        Log.d(TAG, "handleFacebookAccessToken:$token")

        val credential = FacebookAuthProvider.getCredential(token.token)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    createUser(user?.displayName.toString(), user?.providerData.toString(), user?.uid.toString())
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    Toast.makeText(requireContext(), "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser

        if (currentUser != null) {
            Log.v(TAG, "Usuario logueado ${currentUser.uid}")
            navigateToHome()
        }
    }

    fun navigateToHome() {
        startActivity(Intent(context, MainActivity::class.java))
        requireActivity().finish()
    }

    fun createUser(firstName: String, lastName: String, uid: String) {
        val newUser = User()
        newUser.nombre = firstName
        newUser.apellido = lastName

        db.collection("users").document(uid).set(newUser).addOnSuccessListener {
            navigateToHome()
        }.addOnFailureListener { e ->
            Log.w(TAG, "Error adding document", e)
        }
    }

}