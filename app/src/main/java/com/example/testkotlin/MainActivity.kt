package com.example.testkotlin

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONObject

lateinit var editBin:EditText
lateinit var buttGO:Button
lateinit var scheme:TextView
lateinit var type:TextView
lateinit var prepaid:CheckBox
lateinit var bank:TextView
lateinit var number:TextView
lateinit var luhn:CheckBox
lateinit var country:TextView
lateinit var latitude:TextView
lateinit var longitude:TextView
lateinit var brand:TextView
lateinit var tetprep:TextView
lateinit var textluhn:TextView
lateinit var historyList:ListView
var historyArray = ArrayList<String>()

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editBin = findViewById(R.id.BINcode)
        buttGO = findViewById(R.id.buttGO)
        historyList = findViewById(R.id.historyList)

        editBin.setOnClickListener{

            historyList.visibility = if (historyList.visibility == View.VISIBLE){
                View.INVISIBLE
            } else{
                View.VISIBLE
            }
        }

        buttGO.setOnClickListener {
            if (editBin.text.length > 0){
                requesBIN(editBin.text.toString())
                historyList.visibility = View.INVISIBLE
                val target = editBin.text.toString() !in historyArray
                if (historyArray.size > 0){
                    if (target)
                    {
                        historyArray.add(editBin.text.toString())
                    }
                } else {
                    historyArray.add(editBin.text.toString())
                }
                val adapter = ArrayAdapter(this,R.layout.list_white_text,R.id.list_content, historyArray)
                historyList.adapter = adapter
            }

            historyList.setOnItemClickListener{ parent, _, position, _ ->
                val selectedItem = parent.getItemAtPosition(position).toString()
                editBin.setText(selectedItem.toString())

            }
            }
    }

    private fun requesBIN(BIN:String) {
        val url = "https://lookup.binlist.net/"+BIN
        val queue = Volley.newRequestQueue(applicationContext)
            var request = StringRequest(
                Request.Method.GET,
                url,
                {
                        result -> parseBINRequest(result)
                },
                {
                        error -> Log.d("MyLog", "Error: $error")
                }
            )
            queue.add(request)
    }

    private fun parseBINRequest(result:String){
        scheme = findViewById(R.id.scheme)
        type = findViewById(R.id.type)
        prepaid = findViewById(R.id.prepaid)
        bank = findViewById(R.id.bank)
        number = findViewById(R.id.number)
        luhn = findViewById(R.id.luhn)
        country = findViewById(R.id.country)
        latitude = findViewById(R.id.latitude)
        longitude = findViewById(R.id.longitude)
        brand = findViewById(R.id.brand)

        val jsonObject = JSONObject(result)
        try {
            scheme.setText(jsonObject.getString("scheme").toString())
        }catch (e: Exception){
            scheme.setText("-----------------")
        }
        try {
            type.setText(jsonObject.getString("type").toString())
        }catch (e: Exception){
            type.setText("-----------------")
        }
        try {
            brand.setText(jsonObject.getString("brand").toString())
        }catch (e: Exception){
            brand.setText("-----------------")
        }
        try {
            number.setText(jsonObject.getJSONObject("number").getString("length").toString())
        }catch (e: Exception){
            number.setText("-")
        }

        try {
            bank.setText("${jsonObject.getJSONObject("bank").getString("name")}\n,")

        }catch (e: Exception){
            bank.setText("-----------------")
        }

        try {
            bank.append("${jsonObject.getJSONObject("bank").getString("city").toString()}\n")
        }catch (e: Exception){ }

        try {
            bank.append("${jsonObject.getJSONObject("bank").getString("url")}\n")
        }catch (e: Exception){ }

        try {
            bank.append("${jsonObject.getJSONObject("bank").getString("phone")}")
        }catch (e: Exception){ }

        try {
            country.setText("${jsonObject.getJSONObject("country").getString("name").toString()}")
        }catch (e: Exception){
            country.setText("-----------------")
        }
        try {
            latitude.setText("${jsonObject.getJSONObject("country").getString("latitude").toString()}")
        }catch (e: Exception){
            latitude.setText("-")
        }

        try {
            longitude.setText("${jsonObject.getJSONObject("country").getString("longitude").toString()}")
        }catch (e: Exception){
            longitude.setText("-")
        }

        tetprep = findViewById(R.id.textprep)
        textluhn = findViewById(R.id.textluhn)
        try {
            if (jsonObject.getBoolean("prepaid")) {
                prepaid.setChecked(true)
            } else {
                prepaid.setChecked(false)
            }
            tetprep.setText("PREPAID")
        }catch (e: Exception){
            tetprep.setText("PREPAID(no info)")

        }

        try {
            if (jsonObject.getJSONObject("number").getBoolean("luhn")) {

                luhn.setChecked(true)
            } else {
                luhn.setChecked(false)
            }
            textluhn.setText("LUHN")
        }catch (e: Exception){

            textluhn.setText("LUHN(no info)")

        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}