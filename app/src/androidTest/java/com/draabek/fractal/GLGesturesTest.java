package com.draabek.fractal;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.test.espresso.DataInteraction;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.GeneralSwipeAction;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Swipe;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.nio.IntBuffer;

import static android.os.Environment.DIRECTORY_PICTURES;
import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
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
public class GLGesturesTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void gLGesturesTest() throws  InterruptedException {
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
        Thread.sleep(10000);
        int[] buffer1 = saveBuffer();

        ViewInteraction fractalView = onView(
                allOf(withId(R.id.fractalGlView), isDisplayed()));
        fractalView.perform(swipeDown());
        Thread.sleep(10000);
        int[] buffer2 = saveBuffer();

        Assert.assertEquals(buffer1.length, buffer2.length);
        double diff = cumulativeDifference(int2rgb(buffer1), int2rgb(buffer2));
        Assert.assertEquals(true, diff > 1.0);
        fractalView = onView(
                allOf(withId(R.id.fractalGlView), isDisplayed()));
        fractalView.perform(swipeUp());
        Thread.sleep(10000);
        int[] buffer3 = saveBuffer();

        Assert.assertEquals(buffer1.length, buffer3.length);
        double diff2 = cumulativeDifference(int2rgb(buffer1), int2rgb(buffer3));
        Assert.assertEquals(true, diff2 < 1.0);
    }

    private int[] saveBuffer() {
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
        String savedFile = (new File(getExternalStoragePublicDirectory(DIRECTORY_PICTURES)
                .getAbsolutePath(),
                "Mandelbrot-test-" + System.currentTimeMillis() + ".jpg")).getAbsolutePath();
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
        IntBuffer dst = IntBuffer.allocate(saved.getWidth() * saved.getHeight());
        saved.copyPixelsToBuffer(dst);
        return dst.array();
    }

    private double cumulativeDifference(double[][] buffer1, double[][] buffer2) {
        double cumdiff = 0;
        for (int i = 1;i < buffer1.length;i++) {
            cumdiff += Math.abs(buffer1[i][0] - buffer2[i][0]);
            cumdiff += Math.abs(buffer1[i][1] - buffer2[i][1]);
            cumdiff += Math.abs(buffer1[i][2] - buffer2[i][2]);
            cumdiff += Math.abs(buffer1[i][3] - buffer2[i][3]);
        }
        return cumdiff;
    }

    private double [][] int2rgb(int[] intColors) {
        double[][] rgb = new double[intColors.length][4];
        for (int i = 0;i < intColors.length;i++) {
            rgb[i][0] = (double)(intColors[0] & 0xFF)/255;
            rgb[i][1] = (double)((intColors[0] & 0xFF00) >> 8)/255;
            rgb[i][2] = (double)((intColors[0] & 0xFF0000) >> 16)/255;
            rgb[i][3] = (double)((intColors[0] & 0xFF000000) >> 24)/255;
        }
        return rgb;
    }
    public static ViewAction swipeUp() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.BOTTOM_CENTER,
                GeneralLocation.CENTER, Press.FINGER);
    }

    public static ViewAction swipeDown() {
        return new GeneralSwipeAction(Swipe.SLOW, GeneralLocation.CENTER,
                GeneralLocation.BOTTOM_CENTER, Press.FINGER);
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
