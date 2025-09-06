package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.common.exception.NotFoundException
import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class CategoryAdapter(
    private val categoryRepository: CategoryRepository
) {
    fun findById(id: Long): Category {
        return categoryRepository.findByIdOrNull(id)
            ?.toDomain()
            ?: throw NotFoundException("$id ID의 카테고리가 존재하지않습니다.")
    }

    fun findAllByRootId(rootId: Long): List<Category> {
        return categoryRepository.findAllByRootId(rootId).map { it.toDomain() }
    }

    fun findAll(): List<Category> {
        return categoryRepository.findAll().map { it.toDomain() }
    }

    fun save(category: Category): Category {
        return categoryRepository.save(CategoryEntity(category)).toDomain()
    }

    fun existsByParentIdAndSiblingOrder(parentId: Long?, siblingOrder: Int): Boolean {
        return categoryRepository.existsByParentIdAndSiblingOrder(parentId, siblingOrder)
    }
}