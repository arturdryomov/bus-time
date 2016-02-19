package ru.ming13.bustime.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;

public final class Files
{
    private Files() {
    }

    public static void copy(@NonNull File sourceFile, @NonNull File destinationFile) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(sourceFile));
            BufferedSink destination = Okio.buffer(Okio.sink(destinationFile));

            destination.writeAll(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void copy(@NonNull InputStream sourceStream, @NonNull File destinationFile) {
        try {
            BufferedSource source = Okio.buffer(Okio.source(sourceStream));
            BufferedSink destination = Okio.buffer(Okio.sink(destinationFile));

            destination.writeAll(source);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
