package com.gabo.finder.common.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/***
 *   Generic base adapter that will allow us to adapt different rows with different View types in our
 *   RecyclerViews.
 *
 *   This way we will get headers, titles, images, different cards, etc , displayed in the same RecyclerView.
 */
open class BaseAdapter : RecyclerView.Adapter<BaseAdapter.BindingHolder>() {
    val rows = mutableListOf<Row<*>>()

    private val viewCreators = mutableMapOf<Class<*>, (LayoutInflater, ViewGroup) -> ViewBinding>()

    fun  addAll(rows : List<Row<*>>) {
        this.rows.addAll(rows)
        notifyDataSetChanged()
    }

    fun <T :ViewBinding> add(row : Row<T>){
        rows.add(row)
        notifyDataSetChanged()
    }

    fun clear(){
        rows.clear()
        notifyDataSetChanged()
    }

    val isEmpty : Boolean
        get() = rows.isEmpty()

    override fun getItemViewType(position: Int): Int {
        val row = rows[position]
        if(!viewCreators.containsKey(row::class.java)){
            viewCreators[row::class.java] = row::createBinding
        }
        return viewCreators.keys.indexOf(row::class.java)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingHolder {
        val key = viewCreators.keys.elementAt(viewType)
        val binding = viewCreators[key]!!.invoke(LayoutInflater.from(parent.context), parent)
        return BindingHolder(binding)
    }

    override fun getItemCount(): Int {
        return rows.size
    }

    @Suppress("UNCHECKED_CAST")
    override fun onBindViewHolder(holder: BindingHolder, position: Int) {
        val row = rows[position] as Row<in ViewBinding>
        row.bind(holder.binding)
    }

    class BindingHolder(val binding : ViewBinding) : RecyclerView.ViewHolder(binding.root)
}

