package com.example.finalVersionBookProject.service;

import com.example.finalVersionBookProject.dao.BookDAO;
import com.example.finalVersionBookProject.model.Book;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
public class BookService {
    private final BookDAO bookDAO;
    @Autowired
    public BookService(@Qualifier("bookInterface") BookDAO bookDAO){
        this.bookDAO = bookDAO;
    }

    public List<Book> getBookPredictions() throws SQLException, ClassNotFoundException {
        return bookDAO.displayPredictions();
    }

    public List<Book> topTenBooks()throws SQLException, ClassNotFoundException{
        return bookDAO.topTenBooks();
    }


    public List<Book> getAllBooks() throws SQLException, ClassNotFoundException{
        List<Book> list = (List<Book>) bookDAO.findAll();
        return list;
    }

    public List<Book> getByKeyword(String keyword) throws SQLException, ClassNotFoundException {
        return bookDAO.findAll(keyword);
    }

}
