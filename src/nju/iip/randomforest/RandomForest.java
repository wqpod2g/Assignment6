package nju.iip.randomforest;

import java.io.IOException;
import java.util.ArrayList;

public class RandomForest {
	/**
	 * 测试数据路径
	 */
	private static String filePath="lily.data";
	
	/**
	 * 整个样本的特征矩阵集合
	 */
	private static ArrayList<ArrayList<Double>>allMatrix=new ArrayList<ArrayList<Double>>();
	
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
	
	
	
	


}
