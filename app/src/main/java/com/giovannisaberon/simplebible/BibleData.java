package com.giovannisaberon.simplebible;

public class BibleData {
    private String book;
    private int chapter;
    private int verse;
    String word;
//    private String[] verses;

    public BibleData(String book, int chapter, int verse, String word){
        this.book= book;
        this.chapter = chapter;
        this.verse = verse;
        this.word = word;
    }


    public String getBook(){
        return this.book;
    }

    public int getChapter(){
        return this.chapter;
    }

    public int getVerse(){
        return this.verse;
    }

    public String getWord(){
        return this.word;
    }

    public String getReference(){
        return this.book+","+Integer.toString(this.chapter)+","+Integer.toString(this.verse);
    }



}

