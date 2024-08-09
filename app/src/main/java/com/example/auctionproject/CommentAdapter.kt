package com.example.auctionproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class CommentAdapter(private var comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>(){

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgUserProfile: ImageView = view.findViewById(R.id.imgUserProfile)
        val tvUserName: TextView = view.findViewById(R.id.tvUserName)
        val tvCommentContent: TextView = view.findViewById(R.id.tvCommentContent)
        val tvCommentedAt: TextView = view.findViewById(R.id.tvCommentedAt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_other_message, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.tvUserName.text = comment.userName
        holder.tvCommentContent.text = comment.commentContent
        holder.tvCommentedAt.text = comment.commentedAt

        // Load user profile image
        Glide.with(holder.itemView.context)
            .load(comment.userProfileImageUrl)
            .placeholder(R.drawable.ic_bid)  // 기본 이미지 설정
            .into(holder.imgUserProfile)
    }

    override fun getItemCount(): Int = comments.size

    // 댓글 데이터를 업데이트하는 메서드
    fun updateComments(newComments: List<Comment>) {
        this.comments = newComments
        notifyDataSetChanged() // 데이터 변경을 RecyclerView에 알림
    }
}
