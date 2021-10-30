import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

public class GenesLocalization {

	public static void main(String[] args) throws FileNotFoundException
	{
		File file;
		Scanner fileScanner;
		
		// read in training set
		file = new File("mainData.csv");
		fileScanner = new Scanner(file);
		String[][] trainingSet = new String[4347][];
		fileToArray(fileScanner, trainingSet);
		fileScanner.close();
		
		//read in data file to predict
		file = new File("predicting.csv");
		fileScanner = new Scanner(file);
		String[][] predictingSet = new String[1930][];
		fileToArray(fileScanner, predictingSet);
		fileScanner.close();
		
		//reads in the keys and puts them in a HashMap
		file = new File("keys.txt");
		fileScanner = new Scanner(file);
		HashMap<String, String> map = fileToHashmap(fileScanner);
		fileScanner.close();
		
		for(int x = 1; x < predictingSet.length; x++)
		{
			predictingSet[x][8] = getKNN(predictingSet[x], trainingSet);
		}
		
		Double ac = accuracy(predictingSet, map); //This brings back the accuracy
		System.out.printf("Accuracy: %.3f%%", ac * 100 );
	}
	
	public static void fileToArray(Scanner fileScanner, String[][] array)
	{
		int i = 0;
		
		while(fileScanner.hasNext())
		{
			array[i] = fileScanner.nextLine().split(",");
			i++;
		}
	}
	public static HashMap<String, String> fileToHashmap(Scanner fileScanner)
	{	
		HashMap<String, String> map = new HashMap<>();
		while(fileScanner.hasNext())
		{
			String array[] = fileScanner.nextLine().split(",");
			map.put(array[0], array[1]);
		}
		return map;
	}

	public static String getKNN(String[] predictRow, String[][] trainingSet)
	{
		int highestWeight = 0;
		LinkedList<String> NN = new LinkedList<String>();
		for(int x = 1; x < trainingSet.length; x++)
		{
			int currentWeight = 0;
			
			for (int j = 0; j < 7; j++)
            {
                if(predictRow[j].equals(trainingSet[x][j]))
                {
                	currentWeight++;
                }       
            }
			//Clears list because it found a higher weight
			if(currentWeight > highestWeight)
			{
				NN.clear();
				highestWeight = currentWeight;
				NN.add(trainingSet[x][8]);
			}
			//if same weight we just add it to the linked list
			else if(currentWeight == highestWeight)
			{
				NN.add(trainingSet[x][8]);
			}
		}
		return pickNN(NN);
	}
	//Here we pick the ones the ones with the most occurrences that have the highest weight
	public static String pickNN(LinkedList<String> nn)
	{
		HashMap<String, Integer> count = new HashMap<>();
		for(int x = 0; x < nn.size(); x++)
		{
			if(!count.containsKey(nn.get(x)))
			{
				count.put(nn.get(x), 1);
			}
			else
			{
				count.put(nn.get(x), count.get(nn.get(x)) + 1);
			}
		}
		int max = 0;
		String a = "?";
		for (Map.Entry<String, Integer> e : count.entrySet()) 
		{
		    if(e.getValue() > max)
		    {
		    	max = e.getValue();
		    	a = e.getKey();
		    }
		}
		return a;
	}
	public static double accuracy(String predictive[][], HashMap<String, String> map)
    {
		int correct = 0;
        for (int i = 1; i < predictive.length; i++)
        {
        	String key = predictive[i][0].substring(1, predictive[i][0].length()-1);
        	String prediction = predictive[i][8].substring(1, predictive[i][8].length()-1);
        	String valueInMap = map.get(key);
        	
            if(prediction.equalsIgnoreCase(valueInMap))
            {
            	correct++;
            }
        }
        Double a = correct/1929.0;
        return a;
    }
}
