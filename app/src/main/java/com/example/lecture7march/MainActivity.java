package com.example.lecture7march;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;

public class MainActivity extends AppCompatActivity {

    private EditText emailET,passwordET;
    private Button loginBtn;

    private ProgressBar objectProgressBar;
    private FirebaseAuth objectFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        objectFirebaseAuth=FirebaseAuth.getInstance();
        connectXMLObjects();
    }

    private void connectXMLObjects()
    {
        try
        {
            emailET=findViewById(R.id.et_one);
            passwordET=findViewById(R.id.et_two);

            loginBtn=findViewById(R.id.btn_one);
            objectProgressBar=findViewById(R.id.signUpProgressBar);

            loginBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signInUser();
                }
            });


        }
        catch (Exception e)
        {
            Toast.makeText(this, "connectXMLObjects:" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void signup(View v)
    {
        startActivity(new Intent(this, Signup.class));
        finish();
    }


    private void signInUser()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty() && !passwordET.getText().toString().isEmpty())
            {

                if(objectFirebaseAuth.getCurrentUser()!=null)
                {
                    objectFirebaseAuth.signOut();
                    Toast.makeText(this, "User logged out successfully", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    objectProgressBar.setVisibility(View.VISIBLE);


                    objectFirebaseAuth.signInWithEmailAndPassword(emailET.getText().toString(),
                            passwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {

                                    objectProgressBar.setVisibility(View.INVISIBLE);

                                    Toast.makeText(MainActivity.this, "User log in", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this, MainPage.class));

                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    emailET.requestFocus();

                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(MainActivity.this, "Fails to sign in user:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else if(emailET.getText().toString().isEmpty())
            {

                objectProgressBar.setVisibility(View.INVISIBLE);

                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
            else if(passwordET.getText().toString().isEmpty())
            {

                objectProgressBar.setVisibility(View.INVISIBLE);

                passwordET.requestFocus();
                Toast.makeText(this, "Please enter the password", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {
            emailET.requestFocus();


            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "signInUser:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
