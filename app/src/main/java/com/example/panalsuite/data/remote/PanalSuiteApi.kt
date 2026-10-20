package com.example.panalsuite.data.remote

import com.example.panalsuite.data.remote.dto.TicketDto
import com.example.panalsuite.data.remote.dto.UserDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/** Endpoints of the PanalSuite mock API (see api/README.md). */
interface PanalSuiteApi {

    @GET("users")
    suspend fun findUser(
        @Query("email") email: String,
        @Query("password") password: String
    ): List<UserDto>

    @GET("tickets")
    suspend fun getTickets(): List<TicketDto>

    @POST("tickets")
    suspend fun createTicket(@Body ticket: TicketDto): TicketDto

    @PUT("tickets/{id}")
    suspend fun updateTicket(
        @Path("id") id: String,
        @Body ticket: TicketDto
    ): TicketDto
}
