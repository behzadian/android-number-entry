package number.entry.examples

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import farayan.commons.components.NumberEntry

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val FirstNumberEntry = findViewById<NumberEntry>(R.id.FirstNumberEntry);
        val SecondNumberEntry = findViewById<NumberEntry>(R.id.SecondNumberEntry);
        val consoleTextView = findViewById<TextView>(R.id.ConsoleTextView);
        FirstNumberEntry.doubleValue = 14.6001
        SecondNumberEntry.doubleValue = 14.0
    }
}