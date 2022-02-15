package com.plorrios.medialists.ui.games.Views

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
import com.google.gson.Gson
import com.plorrios.medialists.MainActivity
import com.plorrios.medialists.databinding.FragmentGameDetailsBinding
import com.plorrios.medialists.db.Games.gamesItems
import com.plorrios.medialists.ui.games.Objects.Game
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class GameDetailsFragment : Fragment() {

    val args: GameDetailsFragmentArgs by navArgs()

    private var _binding: FragmentGameDetailsBinding? = null

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
        _binding = FragmentGameDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val body = "fields *; where id = " + args.gameId.toString() + ";"

        val url = "https://mp0hzwwcyi.execute-api.us-west-2.amazonaws.com/production/v4/games"

        val requestBody = body.toRequestBody()

        val request = Request.Builder()
            .url(url)
            .post(requestBody)
            .build()

        Log.d("SEARCH", request.toString())

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    //Log.d("SEARCH",response.toString())

                    val str = it.body?.string()

                    val gamesList: Array<Game> = Gson().fromJson(str, Array<Game>::class.java)

                    val game = gamesList[0]

                    activity?.runOnUiThread {

                        binding.textView.text = str

                        binding.button.setOnClickListener {

                            val list = activity?.db?.gamesListDao()?.get(args.listName)

                            val item = gamesItems(game.getId().toString(), game.getName(), list?.id.toString())

                            activity?.db?.gamesDao()?.insert(item)

                            binding.button.text = "added"

                            if (activity?.isSubscriber == true && activity?.hasConnection == true) {

                                val db = Firebase.firestore

                                val emptymap = hashMapOf(
                                    "name" to game.getName()
                                )

                                db.collection("Users")
                                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                    .collection("GamesLists").document(args.listName)
                                    .collection("list")
                                    .document(game.getId().toString())
                                    .set(emptymap)
                                    .addOnSuccessListener {
                                        Log.d("FIREBASE", "DocumentSnapshot successfully written!")
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

                }
            }
        })

        if (activity?.db?.gamesDao()?.get(args.gameId.toString()) != null){

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