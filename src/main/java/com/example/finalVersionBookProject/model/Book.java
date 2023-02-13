package com.example.finalVersionBookProject.model;

//import jakarta.persistence.Entity;
//import jakarta.persistence.Id;
//import jakarta.persistence.Table;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity  //annotates class to show that this is jpa entity
@Table(name = "springBootProjectDB.dbo.book")   //maps to table with this name
public class Book {
    private String author;
    private String title;
    //    private int cost;
//    private int numPages;
    private String genre;
    @Id  //public key in the related class
    private int BID;

    public Book(String author, String title, String genre){
        this.author = author;
        this.title = title;
//        this.numPages = numPages;
        this.genre = genre;
//        this.BID = BID;
    }

    public Book(){

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


}
