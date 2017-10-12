package com.kssc0112.hiratemovie;

/**
 * Class representing a review
 */
public class Review {
    private String mId;
    private String mAuthor;
    private String mContent;

    public Review(String id, String author, String content) {
        mId = id;
        mAuthor = author;
        mContent = content;
    }

    public String getId() {
        return mId;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getContent() {
        return mContent;
    }
}
