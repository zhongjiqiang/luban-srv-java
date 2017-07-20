/**
 * 
 */
package com.shopex.hprose.server;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * @author zjq
 *
 */
@SuppressWarnings({ "rawtypes" ,"finally","unchecked"})
public class Services {
	
	private String docclassename;
	

	public String getDocclassename() {
		return docclassename;
	}


	public void setDocclassename(String docclassename) {
		this.docclassename = docclassename;
	}


	public Services(String docclassename) {
		this.docclassename = docclassename;
	}


	public  HashMap doc(){
		HashMap hm =  null;
		try {
//			Class cl = Class.forName("com.shopex.hprose.getdoc.Doc");
			Class<?> cl = Class.forName(docclassename);
			Method method = cl.getMethod("getdoc");
//			hm = (HashMap) method.invoke(Class.forName("com.shopex.hprose.getdoc.Doc").newInstance());
			hm = (HashMap) method.invoke(Class.forName(docclassename).newInstance());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}finally{
			return hm;
		}
	}
	
//	public static void main(String[] args) {
//		System.out.println(new Services("com.shopex.hprose.getdoc.Doc").doc());
//	}
	
	
}
