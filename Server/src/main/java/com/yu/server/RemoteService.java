package com.yu.server;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.yu.aidl_test.aidl.Book;
import com.yu.aidl_test.aidl.IBookManager;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteService extends Service {
    // 支持并发读写
    List<Book> bookList = new CopyOnWriteArrayList<>();

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("com.yu.server", "onCreate");
        bookList.add(new Book(1, "art programming"));
        bookList.add(new Book(2, "first line code 2"));

    }

    @Override
    public IBinder onBind(Intent intent) {
        return BookManager;
    }

    private Binder BookManager = new IBookManager.Stub() {

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public List<Book> getBookList() throws RemoteException {
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            if (book != null && bookList != null) {
                bookList.add(book);
            //    System.out.println("bookList.toString()=" + list2String(bookList));
            }
        }
    };

    protected String list2String(List<Book> list) {
        StringBuilder sb = new StringBuilder();
        for (Book b : list) {
            sb.append(b.toString()+"\n");
        }
        return sb.toString();
    }
}
