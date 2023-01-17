package com.dream.pixeldraw.activity

import android.os.Bundle
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.mstudio.helper.FileHandle
import com.dream.pixeldraw.AppGlobalData
import com.dream.pixeldraw.adapter.FileChooseAdapter
import com.dream.pixeldraw.databinding.ActivityFileChooseBinding
import java.io.File

class FileChooseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFileChooseBinding
    private var pathStr = Environment.getExternalStorageDirectory().path
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFileChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.chooseToolbar.title = "选择图片"
        setSupportActionBar(binding.chooseToolbar)
        binding.choosePath.text = pathStr
        val linearManager = LinearLayoutManager(applicationContext)
        val globalList: ArrayList<String> = ArrayList()
        FileHandle.listDir(pathStr, globalList)
        val fileAdapter = FileChooseAdapter(globalList)
        fileAdapter.setOnFileSelectedListener(object : FileChooseAdapter.OnFileSelectedListener{
            override fun onFileSelectedListener(position: Int) {
                val singleFile = File(globalList[position]).name
                if (FileHandle.isDirectory(globalList[position])){
                    pathStr += "/$singleFile"
                    binding.choosePath.text = pathStr
                    globalList.clear()
                    FileHandle.listDir(pathStr, globalList)
                    val anotherAdapter = FileChooseAdapter(globalList)
                    anotherAdapter.setOnFileSelectedListener(this)
                    val anotherManager = LinearLayoutManager(applicationContext)
                    binding.chooseList.layoutManager = anotherManager
                    binding.chooseList.adapter = anotherAdapter
                } else if (File(globalList[position]).isFile){
                    if (singleFile.contains("png")){
                        pathStr = binding.choosePath.text.toString() + "/" + singleFile
                        AppGlobalData.MA_INSTANCE.onRead(pathStr)
                        finish()
                    }
                }
            }
        })
        binding.chooseList.layoutManager = linearManager
        binding.chooseList.adapter = fileAdapter
        binding.chooseBack.setOnClickListener {
            if(File(pathStr.substring(0, pathStr.lastIndexOf("/"))).listFiles()?.isNotEmpty() == true) {
                val partList: ArrayList<String> = ArrayList()
                FileHandle.listDir(pathStr.substring(0, pathStr.lastIndexOf("/")), partList)
                val partManager = LinearLayoutManager(applicationContext)
                val partAdapter = FileChooseAdapter(partList)
                partAdapter.setOnFileSelectedListener(object : FileChooseAdapter.OnFileSelectedListener{
                    override fun onFileSelectedListener(position: Int) {
                        val singleFile = File(partList[position]).name
                        if (FileHandle.isDirectory(partList[position])){
                            pathStr += "/$singleFile"
                            binding.choosePath.text = pathStr
                            partList.clear()
                            FileHandle.listDir(pathStr, partList)
                            val anotherAdapter = FileChooseAdapter(partList)
                            anotherAdapter.setOnFileSelectedListener(this)
                            val anotherManager = LinearLayoutManager(applicationContext)
                            binding.chooseList.layoutManager = anotherManager
                            binding.chooseList.adapter = anotherAdapter
                        } else if (File(partList[position]).isFile){
                            if (singleFile.contains("png")){
                                pathStr = binding.choosePath.text.toString() + "/" + singleFile
                                AppGlobalData.MA_INSTANCE.onRead(pathStr)
                                finish()
                            }
                        }
                    }
                })
                binding.chooseList.layoutManager = partManager
                binding.chooseList.adapter = partAdapter
                binding.choosePath.text = pathStr.substring(0, pathStr.lastIndexOf("/"))
                pathStr = pathStr.substring(0, pathStr.lastIndexOf("/"))
            }
        }
    }
}