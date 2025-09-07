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

                Category.createForLeaf(parent = parent, title = title, code = code)
            } else {
                // 루트 카테고리 생성조건
                Category.createForRoot(title = title, code = code)
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
        code: String,
        title: String?,
        newCode: String?,
    ): Category {
        val category = categoryAdapter.findByCode(code)

        if (newCode != null) {
            val oldCode = category.code
            val children = categoryAdapter.findAllByParentCode(oldCode)

            // 타켓 카테고리의 코드 변경시 인접한 자식의 parentCode를 다 변경해줍니다.
            categoryAdapter.saveAll(children.onEach { it.updateParentCode(code) })
        }

        return categoryAdapter.save(category.patch(title, code))
    }

    override fun moveParent(
        code: String,
        newParentCode: String?,
    ): Category {
        val category = categoryAdapter.findByCode(code)

        // 하위 루트아이디 다 바꿔줘야함.
        if (category.parentCode != newParentCode) {
            val oldRootCode = category.rootCode
            val oldDepth = category.depth

            // 새로운 parent 를 찾습니다. parent 가 없으면 루트 카테고리가 된다는 의미입니다.
            val newParentOrNull = categoryAdapter.findByCodeOrNull(newParentCode)
            category.moveParent(newParentOrNull)

            // 타겟 카테고리의 하위 카테고리를 전부 가져옵니다.
            val meAndUnderDepthCategory =
                categoryAdapter.findAllByRootCodeAndDepthGreaterThanEqual(oldRootCode, oldDepth)
            val allChildren = CategoryTree.getAllMeAndChildrenList(meAndUnderDepthCategory, category.code)
                .filter { it.id != category.id }

            // 하위 카테고리의 위치와 연결을 부모에 맞게 변경해줍니다.
            categoryAdapter.saveAll(
                allChildren
                    .onEach { it.moveParent(category) }
            )
        }

        return categoryAdapter.save(category)
    }

    override fun delete(code: String) {
        val category = categoryAdapter.findByCode(code)

        // 타겟 타케고리와 하위의 모든 카테고리를 가져옵니다. 하위의 카테고리는 같은 root 를 공유하지만, 다른 부모를 가진 카테고리도 포함됩니다.
        val meAndUnderDepthCategory =
            categoryAdapter.findAllByRootCodeAndDepthGreaterThanEqual(category.rootCode, category.depth)

        // 타켓 카테고리와 하위의 모든 카테고리를 List 형태로 가져옵니다.
        val meAndAllChildren = CategoryTree.getAllMeAndChildrenList(meAndUnderDepthCategory, category.code)

        categoryAdapter.deleteAll(meAndAllChildren)
    }
}