/*
 * Copyright (C), 2002-2014, 苏宁易购电子商务有限公司
 * Date:     2014年10月23日 上午11:31:56
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.digiwin.code.coverage.backend.common.response;

import java.io.Serializable;

/**
 * 统一Json响应<br>
 * @ResponseBody。将该对象转成json格式，放在response的body
 * 
 * @author
 */
public final class ResponseResult<T> implements Serializable {

    private static final long serialVersionUID = -7199257293894269879L;

    /**
     * 是否成功，true成功，fasle失败
     */
    private boolean success = true;

    /**
     * 错误码
     */
    private String errorCode;
    /**
     * 信息提示
     */
    private String message;
    /**
     * 返回结果
     */
    private T result;

    public ResponseResult() {
        super();
    }
    
    public ResponseResult(boolean success) {
        this.success = success;
    }
    
    public ResponseResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public ResponseResult(boolean success, String message, T result) {
        this.success = success;
        this.message = message;
        this.result = result;
    }

    public ResponseResult(boolean success, String errorCode, String message, T result) {
        this.success = success;
        this.errorCode = errorCode;
        this.message = message;
        this.result = result;
    }

    /**
     * 返回带有code和消息的失败结构
     * @param code
     * @param message
     * @return
     */
    public static ResponseResult fail(String code,String message){
        return build(false,code,message,null);
    }

    /**
     * 直接返回错误
     * @return
     */
    public static ResponseResult fail(){
        return build(false,null,null,null);
    }

    /**
     * 返回带有失败信息的结构
     * @param message
     * @return
     */
    public static ResponseResult fail(String message){
        return build(false,null,message,null);
    }

    /**
     * 返回带有失败信息和参数的结构
     * @param message
     * @param data
     * @return
     */
    public static <T>  ResponseResult fail(String message,T data){
        return build(false,null,message,data);
    }

    /**
     * 返回带有成功信息和成功数据的结构
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T>  ResponseResult ok(String message,T data){
        return  build(true,null,message,data);
    }


    /**
     * 构建只带有成功信息的成功response
     * @param message
     * @return
     */
    public static ResponseResult ok(String message){
        return  build(true,null,message,null);
    }

    /**
     * 构建只带有成功信息的成功response
     * @return
     */
    public static ResponseResult ok(){
        return  build(true,null,null,null);
    }

    /**
     * 构建成功状态返回的结构数据
     * @param data
     * @param <T>
     * @return
     */
    public static <T extends Serializable> ResponseResult ok(T data){
        return  build(true,null,null,data);
    }

    /**
     * 构建成功状态返回的结构数据
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult ok(T data){
        return  build(true,null,null,data);
    }

    /**
     * 构建请求对象s
     * @param isSuccess
     * @param code
     * @param message
     * @param data
     * @param <T>
     * @return
     */
    public static <T> ResponseResult build(boolean isSuccess,String code,String message,T data){
        ResponseResult serviceResponse = new ResponseResult();
        serviceResponse.setErrorCode(code);
        serviceResponse.setSuccess(isSuccess);
        serviceResponse.setMessage(message);
        serviceResponse.setResult(data);
        return serviceResponse;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

}
