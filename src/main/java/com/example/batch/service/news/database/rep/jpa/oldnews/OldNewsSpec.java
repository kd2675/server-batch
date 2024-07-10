package com.example.batch.service.news.database.rep.jpa.oldnews;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class OldNewsSpec {
    public static Specification<OldNewsEntity> searchWith(final List<String> text) {
        return ((root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            for (String s : text) {
                if (StringUtils.hasText(s)) {
                    predicates.add(builder.like(root.get("title"), "%" + s + "%"));
                }
            }

            builder.desc(root.get("id"));

            return builder.and(predicates.toArray(new Predicate[0]));
        });
    }
}
