package com.yu.aidl_test;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.yu.aidl_test.aidl.Book;
import com.yu.aidl_test.aidl.IBookManager;
import com.yu.aidl_test.aidl.R;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    IBookManager bookManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = new Intent();
        // intent.setAction("com.yu.servercom.yu.server.RemoteService"); // 不可以隐式启动service
        intent.setClassName("com.yu.server", "com.yu.server.RemoteService");
        startService(intent);
        RemoteServiceConnection conn = new RemoteServiceConnection();
        bindService(intent, conn, Service.BIND_AUTO_CREATE);
    }

    class RemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                bookManager.addBook(new Book(3, "good work"));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    public void getBookList(View view) {
        if (bookManager != null) {
            try {
                List<Book> bookList = bookManager.getBookList();
                Log.e("com.yu.aidl_test", list2String(bookList));
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    public void addBook(View view) {
        if (bookManager != null) {
            int id = new Random(new Date().getTime()).nextInt();
            Book book = new Book(id, "book" + id);
            try {
                bookManager.addBook(book);
            } catch (RemoteException e) {
                e.printStackTrace();
            }

        }
    }

    protected String list2String(List<Book> list) {
        StringBuilder sb = new StringBuilder();
        for (Book b : list) {
            sb.append(b.toString() + "\n");
        }
        return sb.toString();
    }
}
