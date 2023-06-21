package com.dicoding.storyappsubmission.data

import com.dicoding.storyappsubmission.remote.response.login.Login
import com.dicoding.storyappsubmission.remote.response.login.LoginResponse
import com.dicoding.storyappsubmission.remote.response.story.getstory.ListStory
import java.util.Random
import java.util.UUID

object Dummy {
    fun generateDummyLogin(): LoginResponse {
        return LoginResponse(
            error = false,
            message = "success",
            login = Login(
                userId = "user-${UUID.randomUUID()}",
                name = "test",
                token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VySWQiOiJ1c2VyLXN2Y3hLc1RFN1hOVklXRWIiLCJpYXQiOjE2ODczNjMwNjZ9.SrWGCV-QBuQQ5Q6ZDgIWlWaE2uo3uk2YZqg6tGCiAGo"
            )
        )
    }

    fun generateDummyEntity(): List<ListStory> {
        val listStory = ArrayList<ListStory>()
        for (i in 0..10) {
            val story = ListStory(
                id = "id_${UUID.randomUUID()}",
                name = "name ${UUID.randomUUID()}",
                description = "description ${UUID.randomUUID()}",
                photoUrl = "https://picsum.photos/200?random=$i",
                createdAt = "2022-04-23T05:39:44.781Z",
                lat = Random().nextDouble() * 100,
                lon = Random().nextDouble() * 100
            )
            listStory.add(story)
        }
        return listStory
    }
}