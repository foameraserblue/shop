package org.foameraserblue.shop.domain.category.infrastructure.db

import org.foameraserblue.shop.common.exception.NotFoundException
import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.entitiy.CategoryEntity
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

    fun findByCode(code: String): Category {
        return categoryJpaRepository.findByCode(code)
            ?.toDomain()
            ?: throw NotFoundException("$code code의 카테고리가 존재하지않습니다.")
    }

    fun findByCodeOrNull(code: String?): Category? {
        return categoryJpaRepository.findByCode(code)
            ?.toDomain()
    }

    fun findAllByRootCodeAndDepthGreaterThanEqual(rootCode: String, depth: Int): List<Category> {
        return categoryJpaRepository.findAllByRootCodeAndDepthGreaterThanEqual(rootCode, depth).map { it.toDomain() }
    }

    fun findAllByParentCode(parentCode: String): List<Category> {
        return categoryJpaRepository.findAllByParentCode(parentCode).map { it.toDomain() }
    }

    fun findAll(): List<Category> {
        return categoryJpaRepository.findAll().map { it.toDomain() }
    }

    fun existsByCode(code: String): Boolean {
        return categoryJpaRepository.existsByCode(code)
    }

    fun deleteAll(categories: List<Category>) {
        categoryJpaRepository.deleteAll(categories.map { CategoryEntity(it) })
    }
}