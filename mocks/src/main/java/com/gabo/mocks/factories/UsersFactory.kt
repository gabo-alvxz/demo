package com.gabo.mocks.factories

import com.gabo.models.models.seller.User
import java.util.*

object UsersFactory {
    fun fakeUser(name : String? = null) : User{
        return User(
            id = UUID.randomUUID().toString(),
            displayName = name ?: "Test user",
            url ="http://test.com",
            reputation = User.Reputation(
                level = User.ReputationLevel.GREEN,
                status = User.ReputationStatus.GOLD
            )
        )
    }
}