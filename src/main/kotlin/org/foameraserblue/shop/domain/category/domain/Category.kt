package org.foameraserblue.shop.domain.category.domain


class Category(
    val id: Long = 0,

    var title: String,

    var depth: Int,

    var rootCode: String,

    var parentCode: String?,

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

    fun updateRootCode(newRootCode: String) = apply {
        this.rootCode = newRootCode
    }

    fun moveParent(newParent: Category?) = apply {
        this.rootCode = newParent?.rootCode ?: this.code
        this.depth = newParent?.depth?.plus(1) ?: 0
        this.parentCode = newParent?.code
    }
}


