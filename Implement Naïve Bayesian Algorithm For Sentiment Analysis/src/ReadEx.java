package reader;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReadEx {
	
	/**
	 * this function read the data from excel file
	 * @param start_row: is the first row was found based on cross_fold validation that this function should not read
	 * @param last_row: is the last row was found based on cross_fold validation that this function should not read
	 * @return this function return probablity object which contains probablity values 
	 * @throws IOException
	 */

	public static Probablity read(int start_row, int last_row)throws IOException{
		
		Probablity probablity = new Probablity();
		
		try { 
			//read file from excel file
			File file = new File("C:/dataset-cs6735.xlsx");                                                     
			FileInputStream fis = new FileInputStream(file);
			XSSFWorkbook wb = new XSSFWorkbook(fis);
			XSSFSheet sh = wb.getSheet("label20131201_positive_10000");
			
            //making array list for collecting all words and tokens occur in Examples  
			ArrayList<String> vocab = new ArrayList<String>();
			
			//the initial vale for making attributes for each objects
			double positive_result = 0;
			double negative_result = 0;
			double nutral_result = 0;
			String positive_str = " ";
			String negative_str = " ";
			String nutral_str = " ";

            // start reading form file with ignoring the rows kept for testing set
			for(int z=1; z<=sh.getLastRowNum(); z++){
				if(z >= start_row && z<= last_row){
					continue;
				}
				else{
					String sentence = sh.getRow(z).getCell(2).getStringCellValue();
					int num = (int)sh.getRow(z).getCell(1).getNumericCellValue();
					//count the subset of examples for which target value is negative, positive or neutral
					if(num == 0)
						nutral_result = nutral_result + 1;
					else if(num == 1)
						positive_result = positive_result + 1;
					else
						negative_result = negative_result + 1;

					StringTokenizer st = new StringTokenizer(sentence);
					//the process used for adding all valid words to vocabulary
					while(st.hasMoreTokens()){
						String s = st.nextToken();
						for(int i=0; i<s.length(); i++){
							if(((int)s.charAt(i)>=65 && (int)s.charAt(i)<=90) || ((int)s.charAt(i)>=97 && (int)s.charAt(i)<=122 )|| (int)s.charAt(i)== 39 || (int)s.charAt(i)== 8217){
								if(i == s.length()-1){
									if(!s.equalsIgnoreCase("I")&&!s.equalsIgnoreCase("they")&&!s.equalsIgnoreCase("he")&&!s.equalsIgnoreCase("she")&&!s.equalsIgnoreCase("am")&&
											!s.equalsIgnoreCase("is")&&!s.equalsIgnoreCase("are")&&!s.equalsIgnoreCase("him")&&!s.equalsIgnoreCase("them")&&!s.equalsIgnoreCase("their")
											&&!s.equalsIgnoreCase("his")&&!s.equalsIgnoreCase("her")&&!s.equalsIgnoreCase("me")&&!s.equalsIgnoreCase("you")&&!s.equalsIgnoreCase("your")&&
											!s.equalsIgnoreCase("yours")&& !s.equalsIgnoreCase("we")&&!s.equalsIgnoreCase("it")&&!s.equalsIgnoreCase("my")&&!s.equalsIgnoreCase("our")
											&&!s.equalsIgnoreCase("us")&&!s.equalsIgnoreCase("and")&& !s.equalsIgnoreCase("this")&& !s.equalsIgnoreCase("that") && !s.equalsIgnoreCase("these")
											&&!s.equalsIgnoreCase("those")){
										if(!vocab.contains(s.toLowerCase())){
											vocab.add(s.toLowerCase());
										}
									}
									//the process used for concatenating all positive, negative and neutral words in different strings Textj
									if(num == 0)
										nutral_str = nutral_str +  s + " ";
									else if(num == 1)
										positive_str = positive_str +  s + " ";
									else
										negative_str = negative_str +  s + " ";
								}
							}
							else{
								break;
							}
						}
					}
				}
			}
			
			//variables for finding the probablity of each results p(vj)
			double positive_probablity = positive_result /(10000 - (last_row - start_row));
			double negative_probablity = negative_result /(10000 - (last_row - start_row));
			double nutral_probablity = nutral_result /(10000 - (last_row - start_row));
			
            //defining the value of attributes for probablity object
			probablity.vocab = vocab;
			probablity.generalPositive = positive_probablity;
			probablity.generalNegative = negative_probablity;
			probablity.generalNutarl = nutral_probablity; 

			// the process for finding the number of times that each word in negative, positive and neutral sentence is repeated  
			StringTokenizer st_nutral = new StringTokenizer(nutral_str);
			double total_nutral_words = st_nutral.countTokens();
			StringTokenizer st_positive = new StringTokenizer(positive_str);
			double total_positive_words = st_positive.countTokens();
			StringTokenizer st_negative = new StringTokenizer(negative_str);
			double total_negative_words = st_negative.countTokens();
			double correct_pro=0.035*(total_positive_words-total_negative_words);
			//three arrays were used for storing the repetition of each word
			double[] positivewords = new double [vocab.size()];
			double[] negativewords = new double[vocab.size()];
			double[] nuralwords = new double[vocab.size()];
			//for loop for counting the number of times each word occur in different classes
			for(int i=0; i<vocab.size(); i++){
				String word = vocab.get(i);
				StringTokenizer st_nutral1 = new StringTokenizer(nutral_str);
				StringTokenizer st_positive1 = new StringTokenizer(positive_str);
				StringTokenizer st_negative1 = new StringTokenizer(negative_str);
				while(st_nutral1.hasMoreTokens()){
					String token = st_nutral1.nextToken();
					if(token.equalsIgnoreCase(word)){
						nuralwords[i] = nuralwords[i]+1;
					}
				}
				while(st_positive1.hasMoreTokens()){
					String token = st_positive1.nextToken();
					if(token.equals(word)){
						positivewords[i] = positivewords[i]+1;
					}
				}
				while(st_negative1.hasMoreTokens()){
					String token = st_negative1.nextToken();
					if(token.equals(word)){
						negativewords[i] = negativewords[i]+1;
					}
				}
			}
			
			//giving probablity for each one calsses by the use of this formula 	P(wk/vj) <---- (n_k+1 )/(n+|Vocabulary|)
			double[] positive_pro = new double [vocab.size()];
			double[] negative_pro = new double[vocab.size()];
			double[] nural_pro = new double[vocab.size()];
			for(int i=0; i<vocab.size(); i++){
				positive_pro[i] = (positivewords[i]+1)/(total_positive_words + vocab.size());
				negative_pro[i] = (negativewords[i]+1)/(total_negative_words + vocab.size());
				nural_pro[i] = (nuralwords[i]+1)/(total_nutral_words + vocab.size());
			}
			probablity.positive_pro = positive_pro;
			probablity.negative_pro = negative_pro;
			probablity.nural_pro = nural_pro;
			probablity.generalCorrect = correct_pro;
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return probablity;
	}
	
    /**
     * this function get a sentence as input and delete all meaningless words
     * @param sentence
     * @return the purified sentence
     */
	public static String sentenceCleaner(String sentence){
		String result = "";
		StringTokenizer st = new StringTokenizer(sentence);
		while(st.hasMoreTokens()){
			String s = st.nextToken();
			for(int i=0; i<s.length(); i++){
				if(((int)s.charAt(i)>=65 && (int)s.charAt(i)<=90) || ((int)s.charAt(i)>=97 && (int)s.charAt(i)<=122 )|| (int)s.charAt(i)== 39 || (int)s.charAt(i)== 8217){
					if(i == s.length()-1){
						if(!s.equalsIgnoreCase("I")&&!s.equalsIgnoreCase("they")&&!s.equalsIgnoreCase("he")&&!s.equalsIgnoreCase("she")&&!s.equalsIgnoreCase("am")&&
								!s.equalsIgnoreCase("is")&&!s.equalsIgnoreCase("are")&&!s.equalsIgnoreCase("him")&&!s.equalsIgnoreCase("them")&&!s.equalsIgnoreCase("their")
								&&!s.equalsIgnoreCase("his")&&!s.equalsIgnoreCase("her")&&!s.equalsIgnoreCase("me")&&!s.equalsIgnoreCase("you")&&!s.equalsIgnoreCase("your")&&
								!s.equalsIgnoreCase("yours")&& !s.equalsIgnoreCase("we")&&!s.equalsIgnoreCase("it")&&!s.equalsIgnoreCase("my")&&!s.equalsIgnoreCase("our")
								&&!s.equalsIgnoreCase("us")&&!s.equalsIgnoreCase("and")&&!s.equalsIgnoreCase("this")&& !s.equalsIgnoreCase("that") && !s.equalsIgnoreCase("these")
								&&!s.equalsIgnoreCase("those")){
							result = result + " " +s;
						}			
					}
				}
				else{
					break;
				}
			}
		}
		return result;
	}

	/**
	 * this function get the results of read function and by the use of 
	 * @param pro get the results produced by training data(training part) to decide about the rest of data 
	 * @param start_row: is defined by the use of cross validation to read the rows of test data 
	 * @param last_row: is defined by the use of cross validation to read the rows of test data
	 * @return the accuracy for each one of the folds 
	 * @throws IOException
	 */
	public static double test(Probablity pro, int start_row, int last_row)throws IOException{
		
		//define variables for reading the rest of data 
		File file = new File("C:/dataset-cs6735.xlsx");
		FileInputStream fis = new FileInputStream(file);
		XSSFWorkbook wb = new XSSFWorkbook(fis);
		XSSFSheet sh = wb.getSheet("label20131201_positive_10000");
		String test_sentence = "";
		double positive_value = pro.generalPositive;
		double negative_value = pro.generalNegative;
		double nutral_value = pro.generalNutarl;
		double correct_value = pro.generalCorrect;
		double accuracy;
		
        //read the file for testing part that the star_row and last_row show the fold which should be red
		for(int z= start_row+1; z <= last_row; z++){
			String sentence = sh.getRow(z).getCell(2).getStringCellValue();
			int num = (int)sh.getRow(z).getCell(1).getNumericCellValue();
			//clean the sentence with this function
			test_sentence = sentenceCleaner(sentence);
			StringTokenizer st = new StringTokenizer(test_sentence);
			while(st.hasMoreTokens()){
				String s = st.nextToken();
				/*for each word we want to find the probablity that each word occur in positive, negative and neutral data set 
			     that use the result produced by training part we use this formula for finding the result VNB = argmax P(vj)i E positions P(ai|vj) */
				
				if(pro.vocab.contains(s.toLowerCase())){
					int index = pro.vocab.indexOf(s.toLowerCase());
					positive_value = pro.positive_pro[index] * positive_value;
					negative_value = pro.negative_pro[index] * negative_value;
					nutral_value  = pro.nural_pro[index] * nutral_value;
				}
			} 
			// count the number of true results in test data
			double max_value = Math.max(positive_value, Math.max(nutral_value, negative_value));
			if(max_value == positive_value){
				if(num == 1)
					correct_value = correct_value + 1;}
			else if(max_value == nutral_value){
				if(num == 0)
					correct_value = correct_value +1;
			}
			else{
				if(num == 2)
					correct_value = correct_value + 1;
			}
		}
		
		//find the accuracy by the use of number of correct values divided by the number of rows were red
		accuracy = correct_value / (last_row - start_row);
		return accuracy;
	}


	public static void main(String[] args) throws IOException {
		Probablity probablity = new Probablity();
		int fold_num = 5;
		double variance ;
		double average_accuracy;
		double [] result = new double[fold_num];
		double sum = 0;
		double res = 0;
		
		// define iteration based on the number entered and find folds based on this number and call read and test based on the number of folds 
			for(int i=0; i<fold_num; i++){
				probablity = read((i*(10000/fold_num)),(i*(10000/fold_num))+(10000/fold_num));
				result[i] = test(probablity,(i*(10000/fold_num)),(i*(10000/fold_num))+(10000/fold_num));
				sum = sum + result[i];
				System.out.println("The accuracy for iteration "+ i + " is "+ result[i]);
			}
			average_accuracy = sum / fold_num;
            System.out.println("the average accuracy for "+ fold_num + " times "+10000/fold_num +"-fold corss validation is " + average_accuracy);
			//find the varience
			for(int i=0; i<fold_num; i++){
				res = Math.pow((result[i] - average_accuracy), 2) + res;
			}
			variance = Math.sqrt(res);
			System.out.println("The Standard Deviation "+ fold_num +" times "+ 10000/fold_num+"-fold corss validation is " + variance);	

	}

}
