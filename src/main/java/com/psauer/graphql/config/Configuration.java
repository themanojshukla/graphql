package com.psauer.graphql.config;

import com.psauer.graphql.exception.GraphQLErrorAdapter;
import com.psauer.graphql.persistence.AuthorRepository;
import com.psauer.graphql.persistence.BookRepository;
import com.psauer.graphql.resolver.BookResolver;
import com.psauer.graphql.resolver.Mutation;
import com.psauer.graphql.resolver.Query;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.servlet.GraphQLErrorHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.context.annotation.Bean;

public class Configuration {

  @Bean
  public GraphQLErrorHandler errorHandler() {
    return new GraphQLErrorHandler() {
      @Override
      public List<GraphQLError> processErrors(List<GraphQLError> errors) {
        List<GraphQLError> clientErrors = errors.stream()
            .filter(this::isClientError)
            .collect(Collectors.toList());

        List<GraphQLError> serverErrors = errors.stream()
            .filter(e -> !isClientError(e))
            .map(GraphQLErrorAdapter::new)
            .collect(Collectors.toList());

        List<GraphQLError> e = new ArrayList<>();
        e.addAll(clientErrors);
        e.addAll(serverErrors);
        return e;
      }

      protected boolean isClientError(GraphQLError error) {
        return !(error instanceof ExceptionWhileDataFetching || error instanceof Throwable);
      }
    };
  }

  @Bean
  public BookResolver authorResolver(AuthorRepository authorRepository) {
    return new BookResolver(authorRepository);
  }

  @Bean
  public Query query(BookRepository bookRepository, AuthorRepository authorRepository) {
    return new Query(bookRepository, authorRepository);
  }

  @Bean
  public Mutation mutation(BookRepository bookRepository, AuthorRepository authorRepository) {
    return new Mutation(bookRepository, authorRepository);
  }

}
