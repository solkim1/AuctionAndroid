package com.example.auctionproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.Locale

class BidItemAdapter(private val bidItems: ArrayList<BidItem>) : RecyclerView.Adapter<BidItemAdapter.BidItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_bid, parent, false)
        return BidItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: BidItemViewHolder, position: Int) {
        val bidItem = bidItems[position]
        holder.bind(bidItem)
    }

    override fun getItemCount(): Int {
        return bidItems.size
    }

    class BidItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemImage: ImageView = itemView.findViewById(R.id.item_image)
        private val itemName: TextView = itemView.findViewById(R.id.item_name)
        private val bidPrice: TextView = itemView.findViewById(R.id.bid_price)
        private val sellerNickname: TextView = itemView.findViewById(R.id.seller_nickname)

        fun bind(bidItem: BidItem) {
            itemName.text = bidItem.prodName

            // 가격에 콤마 추가
            val formattedPrice = NumberFormat.getNumberInstance(Locale.US).format(bidItem.bidPrice ?: 0)
            bidPrice.text = itemView.context.getString(R.string.bid_price_format, formattedPrice)

            sellerNickname.text = itemView.context.getString(R.string.seller_nickname_format, bidItem.sellerNickname ?: "알 수 없음")

            // 이미지 URL 변환
            val imageUrl = bidItem.prodImgPath?.let {
                if (it.startsWith("http://") || it.startsWith("https://")) {
                    it
                } else {
                    "http://192.168.219.53:8089$it"
                }
            }

            // Glide를 사용해 이미지 로드
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder) // 로딩 중 표시할 이미지
                .error(R.drawable.error) // 로드 실패 시 표시할 이미지
                .into(itemImage)

            // contentDescription 설정
            itemImage.contentDescription = itemView.context.getString(R.string.item_image_desc, bidItem.prodName)
        }
    }
}
