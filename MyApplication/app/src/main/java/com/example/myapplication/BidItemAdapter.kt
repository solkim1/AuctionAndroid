package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BidItemAdapter(private val bidItemList: List<Products>) : RecyclerView.Adapter<BidItemAdapter.BidItemViewHolder>() {

    class BidItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodName: TextView = itemView.findViewById(R.id.prodName)
        val bidPrice: TextView = itemView.findViewById(R.id.bidPrice)
        val immediatePrice: TextView = itemView.findViewById(R.id.immediatePrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bid, parent, false)
        return BidItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BidItemViewHolder, position: Int) {
        val currentItem = bidItemList[position]
        holder.prodName.text = currentItem.prodName
        holder.bidPrice.text = "현재 입찰 가격: ${currentItem.bidPrice}원"
        holder.immediatePrice.text = "즉시 입찰가: ${currentItem.immediatePrice}원"
    }

    override fun getItemCount() = bidItemList.size
}
