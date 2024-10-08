package com.example.bt3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class AddPhone extends AppCompatActivity {
    EditText name,number;
    Button  cancel,ok;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addphone);
        name=findViewById(R.id.name);
        number=findViewById(R.id.phone);
        cancel=findViewById(R.id.cancelButton);
        ok=findViewById(R.id.okButton);
        cancel.setOnClickListener(v -> {
            finish();
        });
        ok.setOnClickListener(v -> {
            String name=this.name.getText().toString();
            String number =this.number.getText().toString();
            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(AddPhone.this,"please fill all field",Toast.LENGTH_LONG).show();}
            else {
                Toast.makeText(AddPhone.this,"add phone successfully",Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", name);
                resultIntent.putExtra("number",number);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
