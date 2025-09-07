package org.foameraserblue.shop.domain.category.domain

/**
 * 구현의도
 * 1. 카테고리는 일반적인 트리 형태가 아닌, 여러 트리가 모여 여러 루트(상의, 하의 같은 최상위 카테고리)를 가진 구조일것이다.
 * 2. 카테고리는 이진트리가 아니며, 자식으로 여러 노드를 가질 수 있는 트리일 것이다.
 * 3. 카테고리는 생성/수정/삭제 보다 조회의 빈도수가 높을것이다. 즉 조회 최적화를 해야한다.
 * 4. 카테고리끼리 직접 참조하게 도메인을 작성하면 DB 터치 횟수 및 join 쿼리가 많아져 DB 성능에 악영향을 미칠것이다.
 * 5. 직접 참조가 아닌 각 카테고리의 code 만으로 연결관계를 구성한다.
 */
class Category(
    val id: Long = 0,

    var title: String,

    // 깊이
    var depth: Int,

    /**
     * 여러 루트 카테고리가 있다는 가정하에, 특정 루트 카테고리 하위인지 여부만 알아도
     * search 시 가져오는 데이터가 적어질 수 있다는 생각을 했습니다.
     *
     * 카테고리가 속해있는 루트의 code 이며, 카테고리 search 시 결과값을 최소화하려는 용도로 사용합니다.
     */
    var rootCode: String,

    // 부모 카테고리 코드, 노드의 직접적인 연결관계를 나타냅니다.
    // 루트 카테고리의 경우 parentCode 는 null 입니다.
    var parentCode: String?,

    // 카테고리 코드
    var code: String,
) {
    companion object {
        fun createForLeaf(parent: Category, title: String, code: String): Category {
            return Category(
                title = title,
                rootCode = parent.rootCode,
                depth = parent.depth.plus(1),
                parentCode = parent.code,
                code = code
            )
        }

        fun createForRoot(title: String, code: String): Category {
            return Category(
                title = title,
                rootCode = code,
                depth = 0,
                parentCode = null,
                code = code
            )
        }
    }

    init {
        validateDepthAndOrder()
        validateDepthAndParentId()
    }

    val isRoot: Boolean
        get() = this.depth == 0 && this.parentCode == null

    private fun validateDepthAndOrder() {
        require(this.depth >= 0) { "depth 는 음수가 될 수 없습니다." }
    }

    private fun validateDepthAndParentId() {
        if (this.depth == 0 || this.parentCode == null) {
            require(this.isRoot) { "depth 가 0 이거나 부모 code 가 존재하지 않으면 root 카테고리여야합니다." }
        }
    }

    fun patch(title: String?, code: String?) = apply {
        if (title != null) {
            this.title = title
        }
        if (code != null) {
            this.code = code
        }
    }

    fun updateParentCode(newParentCode: String) = apply {
        this.parentCode = newParentCode
    }

    // 바뀐 parent 에 맞게 연결관계 및 depth 를 변경해줍니다.
    fun moveParent(newParent: Category?) = apply {
        this.rootCode = newParent?.rootCode ?: this.code
        this.depth = newParent?.depth?.plus(1) ?: 0
        this.parentCode = newParent?.code
    }
}


