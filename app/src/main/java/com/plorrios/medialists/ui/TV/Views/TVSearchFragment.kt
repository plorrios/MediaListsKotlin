package com.plorrios.medialists.ui.TV.Views

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentSearchTVBinding
import okhttp3.*
import java.io.IOException
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.plorrios.medialists.ui.TV.Adapters.TVListAdapter
import com.plorrios.medialists.ui.TV.Objects.TVObjects
import com.plorrios.medialists.ui.TV.ViewModels.TVSearchViewModel
import org.json.JSONArray
import org.json.JSONObject


class TVSearchFragment : Fragment() {

    val args: TVSearchFragmentArgs by navArgs()

    private var _binding: FragmentSearchTVBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    private lateinit var viewModel: TVSearchViewModel

    private lateinit var adapter: TVListAdapter

    private lateinit var layoutManager: LinearLayoutManager

    private var searchQuery: String? = ""

    private var page : Int = 0

    private var isScrolling = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(TVSearchViewModel::class.java) // init view model
        _binding = FragmentSearchTVBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)

        binding.rvTvList.layoutManager = layoutManager

        viewModel.list.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter = TVListAdapter(it)
                binding.rvTvList.setHasFixedSize(true)
                binding.rvTvList.adapter = adapter
                adapter.onItemClick = {
                    if (it is TVObjects.Movie) {
                        val action =
                            TVSearchFragmentDirections.actionSearchTVFragmentToMovieDetailsFragment(
                                it.id,
                                args.listName
                            )
                        activity?.findNavController(R.id.nav_host_fragment_activity_main)
                            ?.navigate(action)
                    } else if (it is TVObjects.Series){
                        val action =
                            TVSearchFragmentDirections.actionSearchTVFragmentToSeriesDetailsFragment(
                                it.id,
                                args.listName
                            )
                        activity?.findNavController(R.id.nav_host_fragment_activity_main)
                            ?.navigate(action)
                    }
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
                        if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1 && !isScrolling){
                            loadData(layoutManager.findLastVisibleItemPosition(), searchQuery, false)
                        }
                    }
                }

                binding.rvTvList.addOnScrollListener(scrollListener)

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

    fun loadData(fromPos: Int, query: String?, newSearch: Boolean){

        if (page != fromPos/20 + 1 || newSearch) {

            isScrolling = true

            page = fromPos/20 + 1

            val url =
                "https://api.themoviedb.org/3/search/multi?api_key=" + getString(R.string.TMDB_API_Key) +
                        "&language=en-US&query=" + query + "&page=" + page

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

                    val searchAns: JSONArray = jsonObject.getJSONArray("results")

                    val tvList: ArrayList<TVObjects> = arrayListOf()

                    for (i in 0 until searchAns.length()) {
                        val item: JSONObject = searchAns.getJSONObject(i)
                        Log.d("SEARCH", item.toString())
                        if (item.get("media_type") == "movie") {
                            /** MOVIES **/
                            tvList.add(
                                Gson().fromJson(
                                    item.toString(),
                                    TVObjects.Movie::class.java
                                )
                            )
                        } else if (item.get("media_type") == "tv") {
                            /** SERIES **/
                            tvList.add(
                                Gson().fromJson(
                                    item.toString(),
                                    TVObjects.Series::class.java
                                )
                            )
                        }
                    }

                    if (fromPos == 0) {
                        adapter.setData(tvList)
                        viewModel.setNewItems(tvList)
                        activity?.runOnUiThread {
                            adapter.notifyDataSetChanged()
                        }
                    } else {
                        adapter.addData(tvList)
                        viewModel.addNewItems(tvList)
                        activity?.runOnUiThread {
                            //adapter.notifyDataSetChanged()
                            adapter.dataSet
                            val realpos = adapter.dataSet.size - tvList.size - 1 + adapter.dataSet.size/adapter.AdPeriod
                            adapter.notifyItemRangeInserted(
                                realpos,
                                tvList.size
                            )
                        }
                    }

                    //println(response.body()?.string())
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