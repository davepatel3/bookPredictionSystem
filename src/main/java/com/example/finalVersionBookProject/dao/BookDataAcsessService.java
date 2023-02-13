package com.example.finalVersionBookProject.dao;

import com.example.finalVersionBookProject.model.Book;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

//import java.util.UUID;
@Repository("bookInterface")
public class BookDataAcsessService implements BookDAO{

    public BookDataAcsessService() throws SQLException {

    }


    public ResultSet getData(String selectSQL) throws SQLException, ClassNotFoundException {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=springBootProjectDB;integratedSecurity=true;";
//        jdbc:sqlserver://server:port;DatabaseName=dbname
        Connection con = DriverManager.getConnection(connectionUrl);
        ResultSet resultSet = null;
        Statement statement = con.createStatement();
        resultSet = statement.executeQuery(selectSQL);
        return resultSet;
    }

    public PreparedStatement updateData(String selectSQL) throws SQLException, ClassNotFoundException {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=springBootProjectDB;integratedSecurity=true;";
        Connection con = DriverManager.getConnection(connectionUrl);
        PreparedStatement preparedStatement = con.prepareStatement(selectSQL);
        return preparedStatement;
    }

    public Statement removeData(String selectSQL) throws SQLException, ClassNotFoundException {
        String connectionUrl = "jdbc:sqlserver://localhost:1433;databaseName=springBootProjectDB;integratedSecurity=true;";
        Connection con = DriverManager.getConnection(connectionUrl);
        Statement statement = con.createStatement();
        statement.executeUpdate(selectSQL);
        return statement;
    }

    public String createCombinedTemporaryTable(){
        String selectSQL = "DROP TABLE IF EXISTS bigTempTable \n" +
                "SELECT PID, title, author, genre, springBootProjectDB.dbo.sales.BID \n" +
                "INTO springBootProjectDB.dbo.bigTempTable\n" +
                "FROM springBootProjectDB.dbo.sales INNER JOIN springBootProjectDB.dbo.book ON springBootProjectDB.dbo.sales.BID = springBootProjectDB.dbo.book.BID \n" +
                "WHERE PID = 1\n";
        return selectSQL;
    }

    public String createTopTenTable(){
        String selectSQL = "DROP TABLE IF EXISTS topTenTable\n" +
                "SELECT TOP 10 springBootProjectDB.dbo.book.BID, title, author, genre\n" +
                "INTO springBootProjectDB.dbo.topTenTable \n" +
                "FROM springBootProjectDB.dbo.sales \n" +
                "INNER JOIN springBootProjectDB.dbo.book \n" +
                "ON springBootProjectDB.dbo.sales.BID = springBootProjectDB.dbo.book.BID\n" +
                "GROUP BY springBootProjectDB.dbo.book.BID, title, author, genre\n" +
                "ORDER BY COUNT(springBootProjectDB.dbo.book.BID) desc\n" +
                "\n";
        return selectSQL;
    }

    public String selectTopTen(){
        String selectSQL = "SELECT TOP 10 title, author, genre FROM springBootProjectDB.dbo.topTenTable";
        return selectSQL;
    }

    public String createTopFiveTableByAuthor(){
        String selectSQL = "DROP TABLE IF EXISTS predictionsByAuthor\n" +
                "                SELECT t.bid, t.author, t.title, t.genre\n" +
                "                INTO springBootProjectDB.dbo.predictionsByAuthor\n" +
                "                FROM  springBootProjectDB.dbo.topTenTable t \n" +
                "\t\t\t\twhere t.author in (SELECT author FROM springBootProjectDB.dbo.bigTempTable)\n" +
                "\t\t\t\tand\n" +
                "\t\t\t\tt.BID not in (select BID from springBootProjectDB.dbo.bigTempTable)";
        return selectSQL;
    }

    public String createTopFiveByGenre(){
        String selectSQL = " DROP TABLE IF EXISTS predictionsByGenre\n" +
                "SELECT t.bid, t.author, t.title, t.genre\n" +
                "INTO springBootProjectDB.dbo.predictionsByGenre\n" +
                "FROM  springBootProjectDB.dbo.topTenTable t \n" +
                "WHERE t.genre in (SELECT genre from springBootProjectDB.dbo.bigTempTable)\n" +
                "and t.BID not in (select BID from springBootProjectDB.dbo.bigTempTable)";
        return selectSQL;
    }

    public String createPredictionsTable(){
        String selectSQL = "DELETE FROM springBootProjectDB.dbo.combinedPredictionsTable\n" +
                "INSERT INTO springBootProjectDB.dbo.combinedPredictionsTable\n" +
                "SELECT * FROM springBootProjectDB.dbo.predictionsByAuthor  \n" +
                "INSERT INTO springBootProjectDB.dbo.combinedPredictionsTable\n" +
                "SELECT * FROM springBootProjectDB.dbo.predictionsByGenre\n" +
                "\n" +
                "SELECT DISTINCT author, title, genre \n" +
                "FROM springBootProjectDB.dbo.combinedPredictionsTable";
        return selectSQL;
    }

