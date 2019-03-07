package com.example.personalbest;

import android.content.SharedPreferences;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class FriendsListActivity extends AppCompatActivity {
    ArrayList <String> arrayList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        ListView listView = findViewById(R.id.listView);
        arrayList = new ArrayList<>();
        fillArray(arrayList);

        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);

        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(FriendsListActivity.this, "you clicked: " + arrayList.get(i).toString(), Toast.LENGTH_SHORT).show();
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {


                DialogFragment optionsFrag = new OptionsFragment();
                Bundle bundle = new Bundle();
                bundle.putString("name", arrayList.get(i));
                optionsFrag.setArguments(bundle);
                optionsFrag.show(getSupportFragmentManager(), "Long Press");

                Toast.makeText(FriendsListActivity.this, "you long clicked: " + arrayList.get(i).toString(), Toast.LENGTH_SHORT).show();

                return false;
            }
        });
    }

    void fillArray(ArrayList <String> arr){
        //Put values into the arraylist

        arr.add("Hello");
        arr.add("World");
    }

    public void addFriend(View v){
        DialogFragment addFriendFragment = new AddFriendFragment();
        addFriendFragment.show(getSupportFragmentManager(), "Add Friend");

    }
}
