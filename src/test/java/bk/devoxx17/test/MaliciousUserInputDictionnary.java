package bk.devoxx17.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.Maps;
import bk.devoxx17.front.InjectionMethod;
import bk.devoxx17.test.MaliciousUserInputDictionnary.UserInput;

public class MaliciousUserInputDictionnary {

	private Map<InjectionMethod, UserInput> dictionnary = Maps.newHashMap();

	@SuppressWarnings("unchecked")
	public MaliciousUserInputDictionnary() {
		try {
			Properties p = new Properties();
			InputStream in = MaliciousUserInputDictionnary.class.getResourceAsStream("/malicioususerinputs.properties");
			p.load(in);
			in.close();
			for (String key : (Set<String>)(Set<?>)p.keySet()) {
				if (key.contains(".login")) {
					key = key.substring(0,key.lastIndexOf('.'));
					String method	= key.substring(key.lastIndexOf('.')+1);
					String login 	= p.getProperty(key+".login");
					String password = p.getProperty(key+".password");
					if (password!=null) {
						dictionnary.put(InjectionMethod.valueOf(method), new UserInput(login,password));
					}
				}			
			}
			System.out.println("MaliciousUserInputDictionnary loaded");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public UserInput get(InjectionMethod method) {
		return dictionnary.get(method);
	}

	public Set<Map.Entry<InjectionMethod, UserInput>> entrySet() {
		return dictionnary.entrySet();
	}

	public void remove(InjectionMethod method) {
		dictionnary.remove(method);
	}

	public Map<InjectionMethod, UserInput> newCopy() {
		return Maps.newHashMap(dictionnary);
	}
	
	public class UserInput {
		public UserInput(String login, String password) {
			this.login = login;
			this.password = password;
		}

		public String login;
		public String password;
	}
}
