package com.dimata.helpdesk.repository.auth

import com.dimata.helpdesk.core.generateTSID
import com.dimata.helpdesk.dto.param.ActivityParam
import com.dimata.helpdesk.dto.param.BaseParam
import com.dimata.helpdesk.dto.response.auth.ActivityResponse
import com.dimata.helpdesk.gen.jooq.tables.LogActivity.LOG_ACTIVITY
import com.dimata.helpdesk.gen.jooq.tables.Session.SESSION
import com.dimata.helpdesk.gen.jooq.tables.User.USER
import com.dimata.helpdesk.repository.Repository
import jakarta.enterprise.context.ApplicationScoped
import org.jooq.impl.DSL
import java.time.LocalDate
import java.time.LocalDateTime

@ApplicationScoped
class ActivityRepository : Repository() {

    private val ACTIVITY = LOG_ACTIVITY.`as`("activity")

    fun create(event: String, causer: String? = null, sessionId: String? = null): String {
        val activityId = generateTSID()
        jooq.newRecord(ACTIVITY).apply {
            this.activityId = activityId
            this.event = event
            this.activity = "$causer telah melakukan: $event"
            this.sessionId = sessionId
            this.createdAt = LocalDateTime.now()
            this.store()
        }
        return activityId
    }

    fun getUserActivity(userId: String, param: BaseParam): List<ActivityResponse> {
        val query = jooq.select(
            USER.FULL_NAME.`as`("username"),
            SESSION.USER_ID.`as`("userId"),
            SESSION.IP_ADDRESS.`as`("ipAddress"),
            ACTIVITY.ACTIVITY,
            ACTIVITY.CREATED_AT.`as`("causedAt")
        )
            .from(ACTIVITY)
            .leftJoin(SESSION).on(ACTIVITY.SESSION_ID.eq(SESSION.SESSION_ID))
            .leftJoin(USER).on(SESSION.USER_ID.eq(USER.USER_ID))
            .where(SESSION.USER_ID.eq(userId))
            .orderBy(ACTIVITY.CREATED_AT.desc())

        return paginate(query, param.page, param.limit).fetchInto(ActivityResponse::class.java)
    }

    fun getActivity(param: ActivityParam): List<ActivityResponse> {
        val query = jooq.select(
            USER.FULL_NAME.`as`("username"),
            SESSION.USER_ID.`as`("userId"),
            SESSION.IP_ADDRESS.`as`("ipAddress"),
            ACTIVITY.ACTIVITY,
            ACTIVITY.CREATED_AT.`as`("causedAt")
        )
            .from(ACTIVITY)
            .leftJoin(SESSION).on(ACTIVITY.SESSION_ID.eq(SESSION.SESSION_ID))
            .leftJoin(USER).on(SESSION.USER_ID.eq(USER.USER_ID))

        if (param.startDate != null && param.endDate != null) {
            query.where(DSL.cast(ACTIVITY.CREATED_AT, LocalDate::class.java).between(param.startDate, param.endDate))
        }

        return paginate(query, param.page, param.limit).fetchInto(ActivityResponse::class.java)
    }
}
