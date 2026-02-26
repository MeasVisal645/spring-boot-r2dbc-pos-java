package backend.Utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

import java.util.function.Function;

public class FilteredWithPaginationUtils {

    public static final int DEFAULT_PAGE_NUMBER = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    // DTO
    public static <T, R> Mono<PageResponse<R>> fetch(
            R2dbcEntityTemplate template,
            Class<T> entityClass,
            Criteria criteria,
            Integer pageNumber,
            Integer pageSize,
            Sort sort,
            Function<T, R> mapper
    ) {
        int page = pageNumber != null ? pageNumber : DEFAULT_PAGE_NUMBER;
        int size = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        long offset = (long) (page - 1) * size;

        Query baseQuery = Query.query(criteria);
        if (sort != null) baseQuery = baseQuery.sort(sort);

        Query paginatedQuery = baseQuery.limit(size).offset(offset);

        Mono<Long> totalMono = template.count(baseQuery, entityClass);

        return totalMono.flatMap(total ->
                template.select(entityClass)
                        .matching(paginatedQuery)
                        .all()
                        .map(mapper)
                        .collectList()
                        .map(content -> new PageResponse<>(content, page, size, total))
        );
    }

    // Entity
    public static <T> Mono<PageResponse<T>> fetch(
            R2dbcEntityTemplate template,
            Class<T> entityClass,
            Criteria criteria,
            Integer pageNumber,
            Integer pageSize,
            Sort sort
    ) {
        return fetch(template, entityClass, criteria, pageNumber, pageSize, sort, Function.identity());
    }
}