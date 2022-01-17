package com.example.surgeryapptest.ui.activity


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import com.example.surgeryapptest.R
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(LoginActivity::class.java)

    @Test
    fun loginActivityTest() {
        val appCompatEditText = onView(
            allOf(
                withId(R.id.username_login_et),
                childAtPosition(
                    allOf(
                        withId(R.id.loginView),
                        childAtPosition(
                            allOf(
                                withId(R.id.loginMainView),
                                childAtPosition(
                                    allOf(
                                        withId(android.R.id.content),
                                        childAtPosition(
                                            withId(R.id.action_bar_root),
                                            1
                                        )
                                    ),
                                    0
                                )
                            ),
                            2
                        )
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        appCompatEditText.perform(replaceText("Prasanth"), closeSoftKeyboard())

        val appCompatEditText2 = onView(
            allOf(
                withId(R.id.password_login_et),
                childAtPosition(
                    allOf(
                        withId(R.id.loginView),
                        childAtPosition(
                            allOf(
                                withId(R.id.loginMainView),
                                childAtPosition(
                                    allOf(
                                        withId(android.R.id.content),
                                        childAtPosition(
                                            withId(R.id.action_bar_root),
                                            1
                                        )
                                    ),
                                    0
                                )
                            ),
                            2
                        )
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        appCompatEditText2.perform(replaceText("Prasanth"), closeSoftKeyboard())

        val materialButton = onView(
            allOf(
                withId(R.id.login_btn), withText("LOGIN"),
                childAtPosition(
                    allOf(
                        withId(R.id.loginView),
                        childAtPosition(
                            allOf(
                                withId(R.id.loginMainView),
                                childAtPosition(
                                    allOf(
                                        withId(android.R.id.content),
                                        childAtPosition(
                                            withId(R.id.action_bar_root),
                                            1
                                        )
                                    ),
                                    0
                                )
                            ),
                            2
                        )
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
