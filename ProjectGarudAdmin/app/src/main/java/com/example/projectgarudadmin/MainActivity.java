package com.example.projectgarudadmin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    EditText editText,editText2;
    Button button;
    String email,pass;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    TextView textView,textView9;
    ImageView imageView4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

                button=findViewById(R.id.button);
                editText=findViewById(R.id.editText);
                editText2=findViewById(R.id.editText2);
                textView=findViewById(R.id.textView);
                textView9=findViewById(R.id.textView9);
                imageView4=findViewById(R.id.imageView4);
                progressDialog=new ProgressDialog(this);
                firebaseAuth= FirebaseAuth.getInstance();
                firebaseDatabase=FirebaseDatabase.getInstance();

                email=editText.getText().toString();
                pass=editText2.getText().toString();

                FirebaseUser user=firebaseAuth.getCurrentUser();
                if(user != null){
                    finish();
                    startActivity(new Intent(MainActivity.this,ActiveUsers.class));
                }


                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            progressDialog.setMessage("Verifying....");
                            progressDialog.show();
                            if(Validate()){
                                firebaseAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(task.isSuccessful()){
                                            progressDialog.setMessage("Loading Details...");
                                                    finish();
                                                    startActivity(new Intent(MainActivity.this,ActiveUsers.class));
                                                    Toast.makeText(MainActivity.this, "Login Sucessfull", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Toast.makeText(MainActivity.this,"Login Failed",Toast.LENGTH_SHORT).show();
                                            progressDialog.dismiss();
                                            textView9.setVisibility(View.VISIBLE);
                                        }
                                    }
                                });
                            }else{
                                progressDialog.dismiss();
                            }
                    }
                });

            }
            boolean Validate(){
                boolean result= false;
                email=editText.getText().toString();
                pass=editText2.getText().toString();
                if(email.isEmpty() || pass.isEmpty()){
                    Toast.makeText(MainActivity.this,"Email or Password Can't be Empty",Toast.LENGTH_SHORT).show();
                }else if(email.equals("kapil882466@gmail.com")){
                    result=true;
                }else{
                    Toast.makeText(MainActivity.this,"You are Not Authorised Admin",Toast.LENGTH_SHORT).show();
                }
                return result;
            }
}
