package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.jpa.repository.JpaRepository

interface CategoryRepository : JpaRepository<CategoryEntity, Long> {
    fun findAllByRootId(rootId: Long): List<CategoryEntity>
    fun existsByParentIdAndSiblingOrder(parentId: Long?, siblingOrder: Int): Boolean
}