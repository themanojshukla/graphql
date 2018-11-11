package com.psauer.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLMutationResolver;
import com.psauer.graphql.exception.BookNotFoundException;
import com.psauer.graphql.model.Author;
import com.psauer.graphql.model.Book;
import com.psauer.graphql.persistence.AuthorRepository;
import com.psauer.graphql.persistence.BookRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Mutation implements GraphQLMutationResolver {

  private BookRepository bookRepository;

  private AuthorRepository authorRepository;

  public Author newAuthor(String firstName, String lastName) {
    Author author = Author.builder()
        .firstName(firstName)
        .lastName(lastName)
        .build();

    return authorRepository.save(author);
  }

  public Book newBook(String title, String isbn, Integer pageCount, Long authorId) {

    Book book = Book.builder()
        .title(title)
        .isbn(isbn)
        .pageCount(pageCount != null ? pageCount : 0)
        .author(new Author(authorId))
        .build();

    return bookRepository.save(book);
  }

  public boolean deleteBook(Long id) {
    bookRepository.deleteById(id);
    return true;
  }

  public Book updateBookPageCount(Integer pageCount, Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new BookNotFoundException("The book to be updated was not found", id));

    book.setPageCount(pageCount);
    return bookRepository.save(book);
  }

}
