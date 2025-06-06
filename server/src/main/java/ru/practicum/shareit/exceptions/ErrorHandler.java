package ru.practicum.shareit.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleIncorrectParameter(final NotAvailableException exception) {
        return new ErrorResponse("Некорректный параметр в запросе: ", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse handleMethodValidationException(final HandlerMethodValidationException exception) {
        return new ErrorResponse("Некорректный параметр в запросе: ", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler
    public ErrorResponse handleNotFoundId(final NotFoundException exception) {
        return new ErrorResponse("Указанный Id не найден: ", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResponse handleNoAccess(final NoAccessException exception) {
        return new ErrorResponse("Нет доступа: ", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleNotUniqueEmail(final NotUniqueEmailException exception) {
        return new ErrorResponse("Неуникальный email: ", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler
    public ErrorResponse handleNotUniqueEmailFromDB(final DataIntegrityViolationException exception) {
        return new ErrorResponse("Неуникальный email: ", exception.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse handleServerError(final RuntimeException exception) {
        return new ErrorResponse("Ошибка в работе сервера.", exception.getMessage());
    }
}