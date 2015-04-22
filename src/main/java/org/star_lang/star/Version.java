package org.star_lang.star;

import java.util.logging.Logger;

public class Version
{
	public static final String version = "1.01 ${env.BUILD_NUMBER}-${env.MERCURIAL_REVISION}";
	
	private static final Logger logger = Logger.getLogger(StarRules.class.getName());
	
	static {
	  logger.info("Star Compiler Version " + version);
	}
	
	public static void main(String[] args)
	{
	  System.out.println("Star Compiler version: " + version);
	  System.out.println("Starview Inc © 2013");
	}
}