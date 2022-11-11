package com.hexagram2021.infiltrators.common.entities;

public interface InfiltratorDataHolder {
	boolean isInfiltrator();
	void resetPossibilityBreakingWorkstation();
	int getPossibilityBreakingWorkstation();
	
	void increasePossibilityBreakingWorkstation();
	
	void setImmuneToBadOmen();
}
