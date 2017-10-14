package com.draabek.fractal;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;


@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void mainActivityTest() {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.fractalList), withContentDescription("Fractal List"), isDisplayed()));
        actionMenuItemView.perform(click());

        ViewInteraction textView = onView(
                allOf(withId(android.R.id.text1), withText("Julia"),
                        childAtPosition(
                                allOf(withId(android.R.id.list),
                                        withParent(withId(android.R.id.content))),
                                6),
                        isDisplayed()));
        textView.perform(click());

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.save), withContentDescription("Save as JPG"), isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.bitmap_filename),
                        withParent(withId(R.id.save_bitmap_radio_group)),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("/storage/emulated/0/Pictures/Julia1507985989003.jpg"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.bitmap_filename), withText("/storage/emulated/0/Pictures/Julia1507985989003.jpg"),
                        withParent(withId(R.id.save_bitmap_radio_group)),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.bitmap_set_as_background_radio), withText("Set as Home screen background"),
                        withParent(withId(R.id.save_bitmap_radio_group)),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.save_bitmap_ok_button), withText("OK"), isDisplayed()));
        appCompatButton.perform(click());

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
