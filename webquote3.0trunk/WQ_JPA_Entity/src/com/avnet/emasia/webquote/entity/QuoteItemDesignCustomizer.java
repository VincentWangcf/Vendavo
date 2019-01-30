package com.avnet.emasia.webquote.entity;

import org.eclipse.persistence.config.DescriptorCustomizer;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.history.HistoryPolicy;

public class QuoteItemDesignCustomizer implements DescriptorCustomizer  {

	@Override
	public void customize(ClassDescriptor descriptor) {
		HistoryPolicy policy = new HistoryPolicy();
        policy.addHistoryTableName("QUOTE_ITEM_DESIGN_HIS");
        policy.addStartFieldName("START_DATE");
        policy.addEndFieldName("END_DATE");
        descriptor.setHistoryPolicy(policy);

	}

}
