package ru.ming13.bustime.util;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

public final class Strings
{
    private Strings() {
    }

    @NonNull
    public static final String EMPTY = "";

    @Nullable
    public static String capitalize(@Nullable String string) {
        if (Strings.isBlank(string)) {
            return string;
        }

        char firstCharacter = string.charAt(0);

        if (Character.isTitleCase(firstCharacter)) {
            return string;
        }

        return new StringBuilder(string.length())
            .append(Character.toTitleCase(firstCharacter))
            .append(string.substring(1))
            .toString();
    }

    public static boolean isBlank(@Nullable String string) {
        if (TextUtils.isEmpty(string)) {
            return true;
        }

        for (int characterPosition = 0, charactersCount = string.length(); characterPosition < charactersCount; characterPosition++) {
            if (!Character.isWhitespace(string.charAt(characterPosition))) {
                return false;
            }
        }

        return true;
    }

    @NonNull
    public static String join(@NonNull String[] strings, @NonNull String delimiter) {
        return TextUtils.join(delimiter, strings);
    }

    @NonNull
    public static String join(@NonNull Iterable<String> strings, @NonNull String delimiter) {
        return TextUtils.join(delimiter, strings);
    }
}
