package org.foameraserblue.shop.domain.category.controller

import org.foameraserblue.shop.domain.category.controller.dto.*
import org.foameraserblue.shop.domain.category.service.usecase.CommandCategoryUseCase
import org.foameraserblue.shop.domain.category.service.usecase.QueryCategoryUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/categories")
class CategoryController(
    private val queryCategoryUseCase: QueryCategoryUseCase,
    private val commandCategoryUseCase: CommandCategoryUseCase,
) {
    @PostMapping
    fun create(@RequestBody request: CreateCategoryRequest): CategoryResponse {
        return CategoryResponse(
            commandCategoryUseCase.create(
                parentCode = request.parentCode,
                title = request.title,
                code = request.code,
            )
        )
    }

    @GetMapping
    fun getAll(@RequestParam id: Long?): CategoryTreeResponse {
        return id
            ?.let { CategoryTreeResponse.toDto(queryCategoryUseCase.getAllMeAndChildrenTree(id)) }
            ?: CategoryTreeResponse.toDto(queryCategoryUseCase.getAllTree())

    }

    @PatchMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateCategoryRequest): CategoryResponse {
        return CategoryResponse(commandCategoryUseCase.patch(id, request.title, request.code))
    }

    @PutMapping("{id}/move")
    fun moveLocation(
        @PathVariable id: Long,
        @RequestBody request: MoveCategoryRequest,
    ): CategoryResponse {
        return CategoryResponse(
            commandCategoryUseCase.moveParent(id, request.newParentCode)
        )
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) {
        commandCategoryUseCase.delete(id)
    }
}