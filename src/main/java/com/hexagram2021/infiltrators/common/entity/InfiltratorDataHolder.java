package com.hexagram2021.infiltrators.common.entity;

public interface InfiltratorDataHolder {
	boolean isInfiltrator();
	void resetPossibilityBreakingWorkstation();
	int getPossibilityBreakingWorkstation();
	
	void increasePossibilityBreakingWorkstation();
	
	void setImmuneToBadOmen();
}
