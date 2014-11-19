package nju.iip.ensembleboost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


/**
 * @description 随机森林类
 * @author wangqiang
 * @since 2014-11-17
 */
public class RandomForest {
	
	/**
	 * 测试数据路径
	 */
	private static String filePath="lily.data";
	
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
	
	private static int trees_num=40;//决策树棵树
	
	private static int attribute_num=100;//随机抽取属性个数
	
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
	 * @description 随机获得N个训练样本
	 * @return sample_data
	 */
	public static  ArrayList<ArrayList<Double>>getSampleData(ArrayList<ArrayList<Double>>trainSample){
		ArrayList<ArrayList<Double>>sample_data=new ArrayList<ArrayList<Double>>();
		int size=trainSample.size();
		for(int i=0;i<size;i++){
			int Num=new Random().nextInt(size);
			sample_data.add(trainSample.get(Num));
		}
		return sample_data;
	}
	
	
	/**
	 * @description 随机生成m个属性
	 * @return attribute_list
	 */
	public static ArrayList<Integer>getAttributrList(){
		ArrayList<Integer>list=new ArrayList<Integer>();
		ArrayList<Integer>attribute_list=new ArrayList<Integer>();
		int M=allMatrix.get(0).size()-1;//属性总数
		for(int i=0;i<M;i++){
			list.add(i);
		}
		for(int i=0;i<attribute_num;i++){
			int Num=new Random().nextInt(list.size());
			attribute_list.add(list.get(Num));
			list.remove(Num);
		}
		return attribute_list;
	}
	
	/**
	 * @description 创建随机森林
	 * @return ArrayList<Node>
	 */
	public static ArrayList<Node>creatRandomForest(ArrayList<ArrayList<Double>>trainSample){
		ArrayList<Node>random_forest=new ArrayList<Node>();
		for(int i=0;i<trees_num;i++){
			ArrayList<ArrayList<Double>>sample_data=getSampleData(trainSample);
			ArrayList<Integer>attribute_list=getAttributrList();
			DecisionTree DT=new DecisionTree(sample_data,attribute_list);
			Node root_node=DT.getDecisionTree();
			random_forest.add(root_node);
		}
		return random_forest;
	}
	
	/**
	 * @description 预测帖子所属类别
	 * @param vector
	 * @param random_forest
	 * @return Classify
	 */
	public static Double getPredictClassify(ArrayList<Double>vector,ArrayList<Node>random_forest){
		HashMap<Double,Integer>map=new HashMap<Double,Integer>();
		for(Node N:random_forest){
			Double result=Tools.getResult(vector, N);
			if(map.containsKey(result)){
				map.put(result, map.get(result)+1);
			}
			else{
				map.put(result, 1);
			}
		}
		return Tools.sortMap(map);
	}
	
	
	public static void process(){
		ArrayList<Double>result_list=new ArrayList<Double>();
		for(int i=0;i<10;i++){
			ArrayList<ArrayList<Double>>testSample=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>trainSample=new ArrayList<ArrayList<Double>>();
			//int num=Tools.divide2(i,allMatrix,testSample,trainSample);
			Tools.divide(i,allMatrix,testSample,trainSample);
			ArrayList<Node>random_forest=creatRandomForest(trainSample);
			double count=0.0;
			int classify_flag=allMatrix.get(0).size()-1;
			for(ArrayList<Double>vector:testSample){
				double a=getPredictClassify(vector,random_forest);
				double b=vector.get(classify_flag);
				if(a==b){
					count++;
				}
			}
			System.out.println("第"+(i+1)+"折命中率为:"+count/100);
			result_list.add(count/100);
		}
		System.out.println("十折均值为:"+Tools.getMean(result_list));
		System.out.println("十折方差为:"+Tools.getDeviation(result_list));
	}
	
	
	public static void main(String[] args){
		getAllMatrix();
		long start=System.currentTimeMillis();
		process();
		long end=System.currentTimeMillis();
		System.out.printf("运行时间："+(end-start)/1000+"s");
	}
	


}
