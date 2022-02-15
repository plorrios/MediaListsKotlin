package com.plorrios.medialists.ui.games.Views

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentGamesSearchBinding
import com.plorrios.medialists.ui.books.Views.BooksSearchFragmentDirections
import com.plorrios.medialists.ui.games.Adapters.GamesListAdapter
import com.plorrios.medialists.ui.games.Objects.Game
import com.plorrios.medialists.ui.games.ViewModels.GamesSearchViewModel
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class GamesSearchFragment : Fragment() {

    val args: GamesSearchFragmentArgs by navArgs()

    private var _binding: FragmentGamesSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    private lateinit var viewModel: GamesSearchViewModel

    private lateinit var adapter: GamesListAdapter

    private lateinit var layoutManager: LinearLayoutManager

    private var searchQuery: String? = ""

    private var isScrolling = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(GamesSearchViewModel::class.java) // init view model
        _binding = FragmentGamesSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        layoutManager = LinearLayoutManager(context)

        binding.rvGamesList.layoutManager = layoutManager

        viewModel.list.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter = GamesListAdapter(it)
                binding.rvGamesList.setHasFixedSize(true)
                binding.rvGamesList.adapter = adapter
                adapter.onItemClick = {
                    val action =
                        GamesSearchFragmentDirections.actionGamesSearchFragmentToGameDetailsFragment(
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
                        if (layoutManager.findLastVisibleItemPosition() == adapter.itemCount - 1 && !isScrolling){
                            loadData(adapter.getRealSize(), searchQuery, false)
                        }
                    }
                }

                binding.rvGamesList.addOnScrollListener(scrollListener)

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

        if (newSearch || fromPos % 20 == 0) {

            isScrolling = true

            val body = "limit " + 20 + "; search \"$query\"; fields name, id; offset " + fromPos + ";"

            var url =
                "https://mp0hzwwcyi.execute-api.us-west-2.amazonaws.com/production/v4/games"

            val requestBody = body.toRequestBody()

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            Log.d("SEARCH", request.toString())

            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    isScrolling = false
                }

                override fun onResponse(call: Call, response: Response) {
                    response.use {
                        Log.d("SEARCH", response.toString())

                        val gamesList: Array<Game> =
                            Gson().fromJson(it.body?.string(), Array<Game>::class.java)

                        for (game in gamesList) {
                            Log.d("SEARCH", game.getName())
                        }

                        if (fromPos == 0) {
                            adapter.setData(gamesList)
                            viewModel.setNewItems(gamesList)
                            activity?.runOnUiThread {
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            adapter.addData(gamesList)
                            viewModel.addNewItems(gamesList)
                            activity?.runOnUiThread {
                                //adapter.notifyDataSetChanged()
                                adapter.dataSet
                                val realpos = adapter.dataSet.size - gamesList.size - 1 + adapter.dataSet.size/adapter.AdPeriod
                                adapter.notifyItemRangeInserted(
                                    realpos,
                                    gamesList.size
                                )
                            }
                        }

                    }
                    isScrolling = false
                }
            })

            /** REFRESH TOKEN
             *
             *  POST /oauth/token HTTP/1.1
             *  Host: authorization-server.com
             *
             *  grant_type=refresh_token
             *  &amp;refresh_token=xxxxxxxxxxx
             *  &amp;client_id=xxxxxxxxxx
             *  &amp;client_secret=xxxxxxxxxx
             *
             * **/

        }

    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }

}