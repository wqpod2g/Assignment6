package nju.iip.ensembleboost;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class Tools {
	
	
	/**
	 * @decription 从文件中读入数据
	 * @param filePath
	 * @return 
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<Double>> readFile(String filePath) throws IOException{
		ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), "UTF8"));
		 String line = br.readLine();
		 while(line != null){  
	        	String[] str=line.split(",");
	        	ArrayList<Double>vector=new ArrayList<Double>();
	        	for(int i=0;i<str.length;i++){
	        		vector.add(Double.parseDouble(str[i]));
	        	}
	        	allMatrix.add(vector);
	            line = br.readLine();    
	        }
	        br.close();
		return allMatrix;
	}
	
	
	
	/**
	 * @description 十折交叉划分
	 * @param n
	 * @param allMatrix
	 * @param testSample
	 * @param trainSample
	 */
	public static void divide(int n,ArrayList<ArrayList<Double>>allMatrix,ArrayList<ArrayList<Double>>testSample,ArrayList<ArrayList<Double>>trainSample){
		HashMap<Double,ArrayList<ArrayList<Double>>>allMap=new HashMap<Double,ArrayList<ArrayList<Double>>>();
		int size=allMatrix.size();
		int vector_size=allMatrix.get(0).size();
		for(int i=0;i<size;i++){
			Double classify=allMatrix.get(i).get(vector_size-1);
			if(!allMap.containsKey(classify)){
				ArrayList<ArrayList<Double>>vectors=new ArrayList<ArrayList<Double>>();
				vectors.add(allMatrix.get(i));
				allMap.put(classify, vectors);
			}
			else{
				allMap.get(classify).add(allMatrix.get(i));
			}
		}
		
		Set<Double>classifys=allMap.keySet();
		for(Double classify:classifys){
			ArrayList<ArrayList<Double>>vectors=allMap.get(classify);
			for(int i=0;i<vectors.size();i++){
				if(i>=10*n&&i<(n+1)*10){
					testSample.add(vectors.get(i));
				}
				else{
					trainSample.add(vectors.get(i));
				}
			}
		}
	}
	
	/**
	 * @description map按升序排序并输出value最大的值对应的key
	 * @param map
	 * @return
	 */
	public static Double sortMap(HashMap<Double,Integer> map){
		List<Map.Entry<Double,Integer>> list = new ArrayList<Map.Entry<Double,Integer>>(map.entrySet());
	    Collections.sort(list,new Comparator<Map.Entry<Double,Integer>>() { //升序排序
	    	public int compare(Entry<Double, Integer> o1,Entry<Double, Integer> o2) {
	                return o1.getValue().compareTo(o2.getValue());
	            }
	        });
	    
	    int size=list.size();
	    Map.Entry<Double,Integer> mapping=list.get(size-1);
	    Double result=mapping.getKey();
		return result;
	}
	
	
	/**
	 * @description map按升序排序并输出value最大的值对应的key
	 * @param map
	 * @return
	 */
	public static Double sortMapDouble(HashMap<Double,Double> map){
		List<Map.Entry<Double,Double>> list = new ArrayList<Map.Entry<Double,Double>>(map.entrySet());
	    Collections.sort(list,new Comparator<Map.Entry<Double,Double>>() { //升序排序
	    	public int compare(Entry<Double, Double> o1,Entry<Double, Double> o2) {
	                return o1.getValue().compareTo(o2.getValue());
	            }
	        });
	    
	    int size=list.size();
	    Map.Entry<Double,Double> mapping=list.get(size-1);
	    Double result=mapping.getKey();
		return result;
	}
	
	
	 /**
     * @decription 计算平均值
     * @param list
     * @return
     */
    public static Double getMean(ArrayList<Double>list){
    	Double sum=0.0;
		for(int i=0;i<list.size();i++){
			sum=sum+list.get(i);
		}
		Double mean=sum/10;
		return mean;
    }
    
    /**
     * @description 计算标准差
     * @param list
     * @return
     */
    public static Double getDeviation(ArrayList<Double>list){
    	Double mean=getMean(list);
    	Double deviation=0.0;
    	for(int i=0;i<list.size();i++){
    		deviation=deviation+(list.get(i)-mean)*(list.get(i)-mean);
    	}
    	deviation=Math.sqrt(deviation/(list.size()-1));
    	
    	return deviation ;
    }
    
    
    public static int max(int a,int b){
    	if(a>=b){
    		return a;
    	}
    	else{
    		return b;
    	}
    }
    
    /**
     * @description 计算二叉树的高度
     * @param N
     * @return
     */
    public static int getTreeHeight(Node N){
    	if(N==null){
    		return 0;
    	}
    	else{
    		return 1+max(getTreeHeight(N.getLeftChild()),getTreeHeight(N.getRightChild()));
    	}
    }
    
	
	
	/**
	 * @description 计算某篇帖子所属类别
	 * @param vector
	 * @return
	 */
	public  static Double getResult(ArrayList<Double>vector,Node N){
		
		if(N.getLeftChild()==null&&N.getRightChild()==null){
			return N.getClassify();
		}
		
		else{
			if(vector.get(N.getAttribute())<=N.get_split_point()){
				return getResult(vector,N.getLeftChild());
			}
			
			else{
				return getResult(vector,N.getRightChild());
			}
		}
	}
	
	
	 /**
	 * @description 十折交叉划分
	 * @param n
	 * @param allMatrix
	 * @param testSample
	 * @param trainSample
	 */
	public static int  divide2(int n,ArrayList<ArrayList<Double>>allMatrix,ArrayList<ArrayList<Double>>testSample,ArrayList<ArrayList<Double>>trainSample){
		int num=allMatrix.size()/10;
		for(int i=0;i<allMatrix.size();i++){
			if(i>=num*n&&i<(n+1)*num){
				testSample.add(allMatrix.get(i));
			}
			else{
				trainSample.add(allMatrix.get(i));
			}
		}
		return num;
	
	}
	
	
	/**
	 * @description 判断一个向量是否为0向量
	 * @return false:不是零向量，true：是零向量
	 */
    public static Boolean IsZeroVector(ArrayList<Double> vector){
    	Boolean result=true;
    	int size=vector.size()-1;
    	for(int i=0;i<size;i++){
    		if(vector.get(i)!=0){
    			result=false;
    		}
    	}
    	return result;
    }

		 
			
	public static double getSum(ArrayList<Double>list){
		int size=list.size();
		double sum=0.0;
		for(int i=0;i<size;i++){
			sum=sum+list.get(i);
		}
	
		return sum;
	}
	
	
	public static double getSumExp(ArrayList<Double>vector){
		double sum=0.0;
		int vector_size=vector.size();
		for(int i=0;i<vector_size;i++){
			sum=sum+Math.exp(vector.get(i));
		}
		return sum;
	}
		
			


}
