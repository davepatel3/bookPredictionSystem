package com.example.finalVersionBookProject.api;

import com.example.finalVersionBookProject.model.Book;
import com.example.finalVersionBookProject.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.sql.SQLException;
import java.util.List;

@Controller
public class BookController {
    private final BookService bookService;
    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping("/getPredictions")
    String getPredictions(Model model) throws SQLException, ClassNotFoundException{
        model.addAttribute("prediction", bookService.getBookPredictions());
        model.addAttribute("topTen", bookService.topTenBooks());

        return "getPredictions";
    }

    @RequestMapping(path = {"/", "/search"})
    public String viewHomePage(Book book, Model model, String keyword) throws SQLException, ClassNotFoundException {
        if(keyword!= null){
            List<Book> listBooks = bookService.getByKeyword(keyword);
            model.addAttribute("listBooks",listBooks);
        }else{
            List<Book> listBooks = bookService.getAllBooks();
            model.addAttribute("listBooks", listBooks);
        }
        return "index";
    }


}
