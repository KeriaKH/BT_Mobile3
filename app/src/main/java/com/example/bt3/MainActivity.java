package com.example.bt3;

import androidx.core.content.ContextCompat;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import android.content.Context;
import androidx.loader.content.Loader;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class Phone {
    public String name;
    public String number;
    public boolean isSelected;
    public Phone(String name, String number) {
        this.name = name;
        this.number = number;
        this.isSelected=false;
    }
}

class callLog {
    public String number;
    public String date;
    public String type;
    public callLog(String number,String date,String type)
    {
        this.number=number;
        this.date=date;
        this.type=type;
    }
}
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private List<Phone> phones;
    private static final int CONTACT_LOADER = 1;
    private boolean isASC = true;
    private ListView listView;
    private Button addphone,viewCallLog;

    private PhoneListAdapter phoneListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        LoaderManager.getInstance(this).initLoader(CONTACT_LOADER, null, this);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CALL_LOG)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        }
        phones=new ArrayList<>();
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        listView = findViewById(R.id.listview);
        phoneListAdapter = new PhoneListAdapter(this, R.layout.item, phones);
        listView.setAdapter(phoneListAdapter);
        addphone=findViewById(R.id.button2);
        addphone.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPhone.class);
            startActivityForResult(intent,1);
        });
        viewCallLog=findViewById(R.id.button);
        viewCallLog.setOnClickListener(v -> {
            Intent intent=new Intent(this, CallLogActivity.class);
            startActivity(intent);
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, EditPhone.class);
            intent.putExtra("name", phones.get(position).name);
            intent.putExtra("number", phones.get(position).number);
            intent.putExtra("position",position);
            startActivityForResult(intent,1);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK ) {
            String name = data.getStringExtra("name");
            String number = data.getStringExtra("number");
            int position=data.getIntExtra("position",-1);
            if(position!=-1)
            {
                phones.set(position,new Phone(name, number));
                phoneListAdapter.notifyDataSetChanged();
            }
            else
            {
                phones.add(new Phone(name, number));
                phoneListAdapter.notifyDataSetChanged();
            }
        }
    }
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {

            if(item.getItemId()==R.id.option1)
            {
                isASC=true;
                phoneListAdapter.clear();
                LoaderManager.getInstance(this).restartLoader(CONTACT_LOADER, null, this);
            }
            else if(item.getItemId()==R.id.option2)
            {
                isASC=false;
                phoneListAdapter.clear();
                LoaderManager.getInstance(this).restartLoader(CONTACT_LOADER, null, this);
            }
            else if (item.getItemId()==R.id.option3)
            {
                phoneListAdapter.setShowCheckboxes(true);
                phoneListAdapter.notifyDataSetChanged();
            } else if (item.getItemId()==R.id.option4) {
                List<Phone> phonesToRemove = new ArrayList<>();
                for (Phone phone : phones) {
                    if (phone.isSelected==true) {
                        phonesToRemove.add(phone);
                    }
                }
                phones.removeAll(phonesToRemove);
                phoneListAdapter.notifyDataSetChanged();
                return true;
            }

        return super.onOptionsItemSelected(item);
        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted to read contacts", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "Permission denied to read contacts", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        if (id == CONTACT_LOADER) {
            Log.d("MainActivity", "Loader created");
            String[] SELECTED_FIELDS = new String[] {
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                    ContactsContract.CommonDataKinds.Phone.NUMBER,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            };
            return new CursorLoader(this, ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    SELECTED_FIELDS,
                    null,
                    null,
                    ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " " + (isASC ? "ASC" : "DESC"));
        }
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (loader.getId() == CONTACT_LOADER) {
            if (data != null) {
                Log.d("MainActivity", "Number of contacts: " + data.getCount());
                while (!data.isClosed() && data.moveToNext()) {
                    String number = data.getString(1);
                    String name = data.getString(2);
                    phones.add(new Phone(name, number));
                }
                phoneListAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        loader = null;
    }
}



class PhoneListAdapter extends ArrayAdapter<Phone> {
    int resource;
    private List<Phone> phones;
    private boolean showCheckboxes = false;
    public PhoneListAdapter(Context context, int resource, List<Phone> phones) {
        super(context, resource, phones);
        this.phones = phones;
        this.resource = resource;

    }
    public void setShowCheckboxes(boolean show) {
        this.showCheckboxes = show;
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource, null);
        }

        Phone phone = getItem(position);
        if (phone != null) {
            CheckBox checkBox = v.findViewById(R.id.checkBox);
            TextView idTextView = v.findViewById(R.id.textView3);
            TextView nameTextView = v.findViewById(R.id.textView);
            TextView numberTextView = v.findViewById(R.id.textView2);
            if (checkBox != null) {
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    phone.isSelected = isChecked;
                });
                checkBox.setChecked(phone.isSelected);
                checkBox.setVisibility(showCheckboxes ? View.VISIBLE : View.GONE);
            }
            if (nameTextView != null) {
                nameTextView.setText(phone.name);
            }
            if (numberTextView != null) {
                numberTextView.setText(phone.number);
            }
            if (idTextView != null) {
                idTextView.setText(String.valueOf(position));
            }
        }
        return v;
    }
}

class CallLogAdapter extends ArrayAdapter<callLog> {
    int resource;
    private List<callLog> callLogs;
    public CallLogAdapter(Context context, int resource, List<callLog> callLogs) {
        super(context, resource, callLogs);
        this.callLogs = callLogs;
        this.resource = resource;

    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource, null);
        }

        callLog calllog = getItem(position);
        if (calllog != null) {
            TextView numberTextView = v.findViewById(R.id.textView);
            TextView dateTextView = v.findViewById(R.id.textView2);
            ImageView imageView=v.findViewById(R.id.image);
            if (numberTextView != null) {
                numberTextView.setText(calllog.number);
            }
            if (dateTextView != null) {
                dateTextView.setText(calllog.date);
            }
            if (imageView != null) {
                switch (calllog.type){
                    case "Outgoing":
                        imageView.setImageResource(R.drawable.outgoingcall);
                        break;
                    case "Incoming":
                        imageView.setImageResource(R.drawable.incomingcall);
                        break;
                    case "Missed":
                        imageView.setImageResource(R.drawable.missedcall);
                        break;
                    case "Rejected":
                        imageView.setImageResource(R.drawable.rejected);
                        break;
                    default:
                        break;
                }
            }
        }
        return v;
    }
}