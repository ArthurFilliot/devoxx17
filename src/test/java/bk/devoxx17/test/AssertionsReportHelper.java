package bk.devoxx17.test;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

public class AssertionsReportHelper {
	List<String> assertTrueFailedMessages = Lists.newArrayList();
	
	public void init() {
		assertTrueFailedMessages = Lists.newArrayList();
	}
	
	public void end() {
		if (assertTrueFailedMessages.size()>0) {
			throw new AssertionError(StringUtils.join(assertTrueFailedMessages,"\n"), null);
		}
	}
	
	public void assertTrue(String message, boolean expressionResult) {
		if (!expressionResult) {
			assertTrueFailedMessages.add(message);
		}
	}
	
	public void assertFalse(String message, boolean expressionResult) {
		if (expressionResult) {
			assertTrueFailedMessages.add(message);
		}
	}
}
