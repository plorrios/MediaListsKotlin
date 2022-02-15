package com.plorrios.medialists.ui.ListManagement.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.plorrios.medialists.R
import com.plorrios.medialists.ui.games.Objects.Game

class ListAdapter (private val cDataSet: ArrayList<String>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    var onItemClick: ((String) -> Unit)? = null
    var onItemLongClick: ((String) -> Unit)? = null

    var dataSet = cDataSet


    val TYPE_AD = 0
    val TYPE_CONTENT = 1

    val AdPeriod = 8

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView?
        val adView : AdView?

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet.get(adapterPosition - adapterPosition/AdPeriod))
            }
            itemView.setOnLongClickListener {
                onItemLongClick?.invoke(dataSet.get(adapterPosition - adapterPosition/AdPeriod))

                true
            }
            textView = view.findViewById(R.id.textView)
            adView = view.findViewById(R.id.adViewItem)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        if (viewType == TYPE_CONTENT) {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.list_item, viewGroup, false)
            return ViewHolder(view)
        }else {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_ad, viewGroup, false)
            return ViewHolder(view)
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        if (viewHolder.itemViewType == TYPE_CONTENT) {
            viewHolder.textView?.text = dataSet[position - position / AdPeriod]
        } else {
            val adRequest = AdRequest.Builder().build()
            viewHolder.adView?.loadAd(adRequest)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size + dataSet.size/AdPeriod


    override fun getItemViewType(position: Int): Int {
        if (position % AdPeriod == 0 && position!=0){
            return TYPE_AD
        }else {
            return TYPE_CONTENT
        }

    }

    fun remove(item : String){
        var position = dataSet.indexOf(item)
        position += position / AdPeriod
        dataSet.remove(item)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, getItemCount() - position);
    }

}