package nju.iip.ensembleboost;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;


public class DecisinTreeRegression {
	
	
	private  ArrayList<Integer>attribte_list;//属性集合
	
	private  Node root_node;//根节点
	
	private  int attribte_list_size;//属性个数
	
	private  ArrayList<ArrayList<Double>>sample_data;//训练样本
	
	public  ArrayList<ArrayList<Double>> get_sample_data(){
		return this.sample_data;
	}
	
	public Node get_root_node(){
		return this.root_node;
	}
	
	public DecisinTreeRegression(ArrayList<ArrayList<Double>>sample_data){
		this.sample_data=sample_data;
	}
	
	/**
	 * @description 创建属性列表
	 * @return
	 */
	public  ArrayList<Integer>getAttribte_list(){
		attribte_list=new ArrayList<Integer>();
		attribte_list_size=sample_data.get(0).size()-1;
		for(int i=0;i<attribte_list_size;i++){
			attribte_list.add(i);
		}
		return attribte_list;
	}
	
	/**
	 * @decription 初始化根节点
	 * @return rootNode
	 * @throws IOException 
	 */
	public  Node IniRootNode(ArrayList<ArrayList<Double>>matrix){
		Node rootNode=new Node(matrix);
		return rootNode;
	}
	
	/**
	 * @description 计算某个点内元组y值的平方残差
	 * @param D(元组集合)
	 * @return squared_residuals
	 */
	public  Double getSquaredResiduals(ArrayList<ArrayList<Double>>D){
		double squared_residuals=0.0;
		double mean=0.0;
		double sum=0.0;
		double temp=0.0;
		for(ArrayList<Double> vector:D){
			sum=sum+vector.get(attribte_list_size);
		}
		mean=sum/D.size();
		for(ArrayList<Double> vector:D){
			temp=vector.get(attribte_list_size)-mean;
			squared_residuals=squared_residuals+temp*temp;
		}
		return squared_residuals;
	}
	
	
	
	
	/**
	 * @description 计算在某个属性下最佳划分的squared_residuals与split_point
	 * @param n(第几个属性)
	 * @param lefeDocList
	 * @param rightDocList
	 * @return ArrayList<Double>{squared_residuals,split_point}
	 */
    public  ArrayList<Double> get_attribute_squared_residuals(int n,ArrayList<ArrayList<Double>>D,ArrayList<ArrayList<Double>>lefeDocList,ArrayList<ArrayList<Double>>rightDocList){
    	double split_point=0.0;
		double squared_residuals=Double.POSITIVE_INFINITY;
		ArrayList<Double> divide_result = new ArrayList<Double>();
		ArrayList<Double> value_list=new ArrayList<Double>(); 
		for(ArrayList<Double> vector:D){
			if(!value_list.contains(vector.get(n)))
			{
				value_list.add(vector.get(n));//统计所有出现过的属性值（去掉重复的）
			}
		}
		Collections.sort(value_list);
		for(Double value:value_list){
			ArrayList<ArrayList<Double>>D1=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>D2=new ArrayList<ArrayList<Double>>();
			for(ArrayList<Double> vector:D){
				if(vector.get(n)<=value){
					D1.add(vector);
				}
				else{
					D2.add(vector);
				}
				
			}
			double temp_squared_residuals=getSquaredResiduals(D1)+getSquaredResiduals(D2);
			if(temp_squared_residuals<squared_residuals){
				lefeDocList.clear();
				rightDocList.clear();
				squared_residuals=temp_squared_residuals;
				split_point=value;
				lefeDocList.addAll(D1);
				rightDocList.addAll(D2);
			}
		}
		divide_result.add(squared_residuals);
		divide_result.add(split_point);
		return divide_result;
	}
	
    
    
	/**
	 * @description 计算最好的分裂属性
	 * @param N
	 * @param attribte_list
	 * @return 最好的分裂属性
	 */
	public  int Attribute_selection_method(Node N,Node leftChild,Node rightChild){
		int attribute = 0;
		double split_point = 0.0;
		double squared_residuals=Double.POSITIVE_INFINITY;
		ArrayList<ArrayList<Double>>D=N.getDocList();//点N所包含的元组
		int attribte_list_size=attribte_list.size();
		for(int i=0;i<attribte_list_size;i++){
			ArrayList<ArrayList<Double>>lefeDocList=new ArrayList<ArrayList<Double>>();
			ArrayList<ArrayList<Double>>rightDocList=new ArrayList<ArrayList<Double>>();
			ArrayList<Double> divide_result=get_attribute_squared_residuals(i,D,lefeDocList,rightDocList);//返回在某个属性划分下gini与split_point
			double temp_squared_residuals=divide_result.get(0);
			if(temp_squared_residuals<squared_residuals){
				split_point=divide_result.get(1);
				attribute=i;
				squared_residuals=temp_squared_residuals;
				leftChild.setDocList(lefeDocList);
				rightChild.setDocList(rightDocList);
			}
		}
		N.setAttribute(attribute);
		N.set_split_point(split_point);
		return attribute;
	}
	
	
	/**
	 * @description 计算叶节点的gamma值
	 * @param N
	 * @return predict_value
	 */
	public  double getGamma(Node N){
		double sum1=0.0;
		double sum2=0.0;
		ArrayList<ArrayList<Double>>doc_list=N.getDocList();
		int doc_list_size=doc_list.size();
		for(int i=0;i<doc_list_size;i++){
			double Yik=doc_list.get(i).get(attribte_list_size);
			sum1=sum1+Yik;
			sum2=sum2+Math.abs(Yik)*(1-Math.abs(Yik));
		}
		double Gamma=(9.0/10)*(sum1/sum2);
	
		return Gamma;
	}
	
	
	/**
	 * @description 计算一个节点所有元组y的均值
	 * @param N
	 * @return predict_value
	 */
	public  double getPredictValue(Node N){
		double predict_value=0.0;
		double sum=0.0;
		ArrayList<ArrayList<Double>>doc_list=N.getDocList();
		for(ArrayList<Double> vector:doc_list){
			sum=sum+vector.get(attribte_list_size);
		}
		predict_value=sum/doc_list.size();
		return predict_value;
	}
	
	
	/**
	 * @description 创建二叉树
	 * @param N
	 */
	public  void createDecisionTree(Node N){
		if(N!=null){
			if(N.getDocList().size()<=20){
				N.setLeftChild(null);
				N.setRightChild(null);
			    N.set_gamma(getGamma(N));//给叶子节点赋上gamma值
			}
			else{
				Node left=new Node();
				Node right=new Node();
				Attribute_selection_method(N,left,right);
				if(left.getDocList().size()==0||right.getDocList().size()==0){
					N.setLeftChild(null);
					N.setRightChild(null);
					N.set_gamma(getGamma(N));//给叶子节点赋上gamma值
				}
				else{
					N.setLeftChild(left);
					N.setRightChild(right);
					createDecisionTree(left);
					createDecisionTree(right);
				}
			}
		}
	}
	
	
	/**
	 * @description 计算某个元组y的预测值
	 * @param vector
	 * @return
	 */
	public  Double getResult(ArrayList<Double>vector,Node N){
		
		if(N.getLeftChild()==null&&N.getRightChild()==null){
			return N.get_gamma();
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
	 * @description 得到一颗树并返回根节点
	 * @return root_node
	 */
	public Node getDecisionTree(){
		getAttribte_list();
		this.root_node=IniRootNode(this.sample_data);
		createDecisionTree(this.root_node);
		return this.root_node;
	}
	
	
	
	

}
