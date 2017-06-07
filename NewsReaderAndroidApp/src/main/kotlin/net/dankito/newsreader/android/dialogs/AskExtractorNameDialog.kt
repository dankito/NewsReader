package net.dankito.newsreader.android.dialogs

import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.widget.EditText
import kotlinx.android.synthetic.main.dialog_ask_extractor_name.*
import net.dankito.newsreader.R


class AskExtractorNameDialog {

    fun askForName(context: Context, currentName: String, showCancelButton: Boolean, callback: (didSelectName: Boolean, selectedName: String?) -> Unit) {
        val builder = AlertDialog.Builder(context)
        builder.setView(R.layout.dialog_ask_extractor_name)

        var input: EditText? = null

        if(showCancelButton) {
            builder.setNegativeButton(android.R.string.cancel, DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
                callback(false, null)
            })
        }

        builder.setPositiveButton(android.R.string.ok, DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            callback(true, input?.text.toString())
        })

        val dialog = builder.create()
        dialog.show()

        input = dialog.edtxtAskExtractorName
        input.setText(currentName)
        input.selectAll()
        input.requestFocus()
    }
}