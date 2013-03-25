package ru.ming13.bustime.ui.util;


import org.apache.commons.lang3.StringUtils;


public final class NameParser
{
	private static final class Tokens
	{
		private Tokens() {
		}

		public static final String ROUTE_DESCRIPTION_BEGIN_SYMBOL = "(";
		public static final String ROUTE_DESCRIPTION_END_SYMBOL = ")";
	}

	private NameParser() {
	}

	public static String parseRouteNumber(String routeName) {
		return StringUtils.substringBefore(routeName, Tokens.ROUTE_DESCRIPTION_BEGIN_SYMBOL);
	}
}
