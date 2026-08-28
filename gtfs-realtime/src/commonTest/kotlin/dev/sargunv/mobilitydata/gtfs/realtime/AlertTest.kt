@file:Suppress("DEPRECATION")

package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test

class AlertTest {
  @Test
  fun roundTripsAlertEntities() {
    val feed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "alert-advanced",
              alert =
                Alert(
                  activePeriod = listOf(TimeRange(start = 1_744_640_000, end = 1_744_726_400)),
                  informedEntity =
                    listOf(
                      EntitySelector(
                        agencyId = "agency-1",
                        routeId = "route-d",
                        routeType = 3,
                        trip =
                          TripDescriptor(
                            tripId = "trip-alert-1",
                            startDate = "20260415",
                            scheduleRelationship = TripDescriptor.ScheduleRelationship.Canceled,
                          ),
                        stopId = "stop-30",
                        directionId = 0,
                      )
                    ),
                  cause = Alert.Cause.Construction,
                  effect = Alert.Effect.StopMoved,
                  url =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "https://agency.example/alerts/123",
                            language = "en",
                          )
                        )
                    ),
                  headerText =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Platform change at Central",
                            language = "en",
                          )
                        )
                    ),
                  descriptionText =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Use platform 4 instead of platform 2.",
                            language = "en",
                          )
                        )
                    ),
                  ttsHeaderText =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Platform change at Central Station",
                            language = "en",
                          )
                        )
                    ),
                  ttsDescriptionText =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Use platform 4 instead of platform 2",
                            language = "en",
                          )
                        )
                    ),
                  severityLevel = Alert.SeverityLevel.Severe,
                  image =
                    TranslatedImage(
                      localizedImage =
                        listOf(
                          TranslatedImage.LocalizedImage(
                            url = "https://agency.example/images/platform-change.png",
                            mediaType = "image/png",
                            language = "en",
                          )
                        )
                    ),
                  imageAlternativeText =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Map showing the temporary boarding location",
                            language = "en",
                          )
                        )
                    ),
                  causeDetail =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(text = "Track maintenance", language = "en")
                        )
                    ),
                  effectDetail =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Boarding moved to platform 4",
                            language = "en",
                          )
                        )
                    ),
                ),
            )
          ),
      )

    assertFeedRoundTrips(feed)
  }

  @Test
  fun roundTripsCommunicationAndImpactPeriods() {
    val feed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "alert-periods",
              alert =
                Alert(
                  communicationPeriod =
                    listOf(TimeRange(start = 1_744_640_000, end = 1_744_726_400)),
                  impactPeriod = listOf(TimeRange(start = 1_744_643_600, end = 1_744_722_800)),
                  informedEntity = listOf(EntitySelector(routeId = "route-d")),
                  headerText =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Track work on Route D",
                            language = "en",
                          )
                        )
                    ),
                ),
            )
          ),
      )

    assertFeedRoundTrips(feed)
  }
}
