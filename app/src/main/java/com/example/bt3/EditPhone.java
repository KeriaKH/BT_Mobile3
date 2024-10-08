package com.example.bt3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditPhone extends AppCompatActivity {
    EditText name,number;
    Button  cancel,ok;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.changephone);
        name=findViewById(R.id.name);
        number=findViewById(R.id.number);
        cancel=findViewById(R.id.cancelButton);
        ok=findViewById(R.id.okButton);
        String Oname=getIntent().getStringExtra("name");
        String Onumber=getIntent().getStringExtra("number");
        int Oid=getIntent().getIntExtra("position",-1);
        name.setText(Oname);
        number.setText(Onumber);
        cancel.setOnClickListener(v -> {
            finish();
        });
        ok.setOnClickListener(v -> {
            String name=this.name.getText().toString();
            String number =this.number.getText().toString();
            if (name.isEmpty() || number.isEmpty()) {
                Toast.makeText(EditPhone.this,"please fill all field",Toast.LENGTH_LONG).show();}
            else {
                Toast.makeText(EditPhone.this,"Edit phone successfully",Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("name", name);
                resultIntent.putExtra("number",number);
                resultIntent.putExtra("position",Oid);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
    }
}