    public String getAllBooksFromTable(){
        String selectSQL = "SELECT author, title, genre FROM springBootProjectDB.dbo.book";
        return selectSQL;
    }




    @Override
    public List<Book> displayPredictions() throws SQLException, ClassNotFoundException{
        List<Book> getBookPredictions = new ArrayList<>();
        Statement s1 = removeData(createCombinedTemporaryTable());
        Statement s2 = removeData(createTopTenTable());
        Statement s3 = removeData(createTopFiveTableByAuthor());
        Statement s4 = removeData(createTopFiveByGenre());
        ResultSet r1 = getData(createPredictionsTable());
        while(r1.next()){
            getBookPredictions.add(new Book(r1.getString(1), r1.getString(2), r1.getString(3)));
        }
        return getBookPredictions;
    }

    @Override
    public List<Book> topTenBooks() throws SQLException, ClassNotFoundException {
        List<Book> topTenBooks = new ArrayList<>();
        ResultSet r1 = getData(selectTopTen());
        while(r1.next()){
            topTenBooks.add(new Book(r1.getString(1),r1.getString(2),r1.getString(3)));
        }
        return topTenBooks;

    }

//    @Override
//    public List<Book> getAllBooks() throws SQLException, ClassNotFoundException {
//        List<Book> allBooks = new ArrayList<>();
//        ResultSet r1 = getData(getAllBooksFromTable());
//        while(r1.next()){
//            allBooks.add(new Book(r1.getString(1),r1.getString(2),r1.getString(3)));
//        }
//        return allBooks;
//    }



    @Override
    public List<Book> findAll(String keyword) throws SQLException, ClassNotFoundException {
        List<Book> searchBooks = new ArrayList<>();
        String selectSQL = "DROP TABLE IF EXISTS tempSearchTable\n" +
                "                SELECT author, title, genre\n" +
                "                INTO springBootProjectDB.dbo.tempSearchTable\n" +
                "                FROM springBootProjectDB.dbo.book b\n" +
                "                WHERE CONTAINS(b.author, ?) OR CONTAINS(b.title, ?) OR CONTAINS(b.genre, ?)";
        PreparedStatement p1 = updateData(selectSQL);
        p1.setString(1,keyword);
        p1.setString(2,keyword);
        p1.setString(3,keyword);
        p1.executeUpdate();

        String selectSQL2 = "SELECT author, title, genre FROM springBootProjectDB.dbo.tempSearchTable";
        ResultSet r1 = getData(selectSQL2);
        while (r1.next()){
            searchBooks.add(new Book(r1.getString(1),r1.getString(2),r1.getString(3)));
        }
        return searchBooks;
    }

    @Override
    public List<Book> findAll () {
        List<Book> getAllBooks = new ArrayList<>();
        try {
            ResultSet r1 = getData(getAllBooksFromTable());
            while(r1.next()){
                getAllBooks.add(new Book(r1.getString(1), r1.getString(2), r1.getString(3)));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return getAllBooks;
    }

    @Override
    public List<Book> findAll(Sort sort) {
        return null;
    }

    @Override
    public Page<Book> findAll(Pageable pageable) {
        return null;
    }

    @Override
    public List<Book> findAllById(Iterable<Integer> integers) {
        return null;
    }

    @Override
    public long count() {
        return 0;
    }

    @Override
    public void deleteById(Integer integer) {

    }

    @Override
    public void delete(Book entity) {

    }

    @Override
    public void deleteAllById(Iterable<? extends Integer> integers) {

    }

    @Override
    public void deleteAll(Iterable<? extends Book> entities) {

    }

    @Override
    public void deleteAll() {

    }

    @Override
    public <S extends Book> S save(S entity) {
        return null;
    }

    @Override
    public <S extends Book> List<S> saveAll(Iterable<S> entities) {
        return null;
    }

    @Override
    public Optional<Book> findById(Integer integer) {
        return Optional.empty();
    }

    @Override
    public boolean existsById(Integer integer) {
        return false;
    }

    @Override
    public void flush() {

    }

    @Override
    public <S extends Book> S saveAndFlush(S entity) {
        return null;
    }

    @Override
    public <S extends Book> List<S> saveAllAndFlush(Iterable<S> entities) {
        return null;
    }

    @Override
    public void deleteAllInBatch(Iterable<Book> entities) {

    }

    @Override
    public void deleteAllByIdInBatch(Iterable<Integer> integers) {

    }

    @Override
    public void deleteAllInBatch() {

    }

    @Override
    public Book getOne(Integer integer) {
        return null;
    }

    @Override
    public Book getById(Integer integer) {
        return null;
    }

    @Override
    public Book getReferenceById(Integer integer) {
        return null;
    }

    @Override
    public <S extends Book> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends Book> List<S> findAll(Example<S> example) {
        return null;
    }

    @Override
    public <S extends Book> List<S> findAll(Example<S> example, Sort sort) {
        return null;
    }

    @Override
    public <S extends Book> Page<S> findAll(Example<S> example, Pageable pageable) {
        return null;
    }

    @Override
    public <S extends Book> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends Book> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends Book, R> R findBy(Example<S> example, Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }


}
