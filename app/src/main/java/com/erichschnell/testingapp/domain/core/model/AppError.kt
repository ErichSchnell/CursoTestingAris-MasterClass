package com.erichschnell.testingapp.domain.core.model

sealed class AppError : Exception() {
    data object NetworkError : AppError() {
        private fun readResolve(): Any = NetworkError
    }

    data object NotFoundError : AppError() {
        private fun readResolve(): Any = NotFoundError
    }

    data object DatabaseError : AppError() {
        private fun readResolve(): Any = DatabaseError
    }

    sealed class Validation : AppError() {
        data object QuantityMustBePositive : Validation() {
            private fun readResolve(): Any = QuantityMustBePositive
        }

        data class InsufficientStock(
            val available: Int,
        ) : Validation()
    }

    data class UnknownError(
        override val message: String?,
    ) : AppError()
}

/*

: Exception() {

    data object NetworkError: AppError() {
        private fun readResolve(): Any = NetworkError
    }

    data object NotFoundError: AppError() {
        private fun readResolve(): Any = NotFoundError
    }

    data object DatabaseError: AppError() {
        private fun readResolve(): Any = DatabaseError
    }
//    data class ValidationError(override val message:String): AppError()

    sealed class Validation: AppError(){
        data object QuantityMustBePositive: Validation() {
            private fun readResolve(): Any = QuantityMustBePositive
        }


        data class InsufficientStock(val available:Int): Validation()
    }
    data class UnknownError(override val message:String?): AppError()

}
 */
