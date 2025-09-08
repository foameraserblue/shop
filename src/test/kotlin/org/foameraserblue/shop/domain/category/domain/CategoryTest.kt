package org.foameraserblue.shop.domain.category.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class CategoryTest : BehaviorSpec({
    fun root(title: String = "상의", code: String = "TOP"): Category =
        Category.createForRoot(title, code)

    fun leaf(
        parent: Category,
        title: String = "셔츠",
        code: String = "SHIRT",
    ): Category = Category.createForLeaf(parent, title, code)

    given("createForRoot 팩토리") {
        `when`("루트 카테고리를 생성하면") {
            val category = root(title = "루트", code = "ROOT")

            then("depth=0, parentCode=null, rootCode=code, isRoot=true 이어야 한다") {
                category.title.shouldBe("루트")
                category.depth.shouldBe(0)
                category.parentCode.shouldBe(null)
                category.rootCode.shouldBe("ROOT")
                category.code.shouldBe("ROOT")
                category.isRoot.shouldBeTrue()
            }
        }
    }

    given("createForLeaf 팩토리") {
        val parent = root(code = "ROOT")

        `when`("자식 카테고리를 생성하면") {
            val child = leaf(parent, title = "자식", code = "CHILD")

            then("부모의 rootCode를 상속하고, parentCode는 부모의 code, depth는 부모+1 이어야 한다") {
                child.title.shouldBe("자식")
                child.depth.shouldBe(parent.depth + 1)
                child.parentCode.shouldBe(parent.code)
                child.rootCode.shouldBe(parent.rootCode)
                child.code.shouldBe("CHILD")
                child.isRoot.shouldBeFalse()
            }
        }
    }

    given("유효성 검증") {
        `when`("depth가 음수인 카테고리를 생성하면") {
            then("예외가 발생한다") {
                val ex = shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "invalid",
                        depth = -1,
                        rootCode = "R",
                        parentCode = null,
                        code = "C",
                    )
                }
                ex.message.shouldContain("depth 는 음수가 될 수 없습니다.")
            }
        }

        `when`("depth=0 이면서 parentCode != null 인 경우") {
            then("예외가 발생한다") {
                val ex = shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "invalid",
                        depth = 0,
                        rootCode = "R",
                        parentCode = "P",
                        code = "C",
                    )
                }
                ex.message.shouldContain("root 카테고리여야합니다")
            }
        }

        `when`("depth>0 이면서 parentCode == null 인 경우") {
            then("예외가 발생한다") {
                val ex = shouldThrow<IllegalArgumentException> {
                    Category(
                        id = 1L,
                        title = "invalid",
                        depth = 1,
                        rootCode = "R",
                        parentCode = null,
                        code = "C",
                    )
                }
                ex.message.shouldContain("root 카테고리여야합니다")
            }
        }
    }

    given("patch 동작") {
        `when`("title만 변경하면") {
            val category = root(title = "before", code = "ROOT").apply {
                update(title = "after", code = null)
            }

            then("title만 바뀌어야 한다") {
                category.title.shouldBe("after")
                category.code.shouldBe("ROOT")
            }
        }

        `when`("code만 변경하면") {
            val category = root(title = "title", code = "ROOT").apply {
                update(title = null, code = "NEW")
            }

            then("code만 바뀌어야 한다") {
                category.title.shouldBe("title")
                category.code.shouldBe("NEW")
            }
        }

        `when`("title, code 모두 변경하면") {
            val category = root(title = "before", code = "ROOT").apply {
                update(title = "after", code = "NEW")
            }

            then("둘 다 바뀌어야 한다") {
                category.title.shouldBe("after")
                category.code.shouldBe("NEW")
            }
        }

        `when`("둘 다 null이면") {
            val category = root(title = "title", code = "ROOT").apply {
                update(title = null, code = null)
            }

            then("아무 변화가 없어야 한다") {
                category.title.shouldBe("title")
                category.code.shouldBe("ROOT")
            }
        }
    }

    given("updateParentCode 동작") {
        val parent = root(code = "ROOT")
        val child = leaf(parent, code = "CHILD")

        `when`("parentCode만 변경하면") {
            child.updateParentCode("NEW_PARENT")

            then("parentCode만 바뀌고 나머지는 유지된다") {
                child.parentCode.shouldBe("NEW_PARENT")
                child.rootCode.shouldBe("ROOT")
                child.depth.shouldBe(1)
            }
        }
    }

    given("moveWithParent 동작") {
        `when`("양의 depthGap 으로 이동하면") {
            val parent = root(code = "ROOT")
            val child = leaf(parent, code = "CHILD").apply {
                moveWithParent(newRootCode = "NEW_ROOT", depthGap = 2)
            }

            then("rootCode가 교체되고 depth가 gap만큼 증가한다") {
                child.rootCode.shouldBe("NEW_ROOT")
                child.depth.shouldBe(3) // 1 + 2
            }
        }

        `when`("음의 depthGap 으로 이동하면") {
            val parent = root(code = "ROOT")
            val child = leaf(parent, code = "CHILD").apply {
                moveWithParent(newRootCode = "NEW_ROOT", depthGap = -1)
            }

            then("rootCode가 교체되고 depth가 gap만큼 감소한다") {
                child.rootCode.shouldBe("NEW_ROOT")
                child.depth.shouldBe(0) // 1 - 1
            }
        }
    }

    given("moveParent 동작") {
        `when`("부모를 null로 이동하면(루트로 승격)") {
            val parent = root(code = "ROOT")
            val child = leaf(parent, code = "CHILD").apply {
                moveParent(newParent = null)
            }

            then("rootCode=자신의 code, depth=0, parentCode=null 이어야 한다") {
                child.rootCode.shouldBe("CHILD")
                child.depth.shouldBe(0)
                child.parentCode.shouldBe(null)
                child.isRoot.shouldBeTrue()
            }
        }

        `when`("다른 부모로 이동하면") {
            val rootA = root(code = "A")
            val rootB = root(code = "B")
            val a1 = leaf(rootA, code = "A1")
            val b1 = leaf(rootB, code = "B1")

            a1.moveParent(b1)

            then("새 부모 기준으로 rootCode, depth, parentCode가 재설정된다") {
                a1.rootCode.shouldBe(rootB.rootCode)
                a1.parentCode.shouldBe(b1.code)
                a1.depth.shouldBe(b1.depth + 1)
                a1.isRoot.shouldBeFalse()
            }
        }
    }
})


