package com.example.data.repository

sealed class WashlyException(message: String) : Exception(message) {
    class CarWashOfflineException(
        message: String = "This car wash is currently offline and not accepting new bookings."
    ) : WashlyException(message)

    class InvalidStatusTransitionException(
        message: String = "Invalid booking status transition."
    ) : WashlyException(message)

    class ServiceUnavailableException(
        message: String = "One or more selected services are inactive or unavailable."
    ) : WashlyException(message)

    class NotFoundException(
        message: String = "The requested resource was not found."
    ) : WashlyException(message)

    class UnauthorizedException(
        message: String = "You are not authorized to perform this operation."
    ) : WashlyException(message)
}
