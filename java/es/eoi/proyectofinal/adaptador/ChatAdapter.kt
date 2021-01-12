package es.eoi.proyectofinal.adaptador

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.ChatUser
import es.eoi.proyectofinal.modelo.Message
import es.eoi.proyectofinal.modelo.User

class ChatAdapter(private val mDataSet: ArrayList<User>, var click: (chat: User) -> Unit) :
    RecyclerView.Adapter<ChatAdapter.MainViewHolder>() {

    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var uid: String
    var allChats: ArrayList<ChatUser> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_chat, parent, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val miuser = mDataSet[position]
        holder.addData(miuser)

    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val name = v.findViewById<TextView>(R.id.profile_name)
        val logo = v.findViewById<ImageView>(R.id.logoUser)
        val pending = v.findViewById<CardView>(R.id.cvCount)
        val count = v.findViewById<TextView>(R.id.count)
        val card = v.findViewById<MaterialCardView>(R.id.cardView)

        private val TAG = "miApp"

        fun addData(data: User) {

            auth = Firebase.auth
            db = Firebase.firestore
            uid = auth.currentUser!!.uid

            name.text = data.nombre
            card.setOnClickListener {
                click(data)
            }
            Picasso.get().load(data?.photo).into(logo)
            getChatUser(data.id.toString())

        }

        fun getChatUser(idUser: String){
            db.collection(DataHolder.dbUsers).document(uid)
                .collection(DataHolder.CHATS).addSnapshotListener { value, e ->
                    for (doc in value!!) {
                        val chat = doc.toObject(ChatUser::class.java)
                        if(chat.idOtherUser == idUser) getPendingCount(chat.idChat.toString())
                        Log.d(TAG, "otherUser -> ${chat.idOtherUser}")
                    }
                }
        }

        fun getPendingCount(idChat: String){
            var pendingCount = 0
            db.collection(DataHolder.DBCHATS).document(idChat)
                .collection(DataHolder.MESSAGES).addSnapshotListener{ value, e ->
                    for (doc in value!!){
                        val message = doc.toObject(Message::class.java)
                        if(!message.read && message.sender != uid) pendingCount ++
                        Log.d(TAG, "pendingCount -> $pendingCount")
                    }
                    if (pendingCount > 0) {
                        pending.visibility = View.VISIBLE
                        count.text = pendingCount.toString()
                    } else pending.visibility = View.GONE
                }
        }

    }

}