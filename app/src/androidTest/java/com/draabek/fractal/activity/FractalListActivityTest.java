package com.draabek.fractal.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.draabek.fractal.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class FractalListActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void fractalListActivityTest() {
        Intents.init();
        Intent intent = new Intent(FractalZooApplication.getContext(),FractalListActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        getInstrumentation().startActivitySync(intent);

        DataInteraction relativeLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(0);
        relativeLayout.perform(click());

        pressBack();

        DataInteraction relativeLayout2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(1);
        relativeLayout2.perform(click());

        pressBack();

        DataInteraction relativeLayout3 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(2);
        relativeLayout3.perform(click());

        DataInteraction relativeLayout4 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(0);
        relativeLayout4.perform(click());

        intended(hasComponent(MainActivity.class.getName()));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
