package Test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;
import weka.classifiers.Classifier;
import weka.classifiers.bayes.NaiveBayesMultinomial;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.StringToWordVector;

//@SuppressWarnings("serial")
public class TrainAndTest implements Serializable{
//public class TrainAndTest {	
	private static final long serialVersionUID = -796246928043017207L;
	private Instances m_Data = null;
	private StringToWordVector m_Filter = new StringToWordVector();
	private Classifier m_Classifier = new NaiveBayesMultinomial();
	private boolean m_UpToDate;
	
	
	public TrainAndTest() throws Exception {
		String nameOfDataset = "MessageClassificationProblem";
		// Create vector of attributes.
		FastVector attributes = new FastVector(2);
		// Add attribute for holding messages.
		attributes.addElement(new Attribute("Message", (FastVector)null));
		// Add class attribute.
		FastVector classValues = new FastVector(10);

		classValues.addElement("YES");
		classValues.addElement("NO");

		attributes.addElement(new Attribute("Class", classValues));
		// Create dataset with initial capacity of 100, and set index of class.
		m_Data = new Instances(nameOfDataset, attributes, 100);
		m_Data.setClassIndex(m_Data.numAttributes() - 1);
		}
		
	public void updateData(String message, String classValue) throws Exception {
		// Make message into instance.
		Instance instance = makeInstance(message, m_Data);
		// Set class value for instance.
		instance.setClassValue(classValue);
		// Add instance to training data.
		m_Data.add(instance);
		m_UpToDate = false;
		}
	
	

	
		
	public double getyesprob(String message) throws Exception {
		// Check whether classifier has been built.
		if (m_Data.numInstances() == 0) {
		////throw new Exception("No classifier available.");
		}
		// Check whether classifier and filter are up to date.
		if (!m_UpToDate) {
		 // Initialize filter and tell it about the input format.
		m_Filter.setInputFormat(m_Data);
		// Generate word counts from the training data.
		Instances filteredData = Filter.useFilter(m_Data, m_Filter);
		// Rebuild classifier.
		m_Classifier.buildClassifier(filteredData);
		m_UpToDate = true;
		}
		// Make separate little test set so that message
		// does not get added to string attribute in m_Data.
		Instances testset = m_Data.stringFreeStructure();
		//System.out.println("鍒嗙被鍣ㄨ緭鍏�" + testset);
		// Make message into test instance.
		Instance instance = makeInstance(message, testset);
		// Filter instance.
		m_Filter.input(instance);
		Instance filteredInstance = m_Filter.output();
		double prob = m_Classifier. GetYesProb(filteredInstance,0);
		return prob;
		}
		
		
	
	
	private Instance makeInstance(String text, Instances data) {
		// Create instance of length two.
		Instance instance = new Instance(2);
		// Set value for message attribute
		Attribute messageAtt = data.attribute("Message");
		instance.setValue(messageAtt, messageAtt.addStringValue(text));
		// Give instance access to attribute information from the dataset.
		instance.setDataset(data);
		//System.out.println("杩囨护鍓�" + instance);
		return instance;
		}
	
	public static void TrainTrainAndTestFile (String source, String ModleName, String classvalue)
	{
		
		try {
			
			String messageName = source;
			
			FileReader m = new FileReader(messageName);
			StringBuffer message = new StringBuffer(); int l;
			while ((l = m.read()) != -1) {
			message.append((char)l);
			}
			m.close();
			
			String classValue = classvalue;
			String modelName = ModleName;
		
			TrainAndTest messageCl;
			try {
			ObjectInputStream modelInObjectFile =
			new ObjectInputStream(new FileInputStream(modelName));
			messageCl = (TrainAndTest) modelInObjectFile.readObject();
			modelInObjectFile.close();
			} catch (FileNotFoundException e) {
			messageCl = new TrainAndTest();
			}
			
			messageCl.updateData(message.toString(), classValue);
			// Save message classifier object.
			ObjectOutputStream modelOutObjectFile =
			new ObjectOutputStream(new FileOutputStream(modelName));
			modelOutObjectFile.writeObject(messageCl);
			modelOutObjectFile.close();
			System.out.println(message.toString());
			System.out.println("更新完成");
			} 
		catch (Exception e) {
			e.printStackTrace();
			}
		
	}
	
