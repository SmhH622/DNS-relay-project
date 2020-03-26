package dnsrelay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class Check {
	public static Map<String, String> ipTable = new HashMap<String, String>();
	public static long fileSize;
	public static void readData(String path) throws IOException
	{
		String line = "";
		File f = new File(path);
		fileSize = f.length();
		if(!f.exists())
		{
			System.out.println("File doesn't exist!");
		}
		else
		{
			FileInputStream fis = new FileInputStream(f);
			InputStreamReader isr = new InputStreamReader(fis);
			BufferedReader br = new BufferedReader(isr);
			
			while((line=br.readLine())!=null)
			{
				String[] ip = line.split(" ");
				String ipAddress = ip[0];
				String ipDomainName = ip[1];
				ipTable.put(ipDomainName,ipAddress);                               
			}
			fis.close();
			isr.close();
			br.close();
			
		}
	}
}