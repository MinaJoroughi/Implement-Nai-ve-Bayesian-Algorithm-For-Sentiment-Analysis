package reader;

import java.util.ArrayList;


public class Probablity {
	ArrayList<String> vocab = new ArrayList<String>();
	public double generalPositive;
	public double generalNegative;
	public double generalNutarl;
	public double generalCorrect;
	public double[] positive_pro = new double [vocab.size()];
	public double[] negative_pro = new double[vocab.size()];
	public double[] nural_pro = new double[vocab.size()];
	
	public Probablity(){}
	
}
