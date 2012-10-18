package ru.ming13.bustime.ui.provider;


import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import ru.ming13.bustime.db.DbProvider;
import ru.ming13.bustime.db.model.Station;


public class StationsSearchProvider extends ContentProvider
{
	private static final String[] SUGGESTIONS_CURSOR_COLUMNS;

	static {
		SUGGESTIONS_CURSOR_COLUMNS = new String[] {
			BaseColumns._ID,
			SearchManager.SUGGEST_COLUMN_TEXT_1,
			SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA
		};
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		String searchQuery = selectionArgs[0];

		return buildSuggestionsCursor(searchQuery);
	}

	private Cursor buildSuggestionsCursor(String searchQuery) {
		MatrixCursor suggestionsCursor = new MatrixCursor(SUGGESTIONS_CURSOR_COLUMNS);

		int rowId = 0;
		for (Station station : DbProvider.getInstance().getStations().getStationsList(searchQuery)) {
			suggestionsCursor.addRow(buildSuggestionsCursorRow(rowId, station));

			rowId++;
		}

		return suggestionsCursor;
	}

	private Object[] buildSuggestionsCursorRow(int rowId, Station station) {
		return new Object[] {Integer.valueOf(rowId), station.getName(), Long.valueOf(station.getId())};
	}

	@Override
	public boolean onCreate() {
		return true;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues contentValues) {
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArguments) {
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArguments) {
		return 0;
	}
}
