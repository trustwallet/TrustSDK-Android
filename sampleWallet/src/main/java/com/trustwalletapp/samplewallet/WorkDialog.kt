package com.trustwalletapp.samplewallet

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_work.view.*

class WorkDialog : BottomSheetDialogFragment() {

    companion object {
        private const val BUNDLE_TITLE = "title"
        private const val BUNDLE_MESSAGE = "message"

        fun newInstance(title: String, message: String): WorkDialog {
            val args = Bundle(2)
            args.putString(BUNDLE_TITLE, title)
            args.putString(BUNDLE_MESSAGE, message)

            val result = WorkDialog()
            result.arguments = args

            return result
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val title = arguments?.getString(BUNDLE_TITLE)
        val message = arguments?.getString(BUNDLE_MESSAGE)

        val result = inflater.inflate(R.layout.fragment_work, container, false)

        title?.let { result.title.text = it }
        message?.let { result.message.text = it}

        return result
    }
}