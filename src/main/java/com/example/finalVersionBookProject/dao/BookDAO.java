    package com.example.finalVersionBookProject.dao;

    import java.sql.*;

    import com.example.finalVersionBookProject.model.Book;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.NoRepositoryBean;
    import org.springframework.data.repository.query.Param;
    import org.springframework.stereotype.Repository;

    import java.util.List;

    @Repository
    @NoRepositoryBean  //this line seems like a problem
    public interface BookDAO extends JpaRepository<Book, Integer> {

        List<Book> displayPredictions() throws SQLException, ClassNotFoundException;

        List<Book> topTenBooks() throws SQLException, ClassNotFoundException;

    //    List<Book> getAllBooks() throws SQLException, ClassNotFoundException;


        @Query(value = "SELECT t FROM springBootProjectDB.dbo.book t WHERE springBootProjectDB.dbo.book.author LIKE %:keyword% OR springBootProjectDB.dbo.book.title LIKE %:keyword% OR springBootProjectDB.dbo.book.genre LIKE %:keyword%", nativeQuery = true)

        public List<Book> findAll(@Param("keyword") String keyword) throws SQLException, ClassNotFoundException;

    }
