package com.daisa.kreader.activity

import android.os.Bundle
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.daisa.kreader.QRApplication
import com.daisa.kreader.adapter.HistoryAdapter
import com.daisa.kreader.databinding.ActivityHistoryBinding
import com.daisa.kreader.db.entity.Code
import com.daisa.kreader.db.viewmodel.CodeViewModel
import com.daisa.kreader.db.viewmodel.CodeViewModelFactory

class HistoryActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityHistoryBinding

    private lateinit var listView: ListView

    private val codeViewModel: CodeViewModel by viewModels {
        CodeViewModelFactory((application as QRApplication).repository)
    }

    private val elements = ArrayList<Code>()
    private lateinit var adapter : HistoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewBinding = ActivityHistoryBinding.inflate(layoutInflater)

        setContentView(viewBinding.root)

        listView = viewBinding.list

        adapter =  HistoryAdapter(this, elements)

        listView.adapter = adapter

        codeViewModel.allCodes.observe(this) { codes ->
            codes?.let {
                elements.clear()
                elements.addAll(codes)
                adapter.notifyDataSetChanged()
            }
        }
    }
}