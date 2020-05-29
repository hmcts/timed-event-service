# ia-timed-event-service
Service to trigger CCD Events in the future

### Background
There are Business needs to do actions in future with specified period of time like reminders, state change triggers etc.
`ia-timed-event-service` allows to schedule submission for desired CCD Event with given date and time in future.

### Usage

Scheduling new CCD Event can be done by sending request with `TimedEvent` JSON body to `/timed-event` endpoint:
```
{
  "caseId": 0,
  "caseType": "Asylum",
  "event": "someEvent",
  "jurisdiction": "IA",
  "scheduledDateTime": "2020-05-29T14:21:08.758Z"
}
```

`event` - desired CCD Event to being submitted in future

`scheduledDateTime` - exact date and time when CCD Event should be submitted

Response contains created `TimedEvent` object with `id` field for reference and response status `201 CREATED`.

GET endpoint `/timed-event/{id}` allows to check if `TimedEvent` is scheduled in future. The endpoint returns only events scheduled in future. If the event has already been submitted, response code returns as `404 NotFound`.

Sending another POST request for already created `TimedEvent` with valid `id` field, re-schedules it in the system.

API details about usages and error statuses are placed in [Swagger UI](http://ia-timed-event-service-aat.service.core-compute-aat.internal/swagger-ui.html)

### Implementation

Scheduling is based on Quartz Framework and Spring Boot integration. Persistence storage is Postgres database.

[Quartz Documentation](http://www.quartz-scheduler.org/documentation/)

Event submission is done by dedicated system user with `caseworker-ia-system` role. Make sure your CCD definitions are in place before Event submission is done.

`ia-timed-event-service` has finite retry policy and it tries configurable number of times to submit given CCD Event.

Authentication is defined as any other Reform application with Idam `Authorization` token and S2S `ServiceAuthorization` token.

Every Business logic and validation have to be implemented in scheduled CCD Event. `ia-timed-event-serivce` is not responsible for checking case state data.
 
For example: Scheduled Event may become not needed anymore after manual user action is taken. 

In this case downstream application (ia-case-api) must implement robust logic to prevent unsuspected behaviour and handle future CCD Event submission gracefully. 