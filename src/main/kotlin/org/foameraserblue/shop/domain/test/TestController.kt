package org.foameraserblue.shop.domain.test

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.foameraserblue.shop.domain.category.infrastructure.db.CategoryJpaRepository
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/*
카테고리 테스트 트리 (루트 4개, depth ≤ 3)
코드는 Materialized Path(3자리 세그먼트 연결)로 생성됩니다.
예) 루트 "상의"=001, 자식 "티셔츠"=001002, 손자 "반팔"=001002003

상의 001
├── 티셔츠 001002
│   ├── 반팔 001002003
│   └── 긴팔 001002004
└── 셔츠 001005
    ├── 옥스포드 001005006
    └── 린넨 001005007
        ├── 얇은 001005007008
        └── 두꺼운 001005007009

하의 010
├── 청바지 010011
│   ├── 스트레이트 010011012
│   └── 슬림 010011013
├── 슬랙스 010014
└── 반바지 010015

아우터 016
├── 코트 016017
│   └── 트렌치 016017018
│       ├── 롱 016017018019
│       └── 숏 016017018020
└── 자켓 016021

악세서리 022
├── 모자 022023
│   ├── 비니 022023024
│   └── 볼캡 022023025
├── 벨트 022026
└── 양말 022027
    ├── 캐주얼 022027028
    │   └── 단목 022027028029
    └── 스포츠 022027030
        └── 기능성 022027030031
*/

@Tag(name = "테스트 컨트롤러", description = "테스트 전용 컨트롤러")
@RestController
@RequestMapping("/test")
class TestController(
    private val commandCategoryUseCase: CommandCategoryUseCase,
    private val categoryJpaRepository: CategoryJpaRepository,
) {
    @Operation(description = "호출시 모든 데이터를 삭제한 후 테스트 데이터로 insert 합니다.")
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
        fun nextSegment(): String = "%03d".format(seq++)

        // 루트 생성: parentCode="" 로 전달, 반환값은 풀 경로 코드(=세그먼트)
        fun createRoot(title: String): String {
            val seg = nextSegment()
            commandCategoryUseCase.create(
                parentCode = "",
                title = title,
                segmentCode = seg
            )
            return seg // 루트의 풀코드는 세그먼트와 동일
        }

        // 자식 생성: parentPath 는 부모의 풀 경로 코드, 반환값은 자식의 풀 경로 코드
        fun createChild(parentPath: String, title: String): String {
            val seg = nextSegment()
            commandCategoryUseCase.create(
                parentCode = parentPath,
                title = title,
                segmentCode = seg
            )
            return parentPath + seg
        }

        // 트리 1: 상의
        val topRoot = createRoot("상의")                       // 001
        val tShirt = createChild(topRoot, "티셔츠")             // 001002
        createChild(tShirt, "반팔")                             // 001002003
        createChild(tShirt, "긴팔")                             // 001002004
        val shirt = createChild(topRoot, "셔츠")                // 001005
        createChild(shirt, "옥스포드")                          // 001005006
        val linen = createChild(shirt, "린넨")                  // 001005007
        createChild(linen, "얇은")                              // 001005007008
        createChild(linen, "두꺼운")                            // 001005007009

        // 트리 2: 하의
        val bottomRoot = createRoot("하의")                     // 010
        val jeans = createChild(bottomRoot, "청바지")           // 010011
        createChild(jeans, "스트레이트")                        // 010011012
        createChild(jeans, "슬림")                              // 010011013
        createChild(bottomRoot, "슬랙스")                       // 010014
        createChild(bottomRoot, "반바지")                       // 010015

        // 트리 3: 아우터
        val outerRoot = createRoot("아우터")                    // 016
        val coat = createChild(outerRoot, "코트")               // 016017
        val trench = createChild(coat, "트렌치")                // 016017018
        createChild(trench, "롱")                               // 016017018019
        createChild(trench, "숏")                               // 016017018020
        createChild(outerRoot, "자켓")                          // 016021

        // 트리 4: 악세서리
        val accRoot = createRoot("악세서리")                    // 022
        val hat = createChild(accRoot, "모자")                  // 022023
        createChild(hat, "비니")                                // 022023024
        createChild(hat, "볼캡")                                // 022023025
        createChild(accRoot, "벨트")                            // 022026
        val socks = createChild(accRoot, "양말")                // 022027
        val casual = createChild(socks, "캐주얼")               // 022027028
        createChild(casual, "단목")                              // 022027028029
        val sports = createChild(socks, "스포츠")               // 022027030
        createChild(sports, "기능성")                            // 022027030031
    }
}