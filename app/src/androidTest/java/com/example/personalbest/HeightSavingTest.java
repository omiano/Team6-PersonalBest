package com.example.personalbest;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import com.google.android.gms.auth.api.signin.GoogleSignIn;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiObject;
import androidx.test.uiautomator.UiObjectNotFoundException;
import androidx.test.uiautomator.UiSelector;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static org.junit.Assert.*;


import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.scrollTo;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class HeightSavingTest {

    Intent intent;
    SharedPreferences exercisePreferences;
    SharedPreferences.Editor editor;
    private UiDevice mDevice;
    Context targetContext;


    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<>(
            MainActivity.class,
            true,
            false); // Activity is not launched immediately

    @Before
    public void setUp() {
        targetContext = getInstrumentation().getTargetContext();
        exercisePreferences = targetContext.getSharedPreferences("exercise", Context.MODE_PRIVATE);;
        editor = exercisePreferences.edit();
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    @Test
    public void mainActivityTest2() {

        editor.remove("height_feet");
        editor.remove("height_inches");
        editor.commit();

        mActivityRule.launchActivity(new Intent());

        assertEquals(exercisePreferences.contains("height_feet"), false);

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(targetContext))) {
            UiObject appItem = mDevice.findObject(new UiSelector()
                    .resourceId("com.google.android.gms:id/account_profile_picture"));

            try {
                appItem.click();
            } catch (UiObjectNotFoundException e) {
                e.printStackTrace();
            }
        }

        try {
            UiObject acceptButton = mDevice.findObject(new UiSelector().resourceId("android:id/button1"));
            acceptButton.click();
        } catch (UiObjectNotFoundException e) {
            e.printStackTrace();
        }

        exercisePreferences.getAll();

        assertEquals(exercisePreferences.contains("height_feet"), true);
        assertEquals(exercisePreferences.getInt("height_feet", -1), 1);
        assertEquals(exercisePreferences.getInt("height_inches", -1), 0);
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
