package app.ssnc.oasis.entity.response

import app.ssnc.oasis.entity.model.User

data class LoginResponse (
    val token_type: String,
    val access_token: String,
    val refresh_token: String,
    val expires_in: Long,
    val id_token: String,
    val user: User
)
