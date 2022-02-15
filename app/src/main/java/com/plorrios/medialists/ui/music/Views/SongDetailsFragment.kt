package com.plorrios.medialists.ui.music.Views

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.plorrios.medialists.MainActivity
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentSongDetailsBinding
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class SongDetailsFragment : Fragment() {

    val args: SongDetailsFragmentArgs by navArgs()

    private var _binding: FragmentSongDetailsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private val client = OkHttpClient()

    var activity = getActivity() as? MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSongDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val url = "https://ws.audioscrobbler.com/2.0/?method=track.getInfo&api_key=" + getString(R.string.last_fm_API_Key) + "&artist=" + args.songArtist + "&track=" + args.songTitle + "&format=json"

        Log.d("SEARCH",url)

        val request = Request.Builder()
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }
            override fun onResponse(call: Call, response: Response){
                val jsonObject = JSONObject(response.body?.string())

                val track = jsonObject.getJSONObject("track")

                Log.d("FIREBASE",track.toString())

                val artist = track.getJSONObject("artist")

                activity?.runOnUiThread {
                    binding.textView.text = track.toString()

                    val db = Firebase.firestore

                    binding.button.setOnClickListener {
                        val emptymap = hashMapOf(
                            "artist" to artist.get("name")
                        )

                        db.collection("Users")
                            .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                            .collection("MusicLists").document(args.listName)
                            .collection("list").document(track.getString("name"))
                            .set(emptymap)
                            .addOnSuccessListener {
                                Log.d("FIREBASE", "DocumentSnapshot successfully written!")
                                binding.button.text = "added"
                            }
                            .addOnFailureListener { e ->
                                run {
                                    Log.w("FIREBASE", "Error writing document", e)
                                    Toast.makeText(
                                        context,
                                        "error saving item",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                }

            }
        })

        if (activity?.db?.musicDao()?.get(args.songTitle) != null){

            binding.button.text = "added"

        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            activity = context
        }
    }
}