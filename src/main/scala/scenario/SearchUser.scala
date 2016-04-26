package scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scala.concurrent.duration._

class SearchUser extends Simulation {
  val resourceDir = "/Users/jeanbombeur/Desktop/workspace/gatling/src/main/resources"

  val httpConf = http // 4
    .baseURL("http://frontend.preproduction.keljob.internal") // 5
    .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8") // 6
    .doNotTrackHeader("1")
    .acceptLanguageHeader("en-US,en;q=0.5")
    .acceptEncodingHeader("gzip, deflate")
    .userAgentHeader("Mozilla/5.0 (Windows NT 5.1; rv:31.0) Gecko/20100101 Firefox/31.0")

  val wordsFeeder = csv(s"$resourceDir/recherches.txt").random
  val searchScenario = scenario("SearchUser")
    .feed(wordsFeeder)
    .exec(
      http("search").get("/recherche?q=${word}")
    )

  val offerFeeder = csv(s"$resourceDir/offres.txt").random
  val viewScenario = scenario("FcaUser")
      .feed(offerFeeder)
        .exec(
          http("view").get("/offre/${slug}")
        )


  setUp(
//    scn.inject(atOnceUsers(1))
    searchScenario.inject(constantUsersPerSec(25).during(1 minutes)),
    viewScenario.inject(
//                      nothingFor(30 seconds)
                      constantUsersPerSec(25).during(1 minute))
  ).protocols(httpConf)
}
