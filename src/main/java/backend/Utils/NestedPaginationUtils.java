package backend.Utils;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
//          for Dynamic filter value
//            Object activeColumnValue,
            int pageNumber,
            int pageSize,
            Function<P, Mono<List<C>>> fetchSecondary,
            BiFunction<P, List<C>, R> resultMapper
    ) {
        int page = Optional.ofNullable(pageNumber).orElse(PaginationUtils.DEFAULT_PAGE_NUMBER);
        int size = Optional.ofNullable(pageSize).orElse(PaginationUtils.DEFAULT_LIMIT);
        long offset = (long) (page - 1) * size;

        Query baseQuery = Query.query(Criteria.where(activeColumn).isTrue());

//        for Dynamic filter value
//        Query baseQuery = Query.query(Criteria.where(activeColumn).is(activeColumnValue));

        Mono<Long> countMono = template.count(baseQuery, primaryClass);
        Flux<R> contentFlux = template.select(primaryClass)
                .matching(baseQuery.limit(size).offset(offset))
                .all()
                .flatMap(primary ->
                        fetchSecondary.apply(primary)
                                .map(secondary -> resultMapper.apply(primary, secondary))
                );

        return countMono.flatMap(total ->
                contentFlux.collectList()
                        .map(content ->
                                new PageResponse<>(
                                        content,
                                        page,
                                        size,
                                        total
                                )
                        )
        );
    }
}
