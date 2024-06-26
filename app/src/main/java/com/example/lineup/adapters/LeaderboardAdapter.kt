package com.example.lineup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.gdsc.lineup2024.R
import com.example.lineup.models.LeaderboardModel

class LeaderboardAdapter(
    val context: android.content.Context,
    private val list: List<LeaderboardModel>
) : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: ImageView = itemView.findViewById(R.id.player_avatar)
        val name: TextView = itemView.findViewById(R.id.leaderboard_name)
        val membersFound: TextView = itemView.findViewById(R.id.leaderboard_score)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val view = LayoutInflater.from(context).inflate(R.layout.leaderboard_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val leaderboardList = list[position]
        holder.avatar.setImageResource(leaderboardList.avatar)
        holder.name.text = leaderboardList.name
        holder.membersFound.text = leaderboardList.membersFound.toString()
        holder.itemView.setOnClickListener {
            Toast.makeText(context, leaderboardList.name,Toast.LENGTH_SHORT).show()
        }
    }
}