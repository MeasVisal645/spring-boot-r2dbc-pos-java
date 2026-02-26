package backend.Utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

public class FilteredWithNestedPaginationUtils {

    public static <P, C, R> Mono<PageResponse<R>> fetch(
            R2dbcEntityTemplate template,
            Class<P> primaryClass,
            Criteria criteria,
            Integer pageNumber,
            Integer pageSize,
            Sort sort,
            Function<P, Mono<List<C>>> fetchChildren,
            BiFunction<P, List<C>, R> resultMapper
    ) {

        int page = Optional.ofNullable(pageNumber).orElse(1);
        int size = Optional.ofNullable(pageSize).orElse(10);
        long offset = (long) (page - 1) * size;

        Query baseQuery = Query.query(criteria);

        if (sort != null) {
            baseQuery = baseQuery.sort(sort);
        }

        Query paginatedQuery = baseQuery.limit(size).offset(offset);

        Mono<Long> totalMono = template.count(baseQuery, primaryClass);

        Flux<R> contentFlux = template
                .select(primaryClass)
                .matching(paginatedQuery)
                .all()
                .flatMap(primary ->
                        fetchChildren.apply(primary)
                                .defaultIfEmpty(List.of())
                                .map(children ->
                                        resultMapper.apply(primary, children)
                                )
                );

        return totalMono.flatMap(total ->
                contentFlux.collectList()
                        .map(content ->
                                new PageResponse<>(content, page, size, total)
                        )
        );
    }
}