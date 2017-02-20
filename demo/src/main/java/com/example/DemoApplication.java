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
        String critics = "{'Lisa Rose':{'Lady in the Water':2.5,'Snakes on a Plane':3.5,'Just My Luck':3.0,'Superman Returns':3.5,'You, Me and Dupree':2.5,'The Night Listener':3.0},'Gene Seymour':{'Lady in the Water':3.0,'Snakes on a Plane':3.5,'Just My Luck':1.5,'Superman Returns':5.0,'You, Me and Dupree':3.5,'The Night Listener':3.0},'Micheal Phillips':{'Lady in the Water':2.5,'Snakes on a Plane':3.0,'Superman Returns':3.5,'The Night Listener':4.0},'Claudia Puig':{'Snakes on a Plane':3.5,'Just My Luck':3.0,'Superman Returns':4.0,'You, Me and Dupree':2.5,'The Night Listener':4.5},'Mick LaSalle':{'Lady in the Water':3.0,'Snakes on a Plane':4.0,'Just My Luck':2.0,'Superman Returns':3.0,'You, Me and Dupree':2.0,'The Night Listener':3.0},'Jack Matthews':{'Lady in the Water':3.0,'Snakes on a Plane':4.0,'Superman Returns':5.0,'You, Me and Dupree':3.5,'The Night Listener':3.0},'Toby':{'Snakes on a Plane':4.5,'Superman Returns':4.0,'You, Me and Dupree':1.0}}";
        Map originalMap = (Map) JSON.parse(critics);
        simDistance(originalMap, "Lisa Rose", "Gene Seymour");
        RecommendUtil.simDistance1(originalMap, "Lisa Rose", "Gene Seymour");

		sortPerson(3, "Toby",originalMap);

        RecommendUtil.getRecommendation(originalMap, "Toby");
        //Test transform
        RecommendUtil.transformPrefs(originalMap);
    }

    // 计算任意两个人的欧几米的距离，并获得对应的相似系数
    // Checked
    private static double simDistance(Map originalData, String person1, String person2) {
        Map person1Map = (Map) originalData.get(person1);
        Map person2Map = (Map) originalData.get(person2);

        double distance2Person = 0.0;

        for (Object attribute : person1Map.keySet()) {
            BigDecimal valueOfPerson1 = ((BigDecimal) person1Map.get(attribute));
            BigDecimal valueOfPerson2 = null;
            if (person2Map.containsKey(attribute)) {
                valueOfPerson2 = (BigDecimal) person2Map.get(attribute);
                // 两者之间的差异，再平方
                distance2Person += Math.pow(valueOfPerson2.doubleValue() - valueOfPerson1.doubleValue(), 2);
            }
        }

//		System.out.println(1/(1+Math.pow(distance2Person, 0.5)));
        return 1 / (1 + Math.pow(distance2Person, 0.5));
    }

    // 对相关的人进行相似度打分，获取对应的排序
    // Checked
    public static List<Map.Entry<String, Double>> sortPerson(int rank, String person, Map data) {
        List personScoreList = new ArrayList();
        for (Object key : data.keySet()) {
            // 其他人的近似分数
            if (!StringUtils.equals(key.toString(), person)) {
                String personName = String.valueOf(key);
                double simValue = RecommendUtil.simDistance1(data, person, personName);
                Item item = new Item(personName, simValue);
                personScoreList.add(item);
            }
        }

        Comparator<Item> valueComparator = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
               return  o2.getItemValue().compareTo(o1.getItemValue());
            }
        };

        Collections.sort(personScoreList, valueComparator);
        if (rank < 1) {
            return null;

        }
        if (rank > personScoreList.size()) {
            return personScoreList;
        }
        personScoreList = personScoreList.subList(0, rank);

//        personScoreList.forEach( d -> System.out.println(d.toString()));

        return personScoreList;
    }

}
