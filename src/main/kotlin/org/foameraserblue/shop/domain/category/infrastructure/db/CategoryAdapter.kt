package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.common.exception.NotFoundException
import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CategoryAdapter(
    private val categoryJpaRepository: CategoryJpaRepository,
) {
    fun save(category: Category): Category {
        return categoryJpaRepository.save(CategoryEntity(category)).toDomain()
    }

    fun findById(id: Long): Category {
        return categoryJpaRepository.findByIdOrNull(id)
            ?.toDomain()
            ?: throw NotFoundException("$id ID의 카테고리가 존재하지않습니다.")
    }

    fun findAllByRootIdAndDepthGreaterThanEqual(rootId: Long, depth: Int): List<Category> {
        return categoryJpaRepository.findAllByRootIdAndDepthGreaterThanEqual(rootId, depth).map { it.toDomain() }
    }

    fun findAll(): List<Category> {
        return categoryJpaRepository.findAll().map { it.toDomain() }
    }


    fun deleteAll(categories: List<Category>) {
        categoryJpaRepository.deleteAll(categories.map { CategoryEntity(it) })
    }
}