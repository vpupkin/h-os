package org.lzy.fwswaper.swaper;

import java.util.logging.Logger;

public final class SwaperFactory {

	public static String SwaperTypePropertyName = "org.lzy.fwswaper.swaper.type";

	private static final Logger log = Logger.getLogger(SwaperFactory.class
			.getName());

	// Can not instantiation. Factory pattern.
	private SwaperFactory() {
		return;
	}

	public static ISwaper createSwaper() {
		ISwaper swaper = null;

		String swaperType = System.getProperty(SwaperTypePropertyName);

		if ("DirectlySwaper".equals(swaperType))
			swaper = new DirectlySwaper();
		else if ("HtmlParseSwaper".equals(swaperType))
			swaper = new HtmlParseSwaper();
		else // Default swaper.
		{
			log
					.warning("Invalid swaper type property setting. Using 'DirectlySwaper' default swaper.");

			swaper = new DirectlySwaper();
		}

		return swaper;
	}
}
