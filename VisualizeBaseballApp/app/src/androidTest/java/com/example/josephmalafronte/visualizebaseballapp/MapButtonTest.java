package com.example.josephmalafronte.visualizebaseballapp;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
public class MapButtonTest {

    @Rule
    public ActivityTestRule<MainActivity> sActivityRule =
            new ActivityTestRule(MainActivity.class);


    @Test
    public void useAppContext() {
        // Context of the app under test.
        onView(withId(R.id.mapButton)).check(matches(withText("Map")));
    }

}