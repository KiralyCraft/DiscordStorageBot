package com.kiralycraft.dsb.utils;

public class UnicodeMeasurement
{
	/**
	 * Returns the actual length (in characters) of a String, supporting surrogate UTF-8 characters
	 * @param newContent
	 * @return
	 */
	public static int getActualLength(CharSequence newContent)
	{
		if (newContent.length() == 0)
		{
			return 0;
		}
		else
		{
			String toString = newContent.toString();
			int actualLength = 0;
			int i = 0;
			do
			{
				int ch = toString.codePointAt(i);
				actualLength++;
				i += Character.charCount(ch);
			} while (i < newContent.length()); //FIXED
			return actualLength;
		}
	}

}
