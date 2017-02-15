package com.example;

import com.alibaba.fastjson.JSON;
import org.apache.commons.lang.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.util.*;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

		// 获取原始数据，将其转换成对应的数据结构
		String originalData = "{'apple':{'A': 3.4,'B': 2.5, 'C': 4.4, 'D':3.8},'kiwi':{'A': 3.9,'E': 4.5, 'C': 3.4, 'D':4.0},'peach':{'A': 2.4,'B': 2.7, 'C': 4.1, 'D':3.8, 'E':4.2}}";
		Map originalMap = (Map) JSON.parse(originalData);
		simDistance(originalMap, "apple", "peach");

		sortPerson(4, "apple",originalMap);
	}

	// 计算任意两个人的欧几米的距离，并获得对应的相似系数
	private static double simDistance(Map originalData, String person1, String person2){
		Map person1Map = (Map) originalData.get(person1);
		Map person2Map = (Map) originalData.get(person2);

		double distance2Person = 0.0;
		//TODO: Java8 语法改造
		for(Object attribute : person1Map.keySet()){

			BigDecimal valueOfPerson1 = ((BigDecimal) person1Map.get(attribute));
			BigDecimal valueOfPerson2 = null;
			if(person2Map.containsKey(attribute)){
				valueOfPerson2 = (BigDecimal) person2Map.get(attribute);
				distance2Person += Math.abs(Math.pow(valueOfPerson2.doubleValue(), 2) - Math.pow(valueOfPerson1.doubleValue(), 2));
			}
		}

		System.out.println(1/(1+Math.pow(distance2Person, 0.5)));
		return 1/(1+Math.pow(distance2Person, 0.5));
	}

	// 通过皮尔逊方式，计算两个人的相似度
	public static double simDistance1(Map data, String thePerson, String person){
		Map thePersonData = (Map) data.get(thePerson);
		Map personData = (Map) data.get(person);

		// 获取两个不同的人，对同一部电影的评分，并且将他们相乘,再将所有的结果相加
		double xPowSum = 0.0;
		double yPowSum = 0.0;
		double mulSum = 0.0;
		double xSum = 0.0;
		double ySum = 0.0;

		int n = 0;

		for(Object key: thePersonData.keySet()){
			BigDecimal xObj = (BigDecimal) thePersonData.get(key);
			BigDecimal yObj = (BigDecimal) thePersonData.get(key);

			double x = xObj.doubleValue();
			double y = 0.0;

			if(yObj != null){
				y = yObj.doubleValue();
				n ++;
			}

			xPowSum += Math.pow(x, 2);
			yPowSum += Math.pow(y, 2);
			mulSum += (x*y);
			xSum += x;
			ySum += y;

		}

		double a = mulSum - (xSum * ySum)/n;
		double b = Math.pow((xPowSum - xPowSum/n)*(yPowSum - yPowSum/n), 0.5);
		double r = 1 + a/b;

		return r;
	}

	// 对相关的人进行相似度打分，获取对应的排序
	public static List<Map.Entry<String, Double>> sortPerson(int rank, String person, Map data){
		Map personScore = new HashMap();
		for(Object key : data.keySet()){
			// 其他人的近似分数
			if(!StringUtils.equals(key.toString(), person)){
				String personName = String.valueOf(key);
				double simValue = simDistance(data, person, personName);
				personScore.put(key, simValue);
			}
		}

		Comparator<Map.Entry> valueComparator = new Comparator<Map.Entry>() {
			@Override
			public int compare(Map.Entry o1, Map.Entry o2) {
				Double v1 = (Double) o1.getValue();
				Double v2 = (Double) o2.getValue();
				return v1 - v2 > 0 ? 1 : 0;
			}
		};


		List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String,Double>>(personScore.entrySet());

		Collections.sort(list, valueComparator);
		if(rank < 1 ){
			return null;
		}
		list.forEach( d -> System.out.println(d));

		if(rank  > list.size()){
			return list;
		}
		list.subList(0, rank-1);

		return list;
	}

}
