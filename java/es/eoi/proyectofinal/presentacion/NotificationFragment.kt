package es.eoi.proyectofinal.presentacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.MessageActivity
import es.eoi.proyectofinal.adaptador.ChatAdapter
import es.eoi.proyectofinal.databinding.FragmentNotificationBinding
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.ChatUser
import es.eoi.proyectofinal.modelo.Message
import es.eoi.proyectofinal.modelo.User
import kotlinx.android.synthetic.main.activity_main.*


class NotificationFragment : Fragment() {

    private var bind: FragmentNotificationBinding? = null
    private val b get() = bind!!
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var uid: String

    var allChats: ArrayList<ChatUser> = arrayListOf()
    val allUsers: ArrayList<User> = arrayListOf()

    lateinit var mAdapter: ChatAdapter
    private var posicion = 0
    val TAG = "miapp"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        bind = FragmentNotificationBinding.inflate(inflater, container, false)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind?.recyclerChats!!.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        onInit()
    }

    fun onInit() {
        auth = Firebase.auth
        db = Firebase.firestore
        uid = auth.currentUser!!.uid

        mAdapter = ChatAdapter(allUsers) { user ->
            // CLICK
            DataHolder.currentChatUser = user
            var i = Intent(context, MessageActivity::class.java)
            startActivity(i)
        }

        bind?.recyclerChats!!.adapter = mAdapter
        loadAllUsers()
    }


    fun loadAllUsers() {
        db.collection(DataHolder.DBUSERS).get().addOnSuccessListener { result ->
            allUsers.clear()
            for (document in result) {
                val user = document.toObject(User::class.java)
                user.id = document.id
                if (user.id != uid) {
                    allUsers.add(user)
                    Log.v(TAG, "all user: ${allUsers.toString()}")
                }
            }
            mAdapter.notifyDataSetChanged()
        }
            .addOnFailureListener { exception ->
                Log.w("miapp", "Error getting documents: ", exception)
            }
    }

    fun getChatUser(idUser: String) {
        db.collection(DataHolder.dbUsers).document(idUser)
            .collection(DataHolder.CHATS).addSnapshotListener { value, e ->
                allChats.clear()
                for (doc in value!!) {
                    val chat = doc.toObject(ChatUser::class.java)
                    getPendingCount(chat.idChat.toString())
                    allChats.add(chat)
                }
            }
    }

    fun getPendingCount(idChat: String): Int {
        var pending = 0
        db.collection(DataHolder.DBCHATS).document(idChat)
            .collection(DataHolder.MESSAGES).addSnapshotListener { value, e ->
                for (doc in value!!) {
                    val message = doc.toObject(Message::class.java)
                    if (!message.read && message.sender != uid) pending++
                }
            }
        return pending
    }

    override fun onResume() {
        super.onResume()
        loadAllUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        bind = null
    }

}
