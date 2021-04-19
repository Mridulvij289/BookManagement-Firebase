package com.example.bookmanagement_firebase;

import static java.lang.Integer.parseInt;

public class Book {
    private String Title;
    private String Author;
    private String BookImage;
    private String id;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }




    public Book(){



    }

    public Book(String title, String author, String bookImage) {

        Title = title;
        Author = author;
        BookImage = bookImage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getAuthor() {
        return Author;
    }

    public void setAuthor(String author) {
        Author = author;
    }

    public String getBookImage() {
        return BookImage;
    }

    public void setBookImage(String bookImage) {
        BookImage = bookImage;
    }
}
