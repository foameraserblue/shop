package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
    fun findAllByRootCategoryId(rootCategoryId: Long): List<CategoryEntity>
}