package com.example.lecture7march;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class MainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
    }


    public  void signout(View v)
    {
        try{
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
        catch (Exception e)
    {
        Toast.makeText(MainPage.this,"Signout Button"+e.getMessage(),Toast.LENGTH_LONG).show();
    }
    }

    public  void uploadimage(View v)
    {
        try{
        startActivity(new Intent(this, UploadImage.class));
        finish();
        }
        catch (Exception e)
        {
            Toast.makeText( MainPage.this,"Upload ImageButton"+e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
    public  void DownloadImage(View v)
    {
        try{
        startActivity(new Intent(this,  DownloadImage.class));
        finish();
        }
        catch (Exception e)
        {
            Toast.makeText(MainPage.this,"Download Image Button"+e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

}
