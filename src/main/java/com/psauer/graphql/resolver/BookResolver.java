package com.psauer.graphql.resolver;

import com.coxautodev.graphql.tools.GraphQLResolver;
import com.psauer.graphql.model.Author;
import com.psauer.graphql.model.Book;
import com.psauer.graphql.persistence.AuthorRepository;
import java.util.Optional;

public class BookResolver implements GraphQLResolver<Book> {

  private AuthorRepository authorRepository;

  public BookResolver(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  public Optional<Author> getAuthor(Book book) {
    return authorRepository.findById(book.getAuthor().getId());
  }

}
