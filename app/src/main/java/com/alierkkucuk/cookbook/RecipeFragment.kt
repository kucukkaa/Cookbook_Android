package com.alierkkucuk.cookbook

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
import java.io.OutputStream
import kotlin.Exception


class RecipeFragment : Fragment() {

    var choosenImageUri : Uri? = null
    var choosenBitMap : Bitmap? = null

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
        BtnAdd.setOnClickListener {
            addRecipe(it)
        }

        iVFoodImg.setOnClickListener {
            chooseImage(it)
        }

        arguments?.let{
            var takenInfo = RecipeFragmentArgs.fromBundle(it).info
            if(takenInfo.equals("frommenu")){
                eTFoodName.setText("")
                eTFoodIngredients.setText("")
                BtnAdd.visibility = View.VISIBLE

                val imageChooseDefault =BitmapFactory.decodeResource(context?.resources, R.drawable.defaultclick)
                iVFoodImg.setImageBitmap(imageChooseDefault)
            } else {
                BtnAdd.visibility = View.INVISIBLE

                val choosenId = RecipeFragmentArgs.fromBundle(it).id

                context?.let{
                    try {
                        val db = it.openOrCreateDatabase("FoodDb",Context.MODE_PRIVATE,null)
                        val cursor = db.rawQuery("SELECT * FROM fooddb WHERE id = ?", arrayOf(choosenId.toString()))

                        val foodNameIndex = cursor.getColumnIndex("name")
                        val foodIngredients = cursor.getColumnIndex("ingredients")
                        val foodImage = cursor.getColumnIndex("image")

                        while(cursor.moveToNext()){
                            eTFoodName.setText(cursor.getString(foodNameIndex))
                            eTFoodIngredients.setText(cursor.getString(foodIngredients))

                            val byteArray = cursor.getBlob(foodImage)
                            val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

                            iVFoodImg.setImageBitmap(bitmap)
                        }
                        cursor.close()


                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    fun addRecipe(view: View){
        val foodName = eTFoodName.text.toString()
        val foodIngredients = eTFoodIngredients.text.toString()

        if (choosenBitMap != null){
            val choosenBitMap = createSmallSizeBitmap(choosenBitMap!!, 300)
            val outputStream = ByteArrayOutputStream()
            choosenBitMap.compress(Bitmap.CompressFormat.PNG, 50, outputStream)
            val imageByteArray = outputStream.toByteArray()

            try{
                context?.let {
                    val database = it.openOrCreateDatabase("FoodDb", Context.MODE_PRIVATE, null)
                    database.execSQL("CREATE TABLE IF NOT EXISTS fooddb (id INTEGER PRIMARY KEY, name VARCHAR, ingredients VARCHAR, image BLOB)")

                    val sqlString = "INSERT INTO fooddb (name, ingredients, image) VALUES (?, ?, ?)"
                    val statement = database.compileStatement(sqlString)
                    statement.bindString(1, foodName)
                    statement.bindString(2, foodIngredients)
                    statement.bindBlob(3, imageByteArray)
                    statement.execute()
                }


            } catch (e:Exception){
                e.printStackTrace()
            }

            val action = RecipeFragmentDirections.actionRecipeFragmentToListFragment()
            Navigation.findNavController(view).navigate(action)
        }
    }

    fun chooseImage(view: View){
        activity?.let {
            if(ContextCompat.checkSelfPermission(it.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
            } else {
                val galeryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent,2)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1){
            if(grantResults.size >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                val galeryIntent = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(galeryIntent,2)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == 2 && resultCode == Activity.RESULT_OK && data != null){

            choosenImageUri = data.data

            try{
                context?.let {
                    if(choosenImageUri != null){
                        if(Build.VERSION.SDK_INT >= 28){
                            val source = ImageDecoder.createSource(it.contentResolver, choosenImageUri!!)
                            choosenBitMap = ImageDecoder.decodeBitmap(source)
                            iVFoodImg.setImageBitmap(choosenBitMap)
                        } else {
                            choosenBitMap = MediaStore.Images.Media.getBitmap(it.contentResolver, choosenImageUri)
                            iVFoodImg.setImageBitmap(choosenBitMap)
                        }

                    }
                }




            } catch (e: Exception){
                e.printStackTrace()
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun createSmallSizeBitmap (choosenBitmap: Bitmap, maximumSize: Int) : Bitmap {
        var width = choosenBitmap.width
        var height = choosenBitmap.height

        val scale : Double = width.toDouble() / height.toDouble()

        if (scale>1){
            width = maximumSize
            height = (width / scale.toInt()).toInt()
        } else {
            height = maximumSize
            width = (height * scale).toInt()
        }

        return Bitmap.createScaledBitmap(choosenBitmap, width, height, true)
    }

}