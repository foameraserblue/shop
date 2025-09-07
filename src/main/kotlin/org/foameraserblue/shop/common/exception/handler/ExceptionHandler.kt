package org.foameraserblue.shop.common.exception.handler

import org.foameraserblue.shop.common.exception.NotFoundException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

// 테스트 오류 확인용으로 작성
@RestControllerAdvice
class ExceptionHandler {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException::class, IllegalStateException::class)
    fun handleBadRequestException(exception: RuntimeException): ErrorMessageDto {
        return response(exception)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(exception: NotFoundException): ErrorMessageDto {
        return response(exception)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleTotalException(exception: Exception): ErrorMessageDto {
        return ErrorMessageDto()
    }
    
    private fun response(exception: Exception) = ErrorMessageDto(exception.message.toString())
}