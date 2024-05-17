package com.example.prashantadvaitfoundationtask.ui

import android.os.Bundle
import android.view.View
import android.widget.AbsListView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.prashantadvaitfoundationtask.databinding.ActivityMainBinding
import com.example.prashantadvaitfoundationtask.ui.adapter.GridAdapter
import com.example.prashantadvaitfoundationtask.ui.adapter.ImageAdapter
import com.example.prashantadvaitfoundationtask.utils.CONSTANTS
import com.example.prashantadvaitfoundationtask.utils.Status
import com.example.prashantadvaitfoundationtask.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::inflate)
    val mainViewModel : MainViewModel by viewModels()
    private lateinit var gridAdapter: GridAdapter
    private val itemsPerPage = 10
    private val totalItems = 99
    private var currentPage = 0
    private var listOfUrls = emptyList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        mainViewModel.fetchMainData()
        observeData()

    }



    private fun initUi() {
        val recyclerView = binding.gridView
        recyclerView.numColumns = CONSTANTS.GRID_COLUMNS
        gridAdapter = GridAdapter(this)
        recyclerView.adapter = gridAdapter
        binding.gridView.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView?, scrollState: Int) {

            }

            override fun onScroll(
                view: AbsListView?,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int,
            ) {
                val lastItem = firstVisibleItem + visibleItemCount
                if (lastItem == totalItemCount && totalItemCount < totalItems) {
                    currentPage++
                    gridAdapter.submitList(getItemsForPage(currentPage, listOfUrls))
                }
            }

        })
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
                            listOfUrls = list
                            Timber.i("Received list.")
                            initUi()
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

    private fun getItemsForPage(page: Int, listOfUrls: List<String>): List<String> {
        val start = (page - 1) * itemsPerPage
        val end = minOf(start + itemsPerPage, totalItems)
        val items = mutableListOf<String>()
        for (i in start until end) {
            items.add(listOfUrls[i])
        }
        return items
    }
}