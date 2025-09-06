package org.foameraserblue.shop.domain.category.domain


class Category(
    val id: Long = 0,

    var title: String,

    /**
     * parent 와 children 을 연관관계로 갖고있는 방식에선 N+1 문제를 해결하기 힘듬
     * 일반적으로 아래로 뻗어나가는 트리구조인데.. 그 경우의 수만큼 쿼리가 나가고 db 터치가 너무 많아진다..
     * 앞단에 캐시를 놓아서 해결할 순 있겠지만, 이 사유만을 위해 캐시를 쓰는건 나이스하진 않은것같음.
     * 차라리 루트던 상위던 하위던 rootId 하나로 전부 조회한다음에 자바 코드에서 카테고리 분류를 하는게 낫지않을까?
     * 카테고리는 엄청나게 많은 데이터가 쌓이진 않을것이니 트레이드 오프를 따졌을때 쿼리가 많이 나가는것보다, 필요없는 데이터를 좀 더 가져오는게 성능상 나아보인다
     */
    var rootId: Long,

    val parentId: Long?,

    val depth: Int,

    // 형제노드끼리의 순서(현 시점에선 화면 출력에 이용함)
    val siblingOrder: Int,
) {
    companion object {
        fun createForLeaf(title: String, parentCategory: Category, sameParentAndTopOrderCategory: Category?) =
            Category(
                title = title,
                rootId = parentCategory.rootId,
                parentId = parentCategory.id,
                depth = parentCategory.depth + 1,
                siblingOrder = sameParentAndTopOrderCategory?.siblingOrder?.plus(1) ?: 0,
            )

        fun createForRoot(title: String, topOrderRootCategory: Category?) = Category(
            title = title,
            rootId = 0,
            parentId = null,
            depth = 0,
            siblingOrder = topOrderRootCategory?.siblingOrder?.plus(1) ?: 0,
        )
    }

    init {
        validateDepthAndSiblingOrder()

        rootIdInitialize()

        validateRootConsistency()
        validateDepthAndParentId()
    }

    val isRoot: Boolean
        get() = depth == 0

    private fun validateDepthAndSiblingOrder() {
        require(this.depth >= 0) { "depth 는 음수가 될 수 없습니다." }
        require(this.siblingOrder >= 0) { "siblingOrder 는 음수가 될 수 없습니다." }
    }

    private fun rootIdInitialize() {
        if (isRootIdUninitialized()) {
            this.rootId = this.id
        }
    }

    private fun isRootIdUninitialized(): Boolean = this.id > 0 && this.rootId == 0L

    /**
     * 루트 카테고리의 rootId 일관성 검증.
     * - 루트이고 이미 영속된(id > 0) 상태의 경우: rootId == id 이어야 한다.
     */
    private fun validateRootConsistency() {
        if (this.isRoot && this.id > 0) {

            require(this.id == this.rootId) { "루트 카테고리의 rootId 는 자기 자신의 id 여야합니다." }
        }
    }

    private fun validateDepthAndParentId() {
        if (this.isRoot) {
            require(this.depth == 0) { "루트 카테고리의 depth 는 0 이어야 합니다." }
            require(this.parentId == null) { "루트 카테고리는 부모 카테고리를 가질 수 없습니다." }
        } else {
            require(this.depth > 0) { "하위 카테고리의 depth 는 1 이상이어야 합니다." }
            require(this.parentId != null) { "루트 카테고리가 아닐경우 부모 카테고리를 필수로 가져야합니다." }
        }
    }


    fun update(title:String) =apply {
        this.title = title
    }
}


