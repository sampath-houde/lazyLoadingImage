package com.example.prashantadvaitfoundationtask.ui

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.prashantadvaitfoundationtask.databinding.ActivityMainBinding
import com.example.prashantadvaitfoundationtask.ui.adapter.ImageAdapter
import com.example.prashantadvaitfoundationtask.utils.Constants
import com.example.prashantadvaitfoundationtask.utils.Status
import com.example.prashantadvaitfoundationtask.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {


    private val binding by viewBinding(ActivityMainBinding::inflate)
    private val mainViewModel : MainViewModel by viewModels()
    private lateinit var gridAdapter: ImageAdapter
    private var responseList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel.fetchMainData()
        observeData()
    }



    private fun initRecyclerView() {
        gridAdapter = ImageAdapter()
        binding.gridView.apply {
            isVerticalScrollBarEnabled = true
            layoutManager = GridLayoutManager(this@MainActivity, Constants.GRID_COLUMNS)
            adapter = gridAdapter
        }
        gridAdapter.addImageUrls(responseList)
    }

    private fun observeData() {
        lifecycleScope.launch {
            mainViewModel.mainState.collect {
                when (it.status) {
                    Status.LOADING -> {
                        binding.progressBar.visibility = View.VISIBLE
                        binding.gridView.visibility = View.GONE
                        Timber.i("Loading...")
                    }
                    Status.SUCCESS -> {
                        binding.progressBar.visibility = View.GONE
                        binding.gridView.visibility = View.VISIBLE

                        it.data?.let { mainResponseList ->
                            val list: MutableList<String> = mainResponseList.map { res -> res.getThumbnailUrl() }.toMutableList()
                            responseList = list
                            Timber.i("Received list.")
                            initRecyclerView()
                        }
                            ?: run {

                                Timber.e("Error: Failed to fetch list.")
                            }
                    }
                    // error occurred status
                    else -> {
                        binding.progressBar.visibility = View.GONE
                        binding.gridView.visibility = View.GONE

                        Toast.makeText(this@MainActivity, "${it.message}", Toast.LENGTH_SHORT)
                            .show()

                        Timber.e(it.message.toString())
                    }
                }
            }
        }
    }

}