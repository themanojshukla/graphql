package com.psauer.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.psauer.graphql.model.Author;
import com.psauer.graphql.model.Book;
import com.psauer.graphql.persistence.AuthorRepository;
import com.psauer.graphql.persistence.BookRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Query implements GraphQLQueryResolver {

  private BookRepository bookRepository;

  private AuthorRepository authorRepository;

  public Iterable<Book> findAllBooks() {
    return bookRepository.findAll();
  }

  public Iterable<Author> findAllAuthors() {
    return authorRepository.findAll();
  }

  public long countBooks() {
    return bookRepository.count();
  }

  public long countAuthors() {
    return authorRepository.count();
  }

}
