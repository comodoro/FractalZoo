package com.draabek.fractal.activity;


import android.app.Activity;
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

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ClickToolbarTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void clickToolbarTest() {
        Intents.init();
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.fractalList), withContentDescription("Fractal List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        0),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        intended(hasComponent(FractalListActivity.class.getName()));

        DataInteraction relativeLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(0);
        relativeLayout.perform(click());

        DataInteraction relativeLayout2 = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(0);
        relativeLayout2.perform(click());

        intended(hasComponent(MainActivity.class.getName()));

        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.save), withContentDescription("Save as JPG"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        0),
                                1),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        intended(hasComponent(SaveBitmapActivity.class.getName()));

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.bitmap_filename),
                        childAtPosition(
                                allOf(withId(R.id.save_bitmap_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText.perform(replaceText("/storage/sdcard/Pictures/Mandelbrot1532776280010.jpg"), closeSoftKeyboard());

        ViewInteraction appCompatRadioButton = onView(
                allOf(withId(R.id.bitmap_filename_radio), withText("Save as file"),
                        childAtPosition(
                                allOf(withId(R.id.save_bitmap_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                0),
                        isDisplayed()));
        appCompatRadioButton.perform(click());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.bitmap_filename), withText("/storage/sdcard/Pictures/Mandelbrot1532776280010.jpg"),
                        childAtPosition(
                                allOf(withId(R.id.save_bitmap_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText2.perform(click());

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.save_bitmap_ok_button), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());

        ViewInteraction actionMenuItemView3 = onView(
                allOf(withId(R.id.parameters), withContentDescription("Fractal parameters"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        0),
                                2),
                        isDisplayed()));
        actionMenuItemView3.perform(click());

        intended(hasComponent(FractalParametersActivity.class.getName()));

        ViewInteraction button = onView(
                allOf(withText("OK"),
                        childAtPosition(
                                allOf(withId(R.id.layout_parameters),
                                        childAtPosition(
                                                withId(android.R.id.content),
                                                0)),
                                9),
                        isDisplayed()));
        button.perform(click());

        ViewInteraction actionMenuItemView4 = onView(
                allOf(withId(R.id.options), withContentDescription("Options"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        0),
                                3),
                        isDisplayed()));
        actionMenuItemView4.perform(click());

        intended(hasComponent(FractalPreferenceActivity.class.getName()));

        pressBack();

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
