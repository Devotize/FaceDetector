package com.sychev.facedetector.ui.items

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.bold
import androidx.core.text.color
import com.sychev.facedetector.R
import com.sychev.facedetector.domain.Person
import com.xwray.groupie.GroupieViewHolder
import com.xwray.groupie.Item
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Delay
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageItem(private var person: Person? = null, private var message: String = ""): Item<GroupieViewHolder>() {

    private var messageTextView: TextView? = null
    private val listOfIcons:  ArrayList<ImageButton> = ArrayList()

    override fun bind(viewHolder: GroupieViewHolder, position: Int) {
        messageTextView = viewHolder.itemView.findViewById(R.id.message_text)
        val context = viewHolder.itemView.context
        val googleSearch: ImageButton = viewHolder.itemView.findViewById(R.id.google_button)
         val instagram: ImageButton = viewHolder.itemView.findViewById(R.id.instagram_button)
         val facebook: ImageButton = viewHolder.itemView.findViewById(R.id.facebook_icon)
         val kinopoisk: ImageButton = viewHolder.itemView.findViewById(R.id.kinopoisk_icon)
        listOfIcons.addAll(listOf(googleSearch, instagram, facebook, kinopoisk))
        hideIcons()
        person?.let { pr ->
            pr.googleSearch?.let{ url ->
                googleSearch.setOnClickListener {
                    openUrl(url, context)
                }
            }
            pr.instUrl?.let{ url ->
                instagram.setOnClickListener {
                    openUrl(url, context)
                }
            }
            pr.facebookUrl?.let{ url ->
                facebook.setOnClickListener {
                    openUrl(url, context)
                }
            }
            pr.kinopoiskUrl?.let{ url ->
                kinopoisk.setOnClickListener {
                    openUrl(url, context)
                }
            }
        }

        val string = SpannableStringBuilder()
                .append("$message ")
                .color(ContextCompat.getColor(viewHolder.itemView.context, R.color.blue)) {
                    bold {
                        append(
                            person?.name ?: ""
                        )
                    }
                }
        messageTextView?.text = string
        var isIconShown = false
        messageTextView?.setOnClickListener {
            if (isIconShown){
                hideIcons()
                isIconShown = false
            } else {
                showIcons()
                isIconShown = true
            }
        }
    }

    fun setPerson(person: Person){
        this.person = person
    }

    fun setMessage(message: String){
        this.message = message
    }

    fun getMessage() = message

    fun showIcons() {
        listOfIcons.forEach {
            it.visibility = View.VISIBLE
        }
    }

    fun hideIcons() {
        listOfIcons.forEach {
            it.visibility = View.GONE
        }
    }

    fun openUrl(url: String, context: Context) {
        context.startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                data = Uri.parse(url)
            }
        )
    }

    override fun getLayout() = R.layout.message_card

}











