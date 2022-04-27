package com.example.navigationtest.rankings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.navigationtest.Data.AppDatabase
import com.example.navigationtest.Data.LeaderBoards
import com.example.navigationtest.Data.LeaderBoardsDAO
import com.example.navigationtest.R
import kotlinx.android.synthetic.main.device_ranking_fragment.view.*
import java.util.ArrayList

class DeviceRankFragment:Fragment() {
    var recyclerView: RecyclerView?=null
    private var data_array:MutableList<LeaderBoards> = ArrayList()
    private lateinit var entryDao: LeaderBoardsDAO



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?{

        val db= Room.databaseBuilder(
            requireContext(), AppDatabase::class.java,"ranking3.db"
        ).allowMainThreadQueries().build()
        entryDao=db.LeaderBoardsDAO()



        val view: View = inflater.inflate(R.layout.device_ranking_fragment, container, false)
        recyclerView = view.rank_r_view
        recyclerView?.setLayoutManager(LinearLayoutManager(context))


        val entries:List<LeaderBoards> =entryDao.getLeaderboard()

        data_array.addAll(entries)
        data_array.sortByDescending { it.result }

        recyclerView?.adapter= RecyclerAdapter(data_array,entryDao)

        view.back_button.setOnClickListener {
            Navigation.findNavController(view).navigate(R.id.action_deviceRankFragment_to_fragment_one)
        }

        return view
    }

}