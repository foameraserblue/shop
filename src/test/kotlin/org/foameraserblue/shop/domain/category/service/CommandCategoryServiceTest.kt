package org.foameraserblue.shop.domain.category.service

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.foameraserblue.shop.domain.category.domain.Category
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryAdapter

class CommandCategoryServiceTest : BehaviorSpec({
    lateinit var adapter: CategoryAdapter
    lateinit var service: CommandCategoryService

    beforeSpec {
        adapter = mockk(relaxed = true)
        service = CommandCategoryService(adapter)
    }

    beforeContainer {
        clearMocks(adapter, answers = false)
    }

    fun root(title: String = "루트", seg: String = "001") =
        Category.createForRoot(title, seg)

    Given("루트 카테고리를 생성하려한다") {
        When("parentCode가 빈 문자열이면") {
            val title = "루트"
            val segmentCode = "001"

            every { adapter.existsByCode(segmentCode) } returns false
            every { adapter.save(any()) } answers { firstArg() }

            val saved = service.create(parentCode = "", title = title, segmentCode = segmentCode)

            Then("정상적으로 생성된다") {
                saved.title.shouldBe(title)
                saved.code.shouldBe(segmentCode)
                saved.isRoot.shouldBeTrue()

                verify(exactly = 0) { adapter.findByCode(any()) }
                verify(exactly = 1) { adapter.existsByCode(segmentCode) }
                verify(exactly = 1) { adapter.save(match { it.title == title && it.code == segmentCode }) }
            }
        }
    }

    Given("리프 카테고리 생성") {
        When("parentCode가 비어있지 않으면") {
            val parent = root(seg = "010")
            val segmentCode = "011"
            val title = "리프"
            val expectedCode = "010011"

            every { adapter.findByCode(parent.code) } returns parent
            every { adapter.existsByCode(expectedCode) } returns false
            every { adapter.save(any()) } answers { firstArg() }

            val saved = service.create(parentCode = parent.code, title = title, segmentCode = segmentCode)

            Then("정상적으로 생성된다") {
                saved.code.shouldBe(expectedCode)
                saved.title.shouldBe(title)

                verify(exactly = 1) { adapter.findByCode("010") }
                verify(exactly = 1) { adapter.existsByCode(expectedCode) }
                verify(exactly = 1) { adapter.save(match { it.code == expectedCode && it.title == title }) }
            }
        }

        When("해당 코드로 이미 만들어진 카테고리가 존재하면") {
            val parent = root(seg = "020")
            val title = "중복"
            val segmentCode = "021"
            val duplicateCode = "020021"

            every { adapter.findByCode(parent.code) } returns parent
            every { adapter.existsByCode(duplicateCode) } returns true

            Then("예외가 발생하고 저장은 호출되지 않는다") {
                val e = shouldThrow<IllegalArgumentException> {
                    service.create(parentCode = parent.code, title = title, segmentCode = segmentCode)
                }

                e.message.shouldBe("이미 해당 code로 저장된 데이터가 존재합니다.")

                verify(exactly = 1) { adapter.findByCode("020") }
                verify(exactly = 1) { adapter.existsByCode(duplicateCode) }
                verify(exactly = 0) { adapter.save(any()) }
            }
        }
    }

    Given("업데이트시 세그먼트가 동일하여 코드가 변경되지 않는 경우") {
        When("old code 와 new code 가 같을때") {
            val category = Category.createForLeaf(root("부모", "030"), "자식", "031")

            every { adapter.findByCode("030031") } returns category
            every { adapter.save(any()) } answers { firstArg() }

            val saved = service.update(
                code = "030031",
                title = "변경된 제목",
                newSegmentCode = "031"
            )

            Then("코드 변경 없이 title 만 변경된다") {
                saved.title.shouldBe("변경된 제목")
                saved.code.shouldBe("030031")

                verify(exactly = 1) { adapter.findByCode("030031") }
                verify(exactly = 0) { adapter.existsByCode(any()) }
                verify(exactly = 0) { adapter.findAllByCodeStartingWithAndCodeNot(any(), any()) }
                verify(exactly = 0) { adapter.saveAll(any<List<Category>>()) }
                verify(exactly = 1) { adapter.save(match { it.code == "030031" && it.title == "변경된 제목" }) }
            }
        }
    }

    Given("업데이트시 세그먼트 변경으로 코드가 바뀌는 경우") {
        When("old code 와 new code 가 다를때") {
            val category = Category.createForLeaf(root("부모", "040"), "자식", "041")
            val oldCode = "040041"
            val newSegment = "042"
            val newCode = "040042"

            val descendent1 = Category(title = "후손1", code = "040041050")
            val descendent2 = Category(title = "후손2", code = "040041051")

            every { adapter.findByCode(oldCode) } returns category
            every { adapter.existsByCode(newCode) } returns false
            every { adapter.findAllByCodeStartingWithAndCodeNot(oldCode, oldCode) } returns listOf(
                descendent1,
                descendent2
            )
            every { adapter.saveAll(any<List<Category>>()) } answers { firstArg() }
            every { adapter.save(any()) } answers { firstArg() }

            val saved = service.update(
                code = oldCode,
                title = "변경된 제목",
                newSegmentCode = newSegment
            )

            Then("title 과 code 가 변경된다") {
                saved.title.shouldBe("변경된 제목")
                saved.code.shouldBe(newCode)

                descendent1.code.shouldBe("040042050")
                descendent2.code.shouldBe("040042051")

                verify(exactly = 1) { adapter.findByCode(oldCode) }
                verify(exactly = 1) { adapter.existsByCode(newCode) }
                verify(exactly = 1) { adapter.findAllByCodeStartingWithAndCodeNot(oldCode, oldCode) }
                verify(exactly = 1) {
                    adapter.saveAll(
                        match { list ->
                            list.map { it.code }.shouldContainExactly("040042050", "040042051")
                            true
                        }
                    )
                }
                verify(exactly = 1) { adapter.save(match { it.code == newCode && it.title == "변경된 제목" }) }
            }
        }

        When("새로운 코드가 이미 존재하면") {
            val category = Category.createForLeaf(root("부모", "060"), "자식", "061")
            val oldCode = "060061"
            val newCode = "060062"

            every { adapter.findByCode(oldCode) } returns category
            every { adapter.existsByCode(newCode) } returns true

            Then("예외가 발생하고 후손 리베이스, 저장은 발생하지 않는다") {
                val e = shouldThrow<IllegalArgumentException> {
                    service.update(code = oldCode, title = "t", newSegmentCode = "062")
                }

                e.message.shouldBe("이미 해당 code로 저장된 데이터가 존재합니다.")

                verify(exactly = 1) { adapter.findByCode(oldCode) }
                verify(exactly = 1) { adapter.existsByCode(newCode) }
            }
        }
    }

    Given("카테고리를 삭제하려한다") {
        When("삭제를 호출하면") {
            val me = Category(title = "본인", code = "050")
            val meAndDescendants = listOf(
                me,
                Category(title = "자식1", code = "050051"),
                Category(title = "자식2", code = "050052"),
            )

            every { adapter.findByCode("050") } returns me
            every { adapter.findAllByCodeStartingWith("050") } returns meAndDescendants
            every { adapter.deleteAll(meAndDescendants) } returns Unit

            service.delete("050")

            Then("본인을 포함한 후손을 조회하여 일괄 삭제한다") {
                verify(exactly = 1) { adapter.findByCode("050") }
                verify(exactly = 1) { adapter.findAllByCodeStartingWith("050") }
                verify(exactly = 1) { adapter.deleteAll(meAndDescendants) }
            }
        }
    }
})