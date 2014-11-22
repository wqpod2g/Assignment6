package nju.iip.ensembleboost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * @description Gradient boosting类
 * @author wangqiang
 * @since 2014-11-20
 */
public class GradientBoosting {
	
	private static int M=1;//迭代次数
	
	/**
	 * 测试数据路径
	 */
	private static String filePath="lily.data";
	
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
	
	private static ArrayList<ArrayList<Double>>Fx_list=new ArrayList<ArrayList<Double>>();
	
	private static ArrayList<ArrayList<Double>>px_list=new ArrayList<ArrayList<Double>>();
	
	
	
	
	/**
	 * @获取整个样本的特征矩阵集合
	 * @return
	 * @throws IOException
	 */
	public static ArrayList<ArrayList<Double>>getAllMatrix(){
		try {
			allMatrix=Tools.readFile(filePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allMatrix;
	}
	
	
	
	/**
	 * @description 初始化每篇帖子的Fx_list
	 */
    public static void initialize_Fx_list(ArrayList<ArrayList<Double>>train_data){
    	int data_size=train_data.size();
    	for(int i=0;i<data_size;i++){
    		ArrayList<Double>Fx=new ArrayList<Double>();
    		for(int j=0;j<10;j++){
    			Fx.add(0.0);
    		}
    		Fx_list.add(Fx);
    	}
		
	}
    
    /**
     * @description 根据当前Fx_list计算px_list
     */
    public static void caluculate_Px_list(ArrayList<ArrayList<Double>>train_data){
    	int data_size=train_data.size();
    	for(int i=0;i<data_size;i++){
    		ArrayList<Double>Pxi=new ArrayList<Double>();
    		ArrayList<Double>FXi=Fx_list.get(i);
    		double sum=Tools.getSumExp(FXi);
    		for(int j=0;j<10;j++){
    			Pxi.add(Math.exp(FXi.get(j))/sum);
    		}
    		px_list.add(Pxi);
    	}
    }
    
    /**
     * @description 
     * @param k
     * @param train_data
     * @return
     */
    public static DecisinTreeRegression create_k_Tree(int k,ArrayList<ArrayList<Double>>train_data){
    	int vector_size=train_data.get(0).size();
    	int data_size=train_data.size();
    	ArrayList<Double>list=new ArrayList<Double>();
    	for(int i=0;i<data_size;i++){
    		list.add(train_data.get(i).get(vector_size-1));
    	}
    	for(int i=0;i<data_size;i++){
    		double yi=train_data.get(i).get(vector_size-1);
    		double Yik=0;
    		if(yi==k){
    			Yik=1;
    		}
    		double Pkxi=px_list.get(i).get(k);
    		train_data.get(i).set(vector_size-1,Yik-Pkxi);
    	}
    	DecisinTreeRegression DT=new DecisinTreeRegression(train_data);
    	DT.getDecisionTree();
    	updateFkx(k,DT);
    	for(int i=0;i<data_size;i++){
    		train_data.get(i).set(vector_size-1, list.get(i));
    	}
    	return DT;
    }
    
    /**
     * @description 更新Fx
     * @param k
     * @param DT
     */
    public static void updateFkx(int k,DecisinTreeRegression DT){
    	ArrayList<ArrayList<Double>>temp_data=DT.get_sample_data();
    	int data_size=temp_data.size();
    	for(int i=0;i<data_size;i++){
    		ArrayList<Double>vector=temp_data.get(i);
    		double old_Fkx=Fx_list.get(i).get(k);
    		double gamma=DT.getResult(vector,DT.get_root_node());
    		Fx_list.get(i).set(k,old_Fkx+gamma);
    	}
    }
    
    /**
     * @description 预测某篇帖子所属类别
     * @param vector
     * @param all_node_map
     * @return
     */
    public static double getResult(ArrayList<Double>vector,HashMap<Integer,ArrayList<DecisinTreeRegression>>all_node_map){
    	HashMap<Double,Double>result_map=new HashMap<Double,Double>();
    	for(int i=0;i<10;i++){
    		double gamma_sum=0.0;
    		Set<Integer>keys=all_node_map.keySet();
    		for(Integer key:keys){
    			DecisinTreeRegression DT=all_node_map.get(key).get(i);
    			gamma_sum=gamma_sum+DT.getResult(vector,DT.get_root_node());
    		}
    		result_map.put(i*1.0,gamma_sum);
    	}
    	return Tools.sortMapDouble(result_map);
    	
    }
    
    
	
    public static void process(){
    	ArrayList<Double>result_list=new ArrayList<Double>();
    	for(int m=0;m<10;m++){
    		Fx_list.clear();
        	ArrayList<ArrayList<Double>>testSample=new ArrayList<ArrayList<Double>>();
    		ArrayList<ArrayList<Double>>trainSample=new ArrayList<ArrayList<Double>>();
    		Tools.divide(m, allMatrix, testSample, trainSample);
    		initialize_Fx_list(trainSample);
    		HashMap<Integer,ArrayList<DecisinTreeRegression>>all_node_map=new HashMap<Integer,ArrayList<DecisinTreeRegression>>();
    		for(int i=0;i<M;i++){
    			px_list.clear();
    			ArrayList<DecisinTreeRegression>node_list=new ArrayList<DecisinTreeRegression>();
    			caluculate_Px_list(trainSample);
    			for(int k=0;k<10;k++){
    				DecisinTreeRegression DT=create_k_Tree(k,trainSample);
    				node_list.add(DT);
    			}
    			all_node_map.put(i, node_list);
    		}
    		double count=0.0;
    		int testSample_size=testSample.size();
    		for(int j=0;j<testSample_size;j++){
    			ArrayList<Double>vector=testSample.get(j);
    			double a=vector.get(vector.size()-1);
    			double b=getResult(vector,all_node_map);
    			if(a==b){
    				count++;
    			}
    		}
    		System.out.println("第"+(m+1)+"折命中率为:"+count/100);
    		result_list.add(count/100);
    	}
    	System.out.println("十折均值为:"+Tools.getMean(result_list));
		System.out.println("十折方差为:"+Tools.getDeviation(result_list));
    	
    }
    
	
	public static void main(String[] arg){
		getAllMatrix();
		process();
	}

}
