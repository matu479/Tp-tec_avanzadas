package ar.edu.palermo.telemedicina.core.exception;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Component;

/**
 * Equivalente del GlobalExceptionHandler para GraphQL: traduce nuestras excepciones a errores
 * GraphQL con un tipo claro (NOT_FOUND, BAD_REQUEST). El resto queda como error interno.
 */
@Component
public class GraphqlExceptionResolver extends DataFetcherExceptionResolverAdapter {

    @Override
    protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment entorno) {
        ErrorType tipo = tipoDeError(ex);
        if (tipo == null) {
            return null;
        }
        return GraphqlErrorBuilder.newError(entorno)
                .errorType(tipo)
                .message(ex.getMessage())
                .build();
    }

    private static ErrorType tipoDeError(Throwable ex) {
        if (ex instanceof RecursoNoEncontradoException) {
            return ErrorType.NOT_FOUND;
        }
        if (ex instanceof ReglaNegocioException || ex instanceof DatoInvalidoException) {
            return ErrorType.BAD_REQUEST;
        }
        return null;
    }
}
