package org.foameraserblue.shop.domain.category.domain

// 카테고리는 노드간 사이클이 없다는 가정 입니다.
data class CategoryTree(
    val category: Category,
    val children: List<CategoryTree>
) {
    companion object {
        // 특정 code 의 카테고리를 포함하여 하위 카테고리를 리스트 형태로 반환합니다.
        fun getAllMeAndDescendantsList(categories: List<Category>, code: String): List<Category> {
            val childrenByParentCode: Map<String?, List<Category>> =
                categories
                    .groupBy { it.parentCode }

            val result = mutableListOf<Category>()
            val root = categories.find { it.code == code }
                ?: throw IllegalArgumentException("$code code 의 카테고리를 찾을 수 없습니다.")

            return dfsToList(childrenByParentCode, root, result)
        }

        // 특정 code 의 카테고리를 포함하여 하위 카테고리를 전부 반환합니다.
        fun getAllMeAndDescendantsTree(categories: List<Category>, code: String): CategoryTree {
            val childrenByParentCode: Map<String?, List<Category>> =
                categories
                    .groupBy { it.parentCode }

            val root = categories.find { it.code == code }
                ?: throw IllegalArgumentException("$code code 의 카테고리를 찾을 수 없습니다.")

            return dfsToTree(childrenByParentCode, root)
        }

        // 카테고리는 단일 루트를 가진 트리가 아닌 여러 트리의 집합 형태이기때문에 List<CategoryTree> 로 반환하였습니다.
        fun getAllTree(categories: List<Category>): List<CategoryTree> {
            val childrenByParentCode: Map<String?, List<Category>> =
                categories
                    .groupBy { it.parentCode }

            val roots = categories.filter { it.isRoot }

            return roots
                .map { dfsToTree(childrenByParentCode, it) }
        }

        private fun dfsToList(
            childrenByParentCode: Map<String?, List<Category>>,
            category: Category,
            list: MutableList<Category>
        ): List<Category> {
            list.add(category)

            val children = childrenByParentCode[category.code].orEmpty()
            children.forEach { child ->
                dfsToList(childrenByParentCode, child, list)
            }

            return list

        }

        private fun dfsToTree(childrenByParentCode: Map<String?, List<Category>>, category: Category): CategoryTree {
            val children = childrenByParentCode[category.code].orEmpty()
                .map { dfsToTree(childrenByParentCode, it) }

            return CategoryTree(
                category = category,
                children = children,
            )
        }
    }
}