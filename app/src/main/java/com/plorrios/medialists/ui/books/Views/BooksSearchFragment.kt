package com.plorrios.medialists.ui.books.Views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentBooksSearchBinding
import com.plorrios.medialists.ui.books.Adapters.BooksListAdapter
import com.plorrios.medialists.ui.books.Objects.Book
import com.plorrios.medialists.ui.books.Objects.VolumeInfo
import com.plorrios.medialists.ui.books.ViewModels.BooksSearchViewModel
import com.plorrios.medialists.ui.games.Adapters.GamesListAdapter
import com.plorrios.medialists.ui.games.ViewModels.GamesSearchViewModel
import okhttp3.*
import org.json.JSONArray
import java.io.IOException
import org.json.JSONObject


class BooksSearchFragment : Fragment() {

    val args: BooksSearchFragmentArgs by navArgs()

    private var _binding: FragmentBooksSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    private lateinit var viewModel: BooksSearchViewModel

    private lateinit var adapter: BooksListAdapter

    val booksList = ArrayList<Book>()

    private lateinit var layoutManager: LinearLayoutManager

    private var searchQuery: String? = ""

    private var isScrolling = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(BooksSearchViewModel::class.java) // init view model
        _binding = FragmentBooksSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)

        binding.rvBooksList.layoutManager = layoutManager

        viewModel.list.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter = BooksListAdapter(it)
                binding.rvBooksList.setHasFixedSize(true)
                binding.rvBooksList.adapter = adapter

                adapter.onItemClick = {
                    val action =
                        BooksSearchFragmentDirections.actionBooksSearchFragmentToBookDetailsFragment(
                            it.getId(),
                            args.listName
                        )
                    activity?.findNavController(R.id.nav_host_fragment_activity_main)
                        ?.navigate(action)
                }
            }
        })

        viewModel.query.observe(viewLifecycleOwner, {
            if (it != null){

                binding.searchView.setQuery(it, false)

            }
        })

        //binding.toolbar.inflateMenu(R.menu.top_menu)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                searchQuery = query

                viewModel.setQuery(query)

                loadData(0, query, true)

                val scrollListener = object : RecyclerView.OnScrollListener() {
                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        super.onScrollStateChanged(recyclerView, newState)
                        val pos = layoutManager.findLastVisibleItemPosition()
                        if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1 && !isScrolling){
                            loadData(adapter.getRealSize(), searchQuery, false)
                        }
                    }
                }

                binding.rvBooksList.addOnScrollListener(scrollListener)

                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                //println("tap")
                return false
            }
        })

        //+setHasOptionsMenu(true)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun loadData(fromPos: Int, query: String?, newSearch: Boolean) {

        if (newSearch || fromPos % 20 == 0) {

            isScrolling = true

            var url =
                "https://www.googleapis.com/books/v1/volumes?q=" + query + "&maxResults=20&startIndex=" + fromPos + "&key=" + getString(R.string.Google_API_Key)

            Log.d(
                "SEARCH",
                "https://www.googleapis.com/books/v1/volumes?q=" + query + "&key=" + getString(R.string.Google_API_Key)
            )

            //url = "https://www.googleapis.com/books/v1/volumes/oWWNzQEACAAJ?key=" + getString(R.string.Google_API_Key)

            val request = Request.Builder()
                .url(url)
                .build()

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    isScrolling = false
                }

                override fun onResponse(call: Call, response: Response) {

                    val jsonObject = JSONObject(response.body?.string())

                    val books: JSONArray = jsonObject.getJSONArray("items")

                    for (i in 0 until books.length()) {
                        val item: JSONObject = books.getJSONObject(i)
                        var book =
                            Gson().fromJson(item.toString(), Book::class.java)
                        var bookInfo =
                            Gson().fromJson(
                                item.get("volumeInfo").toString(),
                                VolumeInfo::class.java
                            )

                        book.setInfo(bookInfo)
                        //book.setId(item.getString("id"))
                        booksList.add(book)
                        Log.d("SEARCH", item.toString())
                    }

                    if (fromPos == 0) {
                        booksList
                        adapter.setData(booksList)
                        viewModel.setNewItems(booksList)
                        activity?.runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        adapter.addData(booksList)
                        viewModel.addNewItems(booksList)
                        activity?.runOnUiThread {
                            //adapter.notifyDataSetChanged()
                            adapter.dataSet
                            val realpos = adapter.dataSet.size - booksList.size - 1 + adapter.dataSet.size/adapter.AdPeriod
                            adapter.notifyItemRangeInserted(
                                realpos,
                                booksList.size
                            )
                        }
                    }

                    isScrolling = false

                }
            })

        }
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

}