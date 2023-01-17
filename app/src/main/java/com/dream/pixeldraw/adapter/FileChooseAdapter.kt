package com.dream.pixeldraw.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dream.pixeldraw.R
import java.io.File

class FileChooseAdapter(private var fileList: ArrayList<String>) : RecyclerView.Adapter<FileChooseAdapter.FileChooseHold>(){
    class FileChooseHold(itemView: View) : RecyclerView.ViewHolder(itemView){
        val boardButton: LinearLayout = itemView.findViewById(R.id.list_board)
        val iconHint: ImageView = itemView.findViewById(R.id.icon)
        val nameHint: TextView = itemView.findViewById(R.id.name)
    }

    interface OnFileSelectedListener {
        fun onFileSelectedListener(position: Int)
    }

    private var singleFileSelectedListener: OnFileSelectedListener? = null

    fun setOnFileSelectedListener(listener: OnFileSelectedListener){
        singleFileSelectedListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileChooseHold {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.list_theme, parent, false)
        return FileChooseHold(view)
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    override fun onBindViewHolder(holder: FileChooseHold, position: Int) {
        val singleFile = File(fileList[position])
        if (singleFile.isFile){
            holder.iconHint.setImageResource(R.drawable.file)
        } else {
            holder.iconHint.setImageResource(R.drawable.archive_director)
        }
        holder.nameHint.text = singleFile.name
        holder.boardButton.setOnClickListener {
            singleFileSelectedListener!!.onFileSelectedListener(position)
        }
    }
}