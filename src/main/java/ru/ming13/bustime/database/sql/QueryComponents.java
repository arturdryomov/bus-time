package ru.ming13.bustime.database.sql;

public interface QueryComponents
{
	public String getTables();

	public String[] getProjection();

	public String getSelection();

	public String[] getSelectionArguments();

	public String getSortOrder();
}
