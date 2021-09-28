package com.alierkkucuk.cookbook

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_list.*
import java.util.ArrayList

class ListFragment : Fragment() {

    var foodNameList =ArrayList<String>()
    var foodIdList =ArrayList<Int>()
    private lateinit var listAdapter: ListRecyclerAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listAdapter = ListRecyclerAdapter(foodNameList, foodIdList)
        recycleView.layoutManager = LinearLayoutManager(context)
        recycleView.adapter = listAdapter

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    fun getData(){
        try{
            activity?.let{

                val database = it.openOrCreateDatabase("FoodDb", Context.MODE_PRIVATE, null)
                val cursor = database.rawQuery("SELECT * FROM fooddb", null)
                val foodNameIndex = cursor.getColumnIndex("name")
                val foodIdIndex = cursor.getColumnIndex("id")

                foodNameList.clear()
                foodIdList.clear()

                while (cursor.moveToNext()){
                    foodNameList.add(cursor.getString(foodNameIndex))
                    foodIdList.add(cursor.getInt(foodIdIndex))
                }
                listAdapter.notifyDataSetChanged()

                cursor.close()
            }

        } catch (e:Exception){

        }
    }

}