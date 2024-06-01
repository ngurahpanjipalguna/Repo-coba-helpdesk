package com.dimata.helpdesk.repository.auth

import jakarta.interceptor.InterceptorBinding
import java.lang.annotation.Inherited

@Inherited
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@InterceptorBinding
annotation class Permission(val name: String)
