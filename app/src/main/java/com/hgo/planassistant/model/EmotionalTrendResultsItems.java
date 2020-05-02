package com.hgo.planassistant.model;


public class EmotionalTrendResultsItems {
    private int sentiment; //表示情感极性分类结果, 0:负向，1:中性，2:正向
    private float confidence; // 表示分类的置信度
    private float positive_prob; // 表示属于积极类别的概率
    private float negative_prob; // 表示属于消极类别的概率

    public float getConfidence() {
        return confidence;
    }

    public float getNegative_prob() {
        return negative_prob;
    }

    public float getPositive_prob() {
        return positive_prob;
    }

    public int getSentiment() {
        return sentiment;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public void setNegative_prob(float negative_prob) {
        this.negative_prob = negative_prob;
    }

    public void setPositive_prob(float positive_prob) {
        this.positive_prob = positive_prob;
    }

    public void setSentiment(int sentiment) {
        this.sentiment = sentiment;
    }
}
