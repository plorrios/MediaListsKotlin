package com.plorrios.medialists.ui.books.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.plorrios.medialists.R
import com.plorrios.medialists.ui.TV.Objects.TVObjects
import com.plorrios.medialists.ui.books.Objects.Book
import com.plorrios.medialists.ui.games.Adapters.GamesListAdapter
import com.plorrios.medialists.ui.games.Objects.Game

class BooksListAdapter (initialData: ArrayList<Book>) :
    RecyclerView.Adapter<BooksListAdapter.ViewHolder>() {

    var dataSet: MutableList<Book> = initialData.toMutableList()

    var onItemClick: ((Book) -> Unit)? = null

    val TYPE_AD = 0
    val TYPE_CONTENT = 1

    val AdPeriod = 8

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView?
        val adView : AdView?

        init {
            itemView.setOnClickListener {
                onItemClick?.invoke(dataSet.get(adapterPosition - adapterPosition/AdPeriod))
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
                .inflate(R.layout.book_list_item, viewGroup, false)
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
            viewHolder.textView?.text = dataSet[position - position / AdPeriod].getInfo().getTitle()
        }else {
            val adRequest = AdRequest.Builder().build()
            viewHolder.adView?.loadAd(adRequest)
        }    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size + dataSet.size/AdPeriod

    fun getRealSize() = dataSet.size

    override fun getItemViewType(position: Int): Int {
        if (position % AdPeriod == 0 && position!=0){
            return TYPE_AD
        }else {
            return TYPE_CONTENT
        }

    }

    fun addData(list: ArrayList<Book>){
        dataSet.addAll(list)
    }
    fun setData(list: ArrayList<Book>){
        dataSet.clear()
        dataSet.addAll(list)
    }

}