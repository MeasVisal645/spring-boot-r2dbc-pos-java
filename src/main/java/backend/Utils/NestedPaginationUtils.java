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

public class NestedPaginationUtils {

    public static <P, C, R> Mono<PageResponse<R>> fetchPagination(
            R2dbcEntityTemplate template,
            Class<P> primaryClass,
            String activeColumn,
            Integer pageNumber,
            Integer pageSize,

            // ✅ pass Sort directly
            Sort sort,

            Function<P, Mono<List<C>>> fetchSecondary,
            BiFunction<P, List<C>, R> resultMapper
    ) {
        int page = Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER);
        int size = Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT);
        long offset = (long) (page - 1) * size;

        Query baseQuery = Query.query(Criteria.where(activeColumn).isTrue());

        Mono<Long> countMono = template.count(baseQuery, primaryClass);

        Query contentQuery = baseQuery
                .limit(size)
                .offset(offset);

        if (sort != null && sort.isSorted()) {
            contentQuery = contentQuery.sort(sort);
        }

        Flux<R> contentFlux = template.select(primaryClass)
                .matching(contentQuery)
                .all()
                .flatMap(primary ->
                        fetchSecondary.apply(primary)
                                .map(children -> resultMapper.apply(primary, children))
                );

        return countMono.flatMap(total ->
                contentFlux.collectList()
                        .map(content -> new PageResponse<>(content, page, size, total))
        );
    }
}
