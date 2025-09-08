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
        val category = parentCode
            // 리프 카테고리 생성 조건
            ?.let {
                val parent = categoryAdapter.findByCode(parentCode)

                Category.createForLeaf(parent = parent, title = title, code = code)
            }
        // 루트 카테고리 생성조건
            ?: Category.createForRoot(title = title, code = code)

        validateCode(category.code)

        return categoryAdapter.save(category)
    }

    override fun patch(
        code: String,
        title: String?,
        newCode: String?,
    ): Category {
        val category = categoryAdapter.findByCode(code)

        if (newCode != null) {
            validateCode(newCode)
            updateParentCodeOfChildren(category, newCode)
        }

        return categoryAdapter.save(category.patch(title, newCode))
    }

    private fun validateCode(code: String) {
        require(
            !categoryAdapter.existsByCode(
                code
            )
        ) { "이미 해당 code로 저장된 데이터가 존재합니다." }
    }

    private fun updateParentCodeOfChildren(category: Category, newParentCode: String) {
        val oldCode = category.code
        val children = categoryAdapter.findAllByParentCode(oldCode)

        // 타켓 카테고리의 코드 변경시 인접한 자식의 parentCode를 다 변경해줍니다.
        categoryAdapter.saveAll(children.onEach { it.updateParentCode(newParentCode) })
    }

    override fun moveParent(
        code: String,
        newParentCode: String?,
    ): Category {
        val category = categoryAdapter.findByCode(code)

        if (category.parentCode == newParentCode) {
            return category
        }

        val oldDepth = category.depth

        // 새로운 parent 를 찾습니다.(null이면 루트로 이동)
        val newParentOrNull = categoryAdapter.findByCodeOrNull(newParentCode)
        // 타겟 카테고리의 후손을 찾습니다.
        val descendants = getDescendants(category)
        validateNotMovingIntoDescendant(newParentCode, descendants)

        category.moveParent(newParentOrNull)

        // 자손들을, 부모가 이동한 만큼 depth 를 이동시킵니다.
        val depthGap = category.depth - oldDepth
        moveDescendantsWithParent(descendants, depthGap)

        categoryAdapter.saveAll(descendants)
        return categoryAdapter.save(category)
    }

    // category 의 모든 후손을 찾습니다.
    private fun getDescendants(category: Category): List<Category> {
        val candidates =
            categoryAdapter.findAllByDepthGreaterThanEqual(category.depth)

        return CategoryTree
            .getAllMeAndDescendantsList(candidates, category.code)
            .filter { it.id != category.id }
    }

    private fun validateNotMovingIntoDescendant(newParentCode: String?, descendants: List<Category>) {
        require(!descendants.any { it.code == newParentCode }) {
            "자기 후손의 하위카테고리로 이동할 수 없습니다."
        }
    }

    private fun moveDescendantsWithParent(descendants: List<Category>, depthGap: Int) {
        descendants.forEach { it.moveWithParent(depthGap) }
    }

    override fun delete(code: String) {
        val category = categoryAdapter.findByCode(code)

        // 타겟 타케고리의 depth 이하의 모든 카테고리를 가져옵니다.
        val candidates =
            categoryAdapter.findAllByDepthGreaterThanEqual(category.depth)

        // 타켓 카테고리와 하위의 모든 카테고리를 List 형태로 가져옵니다.
        val meAndAllDescendants = CategoryTree.getAllMeAndDescendantsList(candidates, category.code)

        categoryAdapter.deleteAll(meAndAllDescendants)
    }
}