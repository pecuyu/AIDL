// IBookManager.aidl
package com.yu.aidl_test.aidl;

import com.yu.aidl_test.aidl.Book;
// Declare any non-default types here with import statements

interface IBookManager {

    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,
            double aDouble, String aString);
    // 获取图书
    List<Book> getBookList();
    // 添加书籍
    void addBook(in Book book);
}
