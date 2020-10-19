package com.buseturk.yemekdefterim

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.navigation.Navigation

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val menuInflater=menuInflater
        menuInflater.inflate(R.menu.recipe_add,menu)

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId==R.id.recipe_add_item){

            val action=RecipesListFragmentDirections.actionRecipesListFragmentToRecipeFragment("fromMenu",0)
            Navigation.findNavController(this,R.id.fragment).navigate(action)
        }

        return super.onOptionsItemSelected(item)


    }
}