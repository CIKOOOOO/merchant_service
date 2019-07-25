package com.andrew.prototype.Activity;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.andrew.prototype.Model.Merchant;
import com.andrew.prototype.R;
import com.andrew.prototype.Utils.Constant;
import com.andrew.prototype.Utils.PrefConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initVar();
    }

    private void initVar() {
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(Constant.DB_REFERENCE_MERCHANT_PROFILE);
        Button submit = findViewById(R.id.btn_input_mid);
        final EditText input_mid = findViewById(R.id.edit_text_login_mid);


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (input_mid.getText().toString().isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Don't empty", Toast.LENGTH_SHORT).show();
                } else {
                    databaseReference.child(input_mid.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.getValue() == null) {
                                return;
                            }
                            Merchant merchant = dataSnapshot.getValue(Merchant.class);

                            PrefConfig prefConfig = new PrefConfig(LoginActivity.this);

                            prefConfig.insertMerchantData(merchant.getMID(), merchant.getMerchant_name()
                                    , merchant.getMerchant_location(), merchant.getMerchant_profile_picture()
                                    , merchant.getMerchant_email(), merchant.getMerchant_background_picture()
                                    , merchant.getMerchant_coin(), merchant.getMerchant_exp()
                                    , merchant.getMerchant_position());

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }
}
