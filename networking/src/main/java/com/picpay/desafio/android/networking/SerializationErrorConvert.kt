package com.picpay.desafio.android.networking

import com.google.gson.JsonSyntaxException
import com.picpay.desafio.android.domain.errors.ErrorConvert
import com.picpay.desafio.android.domain.errors.RemoteServiceError

object SerializationErrorConvert : ErrorConvert {

    override suspend fun convert(incoming: Throwable) =
        when (incoming) {
            is JsonSyntaxException -> RemoteServiceError.UnexpectedResponse
            else -> incoming
        }
}