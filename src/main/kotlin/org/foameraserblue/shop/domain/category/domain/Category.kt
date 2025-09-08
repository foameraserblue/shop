package org.foameraserblue.shop.domain.category.domain

class Category(
    val id: Long = 0,

    var title: String,

    // Materialized Path 로 사용합니다.
    // 코드를 3자리로 한정한다는 가정하에 사용되며
    // 3자리의 고유 숫자를 segment 로 지칭합니다.
    // 예) "100110111"
    var code: String,
) {
    companion object {
        private val SEGMENT_3DIGIT_REGEX = Regex("\\d{3}")

        private fun validateSegment(segment: String) {
            require(SEGMENT_3DIGIT_REGEX.matches(segment)) { "코드의 세그먼트는 3자리 숫자여야 합니다." }
        }

        private fun makeCode(parentCode: String, segment: String): String {
            return "$parentCode$segment"
        }

        fun createForLeaf(parent: Category, title: String, segment: String): Category {
            validateSegment(segment)

            return Category(
                title = title,
                code = makeCode(parent.code, segment)
            )
        }

        fun createForRoot(title: String, rootSegment: String): Category {
            validateSegment(rootSegment)

            return Category(
                title = title,
                code = rootSegment
            )
        }
    }

    init {
        validateConsistency()
    }

    val isRoot: Boolean
        get() = this.code.length == 3

    // 부모가 없으면 빈 문자열을 반환합니다
    val parentCodeOrEmpty: String
        get() = if (this.isRoot) "" else this.code.dropLast(3)

    val segment: String
        get() = this.code.takeLast(3)

    private fun validateCode(code: String) {
        require(code.isNotBlank()) { "code는 비어있을 수 없습니다." }
        require(code.all { it.isDigit() }) { "code는 숫자만 포함해야 합니다." }
        require(code.length % 3 == 0) { "code의 길이는 3의 배수여야 합니다." }
    }


    private fun validateConsistency() {
        validateCode(this.code)

        if (this.isRoot) {
            require(this.code.length == 3) { "루트의 code 는 정확히 3자리여야 합니다." }
        } else {
            require(this.code.length >= 6) { "비루트의 code 는 최소 6자리(부모 3자리 + 자신 3자리) 이상이어야 합니다." }
        }
    }

    fun update(title: String, newSegment: String) = apply {
        this.title = title
        changeMySegment(newSegment)

        validateConsistency()
    }

    // 마지막 3자리 세그먼트만 교체합니다(부모 경로는 유지)
    private fun changeMySegment(newSegment: String) {
        if (newSegment == this.segment) return
        validateSegment(newSegment)

        val parenCode = this.parentCodeOrEmpty
        val newCode = makeCode(parenCode, newSegment)

        this.code = newCode
    }

    fun rebaseWithParent(oldPrefix: String, newPrefix: String) = apply {
        validateCode(oldPrefix)
        validateCode(newPrefix)

        require(oldPrefix.length == newPrefix.length) {
            "oldPrefix 와 newPrefix 는 동일한 길이를 가져야 합니다."
        }
        require(this.code.startsWith(oldPrefix)) { "후손 code 가 아닙니다." }

        val suffix = this.code.removePrefix(oldPrefix)
        val newCode = makeCode(newPrefix, suffix)

        this.code = newCode

        validateConsistency()
    }
}