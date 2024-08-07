package com.example.auctionproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class BidItemAdapter(private val bidItems: List<Products>) : RecyclerView.Adapter<BidItemAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bid, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bidItem = bidItems[position]

        holder.prodName.text = bidItem.prodName
        holder.bidPrice.text = bidItem.bidPrice.toString()

        // 이미지 로드
        Glide.with(holder.itemView.context)
            .load("data:image/jpeg;base64,${bidItem.prodImgPath}")
            .into(holder.prodImg)
    }

    override fun getItemCount(): Int {
        return bidItems.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val prodName: TextView = itemView.findViewById(R.id.prod_name)
        val bidPrice: TextView = itemView.findViewById(R.id.bid_price)
        val prodImg: ImageView = itemView.findViewById(R.id.prod_img)
    }
}
