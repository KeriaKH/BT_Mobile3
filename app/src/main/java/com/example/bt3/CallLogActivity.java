package com.example.bt3;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CallLogActivity extends AppCompatActivity {
    ListView listView;
    Button back;
    ArrayList<callLog> callLogs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calllog);
        listView=findViewById(R.id.calllog);
        back=findViewById(R.id.button);
        back.setOnClickListener(v -> {
            finish();
        });
        callLogs=new ArrayList<>();
        getCallDetails();
        CallLogAdapter callLogAdapter=new CallLogAdapter(this,R.layout.item2,callLogs);
        listView.setAdapter(callLogAdapter);
    }
    public void getCallDetails() {
        // Truy xuất URI của Call Log
        Uri callUri = android.provider.CallLog.Calls.CONTENT_URI;

        // Lấy content resolver để truy xuất dữ liệu
        ContentResolver contentResolver = getContentResolver();

        // Cột muốn truy xuất: số điện thoại, thời gian, loại cuộc gọi, thời lượng
        String[] projection = new String[]{
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DATE,
                android.provider.CallLog.Calls.TYPE
        };

        // Truy vấn Call Log
        Cursor cursor = contentResolver.query(callUri, projection, null, null, android.provider.CallLog.Calls.DATE + " DESC");

        // Duyệt qua các kết quả
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String number = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.NUMBER));
                String date = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.DATE));
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String dateString = formatter.format(new Date(Long.parseLong(date)));
                String type = cursor.getString(cursor.getColumnIndexOrThrow(android.provider.CallLog.Calls.TYPE));

                // Chuyển đổi loại cuộc gọi thành chuỗi có ý nghĩa
                String callType = null;
                int callTypeCode = Integer.parseInt(type);
                switch (callTypeCode) {
                    case android.provider.CallLog.Calls.OUTGOING_TYPE:
                        callType = "Outgoing";
                        break;
                    case android.provider.CallLog.Calls.INCOMING_TYPE:
                        callType = "Incoming";
                        break;
                    case android.provider.CallLog.Calls.MISSED_TYPE:
                        callType = "Missed";
                        break;
                    case android.provider.CallLog.Calls.REJECTED_TYPE:
                        callType = "Rejected";
                        break;
                    default:
                        callType = "Unknown";
                        break;
                }
                callLogs.add(new callLog(number,dateString,callType));
            }
            cursor.close();
        }
    }
}
