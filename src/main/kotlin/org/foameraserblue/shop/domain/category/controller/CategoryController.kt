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
                title = request.title,
                parentId = request.parentId,
            )
        )
    }

    @GetMapping
    fun getAll(@RequestParam id: Long?): CategoryTreeResponse {
        return CategoryTreeResponse.toDto(
            id
                ?.let { queryCategoryUseCase.getAllWithChildrenById(id) }
                ?: queryCategoryUseCase.getAll())
    }

    @PutMapping("{id}")
    fun update(@PathVariable id: Long, @RequestBody request: UpdateCategoryRequest): CategoryResponse {
        return CategoryResponse(commandCategoryUseCase.update(id, request.title))
    }

    @PutMapping("{id}/location")
    fun moveLocation(
        @PathVariable id: Long,
        @RequestBody request: MoveLocationCategoryRequest,
    ): CategoryResponse {
        return CategoryResponse(
            commandCategoryUseCase.moveParent(id, request.newParentId)
        )
    }

    @DeleteMapping("{id}")
    fun delete(@PathVariable id: Long) {
        commandCategoryUseCase.delete(id)
    }
}