package com.plorrios.medialists.ui.TV.Adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.plorrios.medialists.MainActivity
import com.plorrios.medialists.R
import com.plorrios.medialists.ui.TV.Objects.TVObjects

class TVListAdapter (initialData: ArrayList<TVObjects>):
    RecyclerView.Adapter<TVListAdapter.TVListAdapterViewHolder>() {

    val dataSet : MutableList<TVObjects> = initialData.toMutableList()
    var onItemClick: ((TVObjects) -> Unit)? = null

    val AdPeriod = 8


    //--------onCreateViewHolder: inflate layout with view holder-------
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TVListAdapterViewHolder {

        val layout = when (viewType) {
            TYPE_MOVIE -> R.layout.movie_list_item
            TYPE_SERIES -> R.layout.series_list_item
            TYPE_AD -> R.layout.item_ad
            else -> throw IllegalArgumentException("Invalid view type")
        }

        val view = LayoutInflater
            .from(parent.context)
            .inflate(layout, parent, false)

        return TVListAdapterViewHolder(view)
    }


    //-----------onBindViewHolder: bind view with data model---------
    override fun onBindViewHolder(holder: TVListAdapterViewHolder, position: Int) {
        if (holder.itemViewType == TYPE_AD){
            holder.bindAd()
        }else {
            holder.bind(dataSet[position - position/AdPeriod])
        }
    }

    override fun getItemCount() = dataSet.size + dataSet.size/AdPeriod


    override fun getItemViewType(position: Int): Int {
        if (position % AdPeriod == 0 && position!=0){
            return TYPE_AD
        }

        return when (dataSet[position - position/AdPeriod]) {
            is TVObjects.Movie -> TYPE_MOVIE
            is TVObjects.Series -> TYPE_SERIES
        }
    }

    fun setData(data: ArrayList<TVObjects>) {
        dataSet.apply {
            clear()
            addAll(data)
        }
    }

    fun addData(data: ArrayList<TVObjects>) {
        dataSet.apply {
            addAll(data)
        }
    }

    companion object {
        private const val TYPE_MOVIE = 0
        private const val TYPE_SERIES = 1
        private const val TYPE_AD = 2

    }

    inner class TVListAdapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet[adapterPosition - adapterPosition/AdPeriod])
            }
        }

        private fun bindMovie(item: TVObjects.Movie) {
            //Do your view assignment here from the data model

            itemView.findViewById<TextView>(R.id.textView)?.text = item.title
        }

        private fun bindSeries(item: TVObjects.Series) {
            //Do your view assignment here from the data model
            itemView.findViewById<TextView>(R.id.textView)?.text = item.name
        }

        fun bindAd() {
            val adRequest = AdRequest.Builder().build()
            itemView.findViewById<AdView>(R.id.adViewItem).loadAd(adRequest)
        }

        fun bind(dataModel: TVObjects) {
            when (dataModel) {
                is TVObjects.Movie -> bindMovie(dataModel)
                is TVObjects.Series -> bindSeries(dataModel)
                //else -> bindAd(dataModel)
            }
        }
    }

}