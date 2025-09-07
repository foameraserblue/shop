package org.foameraserblue.shop.common.exception.handler

import org.foameraserblue.shop.common.exception.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.resource.NoResourceFoundException

// 테스트 오류 확인용으로 작성
@RestControllerAdvice
class ExceptionHandler {
    private val log = LoggerFactory.getLogger(this.javaClass)

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

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException::class)
    fun handleNoResourceFoundException(exception: NoResourceFoundException): ErrorMessageDto {
        return response(exception)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception::class)
    fun handleTotalException(exception: Exception): ErrorMessageDto {
        log.error("서버 애러", exception)
        return ErrorMessageDto()
    }

    private fun response(exception: Exception) = ErrorMessageDto(exception.message.toString())
}