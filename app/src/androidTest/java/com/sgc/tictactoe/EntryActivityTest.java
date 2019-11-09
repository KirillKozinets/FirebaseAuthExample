package com.sgc.tictactoe;

import android.app.Activity;
import android.view.View;

import com.sgc.tictactoe.ui.EntryActivity;
import com.sgc.tictactoe.util.BaseActivityIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.NoMatchingViewException;
import androidx.test.espresso.ViewInteraction;
import androidx.test.espresso.contrib.DrawerActions;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class EntryActivityTest {

    private IdlingResource mActivityResource;
    private IdlingRegistry idlingRegistry = IdlingRegistry.getInstance();

    @Rule
    public ActivityTestRule<EntryActivity> mActivityTestRule =
            new ActivityTestRule<>(EntryActivity.class);

    @Before
    public void setUp() {
        if (mActivityResource != null) {
            idlingRegistry.unregister(mActivityResource);
        }

        mActivityResource = new BaseActivityIdlingResource(mActivityTestRule.getActivity());
        idlingRegistry.register(mActivityResource);
    }


    @After
    public void tearDown() {
        if (mActivityResource != null) {
            idlingRegistry.unregister(mActivityResource);
        }
    }

    @Test
    public void failedSignInTest() {
        String email = "randomNoExitsEmail@Random.Random";
        String password = "123456";
        failedSignIn(email, password, "неверный email или пароль");
    }

    @Test
    public void incorrectPasswordFailedSignInTest() {
        String email = "hello@mail.ru";
        String password = "12334124";
        failedSignIn(email, password, "неверный пароль");
    }

    @Test
    public void signInTest() {
        String email = "hellow@mail.ru";
        String password = "qweqwe";
        signIn(email, password);
        onView(withId(R.id.errorInfo))
                .check(doesNotExist());
        signOutIfPossible();
    }

    @Test
    public void emptyFieldsTest(){
        signIn("", "");
        onView(withId(R.id.fieldEmail)).check(matches(hasErrorText("Зполните поле.")));
        onView(withId(R.id.fieldPassword)).check(matches(hasErrorText("Зполните поле.")));
    }


    private void signIn(String email, String password){
        signOutIfPossible();
        enterEmail(email);
        enterPassword(password);
        signIn();
    }

    private void failedSignIn(String email, String password, String errorMessage) {
        signIn(email,password);
        checkError(errorMessage);
    }

    private void signOutIfPossible() {
        try {
            onView(withId(R.id.signOutButton)).perform(click());
        } catch (NoMatchingViewException e) {

        }
    }

    private void signIn() {
        onView(withText(R.string.sign_in)).perform(click());
    }

    private void enterEmail(String email) {
        ViewInteraction emailField = onView(withId(R.id.fieldEmail));
        emailField.perform(replaceText(email));
        }

    private void enterPassword(String password) {
        ViewInteraction emailField = onView(withId(R.id.fieldPassword));
        emailField.perform(replaceText(password));
    }

    private void checkError(String errorMessage) {
        Espresso.pressBack();
        onView(withId(R.id.errorInfo))
                .check(matches(withText(errorMessage)));
    }
}
