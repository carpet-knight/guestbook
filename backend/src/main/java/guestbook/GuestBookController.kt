package guestbook

import guestbook.models.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException
import java.net.MalformedURLException
import java.net.URL
import java.time.OffsetDateTime
import java.time.ZoneId

@RestController
@RequestMapping("/entries")
class GuestBookController(
  @Autowired private val service: GuestBookService
) {

  @GetMapping
  fun lastTen(): JSendResponseWrapper {
    return JSendResponseWrapper(
      JSendStatus.SUCCESS,
      EntryResponseListModel(service.lastTenEntries)
    )
  }

  @GetMapping("/entries/{id}")
  fun getStatus(@PathVariable id: String?): JSendResponseWrapper {
    val request = service.getEntry(id)
      ?: throw ResponseStatusException(
        HttpStatus.NOT_FOUND,
        "Could not find request"
      )

    return if (request.state == EntryRequestState.ERROR) JSendResponseWrapper(
      JSendStatus.ERROR,
      request.statusMessage
    ) else JSendResponseWrapper(
      JSendStatus.SUCCESS,
      EntryResponseModel(request.entry)
    )
  }

  @PostMapping(consumes = ["application/json"])
  @ResponseStatus(HttpStatus.CREATED)
  fun postEntry(@RequestBody request: EntryRequestModel): JSendResponseWrapper {
    val pictureUrl = try {
      URL(request.imageUrl)
    } catch (ex: MalformedURLException) {
      throw ResponseStatusException(
        HttpStatus.BAD_REQUEST,
        "The picture URL you specified was invalid",
        ex
      )
    }

    val entry = GuestBookEntry(
      request.author,
      request.message,
      pictureUrl,
      OffsetDateTime.now(ZoneId.of("UTC"))
    )

    service.schedule(GuestBookEntryRequest(entry))
    return JSendResponseWrapper(
      JSendStatus.SUCCESS,
      mapOf("entryId" to entry.entryId)
    )
  }
}
