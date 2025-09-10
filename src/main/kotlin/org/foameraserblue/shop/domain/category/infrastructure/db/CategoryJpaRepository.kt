package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query

interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {
    fun findByCode(code: String?): CategoryEntity?

    fun existsByCode(code: String): Boolean

    // 자기자신 + 후손 전부
    fun findAllByCodeStartingWith(codePrefix: String): List<CategoryEntity>

    // 후손 전부
    fun findAllByCodeStartingWithAndCodeNot(codePrefix: String, code: String): List<CategoryEntity>

    // 자기자신 + 후손 전부 삭제
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from CategoryEntity c where c.code like concat(:codePrefix, '%')")
    fun deleteAllByCodeStartingWith(codePrefix: String)
}