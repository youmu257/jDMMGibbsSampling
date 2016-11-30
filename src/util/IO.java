package util;

/**
 * 習慣使用的讀寫檔函式
 * @author Guan-Lin Li
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class IO {
	/** 
	 * Create folder if not exits
	 * @param path : folder path
	 */
	public static void mkdir(String path)
	{
		if(!new File(path).exists())
			new File(path).mkdirs();
	}
	
	/**
	 * Open the file and read it by utf-8 encoding
	 * @param path : file path
	 */
	public static BufferedReader Reader(String path) throws IOException
	{
		return new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
	}
	
	/**
	 * Open the file and write it by utf-8 encoding
	 * @param path : file path
	 */
	public static BufferedWriter Writer(String path) throws IOException
	{
		return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path),"UTF-8"));
	}
}
