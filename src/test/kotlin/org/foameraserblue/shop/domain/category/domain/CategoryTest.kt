package org.foameraserblue.shop.domain.category.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

class CategoryTest : BehaviorSpec({

    fun root(title: String = "루트", seg: String = "123"): Category =
        Category.createForRoot(title, seg)

    fun leaf(parent: Category, title: String = "자식", seg: String = "456"): Category =
        Category.createForLeaf(parent, title, seg)

    Given("루트 카테고리를 생성하려한다") {
        When("올바른 3자리 숫자 세그먼트로 생성하면") {
            val category = root(title = "상의", seg = "001")

            Then("루트 카테고리로 정상 생성된다") {
                category.title.shouldBe("상의")
                category.code.shouldBe("001")
                category.isRoot.shouldBeTrue()
                category.parentCodeOrEmpty.shouldBe("")
                category.segment.shouldBe("001")
            }
        }

        When("세그먼트가 3자리 숫자가 아니면") {
            listOf("1", "12", "1234", "12A", "A23", " 12", "AAA")

                .forEach { invalidCategory ->
                    Then("예외가 발생한다") {
                        val e = shouldThrow<IllegalArgumentException> {
                            root(seg = invalidCategory)
                        }

                        e.message.shouldBe("코드의 세그먼트는 3자리 숫자여야 합니다.")
                    }
                }
        }
    }

    Given("리프 카테고리를 생성하려한다") {
        val parent = root(seg = "123")

        When("올바른 3자리 세그먼트로 생성하면") {
            val child = leaf(parent, title = "셔츠", seg = "456")

            Then("정상 생성된다") {
                child.title.shouldBe("셔츠")
                child.code.shouldBe("123456")
                child.isRoot.shouldBeFalse()
                child.parentCodeOrEmpty.shouldBe("123")
                child.segment.shouldBe("456")
            }
        }

        When("세그먼트가 3자리 숫자가 아니면") {
            listOf("0", "45", "4567", "45X").forEach { invalid ->

                Then("예외가 발생한다") {
                    val e = shouldThrow<IllegalArgumentException> {
                        leaf(parent, seg = invalid)
                    }

                    e.message.shouldBe("코드의 세그먼트는 3자리 숫자여야 합니다.")
                }
            }
        }
    }

    Given("생성시 코드 벨리데이션") {
        When("code가 비어있으면") {
            Then("예외가 발생한다") {
                val e = shouldThrow<IllegalArgumentException> {
                    Category(id = 1L, title = "비어있음", code = "")
                }

                e.message.shouldBe("code는 비어있을 수 없습니다.")
            }
        }

        When("code에 숫자 이외의 문자가 포함되면") {
            Then("예외가 발생한다") {
                val e = shouldThrow<IllegalArgumentException> {
                    Category(id = 1L, title = "숫자 아님", code = "12A")
                }

                e.message.shouldBe("code는 숫자만 포함해야 합니다.")
            }
        }

        When("code 길이가 세자릿수가 아니면") {
            Then("예외가 발생한다") {
                val e = shouldThrow<IllegalArgumentException> {
                    Category(id = 1L, title = "4 자릿수", code = "1234")
                }

                e.message.shouldContain("code의 길이는 3의 배수여야 합니다.")
            }
        }

        When("루트 카테고리면") {
            val category = Category(title = "루트", code = "999")

            Then("isRoot 는 true 이다") {
                category.isRoot.shouldBeTrue()
            }
        }

        When("루트 카테고리가 아니면") {
            val category1 = Category(title = "비루트1", code = "123456")
            val category2 = Category(title = "비루트2", code = "123456789")

            Then("isRoot 는 false 이다") {
                category1.isRoot.shouldBeFalse()
                category2.isRoot.shouldBeFalse()
            }
        }
    }

    Given("업데이트 테스트") {
        When("변경되는 세그먼트가 기존과 동일할때") {
            val category = Category.createForRoot("비포", "111")
            category.update(title = "에프터", newSegment = "111")

            Then("title만 변경된다") {
                category.title.shouldBe("에프터")
                category.code.shouldBe("111")
                category.segment.shouldBe("111")
            }
        }

        When("세그먼트를 변경하면") {
            val parent = Category.createForRoot("부모", "123")
            val child = Category.createForLeaf(parent, "자식", "456")
            child.update(title = "자식2", newSegment = "789")

            Then("title과 마지막 3자리가 변경되고, 부모 경로는 유지된다") {
                child.title.shouldBe("자식2")
                child.parentCodeOrEmpty.shouldBe("123")
                child.code.shouldBe("123789")
                child.segment.shouldBe("789")
            }
        }

        When("세그먼트가 유효하지 않으면") {
            val parent = Category.createForRoot("부모", "123")
            val child = Category.createForLeaf(parent, "자식", "456")

            Then("예외가 발생한다") {
                val e = shouldThrow<IllegalArgumentException> {
                    child.update(title = "오류 발생", newSegment = "78X")
                }

                e.message.shouldBe("코드의 세그먼트는 3자리 숫자여야 합니다.")
            }
        }
    }

    Given("부모 코드를 리베이스 하려한다") {
        When("올바른 oldPrefix/newPrefix로 리베이스하면") {
            val category = Category(title = "대상", code = "111222333")
            category.rebaseWithParent(oldPrefix = "111", newPrefix = "999")

            Then("앞쪽 접두사만 교체된다") {
                category.code.shouldBe("999222333")
                category.parentCodeOrEmpty.shouldBe("999222")
                category.segment.shouldBe("333")
            }
        }

        When("oldPrefix와 newPrefix 길이가 다르면") {
            val category = Category(title = "1", code = "111")

            Then("예외가 발생한다") {
                val e = shouldThrow<IllegalArgumentException> {
                    category.rebaseWithParent(oldPrefix = "111", newPrefix = "666666")
                }

                e.message.shouldBe("oldPrefix 와 newPrefix 는 동일한 길이를 가져야 합니다.")
            }
        }

        When("oldPrefix가 현재 code의 접두사가 아니면") {
            val category = Category(title = "111", code = "111222333")

            Then("예외가 발생한다") {
                val e = shouldThrow<IllegalArgumentException> {
                    category.rebaseWithParent(oldPrefix = "222", newPrefix = "999")
                }

                e.message.shouldBe("후손 code 가 아닙니다.")
            }
        }

        When("oldPrefix/newPrefix 자체가 유효한 코드가 아니면") {
            val c = Category(title = "111", code = "111222333")

            Then("예외가 발생한다") {
                val e1 = shouldThrow<IllegalArgumentException> {
                    c.rebaseWithParent(oldPrefix = "11A", newPrefix = "999")
                }

                e1.message.shouldBe("code는 숫자만 포함해야 합니다.")

                val e2 = shouldThrow<IllegalArgumentException> {
                    c.rebaseWithParent(oldPrefix = "111", newPrefix = "99")
                }

                e2.message.shouldBe("code의 길이는 3의 배수여야 합니다.")
            }
        }
    }
})