package com.example.sellcourse.repository.course;

import com.example.sellcourse.entities.Course;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.IntStream;

@Repository
public class CustomCourseRepositoryImpl implements CustomCourseRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<Course> searchByMultipleKeywords(String[] keywords, Pageable pageable) {
        if (keywords == null || keywords.length == 0) {
            return Page.empty(pageable);
        }

        StringBuilder queryBuilder = new StringBuilder("SELECT c FROM Course c WHERE 1=1 ");

        queryBuilder.append("AND (");
        IntStream.range(0, keywords.length).forEach(i -> {
            if (i > 0) {
                queryBuilder.append(" OR ");
            }
            queryBuilder.append("(LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword")
                    .append(i)
                    .append(", '%')) OR LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword")
                    .append(i)
                    .append(", '%')))");
        });
        queryBuilder.append(")");

        // Main query
        TypedQuery<Course> query = entityManager.createQuery(queryBuilder.toString(), Course.class);
        IntStream.range(0, keywords.length).forEach(i -> query.setParameter("keyword" + i, keywords[i]));

        // Count query for total rows
        String countQueryStr = queryBuilder.toString().replaceFirst("SELECT c", "SELECT COUNT(c)");
        TypedQuery<Long> countQuery = entityManager.createQuery(countQueryStr, Long.class);
        IntStream.range(0, keywords.length).forEach(i -> countQuery.setParameter("keyword" + i, keywords[i]));
        long totalRows = countQuery.getSingleResult();

        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<Course> courses = query.getResultList();
        return new PageImpl<>(courses, pageable, totalRows);
    }
}
