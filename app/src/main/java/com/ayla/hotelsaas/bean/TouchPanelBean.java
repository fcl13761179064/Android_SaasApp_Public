package com.ayla.hotelsaas.bean;

import java.io.Serializable;

/**
 * @描述
 * @作者 fanchunlei
 * @时间 2020/8/12
 */
public class TouchPanelBean implements Serializable {

    private int iconRes;
    private String words;//按键别名
    private String pictureCode;//按键图标
    private int wordsId;//按键别名资源ID
    private int pictureCodeId;//按键图标资源ID
    private int sequence;//按键序号

    public TouchPanelBean(int iconRes, String words, int sequence) {
        this.iconRes = iconRes;
        this.words = words;
        this.sequence = sequence;
    }

    public int getIconRes() {
        return iconRes;
    }

    public void setIconRes(int iconRes) {
        this.iconRes = iconRes;
    }

    public String getWords() {
        return words;
    }

    public void setWords(String words) {
        this.words = words;
    }

    public String getPictureCode() {
        return pictureCode;
    }

    public void setPictureCode(String pictureCode) {
        this.pictureCode = pictureCode;
    }

    public int getWordsId() {
        return wordsId;
    }

    public void setWordsId(int wordsId) {
        this.wordsId = wordsId;
    }

    public int getPictureCodeId() {
        return pictureCodeId;
    }

    public void setPictureCodeId(int pictureCodeId) {
        this.pictureCodeId = pictureCodeId;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }
}
