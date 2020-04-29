ARG APP_INSIGHTS_AGENT_VERSION=2.5.1

# Application image

FROM hmctspublic.azurecr.io/base/java:openjdk-8-distroless-1.2

COPY lib/AI-Agent.xml /opt/app/
COPY build/libs/ia-timed-event-service.jar /opt/app/

EXPOSE 8095
CMD [ "ia-timed-event-service.jar" ]
