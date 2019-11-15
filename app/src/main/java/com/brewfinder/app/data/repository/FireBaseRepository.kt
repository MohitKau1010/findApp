package com.brewfinder.app.data.repository

import android.net.Uri
import android.util.Log
import com.brewfinder.app.BrewFinder
import com.brewfinder.app.data.model.CommentSection
import com.brewfinder.app.data.model.Deal
import com.brewfinder.app.data.model.Favourite
import com.brewfinder.app.data.model.User
import com.brewfinder.app.data.sharedpreference.PrefrenceUtil
import com.brewfinder.app.utils.Constants
import com.brewfinder.app.utils.Constants.Storage_Base_URL
import com.brewfinder.app.utils.FirebaseNetworkCallBack
import com.firebase.geofire.GeoFire
import com.firebase.geofire.GeoLocation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


open class FireBaseRepository {

    var user: User? = null

    private var mDealKey: String = ""

    private val mFireBaseAuth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val mFireBaseDB: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_DB_URL)
    }

    private val mFireBaseDbGeoFire: DatabaseReference by lazy {
        FirebaseDatabase.getInstance().getReferenceFromUrl(Constants.FIREBASE_DB_GEOFIRE_URL)
    }

    var mGeoFire = GeoFire(mFireBaseDbGeoFire)

    private val mFireBaseStorage: StorageReference by lazy {
        FirebaseStorage.getInstance().getReferenceFromUrl(Storage_Base_URL)
    }


    /**
     * Sign in user on fireBase db with email and password
     * @param email email of user which is used for sign in
     * @param password password of user which is used fro sign in
     * @return Return data with listener to calling view based on result
     */
    fun fireBaseUser(email: String, password: String, listener: FirebaseNetworkCallBack) {
        mFireBaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            when {
                task.isSuccessful -> {
                    if (mFireBaseAuth.currentUser != null)
                        listener.onSuccess(task.result?.user as FirebaseUser)

//                    PrefrenceUtil(BrewFinder.applicationContext()).userId=task.result?.user?.uid
                }
                else -> {
                    listener.onError(task.exception?.localizedMessage!!)
                }
            }

        }
    }

    /**
     * Sign up user on fireBase db with email and password
     * @param email email of user which is used for sign up
     * @param password password of user which is used fro sign up
     * @return Return data with listener to calling view based on result
     */
    fun fireBaseSignUp(email: String, password: String, listener: FirebaseNetworkCallBack) {
        mFireBaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                when {
                    task.isSuccessful -> listener.onSuccess(task.result?.user!!)

                    else -> listener.onError(task.exception!!.toString())
                }
            }

    }

    //get instance of current user
    fun currentUser() = mFireBaseAuth.currentUser

    //
    fun isLoggedIn(): Boolean {
        return mFireBaseAuth.currentUser != null
    }

    /**
     * Logout from fireBase
     */
    fun makeSignOut() {
        mFireBaseAuth.signOut()
        PrefrenceUtil(BrewFinder.applicationContext()).clearPrefsData()
    }


    //get Current User Id
    private fun getUserId(): String {
        return mFireBaseAuth.currentUser?.uid.toString()
    }


    //for reset paswrod
    fun sendEmailToEmail(email: String, callBack: FirebaseNetworkCallBack) {
        mFireBaseAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callBack.onSuccess("Check mEmail to reset your mPassword!")

                } else {
                    callBack.onError("Something went wrong!!")
                }
            }

    }


