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
import com.yu.aidl_test.aidl.INewBookArriveListener;
import com.yu.aidl_test.aidl.R;

import java.util.Date;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    IBookManager bookManager;
    RemoteDeathRecipient recipient;

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

    // 实现INewBookArriveListener.Stub
    private INewBookArriveListener mListener = new INewBookArriveListener.Stub(){

        @Override
        public void onNewBookArrive(Book book) throws RemoteException {  // 运行与远程binder线程池，需切换到主线程
            Log.e("TAG", "NewBookArrive=" + book.toString());
        }
    };

    class RemoteServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            bookManager = IBookManager.Stub.asInterface(service);
            try {
                // 注册新书提醒 registerNewBookArriveListener
                bookManager.registerNewBookArriveListener(mListener);
                Log.e("com.yu.aidl_test", "registerNewBookArriveListener");
                recipient = new RemoteDeathRecipient();
                service.linkToDeath(recipient, 0);   // 注册死亡监听
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    class RemoteDeathRecipient implements IBinder.DeathRecipient {

        @Override
        public void binderDied() {
            if (bookManager == null) return;
            bookManager.asBinder().unlinkToDeath(recipient, 0);
            bookManager = null;
            try {
                bookManager.unregisterNewBookArriveListener(mListener);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            // 可以选择重新绑定服务
        }
    }

    public void getBookList(View view) {
        if (bookManager != null) {
            new Thread() {
                @Override
                public void run() {
                    try {
                        // 获取远程数据会挂起当前线程，放在子线程中执行
                        List<Book> bookList = bookManager.getBookList();
                        Log.e("com.yu.aidl_test", list2String(bookList));
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();


        }
    }

    public void addBook(View view) {
        if (bookManager != null) {
            int id = new Random(new Date().getTime()).nextInt();
            final Book book = new Book(id, "book" + id);
            new Thread() {
                @Override
                public void run() {
                    try {
                        bookManager.addBook(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    public void register(View view) {
        try {
            bookManager.registerNewBookArriveListener(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }


    public void unregister(View view) {
        try {
            bookManager.unregisterNewBookArriveListener(mListener);
        } catch (RemoteException e) {
            e.printStackTrace();
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
