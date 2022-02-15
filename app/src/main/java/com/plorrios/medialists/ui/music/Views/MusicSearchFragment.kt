package com.plorrios.medialists.ui.music.Views

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
import com.google.gson.Gson
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentMusicSearchBinding
import com.plorrios.medialists.ui.games.Adapters.GamesListAdapter
import com.plorrios.medialists.ui.games.ViewModels.GamesSearchViewModel
import com.plorrios.medialists.ui.games.Views.GamesSearchFragmentDirections
import com.plorrios.medialists.ui.music.Adapters.MusicListAdapter
import com.plorrios.medialists.ui.music.Objects.Song
import com.plorrios.medialists.ui.music.ViewModels.MusicSearchViewModel
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MusicSearchFragment : Fragment() {

    val args: MusicSearchFragmentArgs by navArgs()

    private var _binding: FragmentMusicSearchBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    private lateinit var viewModel: MusicSearchViewModel

    private lateinit var adapter: MusicListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel =
            ViewModelProvider(requireActivity()).get(MusicSearchViewModel::class.java) // init view model
        _binding = FragmentMusicSearchBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewModel.list.observe(viewLifecycleOwner, {
            if (it != null) {
                adapter = MusicListAdapter(it)
                binding.rvMusicList.setHasFixedSize(true)
                binding.rvMusicList.layoutManager = LinearLayoutManager(context)
                binding.rvMusicList.adapter = adapter
                adapter.onItemClick = {
                    val action =
                        MusicSearchFragmentDirections.actionMusicSearchFragmentToSongDetailsFragment(
                            it.getName(),
                            it.getArtist(),
                            args.listName
                        )
                    activity?.findNavController(R.id.nav_host_fragment_activity_main)
                        ?.navigate(action)
                }
            }
        })


        //binding.toolbar.inflateMenu(R.menu.top_menu)
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                val url =
                    "https://ws.audioscrobbler.com/2.0/?method=track.search&track=" + query + "&api_key=" + getString(
                        R.string.last_fm_API_Key
                    ) +
                            "&format=json"

                val request = Request.Builder()
                    .url(url)
                    .build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        e.printStackTrace()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        val jsonObject = JSONObject(response.body?.string())

                        val searchAns: JSONObject = jsonObject.getJSONObject("results")

                        val music: JSONObject = searchAns.getJSONObject("trackmatches")

                        val songs: JSONArray = music.getJSONArray("track")

                        val songsList: ArrayList<Song> = arrayListOf()

                        for (i in 0 until songs.length()) {
                            val item: JSONObject = songs.getJSONObject(i)
                            songsList.add(Gson().fromJson(item.toString(), Song::class.java))
                        }

                        viewModel.addNewItems(songsList)
                        //adapter.addAll(songsList)
                        adapter.setData(songsList)
                        adapter.notifyItemRangeInserted(
                            adapter.dataSet.size - songsList.size,
                            adapter.dataSet.size
                        )

                        Log.d("SEARCH", adapter.dataSet.toString())


                    }
                })
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
}