///////////////////////////////////////////////////////// DATABASE ////////////////////////////////////////////////////


    //region User Auth Function
    fun onCreateUser(firebaseAuthId: String, user: User, callBack: FirebaseNetworkCallBack) {

        // this User model
        // Create Database on firebase Database
        mFireBaseDB.child("users").child(firebaseAuthId).setValue(user)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //listnerDB?.onDBSuccess("Database Added Successfully")
                    callBack.onSuccess("User Is Created")
                } else {
                    //listnerDB?.onDBFailure("Data Not Added")
                    callBack.onError(task.exception!!.localizedMessage!!)

                }
            }
    }
    //endregion


    //ONLY FOR Login AND SPLASH  ///fetch user data when login splash screen
    fun fetchUserData(uid: String, callBack: FirebaseNetworkCallBack) {

        mFireBaseDB.child("users").child(uid)
            .addListenerForSingleValueEvent(object : ValueEventListener {


                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Get user information
                    val user = dataSnapshot.getValue(User::class.java)

                    //saves user Data
                    PrefrenceUtil(BrewFinder.applicationContext()).apply {
                        userId = user?.id
                        userProfile = user!!
                    }

                    // System.out.println(user)
                    if (user?.user_type == null) {
                        callBack.onSuccess("null")
                    } else {
                        callBack.onSuccess(user.user_type!!)
                    }
                    // Toast.makeText(this,"Data is added Now"+user,Toast.LENGTH_LONG).show()

//                  val authorName = user!!.user_name
//                  // Create new comment field_comment_text
//                  val commentText = field_comment_text.text.toString()
//                  val comment = Comment(uid, authorName, commentText)
                }
//                  val comment = CommentSection(uid, authorName, commentText)


                override fun onCancelled(databaseError: DatabaseError) {
                    callBack.onError(databaseError.toString())
                    //System.out.println(databaseError)
                }
            })
    }

    fun onCreateDeals(deal: Deal, callBack: FirebaseNetworkCallBack) {

        val database = mFireBaseDB
        val ref = database.child("Deals")

        mDealKey = database.push().key.toString()
        //this returns the unique key generated by firebase
        deal.deal_id = mDealKey

        // this User model
        // Create Database on firebase Database
        ref.child(mFireBaseAuth.uid.toString()).child(mDealKey).setValue(deal)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    //listnerDB?.onDBSuccess("Database Added Successfully")
                    callBack.onSuccess("Deals Is Created")
                } else {
                    //listnerDB?.onDBFailure("Data Not Added")
                    callBack.onError(task.exception!!.localizedMessage!!)

                }
            }
    }

    fun onDeleteDeals(dealId: String, callBack: FirebaseNetworkCallBack) {

        val database = mFireBaseDB
        val ref = database.child("Deals").child(mFireBaseAuth.uid.toString())

        mDealKey = database.push().key.toString()
        //this returns the unique key generated by firebase

        // this User model
        // Create Database on firebase Database
        ref.child(dealId).removeValue().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //listnerDB?.onDBSuccess("Database Added Successfully")
                callBack.onSuccess("Deal Is Deleted")
            } else {
                //listnerDB?.onDBFailure("Data Not Added")
                callBack.onError(task.exception!!.localizedMessage!!)
            }
        }
    }

    /**
     * updating favourite in deals
     * @param fav which is reference of Favourite Model
     * @return Return data with listener to calling view based on result
     */
    fun updateFavouriteDeal(fav: Favourite, callBack: FirebaseNetworkCallBack) {
        val database = mFireBaseDB

        val ref = database.child("Favourite") //making node of Favourite in DB
        val dealId = fav.deal_id     //fetching deal_id whose status need to be updated

        //adding deals to user id(child of favourite) which has "Favourite model" in its value
        val refUpdate: Task<Void>
        refUpdate = if (fav.is_fav == "false") {
            ref.child(PrefrenceUtil(BrewFinder.applicationContext()).userId!!).child(dealId)
                .removeValue()
        } else {
            ref.child(PrefrenceUtil(BrewFinder.applicationContext()).userId!!).child(dealId)
                .setValue(fav)
        }
        refUpdate.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //listnerDB?.onDBSuccess("Database Added Successfully")
                callBack.onSuccess("Favourite Updated")
            } else {
                //listnerDB?.onDBFailure("Data Not Added")
                callBack.onError(task.exception!!.localizedMessage!!)

            }
        }

    }

    // through this user is fetching all deals in deal fragment
    fun getUserDealDB(callBack: FirebaseNetworkCallBack) {
        //to fetch all the users of firebase Auth app
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dealList = arrayListOf<Deal>()
                dataSnapshot.children.forEach { ds ->
                    ds.children.forEach { item ->

                        val dealModel = item.getValue(Deal::class.java)
                        dealList.add(dealModel!!)

                        //getting user's fav deals
                        mFireBaseDB.child("Favourite")
                            .child(PrefrenceUtil(BrewFinder.applicationContext()).userId!!)
                            .addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) {}
                                override fun onDataChange(ds_favourite: DataSnapshot) {

                                    //to separate the array of keys adn to filter array to find data acc to specific key
                                    val dealsThatMatches =
                                        ds_favourite.children.map { dealItem -> dealItem.key }
                                            .filter { item.key == it }
                                    if (dealsThatMatches.isNotEmpty()) {
                                        for (deal in dealsThatMatches) {
                                            //find object in list and update its isFav value
                                            val dealUpdate = dealList.find { it.deal_id == deal }
                                            dealUpdate?.is_fav = "true"

                                            //update object in array
                                            val position =
                                                dealList.indexOf(dealList.find { it.deal_id == deal })
                                            dealList[position] = dealUpdate!!
                                        }
                                    }
                                    callBack.onSuccess(dealList)
                                }
                            })
                    }
                }
            }

            override fun onCancelled(v: DatabaseError) {
                callBack.onError(v.toString())
            }
        }

        mFireBaseDB.child("Deals").addListenerForSingleValueEvent(eventListener)

    }


    // through this user is fetch all deals in deal fragment
    fun getMuserDealDB(callBack: FirebaseNetworkCallBack) {
        //to fetch all the users of firebase Auth app
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                val dealList = arrayListOf<Deal>()
                dataSnapshot.children.forEach { ds ->
                    ds.children.forEach { item ->

                        val dealModel = item.getValue(Deal::class.java)
                        dealList.add(dealModel!!)
                        callBack.onSuccess(dealList)
                    }
                }
            }

            override fun onCancelled(v: DatabaseError) {
                callBack.onError(v.toString())
            }
        }


        mFireBaseDB.child("Deals").addListenerForSingleValueEvent(eventListener)

    }

    /**
     * getting user's own particular deal created
     */
    fun getMyDealDB(userId: String, callBack: FirebaseNetworkCallBack) {
        //to fetch all the users of firebase Auth app
        val eventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val arrayList = arrayListOf<Deal>()
                //if dataSnapshot has any children
                if (!dataSnapshot.hasChildren()) {
                    callBack.onError("list is empty")
                } else {
                    for (ds in dataSnapshot.children) {
                        val dealModel = ds.getValue(Deal::class.java)
                        arrayList.add(dealModel!!)
                        callBack.onSuccess(arrayList)
                    }
                }
            }

            override fun onCancelled(v: DatabaseError) {
                callBack.onError(v.toString())
            }
        }
        mFireBaseDB.child("Deals").child(userId).addListenerForSingleValueEvent(eventListener)
    }

    /**
     * getting user comments
     */
    fun getUserComments(dealID: String, callBack: FirebaseNetworkCallBack) {
        val commentList = arrayListOf<CommentSection>()
        Log.d("dealIdgetUserComments", dealID)
        val eventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                callBack.onError(p0.toString())
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                commentList.clear()
                if (snapshot.hasChildren()) {
                    snapshot.children.forEach {
                        val commentModel = it.getValue(CommentSection::class.java)
                        commentList.add(commentModel!!)

                    }
                    callBack.onSuccess(commentList)
                }
            }
        }
        mFireBaseDB.child(Constants.FIREBASE_NODE_COMMENTS).child(dealID)
            .addValueEventListener(eventListener)
    }


    /**
     * updating dealComments by getting dealId and commentID
     */
    fun updateDealComment(
        dealId: String,
        _comment: CommentSection,
        callBack: FirebaseNetworkCallBack
    ) {
        val database = mFireBaseDB

        val ref = database.child(Constants.FIREBASE_NODE_COMMENTS)

        val mCommentKey = database.push()
            .key.toString() //using it to generate a unique key every time data is a dded

        _comment.commentID = mCommentKey


        val refUpdate = ref.child(dealId).child(mCommentKey).setValue(_comment)

        refUpdate.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                callBack.onSuccess("Commented")
            } else {
                callBack.onError(task.exception!!.localizedMessage!!)
            }
        }

    }


