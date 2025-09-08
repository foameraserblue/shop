package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryJpaRepository : JpaRepository<CategoryEntity, Long> {
    fun findAllByDepthGreaterThanEqual(depth: Int): List<CategoryEntity>

    fun findByCode(code: String?): CategoryEntity?

    fun existsByCode(code: String): Boolean

    fun findAllByParentCode(parentCode: String): List<CategoryEntity>
}