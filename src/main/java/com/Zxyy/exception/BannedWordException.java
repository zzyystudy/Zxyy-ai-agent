package com.Zxyy.exception;

public class BannedWordException extends BaseException{
    public BannedWordException() {
        super("有敏感词");
    }

    public BannedWordException(String msg) {
        super(msg);
    }
}
