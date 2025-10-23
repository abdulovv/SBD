package com.example.library.services;

import com.example.library.db.entities.Author;
import com.example.library.db.entities.Book;
import com.example.library.db.entities.Bookshelf;
import com.example.library.db.entities.Genre;
import com.example.library.repository.AuthorRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BookshelfRepository;
import com.example.library.repository.GenreRepository;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.sql.SQLException;

@Data
@Service
public class BookService {
    private final BookRepository bookRepository;

//    public Book assembleBook(Book bookFromRepo) throws SQLException {
//        Integer author_id = bookFromRepo.getAuthor().getAuthor_id();
//        Author author = authorRepository.findAuthorById(author_id);
//
//        Integer genre_id = bookFromRepo.getGenre().getGenre_id();
//        Genre genre = genreRepository.findGenreById(genre_id);
//
//        Integer bookshelf_id = bookFromRepo.getBookshelf().getShelf_id();
//        Bookshelf bookshelf = bookshelfRepository.findBookshelfById(bookshelf_id);
//
//        bookFromRepo.setAuthor(author);
//        bookFromRepo.setGenre(genre);
//        bookFromRepo.setBookshelf(bookshelf);
//
//        return bookFromRepo;
//    }
}
