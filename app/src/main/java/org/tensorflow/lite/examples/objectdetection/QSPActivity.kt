/*
 * Copyright 2022 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.tensorflow.lite.examples.objectdetection

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.solver.state.State.Constraint
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.JsonElement
import org.tensorflow.lite.examples.objectdetection.api.ApiInterface
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.Currency
import kotlin.concurrent.thread

/**
 * Main entry point into our app. This app follows the single-activity pattern, and all
 * functionality is implemented in the form of fragments.
 */
class QSPActivity : AppCompatActivity() {

    companion object {
        const val KEY_AUTOMATIC_REDIRECT = "KEY_AUTOMATIC_REDIRECT"
        const val KEY_MANUAL_REDIRECT = "KEY_MANUAL_REDIRECT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qsp)

        val isComingBack = intent.getBooleanExtra(ResultsActivity.KEY_PRESSED_BACK, false)
        val spinnerOverlay: ConstraintLayout = findViewById(R.id.spinnerOverlay)

        var totalContentsSum = 0
        var finishedItems = 1
        val tv_totalcontents = findViewById<TextView>(R.id.tv_totalcontents)

        if (!isComingBack) {
            OverlayView.resultsList.forEachIndexed { index, element ->
                getDataFromSerp(element.name, object : ResponseCallbacks {
                    override fun onSuccess(avg: Int?) {
                        if (avg != null) {
                            OverlayView.resultsList[index].price = avg
                            totalContentsSum += avg

                            val format: NumberFormat = NumberFormat.getCurrencyInstance()
                            format.setMaximumFractionDigits(0)
                            format.setCurrency(Currency.getInstance("AUD"))

                            tv_totalcontents.text = format.format(totalContentsSum)
                            finishedItems += 1

                            println(finishedItems)
                            println(OverlayView.resultsList.size)
                            if ((finishedItems+1).equals(OverlayView.resultsList.size)) {
                                println("here")
                                changePage(isComingBack)
                                spinnerOverlay.setVisibility(View.INVISIBLE)
                            }
                        }
                    }
                })
            }
        } else {
            var sum = 0
            OverlayView.resultsList.forEachIndexed { index, element ->
                sum += element.price
                val format: NumberFormat = NumberFormat.getCurrencyInstance()
                format.setMaximumFractionDigits(0)
                format.setCurrency(Currency.getInstance("AUD"))

                tv_totalcontents.text = format.format(sum)
            }
            spinnerOverlay.setVisibility(View.INVISIBLE)
        }

        val bt_getcontentsdetails = findViewById<Button>(R.id.bt_getcontentsdetails)
        bt_getcontentsdetails.setOnClickListener {
            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra(KEY_MANUAL_REDIRECT, true)
            }
            startActivity(intent)
        }
    }

    fun changePage(isComingBack: Boolean) {
        if (!isComingBack) {
            val intent = Intent(this, ResultsActivity::class.java).apply {
                putExtra(KEY_AUTOMATIC_REDIRECT, true)
            }
            startActivity(intent)
        }
    }
    private fun getDataFromSerp(item: String, responseCallbacks: ResponseCallbacks) {
        val retrofitBuilder = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://serpapi.com/")
            .build()

        val jsonPlaceHolder = retrofitBuilder.create(ApiInterface::class.java)

        val call = jsonPlaceHolder.getSerpApi(
            "google_shopping",
            item,
            "Sydney, New South Wales, Australia",
            "en",
            "us",
            "94aa21c6f1ba8cbb2cb58fbaf470d4a00845add206024048432cd9b413e9de50"
        )

        call.enqueue(object : Callback<JsonElement> {
            override fun onResponse(call: Call<JsonElement>, response: Response<JsonElement>) {
                val responseObject = response.body()?.asJsonObject?.get("shopping_results")?.asJsonArray
                var sum: Int = 0
                if (responseObject != null) {
                    for (i in 0 until responseObject.size()) {
                        val price = responseObject[i].asJsonObject.get("extracted_price").asInt
                        sum += price
                    }
                    val priceAvg = sum / responseObject.size()
                    responseCallbacks.onSuccess(priceAvg)
                }
            }

            override fun onFailure(call: Call<JsonElement>, t: Throwable) {
                Log.e("TAG", "onFailure: " + t.message)
            }
        })
    }

    interface ResponseCallbacks {
        fun onSuccess(avg: Int?)
    }
}