//    //Delet User
//    fun deleteUser(user_id:String,callBack: FirebaseNetworkCallBack){
//
//        //mDB_deleteUser?.child(user_id.toString())
//
//        mFireBaseDB.child(user_id).removeValue().addOnSuccessListener {
//            // Write was successful!
//            // ...
//            callBack.onSuccess("User is Deleted")
//
//
//        }.addOnFailureListener {
//                // Write failed
//                // ...
//                callBack.onError(""+it)
//    //
//            }
//
//    }
//


//    var ref = FirebaseDatabase.getInstance().getReference("path/to/geofire")


    fun setLocationGeoFire(lat: Double, lng: Double) {  //key:String,
        //set location when hotdeal fragment  set Current loactionUserLocation

        mGeoFire.setLocation(
            "UserLocation",
            GeoLocation(lat, lng)
        ) { _, error ->
            if (error != null) {
                System.err.println("There was an error saving the location to GeoFire: $error")
            } else {
                println("Location saved on server successfully!")
            }
        }
        ////mGeoFire.removeLocation("firebase-hq");
    }

//    fun setDealLocationGeoFire(key: String, lat: Double, lng: Double) {
//        //set location when hotdeal fragment  set Current loactionUserLocation
//
////        mGeoFire.setLocation(key,GeoLocation(lat,lng), object : GeoFire.CompletionListener {
////            override fun onComplete(key: String?, error: DatabaseError?) {
////                if (error != null) {
////                    System.err.println("There was an error saving the location to GeoFire: $error")
////                } else {
////                    println("Location saved on server successfully!")
////                }
////            }
//
////        })
//        ////mGeoFire.removeLocation("firebase-hq");
//    }

