package com.example.gf5.uiTests

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.gf5.activities.AdminPanelActivity
import org.junit.Rule
import org.junit.Test

class AdminPanelActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(AdminPanelActivity::class.java)

    @Test
    fun testRecyclerViewVisibility() {
        // Check if the RecyclerView is displayed
        Espresso.onView(ViewMatchers.withId(R.id.usersRecyclerView))
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}