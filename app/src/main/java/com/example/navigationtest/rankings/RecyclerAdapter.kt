package com.example.navigationtest.rankings

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.navigationtest.Data.LeaderBoards
import com.example.navigationtest.Data.LeaderBoardsDAO
import com.example.navigationtest.R
import kotlinx.android.synthetic.main.list_item.view.*

class RecyclerAdapter(var data: List<LeaderBoards>, private val entryDao: LeaderBoardsDAO): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var rank: TextView
        var name: TextView
        var score: TextView

        init {
            rank=itemView.findViewById(R.id.rank)
            name=itemView.findViewById(R.id.name)
            score=itemView.findViewById(R.id.score)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context=parent.context
        val inflater = LayoutInflater.from(context)

        val LeadsView = inflater.inflate(R.layout.list_item, parent, false)
        return ViewHolder(LeadsView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val pos= data[position]
        val place =position+1
        var backgroundColor="#e7e7e7"

        if (position==0) {
            backgroundColor = "#ed4242"

        } else if(position ==1) {
            backgroundColor = "#ff7c84"
        }
        else if (position==2){
            backgroundColor="#ff977c"

        }
        holder.itemView.setBackgroundColor(Color.parseColor(backgroundColor))
        holder.itemView.rank.text = place.toString()
        holder.itemView.score.text = pos.result.toString()
        holder.itemView.findViewById<TextView>(R.id.name).text=pos.name

    }


    override fun getItemCount(): Int {
        return data.size
    }

}