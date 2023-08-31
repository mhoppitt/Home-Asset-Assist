package org.tensorflow.lite.examples.objectdetection

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Currency


class ResultsActivity : AppCompatActivity() {

    companion object {
        const val KEY_HOME_INSURANCE_VISIBILITY = "KEY_HOME_INSURANCE_VISIBILITY"
        const val KEY_PRESSED_BACK = "KEY_PRESSED_BACK"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val automaticRedirect = intent.getBooleanExtra(QSPActivity.KEY_AUTOMATIC_REDIRECT, false)
        val manualRedirect = intent.getBooleanExtra(QSPActivity.KEY_MANUAL_REDIRECT, false)

        val spinnerOverlay: ConstraintLayout = findViewById(R.id.spinnerOverlay)

        if (automaticRedirect) {
            val intent = Intent(this, QSPActivity::class.java).apply {
                putExtra(KEY_PRESSED_BACK, true)
            }
            startActivity(intent)
            spinnerOverlay.setVisibility(View.INVISIBLE)
        }

        if (manualRedirect) {
            spinnerOverlay.setVisibility(View.INVISIBLE)
        }

        val customAdapter = CustomAdapter(OverlayView.resultsList)

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.setFocusable(false)
        recyclerView.setNestedScrollingEnabled(false)
        val linearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = customAdapter

        val btn_back = findViewById<ImageView>(R.id.img_back)
        btn_back.setOnClickListener {
            val intent = Intent(this, QSPActivity::class.java).apply {
                putExtra(KEY_PRESSED_BACK, true)
            }
            startActivity(intent)
        }
    }

    class CustomAdapter(private var dataSet: MutableList<OverlayView.ObjectDetected>) :
            RecyclerView.Adapter<CustomAdapter.ViewHolder>() {

        /**
         * Provide a reference to the type of views that you are using
         * (custom ViewHolder)
         */
        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val object_name: TextView
            val object_score: ProgressBar
            val object_price: EditText
            val btn_delete: ImageButton
            val btn_edit: ImageButton
            val btn_save: ImageButton
            val btn_cancel: ImageButton

            init {
                // Define click listener for the ViewHolder's View
                object_name = view.findViewById(R.id.object_name)
                object_score = view.findViewById(R.id.object_score)
                object_price = view.findViewById(R.id.object_price)
                btn_delete = view.findViewById(R.id.btn_delete)
                btn_edit = view.findViewById(R.id.btn_edit)
                btn_save = view.findViewById(R.id.btn_save)
                btn_cancel = view.findViewById(R.id.btn_cancel)
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

            val objectName = dataSet[position].name.split(" ")
                    .joinToString(separator = " ", transform = String::capitalize)

            val format: NumberFormat = NumberFormat.getCurrencyInstance()
            format.setMaximumFractionDigits(0)
            format.setCurrency(Currency.getInstance("AUD"))

            val objectPrice = dataSet[position].price
            viewHolder.object_price.setText(format.format(objectPrice))

            fun View.hideKeyboard() {
                val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(windowToken, 0)
            }

            fun View.showKeyboard() {
                val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
            }

            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.object_name.text = objectName
            viewHolder.object_score.progress = confidencePercent.toInt()

            println(OverlayView.resultsList)
            println(confidencePercent.toString())
            if (confidencePercent.toInt() < 55) {
                println("<55" + viewHolder.object_name.text + viewHolder.object_score.toString() + confidencePercent.toString())
                viewHolder.object_score.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#B80000")))
            } else if (confidencePercent.toInt() in 55..70) {
                println("in between" + viewHolder.object_name.text + viewHolder.object_score.toString() + confidencePercent.toString())
                viewHolder.object_score.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#DD4500")))
            }

            viewHolder.btn_delete.setOnClickListener {
                dataSet.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, dataSet.size)
            }

            viewHolder.btn_edit.setOnClickListener {
                viewHolder.object_price.isLongClickable()
                viewHolder.object_price.setCursorVisible(true)
                viewHolder.object_price.setFocusableInTouchMode(true)
                viewHolder.object_price.isFocusable()
                viewHolder.object_price.requestFocus()

                it.showKeyboard()

                viewHolder.btn_delete.visibility = View.INVISIBLE
                viewHolder.btn_edit.visibility = View.INVISIBLE
                viewHolder.btn_save.visibility = View.VISIBLE
                viewHolder.btn_cancel.visibility = View.VISIBLE

                viewHolder.object_price.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        s: CharSequence,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        if (viewHolder.object_price.getText().length == 0) {
                            viewHolder.object_price.setText("$")
                        }
                    }

                    override fun afterTextChanged(s: Editable) {}
                })
            }

            viewHolder.btn_cancel.setOnClickListener {
                viewHolder.object_price.setLongClickable(false)
                viewHolder.object_price.setCursorVisible(false)
                viewHolder.object_price.setFocusableInTouchMode(false)
                viewHolder.object_price.setFocusable(false)
                viewHolder.object_price.clearFocus()

                it.hideKeyboard()

                viewHolder.btn_delete.visibility = View.VISIBLE
                viewHolder.btn_edit.visibility = View.VISIBLE
                viewHolder.btn_save.visibility = View.INVISIBLE
                viewHolder.btn_cancel.visibility = View.INVISIBLE

                val format: NumberFormat = NumberFormat.getCurrencyInstance()
                format.setMaximumFractionDigits(0)
                format.setCurrency(Currency.getInstance("AUD"))

                viewHolder.object_price.setText(format.format(dataSet[position].price))
            }

            viewHolder.btn_save.setOnClickListener {
                viewHolder.object_price.setLongClickable(false)
                viewHolder.object_price.setCursorVisible(false)
                viewHolder.object_price.setFocusableInTouchMode(false)
                viewHolder.object_price.setFocusable(false)
                viewHolder.object_price.clearFocus()

                it.hideKeyboard()

                viewHolder.btn_delete.visibility = View.VISIBLE
                viewHolder.btn_edit.visibility = View.VISIBLE
                viewHolder.btn_save.visibility = View.INVISIBLE
                viewHolder.btn_cancel.visibility = View.INVISIBLE

                val format: NumberFormat = NumberFormat.getCurrencyInstance()
                format.setMaximumFractionDigits(0)
                format.setCurrency(Currency.getInstance("AUD"))

                val updatedPrice = viewHolder.object_price.text.toString().drop(1).replace(",", "").toInt()
                dataSet[position].price = updatedPrice
                viewHolder.object_price.setText(format.format(dataSet[position].price))
            }
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
            val intent = Intent(this, QSPActivity::class.java).apply {
                putExtra(KEY_PRESSED_BACK, true)
            }
            startActivity(intent)
            finish()
        }
    }
}
