package com.example;

import org.apache.commons.lang.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by apple on 2017/2/17.
 */
public class RecommendUtil {

    /**
     * 根据某个人，向他推荐相似度高，并且分数高的物品
     * Checked
     * @param originalData
     * @param person       需要得到推荐的人
     *
     */
    public static void getRecommendation(Map originalData, String person) {
        // 先找出Tony没有关注的物品
        Map personData = (Map) originalData.get(person);
        Map itemSumSim = new HashMap();
        Map itemSumScore = new HashMap();

        for (Object key : originalData.keySet()) {
            // 只和别人比较
            if (StringUtils.equals(person, key.toString())) {
                continue;
            }

            String personName = String.valueOf(key);
            // 得到两个人的相似度
            double simValue = simDistance1(originalData, person, personName);
            // 负相关或者不相关的人，评分不计入考虑之内
            if (simValue <= 0) {
                continue;
            }
            Map otherPersonData = (Map) originalData.get(key);
            //找到被推荐人没有评价过的物品
            for (Object item : otherPersonData.keySet()) {
                if (!personData.containsKey(item)) {
                    // 某个物品的评分
                    BigDecimal sObj = (BigDecimal) otherPersonData.get(item);
                    double score = sObj.doubleValue();

                    // 得到其他评价过这个物品的人对这个物品的加权分数（相似度*评分）
                    // 将评价过这个物品的人的相似度相加
                    Double currentSumScore = (Double) itemSumScore.get(item);
                    if (currentSumScore == null) {
                        currentSumScore = simValue * score;
                    } else {
                        currentSumScore += simValue * score;
                    }
                    itemSumScore.put(item, currentSumScore);

                    Double currentSumSim = (Double) itemSumSim.get(item);
                    if (currentSumSim == null) {
                        currentSumSim = simValue;
                    } else {
                        currentSumSim += simValue;
                    }
                    itemSumSim.put(item, currentSumSim);

                }
            }

        }

        Map itemRealScore = new HashMap();

        itemSumScore.forEach((k, v) -> {
            double simValue = (double) itemSumSim.get(k);
            double recommendScore = (double) v / simValue;
            itemRealScore.put(k, recommendScore);
        });

//        LinkedHashMap<String, Double> orderMap = itemRealScore.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Collections.reverseOrder()))
//                .collect(Collectors.toMap(
//                        Map.Entry::getKey,
//                        Map.Entry::getValue,
//                        (e1, e2) -> e1,
//                        LinkedHashMap::new
//                ));
        Comparator<Map.Entry<String, Double>> valueComparator = new Comparator<Map.Entry<String, Double>>() {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2) {
                double value = o1.getValue()-o2.getValue();
                return  (int) value;
            }
        };
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String,Double>>(itemRealScore.entrySet());
        Collections.sort(list, valueComparator);
        list.forEach(a -> System.out.println(a.getKey() + ":" + a.getValue()));

        itemRealScore.forEach((k, v) -> System.out.println(k.toString() + ":" + v.toString()));

    }

    // 通过皮尔逊方式，计算两个人的相似度
    // Checked
    public static double simDistance1(Map data, String thePerson, String person) {
        Map thePersonData = (Map) data.get(thePerson);
        Map personData = (Map) data.get(person);

        // 获取两个不同的人，对同一部电影的评分，并且将他们相乘,再将所有的结果相加
        double xPowSum = 0.0;
        double yPowSum = 0.0;
        double mulSum = 0.0;
        double xSum = 0.0;
        double ySum = 0.0;

        // 先记录共同偏好
        Map commonInstrest = new HashMap();
        for (Object key : thePersonData.keySet()) {
            if (personData.containsKey(key)) {
                commonInstrest.put(key, 1);
            }
        }

        int n = commonInstrest.size();
        if (n == 0) {
            n = 1; // 防止n作为分母出错
        }

        for (Object key : commonInstrest.keySet()) {
            BigDecimal xObj = (BigDecimal) thePersonData.get(key);
            BigDecimal yObj = (BigDecimal) personData.get(key);

            double x = xObj.doubleValue();
            double y = yObj.doubleValue();

            xPowSum += Math.pow(x, 2);
            yPowSum += Math.pow(y, 2);
            mulSum += (x * y);
            xSum += x;
            ySum += y;
        }

        double a = mulSum - (xSum * ySum) / n;
        // TODO：协方差公式理解
        double b = Math.pow((xPowSum - Math.pow(xSum, 2) / n) * (yPowSum - Math.pow(ySum, 2) / n), 0.5);
        double r = a / b;
        if (b == 0) return 0;

//        System.out.println(r);
        return r;
    }

    /**
     * 将之前的输入数据源进行转置，例如key：人名,value: 物品名称及对应评分
     * 转置之后为：key：物品
     *           value：人名及对应评分
     *
     * @param originalData
     * @return
     */
    public static Map transformPrefs(Map originalData){
        Map newOriginalData = new HashMap();
        for(Object person: originalData.entrySet()){
            String personName = ((Map.Entry<String, Map>) person).getKey();
            Map personValue = ((Map.Entry<String, Map>) person).getValue();

            for(Object item: personValue.entrySet()){
                String itemName = ((Map.Entry<String, BigDecimal>) item).getKey();
                BigDecimal itemValue = ((Map.Entry<String, BigDecimal>) item).getValue();

                Map itemValueMap = null;
                if(newOriginalData.containsKey(itemName)){
                    itemValueMap = (Map) newOriginalData.get(itemName);
                }else {
                    itemValueMap = new HashMap();
                    newOriginalData.put(itemName, itemValueMap);
                }
                itemValueMap.put(personName, itemValue);
            }
        }
        newOriginalData.forEach((k, v)->System.out.println(k + ":" + v));

        return newOriginalData;
    }

    public static void getRecommendations(Map data, String person){
        Map itemScore = new HashMap();
        Map itemData = transformPrefs(data);

        for(Object item : itemData.entrySet()){
            // 只获取别人的评价内容
            item = (Map) item;
            if(((Map) item).containsKey(person)){
                continue;
            }




        }
    }

}
