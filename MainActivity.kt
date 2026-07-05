package com.example.ftpplayer

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class MainActivity : AppCompatActivity() {

    private lateinit var repository: FtpRepository
    private lateinit var adapter: FileListAdapter
    private lateinit var pathText: TextView
    private lateinit var progressBar: ProgressBar
    private lateinit var errorText: TextView

    private var currentPath = "/"
    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val host = getString(R.string.ftp_host)
        repository = FtpRepository(host)

        pathText = findViewById(R.id.pathText)
        progressBar = findViewById(R.id.progressBar)
        errorText = findViewById(R.id.errorText)

        val recyclerView: RecyclerView = findViewById(R.id.fileList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FileListAdapter { item -> onItemClicked(item) }
        recyclerView.adapter = adapter

        loadDirectory(currentPath)
    }

    private fun onItemClicked(item: FtpFileItem) {
        if (item.isDirectory) {
            currentPath = item.fullPath
            loadDirectory(currentPath)
        } else if (item.isVideoFile()) {
            val intent = Intent(this, PlayerActivity::class.java)
            intent.putExtra(PlayerActivity.EXTRA_PATH, item.fullPath)
            intent.putExtra(PlayerActivity.EXTRA_TITLE, item.name)
            startActivity(intent)
        }
        // Non-video files are ignored for now; could add download support later.
    }

    private fun loadDirectory(path: String) {
        pathText.text = path
        errorText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        scope.launch {
            try {
                val files = repository.listDirectory(path)
                progressBar.visibility = View.GONE
                if (path != "/") {
                    // Add a ".." entry to go back up
                    val parent = path.substringBeforeLast('/', "/").ifEmpty { "/" }
                    val upItem = FtpFileItem("..", parent, isDirectory = true, size = 0)
                    adapter.submitList(listOf(upItem) + files)
                } else {
                    adapter.submitList(files)
                }
            } catch (e: Exception) {
                progressBar.visibility = View.GONE
                errorText.visibility = View.VISIBLE
                errorText.text = "Could not load $path: ${e.message}"
            }
        }
    }

    override fun onBackPressed() {
        if (currentPath != "/") {
            currentPath = currentPath.substringBeforeLast('/', "/").ifEmpty { "/" }
            loadDirectory(currentPath)
        } else {
            super.onBackPressed()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()
    }
}
