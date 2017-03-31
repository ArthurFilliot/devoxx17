package bk.devoxx17.global;

import java.util.List;

import com.google.common.collect.Lists;

import bk.devoxx17.emulators.InjectionMethod;

public class ApplicationScope {
	private static ApplicationScope instance;
	private List<InjectionMethod> methodsToFind;
	private String errorMessage;
	private String foundMethodMessage;
	private Integer score;
	
	private ApplicationScope(){
		init();
	}
	
	public static ApplicationScope getInstance() {
		if (instance==null) {
			instance=new ApplicationScope();
		}
		return instance;
	}
	
	public void init() {
		errorMessage="";
		methodsToFind = Lists.newArrayList(InjectionMethod.values());
		score=0;
	}
	
//	public void chooseNewMethodToFind() {
//		int pick = new Random().nextInt(InjectionMethod.values().length);
//
//		InjectionMethod newMethodToFind=InjectionMethod.values()[pick];
//
//		while (newMethodToFind.equals(methodToFind)) {
//			pick = new Random().nextInt(InjectionMethod.values().length);
//			newMethodToFind=InjectionMethod.values()[pick];
//		}
//
//		//System.out.println(""+pick + " " + methodToFind.toString());
//		methodToFind=newMethodToFind;
//	}
	
	public List<InjectionMethod> getMethodsToFind() {
		return methodsToFind;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getFoundMethodMessage() {
		return foundMethodMessage;
	}

	public void setFoundMethodMessage(String foundMethodMessage) {
		this.foundMethodMessage = foundMethodMessage;
	}
	
}
