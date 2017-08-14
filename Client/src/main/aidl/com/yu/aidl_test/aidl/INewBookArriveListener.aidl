// INewBookArriveListener.aidl
package com.yu.aidl_test.aidl;

// Declare any non-default types here with import statements
import com.yu.aidl_test.aidl.Book;

interface INewBookArriveListener {
    void onNewBookArrive(in Book book);
}
