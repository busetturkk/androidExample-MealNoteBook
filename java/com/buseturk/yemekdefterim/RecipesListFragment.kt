package com.buseturk.yemekdefterim

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_recipes_list.*


class RecipesListFragment : Fragment() {
var mealNameList=ArrayList<String>()
var mealIdList=ArrayList<Int>()
private lateinit var listAdapter:ListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listAdapter=ListRecyclerAdapter(mealNameList,mealIdList)
        recyclerView.layoutManager=LinearLayoutManager(context)
        recyclerView.adapter=listAdapter

        getSqlData()

    }

    fun getSqlData(){

        try {

            activity?.let {

                val database=it!!.openOrCreateDatabase("Meals",Context.MODE_PRIVATE,null)
                val cursor =database.rawQuery("SELECT * FROM meals",null)

                val mealIdIndex=cursor.getColumnIndex("id")
                val mealNameIndex=cursor.getColumnIndex("mealname")

                mealNameList.clear()
                mealIdList.clear()

                while (cursor.moveToNext()){

                    mealIdList.add(cursor.getInt(mealIdIndex))
                    mealNameList.add(cursor.getString(mealNameIndex))


                }


                listAdapter.notifyDataSetChanged()
                cursor.close()
            }



        }

        catch (e:Exception){
            e.printStackTrace()
        }


    }


}