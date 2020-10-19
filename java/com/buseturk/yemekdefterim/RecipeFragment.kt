package com.buseturk.yemekdefterim

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import kotlinx.android.synthetic.main.fragment_recipe.*
import java.io.ByteArrayOutputStream


class RecipeFragment : Fragment() {
var selectedImage : Uri?=null
var selectedBitmap : Bitmap?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        saveButton.setOnClickListener {
            save(it)
        }

        imageView.setOnClickListener {
            chooseImage(it)
        }

        arguments?.let {

            var comingInfo=RecipeFragmentArgs.fromBundle(it).info

            if(comingInfo.equals("fromMenu")){

                mealMaterialsText.setText("")
                mealNameText.setText("")

                saveButton.visibility=View.VISIBLE

                val chooseImage=BitmapFactory.decodeResource(context?.resources,R.drawable.image_selection)
                imageView.setImageBitmap(chooseImage)

            }

            else{

                saveButton.visibility=View.INVISIBLE

                var chooseId=RecipeFragmentArgs.fromBundle(it).id


                context?.let {

                    try {

                        val db=it.openOrCreateDatabase("Meals",Context.MODE_PRIVATE,null)
                        val cursor=db.rawQuery("SELECT * FROM meals WHERE id=?", arrayOf(chooseId.toString()))

                        val mealNameIndex =cursor.getColumnIndex("mealname")
                        val mealMaterialsIndex=cursor.getColumnIndex("mealmaterials")
                        val mealImageIndex=cursor.getColumnIndex("image")

                        while (cursor.moveToNext()){

                            mealMaterialsText.setText(cursor.getString(mealMaterialsIndex))
                            mealNameText.setText(cursor.getString(mealNameIndex))

                            val byteArray=cursor.getBlob(mealImageIndex)
                            val bitmap=BitmapFactory.decodeByteArray(byteArray,0,byteArray.size)
                            imageView.setImageBitmap(bitmap)
                        }


                        cursor.close()




                    }

                    catch (e :Exception){
                        e.printStackTrace()
                    }
                }


            }




        }




    }



    fun save(view : View){

        val mealName=mealNameText.text.toString()
        val mealMaterials=mealMaterialsText.text.toString()

        if (selectedBitmap !=null){

            val smallBitmap=createSmallBitmap(selectedBitmap!!,300)

            val outputStream=ByteArrayOutputStream()
            smallBitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream)
            val byteArray=outputStream.toByteArray()

            try {
                context.let {
                    val database=it!!.openOrCreateDatabase("Meals",Context.MODE_PRIVATE,null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS meals(id INTEGER PRIMARY KEY, mealname VARCHAR , mealmaterials VARCHAR,image BLOB)")

                    val sqlString ="INSERT INTO meals(mealname,mealmaterials,image) VALUES(?,?,?)"
                    val statement=database.compileStatement(sqlString)
                    statement.bindString(1,mealName)
                    statement.bindString(2,mealMaterials)
                    statement.bindBlob(3,byteArray)
                    statement.execute()
            }



            }
            catch (e : Exception){
                e.printStackTrace()
            }

            val action =RecipeFragmentDirections.actionRecipeFragmentToRecipesListFragment()
            Navigation.findNavController(view).navigate(action)


        }


    }

    fun chooseImage(view : View){

        activity?.let {
            if (ContextCompat.checkSelfPermission(it.applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){

                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)

            }

            else
            {

                val galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)

            }

        }


    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        if (requestCode==1){

            if (grantResults.size>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){

                val galleryIntent=Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galleryIntent,2)
            }

        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode==2 && resultCode==Activity.RESULT_OK && data !=null){

            selectedImage=data.data

            try {

                context?.let {

                    if (selectedImage !=null){

                        if (Build.VERSION.SDK_INT>=28){

                            val source=ImageDecoder.createSource(it.contentResolver,selectedImage!!)
                            selectedBitmap=ImageDecoder.decodeBitmap(source)
                            imageView.setImageBitmap(selectedBitmap)


                        }
                        else{

                            selectedBitmap=MediaStore.Images.Media.getBitmap(it.contentResolver,selectedImage)
                            imageView.setImageBitmap(selectedBitmap)

                        }

                    }

                }

            }

            catch (e : Exception){
                e.printStackTrace()
            }

        }



        super.onActivityResult(requestCode, resultCode, data)
    }



    fun createSmallBitmap(userSelectedBitmap : Bitmap,maximumSize : Int) : Bitmap{

        var width=userSelectedBitmap.width
        var height=userSelectedBitmap.height

        val bitmapRate :Double =width.toDouble()/height.toDouble()

        if (bitmapRate>1){

            width=maximumSize
            val shortenedHeight=width/bitmapRate
            height=shortenedHeight.toInt()

        }

        else{

            height=maximumSize
            val shortenedWidth=width*bitmapRate
            width=shortenedWidth.toInt()

        }

        return Bitmap.createScaledBitmap(userSelectedBitmap,width,height,true)


    }



}