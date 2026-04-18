package com.servicerca.app.domain.usecase

import com.servicerca.app.domain.model.Insignia
import com.servicerca.app.domain.model.User
import javax.inject.Inject

/**
 * UseCase to determine which insignias a user has earned based on their statistics.
 */
class GetEarnedInsigniasUseCase @Inject constructor() {

    operator fun invoke(user: User): List<Insignia> {
        return listOf(
            Insignia("verified", user.isEmailVerified),
            Insignia("trusted", user.completedServices >= 10),
            Insignia("50_services", user.completedServices >= 50),
            Insignia("fast", user.rating >= 4.0),
            Insignia("top_5", user.totalPoints >= 4300),
            Insignia("favorite", user.rating >= 4.8),
            Insignia("expert", user.totalPoints >= 7000),
            Insignia("chat", user.approvedReviews >= 5),
            Insignia("eco", user.completedServices >= 100)
        )
    }
}
