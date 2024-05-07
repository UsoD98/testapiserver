package org.zerock.testapiserver.util;

public class CustomJWTException extends RuntimeException{

        public CustomJWTException(String msg) {
            super(msg);
        }
}
