package es.eoi.proyectofinal.actividad

import android.content.Intent
import android.os.Bundle
import android.text.Spanned
import android.text.style.ImageSpan
import android.util.Log
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.adaptador.MessageAdapter
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.*
import kotlinx.android.synthetic.main.activity_main.*


class MessageActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var uid: String
    lateinit var edMessage: EditText
    lateinit var mAdapter: MessageAdapter

    val allUbications: ArrayList<Ubicacion> = arrayListOf()
    var allMessage: ArrayList<Message> = arrayListOf()
    var allChats: ArrayList<ChatUser> = arrayListOf()
    var idUbicationSelected = ""
    var currChat: ChatUser? = null
    var ubi: Ubicacion? = null

    val TAG = "miapp"
    var pendingCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        onInit()
    }

    fun onInit() {
        title = DataHolder.currentChatUser?.nombre

        auth = Firebase.auth
        db = Firebase.firestore
        uid = auth.currentUser!!.uid

        edMessage = findViewById<EditText>(R.id.edMessage)

        loadAllUbications()

        val btnUbi = findViewById<ImageView>(R.id.ivUbication)
        btnUbi.setOnClickListener {
            showAllUbications()
        }

        val btnSend = findViewById<ImageButton>(R.id.btnSend)
        btnSend.setOnClickListener {
            sendMessage()
        }

        checkCreated(DataHolder.currentChatUser)
    }

    fun loadMessages() {
        db.collection(DataHolder.DBCHATS).document(currChat!!.idChat.toString())
            .collection(DataHolder.MESSAGES).orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, e ->

                allMessage.clear()
                for (doc in value!!) {
                    val msg = doc.toObject(Message::class.java)
                    Log.d(TAG, "sender -> ${msg.sender}  uid -> $uid read -> ${msg.read}")
                    if (msg.sender != uid && !msg.read) {
                        readMessage(doc.id)
                    }
                    allMessage.add(msg)
                }

                // Actualizar el adapter
                mAdapter.notifyDataSetChanged()
            }

        mAdapter = MessageAdapter(allMessage, uid) {
            DataHolder.currentUbication = it
            val intent = Intent(this, VistaUbicacionActivity::class.java)
            startActivity(intent)
        }

        val rycler = findViewById<RecyclerView>(R.id.messageRecycler)
        rycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        rycler.adapter = mAdapter
    }

    fun readMessage(idMessage: String) {
        db.collection(DataHolder.DBCHATS).document(currChat!!.idChat.toString())
            .collection(DataHolder.MESSAGES).document(idMessage).update("read", true)
    }

    fun sendMessage() {
        val txt = edMessage.text.toString()

        if (txt.trim().isNotEmpty()) {
            val msg = Message()
            msg.data = edMessage.text.toString()
            msg.sender = uid
            msg.ubi = ubi
            msg.read = false

            db.collection(DataHolder.DBCHATS).document(currChat!!.idChat.toString())
                .collection(DataHolder.MESSAGES).add(msg)
                .addOnSuccessListener { documentReference ->
                    edMessage.setText("")
                    ubi = null
                }
                .addOnFailureListener { e ->
                    Log.w("miapp", "Error adding document", e)
                    ubi = null
                }
        } else {
            edMessage.setText("")
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            super.onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    fun loadChats() {
        db.collection(DataHolder.DBUSERS).document(uid).collection(DataHolder.CHATS)
            .addSnapshotListener { value, e ->

                allChats.clear()
                for (doc in value!!) {
                    val chat = doc.toObject(ChatUser::class.java)
                    listenAllMessages(chat)
                    Log.v(TAG, "chats: ${allChats.toString()}")
                }
            }
    }

    fun getUserName(chat: ChatUser) {
        db.collection(DataHolder.DBUSERS).document(chat.idOtherUser.toString()).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val user = document.toObject(User::class.java)
                    chat.nameOtherUser = user!!.nombre
                    getMessages(chat)
                }
            }
            .addOnFailureListener { exception ->
                Log.d("miapp", "get failed with ", exception)
            }

    }

    // filtramos mensajes no leidos
    fun getMessages(chat: ChatUser) {
        db.collection(DataHolder.DBCHATS).document(chat.idChat.toString())
            .collection(DataHolder.MESSAGES).whereEqualTo("read", false).get()
            .addOnSuccessListener { documents ->

                val res = allChats.filter { it.idChat == chat.idChat }

                if (res.isEmpty()) {
                    allChats.add(chat)
                } else {
                    allChats.remove(chat)
                    allChats.add(chat)
                }

                allChats.sortByDescending { it.pendingCount }
                // Aqui actualizamos el adapter
                mAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                Log.w("miapp", "Error getting documents: ", exception)
            }
    }

    fun listenAllMessages(chat: ChatUser) {
        db.collection(DataHolder.DBCHATS).document(chat.idChat.toString())
            .collection(DataHolder.MESSAGES)
            .addSnapshotListener { value, e ->

                allChats.remove(chat)
                getUserName(chat)

            }
    }

    fun saveUser(otherUser: User?) {
        createConversation(otherUser)
    }

    fun checkCreated(otherUser: User?): Boolean {
        var isCreated = false
        db.collection(DataHolder.DBUSERS).document(uid).collection(DataHolder.CHATS).get()
            .addOnSuccessListener { documents ->
                for (doc in documents) {
                    val chat = doc.toObject(ChatUser::class.java)
                    if (chat.idOtherUser == otherUser?.id) {
                        isCreated = true
                        currChat = chat
                    }
                }
                if (!isCreated) saveUser(otherUser) else loadMessages()
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
            }
        return isCreated
    }

    fun createConversation(otherUser: User?) {
        val ref = db.collection(DataHolder.DBCHATS)
        val key = ref.document().id

        val chat = Chat()
        chat.idChat = key

        ref.document(key).set(chat).addOnSuccessListener { document ->

            Log.v("miapp", "Conversacion creada")
            createChatInUser(auth.currentUser!!.uid, key, otherUser?.id!!)

        }.addOnFailureListener { exception ->
            Log.w("miapp", "Error getting documents: ", exception)
        }

    }

    fun createChatInUser(uid: String, chatKey: String, otherUser: String, isMe: Boolean = true) {

        val mChat = ChatUser()
        mChat.idChat = chatKey
        mChat.idOtherUser = otherUser
        DataHolder.currentChat = mChat
        currChat = mChat

        db.collection(DataHolder.DBUSERS).document(uid).collection(DataHolder.CHATS).add(mChat)
            .addOnSuccessListener { document ->
                if (isMe) {
                    createChatInUser(otherUser, chatKey, uid, false)
                } else {
                    Log.v("miapp", "Ya hemos creado todos los users")
                }
                loadMessages()
            }
            .addOnFailureListener { exception ->
                Log.w("miapp", "Error getting documents: ", exception)
            }

    }

    fun showAllUbications() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle("Selecciona una ubicación")

        var ubications: Array<String> = arrayOf()
        if (allUbications.size > 0) {
            ubications = allUbications.map { it.nombre!! }.toTypedArray()
        }
        var checkedItem = 0

        builder.setSingleChoiceItems(ubications, checkedItem) { dialog, idSeleccionado ->
            checkedItem = idSeleccionado
        }

        builder.setPositiveButton("OK") { dialog, which ->
            ubi = null
            edMessage.setText(ubications[checkedItem])
            idUbicationSelected = allUbications[checkedItem].id.toString()
            createUbicationChip(allUbications[checkedItem])
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    fun loadAllUbications() {
        val uid = auth.currentUser!!.uid

        db.collection(DataHolder.dbUbication).limit(50)
            .addSnapshotListener { value, e ->

                allUbications.clear()
                for (doc in value!!) {
                    val ubi = doc.toObject(Ubicacion::class.java)
                    ubi.id = doc.id
                    allUbications.add(ubi)
                }
            }
    }

    private fun createUbicationChip(ubicacion: Ubicacion) {
        ubi = ubicacion
        val chipDrawable = ChipDrawable.createFromResource(this, R.xml.standalone_chip)
        chipDrawable.setBounds(0, 0, chipDrawable.intrinsicWidth, chipDrawable.intrinsicHeight)
        val span = ImageSpan(chipDrawable)
        val txMessage = edMessage.text!!
        chipDrawable.text = ubicacion.nombre
        txMessage.setSpan(span, 0, ubicacion.nombre!!.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
}