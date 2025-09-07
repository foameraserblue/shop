package org.foameraserblue.shop.domain.category.service

import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.domain.CategoryTree
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
        parentCode: String?,
        title: String,
        code: String,
    ): Category {
        val category =
            if (parentCode != null) {
                // 리프 카테고리 생성 조건
                val parent = categoryAdapter.findByCode(parentCode)

                return Category.createForLeaf(
                    parent = parent,
                    title = title,
                    code = code,
                )
            } else {
                // 루트 카테고리 생성조건
                Category.createForRoot(
                    title = title,
                    code = code,
                )
            }

        validateForCreate(category)

        return categoryAdapter.save(category)
    }

    private fun validateForCreate(category: Category) {
        require(
            !categoryAdapter.existsByCode(
                category.code
            )
        ) { "이미 해당 code로 저장된 데이터가 존재합니다." }
    }

    override fun patch(
        id: Long,
        title: String?,
        code: String?,
    ): Category {
        val category = categoryAdapter.findById(id)

        // 코드 변경시 인접한 자식의 parentCode를 다 변경해줌.
        if (code != null) {
            val oldCode = category.code
            val children = categoryAdapter.findAllByParentCode(oldCode)

            categoryAdapter.saveAll(children.onEach { it.updateParentCode(code) })
        }

        return categoryAdapter.save(category.patch(title, code))
    }

    override fun moveParent(
        id: Long,
        newParentCode: String?,
    ): Category {
        val category = categoryAdapter.findById(id)

        // 하위 루트아이디 다 바꿔줘야함.
        if (category.parentCode != newParentCode) {
            val oldRootCode = category.rootCode
            val oldDepth = category.depth

            val newParentOrNull = categoryAdapter.findByCodeOrNull(newParentCode)
            category.moveParent(newParentOrNull)

            val meAndUnderDepthCategory =
                categoryAdapter.findAllByRootCodeAndDepthGreaterThanEqual(oldRootCode, oldDepth)
            val allChildren = CategoryTree.getAllMeAndChildrenList(meAndUnderDepthCategory, category.code)
                .filter { it.id != category.id }

            allChildren.forEach { it.updateRootCode(category.rootCode) }

            categoryAdapter.saveAll(allChildren)
        }

        return categoryAdapter.save(category)
    }

    // 카테고리 삭제시, 하위의 children 카테고리들을 전부 삭제해야함.
    override fun delete(id: Long) {
        val category = categoryAdapter.findById(id)
        val meAndUnderDepthCategory =
            categoryAdapter.findAllByRootCodeAndDepthGreaterThanEqual(category.rootCode, category.depth)

        val meAndAllChildren = CategoryTree.getAllMeAndChildrenList(meAndUnderDepthCategory, category.code)
        categoryAdapter.deleteAll(meAndAllChildren)
    }
}