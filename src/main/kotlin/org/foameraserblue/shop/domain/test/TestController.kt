package org.foameraserblue.shop.domain.test

import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryJpaRepository
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
카테고리 테스트 트리 (루트 4개, depth ≤ 3)

상의 001
├── 티셔츠 002
│   ├── 반팔 003
│   └── 긴팔 004
└── 셔츠 005
    ├── 옥스포드 006
    └── 린넨 007
        ├── 얇은 008
        └── 두꺼운 009

하의 010
├── 청바지 011
│   ├── 스트레이트 012
│   └── 슬림 013
├── 슬랙스 014
└── 반바지 015

아우터 016
├── 코트 017
│   └── 트렌치 018
│       ├── 롱 019
│       └── 숏 020
└── 자켓 021

악세서리 022
├── 모자 023
│   ├── 비니 024
│   └── 볼캡 025
├── 벨트 026
└── 양말 027
    ├── 캐주얼 028
    │   └── 단목 029
    └── 스포츠 030
        └── 기능성 031
*/

@RestController
@RequestMapping("/test")
class TestController(
    private val commandCategoryUseCase: CommandCategoryUseCase,
    private val categoryJpaRepository: CategoryJpaRepository,
) {
    @PostMapping
    fun refreshAndMakeTestData() {
        refreshAllData()
        makeTestData()
    }

    private fun refreshAllData() {
        categoryJpaRepository.deleteAll()
    }

    private fun makeTestData() {
        var seq = 1
        fun nextCode(): String = "%03d".format(seq++)

        fun createRoot(title: String): String {
            val code = nextCode()
            commandCategoryUseCase.create(
                parentCode = null,
                title = title,
                code = code
            )
            return code
        }

        fun createChild(parent: String, title: String): String {
            val code = nextCode()
            commandCategoryUseCase.create(
                parentCode = parent,
                title = title,
                code = code
            )
            return code
        }

        // 트리 1: 상의
        val topRoot = createRoot("상의")                 // depth 0
        val tShirt = createChild(topRoot, "티셔츠")       // depth 1
        createChild(tShirt, "반팔")                       // depth 2
        createChild(tShirt, "긴팔")                       // depth 2
        val shirt = createChild(topRoot, "셔츠")          // depth 1
        createChild(shirt, "옥스포드")                    // depth 2
        val linen = createChild(shirt, "린넨")            // depth 2
        createChild(linen, "얇은")                        // depth 3
        createChild(linen, "두꺼운")                      // depth 3

        // 트리 2: 하의
        val bottomRoot = createRoot("하의")               // depth 0
        val jeans = createChild(bottomRoot, "청바지")     // depth 1
        createChild(jeans, "스트레이트")                  // depth 2
        createChild(jeans, "슬림")                        // depth 2
        createChild(bottomRoot, "슬랙스")                 // depth 1
        createChild(bottomRoot, "반바지")                 // depth 1

        // 트리 3: 아우터
        val outerRoot = createRoot("아우터")              // depth 0
        val coat = createChild(outerRoot, "코트")         // depth 1
        val trench = createChild(coat, "트렌치")          // depth 2
        createChild(trench, "롱")                         // depth 3
        createChild(trench, "숏")                         // depth 3
        createChild(outerRoot, "자켓")                    // depth 1

        // 트리 4: 악세서리
        val accRoot = createRoot("악세서리")              // depth 0
        val hat = createChild(accRoot, "모자")            // depth 1
        createChild(hat, "비니")                          // depth 2
        createChild(hat, "볼캡")                          // depth 2
        createChild(accRoot, "벨트")                      // depth 1
        val socks = createChild(accRoot, "양말")          // depth 1
        val casual = createChild(socks, "캐주얼")         // depth 2
        createChild(casual, "단목")                       // depth 3
        val sports = createChild(socks, "스포츠")         // depth 2
        createChild(sports, "기능성")                     // depth 3
    }
}