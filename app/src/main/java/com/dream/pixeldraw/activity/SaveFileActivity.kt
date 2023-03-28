package com.dream.pixeldraw.activity

import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dream.pixeldraw.helper.FileHandle
import com.dream.pixeldraw.AppGlobalData
import com.dream.pixeldraw.adapter.FileChooseAdapter
import com.dream.pixeldraw.databinding.ActivitySaveFileBinding
import java.io.File

class SaveFileActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySaveFileBinding
    private var pathStr = Environment.getExternalStorageDirectory().absolutePath
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySaveFileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.saveToolbar.title = "文件保存"
        setSupportActionBar(binding.saveToolbar)
        binding.savePath.text = pathStr
        val linearManager = LinearLayoutManager(applicationContext)
        try {
            val globalList: ArrayList<String> = ArrayList()
            FileHandle.listDir(pathStr, globalList)
            val fileAdapter = FileChooseAdapter(globalList)
            fileAdapter.setOnFileSelectedListener(object :
                FileChooseAdapter.OnFileSelectedListener {
                override fun onFileSelectedListener(position: Int) {
                    val data = globalList[position]
                    if (File(data).isFile) {
                        Toast.makeText(applicationContext, "这是文件，不能作为存储目录", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        pathStr += "/${File(data).name}"
                        binding.savePath.text = pathStr
                        globalList.clear()
                        FileHandle.listDir(pathStr, globalList)
                        val anotherAdapter = FileChooseAdapter(globalList)
                        anotherAdapter.setOnFileSelectedListener(this)
                        val anotherManager = LinearLayoutManager(applicationContext)
                        binding.saveList.layoutManager = anotherManager
                        binding.saveList.adapter = anotherAdapter
                    }
                }
            })
            binding.saveList.layoutManager = linearManager
            binding.saveList.adapter = fileAdapter
            dealBack()
            dealSave()
        }catch (e: Exception){
            Toast.makeText(applicationContext, "此文件夹不可被访问", Toast.LENGTH_SHORT).show()
        }
    }

    private fun dealSave() {
        binding.saveConfirm.setOnClickListener {
            pathStr = pathStr + "/" + binding.saveName.text.toString() + ".png"
            AppGlobalData.MA_INSTANCE.saveImage(pathStr)
            Toast.makeText(applicationContext, "储存为：${pathStr}", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun dealBack() {
        binding.saveBack.setOnClickListener {
            if(File(pathStr.substring(0, pathStr.lastIndexOf("/"))).listFiles()?.isNotEmpty() == true) {
                val partList: ArrayList<String> = ArrayList()
                FileHandle.listDir(pathStr.substring(0, pathStr.lastIndexOf("/")), partList)
                val partManager = LinearLayoutManager(applicationContext)
                val partAdapter = FileChooseAdapter(partList)
                partAdapter.setOnFileSelectedListener(object : FileChooseAdapter.OnFileSelectedListener{
                    override fun onFileSelectedListener(position: Int) {
                        val data = partList[position]
                        if (File(data).isFile){
                            Toast.makeText(applicationContext, "这是文件，不能作为存储目录", Toast.LENGTH_SHORT).show()
                        } else {
                            pathStr += "/${File(data).name}"
                            binding.savePath.text = pathStr
                            partList.clear()
                            FileHandle.listDir(pathStr, partList)
                            val anotherAdapter = FileChooseAdapter(partList)
                            anotherAdapter.setOnFileSelectedListener(this)
                            val anotherManager = LinearLayoutManager(applicationContext)
                            binding.saveList.layoutManager = anotherManager
                            binding.saveList.adapter = anotherAdapter
                        }
                    }
                })
                binding.saveList.layoutManager = partManager
                binding.saveList.adapter = partAdapter
            }
        }
    }
}