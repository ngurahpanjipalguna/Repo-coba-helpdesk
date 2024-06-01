package com.dimata.helpdesk.repository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectLimitStep;

@ApplicationScoped
public class Repository {

    @Inject
    protected DSLContext jooq;

    protected <R extends Record> SelectLimitStep<R> paginate(SelectLimitStep<R> query, int page, int limit) {
        return (SelectLimitStep<R>) query.limit(limit).offset((page - 1) * limit);
    }
}
