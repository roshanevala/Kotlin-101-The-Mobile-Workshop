package workshop.studylab

import android.app.Activity
import android.os.Process
import android.util.Log
import android.view.View
import android.view.WindowInsets

// Fixed window scaffolding, not part of the Bundle mission. minSdk is 30.
fun applyWorkshopInsets(root: View) {
    val left = root.paddingLeft
    val top = root.paddingTop
    val right = root.paddingRight
    val bottom = root.paddingBottom
    root.setOnApplyWindowInsetsListener { view, windowInsets ->
        val safe = windowInsets.getInsets(
            WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout() or WindowInsets.Type.ime()
        )
        view.setPadding(left + safe.left, top + safe.top, right + safe.right, bottom + safe.bottom)
        windowInsets
    }
    root.requestApplyInsets()
}

fun Activity.traceLifecycle(event: String) {
    Log.d("StudyLifecycle", "pid=${Process.myPid()} activity=${System.identityHashCode(this)} $event")
}
