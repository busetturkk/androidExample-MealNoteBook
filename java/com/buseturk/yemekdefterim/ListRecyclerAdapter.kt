package com.buseturk.yemekdefterim

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recycler_row.view.*

class ListRecyclerAdapter(val foodList : ArrayList<String>, val idList : ArrayList<Int>) : RecyclerView.Adapter<ListRecyclerAdapter.FoodHolder>(){

    class FoodHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {

        val inflater=LayoutInflater.from(parent.context)
        val view =inflater.inflate(R.layout.recycler_row,parent,false)
        return FoodHolder(view)


    }

    override fun getItemCount(): Int {

        return foodList.size

    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {

        holder.itemView.recyclerRowText.text=foodList[position]
        holder.itemView.setOnClickListener {
            val action=RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment("fromRecycler",idList[position])
            Navigation.findNavController(it).navigate(action)
        }


    }

}