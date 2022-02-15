package com.plorrios.medialists.ui.TV.Views

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
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentSeriesDetailsBinding
import com.plorrios.medialists.db.TV.tvItems
import com.plorrios.medialists.ui.TV.Objects.TVObjects
import okhttp3.*
import org.json.JSONObject
import java.io.IOException


class SeriesDetailsFragment : Fragment() {

    val args: SeriesDetailsFragmentArgs by navArgs()

    private var _binding: FragmentSeriesDetailsBinding? = null

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
        _binding = FragmentSeriesDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val url =
            "https://api.themoviedb.org/3/tv/" + args.seriesId + "?api_key=" + getString(R.string.TMDB_API_Key) +
                    "&language=en-US"

        val request = Request.Builder()
            .url(url)
            .build()


        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {

                val jsonObject = JSONObject(response.body?.string())

                activity?.runOnUiThread {
                    binding.textView.text = jsonObject.toString()

                    binding.button.setOnClickListener {

                        val series = Gson().fromJson(jsonObject.toString(), TVObjects.Series::class.java)

                        val list = activity?.db?.TVListDao()?.get(args.listName)

                        val item = tvItems(series.id.toString(),series.name ,"Series" ,list?.id.toString())

                        activity?.db?.TVDao()?.insert(item)

                        binding.button.text = "added"

                        if (activity?.isSubscriber == true && activity?.hasConnection == true) {

                            val db = Firebase.firestore

                            val emptymap = hashMapOf(
                                "type" to "Series"
                            )

                            db.collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .collection("TVLists").document(args.listName)
                                .collection("list")
                                .document(jsonObject.getString("id"))
                                .set(emptymap)
                                .addOnSuccessListener {
                                    Log.d(
                                        "FIREBASE",
                                        "DocumentSnapshot successfully written!"
                                    )
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

            }

        })

        if (activity?.db?.TVDao()?.get(args.seriesId.toString()) != null){

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