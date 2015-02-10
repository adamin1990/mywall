package com.lt.dao;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CommonDao {
	public static List<Map<String,Object>> getList(String urlpath,String pageNo,String cid) throws Exception{
		String path = urlpath;
		List<Map<String ,Object>> list ;
		if(pageNo==null){
			path = path;
		}else{
			path = path+"&pageNo="+pageNo+"&cid="+cid;
		}
		URL url =new URL(path);
		HttpURLConnection conn=(HttpURLConnection)url.openConnection();
		conn.setAllowUserInteraction(true); 
		conn.setConnectTimeout(5* 1000);//设置连接超时
		conn.setRequestMethod("GET");//以get方式发起请求
		if (conn.getResponseCode() != 200) throw new RuntimeException("请求url失败");
		InputStream is = conn.getInputStream();//得到网络返回的输入流
		String result = readData(is, "GBK");
		conn.disconnect();
		JSONArray ja = new JSONArray(result);
		JSONObject jsonObj; 
		list = new ArrayList<Map<String,Object>>(); 
		 for(int i = 0 ; i < ja.length() ; i ++){
			 
             jsonObj = (JSONObject)ja.get(i);
             //以jsonobj的（key value）
             Iterator<String> keyIter= jsonObj.keys();  
             String key;  
             Object value ;  
             Map<String, Object> valueMap = new HashMap<String, Object>();  
             while (keyIter.hasNext()) {  
                 key = keyIter.next();  
                 //key 就是id  value就是值
                 value = jsonObj.get(key);   
//                 if(key.equals("caddr")){
//                 	Bitmap map = null;
//                 	try{
//                 		String urr = value.toString().replaceAll(" ","");
//                 		URL	url1 = new URL(urr);
//	  						HttpURLConnection conn1 = (HttpURLConnection)url1.openConnection();
//	  		                conn1.connect(); 
//	  		                InputStream in = conn1.getInputStream();
//	  		                map = BitmapFactory.decodeStream(in);
//	  		                conn1.disconnect();
//                 	}catch(Exception e){ 
//                 		System.out.println(e.getMessage().toString());
//                 	} 
//		                value = map; 
//                 } 
                 valueMap.put(key, value);
             }
             list.add(valueMap);
        
		 }
		return list;
		
	}
	public static List<Map<String ,Object>> getListaddr(String urlpath,String pageNo,String cid) throws Exception{
		String path = urlpath;
		List<Map<String ,Object>> list ;
		String strjson = null;
		if(pageNo==null){
			path = path;
		}else{
			path = path+"&pageNo="+pageNo+"&cid="+cid;
		} 
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection(); 
			conn.setAllowUserInteraction(true); 
			conn.setAllowUserInteraction(true);
			conn.setConnectTimeout(5* 1000);//设置连接超时
			conn.setRequestMethod("GET");//以get方式发起请求
			if (conn.getResponseCode() != 200) throw new RuntimeException("请求url失败");
			InputStream is = conn.getInputStream();//得到网络返回的输入流
			String result = readData(is, "GBK");
			conn.disconnect();
			JSONArray ja = new JSONArray(result);
			JSONObject jsonObj; 
			list = new ArrayList<Map<String,Object>>();  
	        for(int i = 0 ; i < ja.length() ; i ++){ 
	                jsonObj = (JSONObject)ja.get(i);
	                //以jsonobj的（key value）
	                Iterator<String> keyIter= jsonObj.keys();  
	                String key;  
	                Object value ;  
	                Map<String, Object> valueMap = new HashMap<String, Object>();  
	                while (keyIter.hasNext()) {  
	                    key = keyIter.next();  
	                    //key 就是id  value就是值
	                    value = jsonObj.get(key);   
	                    if(key.equals("caddr")){
	                    	Bitmap map = null;
	                    	try{
	                    		URL	url1 = new URL(value.toString());
		  						HttpURLConnection conn1 = (HttpURLConnection)url1.openConnection();
		  		                conn1.connect(); 
		  		                InputStream in = conn1.getInputStream();
		  		                map = BitmapFactory.decodeStream(in);
		  		                conn1.disconnect();
	                    	}catch(Exception e){ 
	                    		System.out.println(e.getMessage().toString());
	                    	} 
	  		                value = map; 
	  		              valueMap.put(key, value);
	                    } 
	                    
	                }
	                list.add(valueMap);
	           }  
	        return list; 
		
	}
/**
 * 第一个参数为输入流,第二个参数为字符集编码
 * @param is
 * @param string
 * @return
 * @throws Exception
 */
	private static String readData(InputStream is, String string) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while( (len = is.read(buffer)) != -1 ){
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		is.close();
		return new String(data, string);
	}

}
