package com.plorrios.medialists.ui.music.Views

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
import com.plorrios.medialists.databinding.FragmentMusicBinding
import com.plorrios.medialists.ui.ListManagement.adapters.ListAdapter
import com.plorrios.medialists.ui.music.ViewModels.MusicViewModel
import java.lang.Exception
import java.lang.NullPointerException

class MusicFragment : Fragment() {

    private lateinit var musicViewModel: MusicViewModel
    private var _binding: FragmentMusicBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    var checkingLists : Boolean = true

    lateinit var db: FirebaseFirestore
    lateinit var listItems: ArrayList<String>
    lateinit var listItems2: ArrayList<String>
    lateinit var titlesartists: ArrayList<Pair<String,String>>

    lateinit var adapter: ListAdapter

    private var list = ""

    var activity = getActivity() as? MainActivity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        musicViewModel =
            ViewModelProvider(this).get(MusicViewModel::class.java)

        _binding = FragmentMusicBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.toolbar.inflateMenu(R.menu.top_menu)

        binding.toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.top_menu_add -> {
                    if (checkingLists) {
                        activity?.findNavController(R.id.nav_host_fragment_activity_main)
                            ?.navigate(R.id.action_navigation_music_to_addListFragment)
                    } else{
                        val action = MusicFragmentDirections.actionNavigationMusicToMusicSearchFragment(
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
        listItems = ArrayList<String>()
        listItems2 = ArrayList<String>()
        titlesartists = ArrayList<Pair<String,String>>()


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
        db.collection("Users").document(FirebaseAuth.getInstance().currentUser?.email.toString()).collection("MusicLists")
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

        val musicLists = activity?.db?.musicListDao()?.getAll()

        if (musicLists != null) {
            for (item in musicLists){
                listItems.add(item.id)
            }
        }

        adapter.dataSet = listItems

        adapter.notifyDataSetChanged()

        checkingLists = true

    }

    fun setAdapterClicks(){

        adapter.onItemClick = { item->

            if (checkingLists) {

                list = item

                db.collection("Users")
                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                    .collection("MusicLists").document(list).collection("list")
                    .get()
                    .addOnSuccessListener { documentReference ->
                        try {
                            listItems2.clear()
                            titlesartists.clear()
                            val items = documentReference.forEach { item ->
                                listItems2.add(item.id)
                                titlesartists.add(Pair(item.id,item.getString("artist")!!))
                            }
                            adapter.dataSet = listItems2
                            adapter.notifyDataSetChanged()
                            checkingLists = false
                        } catch (e: NullPointerException) {
                            val items = arrayListOf<String>()
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

            } else {
                val position = listItems2.indexOf(item)
                val action = MusicFragmentDirections.actionNavigationMusicToSongDetailsFragment(
                    titlesartists.get(position).first,
                    titlesartists.get(position).second,
                    list
                )
                activity?.findNavController(R.id.nav_host_fragment_activity_main)
                    ?.navigate(action)
            }

        }

        adapter.onItemLongClick = { item ->

            if (checkingLists) {

                val builder: AlertDialog.Builder? = activity?.let {
                    AlertDialog.Builder(it)
                }

                builder?.setMessage(R.string.remove_list)
                    ?.setTitle(R.string.remove_list_title)
                    ?.setPositiveButton(
                        R.string.ok,
                        DialogInterface.OnClickListener { dialog, id ->

                            if (activity?.hasConnection == true && activity?.isSubscriber == true) {


                                db.collection("Users")
                                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                    .collection("MusicLists").document(item)
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

                            if (activity?.hasConnection == true && activity?.isSubscriber == true) {

                                db.collection("Users")
                                    .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                                    .collection("MusicLists").document(list).collection("list")
                                    .document(item)
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

            }

            true
        }

    }

}