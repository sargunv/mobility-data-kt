package dev.sargunv.mobilitydata.gtfs.realtime

import kotlin.test.Test

class ExperimentalEntitiesTest {
  @Test
  fun roundTripsExperimentalEntities() {
    val feed =
      FeedMessage(
        header = FeedHeader(gtfsRealtimeVersion = "2.0"),
        entity =
          listOf(
            FeedEntity(
              id = "shape-1",
              shape = Shape(shapeId = "shape-detour-1", encodedPolyline = "yzocFzynhVq}@n}@o}@nzD"),
            ),
            FeedEntity(
              id = "stop-1",
              stop =
                Stop(
                  stopId = "temporary-stop-1",
                  stopCode =
                    TranslatedString(
                      translation =
                        listOf(TranslatedString.Translation(text = "TMP1", language = "en"))
                    ),
                  stopName =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(text = "Temporary Main St", language = "en")
                        )
                    ),
                  ttsStopName =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Temporary Main Street",
                            language = "en",
                          )
                        )
                    ),
                  stopDesc =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "Temporary stop during street closure",
                            language = "en",
                          )
                        )
                    ),
                  stopLat = 47.6205F,
                  stopLon = -122.3493F,
                  zoneId = "zone-1",
                  stopUrl =
                    TranslatedString(
                      translation =
                        listOf(
                          TranslatedString.Translation(
                            text = "https://agency.example/stops/tmp1",
                            language = "en",
                          )
                        )
                    ),
                  parentStation = "station-central",
                  stopTimezone = "America/Los_Angeles",
                  wheelchairBoarding = Stop.WheelchairBoarding.Available,
                  levelId = "street",
                  platformCode =
                    TranslatedString(
                      translation =
                        listOf(TranslatedString.Translation(text = "4", language = "en"))
                    ),
                ),
            ),
            FeedEntity(
              id = "modifications-1",
              tripModifications =
                TripModifications(
                  selectedTrips =
                    listOf(
                      TripModifications.SelectedTrips(
                        tripIds = listOf("trip-100", "trip-101"),
                        shapeId = "shape-detour-1",
                      )
                    ),
                  startTimes = listOf("08:00:00", "09:00:00"),
                  serviceDates = listOf("20260416", "20260417"),
                  modifications =
                    listOf(
                      TripModifications.Modification(
                        startStopSelector = StopSelector(stopSequence = 5),
                        endStopSelector = StopSelector(stopSequence = 8),
                        propagatedModificationDelay = 180,
                        replacementStops =
                          listOf(
                            ReplacementStop(travelTimeToStop = 0, stopId = "temporary-stop-1"),
                            ReplacementStop(travelTimeToStop = 300, stopId = "stop-9"),
                          ),
                        serviceAlertId = "alert-advanced",
                        lastModifiedTime = 1_744_730_000,
                      )
                    ),
                ),
            ),
          ),
      )

    assertFeedRoundTrips(feed)
  }
}
