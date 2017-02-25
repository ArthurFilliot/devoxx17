package bk.devoxx17.front;

import java.util.Random;

public class ApplicationScope {
	private static ApplicationScope instance;
	private InjectionMethod methodToFind;
	private String errorMessage = "Test";
	
	private ApplicationScope(){
		chooseNewMethodToFind();
	}
	
	public static ApplicationScope getInstance() {
		if (instance==null) {
			instance=new ApplicationScope();
		}
		return instance;
	}
	
	public void chooseNewMethodToFind() {
//		int pick = new Random().nextInt(InjectionMethod.values().length);
//		InjectionMethod newMethodToFind=InjectionMethod.values()[pick];
//		while (newMethodToFind.equals(methodToFind)) {
//			pick = new Random().nextInt(InjectionMethod.values().length);
//			newMethodToFind=InjectionMethod.values()[pick];
//		}
//		methodToFind=newMethodToFind;
		methodToFind=InjectionMethod.CLASSICAL;
	}
	
	public InjectionMethod getMethodToFind() {
		return methodToFind;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
