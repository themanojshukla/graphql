package com.psauer.graphql;

import com.coxautodev.graphql.tools.SchemaParser;
import com.psauer.graphql.exception.GraphQLErrorAdapter;
import com.psauer.graphql.persistence.AuthorRepository;
import com.psauer.graphql.persistence.BookRepository;
import com.psauer.graphql.resolver.BookResolver;
import com.psauer.graphql.resolver.Mutation;
import com.psauer.graphql.resolver.Query;
import graphql.ExceptionWhileDataFetching;
import graphql.GraphQLError;
import graphql.execution.AsyncExecutionStrategy;
import graphql.execution.ExecutionStrategy;
import graphql.schema.GraphQLSchema;
import graphql.servlet.GraphQLErrorHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import graphql.servlet.GraphQLServlet;
import graphql.servlet.SimpleGraphQLServlet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class GraphqlApplication {

	@Autowired
	AuthorRepository ar;

	@Autowired
	BookRepository br;

	public static void main(String[] args) {
		SpringApplication.run(GraphqlApplication.class, args);
	}

	@Bean
	public ServletRegistrationBean servletRegistrationBean() {

		GraphQLSchema schema  = SchemaParser.newParser()
				.resolvers(authorResolver(ar), mutation(br, ar), query(br, ar))
				.file("graphql/author.graphqls")
				.file("graphql/book.graphqls")
				.build().makeExecutableSchema();
		ExecutionStrategy executionStrategy = new AsyncExecutionStrategy();
		GraphQLServlet servlet = new SimpleGraphQLServlet(schema, executionStrategy);
		ServletRegistrationBean bean = new ServletRegistrationBean(servlet, "/graphql");
		return bean;
	}

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

	@Bean
	public WebMvcConfigurer corsConfigurer() {
		return new WebMvcConfigurerAdapter() {
			@Override
			public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("*").allowedOrigins("http://localhost:*");
			}
		};
	}

}
