package nju.iip.ensembleboost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @description adaptive boosting类
 * @author wangqiang
 * @since 2014-11-18
 */
public class Adaboost {
	
	private static ArrayList<Double>weight_list=new  ArrayList<Double>();//每篇帖子的权重
	
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
	
	/**
	 * 测试数据路径
	 */
	private static String filePath="lily.data";
	
	
	private static ArrayList<Integer>attribte_list=new ArrayList<Integer>();//属性集合
	
	private static int Iteration_times=10;//迭代次数
	
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
	 * @description 创建属性列表
	 * @return
	 */
	public static ArrayList<Integer>getAttribte_list(){
		for(int i=0;i<allMatrix.get(0).size()-1;i++){
			attribte_list.add(i);
		}
		return attribte_list;
	}
	
	
	/**
	 * @description 初始化权重
	 * @param train_data（训练样本）
	 */
	
	public static void initial_weight_list(ArrayList<ArrayList<Double>>train_data){
		int post_size=train_data.size();
		for(int i=0;i<post_size;i++){
			weight_list.add(1.0/post_size);
		}
	}
	
	/**
	 * @description 根据当前权重list从train_data当中抽取训练样本
	 * @param train_data
	 * @return sample_data
	 */
	public static ArrayList<ArrayList<Double>>getSampleData(ArrayList<ArrayList<Double>>train_data){
		ArrayList<ArrayList<Double>>sample_data=new ArrayList<ArrayList<Double>>();
		int weight_list_size=weight_list.size();
		for(int i=0;i<weight_list_size;i++){
			int extract_times=Integer.parseInt(new java.text.DecimalFormat("0").format(weight_list.get(i)*weight_list_size));//该元组抽取次数
			for(int j=0;j<extract_times;j++){
				sample_data.add(train_data.get(i));
			}
		}
		return sample_data;
	}
	
	
	/**
	 * @description 创建决策树并且得到当前树的权重并跟新weight_list
	 * @param sample_data
	 * @return
	 */
	public static DecisionTree getDecisionTree(ArrayList<ArrayList<Double>>sample_data,ArrayList<ArrayList<Double>>train_data){
		DecisionTree DT=new DecisionTree(sample_data,attribte_list);
		Node N=DT.getDecisionTree();
		double error=getError(N,train_data);//计算该树的误差率
		double w=Math.log((1-error)/error);
		DT.setWeight(w);
		updataWeightList(error,N,train_data);
		return DT;
	}
	
	
	/**
	 * @description 跟新weight_list
	 * @param error
	 * @param N
	 */
	public static void updataWeightList(double error,Node N,ArrayList<ArrayList<Double>>train_data){
		double w=error/(1-error);
		int classify_flag=allMatrix.get(0).size()-1;
		int size=train_data.size();
		double old_sum=Tools.getSum(weight_list);
		for(int i=0;i<size;i++){
			ArrayList<Double> vector=train_data.get(i);
			double a=Tools.getResult(vector, N);
			double b=vector.get(classify_flag);
			if(a==b){
				double temp=weight_list.get(i);
				weight_list.set(i, temp*w);
			}
		}
		
		double new_sum=Tools.getSum(weight_list);
		double rate=old_sum/new_sum;
		for(int i=0;i<size;i++){
			double temp=weight_list.get(i);
			weight_list.set(i, temp*rate);
		}
	}
	
	
	/**
	 * @description 计算当前树的误差率
	 * @param N
	 * @return
	 */
	public static double getError(Node N,ArrayList<ArrayList<Double>>train_data){
		double error=0.0;
		int classify_flag=allMatrix.get(0).size()-1;
		int size=train_data.size();
		for(int i=0;i<size;i++){
			ArrayList<Double> vector=train_data.get(i);
			int result=0;
			double a=Tools.getResult(vector, N);
			double b=vector.get(classify_flag);
			if(a!=b){
				result=1;
			}
			
			error=error+weight_list.get(i)*result;
			
		}
		return error;
	}
	
	
	/**
	 * @description 迭代产生所有的决策树
	 * @param sample_data
	 * @return
	 */
	public static ArrayList<DecisionTree>getAllTress(ArrayList<ArrayList<Double>>train_data){
		ArrayList<DecisionTree>all_trees=new ArrayList<DecisionTree>();
		for(int i=0;i<Iteration_times;i++){
			ArrayList<ArrayList<Double>>sample_data=getSampleData(train_data);
			DecisionTree DT=getDecisionTree(sample_data,train_data);
			all_trees.add(DT);
		}
		return all_trees;
		
	}
	

	/**
	 * @description 预测帖子所属类别
	 * @param vector
	 * @param random_forest
	 * @return Classify
	 */
	public static Double getPredictClassify(ArrayList<Double>vector,ArrayList<DecisionTree>all_trees){
		double result=0.0;
		HashMap<Double,Double>map=new HashMap<Double,Double>();
		for(DecisionTree tree:all_trees){
			Node root_node=tree.getRootNode();
			double w=tree.getWeight();
			double predict_result=Tools.getResult(vector, root_node);
			if(!map.containsKey(predict_result)){
				map.put(predict_result,w);
			}
			else{
				map.put(predict_result, map.get(predict_result)+w);
			}
		}
		result=Tools.sortMapDouble(map);
		return result;
	}
	
	
	public static void process(){
		ArrayList<Double>result_list=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			ArrayList<ArrayList<Double>>test_data=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>train_data=new ArrayList<ArrayList<Double>>();
			Tools.divide(i,allMatrix,test_data,train_data);
			initial_weight_list(train_data);//根据当前训练样本初始化权重list
			ArrayList<DecisionTree>all_trees=getAllTress(train_data);
			double count=0.0;
			int classify_flag=allMatrix.get(0).size()-1;
			for(ArrayList<Double>vector:test_data){
				double a=getPredictClassify(vector,all_trees);
				double b=vector.get(classify_flag);
				if(a==b){
					count++;
				}
			}
			weight_list.clear();
			System.out.println("第"+(i+1)+"折命中率为:"+count/100);
			result_list.add(count/100);
			
		}
		System.out.println("十折均值为:"+Tools.getMean(result_list));
		System.out.println("十折方差为:"+Tools.getDeviation(result_list));
		
	}
	
	
	
	
	public static void main(String[] arg){
		getAllMatrix();
		getAttribte_list();
		process();
	
	}

}
