package com.vinay.skrawl

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.thefinestartist.finestwebview.FinestWebView
import com.vinay.skrawl.MoviesAdapter.MyViewHolder


public class MoviesAdapter(
    private val rList: List<Page>,
    private val context: Context
) : RecyclerView.Adapter<MyViewHolder>() {

    inner class MyViewHolder(view: View) : ViewHolder(view) {
        var title: TextView
        var desc: TextView
        var imageView: ImageView

        init {
            title = view.findViewById<View>(R.id.textView) as TextView
            desc = view.findViewById<View>(R.id.textView2) as TextView
            imageView =
                view.findViewById<View>(R.id.imageView) as ImageView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_results, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val (_, _, _, terms, thumbnail, title) = rList[position]
        holder.title.text = title
        holder.desc.text = terms?.description?.get(0)
        Glide.with(context).load(thumbnail?.source).centerCrop().placeholder(R.drawable.giphy)
            .error(R.drawable.spider)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {

            FinestWebView.Builder(context)
                .show("https://en.wikipedia.org/wiki/${title.replace(' ', '_')}")
        }
    }

    override fun getItemCount(): Int {
        return rList.size
    }

}