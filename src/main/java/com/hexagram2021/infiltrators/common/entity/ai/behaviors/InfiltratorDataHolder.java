package com.hexagram2021.infiltrators.common.entity.ai.behaviors;

public interface InfiltratorDataHolder {
	boolean isInfiltrator();
	void resetPossibilityBreakingWorkstation();
	int getPossibilityBreakingWorkstation();
	
	void increasePossibilityBreakingWorkstation();
}
