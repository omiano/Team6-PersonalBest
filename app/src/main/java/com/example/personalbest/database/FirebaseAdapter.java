package com.example.personalbest.database;


import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.personalbest.Goal;
import com.example.personalbest.SaveLocal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class FirebaseAdapter implements IFirebase {
    final String PROJECT_ID="PersonalBest";
    final String TAG="FirebaseAdapter";
    FirebaseFirestore db;
    SaveLocal saveLocal;

    public FirebaseAdapter(Activity activity) {
        FirebaseApp.initializeApp(activity);
        db = FirebaseFirestore.getInstance();
        // Create a new user with a first and last name
        saveLocal = new SaveLocal(activity);

    }
    @Override
    public void addUser(String userName, final String email){
        Map<String, Object> user = new HashMap<>();
        user.put("name", userName);
        user.put("email", email);


        // Add a new document with a generated ID
        db.collection("users")
                .document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + email);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void addFriendToFriendsList(String friendsName, final String friendsEmail) {
        final String myEmail = saveLocal.getEmail();
        final String myName = saveLocal.getName();

        Map<String, Object> friend = new HashMap<>();
        friend.put("email", friendsEmail);
        friend.put("name", friendsName);

        Map<String, Object> me = new HashMap<>();
        me.put("email", myEmail);
        me.put("name", myName);


        // Add a new document with a generated ID
        db.collection("users")
                .document(myEmail)
                .collection("friends")
                .document(friendsEmail)
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("users")
                .document(friendsEmail)
                .collection("friends")
                .document(myEmail)
                .set(me)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        db.collection("users")
                .document(friendsEmail)
                .collection("pendingFriends")
                .document(myEmail)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Removed my email: " + myEmail + " from friend: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    @Override
    public void addFriendToPendingFriendsList(final String friendsEmail){
        final String myEmail = saveLocal.getEmail();
        Map<String, Object> friend = new HashMap<>();
        friend.put("email", friendsEmail);


        // Add a new document with a generated ID
        db.collection("users")
                .document(myEmail)
                .collection("pendingFriends")
                .document(friendsEmail)
                .set(friend)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + friendsEmail);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    @Override
    public void addFriend(final String friendsEmail) {
        final String myEmail = saveLocal.getEmail();
        db.collection("users")
                .document(myEmail)
                .collection("friends")
                .document(friendsEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> arr = documentSnapshot.getData();
                        if (documentSnapshot.getData() == null) {
                            addFriendOfficial(friendsEmail);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add friend");
                    }
                });


    }

    private void addFriendOfficial(final String friendsEmail) {
        // Add a new document with a generated ID
        final String myEmail = saveLocal.getEmail();
        db.collection("users")
                .document(friendsEmail)
                .collection("pendingFriends")
                .document(myEmail)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> arr = documentSnapshot.getData();
                        if (documentSnapshot.getData() == null) {
                            addFriendToPendingFriendsList(friendsEmail);
                        } else {
                            addFriendToFriendsList("FRIEND NAME", friendsEmail);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Failed to add friend");
                    }
                });
    }

    @Override
    public void getUsers(){

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    @Override
    public void getFriends(String email){
        final ArrayList<String> arr = new ArrayList<>();

        db.collection("users").document(email)
                .collection("friends").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            arr.add(document.getId());
                        }

                        saveLocal.setFriends(arr);
                    }
                });
    }

    @Override
    public void pushStepStats(Calendar time, int backgroundSteps, int exerciseSteps, String myEmail){

        Calendar newCal=Calendar.getInstance();
        newCal.setTimeInMillis(time.getTimeInMillis());
        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);

        String dateKey=newCal.get(Calendar.DAY_OF_MONTH)+"-"+((int)newCal.get(Calendar.MONTH)+1)+"-"+newCal.get(Calendar.YEAR);


        Map<String, Integer> steps = new HashMap<>();
        steps.put("Background", backgroundSteps);
        steps.put("Exercise", exerciseSteps);
        // Add a new document with a generated ID
        db.collection("users")
                .document(myEmail)
                .collection("steps")
                .document(""+dateKey)
                .set(steps)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Steps set for Email:" + myEmail+" Date: "+dateKey);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error set steps", e);
                    }
                });
    }


    public Task<QuerySnapshot> saveFriendStepLocal(String friendEmail){
        Log.d("Query Results", "Went into method");
        Task<QuerySnapshot> task= db.collection("users")
                .document(friendEmail)
                .collection("steps")
                .get();

               return task;
    }

    public void pushNewGoal(Calendar time, int goal){
        Calendar newCal=Calendar.getInstance();
        newCal.setTimeInMillis(time.getTimeInMillis());
        newCal.set(Calendar.HOUR_OF_DAY,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MINUTE,0);
        newCal.set(Calendar.SECOND,0);
        newCal.set(Calendar.MILLISECOND,0);

        Date newGoalDate= new Date();
        newGoalDate.setTime(newCal.getTimeInMillis());
        String dateKey=newCal.get(Calendar.DAY_OF_MONTH)+"-"+(newCal.get(Calendar.MONTH)+1)+"-"+newCal.get(Calendar.YEAR);

        DocumentReference goalsDocument=db.collection("users")
                .document(saveLocal.getEmail())
                .collection("New Goals")
                .document("Goals");

        goalsDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();

                    Map<String, Object> map=document.getData();
                    if(map==null){
                        map=new HashMap<>();
                    }
                    Goal newGoal=new Goal(newGoalDate,goal);
                    map.put(dateKey,newGoal);
                    goalsDocument.set(map);
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
    public Task<DocumentSnapshot> saveNewGoalsLocal(String email) {
        DocumentReference goalsDocument = db.collection("users")
                .document(email)
                .collection("New Goals")
                .document("Goals");
        ArrayList<Goal> newGoals = new ArrayList<>();

        Task<DocumentSnapshot> task = goalsDocument.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> map = document.getData();
                        for (Object o : map.values()) {
                            HashMap<String, Object> returnedGoal = (HashMap<String, Object>) o;
                            Timestamp newTimestamp = (Timestamp) returnedGoal.get("date");
                            Date derivedDate = newTimestamp.toDate();
                            long stepCount = (long) returnedGoal.get("stepGoal");
                            Goal oneGoal = new Goal(derivedDate, stepCount);
                            newGoals.add(oneGoal);
                        }
                        saveLocal.setNewGoals(newGoals, email);
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

        return task;
    }

}
