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
	public static ArrayList<Integer>getAttribte_list (){
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
	public static DecisionTree getDecisionTree(ArrayList<ArrayList<Double>>sample_data){
		DecisionTree DT=new DecisionTree(sample_data,attribte_list);
		Node N=DT.getDecisionTree();
		double error=getError(N);//计算该树的误差率
		double w=Math.log((1-error)/error);
		DT.setWeight(w);
		updataWeightList(error,N);
		return DT;
	}
	
	
	/**
	 * @description 跟新weight_list
	 * @param error
	 * @param N
	 */
	public static void updataWeightList(double error,Node N){
		
	}
	
	
	/**
	 * @description 计算当前树的误差率
	 * @param N
	 * @return
	 */
	public static double getError(Node N){
		double error=0.0;
		ArrayList<ArrayList<Double>>sample_data=N.getDocList();
		return error;
	}
	
	
	/**
	 * @description 迭代产生所有的决策树
	 * @param sample_data
	 * @return
	 */
	public static ArrayList<DecisionTree>getAllTress(ArrayList<ArrayList<Double>>trainSample){
		ArrayList<DecisionTree>all_trees=new ArrayList<DecisionTree>();
		for(int i=0;i<Iteration_times;i++){
			ArrayList<ArrayList<Double>>sample_data=getSampleData(trainSample);
			DecisionTree DT=getDecisionTree(sample_data);
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
		return result;
	}
	
	
	public static void process(){
		ArrayList<Double>result_list=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			ArrayList<ArrayList<Double>>testSample=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>trainSample=new ArrayList<ArrayList<Double>>();
			Tools.divide(i,allMatrix,testSample,trainSample);
			initial_weight_list(trainSample);
			ArrayList<DecisionTree>all_trees=getAllTress(trainSample);
			double count=0.0;
			int classify_flag=allMatrix.get(0).size()-1;
			for(ArrayList<Double>vector:testSample){
				double a=getPredictClassify(vector,all_trees);
				double b=vector.get(classify_flag);
				if(a==b){
					count++;
				}
			}
			attribte_list.clear();
			System.out.println("第"+(i+1)+"折命中率为:"+count/100);
			result_list.add(count/100);
			
		}
		System.out.println("十折均值为:"+Tools.getMean(result_list));
		System.out.println("十折方差为:"+Tools.getDeviation(result_list));
		
	}
	
	
	
	
	public static void main(String[] arg){
		getAllMatrix();
		initial_weight_list(allMatrix);
	
	}

}
