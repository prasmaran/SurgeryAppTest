package com.example.surgeryapptest.utils.pubsub_state

sealed class PubSub {
    object Unauthorized: PubSub() //logout user when receive http code 401
    object EmptyState: PubSub()
    object DbTransaction: PubSub()
}

