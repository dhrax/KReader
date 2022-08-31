package com.daisa.kreader.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import com.daisa.kreader.R
import com.daisa.kreader.db.entity.Code

class HistoryAdapter(val context: Context, private val elements: ArrayList<Code>) : BaseAdapter() {


    internal class ViewHolder {
        var text: TextView? = null
        var date: TextView? = null
    }

    override fun getCount(): Int {
        return elements.size
    }

    override fun getItem(pos: Int): Any {
        return elements[pos]
    }

    override fun getItemId(pos: Int): Long {
        return pos.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder : ViewHolder?
        var view: View? = convertView

        if(view ==  null){
            view = LayoutInflater.from(context).inflate(R.layout.item_history, null)

            viewHolder = ViewHolder()
            view!!.tag = viewHolder
        }else{
            viewHolder = view.tag as ViewHolder?
        }

        viewHolder!!.text = detail(view, R.id.tvLinkText, elements[position].text)
        viewHolder.date = detail(view, R.id.tvScanDate, elements[position].date)

        return view
    }


    /**
     * [TextView] initialization.
     * @param v View to be initialized.
     * @param resId Id of the view.
     * @param text Text to be initialized with.
     * @return A [TextView] initialized.
     */
    private fun detail(v: View, resId: Int, text: String): TextView? {
        val tv = v.findViewById<View>(resId) as TextView
        tv.text = text
        return tv
    }


    companion object {
        private val CODES_COMPARATOR = object : DiffUtil.ItemCallback<Code>() {
            override fun areItemsTheSame(oldItem: Code, newItem: Code): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Code, newItem: Code): Boolean {
                return oldItem.text == newItem.text
            }
        }
    }
}