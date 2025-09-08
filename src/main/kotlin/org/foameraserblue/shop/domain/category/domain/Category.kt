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

    // 부모 카테고리 코드이며 부모 자식간 연결관계를 나타냅니다.
    // 루트 카테고리의 경우 parentCode 는 null 입니다.
    var parentCode: String?,

    /**
     * id 가 아닌 코드를 사용한 이유
     *
     * 카테고리 코드를 증권 회사코드 같은 개념으로 봤음. 즉 모든 환경에서 고정된 값이였으면 했음.
     * id 는 디비에서 내려주는, 환경마다 다른 값이여서 사용자가 원하는 값을 설정할 수 없음
     * 또한 생성시점에 영속화가 되어야만 id 값 확인이 가능해서 연결관계 매핑이 애매함.
     */
    var code: String,
) {
    companion object {
        fun createForLeaf(parent: Category, title: String, code: String): Category {
            return Category(
                title = title,
                depth = parent.depth.plus(1),
                parentCode = parent.code,
                code = code
            )
        }

        fun createForRoot(title: String, code: String): Category {
            return Category(
                title = title,
                depth = 0,
                parentCode = null,
                code = code
            )
        }
    }

    init {
        validateConsistency()
    }

    val isRoot: Boolean
        get() = this.depth == 0 && this.parentCode == null

    val isNonRoot: Boolean
        get() = this.depth > 0 && this.parentCode != null

    private fun validateConsistency() {
        validateDepth()
        validateRoot()
        validateNonRoot()
    }

    private fun validateDepth() {
        require(this.depth >= 0) { "depth 는 음수가 될 수 없습니다." }
    }

    private fun validateRoot() {
        if (this.depth == 0 || this.parentCode == null) {
            require(this.isRoot) { "depth 가 0 이거나 부모 code 가 존재하지 않으면 root 카테고리여야합니다." }
        }
    }

    private fun validateNonRoot() {
        if (this.depth > 0 || this.parentCode != null) {
            require(this.isNonRoot) { "depth 가 0 보다 크거나 부모 code 가 존재하면 non root 카테고리여야합니다." }
        }
    }

    fun patch(title: String?, code: String?) = apply {
        if (title != null) {
            this.title = title
        }
        if (code != null) {
            this.code = code
            validateConsistency()
        }
    }

    fun updateParentCode(newParentCode: String) = apply {
        this.parentCode = newParentCode

        validateConsistency()
    }

    fun moveWithParent(depthGap: Int) = apply {
        this.depth += depthGap

        validateDepth()
    }

    // 바뀐 parent 에 맞게 연결관계 및 depth 를 변경해줍니다.
    fun moveParent(newParent: Category?) = apply {
        val newParentCode = newParent?.code
        require(newParentCode != this.code) { "자기 자신을 부모로 지정할 수 없습니다." }

        this.depth = newParent?.depth?.plus(1) ?: 0
        this.parentCode = newParentCode

        validateConsistency()
    }
}


