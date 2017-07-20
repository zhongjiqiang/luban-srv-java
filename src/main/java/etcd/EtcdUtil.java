package etcd;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeoutException;

import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
import mousio.etcd4j.responses.EtcdKeysResponse.EtcdNode;

public class EtcdUtil {
	
	private String Uri;
	private EtcdClient client;
	
	public EtcdUtil(String Uri){
		this.setUri(Uri);
		this.setClient(new EtcdClient(URI.create(Uri)));
	}
	
	public String getUri() {
		return Uri;
	}

	public void setUri(String uRI) {
		Uri = uRI;
	}

	public EtcdClient getClient() {
		return client;
	}

	public void setClient(EtcdClient client) {
		this.client = client;
	}

	@SuppressWarnings("finally")
	public String get(String key){
		String retstr = null;
		try {
			retstr =  client.get(key).consistent().send().get().node.value;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return retstr;
		}
	}
	
	public void create(String key,String value){
		
		try {
			client.put(key, value).ttl(10).prevExist(false).send().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	
	public void update(String key,String value){
		
		try {
			client.put(key, value).prevExist(true).send().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	public void put(String key,String value){
		try {
			client.put(key, value).send().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void puttmp(final String key,final String value){
		
		try {
			client.put(key, value).ttl(10).send().get();
			new Thread(){
				@Override
				public void run() {
					while(true){
						
						try {
							Thread.sleep(5000);
							client.put(key, value).ttl(10).send().get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (EtcdException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (EtcdAuthenticationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (TimeoutException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
				};
			}.start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void delete(String key){
		
		try {
			client.delete(key).send().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
	
	@SuppressWarnings("finally")
	public long getindex(String key){
		long rt =-1l;
		try {
			rt = client.get(key).send().get().node.modifiedIndex;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			return rt;
		}
	}
	
	public void putDir(String dir){
		try {
			client.putDir(dir).send().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public HashMap<String,String> getDir(String dir){
		HashMap<String,String> hm = new HashMap<String,String>();
		try {
			EtcdResponsePromise<EtcdKeysResponse> dirs = client.getDir(dir).recursive().send();
			EtcdKeysResponse etck = dirs.get();
			List<EtcdNode> ln = etck.getNode().getNodes();
			for(EtcdNode n : ln){
				hm.put(n.key, n.value);
				System.out.println("key="+n.key+"\tvalue="+n.value);
			}
//			System.out.println(etck.getNode().getNodes().get(0).key+"\t"+etck.getNode().getNodes().get(0).value);
//			System.out.println(client.getDir(dir).recursive().send());
			return hm;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return hm;
	}
	
	public void deleteDir(String dir){
		try {
			client.deleteDir(dir).send().get();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (EtcdAuthenticationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void close(){
		try {
			this.client.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		new EtcdUtil("http://192.168.199.205:2379").puttmp("/luban/nodes/javamember/192.168.199.241:8088", "ok");
	}

}
