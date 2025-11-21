package org.backstage.api.monitoring

import org.eclipse.microprofile.health.HealthCheck
import org.eclipse.microprofile.health.HealthCheckResponse
import org.eclipse.microprofile.health.Liveness

@Liveness
class HealthCheck : HealthCheck {
    override fun call(): HealthCheckResponse {
        return HealthCheckResponse.up("alive")
    }
}
