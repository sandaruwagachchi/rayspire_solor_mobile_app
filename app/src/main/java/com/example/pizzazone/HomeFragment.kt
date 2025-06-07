package com.example.pizzazone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    private var listener: OnHomeFragmentInteractionListener? = null

    interface OnHomeFragmentInteractionListener {
        fun onHomeHistoryButtonClicked()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnHomeFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException("$context must implement OnHomeFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val historyButton = view.findViewById<Button>(R.id.btnConfirm)
        historyButton.setOnClickListener {
            listener?.onHomeHistoryButtonClicked()
        }

        return view
    }
}


