package com.plorrios.medialists.ui.books.Views

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
import com.plorrios.medialists.databinding.FragmentBookDetailsBinding
import com.plorrios.medialists.db.Book.booksItems
import com.plorrios.medialists.ui.books.Objects.Book
import com.plorrios.medialists.ui.books.Objects.VolumeInfo
import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class BookDetailsFragment : Fragment() {

    val args: BookDetailsFragmentArgs by navArgs()

    private var _binding: FragmentBookDetailsBinding? = null

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
        _binding = FragmentBookDetailsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        var url =
            "https://www.googleapis.com/books/v1/volumes/" + args.bookId + "?key=" + getString(R.string.Google_API_Key)

        Log.d("SEARCH",url)

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

                        val book = Gson().fromJson(jsonObject.toString(), Book::class.java)

                        val list = activity?.db?.booksListDao()?.get(args.listName)

                        val bookInfo = VolumeInfo(jsonObject.getJSONObject("volumeInfo").getString("title"))

                        val item = booksItems(book.getId(), book.getInfo().getTitle(),list?.id.toString())

                        activity?.db?.booksDao()?.insert(item)

                        binding.button.text = "added"

                        if (activity?.isSubscriber == true && activity?.hasConnection == true) {

                            val db = Firebase.firestore

                            val emptymap = hashMapOf(
                                "name" to book.getInfo().getTitle()
                            )

                            db.collection("Users")
                                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .collection("BooksLists").document(args.listName).collection("list")
                                .document(book.getId().toString())
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
                                        )
                                            .show()
                                    }
                                }
                        }
                    }
                }
            }
        })

        if (activity?.db?.booksDao()?.get(args.bookId.toString()) != null){

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