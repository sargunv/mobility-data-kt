package dev.sargunv.mobilitydata.mdb.v1

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request body for `POST /v1/tokens/access`. */
@Serializable
public data class AccessTokenRequest(
  /** Long-lived refresh token exchanged for a short-lived access token. */
  @SerialName("refresh_token") public val refreshToken: String
)

/** Access token returned by `POST /v1/tokens/access`. */
@Serializable
public data class AccessToken(
  /** Bearer token used to call catalog endpoints. */
  @SerialName("access_token") public val accessToken: String? = null,
  /** Instant when the access token expires, in UTC. */
  @SerialName("expiration_datetime_utc") public val expirationDatetimeUtc: IsoDateTime? = null,
  /** Token type. The catalog returns `Bearer`. */
  @SerialName("token_type") public val tokenType: String? = null,
)
