package com.example.library.repository;

import com.example.library.db.DBManager;
import com.example.library.db.entities.Author;
import com.example.library.db.entities.Book;
import com.example.library.db.entities.Bookshelf;
import com.example.library.db.entities.Genre;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;

@Data
@Repository
public class BookRepository {
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;
    private final BookshelfRepository bookshelfRepository;

    //КРЧ, НАДО СОСТАВИТЬ ЗАПРОС, КОТОРЫЙ ВОЗЬМЕТ ВСЕ ДАННЫЕ ЗА РАЗ, ЗАТЕМ ИХ ЗАПАРСИТЬ И ПОЛУЧИТЬ ОБЪЕКТ
    //ПОТОМУ ЧТО ДРУГИХ РЕПОЗИТОРЕВ НЕ ДОЛЖНО ТУТ БЫТЬ, НО Я СДЕЛАЛ, ЧТОБЫ СУТЬ БЫЛА ПОНЯТНО
    //РЕПОЗИТОРИИ АВТОРА, ЖАНРА ПО СУТИ СВОЕЙ ХАЛЯВА, ТК В НИХ НЕТУ ВНЕШНЕГО КЛЮЧА ИХ СРАЗУ МОЖНО ГАСИТЬ

    public Book findBookById(int id) throws SQLException {
        Connection connection = DBManager.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE id = ?");
        preparedStatement.setInt(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();

        if (resultSet.next()) {
            Bookshelf bookshelf = new Bookshelf();
            Integer author_id = resultSet.getInt("author_id");
            String name =  resultSet.getString("name");
            //автор

        }
        return null;
    }
}

