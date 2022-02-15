package com.plorrios.medialists

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.plorrios.medialists.databinding.ActivityMainBinding
import com.google.firebase.ktx.Firebase
import com.plorrios.medialists.ui.TV.Views.TVFragment
import com.plorrios.medialists.ui.books.Views.BooksFragment
import com.plorrios.medialists.ui.games.Views.GamesFragment
import com.plorrios.medialists.ui.music.Views.MusicFragment
import com.google.android.gms.ads.MobileAds
import android.content.Context

import android.widget.Toast
import androidx.room.Room

import com.google.firebase.auth.FirebaseAuthException
import com.plorrios.medialists.db.ListsDB
import java.lang.Exception

import android.net.ConnectivityManager


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    lateinit var googleSignInClient : GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    private val RC_SIGN_IN = 10

    lateinit var navView: BottomNavigationView

    lateinit var db: ListsDB

    var hasConnection = false
    var isSubscriber = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView

        MobileAds.initialize(this) {}

        val navController = findNavController(R.id.nav_host_fragment_activity_main)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_tv, R.id.navigation_games, R.id.navigation_music, R.id.navigation_books, R.id.invisible
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        for (i in 0 until navView.getMenu().size()) {
            navView.getMenu().getItem(i).setChecked(false)
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar()?.hide()
        }

        hasConnection = isNetworkAvailable()

        db = Room.databaseBuilder(
            applicationContext,
            ListsDB::class.java, "lists-db"
        ).allowMainThreadQueries().fallbackToDestructiveMigration().build()

        if (hasConnection) {

            auth = Firebase.auth

            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_google_web_client_id))
                .requestEmail()
                .build()

            googleSignInClient = GoogleSignIn.getClient(this, gso)

            signIn()

        }

    }

    override fun onStart() {
        super.onStart()

        if (hasConnection) {

            val onlinedb = Firebase.firestore

            val currentUser = auth.currentUser

            onlinedb.collection("Users")
                .document(FirebaseAuth.getInstance().currentUser?.email.toString())
                .get()
                .addOnSuccessListener { documentReference ->
                    try {
                        if (documentReference.getBoolean("subscriber")!!) {

                            //TODO update local database if not updated
                            isSubscriber = true

                        }
                    } catch (e: Exception) {
                        Log.w("FIREBASE", "Exception caught")
                        e.printStackTrace()
                    }

                }
                .addOnFailureListener { e ->
                    Log.w("FIREBASE", "Error getting document", e)
                }

        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return activeNetworkInfo != null && activeNetworkInfo.isConnected
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("FIREBASE", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("FIREBASE", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("FIREBASE", "signInWithCredential:success")
                    val user = auth.currentUser
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                        val e = task.exception as FirebaseAuthException?
                        Toast.makeText(
                            this@MainActivity,
                            "Failed Registration: " + e!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                    Log.w("FIREBASE", "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }

    override fun onBackPressed() {
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main)
        navHost?.let { navFragment ->
            navFragment.childFragmentManager.primaryNavigationFragment?.let {fragment->

                if (fragment is GamesFragment){
                    if(!fragment.checkingLists){
                        fragment.showLists()
                    } else {
                        super.onBackPressed()
                        for (i in 0 until navView.getMenu().size()) {
                            navView.getMenu().getItem(i).setChecked(false)
                        }
                    }

                } else if (fragment is TVFragment){

                    if(!fragment.checkingLists){
                        fragment.showLists()
                    } else {
                        super.onBackPressed()
                        for (i in 0 until navView.getMenu().size()) {
                            navView.getMenu().getItem(i).setChecked(false)
                        }
                    }

                } else if (fragment is MusicFragment){

                    if(!fragment.checkingLists){
                        fragment.showLists()
                    } else {
                        super.onBackPressed()
                        for (i in 0 until navView.getMenu().size()) {
                            navView.getMenu().getItem(i).setChecked(false)
                        }
                    }

                } else if (fragment is BooksFragment){

                    if(!fragment.checkingLists){
                        fragment.showLists()
                    } else {
                        super.onBackPressed()
                        for (i in 0 until navView.getMenu().size()) {
                            navView.getMenu().getItem(i).setChecked(false)
                        }
                    }

                } else {
                    super.onBackPressed()
                }
            }
        }
    }
}