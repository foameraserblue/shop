package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class CommandCategoryService(
    private val categoryAdapter: CategoryAdapter,
) : CommandCategoryUseCase {
    override fun create(
        title: String,
        parentId: Long?,
    ): Category {
        val category = parentId
            ?.let {
                createLeafCategory(title, parentId)
            }
            ?: run {
                createRootCategory(title)
            }

        validateForCreate(category)
        val saved = categoryAdapter.save(category)

        return if (saved.isRoot) {
            categoryAdapter.save(saved)
        } else {
            saved
        }
    }

    private fun createLeafCategory(title: String, parentId: Long): Category {
        val parent = categoryAdapter.findById(parentId)
        val sameParentAndTopOrderCategory = categoryAdapter
            .findTopByParentIdOrderByOrderDescOrNull(parent.id)

        return Category.createForLeaf(
            title = title,
            parentCategory = parent,
            sameParentAndTopOrderCategory = sameParentAndTopOrderCategory
        )
    }

    private fun createRootCategory(title: String): Category {
        val topOrderRootCategory = categoryAdapter
            .findTopByParentIdOrderByOrderDescOrNull(null)

        return Category.createForRoot(
            title = title,
            topOrderRootCategory = topOrderRootCategory
        )
    }

    private fun validateForCreate(category: Category) {
        require(
            !categoryAdapter.existsByParentIdAndOrder(
                category.parentId,
                category.order
            )
        ) { "이미 해당 정렬 순서로 저장된 데이터가 존재합니다." }

    }

    override fun update(
        id: Long,
        title: String
    ): Category {
        val category = categoryAdapter.findById(id)

        return categoryAdapter.save(category.update(title))
    }

    override fun moveLocation(
        id: Long,
        newParentId: Long?,
        newOrder: Int,
    ): Category {
        val category = categoryAdapter.findById(id)

        when {
            // 부모,순번 변경
            (category.parentId != newParentId) -> {
                moveParent(category, newParentId, newOrder)
            }

            // 같은 부모 내에서 순번만 변경
            (newOrder != category.order) -> {
                moveOrder(category, newOrder)
            }
            // 이동 대상 정보가 없으면 그대로 반환
            else -> return category
        }

        return categoryAdapter.save(category)
    }

    private fun moveParent(
        category: Category,
        newParentId: Long?,
        newOrder: Int,
    ) {
        val oldParentId = category.parentId
        val oldOrder = category.order

        val newParent = newParentId?.let { categoryAdapter.findById(newParentId) }
        // 1) 이전 부모 그룹에서 순서 제거 보정
        orderAdjustWhenRemove(oldParentId, oldOrder)
        // 2) 새 부모/순번으로 이동
        category.moveParent(newParent, newOrder)
        // 3) 새 부모 그룹에서 순서 삽입 보정
        orderAdjustWhenInsert(category.parentId, category.order)
    }

    private fun moveOrder(
        category: Category,
        newOrder: Int,
    ) {
        val oldOrder = category.order

        // 1) 현재 부모 그룹에서 순서 제거 보정(기존 위치를 비움)
        orderAdjustWhenRemove(category.parentId, oldOrder)
        // 2) 새 순번으로 이동
        category.updateOrder(newOrder)
        // 3) 같은 부모(변함 없음) 그룹에서 순서 삽입 보정
        orderAdjustWhenInsert(category.parentId, category.order)
    }

    override fun delete(id: Long) {
        val category = categoryAdapter.findById(id)

        val parentId = category.parentId
        val order = category.order

        categoryAdapter.deleteById(id)
        orderAdjustWhenRemove(parentId, order)
    }

    private fun orderAdjustWhenRemove(
        parentId: Long?, removedOrder: Int
    ) {
        val sameParentCategories = categoryAdapter.findAllByParentId(parentId)

        sameParentCategories
            .filter { it.order > removedOrder }
            .map { it.moveToLeft() }

        categoryAdapter.saveAll(sameParentCategories)
    }

    private fun orderAdjustWhenInsert(
        parentId: Long?, insertedOrder: Int
    ) {
        val sameParentCategories = categoryAdapter.findAllByParentId(parentId)

        sameParentCategories
            .filter { it.order >= insertedOrder }
            .map { it.moveToRight() }

        categoryAdapter.saveAll(sameParentCategories)
    }
}