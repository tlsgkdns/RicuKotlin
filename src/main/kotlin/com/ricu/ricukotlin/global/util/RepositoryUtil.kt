package com.ricu.ricukotlin.global.util

import com.ricu.ricukotlin.global.common.CreatorAuditEntity
import com.ricu.ricukotlin.global.exception.exception.ModelNotFoundException
import com.ricu.ricukotlin.global.exception.exception.NotHaveAuthorityException
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import kotlin.reflect.jvm.jvmName

class RepositoryUtil {
    companion object
    {
        fun <T, ID> getValidatedEntity(repository: JpaRepository<T, ID>, entityId: ID): T
        {
            val entity = repository.findByIdOrNull(entityId)
                ?: throw ModelNotFoundException(repository.javaClass.typeName, entityId.toString())
            return entity
        }

        fun <T, ID> getValidatedEntityWithAuthority(repository: JpaRepository<T, ID>, entityID: ID): T
        {
            val entity = getValidatedEntity(repository, entityID)
            if(entity !is CreatorAuditEntity) throw IllegalArgumentException("잘못된 엔티티입니다.")
            if((entity as CreatorAuditEntity).creator?.username != SecurityUtil.getUsername())
                throw NotHaveAuthorityException(SecurityUtil.getUsername(), entity.javaClass.typeName)
            return entity
        }
    }
}