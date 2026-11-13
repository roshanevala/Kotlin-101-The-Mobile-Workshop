package workshop.studylab

import android.app.Activity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

class MainActivity : Activity() {
    private var completed = 0
    private lateinit var topic: EditText
    private lateinit var completedLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        applyWorkshopInsets(findViewById(R.id.screen_root))
        topic = findViewById(R.id.topic_input)
        completedLabel = findViewById(R.id.completed_label)
        completed = savedInstanceState?.getInt(COMPLETED_KEY, 0) ?: 0
        topic.setText(savedInstanceState?.getString(TOPIC_KEY) ?: getString(R.string.default_topic))
        renderCompleted()
        findViewById<Button>(R.id.complete_button).setOnClickListener {
            completed++
            renderCompleted()
        }
        findViewById<Button>(R.id.reset_button).setOnClickListener {
            completed = 0
            topic.setText(R.string.default_topic)
            renderCompleted()
        }
        findViewById<Button>(R.id.recreate_button).setOnClickListener { recreate() }
        traceLifecycle("onCreate saved=${savedInstanceState != null}")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TOPIC_KEY, topic.text.toString())
        outState.putInt(COMPLETED_KEY, completed)
        traceLifecycle("onSaveInstanceState")
    }

    override fun onStart() { super.onStart(); traceLifecycle("onStart") }
    override fun onResume() { super.onResume(); traceLifecycle("onResume") }
    override fun onPause() { super.onPause(); traceLifecycle("onPause") }
    override fun onStop() { super.onStop(); traceLifecycle("onStop") }
    override fun onDestroy() { super.onDestroy(); traceLifecycle("onDestroy") }

    private fun renderCompleted() {
        completedLabel.text = resources.getQuantityString(R.plurals.completed_sessions, completed, completed)
    }

    companion object {
        private const val TOPIC_KEY = "draft.topic"
        private const val COMPLETED_KEY = "draft.completed"
    }
}
