package com.ilyasmacit.art_book;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.ilyasmacit.art_book.databinding.ActivityDetailsBinding;

import java.io.ByteArrayOutputStream;

public class DetailsActivity extends AppCompatActivity {
    ActivityResultLauncher<Intent> galleryLauncher;
    ActivityResultLauncher<String> permissionLauncher;
    Bitmap selectedImage;
    SQLiteDatabase database;
    boolean info;

    private ActivityDetailsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailsBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        database = this.openOrCreateDatabase("Arts",MODE_PRIVATE, null);
        registerLauncher();

        Intent intent = getIntent();
        info = intent.getBooleanExtra("info", true);
        if(!info){
            int artId = intent.getIntExtra("Id",0);
            binding.saveButton.setVisibility(View.INVISIBLE);
            try{
                Cursor cursor = database.rawQuery("SELECT * FROM arts WHERE id = ? ", new String[] {String.valueOf(artId)});
                int artNameIx = cursor.getColumnIndex("artName");
                int artistNameIx = cursor.getColumnIndex("artistName");
                int yearIx = cursor.getColumnIndex("year");
                int imageIx = cursor.getColumnIndex("image");
                while(cursor.moveToNext()){
                    binding.artNameText.setText(cursor.getString(artNameIx));
                    binding.artistNameText.setText(cursor.getString(artistNameIx));
                    binding.yearText.setText(cursor.getString(yearIx));

                    byte[] bytes = cursor.getBlob(imageIx);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0 , bytes.length);
                    binding.imageView.setImageBitmap(bitmap);

                }
                cursor.close();
            }catch (Exception e){

            }
        }

    }
    public void save(View view){
        //sql
        String artName = binding.artNameText.getText().toString();
        String artistName = binding.artistNameText.getText().toString();
        String year = binding.yearText.getText().toString();

        Bitmap smallImage = scaledImage(selectedImage, 300);


        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        smallImage.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        try{
            database = this.openOrCreateDatabase("Arts",MODE_PRIVATE, null);
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (id INTEGER PRIMARY KEY, artName VARCHAR, artistName VARCHAR, year VARCHAR, image BLOB)");
            String SQLCode = "INSERT INTO arts (artName, artistName, year, image) VALUES (?, ?, ?, ?) ";
            SQLiteStatement sqLiteStatement = database.compileStatement(SQLCode);
            sqLiteStatement.bindString(1 , artName);
            sqLiteStatement.bindString(2, artistName);
            sqLiteStatement.bindString(3 , year);
            sqLiteStatement.bindBlob(4, byteArray);
            sqLiteStatement.execute();

        }catch (Exception e){
            e.printStackTrace();
        }

        Intent intent = new Intent(DetailsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

    }



    public void selectImage(View view){
      if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED ){
          //request permission
          if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)){
              Snackbar.make(view, "permission needed", Snackbar.LENGTH_INDEFINITE).setAction("give permission", new View.OnClickListener() {
                  @Override
                  public void onClick(View view) {
                      //request permission
                      permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);
                  }
              }).show();
          }else{
              //request permission
              permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE);

          }
      }else{
          //gallery
          Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          galleryLauncher.launch(intentToGallery);
      }

    }



    public void registerLauncher(){
        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if(result.getResultCode() == RESULT_OK){
                            Intent intentFromResult = result.getData();
                            if(intentFromResult != null){
                                Uri imageData = intentFromResult.getData();
                                try{
                                    if(Build.VERSION.SDK_INT >= 28){
                                        ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), imageData);
                                        selectedImage = ImageDecoder.decodeBitmap(source);
                                        binding.imageView.setImageBitmap(selectedImage);
                                    }

                                }catch (Exception e){}

                            }
                        }
                    }
                }
        );

        permissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                new ActivityResultCallback<Boolean>() {
                    @Override
                    public void onActivityResult(Boolean result) {
                        if(result){
                            //gallery
                            Intent intentToGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            galleryLauncher.launch(intentToGallery);

                        }else{
                            Toast.makeText(DetailsActivity.this, "permission needed", Toast.LENGTH_LONG);
                        }
                    }
                }
        );
    }

    public Bitmap scaledImage(Bitmap image, int maxSize){
        int width = image.getWidth();
        int height = image.getHeight();
        float ratio;
        if (width > height) {
            ratio = (float) maxSize / (float) width;
            height = (int) ( (float)height * ratio);
            width = maxSize;
        }
        else{
            ratio = (float) maxSize / (float) height;
            width = (int) ( (float)width * ratio);
            height = maxSize;
        }


        return Bitmap.createScaledBitmap(image, width, height, false);
    }
}