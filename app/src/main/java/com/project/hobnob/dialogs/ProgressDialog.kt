package com.project.hobnob.dialogs

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.RoundRectShape
import android.widget.ProgressBar
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import com.project.hobnob.R


class ProgressDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)
    fun show() {
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }

    init {
        val progressBar = ProgressBar(context)
        progressBar.indeterminateTintList =
            AppCompatResources.getColorStateList(context, R.color.bgSecondary)
        dialog.setContentView(progressBar)
        val r = 24f
        val p = 20
        val shape = ShapeDrawable(RoundRectShape(floatArrayOf(r, r, r, r, r, r, r, r), null, null))
        shape.paint.color = ContextCompat.getColor(context, R.color.textPrimary)
        shape.setPadding(p, p, p, p)

        dialog.window?.setBackgroundDrawable(shape)
        dialog.setCancelable(false)
    }
}