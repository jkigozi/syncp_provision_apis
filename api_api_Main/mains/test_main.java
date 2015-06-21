package mains;


import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
import com.google.common.collect.*;



public class test_main {
	private static HashMap<String, Integer> mp = new HashMap<String, Integer>();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		thoseMaps("fil");
		System.out.println( properCase("tHeSE CArS aRE COOL!"));

	}
		

	public static HashMap<String, String>  thoseMaps(String file) {
		
		Multimap<String,String> myMM = ArrayListMultimap.create();
		HashMap<String,String> hm = new HashMap<String,String>();
		HashMap<String,String> hm2 = new HashMap<String,String>();
		
		
		myMM.put("Car", "Tesla");
		myMM.put("Truck", "Mack");
		myMM.put("Van", "Toyota");
		myMM.put("Bus", "VanFoo");
		
		hm.put("firstaa_lastaa@apidev.com","Group_Main");
		hm.put("firstbb_lastbb@apidev.com","Group_Real");
		
		hm2.put("b7afa2b0-f50a-e511-80c4-00259085d089", "firstaa_lastaa@apidev.com");
		hm2.put("b8bfa2b0-f50a-e511-80c4-00259085d089", "firstbb_lastbb@apidev.com");
		
		/*for (Map.Entry<String, String> entry: hm.entrySet())
		{
			for (Map.Entry<String, String> entry2: hm2.entrySet())
			{
				if(entry2.getValue() == entry.getKey())
					System.out.println(String.format("\tKey: %s \n\t\t Value: %s", entry.getKey(),entry2.getKey()));
			}
		}*/
		
		for (Map.Entry<String,String> entryV: myMM.entries())
		{
			System.out.println(String.format("Type: %s Make: %s",entryV.getKey(), entryV.getValue()));
		}
		
		return hm;
		}
	
	public static void checkMap(String[] str) {
		
	    for (String st : str) {
	        if (!mp.containsKey(st)) {
	            mp.put(st, 1);
	        }
	
	        else {
	            Integer ct = mp.get(st);
	            if(ct!=null)
	            {
	            ct++;
	            mp.put(st, ct);
	            }
	        }
	    }
	
	    for (Map.Entry<String, Integer> entry : mp.entrySet()) {
	        System.out.println(entry.getKey() + " ocurrs " + entry.getValue()+ " times");
	        System.out.println("Test GiT 1");
	    }
	}
	
	public static String properCase(String str)
	{
		Pattern pt = Pattern.compile("(^|\\W)([a-z])");
		Matcher m = pt.matcher(str.toLowerCase());
		StringBuffer sb = new StringBuffer(str.length());
		while (m.find())
		{
			m.appendReplacement(sb, m.group(1) + m.group(2).toUpperCase());
		}
		
		m.appendTail(sb);
		
		return sb.toString();
		
	}

}
