package com.example.sellcourse.repository.user;

import com.example.sellcourse.entities.User;
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
public class CustomUserRepositoryImpl implements CustomUserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Page<User> searchByMultipleKeywords(String[] keywords, Pageable pageable) {
        return searchByRoleAndMultipleKeywords(null, keywords, pageable);
    }

    @Override
    public Page<User> searchByRoleAndMultipleKeywords(String roleName, String[] keywords, Pageable pageable) {
        StringBuilder queryBuilder = new StringBuilder("SELECT u FROM User u WHERE 1=1 ");

        if (roleName != null) {
            queryBuilder.append("AND u.role.name = :roleName ");
        }

        if (keywords.length > 0) {
            queryBuilder.append("AND (");
            IntStream.range(0, keywords.length).forEach(i -> {
                if (i > 0) {
                    queryBuilder.append(" OR ");
                }
                queryBuilder.append("(LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword")
                        .append(i)
                        .append(", '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword")
                        .append(i)
                        .append(", '%')))");
            });
            queryBuilder.append(")");
        }

        TypedQuery<User> query = entityManager.createQuery(queryBuilder.toString(), User.class);

        if (roleName != null) {
            query.setParameter("roleName", roleName);
        }

        IntStream.range(0, keywords.length).forEach(i -> query.setParameter("keyword" + i, keywords[i]));

        int totalRows = query.getResultList().size();
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        List<User> users = query.getResultList();
        return new PageImpl<>(users, pageable, totalRows);
    }

}
