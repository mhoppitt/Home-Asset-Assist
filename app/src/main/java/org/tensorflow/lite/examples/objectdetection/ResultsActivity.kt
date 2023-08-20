package org.tensorflow.lite.examples.objectdetection

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ResultsActivity : AppCompatActivity() {

    companion object {
        const val KEY_HOME_INSURANCE_VISIBILITY = "KEY_HOME_INSURANCE_VISIBILITY"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val customAdapter = CustomAdapter(OverlayView.resultsList)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = customAdapter

        val btn_buy = findViewById<Button>(R.id.button_get_quote)
        btn_buy.setOnClickListener {
            val intent = Intent(this, HomePageActivity::class.java).apply {
                putExtra(KEY_HOME_INSURANCE_VISIBILITY, View.VISIBLE)
            }
            startActivity(intent)
        }
    }

    class CustomAdapter(private val dataSet: MutableList<OverlayView.ObjectDetected>) :
            RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val object_name: TextView
            val object_score: TextView

            init {
                // Define click listener for the ViewHolder's View
                object_name = view.findViewById(R.id.object_name)
                object_score = view.findViewById(R.id.object_score)
            }
        }

        // Create new views (invoked by the layout manager)
        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            // Create a new view, which defines the UI of the list item
            val view = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.text_row_item, viewGroup, false)

            return ViewHolder(view)
        }

        // Replace the contents of a view (invoked by the layout manager)
        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            val confidencePercent = (dataSet[position].score) * 100
            var confidenceString = confidencePercent.toString()
            val confidenceStringShortened = if (confidenceString.length > 4) {
                confidenceString.substring(0,5) + "%"
            } else {
                confidenceString.substring(0) + "%"
            }

            val objectName = dataSet[position].name.split(" ")
                    .joinToString(separator = " ", transform = String::capitalize)

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.object_name.text = objectName
            viewHolder.object_score.text = "Confidence: " + confidenceStringShortened
        }

        // Return the size of your dataset (invoked by the layout manager)
        override fun getItemCount() = dataSet.size
    }

    override fun onBackPressed() {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
            // Workaround for Android Q memory leak issue in IRequestFinishCallback$Stub.
            // (https://issuetracker.google.com/issues/139738913)
            finishAfterTransition()
        } else {
            super.onBackPressed()
            startActivity(Intent(this, HomePageActivity::class.java))
            finish()
        }
    }
}
