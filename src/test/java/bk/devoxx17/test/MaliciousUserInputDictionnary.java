package bk.devoxx17.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Maps;
import bk.devoxx17.front.InjectionMethod;
import bk.devoxx17.test.MaliciousUserInputDictionnary.UserInput;

public class MaliciousUserInputDictionnary {

	private ArrayListMultimap<InjectionMethod, UserInput> dictionnary = ArrayListMultimap.create();

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
					String index = key.substring(key.lastIndexOf('.')+1,key.lastIndexOf('.')+3);
					key = key.substring(0,key.lastIndexOf('.'));
					String method	= key.substring(key.lastIndexOf('.')+1);
					String login 	= p.getProperty(key+"." + index + ".login");
					String password = p.getProperty(key+"."+index+".password");
					if (password!=null) {
						dictionnary.put(InjectionMethod.valueOf(method), new UserInput(login,password, Integer.parseInt(index)));
					}
				}			
			}
			System.out.println("MaliciousUserInputDictionnary loaded");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public List<UserInput> get(InjectionMethod method) {
		return dictionnary.get(method);
	}

	public void remove(InjectionMethod method) {
		dictionnary.removeAll(method);
	}

	public ArrayListMultimap<InjectionMethod, UserInput> newCopy() {
		return ArrayListMultimap.create(dictionnary);
	}
	
	public class UserInput {
		public UserInput(String login, String password, Integer index) {
			this.login = login;
			this.password = password;
			this.index = index;
		}

		public String login;
		public String password;
		public Integer index;
	}
}
