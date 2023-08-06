package org.acme

import io.smallrye.graphql.api.Subscription
import io.smallrye.mutiny.Multi
import org.eclipse.microprofile.graphql.GraphQLApi
import org.eclipse.microprofile.graphql.Query

class TestError(errorMessage: String): RuntimeException(errorMessage)

@GraphQLApi
class TestGqlController {

    @Query
    fun testQuery(): String {
        return "Hi from Quarkus!"
    }

    @Subscription
    fun testSubscription(): Multi<String> {

        return Multi.createFrom().failure(TestError("Something went wrong."))
    }
}