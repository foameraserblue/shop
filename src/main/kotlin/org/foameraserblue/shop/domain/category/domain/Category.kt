package org.foameraserblue.shop.domain.category.domain

class Category(
    val id: Long = 0,

    val title: String,

    /** 내가 속한 카테고리의 루트 카테고리 id
     * 일반적인 상황에선 루트 카테고리의 정보를 알 필요가 없으나
     * 만약 카테고리가 상의 > 반팔 > 면티 인 상품의 경우,
     * 그 상품의 카테고리는 면티 가 아닌 상의 > 반팔 > 면티 로 출력해야 된다 가정했을때 사용 가능.
     */
    val rootCategoryId: Long,

    val parentCategoryId: Long?,

    val childCategories: MutableList<Category> = mutableListOf(),

    val depth: Int,

    // 같은 depth 의 카테고리끼리의 순서(현 시점에선 화면 출력에 이용함)
    val sortOrderOfSameDepth: Int,
) {
    init {
        validateDepth()
        validateRootCategoryConsistency()
        validateDepthAndParentId()
    }

    private fun validateDepth() {
        require(depth >= 0) { "depth 는 음수가 될 수 없습니다." }
    }

    /**
     * 루트 카테고리의 rootCategoryId 일관성 검증.
     * - 루트이고 이미 영속된(id > 0) 상태의 경우: rootCategoryId == id 이어야 한다.
     */
    private fun validateRootCategoryConsistency() {
        if (isRootCategory() && id > 0) {
            require(this.id == this.rootCategoryId) { "루트 카테고리의 rootCategoryId 는 자기 자신의 id 여야합니다." }
        }
    }

    private fun validateDepthAndParentId() {
        if (isRootCategory()) {
            require(depth == 0) { "루트 카테고리의 depth 는 0 이어야 합니다." }
            require(parentCategoryId == null) { "루트 카테고리는 부모 카테고리를 가질 수 없습니다." }
        } else {
            require(depth > 0) { "하위 카테고리의 depth 는 1 이상이어야 합니다." }
            require(parentCategoryId != null) { "루트 카테고리가 아닐경우 부모 카테고리를 필수로 가져야합니다." }
        }
    }

    fun isRootCategory(): Boolean = depth == 0
}


