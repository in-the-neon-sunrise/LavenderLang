package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import com.lavenderlang.domain.model.help.Attributes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.data.LanguageRepositoryImpl
import com.lavenderlang.domain.model.help.CharacteristicEntity
import com.lavenderlang.domain.model.language.GrammarEntity
import com.lavenderlang.domain.model.rule.GrammarRuleEntity
import com.lavenderlang.databinding.FragmentGrammarBinding
import com.lavenderlang.domain.getOrigInfo
import com.lavenderlang.domain.getResultInfo
import com.lavenderlang.ui.MyApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GrammarFragment : Fragment() {
    private lateinit var binding: FragmentGrammarBinding

    companion object {
        var idLang: Int = -1

        lateinit var grammar: GrammarEntity

        var isFirst: Boolean = false
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrammarBinding.inflate(inflater, container, false)


        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener {
            val argsToSend = Bundle()
            argsToSend.putInt("block", 8)
            findNavController().navigate(
                R.id.action_dictionaryFragment_to_instructionFragment,
                argsToSend
            )
        }
        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE)
            .getInt("lang", -1)) {
            -1 -> {
                Log.d("GrammarFragment", "goodbye!")
                findNavController().navigate(R.id.action_grammarFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
                grammar = MyApp.language!!.grammar
            }
        }
        Log.d("GrammarFragment", "idLang = $idLang")

        //button new grammar rule listener
        binding.buttonNewGrammarRule.setOnClickListener {
            findNavController().navigate(
                R.id.action_grammarFragment_to_grammarRuleFragment,
            )
        }

        //list of rules
        val adapterGrammarRules: ArrayAdapter<GrammarRuleEntity> =
            GrammarRuleAdapter(
                requireContext(),
                MyApp.language!!.grammar.grammarRules.toMutableList()
            )
        binding.listViewGrammarRules.adapter = adapterGrammarRules
        adapterGrammarRules.notifyDataSetChanged()

        //click listener
        binding.listViewGrammarRules.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val argsToSend = Bundle()
                argsToSend.putInt("rule", position)
                findNavController().navigate(
                    R.id.action_grammarFragment_to_grammarRuleFragment,
                    argsToSend
                )
            }

        return binding.root
    }
}
private class GrammarRuleAdapter(context: Context, listOfRules: MutableList<GrammarRuleEntity>) :
    ArrayAdapter<GrammarRuleEntity>(context, R.layout.grammar_rule_line_activity, listOfRules) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val grammarRule: GrammarRuleEntity? = getItem(position)
        if (newView == null) {
            newView =
                LayoutInflater.from(context).inflate(R.layout.grammar_rule_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView =
            newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = getOrigInfo(grammarRule!!)
        changeableAttributes.text = getResultInfo(grammarRule)

        return newView
    }
}