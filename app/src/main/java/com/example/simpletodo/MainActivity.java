package com.example.simpletodo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final String KEY_ITEM_TEXT = "item_text";
    public static final String KEY_ITEM_POSITION = "item_position";
    public static final int EDIT_TEXT_CODE = 20;


    List<String> items;

    Button btnAdd;
    EditText etitem;
    RecyclerView rvitems;
    ItemsAdapter itemsAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = findViewById(R.id.btnAdd);
        etitem = findViewById(R.id.etitem);
        rvitems = findViewById(R.id.rvitems);

        loadItems();


        ItemsAdapter.OnLongClickListener onLongClickListener = new ItemsAdapter.OnLongClickListener() {
            @Override
            public void onItemLongClicked(int position) {
                //Delete the item from the model
                items.remove(position);
                //Notify the adapter
                itemsAdapter.notifyItemRemoved(position);
                Toast.makeText(getApplicationContext(), "Item was removed", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        };
        ItemsAdapter.OnClickListener onClickListener = new ItemsAdapter.OnClickListener() {
            @Override
            public void onItemClicked(int position) {
            //where we want to open up editing the new activity
                Log.d("MainActivity","Single click at position" + position);
                // create the new activity
                Intent i = new Intent(MainActivity.this,EditActivity.class);
                // pass the data being edited
                i.putExtra(KEY_ITEM_TEXT,items.get(position));
                i.putExtra(KEY_ITEM_POSITION,position);
                // display the activity
                startActivityForResult(i,EDIT_TEXT_CODE);
            }
        };
        itemsAdapter = new ItemsAdapter(items,onLongClickListener, onClickListener);
       rvitems.setAdapter(itemsAdapter);
       // going to put things on our list in a vertical way
       rvitems.setLayoutManager(new LinearLayoutManager(this));

       // We will add a click listener to the add button so we will be notified every time someone clicks the add button
        // then we will be able to take the corresponding action

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            // the onclick would mean someone has actually tapped on the button
            public void onClick(View v) {
              String todoItem = etitem.getText().toString();
              // Add item to the model
                items.add(todoItem);
                // Notify the adapter that an item is inserted
                itemsAdapter.notifyItemInserted(items.size() - 1);
                // clear the edit text once we have submitted it
                etitem.setText("");
                // we can give the adder feedback that they have added the item successfully through a toast
                // toast is a pop up or dialogue that pops up and then disappears
                // we put the .show method because we want the text to actually show up
                Toast.makeText(getApplicationContext(), "Item was added", Toast.LENGTH_SHORT).show();
                saveItems();
            }
        });

    }

    // handle the result of the edit activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode ==RESULT_OK && requestCode ==EDIT_TEXT_CODE){
        // retrieve the updated the text value
        String itemText = data.getStringExtra(KEY_ITEM_TEXT);
            // extract the original position of the edited item from the position key
            int position = data.getExtras().getInt(KEY_ITEM_POSITION);

            // update the model at the right position with the new item text
            items.set(position,itemText);
            // notify the adapter
            itemsAdapter.notifyItemChanged(position);
            // persist the changes
            saveItems();
            Toast.makeText(getApplicationContext(),"Item updated successfully!",Toast.LENGTH_SHORT).show();
        }else{
            Log.w("MainActivity","Unknown call to onActivityResult");
        }
    }

    // the idea is that the app will return to the file we left off on
    private File getDataFile (){
        //getFilesDir() we passed in the directory of this app and then we passed in the name of the file "data.txt"
    return new File(getFilesDir(), "data.txt");
    }

    // This function will load items by reading every line of the data file
    private void loadItems(){
        // we are saying read all the lines in our data and then
        // put that into an array list
        try {
            items = new ArrayList<>(FileUtils.readLines(getDataFile(), Charset.defaultCharset()));
        } catch (IOException e) {
            Log.e("MainActivity", "Error reading items", e);
            items = new ArrayList<>();
        }
    }
    // This function saves items by writing them into the data file
    private void saveItems() {
        try {
            FileUtils.writeLines(getDataFile(), items);
        } catch (IOException e) {
            Log.e("MainActivity", "Error writing items", e);
        }
    }

}