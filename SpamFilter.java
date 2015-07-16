import java.util.*;
import java.io.*;

class SpamFilter
{
	HashMap<String,Integer>  hamWordCount;
	HashMap<String,Integer> spamWordCount;
	Scanner scan;
	FileInputStream fin;
	int total;
	double pSpam,pHam;
	
	void train() throws IOException{
		
		fin =  new FileInputStream("train.txt");
		scan = new Scanner(fin);
	
		spamWordCount = new HashMap<String,Integer>();
		hamWordCount = new HashMap<String,Integer>();
		
		int hamCount = 0;
		int spamCount = 0;
		
		int count = 0;
		while(scan.hasNext())
		{
			StringTokenizer token = new StringTokenizer(scan.nextLine());
			
			token.nextToken();
			String type = token.nextToken();
			
			if(type.equals("ham"))
				hamCount++;
			else
				spamCount++;
				
			while(token.hasMoreTokens())
			{
				String word = token.nextToken();
				
				if(type.equals("spam"))
				{
					int tcount = Integer.parseInt(token.nextToken());
					
					if(spamWordCount.containsKey(word))
					spamWordCount.put(word,spamWordCount.get(word)+tcount);
					else
					spamWordCount.put(word,tcount);
				}
				else
				{
					int tcount = Integer.parseInt(token.nextToken());
					
					if(hamWordCount.containsKey(word))
					hamWordCount.put(word,hamWordCount.get(word)+tcount);
					else
					hamWordCount.put(word,tcount);
				}
			}
			count++;
		}
		
		//System.out.println(hamWordCount.get("energy"));
		total = spamCount + hamCount;
	    pSpam = (1.0*spamCount)/total;
		pHam = (1.0*hamCount)/total;
	}
	
	double probability(StringTokenizer token,int flag)//flag 1 represents the probability of of the mail from spam else ham
	{
		
		double result = 1.0;
		
		while(token.hasMoreTokens())
		{
			String word = token.nextToken();
			
			int sCount = getCount(word,1);
			int hCount = getCount(word,0);
			
			int count = Integer.parseInt(token.nextToken());
			
			
			double tt = sCount + hCount;
			
			double forSpam = sCount/tt;
			double forHam = hCount/tt;
			
			forSpam = Math.pow(forSpam,count);
			forHam = Math.pow(forHam,count);
			//System.out.println(forHam);
			
			if(flag==1)
				result = result * forSpam;
			else if(flag==0&&hCount!=0)
				result = result * forHam;
		}
		
		//System.out.println(result);
		return result;
	}
	
	int getCount(String word,int flag)
	{
		if(flag==0)
		{
			if(hamWordCount.containsKey(word))
				return hamWordCount.get(word);
			else
				return 0;
		}
		else
		{
			if(spamWordCount.containsKey(word))
				return spamWordCount.get(word);
			else
				return 0;
		}
		
	}
	
	int test(StringTokenizer mail,String s)
	{

		double ham  = probability(mail,0);
		
		StringTokenizer token = new StringTokenizer(s);
		token.nextToken();
		token.nextToken();
		
		double spam = probability(token,1);
		
		double tP = spam*pSpam + ham*pHam;

		
		//System.out.println(spam);
		
		if(spam==0.0&&ham==0.0)return 0;//ham
		else
		{
			double tHam = (ham*pHam*1.0)/tP;
			double tSpam = (spam*pSpam*1.0)/tP;
			
			//System.out.println(tHam + " " + tSpam);
			
			if(tSpam>tHam)
				return 1;
			else
				return 0;
		}
	}
	
	public static void main(String args[]) throws IOException
	{
		SpamFilter filter = new SpamFilter();
		filter.train();
		
		//testing accuracy over the Test File
		
		FileInputStream fin = new FileInputStream("test.txt");
		Scanner scan = new Scanner(fin);
		
		int total = 0;
		int correctPredictions = 0;
		
		while(scan.hasNext())
		{
			String mail = scan.nextLine();
			
			StringTokenizer token = new StringTokenizer(mail);
			token.nextToken();
			String ans = token.nextToken();	
			int result = filter.test(token,mail);
				
			if(result==1 && ans.equals("spam"))
				correctPredictions++;
			else if(result==0 &&ans.equals("ham"))
				correctPredictions++;
				
			total++;
		}
		

		double accuracy = ((correctPredictions*1.0)/total)*100.0;
		System.out.println(accuracy);
		
	}
}