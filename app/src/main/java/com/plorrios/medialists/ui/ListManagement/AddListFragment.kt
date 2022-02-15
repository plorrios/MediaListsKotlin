package com.plorrios.medialists.ui.ListManagement

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.plorrios.medialists.MainActivity
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.AddListFragmentBinding
import com.plorrios.medialists.db.Book.BooksListEntity
import com.plorrios.medialists.db.Games.GamesListEntity
import com.plorrios.medialists.db.Music.MusicListEntity
import com.plorrios.medialists.db.TV.TVListEntity

class AddListFragment : Fragment() {


    private lateinit var viewModel: AddListViewModel
    private var _binding: AddListFragmentBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    lateinit var db: FirebaseFirestore

    var activity = getActivity() as? MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = AddListFragmentBinding.inflate(inflater, container, false)

        viewModel = ViewModelProvider(this).get(AddListViewModel::class.java)
        val  root: View = binding.root

        val type = arguments?.getInt("listType")
        lateinit var typeString : String

        when(type) {
            0 -> typeString = "TVLists"
            1 -> typeString = "GamesLists"
            2 -> typeString = "MusicLists"
            3 -> typeString = "BooksLists"
        }

        binding.textView.text = typeString

        db = Firebase.firestore

        val test = hashMapOf(
            "show" to false,
        )

        binding.button.setOnClickListener{ it ->

            if (binding.editText.text.toString() != "") {

                when (typeString) {

                    "TVLists" -> {
                        activity?.db?.TVListDao()?.insert(TVListEntity(binding.editText.text.toString()))
                    }
                    "GamesLists" -> {
                        activity?.db?.gamesListDao()?.insert(GamesListEntity(binding.editText.text.toString()))
                    }
                    "MusicLists" -> {
                        activity?.db?.musicListDao()?.insert(MusicListEntity(binding.editText.text.toString()))
                    }
                    "BooksLists" -> {
                        activity?.db?.booksListDao()?.insert(BooksListEntity(binding.editText.text.toString()))
                    }

                }

                if (activity?.hasConnection == true && activity?.isSubscriber == true) {

                    db.collection("Users")
                        .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                        .collection(typeString).document(binding.editText.text.toString())
                        .set(test)
                        .addOnSuccessListener {
                            Log.d("FIREBASE", "DocumentSnapshot successfully written!")
                            activity?.onBackPressed()
                        }
                        .addOnFailureListener { e ->
                            run {
                                Log.w("FIREBASE", "Error writing document", e)
                                Toast.makeText(context, "error creating list", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        }

                }

                activity?.onBackPressed()

            } else {
                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }

                builder?.setMessage(R.string.list_needs_name_dialog_text)
                    ?.setTitle(R.string.list_needs_name_dialog_title)
                    ?.setPositiveButton(
                        R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->

                        }
                    )

                val dialog: AlertDialog? = builder?.create()

                if (dialog != null) {
                    dialog.show()
                }
            }

        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            activity = context
        }
    }

}