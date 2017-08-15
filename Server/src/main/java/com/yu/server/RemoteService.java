package com.yu.server;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.os.SystemClock;
import android.util.Log;

import com.yu.aidl_test.aidl.Book;
import com.yu.aidl_test.aidl.IBookManager;
import com.yu.aidl_test.aidl.INewBookArriveListener;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RemoteService extends Service {
    // 支持并发读写
    CopyOnWriteArrayList<Book> bookList = new CopyOnWriteArrayList<>();
    // CopyOnWriteArrayList<INewBookArriveListener> listenerList = new CopyOnWriteArrayList<INewBookArriveListener>();
    RemoteCallbackList<INewBookArriveListener> callbackList = new RemoteCallbackList<>();
    private boolean mIsServiceDestory = false;

    public RemoteService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("com.yu.server", "onCreate");
        bookList.add(new Book(1, "art programming"));
        bookList.add(new Book(2, "first line code 2"));
        new Thread(new ServiceWorker()).start();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return bookManager;
    }

    private IBookManager.Stub bookManager = new IBookManager.Stub() {

        @Override
        public List<Book> getBookList() throws RemoteException {
            SystemClock.sleep(5000);  // 模拟耗时
            return bookList;
        }

        @Override
        public void addBook(Book book) throws RemoteException {
            if (book != null && bookList != null) {
                bookList.add(book);
                //    System.out.println("bookList.toString()=" + list2String(bookList));
            }
        }

        @Override
        public void registerNewBookArriveListener(INewBookArriveListener listener) throws RemoteException {
//            if (listener != null && listenerList != null) {
//                if (!listenerList.contains(listener)) {
//                    Log.e("com.yu.server", "registerNewBookArriveListener");
//                    listenerList.add(listener);
//                }
//            }
            callbackList.register(listener);
            Log.e("com.yu.server", "registerNewBookArriveListener:"+callbackList.getRegisteredCallbackCount());

        }

        @Override
        public void unregisterNewBookArriveListener(INewBookArriveListener listener) throws RemoteException {
//            if (listener != null && listenerList != null) {
//                if (listenerList.contains(listener)) {
//                    listenerList.remove(listener);
//                    Log.e("com.yu.server", "unregisterNewBookArriveListener");
//                } else {
//                    Log.e("com.yu.server", "listener not found,unregister failed");
//                }
//            }
            callbackList.unregister(listener);
            Log.e("com.yu.server", "unregisterNewBookArriveListener:"+callbackList.getRegisteredCallbackCount());
        }
    };



    protected String list2String(List<Book> list) {
        StringBuilder sb = new StringBuilder();
        for (Book b : list) {
            sb.append(b.toString() + "\n");
        }
        return sb.toString();
    }

    private class ServiceWorker implements Runnable {
        @Override
        public void run() {
            while (!mIsServiceDestory) {
                Book book = new Book(bookList.size(), "book" + bookList.size());
                bookList.add(book);
//                for (int i = 0; i < listenerList.size(); i++) {  // 通知更新
//                    INewBookArriveListener listener = listenerList.get(i);
//                    if (listener != null) {
//                        try {
//                            listener.onNewBookArrive(book);
//                            Log.e("com.yu.server", "listener.onNewBookArrive");
//                        } catch (RemoteException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
                int n = callbackList.beginBroadcast();
                for (int i = 0; i < n; i++) {
                    INewBookArriveListener listener = callbackList.getBroadcastItem(i);
                    try {
                        if (listener != null) listener.onNewBookArrive(book);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                callbackList.finishBroadcast();
                SystemClock.sleep(3000);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mIsServiceDestory = true;
    }
}
