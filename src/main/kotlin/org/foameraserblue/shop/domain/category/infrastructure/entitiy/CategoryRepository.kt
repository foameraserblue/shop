package org.foameraserblue.shop.domain.category.infrastructure.entitiy

import org.foameraserblue.shop.domain.category.domain.Category
import org.springframework.data.jpa.repository.EntityGraph
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface CategoryRepository: JpaRepository<Category, Long> {
    @Query("select c from Category c where c.parent is null")
    @EntityGraph(attributePaths = ["children"])
    fun findAllTest():List<Category>

    @EntityGraph(attributePaths = ["children"])
    fun findByIdAndParentIsNull(id:Long):Category?
}