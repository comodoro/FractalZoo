package com.draabek.fractal;


import android.Manifest;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.draabek.fractal.activity.MainActivity;
import com.draabek.fractal.fractal.FractalRegistry;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.IntBuffer;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.action.GeneralLocation;
import androidx.test.espresso.action.GeneralSwipeAction;
import androidx.test.espresso.action.Press;
import androidx.test.espresso.action.Swipe;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.rule.GrantPermissionRule;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;
//FIXME
@Ignore
@LargeTest
@RunWith(AndroidJUnit4.class)
public class GLGesturesTest {

    @Rule
    public GrantPermissionRule permissionRule = GrantPermissionRule.grant(Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    private void waitForRender() {
        for (int timeout = 0; timeout < 10;timeout++) {
            try {
                onView(allOf(withId(R.id.indeterminateBar),
                        withContentDescription("Progress Bar"))).check(matches(isDisplayed()));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (NoMatchingViewException e) {
                return;
            }
        }
        throw new RuntimeException("Timeout");
    }
    
    @Test
    public void gLGesturesTest() throws IOException {
        ViewInteraction actionMenuItemView = onView(
                allOf(withId(R.id.fractalList), withContentDescription("Fractal List"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        0),
                                0),
                        isDisplayed()));
        actionMenuItemView.perform(click());

        DataInteraction relativeLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(0);
        relativeLayout.perform(click());
        relativeLayout = onData(anything())
                .inAdapterView(allOf(withId(android.R.id.list),
                        childAtPosition(
                                withId(android.R.id.content),
                                0)))
                .atPosition(0);
        relativeLayout.perform(click());
        waitForRender();
        int[] buffer1 = saveBuffer();

        float centerY = FractalRegistry.getInstance().getCurrent().getParameters().get("centerY");
        ViewInteraction fractalView = onView(
                allOf(withId(R.id.fractalGlView), isDisplayed()));

        fractalView.perform(swipeDown());
        waitForRender();
        float centerY2 = FractalRegistry.getInstance().getCurrent().getParameters().get("centerY");
        int[] buffer2 = saveBuffer();

        Assert.assertEquals(buffer1.length, buffer2.length);
        double diff = cumulativeDifference(buffer1, buffer2);
        Assert.assertTrue(diff > 1);
        fractalView = onView(
                allOf(withId(R.id.fractalGlView), isDisplayed()));
        fractalView.perform(swipeUp());
        waitForRender();
        float newCenterY = FractalRegistry.getInstance().getCurrent().getParameters().get("centerY");
        Assert.assertTrue(Math.abs(centerY - centerY2) > Math.abs(centerY - newCenterY));
        int[] buffer3 = saveBuffer();
        Assert.assertEquals(buffer1.length, buffer3.length);
//        TODO Increase precision to be actually relevant
//        This just asserts that Original difference is bigger than current difference
        double diff2 = cumulativeDifference(buffer1, buffer3);
       // Assert.assertEquals(0, diff2, diff);
    }


    private int[] saveBuffer() throws IOException {
        ViewInteraction actionMenuItemView2 = onView(
                allOf(withId(R.id.save), withContentDescription("Save as JPG"),
                        childAtPosition(
                                childAtPosition(
                                        withId(R.id.toolbar),
                                        0),
                                1),
                        isDisplayed()));
        actionMenuItemView2.perform(click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.bitmap_filename),
                        childAtPosition(
                                allOf(withId(R.id.save_bitmap_radio_group),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        String savedFile = (File.createTempFile("Mandelbrot-test-",".jpg").getAbsolutePath());
        appCompatEditText.perform(replaceText(savedFile),
                closeSoftKeyboard());

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

        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.save_bitmap_ok_button), withText("OK"),
                        childAtPosition(
                                childAtPosition(
                                        withId(android.R.id.content),
                                        0),
                                1),
                        isDisplayed()));
        appCompatButton.perform(click());
        Bitmap saved = BitmapFactory.decodeFile(savedFile);
        saved = Bitmap.createScaledBitmap(saved, 8, 8, false);
        IntBuffer dst = IntBuffer.allocate(saved.getWidth() * saved.getHeight());
        saved.copyPixelsToBuffer(dst);
        return dst.array();
    }

    private double cumulativeDifference(int[] buffer1, int[] buffer2) {
        double cumdiff = 0;
        for (int i = 1;i < buffer1.length;i++) {
            cumdiff += Math.abs((double) (buffer1[i] & 0xFF) / 255 - (double) (buffer1[i] & 0xFF) / 255);
            cumdiff += Math.abs((double) ((buffer1[i] & 0xFF00) >> 8) / 255 - (double) ((buffer2[i] & 0xFF00) >> 8) / 255);
            cumdiff += Math.abs((double) ((buffer1[i] & 0xFF0000) >> 16) / 255 - (double) ((buffer2[i] & 0xFF0000) >> 16) / 255);
            cumdiff += Math.abs((double) ((buffer1[i] & 0xFF000000) >> 24) / 255 - (double) ((buffer2[i] & 0xFF000000) >> 24) / 255);
        }
        return cumdiff;
    }

    public static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.BOTTOM_CENTER,
                GeneralLocation.CENTER_RIGHT, Press.PINPOINT);
    }

    public static ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER_RIGHT,
                GeneralLocation.BOTTOM_CENTER, Press.PINPOINT);
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