/////////////////////////////////////////////////////////// STORAGE ////////////////////////////////////////////////////

    //upload deal images
    fun uploadDealImageToFireBaseStorage(fileUri: Uri?, callBack: FirebaseNetworkCallBack) {
        // Save the File URI
        if (fileUri != null) {
            val photoRef =
                mFireBaseStorage.child("/Storage").child("deal Images").child(getUserId())
                    .child(fileUri.lastPathSegment.toString())

            photoRef.putFile(fileUri).addOnSuccessListener {
                // Upload succeeded
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    val urilink = uri.toString()
                    callBack.onSuccess(urilink)
                }
//                callBack.onSuccess(it.metadata?.path.toString())
            }.addOnFailureListener { exception ->
                // Upload failed
                callBack.onError(exception.localizedMessage!!.toString())
            }
        }
    }

    /// only for busines logo upload
    @Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun uploadToFireBaseStorage(fileUri: Uri?, callBack: FirebaseNetworkCallBack) {
        // Save the File URI
        if (fileUri != null) {
            val photoRef = mFireBaseStorage.child("/Storage").child("/busines_logo")
                .child(fileUri.lastPathSegment.toString())

            photoRef.putFile(fileUri).addOnSuccessListener {
                // Upload succeeded
                it.storage.downloadUrl.addOnSuccessListener { uri ->
                    //                    Log.d(TAG, "onSuccess: uri= $uri")
                    val urilink = uri.toString()
                    callBack.onSuccess(urilink)
                }
            }.addOnFailureListener { exception ->
                // Upload failed=
                callBack.onError(exception.localizedMessage.toString())
            }
        }


    }


}