package com.lavenderlang

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class TopMenuFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    /*override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_top_menu, container, false)

        //top navigation menu
        /*val buttonPrev: Button = view.findViewById(R.id.buttonPrev)
        buttonPrev.setOnClickListener {
            val intent = Intent(view.context, MainActivity::class.java)
            startActivity(intent)
        }
        val buttonInformation: Button = view.findViewById(R.id.buttonInf)
        buttonInformation.setOnClickListener{
            val intent = Intent(view.context, InformationActivity::class.java)
            intent.putExtra("lang", LanguageActivity.id_lang)
            startActivity(intent)
        }*/
        return view
    }*/

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_menu, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() = TopMenuFragment()
    }
}