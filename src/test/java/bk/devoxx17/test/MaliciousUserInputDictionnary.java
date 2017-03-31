package bk.devoxx17.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.SortedSetMultimap;
import com.google.common.collect.TreeMultimap;

import bk.devoxx17.emulators.InjectionMethod;
import bk.devoxx17.global.ApplicationScope;
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
				if (key.contains(".login") && (!key.contains(".step") || key.contains(".step1"))) {
					if (key.contains(".step1")) {
						key = key.substring(0,key.lastIndexOf('.'));
					}
					key = key.substring(0,key.lastIndexOf('.'));
					String index = key.substring(key.lastIndexOf('.')+1,key.lastIndexOf('.')+3);
					key = key.substring(0,key.lastIndexOf('.'));
					String method	= key.substring(key.lastIndexOf('.')+1);
					String login 	= p.getProperty(key+"." + index + ".login");
					String password = p.getProperty(key+"."+index+".password");
					String login1 	= p.getProperty(key+"." + index + ".login.step1");
					String login2 	= p.getProperty(key+"." + index + ".login.step2");
					String login3 	= p.getProperty(key+"." + index + ".login.step3");
					String password1 = p.getProperty(key+"." + index + ".password.step1")==null?"":p.getProperty(key+"." + index + ".password.step1");
					String password2 = p.getProperty(key+"." + index + ".password.step2")==null?"":p.getProperty(key+"." + index + ".password.step2");
					String password3 = p.getProperty(key+"." + index + ".password.step3")==null?"":p.getProperty(key+"." + index + ".password.step3");

					if (login!=null) {
						dictionnary.put(InjectionMethod.valueOf(method), new UserInput(login,password!=null?password:"", Integer.parseInt(index),null));
					}
					if (login1!=null) {
						dictionnary.put(InjectionMethod.valueOf(method), new UserInput(login1,password1, Integer.parseInt(index),1));
						dictionnary.put(InjectionMethod.valueOf(method), new UserInput(login2,password2, Integer.parseInt(index),2));
						dictionnary.put(InjectionMethod.valueOf(method), new UserInput(login3,password3, Integer.parseInt(index),3));
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
	
	public void check(boolean trueOrFalse, String objectName, BiFunction<String, String, Boolean> funcCheck, BiConsumer<String, Boolean> funcAssert, Map.Entry<InjectionMethod, UserInput> entry, ArrayListMultimap<InjectionMethod, UserInput> tmpDictionnary) {
		InjectionMethod method 	= entry.getKey();
		UserInput input 		= entry.getValue();
		String login 			= input.login;
		String password 		= input.password;
		Integer index 			= input.index;
		Integer stepnb			= input.stepnb;
		String message			= objectName+"/"+method+"_"+index+ " should "+ (trueOrFalse?"":"not ") +"work";
		if (stepnb==null) {
			funcAssert.accept(message, funcCheck.apply(login, password));
		}else if (stepnb==1) {
			List<UserInput> steps = Lists.newArrayList();
			for (UserInput minput : tmpDictionnary.get(method)) {
				if (index.equals(minput.index)) {
					steps.add(minput);
				}
			}
			login = steps.get(0).login;
			password = steps.get(0).password;
			funcCheck.apply(login, password);
			String step1data = getCompromisedData(ApplicationScope.getInstance().getErrorMessage());
			login = steps.get(1).login.replace("step1", step1data);
			password = steps.get(1).password.replace("step1", step1data);
			funcCheck.apply(login, password);
			String step2data = getCompromisedData(ApplicationScope.getInstance().getErrorMessage());
			if (steps.get(2).login==null) {
				funcAssert.accept(message, funcCheck.apply(login, password));
				return;
			}
			login = steps.get(2).login.replace("step1", step1data).replace("step2", step2data);
			password = steps.get(2).password.replace("step1", step1data).replace("step2", step2data);
			funcAssert.accept(message, funcCheck.apply(login, password));
		}	
	}
	
	private String getCompromisedData(String errorMessage) {
		if (errorMessage.indexOf('\'')==-1) {
			return StringUtils.EMPTY;
		}
		return errorMessage.substring(errorMessage.indexOf('\'')+1, errorMessage.lastIndexOf('\''));
	}
	
	public class UserInput {
		public UserInput(String login, String password, Integer index, Integer stepnb) {
			this.login = login;
			this.password = password;
			this.index = index;
			this.stepnb=stepnb;
		}

		public String login;
		public String password;
		public Integer index;
		public Integer stepnb;
	}
	
	@FunctionalInterface
    public interface TriFunction<R, S, T, U> {

        /**
         * Applies this function to the given arguments.
         *
         * @param t the first function argument
         * @param u the second function argument
         * @param s the third function argument
         * @return the function result
         */
        R apply(S s, T t, U u);
    }
}