	public static void TrainedAModle(String sourceFile,String ModleFile){

		 File[] file = (new File( sourceFile )).listFiles();
      for (int i = 0; i < file.length; i++){
   	   String classes = file[i].getName();
   	   //String test1 = file[i].getAbsolutePath();

   		   if (file[i].isFile()) 
   		   	{
   			   TrainTrainAndTestFile(file[i].getAbsolutePath(), ModleFile,classes );
   	         }
   	           if (file[i].isDirectory()) 
   	           {
   	        	   System.out.println("路径"+file[i].getAbsolutePath());
   	        	   String Dir = sourceFile + File.separator + file[i].getName();
   	        	   System.out.println("Dir："+Dir);
   	        	   File[] file2 = (new File( Dir )).listFiles();
   	        	   for (int j = 0; j < file2.length; j++){
   	              TrainTrainAndTestFile(file2[j].getAbsolutePath(),ModleFile,classes);
   	              System.out.println(file2[j].getAbsolutePath());
   	              System.out.println(classes);
   	              }
   	           }
   	   }
      		
	}
	

	
	
	public static double ProbOfADoc(String messages, String ModleFile){
		double prob = 0.0;
		try {
			// Read message file into string.
			String messageName = messages;
			FileReader m = new FileReader(messageName);
			StringBuffer message = new StringBuffer(); int l;
			while ((l = m.read()) != -1) {
			message.append((char)l);
			}
			m.close();
			String modelName = ModleFile;
			
			TrainAndTest messageCl1;
			try {
			ObjectInputStream modelInObjectFile =
			new ObjectInputStream(new FileInputStream(modelName));
			messageCl1 = (TrainAndTest) modelInObjectFile.readObject();
			modelInObjectFile.close();
			} catch (FileNotFoundException e) {
			messageCl1 = new TrainAndTest();
			}
			long startTime1 = System.currentTimeMillis();
			//System.out.println(message.toString());
			prob = messageCl1.getyesprob(message.toString());	
			long endTime1 = System.currentTimeMillis();
			System.out.println("分类时间 " + (endTime1 - startTime1) + "ms");
			ObjectOutputStream modelOutObjectFile =
			new ObjectOutputStream(new FileOutputStream(modelName));
			modelOutObjectFile.writeObject(messageCl1);
			modelOutObjectFile.close();
			} catch (Exception e) {
			e.printStackTrace();
			}	
		return prob;
	}
	
	
	public static ArrayList<Entry<String,Double>> sort(HashMap<String, Double> forsort){
		ArrayList<Map.Entry<String, Double>> tag = new ArrayList<Map.Entry<String, Double>>(forsort.entrySet());
		System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
		Collections.sort(tag, new Comparator<Map.Entry<String, Double>>() {
			public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
				if (o2.getValue() != null && o1.getValue() != null && o2.getValue().compareTo(o1.getValue()) > 0) {
					return 1;
				} else {
					return -1;
				}
			}
		});
		System.out.println(" 10类的概率分别为：");
		for(int i=0;i<10;i++)
			System.out.println("概率："+tag.get(i));
		
		
		return tag;
	}
	

	
	public static void main(String[] arg){
		String sourceFileEntrepreneurship = "Entrepreneurship";
		String sourceFileExhibition = "Exhibition";
		String sourceFileIT = "IT";
		String sourceFileLecture = "Lecture";
		String sourceFileMovie = "Movie";
		String sourceFileOutdoor = "Outdoor";
		String sourceFileParenting = "Parenting";
		String sourceFileParty = "Party";
		String sourceFileShow = "Show";
		String sourceFileSports = "Sports";
		
		String ModleFileEntrepreneurship = "Entrepreneurship.modle";
		String ModleFileExhibition = "Exhibition.modle";
		String ModleFileIT = "IT.modle";
		String ModleFileLecture = "Lecture.modle";
		String ModleFileMovie = "Movie.modle";
		String ModleFileOutdoor = "Outdoor.modle";
		String ModleFileParenting = "Parenting.modle";
		String ModleFileParty = "Party.modle";
		String ModleFileShow = "Show.modle";
		String ModleFileSports = "Sports.modle";
		
		String Entrepreneurship = "创业";
		String Exhibition = "展览";
		String IT = "IT";
		String Lecture = "讲座";
		String Movie = "电影";
		String Outdoor ="户外";
		String Parenting ="亲子";
		String Party ="聚会";
		String Show = "演出";
		String Sports ="体育";
		
		String messages = "mid.txt";
		String MidFile = "mid.txt";
		String inputs ="单身青年一起来打羽毛球	每周组织羽毛球爱好者在不同地点打球,羽毛球可以健身强体,通过打球还能结识好多朋友,通过打球找到另一半是多么正确的决定哈.因为TA一定是积极乐观健康向上的,一起打球的小伙伴已经有牵手的了...                                                                                                  如果你也热爱羽毛球这项运动就加入我们吧~一般奥体中心,东单体育馆,宣武体育中心,青年路董炯这几个地方打球,如果您有好的场地可以提供给我们.想打球的小伙伴可以加微信letianpai20121211~奥体中心坐地铁八号线奥体中心站下C口出来走700米即是~费用大概每两小时24元左右~";
		
		Scanner sc = new Scanner(System.in);
		System.out.println("训练：1        分类：2");
		int k = sc.nextInt();
		
		switch (k){
		case 1:
			
			TrainedAModle(sourceFileEntrepreneurship,ModleFileEntrepreneurship);
			TrainedAModle(sourceFileExhibition,ModleFileExhibition);
			TrainedAModle(sourceFileLecture,ModleFileLecture);
			TrainedAModle(sourceFileIT,ModleFileIT);
			TrainedAModle(sourceFileMovie,ModleFileMovie);
			TrainedAModle(sourceFileOutdoor,ModleFileOutdoor);
			TrainedAModle(sourceFileParenting,ModleFileParenting);
			TrainedAModle(sourceFileParty,ModleFileParty);
			TrainedAModle(sourceFileShow,ModleFileShow);
			TrainedAModle(sourceFileSports,ModleFileSports);
			
		break;
		
		case 2:
			
			try {
				
				StringReader reader = new StringReader(inputs);
			    FileWriter fw = new FileWriter( MidFile );
			    BufferedWriter bw = new BufferedWriter(fw );
			    
			    IKSegmenter ik = new IKSegmenter(reader,true);
			    Lexeme lex = null;
			    while((lex=ik.next())!=null)
			    {
			 	   bw.write(lex.getLexemeText());
			 	   bw.write(' ');
			    }
			    bw.close();
			    fw.close();
			} catch( IOException e ) {
			    e.printStackTrace();
			}
			
					
			HashMap<String, Double> hp = new HashMap<String, Double>();
			
			hp.put(Entrepreneurship,ProbOfADoc(messages,ModleFileEntrepreneurship));
			hp.put(Exhibition,ProbOfADoc(messages,ModleFileExhibition));
			hp.put(Lecture,ProbOfADoc(messages,ModleFileLecture));
			hp.put(IT,ProbOfADoc(messages,ModleFileIT));
			hp.put(Movie,ProbOfADoc(messages,ModleFileMovie));
			hp.put(Outdoor,ProbOfADoc(messages,ModleFileOutdoor));
			hp.put(Parenting,ProbOfADoc(messages,ModleFileParenting));
			hp.put(Party,ProbOfADoc(messages,ModleFileParty));
			hp.put(Show,ProbOfADoc(messages,ModleFileShow));
			hp.put(Sports,ProbOfADoc(messages,ModleFileSports));
			
			List<Map.Entry<String, Double>> hp1 = sort(hp);
			System.out.println("分类为："+hp1.get(0).getKey());
			
			if(hp1.get(0).getKey().equals(Entrepreneurship)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"YES");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
								
			}else if(hp1.get(0).getKey().equals(Exhibition)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"YES");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(Lecture)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"YES");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(IT)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"YES");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(Movie)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"YES");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(Outdoor)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"YES");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(Parenting)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"YES");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(Party)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"YES");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else if(hp1.get(0).getKey().equals(Show)){
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"YES");
				TrainTrainAndTestFile(messages,ModleFileSports,"NO");
				
			}else{
				
				TrainTrainAndTestFile(messages,ModleFileEntrepreneurship,"NO");
				TrainTrainAndTestFile(messages,ModleFileExhibition,"NO");
				TrainTrainAndTestFile(messages,ModleFileLecture,"NO");
				TrainTrainAndTestFile(messages,ModleFileIT,"NO");
				TrainTrainAndTestFile(messages,ModleFileMovie,"NO");
				TrainTrainAndTestFile(messages,ModleFileOutdoor,"NO");
				TrainTrainAndTestFile(messages,ModleFileParenting,"NO");
				TrainTrainAndTestFile(messages,ModleFileParty,"NO");
				TrainTrainAndTestFile(messages,ModleFileShow,"NO");
				TrainTrainAndTestFile(messages,ModleFileSports,"YES");
				
			}
			
	
		}
		
	}
	
}


