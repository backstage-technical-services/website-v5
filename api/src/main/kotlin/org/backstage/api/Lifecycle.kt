package org.backstage.api

import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import io.quarkus.runtime.configuration.ConfigUtils
import jakarta.enterprise.context.ApplicationScoped
import jakarta.enterprise.event.Observes
import org.jboss.logging.Logger

@ApplicationScoped
class Lifecycle {
    private val logger = Logger.getLogger(this::class.java.canonicalName)

    @Suppress("UnusedParameter")
    fun onStart(@Observes event: StartupEvent) {
        logger.info("Started application with profiles: ${ConfigUtils.getProfiles().joinToString(separator = ", ")}")
    }

    @Suppress("UnusedParameter")
    fun onStop(@Observes event: ShutdownEvent) {
        logger.info("Stopping application cleanly ...")
    }
}
