package com.ilyasmacit.art_book;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ilyasmacit.art_book.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    ArrayList<Arts> artsArrayList = new ArrayList<>();

    ArtAdapter artAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);



        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        artAdapter = new ArtAdapter(artsArrayList);
        binding.recyclerView.setAdapter(artAdapter);

        getData();

    }

    private void getData(){
        try{
            SQLiteDatabase sqLiteDatabase = this.openOrCreateDatabase("Arts", MODE_PRIVATE, null);
            Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM arts", null);
            int nameIx = cursor.getColumnIndex("artName");
            int idIx = cursor.getColumnIndex("id");

            while (cursor.moveToNext()){
                String artName = cursor.getString(nameIx);
                int id = cursor.getInt(idIx);
                Arts art = new Arts(artName, id);
                artsArrayList.add(art);
            }

            cursor.close();
            //artAdapter.notifyDataSetChanged();

        }catch(Exception e ){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.art_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_art)
        {
            Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
            intent.putExtra("info",true);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}