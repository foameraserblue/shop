package org.foameraserblue.shop.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND)
class NotFoundException : RuntimeException {
    constructor() : super("존재하지 않는 데이터입니다.")
    constructor(message: String) : super(message)
}