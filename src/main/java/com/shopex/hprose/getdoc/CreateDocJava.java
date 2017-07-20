package com.shopex.hprose.getdoc;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
/***
 * 
 * @author zhongjiqiang
 *
 */

@SuppressWarnings({ "rawtypes", "unchecked", "finally", "unused"  })
public class CreateDocJava {
	private  String injavapath;
	private  String outdocfile;
	static StringBuffer qidongxiang = new StringBuffer();
	public  String getInjavapath() {
		return injavapath;
	}


	public  void setInjavapath(String injavapath) {
		this.injavapath = injavapath;
	}



	public  String getOutdocfile() {
		return outdocfile;
	}


	public  void setOutdocfile(String outdocfile) {
		this.outdocfile = outdocfile;
	}

	public CreateDocJava(String injavapath,String outdocfile){
		this.injavapath=injavapath;
		this.outdocfile=outdocfile;
	}


	public static void main(String[] args) {

//		createDoc();
	}
	
	
	
	
	public  void createDoc(){
		
		String ss = outdocfile.replaceAll("src/main/java/", "").replaceAll("/", ".");
		String[] sss = ss.split("\\.");
		String pack = "";
		for(int i=0; i< sss.length-2;i++){
			pack=pack+sss[i];
			if(i<sss.length-3){
				pack=pack+".";
			}
		}
		StringBuffer sb = new StringBuffer();
		sb.append("package "+pack+";\n");
		sb.append("import java.io.IOException;\n");
		sb.append("import java.util.HashMap;\n");
		sb.append("import com.fasterxml.jackson.core.JsonParseException;\n");
		sb.append("import com.fasterxml.jackson.databind.JsonMappingException;\n");
		sb.append("import com.fasterxml.jackson.databind.ObjectMapper;\n");
		sb.append("@SuppressWarnings({ \"rawtypes\", \"finally\" })\n");
		sb.append("public class Doc {\n");
		sb.append("private String doc = "+getmapstr()+";\n");
		sb.append("private HashMap hm = null;\n");
		sb.append("public  HashMap getdoc(){\n");
		sb.append("try {\n");
		sb.append("if(hm==null){\n");
		sb.append("hm = new ObjectMapper().readValue(doc, HashMap.class);\n");
		sb.append("}\n");
		sb.append("} catch (JsonParseException e) {\n");
		sb.append("e.printStackTrace();\n");
		sb.append("} catch (JsonMappingException e) {\n");
		sb.append("e.printStackTrace();\n");
		sb.append("} catch (IOException e) {\n");
		sb.append("e.printStackTrace();\n");
		sb.append("}finally{\n");
		sb.append("return hm;\n");
		sb.append("}\n");
		sb.append("}\n");
		sb.append("public static void main(String[] args) {\n");
		sb.append("System.out.println(new Doc().getdoc());\n");
		sb.append("}\n");
		sb.append("}\n");
//		System.out.println(sb);
		try {
			Files.deleteIfExists(Paths.get(outdocfile));
			Files.write(Paths.get(outdocfile),(sb.toString()+"/*\n"+qidongxiang.toString()+"*/\n").getBytes(), StandardOpenOption.CREATE_NEW);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private  String getmapstr(){
		String s=null;
		try {
			s = new ObjectMapper().writeValueAsString(getdoc());
			s=s.replaceAll("\"","\\\\\"");
			s="\""+s+"\"";
//			System.out.println(s);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return s;
		}
	}
	
	
	private  HashMap  getdoc(){
		
		HashMap docmap = new HashMap();
		List<String> lines=null;
		try {
			boolean startflag = false;
			List  apilist = new ArrayList();
			List paramlist = new ArrayList();
			Map mothedmap = new HashMap();
			Map parmpropmap = new HashMap();
			
			File f = new File(injavapath);
			String[] files = f.list();
			for(String file: files){
				System.out.println(file);
				lines = Files.readAllLines(Paths.get(injavapath+"/"+file), Charset.forName("UTF-8"));
				String classname = file.replaceAll(".java", "");
				if(!classname.equals("Services")){
					qidongxiang.append(classname+" "+classname.toLowerCase()+" = new "+classname+"();\n");
				}
				String summary = "";
				for(String line:lines){
					line =line.replaceAll("/", "").trim();
					if(line.startsWith("@Title") && !startflag){
						startflag=true;
						mothedmap.put("title", getlast(line.split("\\s+")));
					}else if(startflag && line.startsWith("@Param")){
						String[] paramprops = line.split("\\s+");
						parmpropmap.put("name", paramprops[1]);
						parmpropmap.put("type", paramprops[2]);
						parmpropmap.put("required", paramprops[3].endsWith("true"));
						parmpropmap.put("description", paramprops[4]);
						parmpropmap.put("format", "");
						parmpropmap.put("allowMultiple", false);
						parmpropmap.put("minimum", 0);
						parmpropmap.put("maximum", 0);
						paramlist.add(parmpropmap);
						parmpropmap = new HashMap();
					}else if(line.startsWith("@Doc")){
						summary= getlast(line.split("\\s+")).equals("@Doc")?"":getlast(line.split("\\s+"));
					}else if(startflag && !line.isEmpty() && !line.startsWith("@Doc")){
						startflag = false;
						mothedmap.put("parameters", paramlist);
						mothedmap.put("name", getalisname(classname,  getlast(line.split("\\(")[0].split("\\s+"))));
						//server.add("New", bs,"base_new");
						qidongxiang.append("server.add(\""+getlast(line.split("\\(")[0].split("\\s+"))
								+"\","+classname.toLowerCase()+",\""
								+getalisname(classname,  getlast(line.split("\\(")[0].split("\\s+")))+"\");\n");
						mothedmap.put("summary",summary);
						apilist.add(mothedmap);
						mothedmap = new HashMap();
						paramlist = new ArrayList();
//					System.out.println("Mothedname="+getlast(line.split("\\(")[0].split("\\s+")));
					}
				}
			}
			docmap.put("apis", apilist);
//			System.out.println(new ObjectMapper().writeValueAsString(docmap));
//			System.out.println(docmap);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return docmap;
			
		}
		
	}
	
	
	private static String getlast(String[] split) {
		// TODO Auto-generated method stub
		return split[split.length-1];
	}
	
	
	
	private static String getalisname(String classname, String mothedname){
		StringBuffer sb=new StringBuffer();
//		String st = classname.replaceAll("Serv", "");
		char[] cs = classname.toCharArray();
		for(int i=0;i<cs.length;i++){
			if(i!=0 && cs[i]>=65 && cs[i]<=90){
				sb.append("_"+cs[i]);
			}else{
				sb.append(cs[i]);
			}
		}
		sb.append("_"+mothedname);
		
		return sb.toString().toLowerCase();
	}
	

}
