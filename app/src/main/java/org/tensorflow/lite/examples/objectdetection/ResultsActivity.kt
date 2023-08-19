package org.tensorflow.lite.examples.objectdetection

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        val table = findViewById<TableLayout>(R.id.tblObjects)
        for (result in OverlayView.resultsList) {
            val row = layoutInflater.inflate(R.layout.obj_tbl_row, null) as TableRow;
            (row.findViewById<View>(R.id.attrib_name) as TextView).text = result.name
            (row.findViewById<View>(R.id.attrib_value) as TextView).text = result.score.toString()
            table.addView(row)
        }
        table.requestLayout()
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
