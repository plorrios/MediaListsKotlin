package com.plorrios.medialists.ui.books.Views

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.plorrios.medialists.MainActivity
import com.plorrios.medialists.R
import com.plorrios.medialists.databinding.FragmentBooksBinding
import com.plorrios.medialists.ui.ListManagement.adapters.ListAdapter
import com.plorrios.medialists.ui.books.ViewModels.BooksViewModel

class BooksFragment : Fragment() {

    private lateinit var booksViewModel: BooksViewModel
    private var _binding: FragmentBooksBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var checkingLists : Boolean = true

    lateinit var db: FirebaseFirestore
    lateinit var listItems: ArrayList<String>
    lateinit var listItems2: ArrayList<String>

    private var list = ""

    lateinit var adapter : ListAdapter

    var activity = getActivity() as? MainActivity


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        booksViewModel =
            ViewModelProvider(this).get(BooksViewModel::class.java)

        _binding = FragmentBooksBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.toolbar.inflateMenu(R.menu.top_menu)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.top_menu_add -> {
                    if (checkingLists) {
                        activity?.findNavController(R.id.nav_host_fragment_activity_main)
                            ?.navigate(R.id.action_navigation_books_to_addListFragment)
                    } else {
                        val action = BooksFragmentDirections.actionNavigationBooksToBooksSearchFragment(
                            list
                        )
                        activity?.findNavController(R.id.nav_host_fragment_activity_main)
                            ?.navigate(action)
                    }

                }
            }
            true
        }

        /*val textView: TextView = binding.textDashboard
        gamesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })*/
        db = Firebase.firestore
        listItems = ArrayList<String>();
        listItems2 = ArrayList<String>();



        adapter = ListAdapter(listItems)
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


        if (checkingLists) {

            showLists()
            setAdapterClicks()

        } else {

            checkingLists = true
            showLists()
            setAdapterClicks()

        }

        return root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            activity = context
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun showLists(){

        /*
        db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.email.toString()).collection("BooksLists")
            .get()
            .addOnSuccessListener { documentReference ->
                listItems.clear()

                documentReference.forEach { item ->
                    listItems.add(item.id)
                }

                adapter.dataSet = listItems

                adapter.notifyDataSetChanged()

                checkingLists = true

            }
            .addOnFailureListener { e ->
                Log.w("FIREBASE", "Error adding document", e)
            }
        */

        listItems.clear()

        val booksLists = activity?.db?.booksListDao()?.getAll()

        if (booksLists != null) {
            for (item in booksLists){
                listItems.add(item.id)
            }
        }

        adapter.dataSet = listItems

        adapter.notifyDataSetChanged()

        checkingLists = true

    }

    fun setAdapterClicks(){
        //TODO check if looking at lists or at games
        adapter.onItemClick = {

            if (checkingLists) {

                list = it

                listItems2.clear()
                val booksList = activity?.db?.booksDao()?.getOfList(list)
                if (booksList != null) {
                    for (item in booksList){
                        listItems2.add(item.title)
                    }
                }
                adapter.dataSet = listItems2
                adapter.notifyDataSetChanged()
                checkingLists = false
                /*

                db.collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                    .collection("BooksLists").document(list).collection("list")
                    .get()
                    .addOnSuccessListener { documentReference ->
                        try {
                            listItems2.clear()
                            val items = documentReference.forEach { item ->
                                listItems2.add(item.id)
                            }
                            //TODO change to a custom adapter
                            adapter.dataSet = listItems2
                            adapter.notifyDataSetChanged()
                            checkingLists = false

                        } catch (e: NullPointerException) {
                            val items = arrayListOf<String>()
                            //TODO change to a custom adapter
                            adapter.dataSet = items
                            adapter.notifyDataSetChanged()
                            checkingLists = false
                        } catch (e: Exception) {
                            Log.w("FIREBASE", "Exception caught")
                            e.printStackTrace()
                        }

                    }
                    .addOnFailureListener { e ->
                        Log.w("FIREBASE", "Error getting document", e)
                    }

                 */



            } else {
                val id = activity?.db?.booksDao()?.get(it)?.id

                val action =
                    BooksFragmentDirections.actionNavigationBooksToBookDetailsFragment(
                        id!!,
                        list
                    )
                activity?.findNavController(R.id.nav_host_fragment_activity_main)
                    ?.navigate(action)
            }

        }

        adapter.onItemLongClick = {

            val item = it

            if (checkingLists) {

                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }

                builder?.setMessage(R.string.remove_list)
                    ?.setTitle(R.string.remove_list_title)
                    ?.setPositiveButton(
                        R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->

                            activity?.db?.gamesListDao()?.delete(item)

                            if (activity?.hasConnection == true && activity?.isSubscriber == true) {

                                db.collection("Users")
                                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                    .collection("BooksLists").document(item)
                                    .delete()
                                    .addOnSuccessListener {
                                        Log.d(
                                            "FIREBASE",
                                            "DocumentSnapshot successfully deleted!"
                                        )
                                    }
                                    .addOnFailureListener { e ->
                                        Log.w(
                                            "FIREBASE",
                                            "Error deleting document",
                                            e
                                        )
                                    }

                            }

                            adapter.remove(item)

                        }
                    )?.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->  })

                val dialog: AlertDialog? = builder?.create()

                if (dialog != null) {
                    dialog.show()
                }

            } else {

                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }

                builder?.setMessage(R.string.remove_item)
                    ?.setTitle(R.string.remove_item_title)
                    ?.setPositiveButton(
                        R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->

                            activity?.db?.booksDao()?.delete(item)

                            if (activity?.hasConnection == true && activity?.isSubscriber == true) {

                                db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                .collection("BooksLists").document(list).collection("list").document(item)
                                .delete()
                                .addOnSuccessListener { Log.d("FIREBASE", "DocumentSnapshot successfully deleted!") }
                                .addOnFailureListener { e -> Log.w("FIREBASE", "Error deleting document", e) }

                            }

                            adapter.remove(item)

                        }
                    )?.setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { dialogInterface, i ->  })

                val dialog: AlertDialog? = builder?.create()

                if (dialog != null) {
                    dialog.show()
                }

            }

            true
        }
    }

}