package com.example.whattsappclone

import android.app.DownloadManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.Query
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.core.Query
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_chats.*

class PeopleFragment : Fragment() {

    lateinit var mAdapter: FirestorePagingAdapter<User,UserViewHolder>
    val auth by lazy {
        FirebaseAuth.getInstance()
    }
    val database by lazy {
        FirebaseFirestore.getInstance().collection("users")
            .orderBy("name", com.google.firebase.firestore.Query.Direction.DESCENDING)
    }

    override fun onCreate(
            inflater:LayoutInflater,container:ViewGroup?,
            savedInstanceState:Bundle?
    ): View?{
        return inflater.inflate(R.layout.fragment_chats,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRecyclerView.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=mAdapter
        }
    }
}
