package com.example.whattsappclone

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class UserViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){

    fun bind(user:User)=with(itemView){
        tvCount.isEnabled=false
        tvTime.isEnabled=false

        tvTitle.text=user.name
        tvSubtitle.text=user.status

        Picasso.get()
                .load(user.thumbImage)
                .placeholder(R.drawable.ic_default_avatar)
                .error(R.drawable.ic_default_avatar)
                .into(ivImageView)
    }
}