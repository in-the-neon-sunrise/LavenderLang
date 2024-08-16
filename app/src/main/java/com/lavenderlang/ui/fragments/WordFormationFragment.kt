package com.lavenderlang.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.lavenderlang.R
import com.lavenderlang.backend.dao.rule.WordFormationRuleDao
import com.lavenderlang.backend.dao.rule.WordFormationRuleDaoImpl
import com.lavenderlang.domain.model.rule.WordFormationRuleEntity
import com.lavenderlang.databinding.FragmentWordFormationBinding
import com.lavenderlang.ui.MyApp

class WordFormationFragment : Fragment() {
    private lateinit var binding: FragmentWordFormationBinding
    companion object{
        var idLang: Int = 0
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentWordFormationBinding.inflate(inflater, container, false)

        //top navigation menu
        binding.buttonPrev.setOnClickListener {
            findNavController().popBackStack()
        }
        binding.buttonInf.setOnClickListener{
            val argsToSend = Bundle()
            argsToSend.putInt("block", 9)
            findNavController().navigate(
                R.id.action_wordFormationFragment_to_instructionFragment,
                argsToSend
            )
        }

        //bottom navigation menu
        binding.buttonHome.setOnClickListener {
            findNavController().navigate(R.id.action_wordFormationFragment_to_mainFragment)
        }

        binding.buttonLanguage.setOnClickListener {
            findNavController().navigate(R.id.action_wordFormationFragment_to_languageFragment)
        }

        binding.buttonTranslator.setOnClickListener {
            findNavController().navigate(R.id.action_wordFormationFragment_to_translatorFragment)
        }
        //how it was started?
        when (val lang = requireContext().getSharedPreferences("pref", Context.MODE_PRIVATE).getInt("lang", -1)) {
            -1 -> {
                findNavController().navigate(R.id.action_wordFormationFragment_to_languageFragment)
            }

            else -> {
                idLang = lang
            }
        }
        //button new grammar rule listener
        binding.buttonNewWordFormationRule.setOnClickListener {
            findNavController().navigate(R.id.action_wordFormationFragment_to_wordFormationRuleFragment)
        }

        //list of rules
        val adapterWordFormationRules: ArrayAdapter<WordFormationRuleEntity> = WordFormationRuleAdapter(requireContext(), MyApp.language!!.grammar.wordFormationRules.toMutableList())
        binding.listViewWordFormationRules.adapter = adapterWordFormationRules
        adapterWordFormationRules.notifyDataSetChanged()

        //click listener
        binding.listViewWordFormationRules.onItemClickListener =
            AdapterView.OnItemClickListener { parent, itemClicked, position, id ->
                val argsToSend = Bundle()
                argsToSend.putInt("rule", -1)
                findNavController().navigate(
                    R.id.action_wordFormationFragment_to_wordFormationRuleFragment,
                    argsToSend)
            }
        return binding.root
    }
}
private class WordFormationRuleAdapter(context: Context, listOfRules: MutableList<WordFormationRuleEntity>) :
    ArrayAdapter<WordFormationRuleEntity>(context,
        R.layout.word_formation_rule_line_activity, listOfRules) {
    companion object{
        val wordFormationRuleDao: WordFormationRuleDao = WordFormationRuleDaoImpl()
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        var newView = convertView
        val wordFormationRule: WordFormationRuleEntity? = getItem(position)
        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.word_formation_rule_line_activity, null)
        }

        //textview is visible
        val unchangeableAttributes: TextView = newView!!.findViewById(R.id.textViewUnchangeableAttributes)
        val changeableAttributes: TextView = newView!!.findViewById(R.id.textViewChangeableAttributes)
        unchangeableAttributes.text = wordFormationRuleDao.getOrigInfo(wordFormationRule!!)
        changeableAttributes.text = wordFormationRuleDao.getResultInfo(wordFormationRule!!)

        return newView
    }
}