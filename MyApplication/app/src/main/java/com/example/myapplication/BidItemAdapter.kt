package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BidItemAdapter(private val items: List<Products>) : RecyclerView.Adapter<BidItemAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvProductName: TextView = view.findViewById(R.id.tv_product_name)
        val tvBidPrice: TextView = view.findViewById(R.id.tv_bid_price)
        val tvBidStatus: TextView = view.findViewById(R.id.tv_bid_status)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvProductName.text = item.prodName
        holder.tvBidPrice.text = "입찰가: ${item.bidPrice}원"
        holder.tvBidStatus.text = when(item.bidStatus) {
            'Y' -> "낙찰"
            'N' -> "유찰"
            else -> "진행중"
        }
    }

    override fun getItemCount() = items.size
}