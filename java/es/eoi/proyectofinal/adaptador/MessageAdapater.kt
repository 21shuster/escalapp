package es.eoi.proyectofinal.adaptador

import android.content.Intent
import android.graphics.Color
import android.media.Image
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ImageSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.ChipDrawable
import es.eoi.proyectofinal.R
import es.eoi.proyectofinal.actividad.VistaUbicacionActivity
import es.eoi.proyectofinal.datos.DataHolder
import es.eoi.proyectofinal.modelo.Message
import es.eoi.proyectofinal.modelo.Ubicacion
import kotlinx.android.synthetic.main.activity_message.view.*


class MessageAdapter(private val mDataSet: ArrayList<Message>, private val uid: String, val function: (Ubicacion) -> Unit) :
    RecyclerView.Adapter<MessageAdapter.MainViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_message, parent, false)
        return MainViewHolder(v)
    }

    override fun onBindViewHolder(holder: MainViewHolder, position: Int) {
        val data = mDataSet[position]
        holder.addData(data, uid)
        holder.linear.setOnClickListener {
            if(data.ubi != null){
                function(data.ubi!!)
            }
        }
    }

    override fun getItemCount(): Int {
        return mDataSet.size
    }

    inner class MainViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val txt = v.findViewById<TextView>(R.id.txtMessage)
        val linear = v.findViewById<LinearLayout>(R.id.layoutText)
        val linearParent = v.findViewById<LinearLayout>(R.id.layoutPapi)
        val img = v.findViewById<ImageView>(R.id.ivLoc)

        fun addData(data: Message, uid: String) {

            txt.text = data.data

            if(data.ubi!= null){
                linear.setBackgroundColor(Color.parseColor("#FF6BE8AE"))
                img.visibility = View.VISIBLE
                linear.isClickable = true
                linear.isFocusable = true
            } else {
                img.visibility = View.GONE
                linear.isClickable = false
                linear.isFocusable = false
            }

            if (data.sender != uid) {
                linearParent.gravity = Gravity.START
            } else {
                linearParent.gravity = Gravity.END
            }

        }

    }

}