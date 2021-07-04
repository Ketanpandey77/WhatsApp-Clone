package com.example.whattsappclone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.fragment.app.Fragment
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.firestore.paging.FirestorePagingAdapter
import com.firebase.ui.firestore.paging.FirestorePagingOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirestoreRegistrar
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

    override fun onCreateView(
            inflater:LayoutInflater,container:ViewGroup?,
            savedInstanceState:Bundle?
    ): View?{
        setAdapter()
        return inflater.inflate(R.layout.fragment_chats,container,false)
    }

    private fun setAdapter() {
        val config= PagedList.Config.Builder()
                .setPrefetchDistance(2)
                .setPageSize(10)
                .setEnablePlaceholders(false)
                .build()

        val options= FirestorePagingOptions.Builder<User>()
                .setLifecycleOwner(viewLifecycleOwner)
                .setQuery(database,config,User::class.java)
                .build()

        mAdapter=object: FirestorePagingAdapter<User, UserViewHolder>(options){
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
                val view=layoutInflater.inflate(R.layout.list_item,parent,false)
                return UserViewHolder(view)
            }

            override fun onBindViewHolder(holder: UserViewHolder, position: Int, model: User) {
                holder.bind(user=model)
            }

        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRecyclerView.apply {
            layoutManager=LinearLayoutManager(requireContext())
            adapter=mAdapter
        }
    }
}
