package com.sgc.tictactoe.util;

import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ConverterExceptionToErrorMessage {

    public static String errorAuthProcessing(Task task) {
        String message;

        try {
            throw task.getException();
        } catch (FirebaseAuthWeakPasswordException e) {
            message = "пароль должен состоять минимум из 6 симвалов";
        } catch (FirebaseAuthUserCollisionException e) {
            message = "данный email уже используется";
        } catch (Exception e) {
            message = "ошибка регистрации";
        }

        return message;
    }

    public static String errorEntryProcessing(Task task) {
        String message;

        try {
            throw task.getException();
        } catch (FirebaseAuthInvalidCredentialsException e) {
            message = "неверный пароль";
        } catch (FirebaseAuthUserCollisionException e) {
            message = "неверный email";
        } catch (FirebaseAuthInvalidUserException e) {
            message = "неверный email или пароль";
        } catch (FirebaseTooManyRequestsException e) {
            message = "из-за слишкой большой активности вход в приложение временно заблокирован";
        } catch (Exception e) {
            message = "ошибка входа";
        }

        return message;
    }

    public static String errorChangeEmailProcessing(Task task) {
        String message;

        try {
            throw task.getException();
        } catch (FirebaseAuthUserCollisionException e) {
            message = "данный email уже используется";
        } catch (FirebaseAuthInvalidCredentialsException e) {
            message = "неверный формат электронной почты";
        }  catch (Exception e) {
            message = "ошибка смены электронной почты";
        }

        return message;
    }

    public static String errorChangePasswordProcessing(Task task) {
        String message;

        try {
            throw task.getException();
        }catch (FirebaseAuthWeakPasswordException e) {
            message = "пароль должен состоять минимум из 6 симвалов";
        }  catch (Exception e) {
            message = "ошибка смены пароля";
        }

        return message;
    }
}
