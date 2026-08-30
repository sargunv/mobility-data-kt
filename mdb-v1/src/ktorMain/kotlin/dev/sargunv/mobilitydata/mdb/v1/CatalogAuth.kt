package dev.sargunv.mobilitydata.mdb.v1

/** Credentials for the Mobility Database Catalog API. */
public sealed class CatalogAuth {
  /** Exchange a long-lived refresh token for a one-hour access token. */
  public data class Refresh(
    /** Refresh token issued by MobilityData. */
    public val refreshToken: String
  ) : CatalogAuth()

  /** Call the catalog with an existing access token. */
  public data class Access(
    /** Bearer access token. */
    public val accessToken: String
  ) : CatalogAuth()
}
