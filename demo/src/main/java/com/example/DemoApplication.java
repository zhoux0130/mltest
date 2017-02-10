package com.example;

import com.alibaba.fastjson.JSON;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {

		SpringApplication.run(DemoApplication.class, args);

		// 获取原始数据，将其转换成对应的数据结构
		String originalData = "{'apple':{'A': 3.4,'B': 2.5, 'C': 4.4, 'D':3.8},'kiwi':{'A': 3.9,'E': 4.5, 'C': 3.4, 'D':4},'peach':{'A': 2.4,'B': 2.7, 'C': 4.1, 'D':3.8, 'E':4}}";
		Map originalMap = (Map) JSON.parse(originalData);

		//

	}

	// 计算任意两个人的欧几米的距离，并获得对应的相似系数
	private static double simDistance(Map originalData, String person1, String person2){

		return 0.0;
	}


}
