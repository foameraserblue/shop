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

    fun saveAll(categories: List<Category>): List<Category> {
        return categoryJpaRepository.saveAll(categories.map { CategoryEntity(it) }).map { it.toDomain() }
    }

    fun findById(id: Long): Category {
        return categoryJpaRepository.findByIdOrNull(id)
            ?.toDomain()
            ?: throw NotFoundException("$id ID의 카테고리가 존재하지않습니다.")
    }

    fun findAllByRootId(rootId: Long): List<Category> {
        return categoryJpaRepository.findAllByRootId(rootId).map { it.toDomain() }
    }

    fun findAll(): List<Category> {
        return categoryJpaRepository.findAll().map { it.toDomain() }
    }

    fun existsByParentIdAndOrder(parentId: Long?, order: Int): Boolean {
        return categoryJpaRepository.existsByParentIdAndOrder(parentId, order)
    }

    fun findTopByParentIdOrderByOrderDescOrNull(parentId: Long?): Category? {
        return categoryJpaRepository.findTopByParentIdOrderByOrderDesc(parentId)?.toDomain()
    }

    fun deleteById(id: Long) {
        categoryJpaRepository.deleteById(id)
    }

    fun findAllByParentId(parentId: Long?): List<Category> {
        return categoryJpaRepository.findAllByParentId(parentId).map { it.toDomain() }
    }
}