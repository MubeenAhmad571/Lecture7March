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

public class Signup extends AppCompatActivity {

    private EditText emailET,passwordET;
    private Button signupBtn;

    private ProgressBar objectProgressBar;
    private FirebaseAuth objectFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        objectFirebaseAuth=FirebaseAuth.getInstance();

        connectXMLObjects();
    }
    public void login(View v)
    {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void connectXMLObjects()
    {
        try
        {
            emailET=findViewById(R.id.et_one);
            passwordET=findViewById(R.id.et_two);

            signupBtn=findViewById(R.id.btn_one);
            objectProgressBar=findViewById(R.id.signUpProgressBar);

            signupBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
              checkIfUserExists();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "connectXMLObjects:" +e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void checkIfUserExists()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    objectProgressBar.setVisibility(View.VISIBLE);


                    objectFirebaseAuth.fetchSignInMethodsForEmail(emailET.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                                    boolean check=task.getResult().getSignInMethods().isEmpty();
                                    if(!check)
                                    {

                                        objectProgressBar.setVisibility(View.INVISIBLE);

                                        Toast.makeText(Signup.this, "User already exists", Toast.LENGTH_SHORT).show();
                                    }
                                    else if(check)
                                    {

                                        signUpUser();
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    objectProgressBar.setVisibility(View.INVISIBLE);

                                    Toast.makeText(Signup.this, "Fails to check if user exists:"
                                            +e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
            else
            {
                emailET.requestFocus();
                Toast.makeText(this, "Please enter the email", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e)
        {

            objectProgressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "checkIfUserExists:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void signUpUser()
    {
        try
        {
            if(!emailET.getText().toString().isEmpty()
                    && !passwordET.getText().toString().isEmpty())
            {
                if(objectFirebaseAuth!=null)
                {
                    objectProgressBar.setVisibility(View.VISIBLE);

                    objectFirebaseAuth.createUserWithEmailAndPassword(emailET.getText().toString(),
                            passwordET.getText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    Toast.makeText(Signup.this, "User created Successfully", Toast.LENGTH_SHORT).show();
                                    if(authResult.getUser()!=null)
                                    {
                                        objectFirebaseAuth.signOut();
                                        emailET.setText("");

                                        passwordET.setText("");
                                        emailET.requestFocus();


                                        objectProgressBar.setVisibility(View.INVISIBLE);
                                    }
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    emailET.requestFocus();

                                    objectProgressBar.setVisibility(View.INVISIBLE);
                                    Toast.makeText(Signup.this, "Fails to create user:"
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
            Toast.makeText(this, "signUpUser:"+
                    e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}
