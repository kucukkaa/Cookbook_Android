package com.alierkkucuk.cookbook

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListRecyclerAdapter(val foodNameList: ArrayList<String>, val foodIdList: ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.FoodHolder>() {

    class FoodHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        var inflater = LayoutInflater.from(parent.context)
        var view = inflater.inflate(R.layout.recycler_row,parent, false)
        return FoodHolder(view)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.itemView.recycler_row_text.text= foodNameList[position]
        holder.itemView.setOnClickListener {
            val action = ListFragmentDirections.actionListFragmentToRecipeFragment("fromrecycler", 0)
        }
    }

    override fun getItemCount(): Int {
        return foodNameList.size
    }
}