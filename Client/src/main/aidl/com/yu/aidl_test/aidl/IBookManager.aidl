// IBookManager.aidl
package com.yu.aidl_test.aidl;

import com.yu.aidl_test.aidl.Book;
import com.yu.aidl_test.aidl.INewBookArriveListener;

interface IBookManager {

    // 获取图书
    List<Book> getBookList();
    // 添加书籍
    void addBook(in Book book);

    // 注册新书提醒
    void registerNewBookArriveListener(INewBookArriveListener listener);

    // 取消新书提醒
    void unregisterNewBookArriveListener(INewBookArriveListener listener);

}
