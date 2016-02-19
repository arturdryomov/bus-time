package ru.ming13.bustime.database.sql;

public interface QueryComponents
{
	String getTables();

	String[] getProjection();

	String getSelection();

	String[] getSelectionArguments();

	String getSortOrder();
}
