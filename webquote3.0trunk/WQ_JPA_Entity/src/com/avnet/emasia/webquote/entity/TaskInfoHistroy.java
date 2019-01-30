package com.avnet.emasia.webquote.entity;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.history.HistoryPolicy;

public class TaskInfoHistroy implements DescriptorCustomizer{

	@Override
	public void customize(ClassDescriptor classdescriptor) {
		HistoryPolicy policy = new HistoryPolicy();
		policy.addHistoryTableName("TASK_HISTORY");
		policy.addStartFieldName("HISTORY_START");
		policy.addEndFieldName("HISTORY_END");
		classdescriptor.setHistoryPolicy(policy);
	}

}
