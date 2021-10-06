package com.example.hyperonetask.base

import com.example.hyperonetask.data.model.ErrorModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException

abstract class BaseRepository {

    /*companion object {
        val errorNullableType = object : TypeToken<BaseNullableResponse<ErrorResponse>>() {}.type
        val errorType = object : TypeToken<BaseResponse<ErrorResponse>>() {}.type
    }*/

    suspend fun <T> execute(job: suspend () -> T): T = withContext(Dispatchers.IO) {
        try {
            return@withContext job()
        } catch (e: Exception) {
            Timber.e(e)
            when (e) {
                is IOException -> throw ErrorModel.Connection
                is HttpException -> throw getHttpErrorMessage(e)
            }
            throw e
        }
    }

    private fun getHttpErrorMessage(httpException: HttpException): Exception {
        return try {
            val response = httpException.response()!!.errorBody()!!.string()
            return ErrorModel.Network(httpException.code(), response)
            /*val errorResponse: BaseNullableResponse<ErrorResponse> = Gson().fromJson(response, errorNullableType)
            if(errorResponse.data != null) {
                return ErrorModel.Network(httpException.code(), errorResponse.data.errors.values.toList()[0])
            }
            else {
                val error: ErrorOldResponse = Gson().fromJson(response, ErrorOldResponse::class.java)
                return ErrorModel.Network(httpException.code(), error.errors[0].message)
            }*/
        } catch (e: Exception) {
            ErrorModel.Unknown
        }
    }
}