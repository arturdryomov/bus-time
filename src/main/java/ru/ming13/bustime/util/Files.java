package ru.ming13.bustime.util;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

public final class Files
{
	private Files() {
	}

	public static void copy(@NonNull File sourceFile, @NonNull File destinationFile) {
		try {
			Files.copy(Okio.source(sourceFile), Okio.sink(destinationFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void copy(@NonNull InputStream sourceStream, @NonNull File destinationFile) {
		try {
			Files.copy(Okio.source(sourceStream), Okio.sink(destinationFile));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	private static void copy(Source source, Sink destination) {
		try {
			BufferedSource bufferedSource = Okio.buffer(source);
			BufferedSink bufferedDestination = Okio.buffer(destination);

			bufferedDestination.writeAll(bufferedSource);
			bufferedDestination.flush();

			bufferedSource.close();
			bufferedDestination.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
